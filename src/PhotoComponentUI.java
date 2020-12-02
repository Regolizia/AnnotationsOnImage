import fr.lri.swingstates.sm.BasicInputStateMachine;
import fr.lri.swingstates.sm.State;
import fr.lri.swingstates.sm.Transition;
import fr.lri.swingstates.sm.transitions.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static java.awt.event.KeyEvent.*;

public class PhotoComponentUI implements MouseListener, MouseMotionListener, KeyListener {

    private BufferedImage img;
    private int width = 0;
    private int height = 0;
    private int x = 0;
    private int y = 0;
    private PhotoComponent controller;
    private Graphics2D g;

    PhotoComponentUI(PhotoComponent controller) {

        this.controller = controller;
        controller.addMouseListener(this);
        controller.addMouseMotionListener(this);
        controller.addKeyListener(this);
        controller.setFocusable(true);

        stateMachine();
    }

    public void stateMachine() {
        BasicInputStateMachine sm = new BasicInputStateMachine() {
            Point2D dragStart;

            State start = new State() {

                Transition click = new Click(BUTTON1) {

                    @Override
                    public void action() {
                        int clickCount = getMouseEvent().getClickCount();

                        if (clickCount == 1) {
                            manageSelectionOrWriting(getPoint());

                        } else if (clickCount == 2) {
                            managePhotoFlip();
                        }
                    }

                };

                Transition shiftPress = new KeyPress(VK_SHIFT, "-> selecting") {

                    @Override
                    public boolean guard() {
                        return controller.isFlipped();
                    }

                };
                Transition writing = new KeyPress() {
                    @Override
                    public void action() {
                        manageWritingASingleLetter(getChar());
                    }

                };
                Transition mousePressToDragOrDraw = new Press(BUTTON1, "-> draggingOrDrawing") {

                    @Override
                    public boolean guard() {
                        return controller.isFlipped();
                    }

                    @Override
                    public void action() {
                        dragStart = getPoint();
                    }
                };

            };

            State selecting = new State() {

                Transition click = new Click(BUTTON1) {
                    @Override
                    public void action() {
                        addDrawableToSelection(getPoint());
                    }
                };

                Transition shiftRelease = new KeyRelease(VK_SHIFT, "-> start");

            };


            State draggingOrDrawing = new State() {
                Point2D lastDragPoint;

                Transition dragging = new Drag(BUTTON1) {

                    @Override
                    public void action() {
                        if(!controller.getSelectedDrawables().isEmpty()) {

                            moveSelection(getPoint(), lastDragPoint);
                            lastDragPoint = getPoint();
                        }
                        else{
                            manageDrawing(getPoint());
                        }
                    }
                };

                Transition release = new Release(BUTTON1, "-> start"){
                    @Override
                    public void action() {
                        controller.notDrawing();
                    }
                };

                @Override
                public void enter() {
                    lastDragPoint = dragStart;
                }

            };

        };

        sm.addAsListenerOf(controller);
        sm.setActive(true);
    }

    public void addDrawableToSelection(Point2D where) {
        Drawable selectedDrawable = pick(where);

        if (selectedDrawable != null) {
            select(selectedDrawable);
        }
    }

    public void manageSelectionOrWriting(Point2D eventPoint) {
        controller.requestFocusInWindow();
        unselectAll();
        addDrawableToSelection(eventPoint);
        controller.cleanEmptyText();

        if (!isSomethingSelected()) {
            controller.getDrawables().add(new Text(eventPoint, controller.getModel().getColor(), controller.getModel().getStroke(), controller.getModel().getFont()));

        }
    }

    public void managePhotoFlip() {
        if (controller.isFlipped()) {
            controller.unflipImage();

        } else {
            controller.flipImage();

        }
        controller.notDrawing();
    }

    public void manageWritingASingleLetter(char c) {
        if (controller.isFlipped()) {
            addChar(c, controller);
            controller.writing();
        }
    }

    public void manageDrawing(Point2D where) {
        unselectAll();
        int s = controller.getModel().getStroke();
        if (withinRange((int) where.getX(), (int) where.getY(), s)) {
            if (!controller.isDrawing()) {
                controller.getDrawables().add(new Stroke(s, controller.getModel().getColor()));
            }
            // DRAW
            controller.drawing();
            ((Stroke) controller.getDrawables().get(controller.getDrawables().size() - 1)).addPointXY((int) where.getX(), (int) where.getY());
        }
    }

    public void paintComponent(Graphics2D g) throws IOException {

        if (!controller.getImgPath().equals("")) {
            img = ImageIO.read(new FileInputStream(controller.getImgPath()));
            width = img.getWidth(null);
            height = img.getHeight(null);

            this.g = g;
            g.setColor(Color.lightGray);
            g.fillRoundRect(0, 0, controller.getWidth(), controller.getHeight(), 0, 0);


            drawImage(g);

            if (controller.isFlipped()) {
                drawFlipped(g);
            }
        }

    }

    private void drawFlipped(Graphics2D g) {

        for (Drawable d : controller.getDrawables()) {
            d.draw(g);
        }

    }

    private void drawImage(Graphics2D g) {
        g.drawImage(img, x, y, controller);
    }


    Dimension getPreferredSize() {
        return new Dimension(800, 500);
    }

    Dimension getSize() {
        if (width == 0 && height == 0) {
            return new Dimension(800, 600);
        } else {
            return new Dimension(width, height);
        }
    }


    private boolean withinRange(int mx, int my, int s) {
        if (mx + s > x && mx < x + width - s / 2) {
            return my + s > y && my < y + height - s / 2;
        }
        return false;
    }

    private void addChar(char c, PhotoComponent pc) {

        if (!controller.getDrawables().isEmpty()) {
            boolean isText = controller.getDrawables().get(controller.getDrawables().size() - 1).getClass().getName().equals("Text");

            if (isText) {
                ((Text) controller.getDrawables().get(controller.getDrawables().size() - 1)).addCharToText(c);
                ((Text) controller.getDrawables().get(controller.getDrawables().size() - 1)).fixTooBig(g, x, y, width, height);
            }
        }
    }

    public Drawable pick(Point2D where) {

        ArrayList<Drawable> reverse = new ArrayList<>(controller.getDrawables());
        Collections.reverse(reverse);
        for (Drawable d : reverse) {

            if (d.hitTest(where, g)) {
                return d;
            }
        }
        return null;
    }


    public void moveSelection(Point2D to, Point2D from) {
        for (Drawable d : controller.getSelectedDrawables()) {
            Point2D currentPoint = to;
            int dx = (int) currentPoint.getX() - (int) from.getX();
            int dy = (int) currentPoint.getY() - (int) from.getY();
            d.moveBy(dx, dy);
            controller.repaint();
        }
    }

    public void select(Drawable drawable) {
        ArrayList<Drawable> selectedDrawables = controller.getSelectedDrawables();
        drawable.setSelected(true);
        selectedDrawables.add(drawable);
        controller.repaint();
    }

    public void unselectAll() {
        ArrayList<Drawable> selectedDrawables = controller.getSelectedDrawables();
        for (Drawable selected : selectedDrawables) {
            selected.setSelected(false);
        }
        selectedDrawables.clear();
        controller.repaint();
    }

    public boolean isSomethingSelected() {
        ArrayList<Drawable> selectedDrawables = controller.getSelectedDrawables();
        return !selectedDrawables.isEmpty();
    }


    public void reloadImage() throws IOException {
        String imgPath = controller.getImgPath();
        if (!imgPath.equals("")) {
            img = ImageIO.read(new FileInputStream(imgPath));
            controller.setPreferredSize(new Dimension(img.getWidth(controller), img.getHeight(controller)));
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

}
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class Text extends Drawable {
    Point2D point;
    private String text = "";
    private Color color = Color.black;
    private int size = 50;
    //    offset since the font could be size 1 but kinda invisible
    private int offset = 10;
    private String fontName = "Comic Sans MS";

    Text(Point2D point, Color color, int size, String fontName) {
        this.point = point;
        this.text = "";
        this.color = color;
        this.size = size;
        this.fontName = fontName;
    }

    Text(Point2D point, Color color, int size, String fontName, String text) {
        this.point = point;
        this.text = "";
        this.color = color;
        this.size = size;
        this.fontName = fontName;
        this.text = text;
    }

    @Override
    public void draw(Graphics2D g) {

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font(fontName, Font.PLAIN, size + offset));

        if (isSelected()) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(color);
        }

        if (text != null) {
            if (text.contains(System.getProperty("line.separator"))) {
                int lineHeight = g.getFontMetrics().getHeight();
                int pX = getX();
                int pY = getY();
                int newH = pY;
                for (String line : text.split(System.getProperty("line.separator"))) {
                    g.drawString(line, pX, newH);
                    newH = newH + lineHeight;
                }
            } else {
                g.drawString(text, (int) point.getX(), (int) point.getY());
            }
        }

    }


    void fixTooBig(Graphics2D g, int x, int y, int width, int height) {

        int descent = g.getFontMetrics().getMaxDescent();
        int ascent = g.getFontMetrics().getMaxAscent();
        int lineHeight = g.getFontMetrics().getHeight();
        int count = -1;
        for (String line : text.split(System.getProperty("line.separator"))) {
            count += 1;
        }


        if (count * lineHeight + this.getY() + descent > y + height) {
            this.text = this.text.substring(0, text.length() - 1);
            return;
        }

        if (this.getY() - ascent < y) {
            this.text = this.text.substring(0, text.length() - 1);
            return;
        }


        int sWidth;
        for (String line : text.split(System.getProperty("line.separator"))) {
            sWidth = g.getFontMetrics().stringWidth(line);

            if (sWidth + this.getX() >= width + x) {


                int index = text.lastIndexOf(' ');

                if (index > 0) {
                    text = text.substring(0, index) + System.getProperty("line.separator") + text.substring(index + 1);
                }
            }

            sWidth = g.getFontMetrics().stringWidth(line);
            if (sWidth + this.getX() > width + x) {
                this.text = this.text.substring(0, text.length() - 1);
            }

        }

        fireChangeListeners();
    }


    private int getX() {
        return (int) point.getX();
    }

    private void setPoint(int x, int y) {
        point.setLocation((int) x, (int) y);
    }

    private int getY() {
        return (int) point.getY();
    }

    Point2D getPoint() {
        return this.point;
    }

    String getText() {
        return text;
    }

    void addCharToText(char c) {
        this.text = text + c;
        fireChangeListeners();
    }


    public boolean hitTest(Point2D point, Graphics2D g) {
        if (text == null) {
            return false;
        }
        int pointX = (int) point.getX();
        int pointY = (int) point.getY();
        int descent = g.getFontMetrics().getMaxDescent();
        int ascent = g.getFontMetrics().getMaxAscent();
        int lineHeight = g.getFontMetrics().getHeight();
        int count = -1;
        int stringWidth = 0;
        for (String line : text.split(System.getProperty("line.separator"))) {
            count += 1;
            var newStringWidth = g.getFontMetrics().stringWidth(line);
            if (newStringWidth > stringWidth) {
                stringWidth = newStringWidth;
            }
        }

        return this.getY() - ascent <= pointY && pointY <= count * lineHeight + this.getY() + descent &&
                this.getX() <= pointX && pointX <= stringWidth + this.getX();
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    public int getSize() {
        return size;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName){
        this.fontName = fontName;
    }

    @Override
    public void moveBy(int dx, int dy) {
        int x = (int) dx + (int) point.getX();
        int y = (int) dy + (int) point.getY();
        setPoint(x, y);
    }

    public void setSize(int size){
        this.size =size;
    }

}

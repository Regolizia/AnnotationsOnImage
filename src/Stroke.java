import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;


import static java.awt.Color.black;

public class Stroke extends Drawable {

    private ArrayList<Point> points;
    private Color color = black;
    private int stroke = 50;

    public Stroke(int stroke, Color color) {
        this.points = new ArrayList<>();
        this.stroke = stroke;
        this.color = color;
    }


    @Override
    public void draw(Graphics2D g) {


        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setPaint(color);

        if (isSelected()) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(color);
        }
        g.setStroke(new BasicStroke((float) stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int i = 1; i < points.size(); i++) {
            int oldMouseX = points.get(i - 1).x;
            int oldMouseY = points.get(i - 1).y;
            int currMouseX = points.get(i).x;
            int currMouseY = points.get(i).y;
            g.drawLine(oldMouseX, oldMouseY, currMouseX, currMouseY);

        }

    }


    public void addPointXY(int x, int y) {
        Point p = new Point(x, y);
        this.points.add(p);

        fireChangeListeners();
    }

    public boolean hitTest(Point2D point, Graphics2D g) {
        int hitX = (int) point.getX();
        int hitY = (int) point.getY();

        for (int i=0;i< points.size();i++) {

            if (Math.pow((hitX - (int) points.get(i).getX()), 2) + Math.pow((hitY - (int) points.get(i).getY()), 2) < Math.pow((stroke / 2), 2)
                    || (i>0 && hitOnSegment(points.get(i-1), points.get(i), point))){
                return true;
            }
        }
        return false;
    }

    boolean hitOnSegment(Point2D x, Point2D y, Point2D z) {
            Line2D line = new Line2D.Double(x.getX(), x.getY(), y.getX(), y.getY());
            return line.ptSegDist(z) <= stroke /2;
    }


    @Override
    public void moveBy(int dx, int dy) {

        for (Point p : points) {
            p.x += dx;
            p.y += dy;
        }
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    public int getStroke() {
        return stroke;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setSize(int size){
        this.stroke =size;
    }

}

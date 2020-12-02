import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class Drawable {

    public ArrayList<ChangeListener> changeListeners = new ArrayList<>();
    private boolean isSelected;

    public abstract void draw(Graphics2D g);

    ;

    public void addChangeListener(ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    protected void fireChangeListeners() {
        for (ChangeListener changeListener : changeListeners) {
            changeListener.stateChanged(new ChangeEvent(this));
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public abstract boolean hitTest(Point2D point, Graphics2D g);

    public abstract void setColor(Color c);


    public abstract void moveBy(int dx, int dy);

    public abstract void setSize(int size);
}

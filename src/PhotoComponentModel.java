import org.json.simple.JSONObject;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PhotoComponentModel {

    private String imgPath = "";

    private boolean flipped = false;
    private boolean drawing = false;
    private boolean writing = false;
    private int stroke = 50;
    private Color color = Color.black;
    private String font = "Comic Sans MS";
    private JSONObject image;
    private boolean shiftPressed = false;


    private ArrayList<Drawable> drawables = new ArrayList<>();
    private ArrayList<Drawable> selectedDrawables = new ArrayList<>();

    private ArrayList<ChangeListener> changeListeners = new ArrayList<>();
    private ArrayList<ChangeListener> pathChangeListeners = new ArrayList<>();

    public PhotoComponentModel(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgPath() {
        return this.imgPath;
    }

    public void setImgPath(String path) {
        this.imgPath = path;
        firePathChangeListeners();
    }

    //    MANAGE LISTENERS

    private void notifyChangeListeners() {
        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }

    public void firePathChangeListeners() {
        for (ChangeListener listener : pathChangeListeners) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }

    public void addPathChangeListener(ChangeListener listener) {
        pathChangeListeners.add(listener);
    }

    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    public void addDrawable(Drawable drawable) {
        drawable.addChangeListener(e -> notifyChangeListeners());
        drawables.add(drawable);
        notifyChangeListeners();
    }

    public void addSelectedDrawable(Drawable drawable) {
        drawable.addChangeListener(e -> notifyChangeListeners());
        selectedDrawables.add(drawable);
        notifyChangeListeners();
    }

    public ArrayList<Drawable> getDrawables() {
        return drawables;
    }

    public ArrayList<Drawable> getSelectedDrawables() {
        return selectedDrawables;
    }

    public boolean isFlipped() {
        return this.flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
        notifyChangeListeners();
    }

    public boolean isDrawing() {
        return this.drawing;
    }

    public void setDrawing(boolean drawing) {
        this.drawing = drawing;
        notifyChangeListeners();
    }

    public boolean isWriting() {
        return this.writing;
    }

    public void setWriting(boolean writing) {
        this.writing = writing;
        notifyChangeListeners();
    }

    public int getStroke() {
        return stroke;
    }

    public void setStroke(int s) {
        this.stroke = s;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String fontName) {
        this.font = fontName;
    }

    public JSONObject getImage() {
        return image;
    }

    public void setImage(JSONObject image) {
        this.image = image;
    }

    public void resetArrays() {
        drawables = new ArrayList<>();
    }

}

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class PhotoComponent extends JComponent {

    private PhotoComponentModel model;
    private PhotoComponentUI view;

    public PhotoComponent(String title) throws IOException {

        this.model = new PhotoComponentModel(title);
        this.view = new PhotoComponentUI(this);

        this.model.addChangeListener(e -> repaint());
        this.model.addPathChangeListener(e -> {
            try {
                view.reloadImage();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

    }

    public ArrayList<Drawable> getDrawables() {
        return model.getDrawables();
    }

    public void addDrawable(Drawable drawable) {
        model.addDrawable(drawable);
    }

    public ArrayList<Drawable> getSelectedDrawables() {
        return model.getSelectedDrawables();
    }

    public void addSelectedDrawable(Drawable drawable) {
        model.addSelectedDrawable(drawable);
    }

    PhotoComponentModel getModel() {
        return this.model;
    }

    public void setModel(PhotoComponentModel model) {
        this.model = model;
    }

    void flipImage() {
        this.model.setFlipped(true);
        repaint();
    }

    void unflipImage() {
        this.model.setFlipped(false);
        repaint();
    }

    void drawing() {
        this.model.setDrawing(true);
    }

    void notDrawing() {
        this.model.setDrawing(false);
    }

    void writing() {
        this.model.setWriting(true);
    }

    void notWriting() {
        this.model.setWriting(false);
    }

    boolean isFlipped() {
        return this.model.isFlipped();
    }

    boolean isDrawing() {
        return this.model.isDrawing();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            view.paintComponent((Graphics2D) g);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return view.getPreferredSize();
    }

    @Override
    public Dimension getSize() {
        return view.getSize();
    }

    String getImgPath() {
        return model.getImgPath();
    }

    void setImgPath(String path) {
        model.setImgPath(path);
    }

    void reset() {
        model.resetArrays();
    }

    public void cleanEmptyText() {
        ArrayList<Drawable> toDelete = new ArrayList<>();
        for (Drawable d : getDrawables()) {
            if (d.getClass().getName().equals("Text")) {
                if (((Text) d).getText() == null || ((Text) d).getText().equals("")) {
                    toDelete.add(d);
                }
            }
        }
        getDrawables().removeAll(toDelete);
    }

    private String convertColor(Color c) {
        return "#" + Integer.toHexString(c.getRGB()).substring(2);
    }

    public void addJSONAnnotations() {

        cleanEmptyText();
        JSONArray drawablesJSON = (JSONArray) getModel().getImage().get("drawables");
        drawablesJSON.clear();

        for (Drawable drawable : getDrawables()) {

            if (drawable.getClass().getName().equals("Text")) {

                drawablesJSON.add(JSONtextFromDrawable(drawable));

            } else if (drawable.getClass().getName().equals("Stroke")) {

                drawablesJSON.add(JSONStrokeFromDrawable(drawable));
            }
        }
    }

    private Object JSONStrokeFromDrawable(Drawable drawable) {
        JSONArray pointsJSON = new JSONArray();
        JSONObject stroke = new JSONObject();

        stroke.put("color", convertColor(((Stroke) drawable).getColor()));
        stroke.put("strokeSize", ((Stroke) drawable).getStroke());
        stroke.put("points", pointsJSON);

        for (Point p : ((Stroke) drawable).getPoints()) {
            JSONObject point = new JSONObject();
            point.put("x", p.getX());
            point.put("y", p.getY());
            pointsJSON.add(point);
        }
        return stroke;
    }

    private JSONObject JSONtextFromDrawable(Drawable drawable) {
        JSONObject text = new JSONObject();
        text.put("x", ((Text) drawable).getPoint().getX());
        text.put("y", ((Text) drawable).getPoint().getY());
        text.put("color", convertColor(((Text) drawable).getColor()));
        text.put("size", ((Text) drawable).getSize());
        text.put("fontName", ((Text) drawable).getFontName());
        text.put("text", ((Text) drawable).getText());
        return text;
    }



}

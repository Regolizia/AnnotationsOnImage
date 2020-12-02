import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.Hashtable;
import static javax.swing.ScrollPaneConstants.*;

class PhotoBrowser extends JFrame {

    // COLORS
    private PhotoComponent photoComponent;
    private Color purple = Color.decode("#541388");
    private Color yellow = Color.decode("#FFD400");
    private Color white = Color.decode("#FFFFFF");
    private boolean hasImage = false;
    private JSONObject jsonObject = new JSONObject();
    //        WILL CHANGE THIS, IT IS NOT QUITE RIGHT EVEN THOUGH I SUM ALL THE PARTS
    private int toolbarWidth = 20;
    private int menuBarHeight = 30;
    private JLabel statusBar = new JLabel();


    PhotoBrowser(String title) {
        super(title);
        setupUI();

    }

    private void setupUI() {

        setupMainPanel();
        setupStatusBar();
        setupToolbar();

        this.setPreferredSize(new Dimension(800, 400));
        this.pack();
    }

    private void setupMainPanel() {
        try {
            photoComponent = new PhotoComponent("");

        } catch (IOException e) {
            e.printStackTrace();
        }
        JScrollPane scrollPane = new JScrollPane(photoComponent);
        scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBackground(white);
        scrollPane.revalidate();

        menuBarHeight += scrollPane.getHorizontalScrollBar().getHeight();
        toolbarWidth += scrollPane.getVerticalScrollBar().getWidth();

        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        setupMenubar(photoComponent);

    }

    private void setupStatusBar() {

        statusBar.setBackground(white);
        statusBar.setText(" ");
        this.getContentPane().add(statusBar, BorderLayout.SOUTH);

        this.pack();
        menuBarHeight += statusBar.getHeight();
    }

    PhotoComponent getPhotoComponent() {
        return photoComponent;
    }


    private void setupMenubar(PhotoComponent photoComponent) {

        photoComponent.setFocusable(true);
        JPanel p = new JPanel(new BorderLayout());
        JMenuBar menubar = new JMenuBar();
        menubar.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        p.add(menubar, BorderLayout.WEST);

        menubar.setBackground(white);
        p.setBackground(white);

        setupFileMenu(menubar);
        setupViewMenu(menubar);
        setupFontMenu(menubar);

        JPanel panel = new JPanel(new GridBagLayout());
        setupSizeSlider(panel);
        setupColorChooser(panel);
        p.add(panel, BorderLayout.EAST);

        this.getContentPane().add(p, BorderLayout.NORTH);

        this.pack();

        menuBarHeight += this.getContentPane().getComponent(1).getHeight();

    }

    private void setupFileMenu(JMenuBar menubar) {
        JMenu fileMenu = new JMenu("File");
        JMenuItem imp = new JMenuItem("Import");
        JMenuItem del = new JMenuItem("Delete");
        JMenuItem quit = new JMenuItem("Quit");

//        imp.setBorderPainted(false);
//        del.setBorderPainted(false);
//        quit.setBorderPainted(false);
        imp.setBackground(white);
        del.setBackground(white);
        quit.setBackground(white);

        imp.addActionListener(e -> {
            statusBar.setText("Import");
            if (hasImage) {
                save(photoComponent);
            }

            photoComponent.getModel().setFlipped(false);
            JFileChooser fc = new JFileChooser();

            fc.addChoosableFileFilter(new FileNameExtensionFilter(
                    "Image files", ImageIO.getReaderFileSuffixes()));
            fc.setAcceptAllFileFilterUsed(false);


            int i = fc.showOpenDialog(this);
            if (i == JFileChooser.APPROVE_OPTION) {
                hasImage = true;

                String path = fc.getSelectedFile().getAbsolutePath();
                photoComponent.setImgPath(path);
                photoComponent.revalidate();
                photoComponent.repaint();

                restoreNotes(photoComponent);

                Image img;
                try {
                    img = ImageIO.read(new FileInputStream(path));

                    int width = img.getWidth(null);
                    int height = img.getHeight(null);

                    width = width + toolbarWidth;
                    height = height + menuBarHeight;

                    Dimension d = new Dimension(width, height);

                    this.setPreferredSize(d);
                    this.setSize(d);
                    this.revalidate();
                    this.pack();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        del.addActionListener(e -> {
            statusBar.setText("Delete");

            if (hasImage) {
                System.out.println("SAVING and CLEANING...");
                saveAndReset(photoComponent);
                hasImage = false;

                photoComponent.setImgPath("");
                photoComponent.revalidate();
                photoComponent.repaint();
            }
        });

        quit.addActionListener(e -> {
            save(photoComponent);
            System.exit(0);
        });

        fileMenu.add(imp);
        fileMenu.add(del);
        fileMenu.add(quit);
        menubar.add(fileMenu);
    }

    private void setupViewMenu(JMenuBar menubar) {

        JMenu viewMenu = new JMenu("View");
        JRadioButtonMenuItem viewer = new JRadioButtonMenuItem("Photo viewer");
        JRadioButtonMenuItem browser = new JRadioButtonMenuItem("Browser");

        viewer.setBackground(white);
        browser.setBackground(white);

        viewer.setBorderPainted(false);
        browser.setBorderPainted(false);
        viewer.setSelected(true);
        viewer.addActionListener(e -> {
            statusBar.setText("Photo viewer");
            browser.setSelected(false);
        });

        browser.addActionListener(e -> {
            statusBar.setText("Browser");
            viewer.setSelected(false);
        });
        viewMenu.add(viewer);
        viewMenu.add(browser);
        menubar.add(viewMenu);
    }

    private void setupColorChooser(JPanel panel) {
        JColorChooser colorChooser = new JColorChooser();
        colorChooser.setColor(Color.black);
        colorChooser.setBackground(white);
        panel.setBackground(white);
        colorChooser.getSelectionModel().addChangeListener(e -> {

            Color color = colorChooser.getColor();
            photoComponent.getModel().setColor(color);
            for (Drawable d : photoComponent.getSelectedDrawables()) {
                d.setColor(color);
            }
        });
        AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();

        colorChooser.setChooserPanels(new AbstractColorChooserPanel[]{panels[0]});
        colorChooser.setPreviewPanel(new JPanel());

        colorChooser.setFocusable(false);
        panel.add(colorChooser);
    }

    private void setupSizeSlider(JPanel panel) {

        JSlider jSlider = new JSlider();
        jSlider.setBackground(white);
        jSlider.setMajorTickSpacing(25);
        jSlider.setMinorTickSpacing(5);
        jSlider.setMaximum(100);
        jSlider.setMinimum(1);
        jSlider.setSnapToTicks(true);
        jSlider.addChangeListener(evt -> {
            int size = jSlider.getValue();
            photoComponent.getModel().setStroke(size);
            changeSizeTo(size);
        }
        );

        jSlider.setPaintLabels(true);
        Hashtable position = new Hashtable();
        position.put(1, new JLabel("1"));
        position.put(25, new JLabel("25"));
        position.put(50, new JLabel("50"));
        position.put(75, new JLabel("75"));
        position.put(100, new JLabel("100"));
        jSlider.setLabelTable(position);
        jSlider.setFocusable(false);
        panel.add(jSlider);

    }

    private void setupFontMenu(JMenuBar menubar){
        JMenu fontMenu = new JMenu("Fonts");
        JMenuItem font1 = new JMenuItem("Comic Sans");
        JMenuItem font2 = new JMenuItem("Calibri");

        font1.setBorderPainted(false);
        font2.setBorderPainted(false);
        font1.setBackground(white);
        font2.setBackground(white);

        font1.addActionListener(e -> {
            changeFontTo("Comic Sans MS");
        });

        font2.addActionListener(e -> {
            changeFontTo("Calibri");
        });

        fontMenu.add(font1);
        fontMenu.add(font2);
        menubar.add(fontMenu);
    }

    private void changeFontTo(String fontName){
        statusBar.setText("Changed font to "+ fontName);
        photoComponent.getModel().setFont(fontName);
        for (Drawable d : photoComponent.getSelectedDrawables()) {
            if(d.getClass().getName()=="Text")
            {
                ((Text)d).setFontName(fontName);
            }
        }
    }

    private void changeSizeTo(int size){
        statusBar.setText("Changed size to "+ size);
        photoComponent.getModel().setStroke(size);
        for (Drawable d : photoComponent.getSelectedDrawables()) {
            d.setSize(size);

        }
    }

    private void restoreNotes(PhotoComponent photoComponent) {
        try {
            photoComponent.requestFocus();
            JSONArray imageList = new JSONArray();

            File file = new File("src/resources/saveFile.json");
            if (!file.exists()) {

                jsonObject.put("imageList", imageList);
                System.out.println("Created New Save File");

            } else {

                FileReader reader = new FileReader(file);
                JSONParser jsonParser = new JSONParser();
                jsonObject = (JSONObject) jsonParser.parse(reader);
                imageList = (JSONArray) jsonObject.get("imageList");
            }

            JSONObject image = new JSONObject();

            boolean foundImage = searchForImageAndRestoreData(imageList);

            if (!foundImage) {

                JSONArray drawables = new JSONArray();
                image = new JSONObject();
                image.put("path", photoComponent.getModel().getImgPath());
                image.put("drawables", drawables);
                imageList.add(image);

                photoComponent.getModel().setImage(image);
                photoComponent.reset();
            }

            photoComponent.revalidate();
            photoComponent.repaint();

        } catch (ParseException | NullPointerException | IOException ex) {
            ex.printStackTrace();
        }

    }

    private boolean searchForImageAndRestoreData(JSONArray imageList){
        boolean foundImage = false;

        if (imageList != null) {

            for (Object o : imageList) {
                JSONObject image = (JSONObject) o;
                String path = (String) image.get("path");

                if (path.equals(photoComponent.getModel().getImgPath())) {

                    photoComponent.getModel().setImage(image);
                    photoComponent.reset();

                    foundImage = true;

                    restoreDataOfThisImage(image);
                }
            }
        }
        return foundImage;
    }

    private void restoreDataOfThisImage(JSONObject image) {
        JSONArray drawables = (JSONArray) image.get("drawables");
        for (Object drawable : drawables) {

            JSONObject obj = (JSONObject) drawable;

            if (obj.get("fontName") != null) {

                restoreText(obj);

            } else {

                restoreStroke(obj);
            }
        }
    }

    private void restoreStroke(JSONObject obj) {
        String colorAsString = (String) obj.get("color");
        Color color = Color.decode(colorAsString);

        int strokeSize = ((Number) obj.get("strokeSize")).intValue();

        Stroke strokeToAdd = new Stroke(strokeSize, color);

        JSONArray points = (JSONArray) obj.get("points");
        for (Object o : points) {
            JSONObject point = (JSONObject) o;
            int x = ((Number) point.get("x")).intValue();
            int y = ((Number) point.get("y")).intValue();

            strokeToAdd.addPointXY(x, y);
        }

        photoComponent.addDrawable(strokeToAdd);
    }

    private void restoreText(JSONObject obj) {
        String fontName = (String) obj.get("fontName");

        String colorAsString = (String) obj.get("color");
        Color color = Color.decode(colorAsString);

        int size = ((Number) obj.get("size")).intValue();
        int xText = ((Number) obj.get("x")).intValue();
        int yText = ((Number) obj.get("y")).intValue();

        String textString = (String) obj.get("text");

        Point textPoint = new Point(xText, yText);

        photoComponent.addDrawable(new Text(textPoint, color, size, fontName, textString));
    }

    private void setupToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setBackground(white);

        JToggleButton dogsButton = new JToggleButton("Folder 1");
        JToggleButton catsButton = new JToggleButton("Folder 2");
        JToggleButton birdsButton = new JToggleButton("Folder 3");

        dogsButton.setBackground(white);
        catsButton.setBackground(white);
        birdsButton.setBackground(white);
//        dogsButton.setBorderPainted(false);
//        dogsButton.setContentAreaFilled(false);


        dogsButton.setFocusable(false);
        catsButton.setFocusable(false);
        birdsButton.setFocusable(false);

        dogsButton.setBorderPainted(false);
        catsButton.setBorderPainted(false);
        birdsButton.setBorderPainted(false);

        dogsButton.addActionListener(e -> {
            statusBar.setText("Folder 1");
            catsButton.setSelected(false);
            birdsButton.setSelected(false);
        });

        catsButton.addActionListener(e -> {
            statusBar.setText("Folder 2");
            dogsButton.setSelected(false);
            birdsButton.setSelected(false);
        });

        birdsButton.addActionListener(e -> {
            statusBar.setText("Folder 3");
            catsButton.setSelected(false);
            dogsButton.setSelected(false);
        });

        toolbar.setLayout(new GridLayout(3, 1));
        toolbar.add(dogsButton);
        toolbar.add(catsButton);
        toolbar.add(birdsButton);

        this.getContentPane().add(toolbar, BorderLayout.WEST);
        this.pack();

        toolbarWidth += this.getContentPane().getComponent(3).getWidth();

    }

    void save(PhotoComponent photoComponent) {
        if (hasImage) {
            photoComponent.addJSONAnnotations();

            var fileName = "src" + File.separator + "resources" + File.separator + "saveFile.json";
            try (FileWriter file = new FileWriter(fileName)) {

                System.out.println("SAVING...");
                file.write(jsonObject.toString());
                file.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAndReset(PhotoComponent photoComponent) {

        photoComponent.addJSONAnnotations();

        var fileName = "src" + File.separator + "resources" + File.separator + "saveFile.json";
        try (FileWriter file = new FileWriter(fileName)) {

            file.write(jsonObject.toString());
            file.flush();
            photoComponent.reset();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

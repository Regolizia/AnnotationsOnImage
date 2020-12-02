import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    public static void main(String args[]) {

        PhotoBrowser photoBrowser = new PhotoBrowser("PhotoBrowser");
        photoBrowser.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        photoBrowser.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JFrame frame = (JFrame) e.getSource();
                photoBrowser.save(photoBrowser.getPhotoComponent());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
        photoBrowser.setVisible(true);

    }
}

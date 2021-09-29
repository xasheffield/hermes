import GUI.*;
import com.apple.eawt.Application;
import org.apache.commons.*;

import javax.swing.*;
import java.awt.*;

public class Main {

    private static final String OPERATING_SYSTEM = System.getProperty("os.name");
    public static void main(String[] args) {

        if (OPERATING_SYSTEM.startsWith("Mac")) {
            System.setProperty("apple.awt.application.name", "Hermes"); //Update application name - must be done before any swing is used
            Application application = Application.getApplication();
            Image image = Toolkit.getDefaultToolkit().getImage("resources/hermes_logo.png");
            application.setDockIconImage(image);
            //TODO deprecated API for changing dock icon - update as per https://stackoverflow.com/a/56924202

            //Should set application name on MacOs

            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name", "Name"); //TODO working implementation - may need MacOS specific app bundle
        }

        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        GUI gui = new GUI("Hermes");
    }

}

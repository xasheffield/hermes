import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class GUI extends JFrame {
    private JPanel basePanel;
    private JButton fileChooser;
    private JPanel image1Panel;
    private JLabel image2;
    private JLabel image1;
    private JPanel controlPanel;
    private JTabbedPane tabbedPane1;
    private JButton continueButton;
    private JList list1;
    private JButton loadFilesButton;
    private JButton button2;


    private JTabbedPane rootTabPane; //Base panel, which encapsulates the rest of the program
    private JButton generateMeanButton;
    private JPanel plottingPanel;
    private JList list2;
    private JList list3;
    private JButton button3;
    private JButton button4;
    private JCheckBox plotWithYOffsetCheckBox;
    private JTextField textField1;
    private JCheckBox plotPolynomialFitCheckBox;
    private JButton plotGraphsButton;
    private JTextField textField2;
    private JTextField textField3;
    private JCheckBox backgroundIsSignificantCheckBox;
    private JButton plotAbsorptionButton;
    private JButton generateAbsorptionFileButton;
    private JButton button1;


    public GUI(String title) {
        super(title);
        this.pack();
        this.initComponents();
        this.setContentPane(rootTabPane);
        //addImages();

        fileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileChooser();

            }
        });
        this.pack();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents(){
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(600,450));
        this.setLocationRelativeTo(null);//Centers the frame
        this.fileChooser = fileChooser;
    }

    private void openFileChooser(){
        File file;
        Scanner fileIn;
        int response;
        JFileChooser chooser = new JFileChooser(".");

        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        response = chooser.showOpenDialog(null);

        if (response == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            System.out.println(file.getAbsolutePath());
        }
    }

    private void addImages(){
        try {
            BufferedImage myPicture = ImageIO.read(new File("/Users/Marco/Desktop/graph1.png"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            image1Panel.add(picLabel);

            myPicture = ImageIO.read(new File("/Users/Marco/Desktop/graph2.png"));
            picLabel = new JLabel(new ImageIcon(myPicture));
            image2 = picLabel;

            SwingUtilities.updateComponentTreeUI(this);
        } catch (IOException e) {
            System.out.println("File was unable to be read");
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
            /*
            BufferedImage image1 = ImageIO.read(new File("/Users/Marco/Desktop/graph1.png"));
            JLabel picLabel = new JLabel(new ImageIcon(image1));
            this.image1 = picLabel; */
            this.image1 = new JLabel();


            /*
            BufferedImage image2 = ImageIO.read(new File("/Users/Marco/Desktop/graph2.png"));
            picLabel = new JLabel(new ImageIcon(image2));
            this.image2 = picLabel;
             */
            this.image2 = new JLabel();
    }
}

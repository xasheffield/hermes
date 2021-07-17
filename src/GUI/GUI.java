package GUI;

import DataProcessing.Models.DataFile;
import DataProcessing.Models.DataManager;
import DataProcessing.Models.MeasurementType;
import IO.FileLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class GUI extends JFrame {
    private JPanel basePanel;
    private JButton fileChooser;
    private JPanel image1Panel;
    private JLabel image2;
    private JLabel image1;
    private JPanel controlPanel;
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
    private JButton generatePolynomialButton;
    private JList list4;
    private JList list5;
    private JList list6;
    private JComboBox dataTypeComboBox;
    private JButton resetButton;
    private JButton continueButton;

    FileLoader fileLoader;
    DataManager dataManager;


    public GUI(String title) {
        super(title);
        this.pack();
        this.initComponents();
        this.pack();
        this.setContentPane(rootTabPane);
        this.fileLoader = new FileLoader();
        this.dataManager = new DataManager();
        //addImages();

        loadFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File[] filesToLoad = openFileChooser();
                //If no files are found, return and print an error
                if (filesToLoad.length == 0) {
                    System.out.println("No valid files seem to have been selected");
                    return;
                }

                //If this is the first time loading data, create a window prompting the user to select
                //which columns correspond to the relevant data
                if (!fileLoader.valuesInitialised) {
                    String[] columnNames = fileLoader.getColumnNames(filesToLoad[0]);

                    columnSelectionDialog(columnNames);
                    /*
                    FileLoadingGUI flGUI = new FileLoadingGUI("Think of a name", fileLoader, filesToLoad);
                    Thread selectionGUI = new Thread(flGUI);
                    //while (flGUI.isVisible()) {
                    //    System.out.println("asd");
                    //}
                    selectionGUI.start();
                    try {
                        Thread.currentThread().wait();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    System.out.println(selectionGUI.getState());
                    System.out.println(Thread.currentThread().getState());
                    Thread.currentThread().run();

                     */


                }
                //Get the MeasurementType of files to be loaded from user input
                MeasurementType fileType = MeasurementType.valueOf((String) dataTypeComboBox.getSelectedItem());

                //Load files and store in DataManager
                ArrayList<DataFile> loadedFiles = fileLoader.loadFiles(fileType, filesToLoad);
                dataManager.addFiles(loadedFiles, fileType);
                System.out.println("Let's see");
            }
        });
        this.pack();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void columnSelectionDialog(String[] columnNames) {
        int input;
        JComboBox box = new JComboBox(columnNames);
        input = JOptionPane.showConfirmDialog(this, box, "Energy", JOptionPane.DEFAULT_OPTION);

        if (input == JOptionPane.OK_OPTION) {
            int energyIndex = box.getSelectedIndex();
            System.out.println(energyIndex);
        }
    }

    private void initComponents(){
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(600,450));
        this.setLocationRelativeTo(null);//Centers the frame
        this.fileChooser = fileChooser;
    }

    /**
     * Opens an interface allowing a user to sele
     * @return The files selected by the user, in an array. Returns an empty array if no files are selected.
     */
    private File[] openFileChooser(){
        File[] files;
        Scanner fileIn;
        int response;
        JFileChooser chooser = new JFileChooser(".");

        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setMultiSelectionEnabled(true);
        response = chooser.showOpenDialog(null);

        if (response == JFileChooser.APPROVE_OPTION) {
            files = chooser.getSelectedFiles();
            for (File file: files) {
                System.out.println(file.getAbsolutePath());
            }
            return files;
        }
        return new File[0];
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

    private void addActionListeners() {
        
    }
}

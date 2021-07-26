package GUI;

import DataProcessing.Models.DataFile;
import DataProcessing.Models.DataManager;
import DataProcessing.Models.MeasurementType;
import DataProcessing.Processors.DataProcessor;
import IO.FileLoader;
import IO.FileWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class GUI extends JFrame {
    private JPanel basePanel;
    private JButton fileChooser;
    private JPanel image1Panel;
    private JLabel image2;
    private JLabel image1;
    private JPanel controlPanel;
    private JList i0List;
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
    private JList i0bList;
    private JList itList;
    private JList itbList;
    private JComboBox dataTypeComboBox;
    private JButton resetButton;
    private JButton continueButton;
    private JButton resetDataButton;

    FileLoader fileLoader;
    FileWriter fileWriter;
    DataManager dataManager;
    DataProcessor dataProcessor;


    public GUI(String title) {
        super(title);
        this.pack();
        this.initComponents();
        this.pack();
        this.setContentPane(rootTabPane);

        /**
         * Initialise IO and DataProcessing
         */
        this.fileLoader = new FileLoader();
        this.fileWriter = new FileWriter();
        this.dataManager = new DataManager();
        this.dataProcessor = new DataProcessor();
        this.pack();

        /**
         * Add action listeners
         */
        loadFilesButton.addActionListener(loadFilesActionListener());
        generateMeanButton.addActionListener(generateMeanActionListener());
        generatePolynomialButton.addActionListener(generatePolynomialActionListener());
        resetDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you would like to clear all loaded files?");
                //TODO DRY
                if (option == JOptionPane.YES_OPTION) {
                    ((DefaultListModel) i0List.getModel()).removeAllElements();
                    ((DefaultListModel) i0bList.getModel()).removeAllElements();
                    ((DefaultListModel) itList.getModel()).removeAllElements();
                    ((DefaultListModel) itbList.getModel()).removeAllElements();
                }
            }
        });
    }


    /**
     * A pop-up dialog which prompts users to input correct columns to use
     * @param columnNames - The names of columns in the data
     * @param fl - FileLoader, in order to set the column indeces
     */
    private boolean columnSelectionDialog(String[] columnNames, FileLoader fl) {
        JComboBox energyBox = new JComboBox(columnNames);
        JComboBox thetaBox = new JComboBox(columnNames);
        JComboBox countsBox = new JComboBox(columnNames);
        JLabel eLabel = new JLabel("Energy");
        JLabel tLabel = new JLabel("Theta");
        JLabel cLabel = new JLabel("Counts");

        Object[] options = new Object[] {energyBox, thetaBox, countsBox};

        JPanel optionPanel = new JPanel();
        optionPanel.add(eLabel);
        optionPanel.add(energyBox);
        optionPanel.add(Box.createVerticalStrut(15));
        optionPanel.add(tLabel);
        optionPanel.add(thetaBox);
        optionPanel.add(Box.createVerticalStrut(15));
        optionPanel.add(cLabel);
        optionPanel.add(countsBox);

        int result = JOptionPane.showConfirmDialog(null, optionPanel,
                "Please Select Energy, Theta, Counts", JOptionPane.OK_CANCEL_OPTION);

        // If the user presses okay button
        if (result == JOptionPane.YES_OPTION) {
            int energyIndex = energyBox.getSelectedIndex();
            int thetaIndex = thetaBox.getSelectedIndex();
            int countsIndex = countsBox.getSelectedIndex();
            fl.setEnergyIndex(energyIndex);
            fl.setThetaIndex(thetaIndex);
            fl.setCountsIndex(countsIndex);
            fl.setValuesInitialised(true);
            return true;
        }
        else {
            JOptionPane.showMessageDialog(this, "File generation cancelled");
            return false;

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
     * Opens an interface allowing a user to select a file from file system
     * @return The file(s) selected by the user, in an array. Returns an empty array if no files are selected.
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

    private ActionListener loadFilesActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File[] filesToLoad = openFileChooser();
                //If no files are found, return and print an error
                if (filesToLoad.length == 0) {
                    System.out.println("No valid files have been selected");
                    return;
                }

                //If this is the first time loading data, create a window prompting the user to select
                //which columns correspond to the relevant data, and update FileLoader to save these values
                if (!fileLoader.valuesInitialised) {
                    String[] columnNames = fileLoader.getColumnNames(filesToLoad[0]);
                    if (columnNames.length == 0) {
                        return;
                    }

                    //Cancel file generation if user exits column selection popup
                    if (!columnSelectionDialog(columnNames, fileLoader)) {
                        return;
                    }
                }
                //Get the MeasurementType of files to be loaded from user input
                MeasurementType fileType = MeasurementType.valueOf((String) dataTypeComboBox.getSelectedItem());

                //Load files and store in DataManager
                ArrayList<DataFile> loadedFiles = fileLoader.loadFiles(fileType, filesToLoad);
                dataManager.addFiles(loadedFiles, fileType);

                //Set model of corresponding list
                JList listToUpdate = getList(fileType);

                //Get and update appropriate list model
                DefaultListModel model = (DefaultListModel) listToUpdate.getModel();
                loadedFiles.stream().forEach(file -> model.addElement(file));
                pack();
            }
        };
    }

    /**
     * Adds functionality to generate mean button. Creates a pop-up dialogue prompting
     * user to name the file and provide a header.
     * @return
     */
    private ActionListener generateMeanActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO ask if user wants to use entire energy range
                //If not, ask user to specify a range (in energy)

                String fileName = JOptionPane.showInputDialog("Enter File Name", "");
                String header = JOptionPane.showInputDialog("Enter File Header", "");
                MeasurementType type = MeasurementType.valueOf((String) dataTypeComboBox.getSelectedItem());
                List<DataFile> files = getList(type).getSelectedValuesList();
                if (files.isEmpty()) {
                    JOptionPane.showMessageDialog(new JFrame(), "No data files found of type: " + type.toString());
                    return;
                }
                DataFile meanFile = dataProcessor.generateMean(type, fileName, header, files.toArray(new DataFile[0]));

                try {
                    fileWriter.writeDataFile(meanFile);
                    String directoryPath = Paths.get(meanFile.getFilePath()).getParent().toString() + System.getProperty("file.separator");
                    fileWriter.writeLstFile(files, fileName, header, directoryPath);
                } catch (IOException ioe) {

                }
            }
        };
    }

    /**
     * Functionality for generate polynomial button. Prompts user to to enter file name, header, and choose degree to fit
     * @return
     */
    private ActionListener generatePolynomialActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(new JFrame(), "Something");

            }
        };
    }

    private JList getList(MeasurementType type) {
        switch (type) {
            case I0: return i0List;
            case I0b: return i0bList;
            case It: return itList;
            case Itb: return itbList;
        }
        return null;
    }
}

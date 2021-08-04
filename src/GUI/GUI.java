package GUI;

import DataProcessing.Models.*;
import DataProcessing.Processors.DataProcessor;
import Graphing.Grapher;
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
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class GUI extends JFrame {
    private JPanel basePanel;
    private JButton fileChooser;
    private JPanel image1Panel;
    private JLabel image2;
    private JLabel image1;
    private JPanel controlPanel;
    private JList i0List; // TODO either format all lists like this, with the title in the border of the JList, or remove for this one
    private JButton loadFilesButton;
    private JButton button2;


    private JTabbedPane rootTabPane; //Base panel, which encapsulates the rest of the program
    private JButton generateMeanButton;
    private JList plotFileList;
    private JCheckBox plotWithYOffsetCheckBox;
    private JTextField offsetInput;
    private JButton plotGraphsButton;
    private JCheckBox backgroundIsSignificantCheckBox;
    private JButton plotAbsorptionButton;
    private JButton generateAbsorptionFileButton;
    private JButton generatePolynomialButton;
    private JList i0bList;
    private JList itList;
    private JList itbList;
    private JComboBox dataTypeComboBox;
    private JButton resetPlotFilesButton;
    private JButton resetAllDataButton;
    private JButton resetDataButton;
    private JPanel i0bPlotFiles;
    private JButton resetDataFormatButton;


    FileLoader fileLoader;
    FileWriter fileWriter;
    DataManager dataManager;
    DataProcessor dataProcessor;
    Grapher grapher;


    public GUI(String title) {
        super(title);
        this.pack();
        this.initComponents();
        this.pack();
        this.setContentPane(rootTabPane);

        CheckBoxList test = new CheckBoxList();
        DefaultListModel model = new DefaultListModel();
        model.addElement(new JCheckBox("I0UL3Si1266_1_alldata_1.txt"));
        model.addElement(new JCheckBox("I0UL3Si1266_1_alldata_2.txt"));
        model.addElement(new JCheckBox("I0UL3Si1266_1_alldata_3.txt"));
        test.setModel(model);
        //i0bPlotFiles.add(test);

        /**
         * Initialise IO, DataProcessing and Grapher
         */
        fileLoader = new FileLoader();
        fileWriter = new FileWriter();
        dataManager = new DataManager();
        dataProcessor = new DataProcessor();
        grapher = new Grapher();

        /*
        ChartPanel cp = grapher.createGraph();
        plotPanel.setLayout(new java.awt.BorderLayout());
        plotPanel.add(cp);
        plotPanel.validate();

         */
        this.pack();

        /**
         * Add action listeners
         */
        loadFilesButton.addActionListener(loadFilesActionListener());
        generateMeanButton.addActionListener(generateMeanActionListener());
        generatePolynomialButton.addActionListener(generatePolynomialActionListener());
        resetAllDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you would like to clear all loaded files?");

                //Clear each list if user decides to proceed
                if (option == JOptionPane.YES_OPTION) {
                    for (MeasurementType mt : MeasurementType.values()) {
                        ((DefaultListModel) getList(mt).getModel()).removeAllElements();
                    }
                }/*
                if (option == JOptionPane.YES_OPTION) {
                    ((DefaultListModel) i0List.getModel()).removeAllElements();
                    ((DefaultListModel) i0bList.getModel()).removeAllElements();
                    ((DefaultListModel) itList.getModel()).removeAllElements();
                    ((DefaultListModel) itbList.getModel()).removeAllElements();
                }
                */
            }
        });
        plotGraphsButton.addActionListener(plotGraphsListener());
        /*
        plotFileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if ( SwingUtilities.isRightMouseButton(e) )
                {
                    System.out.println("Right click");

                }
            }
        });
        Right click test
         */
        /*
        resetPlotFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DefaultListModel)plotFileList.getModel()).removeAllElements();
            }
        });

         */
        resetDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MeasurementType typeToClear = MeasurementType.valueOf((String) dataTypeComboBox.getSelectedItem());
                int result = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you would like to clear all loaded "
                        + typeToClear.toString() + " files?");
                if (result == JOptionPane.YES_OPTION) {
                    ((DefaultListModel) getList(MeasurementType.valueOf((String) dataTypeComboBox.getSelectedItem())).getModel()).removeAllElements();
                }
            }
        });
        plotAbsorptionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (backgroundIsSignificantCheckBox.isSelected()) {
                    //TODO case split
                }
                DataFile i0 = (DataFile) i0List.getModel().getElementAt(0);
                DataFile it = (DataFile) itList.getModel().getElementAt(0);
                ArrayList<Double> absorption = dataProcessor.calculateAbsorption(i0, it);

                //JOptionPane.showOptionDialog();
                //TODO give user way to select files
            }
        });
        resetDataFormatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileLoader.setValuesInitialised(false);
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

                String[] columnNames = fileLoader.parseColumnNames(filesToLoad[0]);
                if (!fileLoader.valuesInitialised || !Arrays.equals(columnNames, fileLoader.getColumnNames())) {
                    if (columnNames.length == 0) {
                        return;
                    }

                    //Cancel file generation if user exits column selection popup
                    if (!columnSelectionDialog(columnNames, fileLoader)) {
                        return;
                    }
                    fileLoader.setColumnNames(columnNames);
                }

                        /*
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

                         */

                //Get the MeasurementType of files to be loaded from user input
                MeasurementType fileType = getSelectedType();

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

                MeasurementType type = getSelectedType();
                List<DataFile> files = getList(type).getSelectedValuesList();
                if (files.isEmpty()) {
                    JOptionPane.showMessageDialog(new JFrame(), "No data files found of type: " + type.toString());
                    return;
                }

                String fileName = JOptionPane.showInputDialog("Enter File Name", "");
                String header = JOptionPane.showInputDialog("Enter File Header", "");

                //Will be null if user cancelled input dialog, return if so
                if (fileName == null || header == null) {
                    //JOptionPane.showMessageDialog(new JFrame(), "Action cancelled.");
                    return;
                }

                DataFile meanFile = dataProcessor.generateMean(type, fileName, header, files.toArray(new DataFile[0]));
                try {
                    fileWriter.writeDataFile(meanFile);
                    String directoryPath = Paths.get(meanFile.getFilePath()).getParent().toString() + System.getProperty("file.separator");
                    fileWriter.writeLstFile(files, fileName, header, directoryPath);
                    dataManager.addFile(meanFile, type);
                    ((DefaultListModel) getList(type).getModel()).addElement(meanFile); //Update GUI to include new file
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(new JFrame(), "There was an error writing the file.");
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
                //double[] coeff = dataProcessor.generatePoly((DataFile) i0List.getSelectedValue(), 3);
                //TODO option for polynomial degree
                MeasurementType type = getSelectedType();
                JList list = getList(type);
                if (list.getSelectedValuesList().size() != 1) {
                    JOptionPane.showMessageDialog(new JFrame(), "Please select one " + getSelectedType().toString() + " file.");
                    return;
                }
                DataFile sourceFile = (DataFile) list.getSelectedValue();
                String fileName = JOptionPane.showInputDialog(new JFrame(), "Enter File Name");
                DataFile polynomialFile = dataProcessor.generatePolyFile(sourceFile, fileName);
                try {
                    //TODO tryWriteDataFile method which handles exception
                    fileWriter.writeDataFile(polynomialFile);
                    dataManager.addFile(polynomialFile, type);
                    ((DefaultListModel) getList(type).getModel()).addElement(polynomialFile);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    JOptionPane.showMessageDialog(new JFrame(), "Failed to write file.");
                }

            }
        };
    }

    private ActionListener plotGraphsListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JList selected = getList(getSelectedType());
                int[] selectedIndices = selected.getSelectedIndices();
                if (selectedIndices.length == 0) {
                    JOptionPane.showMessageDialog(new JFrame(), "Select " + getSelectedType() + " file(s) to plot");
                    return;
                }

                ArrayList<DataFile> filesToPlot = new ArrayList<>();
                for (int i: selectedIndices) {
                    filesToPlot.add((DataFile) selected.getModel().getElementAt(i));
                }

                //Plot data with offset, or on separate axes
                if (plotWithYOffsetCheckBox.isSelected()) {
                    try {
                        int offset = Integer.parseInt(offsetInput.getText());
                        grapher.displayOffsetGraph(filesToPlot, offset);
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(new JFrame(), "Please enter a valid offset");
                    }
                }
                else {
                    grapher.displayGraph(filesToPlot);
                }
            }
        };
    }

    /**
     * The JList containing the loaded files of a given measurement type
     * @param type MeasurementType of list to retrieve
     * @return JList object containing DataFiles
     */
    private JList getList(MeasurementType type) {
        switch (type) {
            case I0: return i0List;
            case I0b: return i0bList;
            case It: return itList;
            case Itb: return itbList;
        }
        return null;
    }

    private MeasurementType getSelectedType() {
        return MeasurementType.valueOf((String) dataTypeComboBox.getSelectedItem());
    }
}

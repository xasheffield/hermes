package GUI;

import Data.Models.*;
import Data.Processors.DataProcessor;
import Graphing.Grapher;
import IO.FileLoader;
import IO.FileWriter;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class GUI extends JFrame {
    private JPanel basePanel; //TODO remove?
    private JPanel controlPanel; //TODO remove?

    private JTabbedPane rootTabPane; //Base panel, which encapsulates the rest of the GUI


    /**
     * Data Input Tab
     */
    //
    private JList i0List;
    private JList i0bList;
    private JList itList;
    private JList itbList;
    //Input & processing components
    private JButton loadFilesButton;
    private JButton generateMeanButton;
    private JComboBox measurementTypeComboBox;
    private JButton generatePolynomialButton;
    private JButton resetDataButton;
    private JButton resetAllDataButton;
    //Plotting components
    private JCheckBox plotWithYOffsetCheckBox;
    private JTextField offsetInput;
    private JButton plotGraphsButton;

    /**
     * Absorption tab
     */
    private JScrollPane absi0Pane;
    private JScrollPane absitPane;
    private JScrollPane absitbPane;
    private JScrollPane absi0bPane;
    private JList absorptioni0List;
    private JList absorptioni0bList;
    private JList absorptionitList;
    private JList absorptionitbList;
    private JCheckBox leakageIsSignificantCheckBox;
    private JButton plotAbsorptionButton;
    private JButton generateAbsorptionFileButton;

    /**
     *
     */
    private JList correctionFilesList;
    private JButton correctScalesButton;

    /**
     * Cross-tab components
     */
    private DefaultListModel i0Model = (DefaultListModel) i0List.getModel();
    private DefaultListModel i0bModel = (DefaultListModel) i0bList.getModel();
    private DefaultListModel itModel = (DefaultListModel) itList.getModel();
    private DefaultListModel itbModel = (DefaultListModel) itbList.getModel();

    /**
     * Classes providing I0, Data Processing, and Graphing functionality
     */
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
        this.synchroniseLists();

        /**
         * Initialise IO, Data and Grapher
         */
        fileLoader = new FileLoader();
        fileWriter = new FileWriter();
        dataManager = new DataManager();
        dataProcessor = new DataProcessor();
        grapher = new Grapher();
        addActionListeners();
    }

    private ActionListener generateAbsFileListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String header = JOptionPane.showInputDialog("Enter File Header", "");
                if (header == null) { //Null if user cancelled input dialog
                    return;
                }
                DataFile absorptionFile = getAbsorptionFile();
                if (absorptionFile == null)
                    return;
                absorptionFile.setHeader(header);
                try {
                    File file = saveDialogue();
                    absorptionFile.setFilePath(file.getAbsolutePath());

                    ArrayList<DataFile> sourceFiles = new ArrayList<>();
                    DataFile i0 = (DataFile) absorptioni0List.getSelectedValue();
                    DataFile it = (DataFile) absorptionitList.getSelectedValue();
                    sourceFiles.add(i0); sourceFiles.add(it);

                    if (leakageIsSignificantCheckBox.isSelected()) {
                        DataFile i0b = (DataFile) absorptioni0bList.getSelectedValue();
                        DataFile itb = (DataFile) absorptionitbList.getSelectedValue();
                        sourceFiles.add(i0b); sourceFiles.add(itb);
                        fileWriter.writeAbsorptionFile(absorptionFile, i0, it, i0b, itb);
                    }
                    else
                        fileWriter.writeAbsorptionFile(absorptionFile, i0, it);
                    dataManager.addFile(absorptionFile, MeasurementType.ABSORPTION);
                    String directoryPath = Paths.get(absorptionFile.getFilePath()).getParent().toString() + System.getProperty("file.separator");
                    fileWriter.writeLstFile(sourceFiles, file.getName(), header, directoryPath);
                    JOptionPane.showMessageDialog(new JFrame(), ".dat and .lst files have been succesfully saved to " + absorptionFile.getFilePath());
                } catch (Exception ioException) {
                    ioException.printStackTrace();
                }
            }
        };
    }

    private void initComponents(){
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(600,450));
        this.setLocationRelativeTo(null);//Centers the frame
    }


    private void addActionListeners() {
        loadFilesButton.addActionListener(loadFilesActionListener());
        generateMeanButton.addActionListener(generateMeanActionListener());
        generatePolynomialButton.addActionListener(generatePolynomialActionListener());
        resetAllDataButton.addActionListener(resetAllDataListener());
        plotGraphsButton.addActionListener(plotGraphsListener());

        resetDataButton.addActionListener(resetDataListener());
        plotAbsorptionButton.addActionListener(plotAbsorptionListener());
        rootTabPane.addComponentListener(new ComponentAdapter() {
        });
        rootTabPane.addChangeListener(changeTabListener());
        leakageIsSignificantCheckBox.addActionListener(bgAbsorptionListener());
        generateAbsorptionFileButton.addActionListener(generateAbsFileListener());
        correctScalesButton.addActionListener(correctScaleListener());
    }

    /**
     * Ensures that the lists of DataFiles in each tab share a common list model, so that changes are automtically
     * propagated to all instances.
     */
    private void synchroniseLists() {
        absorptioni0List.setModel(i0Model);
        absorptioni0bList.setModel(i0bModel);
        absorptionitList.setModel(itModel);
        absorptionitbList.setModel(itbModel);
    }

    /**
     * A pop-up dialog which prompts users to input correct columns to use (energy, theta, and counts)
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

    /**
     * Open Save Dialogue
     */
    private File saveDialogue() {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setDialogTitle("Specify where to save your file");
        int userSelection = chooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = chooser.getSelectedFile();
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            return fileToSave;
        }
        else return null;
    }
    /**
     * Opens an interface allowing a user to select a file from file system
     * @return The file(s) selected by the user, in an array. Returns an empty array if no files are selected.
     */
    private File[] openFileChooser(boolean fileSelection, boolean directorySelection, boolean multiSelection){
        File[] files;
        Scanner fileIn;
        int response;
        JFileChooser chooser = new JFileChooser(".");

        if (fileSelection && directorySelection)
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        else if (fileSelection)
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        else
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(multiSelection);
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private ChangeListener changeTabListener() {
        return new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                pack();
            }
        };
    }

    private ActionListener loadFilesActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File[] filesToLoad = openFileChooser(true, false, true);
                if (filesToLoad.length == 0) { //If no files are found, return and print an error
                    System.out.println("No files have been selected");
                    return;
                }

                /* If this is the first time loading data, create a window prompting the user to select
                which columns correspond to the relevant data, and update FileLoader to save these values */
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


                //Get the MeasurementType of files to be loaded from user input
                MeasurementType fileType = getSelectedType();

                //Load files and store in DataManager
                ArrayList<DataFile> loadedFiles = fileLoader.loadFiles(fileType, filesToLoad);
                if (!dataProcessor.checkRanges(loadedFiles.toArray(new DataFile[0]))) {
                    JOptionPane.showMessageDialog(new JFrame(), "File ranges do not match.");
                }
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
     * Adds functionality to generate data merge button. Creates a pop-up dialogue prompting
     * user to name the file and provide a header, truncates files if necessary, and generates a mean file
     * from the selected files.
     * @return
     */
    private ActionListener generateMeanActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MeasurementType type = getSelectedType();
                List<DataFile> files = getList(type).getSelectedValuesList();
                DataFile[] filesArr = files.toArray(new DataFile[0]);
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


                files = dataProcessor.truncateIfNeeded(files);
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
                        grapher.displayOffsetGraph(filesToPlot, offset, DataType.ENERGY, DataType.COUNTS_PER_LIVE);
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(new JFrame(), "Please enter a valid (integer) offset");
                    }
                }
                else {
                    grapher.displayGraph(filesToPlot, DataType.ENERGY, DataType.COUNTS_PER_LIVE);
                }
            }
        };
    }

    /**
     *
     * @return
     */
    private ActionListener bgAbsorptionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (leakageIsSignificantCheckBox.isSelected()) {
                    absi0bPane.setVisible(true);
                    absitbPane.setVisible(true);
                }
                else {
                    absi0bPane.setVisible(false); //Hide background selection panes if background insigfinicant, and reset selection
                    absitbPane.setVisible(false);
                    absorptioni0bList.clearSelection();
                    absorptionitbList.clearSelection();
                }
                pack();
            }
        };
    }

    /**
     * Create two plots, Theta vs Absorption and Energy vs Absorption from user selected files
     * @return
     */
    private ActionListener plotAbsorptionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DataFile absorptionFile = getAbsorptionFile();
                if (absorptionFile == null)
                    return;

                ChartPanel energy = grapher.createGraph(absorptionFile, DataType.ENERGY, DataType.ABSORPTION);
                ChartPanel theta = grapher.createGraph(absorptionFile, DataType.THETA, DataType.ABSORPTION);
                ArrayList<ChartPanel> graphs = new ArrayList<>();
                graphs.add(energy); graphs.add(theta);
                try {
                    grapher.plotPanels(graphs, null, null);
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                    //JOptionPane.showMessageDialog(new JFrame(), "");
                }

            }
        };
    }

    /**
     * Returns a new DataFile, from either 2  or 4 selected files in the Absorption tab.
     * @return
     */
    private DataFile getAbsorptionFile() {
        boolean background = leakageIsSignificantCheckBox.isSelected(); //Whether to include background correction (i.e use leakage files)
        boolean validSelection = checkAbsSelection(background); // Check if selected files are valid
        if (!validSelection) { //Prompt user to select valid combination of files if necessary
            if (!background)
                JOptionPane.showMessageDialog(this, "Please select one i0 and one it file");
            else
                JOptionPane.showMessageDialog(this, "Please select one i0, one it, one i0b and one itb file");
            return null;
        }
        DataFile i0 = (DataFile) absorptioni0List.getSelectedValue();
        DataFile it = (DataFile) absorptionitList.getSelectedValue();
        DataFile absorptionFile;
        if (background) {
            DataFile i0b = (DataFile) absorptioni0bList.getSelectedValue();
            DataFile itb = (DataFile) absorptionitbList.getSelectedValue();
            absorptionFile = dataProcessor.generateAbsorptionFile(i0, it, i0b, itb, "");
        }
        else {
            absorptionFile = dataProcessor.generateAbsorptionFile(i0, it, "");
        }
        return absorptionFile;
    }

    /**
     * When calculating or plotting absorption, checks that the user selected files are a valid combination.
     * @param background Whether background readings are being used for for absorption
     * @return
     */
    private boolean checkAbsSelection(boolean background) {
        if (!background && (absorptioni0List.isSelectionEmpty() || absorptionitList.isSelectionEmpty())) {
            JOptionPane.showMessageDialog(this, "Please select one i0 and one it file");
            return false;
        }
        if (leakageIsSignificantCheckBox.isSelected()) {
            if (absorptioni0List.isSelectionEmpty() || absorptioni0bList.isSelectionEmpty() ||
                    absorptionitList.isSelectionEmpty() || absorptionitbList.isSelectionEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select one i0, one it, one i0b and one itb file");
                return false;
            }
        }
        return true;
    }

    /**
     * Resets data in the selected file workspace in the first tab
     * @return
     */
    private ActionListener resetDataListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MeasurementType typeToClear = MeasurementType.valueOf((String) measurementTypeComboBox.getSelectedItem());
                int result = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you would like to clear all loaded "
                        + typeToClear.toString() + " files?");
                if (result == JOptionPane.YES_OPTION) {
                    ((DefaultListModel) getList(MeasurementType.valueOf((String) measurementTypeComboBox.getSelectedItem())).getModel()).removeAllElements();
                }
            }
        };
    }

    /**
     * Clears all currently loaded data
     */
    private ActionListener resetAllDataListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you would like to clear all loaded files?");
                //Clear each list if user decides to proceed
                if (option == JOptionPane.YES_OPTION) {
                    for (MeasurementType mt : MeasurementType.values()) {
                        if (getList(mt) != null)
                            ((DefaultListModel) getList(mt).getModel()).removeAllElements();
                    }
                    dataManager.clearFiles();
                }
            }
        };
    }


    /**
     * //TODO write
     * @return
     */
    private ActionListener correctScaleListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO finish

                ArrayList<DataFile> files = new ArrayList<>();
                Object[] filesArr = ((DefaultListModel) correctionFilesList.getModel()).toArray();
                JOptionPane.showInputDialog(new JFrame(), "Choose a file to define define characteristic monochromator energy (Emono)");
                DataFile file = null; //File which defines Emono TODO get from user input
                double energyMin = file.getEnergy().get(0);
                double thetaMin = file.getTheta().get(0);
                int result = JOptionPane.showConfirmDialog(new JFrame(), "Energy: " + energyMin + "\n Theta: " + thetaMin);
                if (result != JOptionPane.YES_OPTION) {
                    return; //Cancel correction if user is not happy with theta and energy values
                }

                double emono = energyMin * Math.sin(thetaMin);
                JOptionPane.showMessageDialog(new JFrame(), "Emono: " + emono);
                double eObserved = -1;
                double eTrue = -1;
                while (eObserved == -1 || eTrue == -1)  {
                    try {
                        String energyObserved = JOptionPane.showInputDialog(new JFrame(), "Enter observed energy");
                        eObserved = Double.parseDouble(energyObserved);
                        String energyTrue = JOptionPane.showInputDialog(new JFrame(), "Enter true energy");
                        eTrue = Double.parseDouble(energyTrue);
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(new JFrame(), "Please enter a valid value");
                    }
                }
                double thetaObserved = Math.asin(emono / eObserved);
                double thetaTrue = Math.asin(emono / eTrue);
                double thetaShift = thetaObserved - thetaTrue; //TODO which way round is this?

            }
        };
    }

    /**
     * The JList (Data Input Tab) containing the loaded files of a given measurement type. As all xxLists share the same model, can be used
     * to get a list of files of a given type by then getting the DefaultListModel of returned list.
     * @param type MeasurementType of list to retrieve
     * @return JList object containing DataFiles
     */
    private JList getList(MeasurementType type) {
        switch (type) {
            case I0: return i0List;
            case I0b: return i0bList;
            case It: return itList;
            case Itb: return itbList; //TODO handle Absorption
        }
        return null;
    }

    /**
     * Gets the MeasurementType of the combo box in the data processing tab
     * @return
     */
    private MeasurementType getSelectedType() {
        String mtLabel = ((String) measurementTypeComboBox.getSelectedItem());
        try {
            MeasurementType type = MeasurementType.labelToValue(mtLabel);
            return type;
        }
        catch (RuntimeException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error looking up MeasurementType");
            e.printStackTrace();
        }
        //return MeasurementType.valueOf((String) measurementTypeComboBox.getSelectedItem());
        return null; //TODO handle
    }
}

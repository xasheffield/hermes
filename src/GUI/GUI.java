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
    protected JCheckBox plotWithYOffsetCheckBox;
    protected JTextField offsetInput;
    private JButton plotGraphsButton;

    /**
     * Absorption tab
     */
    private JScrollPane absi0Pane; //JScrollPanes encapsulating file JLists in absorption tab
    private JScrollPane absitPane;
    private JScrollPane absitbPane;
    private JScrollPane absi0bPane;
    private JList absorptioni0List; //JLists in absorption tab, share model with lists from Data Input
    private JList absorptioni0bList;
    private JList absorptionitList;
    private JList absorptionitbList;
    private JCheckBox leakageIsSignificantCheckBox;
    private JButton plotAbsorptionButton;
    private JButton generateAbsorptionFileButton;

    /**
     * Energy Correction tab
     */
    private JList absorptionList;
    private JButton initiateCalibrationButton;
    private JButton chooseFileToDefineButton;
    private JLabel correctedThetaSample;
    private JLabel correctedEnergySample;
    private JTextArea addSomeTextAboutTextArea;

    /**
     * Cross-tab components
     * TODO dry
     */
    private DefaultListModel i0Model = (DefaultListModel) i0List.getModel();
    private DefaultListModel i0bModel = (DefaultListModel) i0bList.getModel();
    private DefaultListModel itModel = (DefaultListModel) itList.getModel();
    private DefaultListModel itbModel = (DefaultListModel) itbList.getModel();
    private DefaultListModel absorptionModel = (DefaultListModel) absorptionList.getModel();


    /**
     * Classes providing I0, Data Processing, and Graphing functionality
     */
    FileLoader fileLoader;
    FileWriter fileWriter;
    DataManager dataManager;
    DataProcessor dataProcessor;
    Grapher grapher;
    PopUpMaker popUpMaker = new PopUpMaker();

    String fileChooserPath = ".";//Initialise to current directory

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
        chooseFileToDefineButton.addActionListener(new ChooseCalibrationFileListener());
    }

    private void initComponents(){
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(650,550)); //TODO better sizing
        this.setSize(600, 600); //Does nothing?
        this.setLocationRelativeTo(null);//Centers the frame
    }

    private void addActionListeners() {
        /* TODO move to new file and pass GUI as argument to constructor for access to fields
        */
        loadFilesButton.addActionListener(new LoadFilesListener());
        generateMeanButton.addActionListener(new GenerateMeanListener());
        generatePolynomialButton.addActionListener(new GeneratePolynomialListener());
        resetAllDataButton.addActionListener(new ResetAllDataListener());
        plotGraphsButton.addActionListener(new PlotGraphsListener());

        resetDataButton.addActionListener(new ResetDataListener());
        plotAbsorptionButton.addActionListener(new PlotAbsorptionListener());
        rootTabPane.addComponentListener(new ComponentAdapter() {});
        rootTabPane.addChangeListener(new ChangeTabListener());
        leakageIsSignificantCheckBox.addActionListener(new SignificantLeakageListener());
        generateAbsorptionFileButton.addActionListener(new GenerateAbsorptionListener(this));
        initiateCalibrationButton.addActionListener(new CorrectScaleListener());
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

    //TODO move to PopUpMaker
    /**
     * Open Save Dialogue
     */
    private File saveDialogue() {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setDialogTitle("Specify where to save your file");
        int userSelection = chooser.showSaveDialog(this); //

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = chooser.getSelectedFile();
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            return fileToSave;
        }
        else return null;
    }

    //TODO move to PopUpMaker
    /**
     * Opens an interface allowing a user to select a file from file system
     * @return The file(s) selected by the user, in an array. Returns an empty array if no files are selected.
     */
    private File[] openFileChooser (boolean fileSelection, boolean directorySelection, boolean multiSelection){
        File[] files;
        Scanner fileIn;
        int response;
        JFileChooser chooser = new JFileChooser(fileChooserPath);

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
            fileChooserPath = files[0].getParentFile().getAbsolutePath();
            return files;
        }
        return new File[0];
    }

    //TODO move to PopUpMaker
    /**
     * Opens an interface allowing a user to select a directory
     * @return The directory selected, or null if user cancels action
     */
    private File directoryChooser(){
        File saveDirectory;
        int response;
        JFileChooser chooser = new JFileChooser(".");

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        //response = chooser.showOpenDialog(null);
        response = chooser.showDialog(this, "Save");

        if (response == JFileChooser.APPROVE_OPTION) {
            saveDirectory = chooser.getCurrentDirectory();
            return saveDirectory;
        }
        return null;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private void updateModel(MeasurementType fileType, JList listToUpdate) {
        DefaultListModel model = (DefaultListModel) listToUpdate.getModel();
        ArrayList<DataFile> filesToAdd = dataManager.getList(fileType);
        model.clear();
        filesToAdd.stream().forEach(file -> model.addElement(file)); // TODO figure out why model.addAll() doesn't work
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
                JOptionPane.showMessageDialog(this, "Please select one I0 and one It file");
            else
                JOptionPane.showMessageDialog(this, "Please select one I0, one It, one I0 leakage and one It leakage file");
            return null;
        }
        DataFile i0 = (DataFile) absorptioni0List.getSelectedValue();
        DataFile it = (DataFile) absorptionitList.getSelectedValue();
        DataFile absorptionFile;
        if (background) {
            DataFile i0b = (DataFile) absorptioni0bList.getSelectedValue();
            DataFile itb = (DataFile) absorptionitbList.getSelectedValue();
            try {
                absorptionFile = dataProcessor.generateAbsorptionFile(i0, it, i0b, itb, "");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Error generating absorption file. Tried to calculate logarithm of a negative number, check that supplied" +
                        " files are correct.");
                return null;
            }
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
                JOptionPane.showMessageDialog(this, "Please select one i0, one it, one i0 leakage and one it leakage file");
                return false;
            }
        }
        return true;
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
            case ABSORPTION: return absorptionList;
        }
        return null;
    }

    /**
     * Gets the MeasurementType of the combo box in the data processing tab
     * @return
     */
    protected MeasurementType getSelectedType() {
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


    /** ACTION LISTENERS FOR GUI COMPONENTS */

    /**
     *
     */
    private class LoadFilesListener implements ActionListener {

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
                if (columnNames.length == 0)
                    return; // Return if unable to parse data
                if (!popUpMaker.columnSelectionDialog(columnNames, fileLoader))
                    return; //Cancel file generation if user exits column selection popup
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
            updateModel(fileType, listToUpdate);
            //loadedFiles.stream().forEach(file -> model.addElement(file));
            //pack();
        }
    }

    /**
     * Resets data in the selected file workspace in the first tab
     */
    private class ResetDataListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //MeasurementType typeToClear = MeasurementType.valueOf((String) measurementTypeComboBox.getSelectedItem());
            MeasurementType typeToClear = MeasurementType.labelToValue((String) measurementTypeComboBox.getSelectedItem());
            int result = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you would like to clear all loaded "
                    + typeToClear.toString() + " files?");
            if (result == JOptionPane.YES_OPTION) {
                ((DefaultListModel) getList(typeToClear).getModel()).removeAllElements();
                dataManager.clearFiles(typeToClear);
            }
        }
    }

    private class PlotGraphsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JList selected = getList(getSelectedType());
            int[] selectedIndices = selected.getSelectedIndices();
            if (selectedIndices.length == 0) {
                JOptionPane.showMessageDialog(new JFrame(), "Select " + getSelectedType().label + " file(s) to plot");
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
    }

    /**
     * Clears all currently loaded data
     */
    private class ResetAllDataListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int option = JOptionPane.showConfirmDialog(new JFrame(), "This will clear ALL files loaded and generated in the session.\n" +
                    "Are you sure you would like to clear all files?");
            //Clear each list if user decides to proceed
            if (option == JOptionPane.YES_OPTION) {
                for (MeasurementType mt : MeasurementType.values()) {
                    if (getList(mt) != null)
                        ((DefaultListModel) getList(mt).getModel()).removeAllElements();
                }
                dataManager.clearFiles();
            }
        }
    }

    /**
     * Functionality for generate polynomial button. Prompts user to to enter file name, header, and choose degree to fit
     * @return
     */
    private class GeneratePolynomialListener implements ActionListener {
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
    }

    /**
     * Create two plots, Theta vs Absorption and Energy vs Absorption from user selected files (tab 2)
     * @return
     */
    private class PlotAbsorptionListener implements ActionListener {
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
    }

    /**
     * Adds functionality to generate data merge button. Creates a pop-up dialogue prompting
     * user to name the file and provide a header, truncates files if necessary, and generates a mean file
     * from the selected files.
     * @return
     */
    private class GenerateMeanListener implements ActionListener {
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
            if (fileName == null || header == null)
                return;


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
    }

    private class GenerateAbsorptionListener implements ActionListener {
        GenerateAbsorptionListener(GUI gui) {
            this.gui = gui;
        }
        GUI gui;
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
                File file = popUpMaker.saveDialogue(gui);
                absorptionFile.setFilePath(file.getAbsolutePath());

                ArrayList<DataFile> sourceFiles = new ArrayList<>();
                DataFile i0 = (DataFile) absorptioni0List.getSelectedValue();
                DataFile it = (DataFile) absorptionitList.getSelectedValue();
                sourceFiles.add(i0); sourceFiles.add(it);

                //Case split, absorption calculated differently if leakage is significant
                if (leakageIsSignificantCheckBox.isSelected()) {
                    DataFile i0b = (DataFile) absorptioni0bList.getSelectedValue();
                    DataFile itb = (DataFile) absorptionitbList.getSelectedValue();
                    sourceFiles.add(i0b); sourceFiles.add(itb);
                    fileWriter.writeAbsorptionFile(absorptionFile, i0, it, i0b, itb);
                }
                else
                    fileWriter.writeAbsorptionFile(absorptionFile, i0, it);

                dataManager.addFile(absorptionFile, MeasurementType.ABSORPTION);
                updateModel(MeasurementType.ABSORPTION, absorptionList);
                String directoryPath = Paths.get(absorptionFile.getFilePath()).getParent().toString() + System.getProperty("file.separator");
                fileWriter.writeLstFile(sourceFiles, file.getName(), header, directoryPath);
                JOptionPane.showMessageDialog(new JFrame(), ".dat and .lst files have been succesfully saved to " + absorptionFile.getFilePath());
            } catch (Exception ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private class ChangeTabListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            //TODO something smarter
            //pack();
        }
    }

    private class SignificantLeakageListener implements ActionListener {
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
            pack(); //TODO test what having this commented out does - answer: invisible panels, needs to be repacked
        }
    }

    private class CorrectScaleListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            double energyMin;
            double thetaMin;
            try {
                energyMin = Double.parseDouble(correctedEnergySample.getText());
                thetaMin = Double.parseDouble(correctedThetaSample.getText());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(new JFrame(), "Select a file to define characteristic monochromator energy before proceeding");
                return;
            }
            double emono = energyMin * Math.sin(Math.toRadians(thetaMin)); //Calculate monochromator energy from  values in GUI
            JOptionPane.showMessageDialog(new JFrame(), "Emono (eV): " + emono);


            double eObserved = -1;
            double eTrue = -1;
            while (eObserved == -1 || eTrue == -1)  {
                try {
                    String energyObserved = JOptionPane.showInputDialog(new JFrame(), "Enter observed energy");
                    eObserved = Double.parseDouble(energyObserved);
                    String energyTrue = JOptionPane.showInputDialog(new JFrame(), "Enter true energy");
                    eTrue = Double.parseDouble(energyTrue); //TODO handle user cancelling input
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(new JFrame(), "Please enter a valid value");
                    return;
                }
            }

            List<DataFile> filesToCorrect = new ArrayList<DataFile>();
            for (Object dataFile: absorptionList.getSelectedValuesList()) {
                filesToCorrect.add((DataFile) dataFile);
            }

            ArrayList<DataFile> correctedFiles = new ArrayList<>();
            //File saveDir = saveDialogue();
            File saveDir = directoryChooser();
            String savePath;
            try {
                savePath = saveDir.getAbsolutePath();
                System.out.println(savePath);
            } catch (NullPointerException npe) { // If user cancelled save dialogue, this is thrown
                return;
            }


            //TODO move most code into DataProcessor
            double thetaObserved = Math.asin(emono / eObserved);
            double thetaTrue = Math.asin(emono / eTrue);
            double thetaShift = Math.toDegrees(thetaTrue - thetaObserved); //TODO which way round is this?
            JOptionPane.showMessageDialog(new JFrame(), "Theta shift (deg) = " + thetaShift);



            for (DataFile file: filesToCorrect) {
                DataFile correctedFile;
                ArrayList<XRaySample> correctedSamples = new ArrayList<>();
                boolean hasBackground = false;
                for (XRaySample s: file.getData()) {
                    ProcessedSample sample = (ProcessedSample) s;
                    double theta = sample.getTheta();
                    double energy = sample.getEnergy();
                    double counts = sample.getCnts_per_live();
                    double absorption = sample.getAbsorption();
                    double i0 = sample.getI0();
                    double it = sample.getIt();
                    double i0Corr = sample.getI0Corrected();
                    double itCorr = sample.getItCorrected();

                    hasBackground = sample.hasBackground();
                    ProcessedSample copySample;
                    if (hasBackground) {
                        copySample = new ProcessedSample(energy, theta, counts, absorption, i0, it, i0Corr, itCorr);
                    }
                    else {
                        copySample = new ProcessedSample(energy, theta, counts, absorption, i0, it);
                    }
                    double correctedTheta = theta + thetaShift;
                    double correctedEnergy = emono / Math.sin(Math.toRadians(correctedTheta));
                    copySample.setCorrected(correctedEnergy, correctedTheta);
                    correctedSamples.add(copySample);
                }
                correctedFile = new DataFile(file.getFileType(), savePath + System.getProperty("file.separator") + file.getFileName() + "_corr", "", correctedSamples);
                correctedFiles.add(correctedFile);
                try {
                    if (hasBackground) {
                        fileWriter.writeDataFile(correctedFile, DataType.ENERGY, DataType.THETA, DataType.COUNTS_PER_LIVE,
                                DataType.ABSORPTION, DataType.I0, DataType.IT, DataType.I0CORRECTED, DataType.ITCORRECTED,  DataType.ENERGY_CORRECTED, DataType.THETA_CORRECTED);
                    }
                    else {
                        fileWriter.writeDataFile(correctedFile, DataType.ENERGY, DataType.THETA, DataType.COUNTS_PER_LIVE,
                                DataType.ABSORPTION, DataType.I0, DataType.IT, DataType.ENERGY_CORRECTED, DataType.THETA_CORRECTED);

                    }
                    DataFile calibrationFile = dataManager.getCalibrationFile();
                    fileWriter.writeCorrectedLstFile(correctedFile, calibrationFile.getFileName(), energyMin, thetaMin, emono, thetaShift);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                grapher.displayCompareGraph(file, DataType.ENERGY, DataType.ABSORPTION, correctedFile, DataType.ENERGY_CORRECTED, DataType.ABSORPTION);
                grapher.displayCompareGraph(file, DataType.THETA, DataType.ABSORPTION, correctedFile, DataType.THETA_CORRECTED, DataType.ABSORPTION);
                //TODO case split for has/doesn't have background

                //TODO plot Absorption vs original theta, vs original energy
                //Absorption vs corrected theta, vs corrected energy
            }
            /*
            grapher.displayGraph((ArrayList<DataFile>) filesToCorrect, DataType.ENERGY, DataType.ABSORPTION);
            grapher.displayGraph((ArrayList<DataFile>) correctedFiles, DataType.ENERGY_CORRECTED, DataType.ABSORPTION);
            grapher.displayGraph((ArrayList<DataFile>) filesToCorrect, DataType.THETA, DataType.ABSORPTION);
            grapher.displayGraph((ArrayList<DataFile>) correctedFiles, DataType.THETA_CORRECTED, DataType.ABSORPTION);

             */
        }


    }

    /**
     * Allows user to select one file in Energy Correction tab to define Monochromator Energy
     * Displays the first Energy and Theta values in the chosen file, so that the user can choose whether
     * to proceed or use another file.
     */
    private class ChooseCalibrationFileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (absorptionList.getSelectedIndices().length != 1) {
                JOptionPane.showMessageDialog(new JFrame(), "Please select exactly one file");
                return;
            }
            DataFile file = (DataFile) absorptionList.getSelectedValue();
            dataManager.setCalibrationFile(file);
            double energy0 = file.getData(DataType.ENERGY).get(0);
            double theta0 = file.getData(DataType.THETA).get(0);
            correctedEnergySample.setText(String.valueOf(energy0));
            correctedThetaSample.setText(String.valueOf(theta0));
        }
    }
}

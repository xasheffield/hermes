package GUI;
/**
 * Handles the various pop-up windows needed, such as FileChoosers and Dialogs
 */

import IO.FileLoader;
import sun.swing.FilePane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Scanner;

public class PopUpMaker {
    GUI gui;
    boolean listView = true;
    String savePath = ".";

    public PopUpMaker(GUI gui) {
        this.gui = gui;
    }

    /**
     * A pop-up dialog which prompts users to input correct columns to use (energy, theta, and counts)
     * @param columnNames - The names of columns in the data
     * @param fl - FileLoader, in order to set the column indeces
     */
    boolean columnSelectionDialog(String[] columnNames, FileLoader fl) {
        JComboBox energyBox = new JComboBox(columnNames);
        JComboBox thetaBox = new JComboBox(columnNames);
        JComboBox countsBox = new JComboBox(columnNames);
        JComboBox icrBox = new JComboBox(columnNames);
        JComboBox ocrBox = new JComboBox(columnNames);
        JLabel eLabel = new JLabel("Energy");
        JLabel tLabel = new JLabel("Theta");
        JLabel cLabel = new JLabel("Counts");
        JLabel icrLabel = new JLabel("ICR");
        JLabel ocrLabel = new JLabel("OCR");

        //TODO add OCR/ICR

        Object[] options = new Object[] {energyBox, thetaBox, countsBox, icrBox, ocrBox};

        JPanel optionPanel = new JPanel();
        optionPanel.add(eLabel);
        optionPanel.add(energyBox);
        optionPanel.add(Box.createVerticalStrut(15));
        optionPanel.add(tLabel);
        optionPanel.add(thetaBox);
        optionPanel.add(Box.createVerticalStrut(15));
        optionPanel.add(cLabel);
        optionPanel.add(countsBox);
        optionPanel.add(Box.createVerticalStrut(15));
        optionPanel.add(icrLabel);
        optionPanel.add(icrBox);
        optionPanel.add(Box.createVerticalStrut(15));
        optionPanel.add(ocrLabel);
        optionPanel.add(ocrBox);

        int result = JOptionPane.showConfirmDialog(null, optionPanel,
                "Please Select Energy, Theta, Counts, ICR, OCR", JOptionPane.OK_CANCEL_OPTION);

        // If the user presses okay button
        if (result == JOptionPane.YES_OPTION) {
            int energyIndex = energyBox.getSelectedIndex();
            int thetaIndex = thetaBox.getSelectedIndex();
            int countsIndex = countsBox.getSelectedIndex();
            int icrIndex = icrBox.getSelectedIndex();
            int ocrIndex = ocrBox.getSelectedIndex();

            fl.setIndeces(energyIndex, thetaIndex, countsIndex, icrIndex, ocrIndex);
            fl.setValuesInitialised(true);
            return true;
        }
        else {
            JOptionPane.showMessageDialog(new JFrame(), "File generation cancelled");
            return false;

        }
    }

    /**
     * @return Absolute path to file, with file extension omitted (e.g. "C:/Users/user/Folder/File"). Null if user cancels dialogue
     */
    protected File saveDialogue(String suggested_name) {
        JFileChooser chooser = new JFileChooser(savePath);
        chooser.setDialogTitle("Select where to save your file");
        chooser.setSelectedFile(new File(suggested_name));

        int userSelection = chooser.showSaveDialog(gui); //
        File returnFile = null;


        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = chooser.getSelectedFile();
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            savePath = fileToSave.getParentFile().getAbsolutePath();
            returnFile =  fileToSave;
        }
        return returnFile;
    }

    protected File saveDialogue() {
        return saveDialogue("");
    }

    /**
     * Opens an interface allowing a user to select a file from file system
     * @return The file(s) selected by the user, in an array. Returns an empty array if no files are selected.
     * @param fileSelection
     * @param directorySelection
     * @param multiSelection
     */
    File[] openFileChooser(boolean fileSelection, boolean directorySelection, boolean multiSelection){
        File[] files;
        Scanner fileIn;
        int response;
        JFileChooser chooser = new JFileChooser(gui.fileChooserPath);

        if (fileSelection && directorySelection)
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        else if (fileSelection)
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        else
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(multiSelection);


        //Original
        //response = chooser.showOpenDialog(null);
        setViewType(chooser);
        /*
        Action details = chooser.getActionMap().get("viewTypeDetails");
        details.actionPerformed(null);
      */


        response = chooser.showOpenDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) {
            files = chooser.getSelectedFiles();
            gui.fileChooserPath = files[0].getParentFile().getAbsolutePath();
            updateViewType(chooser);
            return files;
        }
        updateViewType(chooser);
        return new File[0];
    }

    /**
     * Saves the view type selected by user for a pop up, so that future pop ups will be initialised in that same view
     * by default.
     * @param chooser
     */
    private void updateViewType(JFileChooser chooser) {
        for (Component component: chooser.getComponents()) {
            if (component instanceof JPanel) {
                for (Component filePane: ((JPanel) component).getComponents()) {
                    if (filePane instanceof FilePane) {
                        ((FilePane) filePane).getViewType();
                        if (((FilePane)filePane).getViewType() == FilePane.VIEWTYPE_LIST)
                            listView = true;
                        else
                            listView = false;
                    }
                }
            }
        }
        System.out.println(listView);
    }

    /**
     * Sets the view type of a pop up to that previously selected by the user.
     * @param chooser
     */
    private void setViewType(JFileChooser chooser) {
        Action details;
        if (listView)
            details = chooser.getActionMap().get("viewTypeList");
        else
            details = chooser.getActionMap().get("viewTypeDetails");
        if (details != null) // TODO replace with operating system check for clarity
            details.actionPerformed(null);
    }


    /**
     * Opens an interface allowing a user to select a directory
     * @return The directory selected, or null if user cancels action
     */
    File directoryChooser(){
        File saveDirectory;
        int response;
        JFileChooser chooser = new JFileChooser(savePath);

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        //response = chooser.showOpenDialog(null);
        response = chooser.showDialog(gui, "Save");

        if (response == JFileChooser.APPROVE_OPTION) {
            saveDirectory = chooser.getCurrentDirectory();
            return saveDirectory;
        }
        return null;
    }
}

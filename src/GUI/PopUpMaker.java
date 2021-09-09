package GUI;

import IO.FileLoader;

import javax.swing.*;
import java.io.File;

public class PopUpMaker {
    /**
     * A pop-up dialog which prompts users to input correct columns to use (energy, theta, and counts)
     * @param columnNames - The names of columns in the data
     * @param fl - FileLoader, in order to set the column indeces
     */
    boolean columnSelectionDialog(String[] columnNames, FileLoader fl) {
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
            JOptionPane.showMessageDialog(new JFrame(), "File generation cancelled");
            return false;

        }
    }

    /**
     * Open Save Dialogue
     */
    protected File saveDialogue(GUI gui) {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setDialogTitle("Specify where to save your file");
        int userSelection = chooser.showSaveDialog(gui); //

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = chooser.getSelectedFile();
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            return fileToSave;
        }
        else return null;
    }
}

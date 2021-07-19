package GUI;

import IO.FileLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * An interface which allows the user to input which columns of data from loaded files correspond
 * to energy, theta, and counts_per_live.
 */
public class FileLoadingGUI extends JFrame implements Runnable {
    private FileLoader fileLoader;

    private JComboBox energyComboBox;
    private JComboBox thetaComboBox;
    private JComboBox countsComboBox;
    private JButton enterButton;
    private JPanel rootPanel;
    private String[] testModel = new String[] {"Energy_(eV)","theta_(deg)","source_(ustep)", "detect_(ustep)","ROI_counts",
            "total_counts","real_time","live_time","cnts_per_live","ICR_(cnts/sec)","OCR_(cnts/sec)"};

    public FileLoadingGUI(String title, FileLoader fLoader, File[] files) throws HeadlessException {
        super(title);
        this.fileLoader = fLoader;
        this.initComponents(fLoader.getColumnNames(files[0]));
        this.setContentPane(rootPanel);
        this.pack();

        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileLoader.setEnergyIndex(energyComboBox.getSelectedIndex());
                fileLoader.setThetaIndex(thetaComboBox.getSelectedIndex());
                fileLoader.setCountsIndex(countsComboBox.getSelectedIndex());
                fileLoader.setValuesInitialised(true);
                dispose();
            }
        });
    }

    private void initComponents(String[] columnNames){
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //this.setMinimumSize(new Dimension(600,450));
        this.setLocationRelativeTo(null);//Centers the frame



        energyComboBox.setModel(new DefaultComboBoxModel(columnNames));
        thetaComboBox.setModel(new DefaultComboBoxModel(columnNames));
        countsComboBox.setModel(new DefaultComboBoxModel(columnNames));
    }

    @Override
    public void run() {

        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //this.setMinimumSize(new Dimension(600,450));
        this.setLocationRelativeTo(null);//Centers the frame
    }
}

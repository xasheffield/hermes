package Graphing;

import javax.swing.*;

public class GraphTestGUI extends JFrame{
    public JPanel getBasePanel() {
        return basePanel;
    }

    private JPanel basePanel;


    public GraphTestGUI(String title) {
        super(title);
        this.setContentPane(basePanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
}

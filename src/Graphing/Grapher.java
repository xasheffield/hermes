package Graphing;
import DataProcessing.Models.DataFile;
import DataProcessing.Models.XRaySample;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Grapher {

    private final int COLUMNS = 3; //

    public void createWindow(JPanel panel) {
        JFrame frame = new JFrame("Plot");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * General purpose function for displaying any number of files in seperate plots, in a JFrame
     * @param files The files to plot
     * @return
     */
    public void displayGraph(ArrayList<DataFile> files) {
        int plots = files.size(); //Number of plots
        int rows = plots / COLUMNS; //nb Java rounds down in integer division
        if (plots % COLUMNS != 0) //Add another row if there is remainder
            rows++;

        //JPanel panel = new JPanel(new GridLayout(rows, COLUMNS));
        JPanel panel = new JPanel(new BorderLayout());
        JPanel subPanel = new JPanel(new GridLayout(rows, COLUMNS));
        subPanel.setBackground(Color.white);


        //Divide screen into spaces for each column, taking off 5% of screensize to leave space for
        // scroll bars and edges to avoid need for a horizontal scrollbar
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        double width = size.getWidth();
        double plotWidth = (width * 0.95) / (double) COLUMNS;



        for (DataFile file: files) {

            ChartPanel graph = createGraph(file);
            Dimension graphSize = graph.getPreferredSize();
            graphSize.width = (int) plotWidth;
            graph.setPreferredSize(graphSize);
            subPanel.add(graph);

            //subPanel.add(createGraph(file)); //Plot each data file and add to main panel
        }

        JScrollPane scroller = new JScrollPane(subPanel);
        panel.add(scroller, BorderLayout.CENTER);

        createWindow(panel);
    }

    public void displayOffsetGraph(ArrayList<DataFile> files, int offset) {
        JFrame frame = new JFrame("Plot");
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createOffsetGraph(files, offset)); //Plot each data file and add to main panel
        createWindow(panel);
    }


    public ChartPanel createGraph(DataFile file) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries(file.getFileName());

        XYSeries series2 = new XYSeries(file.getFileName()+"2");

        for (XRaySample sample: file.getData()) {
            series1.add(sample.getEnergy(), sample.getCnts_per_live());


        }
        dataset.addSeries(series1);

        JFreeChart scatterPlot = ChartFactory.createScatterPlot(file.getFileName(), "Energy", "Counts per live", dataset,
                PlotOrientation.VERTICAL, false, false, false);
        scatterPlot.setBackgroundPaint(Color.white);


        XYPlot plot = scatterPlot.getXYPlot();
        XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer();
        renderer0.setSeriesLinesVisible(0, false); // Hide lines between points
        plot.setRenderer(0, renderer0);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.black);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(0, ShapeUtilities.createDiagonalCross(1, 1));

        return new ChartPanel(scatterPlot);
    }

    public ChartPanel createOffsetGraph(ArrayList<DataFile> files, int offset) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        ArrayList<XYSeries> seriesList = new ArrayList<>();
        int i = 0;//Counter for offset on each dataset

        for (DataFile file: files) {
            XYSeries set = new XYSeries(file.getFileName());
            for (XRaySample sample: file.getData()) {
                set.add(sample.getEnergy(), sample.getCnts_per_live() + offset*i);
            }
            seriesList.add(set);
            i++;
        }
        for (XYSeries series: seriesList) {
            dataset.addSeries(series);
        }

        JFreeChart scatterPlot = ChartFactory.createScatterPlot("Energy vs Counts", "Energy", "Counts per live", dataset,
                PlotOrientation.VERTICAL, true, false, false);
        scatterPlot.setBackgroundPaint(Color.white);

        XYPlot plot = scatterPlot.getXYPlot();
        XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer();
        renderer0.setSeriesLinesVisible(0, false); // Hide lines between points
        plot.setRenderer(0, renderer0);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.black);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(0, ShapeUtilities.createDiagonalCross(1, 1));

        //Make points smaller for all sets
        //TODO make this work
        for (int j = 0; j < plot.getDatasetCount(); j++) {
            renderer0.setSeriesLinesVisible(j, false);
            plot.setRenderer(j, renderer0);
            XYItemRenderer datasetRenderer = plot.getRendererForDataset(plot.getDataset(j));
            Shape shape = datasetRenderer.getSeriesShape(j);
            //datasetRenderer.setSeriesShape(j, shape.);
            datasetRenderer.setSeriesShape(j, ShapeUtilities.createDiagonalCross(1, 1));
        }

        return new ChartPanel(scatterPlot);
    }
}

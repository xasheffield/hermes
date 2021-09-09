package Graphing;
import Data.Models.DataFile;
import Data.Models.DataType;
import Data.Models.ProcessedSample;
import Data.Models.XRaySample;
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
     * @param x
     * @param y
     * @return
     */
    public void displayGraph(ArrayList<DataFile> files, DataType x, DataType y) {
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
            ChartPanel graph = createGraph(file, x, y);
            Dimension graphSize = graph.getPreferredSize();
            graphSize.width = (int) plotWidth;
            graph.setPreferredSize(graphSize);
            subPanel.add(graph);
        }
        JScrollPane scroller = new JScrollPane(subPanel);
        panel.add(scroller, BorderLayout.CENTER);
        createWindow(panel);
    }

    /**
     * Creates a window with a scatter plot for each file or panels provided
     * @param filesOrPanels - can either take in DataFile or ChartPanels
     * @param x - data type to plot on x axis, only used when passing DataFiles
     * @param y - ^
     */
    public void plotPanels(ArrayList<?> filesOrPanels, DataType x, DataType y) throws ClassNotFoundException {
        int plots = filesOrPanels.size(); //Number of plots
        int rows = plots / COLUMNS; //nb Java rounds down in integer division
        if (plots % COLUMNS != 0) //Add another row if there is remainder
            rows++;

        JPanel panel = new JPanel(new BorderLayout());
        JPanel subPanel = new JPanel(new GridLayout(rows, COLUMNS));
        subPanel.setBackground(Color.white);


        //Divide screen into spaces for each column, taking off 5% of screensize to leave space for
        // scroll bars and edges to avoid need for a horizontal scrollbar
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        double width = size.getWidth();
        double plotWidth = (width * 0.95) / (double) COLUMNS;


        for (Object graph: filesOrPanels) {
            ChartPanel resized_graph;
            if (graph instanceof ChartPanel) {
                resized_graph = (ChartPanel) graph;
            }
            else if (graph instanceof DataFile) {
               resized_graph = createGraph((DataFile) graph, x, y);
            }
            else {
                throw new ClassNotFoundException();
            }
            Dimension graphSize = resized_graph.getPreferredSize();
            graphSize.width = (int) plotWidth;
            resized_graph.setPreferredSize(graphSize);
            subPanel.add(resized_graph);
        }

        /*
        else if (filesOrPanels.get(0) instanceof  DataFile) {
            for (Object file : filesOrPanels) {
                ChartPanel graph = createGraph((DataFile) file, x, y);
                Dimension graphSize = graph.getPreferredSize();
                graphSize.width = (int) plotWidth;
                graph.setPreferredSize(graphSize);
                subPanel.add(graph);
            }
        }
         */
        JScrollPane scroller = new JScrollPane(subPanel);
        panel.add(scroller, BorderLayout.CENTER);
        createWindow(panel);
    }



    /**
     * Plots a series of files with a provided y axis offset on the one set of axes
     * @param files The files to plot
     * @param offset The offset
     * @param x
     * @param y
     */
    public void displayOffsetGraph(ArrayList<DataFile> files, int offset, DataType x, DataType y) {
        JFrame frame = new JFrame("Plot");
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createOffsetGraph(files, offset, x, y)); //Plot each data file and add to main panel
        createWindow(panel);
    }

    public void displayCompareGraph(DataFile file1, DataType x1, DataType y1, DataFile file2, DataType x2, DataType y2) {
        JFrame frame = new JFrame("Plot");
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createCompareGraph(file1, x1, y1, file2, x2, y2)); //Plot each data file and add to main panel
        createWindow(panel);
    }

    /**
     * Creates a scatter plot from a given file.
     * @param file File to plot
     * @param x Data to plot on x axis
     * @param y Data to plot on y axis
     * @return A ChartPanel containing a scatter plot of the specified data
     */
    public ChartPanel createGraph(DataFile file, DataType x, DataType y) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries(file.getFileName());

        for (XRaySample sample: file.getData()) {
            series1.add(sample.getData(x), sample.getData(y));
        }
        dataset.addSeries(series1);

        JFreeChart scatterPlot = ChartFactory.createScatterPlot(file.getFileName(), x.label, y.label, dataset,
                PlotOrientation.VERTICAL, false, false, false);
        scatterPlot.setBackgroundPaint(Color.white);


        XYPlot plot = scatterPlot.getXYPlot();
        XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer();
        renderer0.setSeriesLinesVisible(0, true); // Hide or show lines between points
        plot.setRenderer(0, renderer0);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.black);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(0, ShapeUtilities.createDiagonalCross(1, 1));

        return new ChartPanel(scatterPlot);
    }

    public ChartPanel createCompareGraph(DataFile file1, DataType x1, DataType y1, DataFile file2, DataType x2, DataType y2) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        ArrayList<XYSeries> seriesList = new ArrayList<>(); //One series corresponds to one file, the collection of series make up a dataset

        XYSeries set1 = new XYSeries(file1.getFileName());
        for (XRaySample sample:file1.getData()) {
            if (sample instanceof ProcessedSample) //Cast to subclass if necessary
                sample = (ProcessedSample) sample;
            set1.add(sample.getData(x1), sample.getData(y1));
        }

        XYSeries set2 = new XYSeries(file2.getFileName());
        for (XRaySample sample:file2.getData()) {
            if (sample instanceof ProcessedSample) {
                sample = (ProcessedSample) sample;
            }
            set2.add(sample.getData(x2), sample.getData(y2));
        }

        dataset.addSeries(set1);
        dataset.addSeries(set2);

        JFreeChart scatterPlot = createScatterPlot(x1, y1, dataset);
        return new ChartPanel(scatterPlot);
    }

    /**
     * Creates a scatter plot of given files, offset on y axis by user specified value
     * @param files DataFiles to plot
     * @param offset
     * @param x Measurement type to plot on x
     * @param y Measurement type to plot on y
     * @return The graph as a ChartPanel
     */
    public ChartPanel createOffsetGraph(ArrayList<DataFile> files, int offset, DataType x, DataType y) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        ArrayList<XYSeries> seriesList = new ArrayList<>(); //One series corresponds to one file, the collection of series make up a dataset
        int i = 0;//Counter for offset on each dataset

        for (DataFile file: files) {
            XYSeries set = new XYSeries(file.getFileName());
            for (XRaySample sample: file.getData()) {
                set.add(sample.getEnergy(), sample.getCnts_per_live() + offset*i);//TODO generify
            }
            seriesList.add(set);
            i++;
        }
        for (XYSeries series: seriesList) {
            dataset.addSeries(series);
        }

        JFreeChart scatterPlot = createScatterPlot(x, y, dataset);
        return new ChartPanel(scatterPlot);
    }

    private JFreeChart createScatterPlot(DataType x, DataType y, XYSeriesCollection dataset) {
        JFreeChart scatterPlot = ChartFactory.createScatterPlot("", x.label, y.label, dataset,
                PlotOrientation.VERTICAL, true, false, false);
        scatterPlot.setBackgroundPaint(Color.white);

        XYPlot plot = scatterPlot.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(0, renderer);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.black);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(0, ShapeUtilities.createDiagonalCross(1, 1));


        for (int j = 0; j < plot.getSeriesCount(); j++) {
            renderer.setSeriesLinesVisible(j, true); // Hide lines between points
            plot.setRenderer(j, renderer);
            //plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(j, Color.black);
            plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(j, ShapeUtilities.createDiagonalCross(1, 1));

            /*
            XYItemRenderer datasetRenderer = plot.getRendererForDataset(plot.getDataset(j));
            Shape shape = datasetRenderer.getSeriesShape(j);
            //datasetRenderer.setSeriesShape(j, shape.);
            datasetRenderer.setSeriesShape(j, ShapeUtilities.createDiagonalCross(1, 1));

             */
        }
        return scatterPlot;
    }
}

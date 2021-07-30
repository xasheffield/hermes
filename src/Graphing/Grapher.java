package Graphing;
import DataProcessing.Models.DataFile;
import DataProcessing.Models.XRaySample;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Grapher {

    public ChartPanel createGraph() {


    XYSeriesCollection dataset = new XYSeriesCollection();
    XYSeries series1 = new XYSeries("Energy vs Theta");

    series1.add(1, 1);
    series1.add(2, 2);
    series1.add(3, 3);
    series1.add(4, 4);
    series1.add(5, 5);
    series1.add(6, 6);
    series1.add(7, 7);
    series1.add(8, 8);

    dataset.addSeries(series1);

    /*
    series1.add(16934.0,2131.02883875);
    series1.add(16935.0,2170.21825125);
    series1.add(16936.0,2115.59852125);
    series1.add(16937.0,2050.57868625);
    series1.add(16938.0,2073.32508625);
    series1.add(16939.0,2059.36046625);
    series1.add(16940.0,2029.29019);
    series1.add(16941.0,2030.972697499999);
     */

    JFreeChart scatterPlot = ChartFactory.createScatterPlot("Energy vs Counts", "Energy", "Counts per live", dataset,
            PlotOrientation.HORIZONTAL, false, false, false);

    return new ChartPanel(scatterPlot);
    }

    public ChartPanel testGraphCreation(DataFile file) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries(file.getFileName());

        XYSeries series2 = new XYSeries(file.getFileName()+"2");

        for (XRaySample sample: file.getData()) {
            series1.add(sample.getEnergy(), sample.getCnts_per_live());
            series2.add(sample.getEnergy(), sample.getCnts_per_live()+1000);

        }
        dataset.addSeries(series1);
        dataset.addSeries(series2);

        JFreeChart scatterPlot = ChartFactory.createScatterPlot("Energy vs Counts", "Energy", "Counts per live", dataset,
                PlotOrientation.VERTICAL, true, false, false);
        scatterPlot.setBackgroundPaint(Color.white);


        XYPlot plot = scatterPlot.getXYPlot();
        XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer();
        renderer0.setSeriesLinesVisible(0, false); // Hide lines between points
        plot.setRenderer(0, renderer0);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.black);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesShape(0, ShapeUtilities.createDiagonalCross(1, 1));

        return new ChartPanel(scatterPlot);
    }

    public ChartPanel createOffsetGraph(int offset, ArrayList<DataFile> files) {
        //TODO method body
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


        return new ChartPanel(scatterPlot);
    }
}

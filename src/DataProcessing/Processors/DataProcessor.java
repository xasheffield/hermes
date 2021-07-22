package DataProcessing.Processors;

import DataProcessing.Models.DataFile;
import DataProcessing.Models.MeasurementType;
import DataProcessing.Models.XRaySample;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.fitting.*;

public class DataProcessor {

    /**
     * @param files - any number of data files from which to generate the mean
     * @return mean data file generated from inputs
     */
    public DataFile generateMean(MeasurementType dataType, String fileHeader, DataFile... files){
        //TODO method body
        /*
        for (DataFile file: files){
            System.out.println(file.getFilePath());
        }
         */
        //TODO Better way of iterating through samples
        int sampleNumber = files[0].getData().size();
        double energy = 0;
        double theta = 0;
        double counts = 0;


        ArrayList<XRaySample> meanSamples = new ArrayList<>();
        for (int i = 0; i <sampleNumber; i++) {
            ArrayList<XRaySample> samplesList = new ArrayList<>();
            for (DataFile file: files) {
                samplesList.add(file.getData().get(i));
            }
            XRaySample meanSample = generateMean(samplesList);
            meanSamples.add(meanSample);
        }
        //TODO filepath and fileheader
        return new DataFile(dataType, files[0].getFilePath(), "", meanSamples);
    }

    /**
     * Generates a mean sample from a list of XRaySamples
     * @param samples
     * @return Mean sample
     */
    public XRaySample generateMean(ArrayList<XRaySample> samples) {
        //TODO create test

        double sampleNumber = samples.size();
        ArrayList<Double> energy = (ArrayList<Double>) samples.stream().map(x -> x.getEnergy()).collect(Collectors.toList());
        ArrayList<Double> theta = (ArrayList<Double>) samples.stream().map(x -> x.getTheta()).collect(Collectors.toList());
        ArrayList<Double> counts = (ArrayList<Double>) samples.stream().map(x -> x.getCnts_per_live()).collect(Collectors.toList());

        Double meanEnergy = energy.stream().mapToDouble(a -> a).sum() / sampleNumber;
        Double meanTheta = theta.stream().mapToDouble(a -> a).sum() / sampleNumber;
        Double meanCounts = counts.stream().mapToDouble(a -> a).sum() / sampleNumber;

        return new XRaySample(meanEnergy, meanTheta, meanCounts);
    }

    /**
     *
     * @param file
     * @return String representation of polynomial fit to data file
     */
    public String generatePolynomial(DataFile file, int polyDegree) {
        //TODO create test


        PolynomialCurveFitter pcf = PolynomialCurveFitter.create(3);

        // Store data as WeightedObservedPoints
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (XRaySample sample: file.getData()) {
            obs.add(sample.getTheta(), sample.getCnts_per_live());
        }
        final PolynomialCurveFitter fitter
                = PolynomialCurveFitter.create(0).withStartPoint(new double[] { -1e-20, 3e15, -5e25 });

        final double[] best = fitter.fit(obs.toList());



        return "";
    }

    /**
     *
     * @return Value of absorption (for file?)
     */
    public int calculateAbsorption() {
        //TODO method body
        return 0;
    }

    /**
     * Corrects the theta scale for a (file? sample?)
     */
    public void correctThetaScale(DataFile file) {
        //
    }
}

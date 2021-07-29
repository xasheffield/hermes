package DataProcessing.Processors;

import DataProcessing.Models.DataFile;
import DataProcessing.Models.MeasurementType;
import DataProcessing.Models.XRaySample;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.fitting.*;

public class DataProcessor {

    /**
     * @param files - any number of data files from which to generate the mean
     * @return mean data file generated from inputs
     */
    public DataFile generateMean(MeasurementType dataType, String fileName, String fileHeader, DataFile... files){
        //TODO method body
        /*
        for (DataFile file: files){
            System.out.println(file.getFilePath());
        }
         */
        //TODO Better way of iterating through samples
        DataFile firstFile = files[0];
        int sampleNumber = firstFile.getData().size();
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
        File saveDirectory = new File(firstFile.getFilePath()).getParentFile();
        //TODO dat file
        String filePath = saveDirectory.getAbsolutePath() + System.getProperty("file.separator") + fileName + ".txt";
        return new DataFile(dataType, filePath, fileHeader, meanSamples);
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

    //TODO theta or energy against counts? ENERGY
    public double[] generatePoly(DataFile file, int polyDegree) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        ArrayList<XRaySample> samples = file.getData();
        for (int i = 0; i < samples.size(); i++) {
            XRaySample sample = samples.get(i);
            obs.add(sample.getEnergy(), sample.getCnts_per_live());
        }
        // Instantiate a third-degree polynomial fitter.
        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(3);

        // Retrieve fitted parameters (coefficients of the polynomial function).
        final double[] coeff = fitter.fit(obs.toList());
        return coeff;
    }

    //TODO theta or energy against counts? ENERGY
    public double[] generatePolyTest() {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 1; i < 1000; i++) {
            obs.add(i, i*i);
        }
        //ArrayList<XRaySample> samples = file.getData();
        /*
        for (int i = 0; i < samples.size(); i++) {
            XRaySample sample = samples.get(i);
            obs.add(sample.getEnergy(), sample.getCnts_per_live());
        }

         */
        // Instantiate a third-degree polynomial fitter.
        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(3);

        // Retrieve fitted parameters (coefficients of the polynomial function).
        final double[] coeff = fitter.fit(obs.toList());
        return coeff;
    }


    /**
     *
     * @return ArrayList of absorption values
     */
    public ArrayList<Double> calculateAbsorption(DataFile i0File, DataFile itFile) {
        //TODO check data ranges match
        List<Double> it_counts = itFile.getCounts();
        Iterator<Double> i0 = i0File.getCounts().iterator();
        Iterator<Double> it = itFile.getCounts().iterator();

        ArrayList<Double> absorption = new ArrayList<>();
        while (i0.hasNext() && it.hasNext()) {
            absorption.add(Math.log(i0.next() / it.next()));
        }
        //TODO method body
        return absorption;
    }

    public List<Double> calculateAbsorption(DataFile i0File, DataFile itFile, DataFile i0bFile, DataFile itbFile) {
        //TODO method body
        List<Double> it_counts = itFile.getCounts();
        Iterator<Double> i0 = i0File.getCounts().iterator();
        Iterator<Double> it = itFile.getCounts().iterator();
        Iterator<Double> i0b = i0bFile.getCounts().iterator();
        Iterator<Double> itb = itbFile.getCounts().iterator();

        ArrayList<Double> absorption = new ArrayList<>();
        while (i0.hasNext() && it.hasNext()) {
            double a = Math.log((i0.next() - i0b.next()) / (it.next() - itb.next()));
            absorption.add(a);
        }
        return absorption;
    }

    /**
     * Corrects the theta scale for a (file? sample?)
     */
    public void correctThetaScale(DataFile file) {
        //
    }

    //TODO check if this way of validating file ranges is acceptable (possibly able to check if two <Double> ArrayLists are identical?)
    //TODO test

    /**
     *
     * @param files
     * @return true if all files are same length, and have same values for first and last energy
     */
    public boolean checkRanges(DataFile... files) {
        if (files.length == 0 || files.length == 1)
            return true;
        List<Double> file0_energy = files[0].getEnergy();
        int size = file0_energy.size();
        int lastIndex = file0_energy.size() -1;
        double energy1 = file0_energy.get(0);
        double energy2 = file0_energy.get(file0_energy.size()-1);

        for (DataFile file: files) {
            if (file.getEnergy().size() != size) // Check that data sets are same size
                return false;

            double e1 = file.getEnergy().get(0);
            double e2 = file.getEnergy().get(lastIndex);
            if (e1 != energy1 || e2 != energy2) //Check that first and last energy values match
                return false;

            /*
                More concise implementation?
            if (!file.getEnergy().equals(file0_energy])
                return false;
             */
        }
        return true;
    }
}

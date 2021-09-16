package Data.Processors;

import Data.Models.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.fitting.*;

import javax.swing.*;

public class DataProcessor {

    private final String TEXTFILE = ".txt";
    private final String DATFILE = ".dat";
    private final String LSTFILE = ".lst";

    /**
     *
     * @param dataType - Type of file (I0, It, Itb, I0b)
     * @param fileName name of file (including extension)
     * @param fileHeader - Header of mean file
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

        ArrayList<XRaySample> meanSamples = new ArrayList<>();
        for (int i = 0; i <sampleNumber; i++) {
            ArrayList<XRaySample> samplesList = new ArrayList<>();
            for (DataFile file: files) {
                samplesList.add(file.getData().get(i));
            }
            XRaySample meanSample = generateMean(samplesList);
            meanSamples.add(meanSample);
        }
        String filePath = getFilePath(firstFile, fileName, TEXTFILE);
        return new DataFile(dataType, filePath, fileHeader, meanSamples);
    }

    /**
     * Generates a mean sample from a list of XRaySamples
     * @param samples
     * @return Mean sample
     */
    public XRaySample generateMean(ArrayList<XRaySample> samples) {
        //TODO data ranges problem

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
     * Takes a source DataFile and returns a new DataFile which generates counts
     * @param sourceFile
     * @return A DataFile which samples a polynomial to get a val
     */
    public DataFile generatePolyFile(DataFile sourceFile, String fileName, int polyDegree) {
        double[] coeff = generatePoly(sourceFile, polyDegree);
        String polynomialFunction = "f(x) = "; //String repesentation of polynomial function
        for (int i = 0; i < coeff.length; i++)
            polynomialFunction += coeff[i] + "*x^" + i + " ";

        ArrayList<XRaySample> samples = new ArrayList<>();
        for (XRaySample sample: sourceFile.getData()) {
            Double cnts = 0D;
            double energy = sample.getEnergy();
            double theta = sample.getTheta();
            for (int i = 0; i < coeff.length; i++) { //Sample polynomial to get counts_per_live value
                cnts+= Math.pow(energy, i) * coeff[i];
            }
            samples.add(new XRaySample(energy, theta, cnts));
        }

        MeasurementType type = sourceFile.getFileType();
        String path = getFilePath(sourceFile, fileName, TEXTFILE);
        String header = sourceFile.getHeader() + "\n";
        header += sourceFile.getFileName() + "\n";
        header += polynomialFunction;



        DataFile polynomialFile = new DataFile(type, path, header, samples);
        return polynomialFile;
    }

    /**
     *
     * @param file The source file to fit a polynomial to
     * @param polyDegree The degree of the polynomial to fit to the data
     * @return An array of coefficients of the polynomial in order of increasing degree, i.e. (a + bx + cx^2 ...) returns [a,b,c]
     */
    public double[] generatePoly(DataFile file, int polyDegree) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        ArrayList<XRaySample> samples = file.getData();

        /* Normalisation testing

         */
        double firstEnergy = samples.get(0).getEnergy();
        double firstCnts = samples.get(0).getCnts_per_live();


        for (int i = 0; i < samples.size(); i++) {
            XRaySample sample = samples.get(i);
            obs.add(sample.getEnergy(), sample.getCnts_per_live());
        }
        // Instantiate a third-degree polynomial fitter.
        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(polyDegree);

        // Retrieve fitted parameters (coefficients of the polynomial function).
        final double[] coeff = fitter.fit(obs.toList());
        return coeff;
    }

    //TODO theta or energy against counts? ENERGY
    public double[] generatePolyTest() {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < 100; i++) {
            obs.add(i,  Math.pow(i, 6) - Math.pow(i*2, 3) + 80);
        }

        // Instantiate a third-degree polynomial fitter.
        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(7);

        // Retrieve fitted parameters (coefficients of the polynomial function).
        final double[] coeff = fitter.fit(obs.toList());
        for (Double c: coeff
             ) {
            System.out.println(c);

        }
        return coeff;
    }

    public List<Double> calculatePolynomial (List<Double> inputs, List<Double> coefficients) {
        for (Double x: inputs) {
            for (int i = 0; i < coefficients.size(); i++) {

            }
        }
        return null;
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

    //TODO smarter way than reusing code for these methods
    public DataFile generateAbsorptionFile(DataFile i0, DataFile it, String header) {
        DataFile i0File = i0;
        DataFile itFile = it;
        if (!checkRanges(i0, it)) {
            ArrayList<DataFile> truncatedFiles = (ArrayList<DataFile>) truncateIfNeeded(i0File, itFile);
            i0File = truncatedFiles.get(0); itFile = truncatedFiles.get(1);
        }
        ArrayList<Double> absorptionList = calculateAbsorption(i0File, itFile);
        ArrayList<XRaySample> samples = new ArrayList<>();
        for (int i = 0; i < i0File.getData().size(); i++) {
            XRaySample source = i0File.getData().get(i); // As we have checked that ranges of all files match, it's okay to pick one arbitrarily
            double energy = source.getEnergy();
            double theta = source.getTheta();
            double counts = source.getCnts_per_live();
            double absorption = absorptionList.get(i);
            double i0Counts =  i0File.getData(DataType.COUNTS_PER_LIVE).get(i);
            double itCounts = itFile.getData(DataType.COUNTS_PER_LIVE).get(i);

            samples.add(new ProcessedSample(energy, theta, counts, absorption, i0Counts, itCounts));
            //samples.add(new XRaySample(source.getEnergy(), source.getTheta(), source.getCnts_per_live(), absorptionList.get(i)));
        }
        return new DataFile(MeasurementType.ABSORPTION, "", header,samples);
    }

    public DataFile generateAbsorptionFile(DataFile i0, DataFile it, DataFile i0b, DataFile itb, String header) throws NumberFormatException {
        DataFile i0File = i0;
        DataFile itFile = it;
        DataFile i0bFile = i0b;
        DataFile itbFile = itb;
        if (!checkRanges(i0File, itFile, i0bFile, itbFile)) {
            ArrayList<DataFile> truncatedFiles = (ArrayList<DataFile>) truncateIfNeeded(i0File, itFile, i0bFile, itbFile);
            i0File = truncatedFiles.get(0); itFile = truncatedFiles.get(1);
            i0bFile = truncatedFiles.get(2); itbFile = truncatedFiles.get(3);
        }
        ArrayList<Double> absorptionList = null;
        try {
            absorptionList = calculateAbsorption(i0File, itFile, i0bFile, itbFile);
        } catch (NumberFormatException nfe) {
            throw nfe;
        }
        ArrayList<XRaySample> samples = new ArrayList<>();
        for (int i = 0; i < i0File.getData().size(); i++) {
            XRaySample source = i0File.getData().get(i);
            double energy = source.getEnergy();
            double theta = source.getTheta();
            double counts = source.getCnts_per_live();
            double absorption = absorptionList.get(i);
            double i0Counts =  i0File.getData(DataType.COUNTS_PER_LIVE).get(i);
            double itCounts = itFile.getData(DataType.COUNTS_PER_LIVE).get(i);
            double i0bCounts =  i0bFile.getData(DataType.COUNTS_PER_LIVE).get(i);
            double itbCounts = itbFile.getData(DataType.COUNTS_PER_LIVE).get(i);


            samples.add(new ProcessedSample(energy, theta, counts, absorption, i0Counts, itCounts, i0bCounts, itbCounts));

            //samples.add(new XRaySample(source.getEnergy(), source.getTheta(), source.getCnts_per_live(), absorption.get(i)));
        }
        return new DataFile(MeasurementType.ABSORPTION, "", header,samples);
    }

    /**
     * Calculates a list of absorption values from i0, it, i0b and itb files.
     * @param i0File
     * @param itFile
     * @param i0bFile
     * @param itbFile
     * @return ArrayList of absorption values
     * @throws NumberFormatException
     */
    public ArrayList<Double> calculateAbsorption(DataFile i0File, DataFile itFile, DataFile i0bFile, DataFile itbFile) throws NumberFormatException {
        //TODO method body
        List<Double> it_counts = itFile.getCounts();
        Iterator<Double> i0 = i0File.getCounts().iterator();
        Iterator<Double> it = itFile.getCounts().iterator();
        Iterator<Double> i0b = i0bFile.getCounts().iterator();
        Iterator<Double> itb = itbFile.getCounts().iterator();

        ArrayList<Double> absorption = new ArrayList<>();
        while (i0.hasNext() && it.hasNext() && i0b.hasNext() && itb.hasNext()) {
            double a = Math.log((i0.next() - i0b.next()) / (it.next() - itb.next()));
            absorption.add(a);
            if (Double.isNaN(a))
                throw new NumberFormatException("Tried to calculate the logarithm of a negative number");
        }
        return absorption;
    }

    /**
     * Corrects the theta scale for a (file? sample?)
     */
    public void correctThetaScale(DataFile file) {
        //
    }

    /**
     * Checks that file (energy) ranges match
     * @param files - to check
     * @return true if ranges match
     */
    public boolean checkRanges(DataFile... files) {
        if (files.length == 0 || files.length == 1)
            return true;
        List<Double> file0_energy = files[0].getEnergy();
        DataFile firstFile = files[0];

        for (DataFile file: files) {
            if (!file.getEnergy().equals(file0_energy)) {
                return false;
            }

        }
        return true;
    }


    /**
     * For a list of files, returns versions for which the data is truncated such that only samples
     * with energy values present in all files remain.
     * @param files Input DataFiles
     * @return
     */
    public List<DataFile> correctRanges(DataFile... files) {
        double energyMin = 0; // The largest lower bound of energy among the files
        double energyMax = Double.MAX_VALUE; // The smallest upper bound of energy among the files
        ArrayList<DataFile> truncatedFiles = new ArrayList<>(); // List to store truncated files

        // Finds the smallest range appearing in the files
        for (DataFile file: files) {
            List<Double> energy = file.getEnergy();
            double lowerEnergy = energy.get(0);
            double upperEnergy = energy.get(energy.size() - 1);

            energyMin = Math.max(lowerEnergy, energyMin);
            energyMax = Math.min(upperEnergy, energyMax);

        }
        //TODO test

        //Copies the files, removing the measurements outside the minimum range
        for (DataFile file: files) {
            ArrayList<XRaySample> samples = new ArrayList<>();
            for (XRaySample sample: file.getData()) {
                if (sample.getEnergy() >= energyMin && sample.getEnergy() <= energyMax)
                    samples.add(sample);
            }
            DataFile truncatedFile = new DataFile(file.getFileType(), file.getFilePath(),
                    file.getHeader(), samples);
            truncatedFiles.add(truncatedFile);
        }
        return truncatedFiles;
    }


    /**
     * Creates a String path for a file of a given name, in the same directory as a given file
     * @param sourceFile
     * @param fileName
     * @return
     */
    private String getFilePath(DataFile sourceFile, String fileName, String fileExtension) {
        File saveDirectory = new File(sourceFile.getFilePath()).getParentFile();
        //TODO dat file
        String filePath = saveDirectory.getAbsolutePath() + System.getProperty("file.separator") + fileName;// + fileExtension;
        return filePath;
    }


    public List<DataFile> truncateIfNeeded(DataFile... files) {
        ArrayList<DataFile> fileList = new ArrayList<>();
        for (DataFile file: files) 
            fileList.add(file);
        return truncateIfNeeded(fileList);
    }

    /**
     * If the ranges of the data in the input files do not match, this function will return the files after truncating them so that
     * only the smallest range present throughout all files remains.
     * @param files
     * @return Truncated Files
     */
    public List<DataFile> truncateIfNeeded(List<DataFile> files) {
        DataFile[] filesArr = files.toArray(new DataFile[0]);
        if (!checkRanges(filesArr)) {
            files = correctRanges(filesArr);
            JOptionPane.showMessageDialog(new JFrame(), "File ranges do not match, using the smallest range present in all files.");
        }
        return files;
    }
}

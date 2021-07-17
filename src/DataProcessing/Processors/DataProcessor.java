package DataProcessing.Processors;

import DataProcessing.Models.DataFile;

public class DataProcessor {

    /**
     * @param files - any number of data files from which to generate the mean
     * @return mean data file generated from inputs
     */
    public DataFile generateMean(DataFile... files){
        //TODO method body
        for (DataFile file: files){
            System.out.println(file.getFilePath());
        }
        return null;
    }

    /**
     *
     * @param file
     * @return String representation of polynomial fit to data file
     */
    public String generatePolynomial(DataFile file) {
        //TODO method body
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

/**
 * Represents a data file, in the format:
 * - Header line(s)
 * - Asterisks
 * - Column names
 * - Column Data
 * @author Marco Seddon-Ferretti
 */

package Data.Models;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DataFile {

    private MeasurementType fileType; // The data type of the file (I0, It, I0b, Itb)
    private String filePath; // Path to the file in the user's file system
    private String header; // Header line(s) of file
    private ArrayList<XRaySample> data; // The data contained in the file

    public DataFile(MeasurementType dataType, String filePath, String fileHeader, ArrayList<XRaySample> data) {
        this.fileType = dataType;
        this.filePath = filePath;
        this.header = fileHeader;
        this.data = data;
    }

    /**
     * Getters and setters
     */
    public MeasurementType getFileType() {
        return fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public ArrayList<XRaySample> getData() {
        return data;
    }

    @Deprecated
    /**
     * @deprecated Should be replaced with getDataAsString(DataType... types) in all instances
     * @return The data contained in the sample as a string, in order Energy, Theta, Cnts_per_live
     */
    public List<String> getDataAsString() {
        List<String> stringData = new LinkedList<>();
            stringData.add("Energy (ev)\tTheta (deg)\tcnts_per_live");
            stringData.addAll(data.stream().map(x -> x.getEnergy() + "\t" + x.getTheta() +
                    "\t" + x.getCnts_per_live() + "\t").collect(Collectors.toList()));
        return stringData;
    }

    /**
     * Returns a string representation of the data in a file, of given types (one Sample of data = 1 line), with
     * column headers. Used when writing out .dat files.
     * @param types The DataType(s) to extract from the file
     * @return A string representation of the data, in the form: (column headers, line1, line2... lineN)
     */
    public List<String> getDataAsString(DataType... types) {

        List<String> stringData = new LinkedList<>();

        //Create line containing column names
        List<DataType> dataTypes = Arrays.asList(types);
        String columnNames = dataTypes.stream().map(x -> x.label).collect(Collectors.joining("\t ")) + "\t"; //Split column names with tabs
        stringData.add(columnNames);

        //Add data line by line
        for (XRaySample sample: getData()) {
            String line = "";
            for (DataType type: types) {
                line += (sample.getData(type) + "\t");
            }
            stringData.add(line);
        }
        return stringData;
    }

    @Deprecated
    /**
     *
     * @return A collection of all the measurements of energy in the data file
     */
    public List<Double> getEnergy() {
        ArrayList<XRaySample> data = this.getData();
        List<Double> energy = data.stream().map(x -> x.getEnergy()).collect(Collectors.toList());
        return energy;
    }

    @Deprecated
    /**
     *
     * @return A collection of all the measurements of theta in the data file
     */
    public List<Double> getTheta() {
        ArrayList<XRaySample> data = this.getData();
        List<Double> theta = data.stream().map(x -> x.getTheta()).collect(Collectors.toList());
        return theta;
    }

    /**
     * @return A collection of all the measurements of cnts_per_live in the data file
     */
    public List<Double> getCounts() {
        ArrayList<XRaySample> data = this.getData();
        List<Double> counts = data.stream().map(x -> x.getCnts_per_live()).collect(Collectors.toList());
        return counts;
    }

    @Deprecated
    /**
     * @return A collection of all the values of absorption in the data file
     */
    public List<Double> getAbsorption() {
        ArrayList<XRaySample> data = this.getData();
        List<Double> absorption = data.stream().map(x -> x.getAbsorption()).collect(Collectors.toList());
        return absorption;
    }
    //TODO handle corrected theta/energy

    /**
     * Gets all the data of one type from a DataFile
     * @param type The data type to return
     * @return List of doubles containing the data
     */
    public List<Double> getData(DataType type) {
        switch (type) {
            case ENERGY:
            case ENERGY_CORRECTED: //TODO IMPLEMENT
                return getEnergy();
            case THETA:
            case THETA_CORRECTED:
                return getTheta();
            case COUNTS_PER_LIVE: return getCounts();
            case ABSORPTION: return getAbsorption();
            default: return new ArrayList<Double>();
        }
    }

    public String getHeader() {
        return header;
    }

    public String getFileName() {
        Path filePath = Paths.get(this.getFilePath());
        return filePath.getFileName().toString();
    }

    @Override
    public String toString() {
        return getFileName();
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}

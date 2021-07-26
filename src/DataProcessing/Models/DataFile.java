/**
 * Represents a data file, in the format:
 * - Header line(s)
 * - Asterisks
 * - Column names
 * - Column Data
 * @author Marco Seddon-Ferretti
 */

package DataProcessing.Models;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DataFile {

    private MeasurementType dataType; // The data type of the file (I0, It, I0b, Itb)
    private String filePath; // Path to the file in the user's file system
    private String header; // Header line(s) of file
    private ArrayList<XRaySample> data; // The data contained in the file

    public DataFile(MeasurementType dataType, String filePath, String fileHeader, ArrayList<XRaySample> data) {
        this.dataType = dataType;
        this.filePath = filePath;
        this.header = fileHeader;
        this.data = data;
    }

    /**
     * Getters and setters
     */
    public MeasurementType getDataType() {
        return dataType;
    }

    public String getFilePath() {
        return filePath;
    }

    public ArrayList<XRaySample> getData() {
        return data;
    }

    /**
     *
     * @return The data contained in the sample as a string, in order Energy, Theta, Cnts_per_live
     */
    public List<String> getDataAsString() {
        //TODO design choice whether to duplicate and overload method for absorption, or add parameter
        List<String> stringData = new LinkedList<>();
        stringData.add("Energy(ev)\tTheta\tcnts_per_live");
        stringData.addAll(data.stream().map( x -> x.getEnergy() + "\t" + x.getTheta() +
                "\t" + x.getCnts_per_live() + "\t").collect(Collectors.toList()));
        return stringData;
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
}

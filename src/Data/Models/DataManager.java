/**
 * Represents all the data files which have been loaded in by the user
 * @author Marco Seddon-Ferretti
 */
package Data.Models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataManager {

    //Lists of data by Measurement Type
    ArrayList<DataFile> i0Files = new ArrayList<>();
    ArrayList<DataFile> i0bFiles = new ArrayList<>();
    ArrayList<DataFile> itFiles = new ArrayList<>();
    ArrayList<DataFile> itbFiles = new ArrayList<>();
    ArrayList<DataFile> absorptionFiles = new ArrayList<>();
    ArrayList<DataFile> correctedFiles = new ArrayList<>();

    DataFile calibrationFile; //File used for calibration when correcting energy and theta scales


    /**
     * Adds a  DataFile to the list of the appropriate type
     * @param file
     * @param type
     */
    public void addFile(DataFile file, MeasurementType type) {
        getList(type).add(file);
    }

    /**
     * Adds an ArrayList of DataFiles to the appropriate array
     * @param files
     * @param type
     */
    public void addFiles(ArrayList<DataFile> files, MeasurementType type) {
        getList(type).addAll(files);
    }

    /**
     * Gets a list of Data File names of the given type
     * @param type - MeasurementType of data
     * @return Array of file names
     */
    public ArrayList<String> getFileNames(MeasurementType type) {
        ArrayList<DataFile> files = getList(type);
        ArrayList<String> fileNames = (ArrayList<String>) files.stream().map(
                                    datafile -> datafile.getFileName()).collect(Collectors.toList());
        return fileNames;
    }

    /**
     * Clears all files of a given type
     * @param type
     */
    public void clearFiles(MeasurementType type, List<DataFile> files) {
        ArrayList<DataFile> fileList = getList(type);
        for (DataFile file: files) {
            fileList.remove(file);
        }
    }

    /**
     * Clears all files of a given type
     * @param type
     */
    public void clearFiles(MeasurementType type) {
        getList(type).clear();
    }

    /**
     * Clears all currently loaded files
     */
    public void clearFiles() {
        for (MeasurementType type: MeasurementType.values()) {
            getList(type).clear();
        }
    }

    /**
     * Returns the file list containing the files of a given MeasurementType
     * @param type MeasurementType of list to retrieve
     * @return List of files of "type" stored in DataManager
     * @throws IllegalArgumentException - if type parameter is not a member of enum MeasurementType
     */
    public ArrayList<DataFile> getList(MeasurementType type) throws IllegalArgumentException {
        ArrayList<DataFile> files;
        switch (type) {
            case I0: files = i0Files; break;
            case I0b: files = i0bFiles; break;
            case It: files = itFiles; break;
            case Itb: files = itbFiles; break;
            case ABSORPTION: files = absorptionFiles; break;
            case CORRECTED: files = correctedFiles; break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + type +". \n MeasurementType not recognised.");
        }
        return files;
    }

    public DataFile getCalibrationFile() {
        return calibrationFile;
    }

    public void setCalibrationFile(DataFile calibrationFile) {
        this.calibrationFile = calibrationFile;
    }
}

/**
 * Represents all the data files which have been loaded in by the user
 * @author Marco Seddon-Ferretti
 */
package DataProcessing.Models;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DataManager {

    //Lists of data by Measurement Type
    ArrayList<DataFile> i0Files = new ArrayList<>();
    ArrayList<DataFile> i0bFiles = new ArrayList<>();
    ArrayList<DataFile> itFiles = new ArrayList<>();
    ArrayList<DataFile> itbFiles = new ArrayList<>();

    public DataManager() {
    }


    //Adds a collection of Data Files to the appropriate array
    public void addFiles(ArrayList<DataFile> files, MeasurementType type) {
        switch (type) {
            case I0: i0Files.addAll(files);
            case I0b: i0bFiles.addAll(files);
            case It: itFiles.addAll(files);
            case Itb: itbFiles.addAll(files);
        }
    }


    /**
     * Gets a list of Data File names of the given type
     * @param type - MeasurementType of data
     * @return Array of file names
     */
    public ArrayList<String> getFileNames(MeasurementType type) {
        ArrayList<DataFile> files;
        switch (type) {
            case I0: files = i0Files;
            case I0b: files = i0bFiles;
            case It: files = itFiles;
            case Itb: files = itbFiles;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        ArrayList<String> fileNames = (ArrayList<String>) files.stream().map(
                                    datafile -> datafile.getFileName()).collect(Collectors.toList());
        return fileNames; //Passing 0 size array causes it to create new array
    }
}

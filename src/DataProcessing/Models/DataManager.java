/**
 * Represents all the data files which have been loaded in by the user
 * @author Marco Seddon-Ferretti
 */
package DataProcessing.Models;

import java.util.ArrayList;

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
}

package IO;
/**
 * Reads x-ray data files and stores the contents as DataFile objects
 * @author Marco Seddon-Ferretti
 */

import DataProcessing.Models.DataFile;
import DataProcessing.Models.MeasurementType;
import DataProcessing.Models.XRaySample;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class FileLoader {


    /**
     * Indeces of the relevant columns of data, input by the user when the first data set is loaded
     * Set initially to -1
     */
    int energyIndex = -1;
    int thetaIndex = -1;
    int countsIndex = -1;
    public boolean valuesInitialised = false;

    public void setEnergyIndex(int energyIndex) {
        this.energyIndex = energyIndex;
    }
    public void setThetaIndex(int thetaIndex) {
        this.thetaIndex = thetaIndex;
    }
    public void setCountsIndex(int countsIndex) {
        this.countsIndex = countsIndex;
    }
    public void setValuesInitialised(boolean valuesInitialised) {
        this.valuesInitialised = valuesInitialised;
    }

    /**
     *
     * @param file
     * @return String array of file names, or empty array if error reading file
     */
    public String[] getColumnNames(File file) {
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                //Find the line which separates the header from column names
                if (line.startsWith("*")) {
                    String[] columnNames = scanner.nextLine().split("\t");
                    return columnNames;
                }
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.out.println("Could not locate file: " + file.getName());
        }
        return new String[0];
    }








    //-----------------------

    /**
     * Loads a data file from the file system
     * @param type The data type of the file, provided by the user through the GUI
     * @param file The file to load data from
     * @return Returns a DataFile object representing the loaded file
     */
    public DataFile loadFile(MeasurementType type, File file) {
        String filePath = file.getAbsolutePath();
        String fileHeader = parseHeader(file);
        ArrayList<XRaySample> fileMeasurements = parseMeasurements(file,1,2,3);

        DataFile loadedFile = new DataFile(type, filePath, fileHeader, fileMeasurements);
        return loadedFile;
    }

    /**
     * Loads multiple files from
     * @param dataType The data type of the file, provided by the user through the GUI
     * @param files
     * @return ArrayList of loaded DataFile objects
     */
    public ArrayList<DataFile> loadFiles(MeasurementType dataType, File... files) {
        ArrayList<DataFile> loadedFiles = new ArrayList<>();
        for (File file: files) {
            loadedFiles.add(loadFile(dataType, file));
        }
        return loadedFiles;
    }

    /**
     * Reads the header line(s) from the given file
     * @param file
     * @return File header
     */
    private String parseHeader(File file) {
        //TODO method body
        return "";
    }


    /**
     * Converts each data line in a text file into an DataProcessing.Models.XRaySample object
     * @param file
     * @return - The list of XRaySamples in the file
     */
    private static ArrayList<XRaySample> parseMeasurements(File file, int energyIndex, int thetaIndex, int countsIndex) {
        LinkedList<String> fileLines = new LinkedList<>();

        //Read line of file which contains data
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                //TODO better check for which lines are actual data lines
                if (!line.contains("eV") && !line.contains("*"))  //Only read the actual data lines
                    fileLines.add(line);
            }

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.out.println("Could not locate file: " + file.getName());
        }

        //Parse each line, extracting the relevant measurements and creating DataProcessing.Models.XRaySample for each line
        ArrayList<XRaySample> samples = new ArrayList<>();
        for (String line: fileLines) {
            String[] measurements = line.split("\t");
            //TODO detect/give user option of which columns are relevant
            samples.add(new XRaySample(measurements[energyIndex], measurements[countsIndex], measurements[thetaIndex]));
        }
        return samples;
    }


}

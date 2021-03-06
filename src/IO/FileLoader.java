package IO;
/**
 * Reads x-ray data files (text files) and stores the contents as DataFile objects.
 */

import Data.Models.DataFile;
import Data.Models.MeasurementType;
import Data.Models.XRaySample;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class FileLoader {


    /**
     * Indeces of the relevant columns of data, input by the user when the first data set is loaded
     * Set initially to -1, when they are set, the valuesInitialised flag is set to true and the
     * values are stored for the rest of the session.
     */
    int energyIndex = -1;
    int thetaIndex = -1;
    int countsIndex = -1;
    int icrIndex = -1;
    int ocrIndex = -1;

    public boolean valuesInitialised = false;
    String[] columnNames;

    public void setEnergyIndex(int energyIndex) {
        this.energyIndex = energyIndex;
    }
    public void setThetaIndex(int thetaIndex) {
        this.thetaIndex = thetaIndex;
    }
    public void setCountsIndex(int countsIndex) { this.countsIndex = countsIndex; }
    public void setICRIndex(int icrIndex) { this.icrIndex = icrIndex; }
    public void setOCRIndex(int ocrIndex) { this.ocrIndex = ocrIndex; }

    public void setValuesInitialised(boolean valuesInitialised) {
        this.valuesInitialised = valuesInitialised;
    }
    public void setIndeces(int energy, int theta, int counts) {
        this.energyIndex = energy;
        this.thetaIndex = theta;
        this.countsIndex = counts; //TODO icr ocr
    }

    public void setIndeces(int energy, int theta, int counts, int icr, int ocr) {
        this.energyIndex = energy;
        this.thetaIndex = theta;
        this.countsIndex = counts;
        this.icrIndex = icr;
        this.ocrIndex = ocr;
    }

    /**
     * Returns a String[] of the column names of each type of measurement in the data file.
     * @param file
     * @return String array of file names, or empty array if error reading file
     */
    public String[] parseColumnNames(File file) {
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                //Find the line which separates the header from column names
                if (line.startsWith("*")) {
                    String[] columnNames = scanner.nextLine().split("\t");
                    if (columnNames.length == 0) {
                        JOptionPane.showMessageDialog(new JFrame(), "Unable to find any data columns");
                    }
                    scanner.close();
                    return columnNames;
                }
            }
            scanner.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.out.println("Could not locate file: " + file.getName());
        }
        return new String[0];
    }

    /**
     * Loads a data file from the file system
     * @param type The data type of the file, provided by the user through the GUI
     * @param file The file to load data from
     * @return Returns a DataFile object representing the loaded file
     */
    public DataFile loadFile(MeasurementType type, File file) throws ArrayIndexOutOfBoundsException {
        String filePath = file.getAbsolutePath();
    String fileHeader = parseHeader(file);
        ArrayList<XRaySample> fileMeasurements = parseMeasurements(file, energyIndex, thetaIndex, countsIndex, icrIndex, ocrIndex);
        DataFile loadedFile = new DataFile(type, filePath, fileHeader, fileMeasurements);
        return loadedFile;
    }

    /**
     * Loads multiple files from the file system, using the loadFile method
     * @param dataType The data type of the file, provided by the user through the GUI
     * @param files
     * @return ArrayList of loaded DataFile objects
     */
    public ArrayList<DataFile> loadFiles(MeasurementType dataType, File... files) throws ArrayIndexOutOfBoundsException {
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
    public String parseHeader(File file) {
        String header = "";
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Read lines until data separator (*)
                if (!line.contains("***"))  //Only read the actual data lines
                    header += line;
                else {
                    scanner.close();
                    break;
                }
            }

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.out.println("Could not locate file: " + file.getName());
        }
        return header;
    }


    /**
     * Converts each data line in a text file into an Data.Models.XRaySample object
     * @param file
     * @return - The list of XRaySamples in the file
     */
    private static ArrayList<XRaySample> parseMeasurements(File file, int energyIndex, int thetaIndex, int countsIndex, int ocrIndex, int icrIndex)
    throws ArrayIndexOutOfBoundsException {
        LinkedList<String> fileLines = new LinkedList<>();

        //Read line of file which contains data
        try {
            Scanner scanner = new Scanner(file);
            //Skip lines until data separator, and then column names
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("***")) {
                    scanner.nextLine(); // skip column names line
                    //TODO better implementation, make it check for numbers -   line.contains(({0-9}.\t{0.9}) maybe;
                    break;
                }
            }
            while (scanner.hasNextLine()) {
                fileLines.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.out.println("Could not locate file: " + file.getName());
        }

        //Parse each line, extracting the relevant measurements and creating Data.Models.XRaySample for each line
        ArrayList<XRaySample> samples = new ArrayList<>();
        for (String line: fileLines) {
            String[] measurements = line.split("\t");
            //TODO do this in a sensible way - update xray sample to contain both corrected and uncorrected, and correct counts in constructor
                String icr = measurements[icrIndex];
                String ocr = measurements[ocrIndex];
                samples.add(new XRaySample(measurements[energyIndex], measurements[thetaIndex], measurements[countsIndex], icr, ocr));
        }
        return samples;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }




}

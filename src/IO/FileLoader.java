package IO;
/**
 * Reads x-ray data files and stores the contents as DataFile objects
 * @author Marco Seddon-Ferretti
 */

import DataProcessing.Models.DataFile;
import DataProcessing.Models.MeasurementType;
import DataProcessing.Models.XRaySample;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class FileLoader extends AbstractIO {


    /**
     * Indeces of the relevant columns of data, input by the user when the first data set is loaded
     * Set initially to -1, when they are set, the valuesInitialised flag is set to true and the
     * values are stored for the rest of the session.
     */
    int energyIndex = -1;
    int thetaIndex = -1;
    int countsIndex = -1;
    public boolean valuesInitialised = false;
    String[] columnNames;

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
    public void setIndeces(int energy, int theta, int counts) {
        this.energyIndex = energy;
        this.thetaIndex = theta;
        this.countsIndex = counts;
    }

    /**
     *
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
                    return columnNames;
                }
            }
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
    public DataFile loadFile(MeasurementType type, File file) {
        String filePath = file.getAbsolutePath();
        String fileHeader = parseHeader(file);
        ArrayList<XRaySample> fileMeasurements = parseMeasurements(file,energyIndex,thetaIndex,countsIndex);

        DataFile loadedFile = new DataFile(type, filePath, fileHeader, fileMeasurements);
        return loadedFile;
    }

    /**
     * Loads multiple files from the file system, using the loadFile method
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
    public String parseHeader(File file) {
        String header = "";
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Read lines until data separator (*)
                if (!line.contains("*"))  //Only read the actual data lines
                    header += line;
                else
                    break;
            }

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.out.println("Could not locate file: " + file.getName());
        }
        return header;
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
            //Skip lines until data separator, and then column names
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("***")) {
                    scanner.nextLine(); // skip column names line
                    //TODO janky fix, make it check for numbers
                    break;
                }
            }
            while (scanner.hasNextLine()) {
                fileLines.add(scanner.nextLine());
            }



        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.out.println("Could not locate file: " + file.getName());
        }

        //Parse each line, extracting the relevant measurements and creating DataProcessing.Models.XRaySample for each line
        ArrayList<XRaySample> samples = new ArrayList<>();
        for (String line: fileLines) {
            String[] measurements = line.split("\t");
            samples.add(new XRaySample(measurements[energyIndex], measurements[thetaIndex], measurements[countsIndex]));
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

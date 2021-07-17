package IO; /**
 *
 * @author - Marco Seddon-Ferretti
 *
 * Data information:
 *  I0 = no sample present
 *  It = sample present
 *  I0b = instrument background measured with no sample present
 *  Itb = instrument background measured with sample present
 */

import DataProcessing.Models.XRaySample;

import java.io.File;
import java.io.FileNotFoundException;
//import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class FileInput {

    //Connect each individual data file with the data it contains
    static HashMap<File, LinkedList<XRaySample>> noSampleMap;
    static HashMap<File, LinkedList<XRaySample>> sampleMap;
    static HashMap<File, LinkedList<XRaySample>> noSampleBgMap;
    static HashMap<File, LinkedList<XRaySample>> sampleBgMap;
    //Directories containing the 4 types of measurement data
    static File noSampleDir;
    static File sampleDataDir;
    static File noSampleBackgroundDir;
    static File sampleBackgroundDir;

    /**
     * Static initialiser to load data from files
     */
    static {

        //Main data directory - directory which contains 1 directory for each data type
        String dataPath = (System.getProperty("user.dir") + "/data" );
        File mainDataFolder = new File(dataPath);

        //Directories for each type of data file
        noSampleDir = new File(dataPath + "/I0");
        sampleDataDir = new File(dataPath + "/It");
        noSampleBackgroundDir = new File(dataPath + "/I0b");
        sampleBackgroundDir = new File(dataPath + "/Itb");

        noSampleMap = processDataFile(noSampleDir);
        sampleMap = processDataFile(sampleDataDir);
        noSampleBgMap = processDataFile(noSampleBackgroundDir);
        sampleBgMap = processDataFile(sampleBackgroundDir);
    }

    /**
     * Proccesses all text files in a directory
     * @param dataFolder - the directory containing the text files to process
     * @return - a map between each file and the data it contains
     */
    private static HashMap<File, LinkedList<XRaySample>> processDataFile(File dataFolder) {

        //A map linking each text file with the data it contains
        HashMap<File, LinkedList<XRaySample>> fileSamplesMap = new HashMap();
        //Attempt to read files from directory
        File[] testFiles = dataFolder.listFiles();

        //Check valid parent directory was provided
        if (testFiles == null) {
            System.out.println("Error locating file directory");
            System.exit(1);
        }
        //Check provided directory contains files
        if (testFiles.length == 0) {
            System.out.println("No files found in provided directory");
            System.exit(1);
        }

        //Iterate through files, populating map
        for(File file: testFiles) {
            fileSamplesMap.put(file, extractSamples(file));
        }
        return fileSamplesMap;
    }


    /**
     * Converts each data line in a text file into an DataProcessing.Models.XRaySample object
     * @param file
     * @return - The list of XRaySamples in the file
     */
    private static LinkedList<XRaySample> extractSamples(File file) {
        LinkedList<String> fileLines = new LinkedList<>();

        //Read line of file which contains data
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.contains("eV") && !line.contains("*"))  //Only read the actual data lines
                    fileLines.add(line);
            }

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.out.println("Could not locate file: " + file.getName());
        }

        //Parse each line, extracting the relevant measurements and creating DataProcessing.Models.XRaySample for each line
        LinkedList<XRaySample> samples = new LinkedList<>();
        for (String line: fileLines) {
            String[] measurements = line.split("\t");
            samples.add(new XRaySample(measurements[0], measurements[1], measurements[8]));
        }
        return samples;
    }

    /**
     * Getters
     */
    public HashMap getNoSampleMap() {
        return noSampleMap;
    }

    public HashMap getSampleMap() {
        return sampleMap;
    }

    public HashMap getNoSampleBgMap() {
        return noSampleBgMap;
    }

    public HashMap getSampleBgMap() {
        return sampleBgMap;
    }
}

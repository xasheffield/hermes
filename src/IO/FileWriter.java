package IO;

import Data.Models.DataFile;
import Data.Models.DataType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Writes user generated/edited data sets to files
 * @author Marco Seddon-Ferretti
 */

public class FileWriter {

    private final String fileSeparator = new String(new char[27]).replace("\0", "*");

    /**
     * Writes a DataFile out as a .dat file
     * @param file The file to write out
     */
    public void writeDataFile(DataFile file) throws IOException {
        String header = file.getHeader();
        List<String> data = file.getDataAsString();
        data.add(0, fileSeparator);
        data.add(0, header);
        Path testfile = Paths.get(file.getFilePath() + ".dat");
        Files.write(testfile, data, StandardCharsets.UTF_8);//, StandardOpenOption.APPEND);
    }

    /**
     * Writes a DataFile out as a .dat file
     * @param file The file to write out
     */
    public void writeDataFile(DataFile file, DataType... types) throws IOException {
        String header = file.getHeader();
        List<String> data = file.getDataAsString(types);
        data.add(0, fileSeparator);
        data.add(0, header);
        Path testfile = Paths.get(file.getFilePath() + ".dat");
        Files.write(testfile, data, StandardCharsets.UTF_8);//, StandardOpenOption.APPEND);
    }

    @Deprecated
    /**
     *
     */
    public void writeAbsorptionFile(DataFile file) throws IOException {
        String header = file.getHeader();
        List<String> data = file.getDataAsString(DataType.ENERGY, DataType.THETA, DataType.ABSORPTION);
        data.add(0, fileSeparator);
        data.add(0, header);
        Path testfile = Paths.get(file.getFilePath() + ".dat");
        Files.write(testfile, data, StandardCharsets.UTF_8);//, StandardOpenOption.APPEND);
    }

    //TODO unify overloaded writeAbsorptionFile if possible
    /**
     * Writes out an absorption file (.dat) for case where it is generated from two files
     * @param absorption File to write out
     * @param i0 i0 source for absorption file
     * @param it i0t source for absorption file
     * @throws IOException
     */
    public void writeAbsorptionFile(DataFile absorption, DataFile i0, DataFile it) throws IOException {
        String header = absorption.getHeader();
        List<String> data = absorption.getDataAsString(DataType.ENERGY, DataType.THETA, DataType.ABSORPTION);
        String columnNames = data.get(0);
        columnNames += "i0\tit\t";
        data.remove(0);

        //Add i0 and it counts per live columns
        Iterator<Double> i0Iter = i0.getCounts().iterator();
        Iterator<Double> itIter = it.getCounts().iterator();
        for (int i = 0; i < data.size(); i++) {
            String extraData = i0Iter.next() + "\t" + itIter.next() + "\t";
            data.set(i, data.get(i) + extraData);
        }

        data.add(0, columnNames);
        data.add(0, fileSeparator);
        data.add(0, header);
        Path testfile = Paths.get(absorption.getFilePath() + ".dat");
        Files.write(testfile, data, StandardCharsets.UTF_8);//, StandardOpenOption.APPEND);
    }

    /**
     *
     * @param absorption
     * @param i0 i0 source for absorption file
     * @param it i0t source for absorption file
     * @param i0b i0b source for absorption file
     * @param itb itb source for absorption file
     * @throws IOException
     */
    public void writeAbsorptionFile(DataFile absorption, DataFile i0, DataFile it, DataFile i0b, DataFile itb) throws IOException {
        String header = absorption.getHeader();
        List<String> data = absorption.getDataAsString(DataType.ENERGY, DataType.THETA, DataType.ABSORPTION);
        String columnNames = data.get(0);
        columnNames += "i0\tit\ti0 - i0b\tit - itb\ti0b\titb";
        data.remove(0);

        //Add i0 and it counts per live columns
        Iterator<Double> i0Iter = i0.getCounts().iterator();
        Iterator<Double> itIter = it.getCounts().iterator();
        Iterator<Double> i0bIter = i0.getCounts().iterator();
        Iterator<Double> itbIter = it.getCounts().iterator();
        for (int i = 0; i < data.size(); i++) {
            Double itCount = itIter.next();
            Double itbCount = itbIter.next();
            Double i0Count = i0Iter.next();
            Double i0bCount = i0bIter.next();

            String extraData = i0Count + "\t" + itCount + "\t" + (i0Count - i0bCount) + "\t" + (itCount - itbCount) + "\t" + i0bCount + itbCount;
            data.set(i, data.get(i) + extraData);
        }

        data.add(0, columnNames);
        data.add(0, fileSeparator);
        data.add(0, header);
        Path testfile = Paths.get(absorption.getFilePath() + ".dat");
        Files.write(testfile, data, StandardCharsets.UTF_8);//, StandardOpenOption.APPEND);
    }

    /**
     * Writes out a .lst file containing a header, followed by a list of names of each DataFile and its respective header
     * @param files DataFiles to list
     * @param fileName Name of .lst file
     * @param header Header of .lst file
     * @param path Directory to write file to
     * @throws IOException
     */
    public void writeLstFile(List<DataFile> files, String fileName, String header, String path) throws IOException {
        List<String> toWrite = new LinkedList<>();
        toWrite.add(header);
        for (DataFile file: files) {
            toWrite.add(file.getHeader());
            toWrite.add(file.getFileName());
            toWrite.add("---");
        }
        Path testfile = Paths.get(path + fileName + ".txt");
        Files.write(testfile, toWrite, StandardCharsets.UTF_8);
    }

    /**
     * Method for writing an lst file describing a processed file with corrected energy and theta scales.
     * @param correctedFile - Name of the corrected file described by the lst
     * @param calibrationFileName - Name of file used for calibration
     * @param calibrationEnergy - Value of energy used for calibrationm
     * @param calibrationTheta - Value of theta used for calibration
     * @param eMono - Monochromator energy
     * @param thetaShift - Applied theta shift (deg)
     */
    public void writeCorrectedLstFile(DataFile correctedFile, String calibrationFileName, double calibrationEnergy, double calibrationTheta,
                                      double eMono, double thetaShift, double energyObserved, double energyTrue) throws IOException {
        List<String> toWrite = new LinkedList<>();
        toWrite.add("Calibration data file name: " + calibrationFileName);
        toWrite.add("Calibration Energy (eV): " + calibrationEnergy);
        toWrite.add("Calibration Theta (deg): " + calibrationTheta);
        toWrite.add("Emono (eV): " + eMono);
        toWrite.add("Theta Shift: " + thetaShift);
        toWrite.add("Observed Energy (eV): " + energyObserved);
        toWrite.add("True Energy (eV): " + energyTrue);

        Path path = Paths.get(correctedFile.getFilePath() + ".txt");
        Files.write(path, toWrite, StandardCharsets.UTF_8);

    }
}

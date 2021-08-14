package IO;

import DataProcessing.Models.DataFile;
import DataProcessing.Models.DataType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Writes user generated/edited data sets to files
 * @author Marco Seddon-Ferretti
 */

public class FileWriter {

    private final String fileSeparator = new String(new char[27]).replace("\0", "*");

    /**
     *
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

    public void writeAbsorptionFile(DataFile file) throws IOException {
        String header = file.getHeader();
        List<String> data = file.getDataAsString(DataType.ENERGY, DataType.THETA, DataType.ABSORPTION);
        data.add(0, fileSeparator);
        data.add(0, header);
        Path testfile = Paths.get(file.getFilePath() + ".dat");
        Files.write(testfile, data, StandardCharsets.UTF_8);//, StandardOpenOption.APPEND);
    }

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

    public void writeLstFile(List<DataFile> files, String fileName, String header, String path) throws IOException {
        List<String> toWrite = new LinkedList<>();
        toWrite.add(header);
        for (DataFile file: files) {
            toWrite.add(file.getHeader());
            toWrite.add(file.getFileName());
            toWrite.add("---");
        }

        Path testfile = Paths.get(path + fileName + ".lst");
        Files.write(testfile, toWrite, StandardCharsets.UTF_8);//, StandardOpenOption.APPEND);
        /*
        if (string.contains("/")) {

        }

         */
    }
}

package IO;

import DataProcessing.Models.DataFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Writes user generated/edited data sets to files
 * @author Marco Seddon-Ferretti
 */

public class FileWriter {

    private final String fileSeparator = new String(new char[27]).replace("\0", "*");

    /**
     *
     * @param file The file to write out
     * @param file The DataFile to be written
     */
    public void writeDataFile(DataFile file) throws IOException {
        String header = file.getHeader();
        List<String> data = file.getDataAsString();
        data.add(0, fileSeparator);
        data.add(0, header);
        Path testfile = Paths.get(file.getFilePath());
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

package IO;

import DataProcessing.Models.DataFile;

import java.io.File;
import java.nio.file.Path;

/**
 * Writes user generated/edited data sets to files
 * @author Marco Seddon-Ferretti
 */

public class FileWriter {

    /**
     *
     * @param destination The directory to which to write the file
     * @param file The DataFile to be written
     */
    public void writeDataFile(Path destination, String fileName, DataFile file) {
        String header = file.getHeader();

        File newFile = new File(destination + fileName);


    }

    public void writeLstFile() {

    }
}

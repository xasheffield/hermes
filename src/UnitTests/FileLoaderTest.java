package UnitTests;

import DataProcessing.Models.DataFile;
import DataProcessing.Models.DataFileExt;
import DataProcessing.Models.MeasurementType;
import IO.FileLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileLoaderTest {

    private static FileLoader fileLoader;
    private static DataFile dfile;
    private static File file;
    private static String FILEPATH = "/Users/Marco/IdeaProjects/XRayData/data/I0/I0UL3Si1266_1_alldata_1.txt";
    static String[] COLUMNNAMES = {"Energy_(eV)","theta_(deg)","source_(ustep)", "detect_(ustep)","ROI_counts",
        "total_counts","real_time","live_time","cnts_per_live","ICR_(cnts/sec)","OCR_(cnts/sec)"};


    @BeforeAll
    public static void setUp(){
        fileLoader = new FileLoader();
        file = new File(FILEPATH);
    }

    @Test
    public void testGetColumnNames() {
        String[] names = fileLoader.getColumnNames(file);
        for (int i = 0; i < names.length; i++) {
            assertEquals(names[i], COLUMNNAMES[i]);
            System.out.println(names[i]);
        }
    }

}
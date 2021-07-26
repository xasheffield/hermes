package UnitTests;

import DataProcessing.Models.DataFile;
import DataProcessing.Models.MeasurementType;
import DataProcessing.Models.XRaySample;
import IO.FileLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileLoaderTest {

    private static FileLoader fileLoader;
    private static DataFile dfile;
    private static File file;
    private static String FILE = "/data/I0/I0UL3Si1266_1_alldata_1.txt";

    static String[] COLUMNNAMES = {"Energy_(eV)","theta_(deg)","source_(ustep)", "detect_(ustep)","ROI_counts",
        "total_counts","real_time","live_time","cnts_per_live","ICR_(cnts/sec)","OCR_(cnts/sec)"};

    static String fileHeader = "I0 Pd 25kV 3.5mA 0.5eV Si1266 20s";
    static String filePath;


    @BeforeAll
    public static void setUp(){
        fileLoader = new FileLoader();
        fileLoader.setIndeces(0,1, 8);


        String basePath = System.getProperty("user.dir");
        basePath += FILE;
        basePath = formatForOS(basePath);
        filePath = basePath;
        file = new File(basePath);

    }

    @Test
    public void testGetColumnNames() {
        String[] names = fileLoader.getColumnNames(file);
        for (int i = 0; i < names.length; i++) {
            assertEquals(names[i], COLUMNNAMES[i]);
            //System.out.println(names[i]);
        }
    }

    @Test
    public void testParseHeader() {
        String header = fileLoader.parseHeader(file);
        assertEquals(header, fileHeader);
    }

    /**
     * Test the load file function by loading a file, and comparing the measurements
     * it reads for the first and last line to the actual data in the file.
     */
    @Test
    public void testLoadFile() {
        DataFile i0_1 = fileLoader.loadFile(MeasurementType.I0, file);
        XRaySample firstLine = i0_1.getData().get(0);
        XRaySample lastLine = i0_1.getData().get(i0_1.getData().size() - 1);

        //Test first line of file
        assertEquals(firstLine.getEnergy(),16934.0);
        assertEquals(firstLine.getTheta(), 82.1662737);
        assertEquals(firstLine.getCnts_per_live(), 2127.48247);
        //Test last line of file
        assertEquals(lastLine.getEnergy(),17454.0);
        assertEquals(lastLine.getTheta(), 73.9774659);
        assertEquals(lastLine.getCnts_per_live(), 1428.51883);

        //System.out.println(firstLine.toString());
        //System.out.println(lastLine.toString());
    }


    /**
     * Cross-platform compatibility
     */
    //Needed to properly format file paths
    public static boolean isWindows(){
        return System.getProperty("os.name").startsWith("Windows");
    }

    //Formats paths to work on Windows
    private static String formatForOS(String path) {
        if (isWindows())
            return path.replace("/", "\\");
        else
            return path.replace("\\", "/");
    }


    }
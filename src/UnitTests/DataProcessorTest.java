package UnitTests;

import DataProcessing.Models.DataFile;
import DataProcessing.Models.MeasurementType;
import DataProcessing.Models.XRaySample;
import DataProcessing.Processors.DataProcessor;
import IO.FileLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DataProcessorTest {

    static DataProcessor dp;
    static FileLoader dl;
    static ArrayList<XRaySample> samples = new ArrayList<>();
    private static File file;
    private static File file2;
    private static File file3;
    private static String FILE = "/data/I0/I0UL3Si1266_1_alldata_1.txt";
    private static String FILE2 = "/data/I0/I0UL3Si1266_1_alldata_1.txt";
    private static String FILE3 = "/data/I0/shorter_file.txt";
    private static DataFile dfile;
    private static DataFile dfile2;
    private static DataFile dfile3;

    @BeforeAll
    static void setup(){
        XRaySample s1 = new XRaySample(5D, 30D, 50D);
        XRaySample s2 = new XRaySample(10D, 50D, 100D);
        XRaySample s3 = new XRaySample(15D, 100D, 300D);
        dp = new DataProcessor();
        samples.add(s1);
        samples.add(s2);
        samples.add(s3);

        file = pathToFile(FILE);
        file2 = pathToFile(FILE2);
        file3 = pathToFile(FILE3);

        dl = new FileLoader();
        dl.setIndeces(0, 1, 8);
        dfile = dl.loadFile(MeasurementType.I0, file);
        dfile2 = dl.loadFile(MeasurementType.I0, file2);
        dfile3 = dl.loadFile(MeasurementType.I0, file3);
    }

    @Test
    void generateMeanSample() {
        XRaySample meanSample = dp.generateMean(samples);
        assertEquals(10, meanSample.getEnergy());
        assertEquals(60, meanSample.getTheta());
        assertEquals(150, meanSample.getCnts_per_live());
    }

    @Test
    void generateMeanFile() {
    }

    @Test
    void generatePolynomial() {
        //double[] x = dp.generatePoly(dfile, 7);
        double[] x = dp.generatePolyTest();
        for (double degree: x) {
            System.out.println(degree);
        }
        //TODO test this

    }

    /** dfile and dfile2 are the same file, 3 is a shorter file
     *
     */
    @Test
    void checkRangesTest() {
        assertTrue(dp.checkRanges(dfile, dfile2));
        assertFalse(dp.checkRanges(dfile, dfile3));
        assertFalse(dp.checkRanges(dfile2, dfile3));

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

    private static File pathToFile(String path) {
        String basePath = System.getProperty("user.dir");
        basePath += path;
        basePath = formatForOS(basePath);
        File newFile = new File(basePath);
        return newFile;
    }
}
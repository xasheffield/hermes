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
    private static String FILE = "/data/I0/I0UL3Si1266_1_alldata_1.txt";
    private static DataFile dfile;

    @BeforeAll
    static void setup(){
        XRaySample s1 = new XRaySample(5D, 30D, 50D);
        XRaySample s2 = new XRaySample(10D, 50D, 100D);
        XRaySample s3 = new XRaySample(15D, 100D, 300D);
        dp = new DataProcessor();
        samples.add(s1);
        samples.add(s2);
        samples.add(s3);

        String basePath = System.getProperty("user.dir");
        basePath += FILE;
        basePath = formatForOS(basePath);
        //filePath = basePath;
        file = new File(basePath);

        dl = new FileLoader();
        dl.setIndeces(0, 1, 8);
        dfile = dl.loadFile(MeasurementType.I0, file);
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
package UnitTests;

import DataProcessing.Models.XRaySample;
import DataProcessing.Processors.DataProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DataProcessorTest {

    static DataProcessor dp;
    static ArrayList<XRaySample> samples = new ArrayList<>();

    @BeforeAll
    static void setup(){
        XRaySample s1 = new XRaySample(5D, 30D, 50D);
        XRaySample s2 = new XRaySample(10D, 50D, 100D);
        XRaySample s3 = new XRaySample(15D, 100D, 300D);
        dp = new DataProcessor();
        samples.add(s1);
        samples.add(s2);
        samples.add(s3);
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
    }
}
package Data.Models;

/**
 * Represents a single data point from a set
 * The detector on the instrument measures X-ray counts (Col 9) as a function of Theta; Energy is
 * computed from theta using a simple equation.
 */

public class XRaySample {

    private double energy; //Column 1
    private double theta; //Column 2
    private double cnts_per_live; //Column 9 ????
    private double absorption;
    private double icrCounts;
    private double ocrCounts;
    private double cnts_per_live_corr;

    //private double energyCorrected;
    //private double thetaCorrected;

    public XRaySample(String energy, String theta, String cnts_per_live) {
        this.energy = formatExponents(energy);
        this.theta = formatExponents(theta);
        this.cnts_per_live = formatExponents(cnts_per_live);
    }

    public XRaySample(String energy, String theta, String cnts_per_live, String icr, String ocr) {
        this.energy = formatExponents(energy);
        this.theta = formatExponents(theta);
        double rawCounts = formatExponents(cnts_per_live);
        double icrCounts = formatExponents(icr);
        double ocrCounts = formatExponents(ocr);
        double correctedCounts = deadtimeCorrectCounts(rawCounts, icrCounts, ocrCounts);
        this.cnts_per_live = correctedCounts; //TODO temporary measure, fix to be raw
        this.cnts_per_live_corr = correctedCounts;
    }

    /**
     * Corrects the measured counts_per_live for dead time (1st order correct counts = raw counts * icr counts / ocr counts)
     * @param rawCounts
     * @param icrCounts
     * @param ocrCounts
     * @return Corrected counts
     */
    private double deadtimeCorrectCounts(double rawCounts, double icrCounts, double ocrCounts) {
        return (rawCounts * icrCounts / ocrCounts);
    }

    public XRaySample(Double meanEnergy, Double meanTheta, Double meanCounts) {
        this.energy = meanEnergy;
        this.theta = meanTheta;
        this.cnts_per_live = meanCounts;
    }

    public XRaySample(Double energy, Double theta, Double counts, Double absorption) {
        this.energy = energy;
        this.theta = theta;
        this.cnts_per_live = counts;
        this.absorption = absorption;
    }

    /*
    public XRaySample(Double energy, Double theta, Double counts, Double absorption, Double energyCorrected, Double thetaCorrected) {
        this.energy = energy;
        this.theta = theta;
        this.cnts_per_live = counts;
        this.absorption = absorption;
        this.energyCorrected = energyCorrected;
        this.thetaCorrected = thetaCorrected;
    }

     */

    /**
     * Converts string representation of exponential number to a double
     * @param number - String in the form: number -> E -> +/- -> number, e.g. 2.612412E+5
     * @return
     */
    private double formatExponents(String number) {
        try {
            String result = number.replace("E", "e");
            double answer = Double.parseDouble(result);
            return answer;
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid data format");
            nfe.printStackTrace();
        }
        return 0;
    }

    public String getData() {
        return (this.energy + "," + this.theta + "," + this.cnts_per_live + "!");// "!" Divides samples
    }

    //TODO throw exception instead of returning 0
    public double getData(DataType type) {
        switch (type) {
            case ENERGY:
            case ENERGY_CORRECTED:
                return getEnergy();
            case THETA:
            case THETA_CORRECTED:
                return getTheta();
            case COUNTS_PER_LIVE: return getCnts_per_live();
            case ABSORPTION: return getAbsorption();
            default: return 0;
        }
    }
    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public double getCnts_per_live() {
        return cnts_per_live;
    }

    public void setCnts_per_live(double cnts_per_live) {
        this.cnts_per_live = cnts_per_live;
    }

    public double getAbsorption() {
        return absorption;
    }

    public void setAbsorption(double absorption) {
        this.absorption = absorption;
    }

    @Override
    public String toString() {
        return "XRaySample{" +
                "energy=" + energy +
                ", theta=" + theta +
                ", cnts_per_live=" + cnts_per_live +
                ", absorption=" + absorption +
                '}';
    }

    /*
    public void setCorrected(double energyCorrected, double thetaCorrected) {
        this.energyCorrected = energyCorrected;
        this.thetaCorrected = thetaCorrected;
    }

     */
}

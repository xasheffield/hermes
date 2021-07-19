package DataProcessing.Models;

/**
 * Represents a single data point from a set
 *
 * Energy = Column 1
 *  Theta = Column 2
 *  Cnts_per_live = Column 9 (= I 0 , I t , I 0b , I tb )
 * The detector on the instrument measures X-ray counts (Col 9) as a function of Theta; Energy is
 * computed from theta using a simple equation.
 */

public class XRaySample {

    private double energy; //Column 1
    private double theta; //Column 2
    private double cnts_per_live; //Column 9 ????
    private double absorption;

    public XRaySample(String energy, String theta, String cnts_per_live) {
        this.energy = formatExponents(energy);
        this.theta = formatExponents(theta);
        this.cnts_per_live = formatExponents(cnts_per_live);
    }

    public XRaySample(Double meanEnergy, Double meanTheta, Double meanCounts) {
        this.energy = meanEnergy;
        this.theta = meanTheta;
        this.cnts_per_live = meanCounts;
    }

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
}

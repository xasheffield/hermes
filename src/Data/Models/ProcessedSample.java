package Data.Models;

/**
 * An extension of XRaySample to include data which processed files require. This include absorption,
 * - Absorption
 * - I0 counts (from files the processed file is generated from.)
 * - IT counts
 * - I0b counts (if applicable)
 * - Itb counts
 */
public class ProcessedSample extends XRaySample {


    //Processed samples are generated from two or four files
    double i0; // Counts from i0 and it files
    double it;
    double i0b; // Counts from i0b and i0t files
    double itb;
    double i0corrected; // i0 - i0b
    double itcorrected; // it - itb
    double energyCorrected = -1;
    double thetaCorrected = -1;

    boolean hasBackground; // Files calculated with background will contain two extra columns (i0Corrected, itCorrected)

    public ProcessedSample(Double energy, Double theta, Double counts, Double absorption, double i0, double it) {
        super(energy, theta, counts);
        this.setAbsorption(absorption);
        this.i0 = i0;
        this.it = it;
        hasBackground = false;
    }

    public ProcessedSample(Double energy, Double theta, Double counts, Double absorption,
                           double i0, double it, double i0b, double itb) {
        super(energy, theta, counts);
        this.setAbsorption(absorption);
        this.i0 = i0;
        this.it = it;
        this.i0b = i0b;
        this.itb = itb;
        this.i0corrected = i0 - i0b;
        this.itcorrected = it - itb;
        hasBackground = true;
    }


    public double getData(DataType type) {
        switch (type) {
            case ENERGY: return getEnergy();
            case ENERGY_CORRECTED: return getEnergyCorrected();
            case THETA: return getTheta();
            case THETA_CORRECTED: return getThetaCorrected();
            case COUNTS_PER_LIVE: return getCnts_per_live();
            case ABSORPTION: return getAbsorption();
            case I0: return getI0();
            case IT: return getIt();
            case I0B: return getI0b();
            case ITB: return getItb();
            case I0CORRECTED: return getI0corrected();
            case ITCORRECTED: return getITcorrected();
            default: return 0;
        }
    }

    public double getI0() {
        return i0;
    }

    public double getIt() {
        return it;
    }

    public double getI0b() {
        return i0b;
    }

    public double getItb() {
        return itb;
    }

    public double getI0corrected() { return i0corrected; }

    public double getITcorrected() { return itcorrected; }

    public double getEnergyCorrected() {
        return energyCorrected;
    }

    public double getThetaCorrected() {
        return thetaCorrected;
    }

    public boolean hasBackground() {
        return hasBackground;
    }

    public void setCorrected(double energyCorrected, double thetaCorrected) {
        this.energyCorrected = energyCorrected;
        this.thetaCorrected = thetaCorrected;
    }

}

package Data.Models;

public class AbsorptionSample extends XRaySample {

    private Double i0counts;
    private Double itcounts;
    private Double i0bcounts;
    private Double itbcounts;

    public AbsorptionSample(String energy, String theta, String cnts_per_live) {
        super(energy, theta, cnts_per_live);
    }

    public AbsorptionSample(Double meanEnergy, Double meanTheta, Double meanCounts) {
        super(meanEnergy, meanTheta, meanCounts);
    }

    public AbsorptionSample(Double meanEnergy, Double meanTheta, Double meanCounts, Double absorption) {
        super(meanEnergy, meanTheta, meanCounts, absorption);
    }
}

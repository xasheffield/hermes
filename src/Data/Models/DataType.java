package Data.Models;

public enum DataType {
    ENERGY("Energy (eV)"),
    THETA("Theta (deg)"),
    COUNTS_PER_LIVE("Deadtime Corrected Counts"),//TODO actually correct the counts
    ABSORPTION("Absorption (mu.x)"),
    ENERGY_CORRECTED ("Energy Corrected (eV)"),
    THETA_CORRECTED ("Theta Corrected (deg)"),
    I0("i0"),
    IT("it"),
    I0B("i0lk"),
    ITB("itlk"),
    I0CORRECTED("i0 - i0lk"),
    ITCORRECTED("it - itlk");

    public final String label;

    DataType(String label) {
        this.label = label;
    }
}

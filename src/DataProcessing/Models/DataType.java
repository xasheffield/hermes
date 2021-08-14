package DataProcessing.Models;

public enum DataType {
    ENERGY("Energy (eV)"), THETA("Theta (deg)"), COUNTS_PER_LIVE("Counts per live"),
    ABSORPTION("Absorption (mu.x)"), ENERGY_CORRECTED ("Energy Corrected (eV)"),
    THETA_CORRECTED ("Theta Corrected (deg)");

    public final String label;

    DataType(String label) {
        this.label = label;
    }
}

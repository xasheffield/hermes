package DataProcessing.Models;

public enum DataType {
    ENERGY("Energy"), THETA("Theta"), COUNTS_PER_LIVE("Counts per live"),
    ABSORPTION("Absorption"), ENERGY_CORRECTED ("Energy (Corrected)"),
    THETA_CORRECTED ("Theta (Corrected)");

    public final String label;

    DataType(String label) {
        this.label = label;
    }
}

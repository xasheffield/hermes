package Data.Models;

public enum DataType {
    ENERGY("Energy (eV)"),
    THETA("Theta (deg)"),
    COUNTS_PER_LIVE("Counts per live"),
    ABSORPTION("Absorption (mu.x)"),
    ENERGY_CORRECTED ("Energy Corrected (eV)"),
    THETA_CORRECTED ("Theta Corrected (deg)"),
    I0("I0 Counts"),
    IT("It Counts"),
    I0CORRECTED("I0 - I0 leakage"),
    ITCORRECTED("It - It leakage");

    public final String label;

    DataType(String label) {
        this.label = label;
    }
}

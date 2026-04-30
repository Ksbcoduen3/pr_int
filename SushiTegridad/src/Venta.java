/**
 * Representa un registro de venta realizada.
 */
public class Venta {

    private String nombrePlatillo;
    private int    cantidad;
    private float  total;

    public Venta(String nombrePlatillo, int cantidad, float total) {
        this.nombrePlatillo = nombrePlatillo;
        this.cantidad       = cantidad;
        this.total          = total;
    }

    // ── Getters ───────────────────────────────────────────────
    public String getNombrePlatillo() { return nombrePlatillo; }
    public int    getCantidad()       { return cantidad; }
    public float  getTotal()          { return total; }

    @Override
    public String toString() {
        return String.format("%-30s x%-5d $%.2f", nombrePlatillo, cantidad, total);
    }
}

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Representa un ingrediente del almacén.
 * Incluye nombre, cantidad disponible y control de caducidad.
 */
public class Ingrediente {

    private String nombre;
    private float  cantidad;        // en kg o Lt
    private int    diasCaducidad;   // días de vida útil (0 = no caduca)
    private String fechaRegistro;   // formato yyyy-MM-dd

    /** Constructor completo. */
    public Ingrediente(String nombre, float cantidad, int diasCaducidad, String fechaRegistro) {
        this.nombre         = nombre;
        this.cantidad       = cantidad;
        this.diasCaducidad  = diasCaducidad;
        this.fechaRegistro  = fechaRegistro;
    }

    /** Constructor sin caducidad (se asigna 0 = no caduca, fecha = hoy). */
    public Ingrediente(String nombre, float cantidad) {
        this(nombre, cantidad, 0, LocalDate.now().toString());
    }

    // ── Caducidad ─────────────────────────────────────────────

    /** Días restantes antes de caducar. Retorna Long.MAX_VALUE si no caduca. */
    public long diasRestantes() {
        if (diasCaducidad == 0) return Long.MAX_VALUE;
        LocalDate registro  = LocalDate.parse(fechaRegistro);
        LocalDate caducidad = registro.plusDays(diasCaducidad);
        return ChronoUnit.DAYS.between(LocalDate.now(), caducidad);
    }

    /** ¿Ya caducó? */
    public boolean estaCaducado() {
        if (diasCaducidad == 0) return false;
        return diasRestantes() <= 0;
    }

    /** Texto descriptivo del estado de caducidad. */
    public String estadoCaducidad() {
        if (diasCaducidad == 0) return "No caduca";
        long restantes = diasRestantes();
        if (restantes <= 0) return "⚠ CADUCADO";
        if (restantes <= 2) return "⚠ Por caducar (" + restantes + "d)";
        return "OK (" + restantes + "d)";
    }

    // ── Getters y Setters ─────────────────────────────────────
    public String getNombre()                   { return nombre; }
    public void   setNombre(String n)           { this.nombre = n; }
    public float  getCantidad()                 { return cantidad; }
    public void   setCantidad(float c)          { this.cantidad = c; }
    public int    getDiasCaducidad()            { return diasCaducidad; }
    public void   setDiasCaducidad(int d)       { this.diasCaducidad = d; }
    public String getFechaRegistro()            { return fechaRegistro; }
    public void   setFechaRegistro(String f)    { this.fechaRegistro = f; }

    @Override
    public String toString() {
        return String.format("%-25s %8.3f kg/Lt  [%s]", nombre, cantidad, estadoCaducidad());
    }
}

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Representa un platillo del menú de sushi.
 * Contiene nombre, precio y su receta expresada como
 * un mapa: nombre_ingrediente → cantidad por unidad del platillo.
 */
public class Platillo {

    private String nombre;
    private float  precio;

    /**
     * Receta: clave = nombre del ingrediente,
     *         valor = cantidad requerida por 1 unidad del platillo (kg/Lt).
     * Se usa LinkedHashMap para mantener el orden de inserción.
     */
    private Map<String, Float> receta;

    public Platillo(String nombre, float precio) {
        this.nombre = nombre;
        this.precio = precio;
        this.receta = new LinkedHashMap<>();
    }

    // ── Manejo de receta ──────────────────────────────────────
    public void agregarIngredienteReceta(String nombreIng, float cantidadPorUnidad) {
        receta.put(nombreIng, cantidadPorUnidad);
    }

    public void eliminarIngredienteReceta(String nombreIng) {
        receta.remove(nombreIng);
    }

    public Map<String, Float> getReceta() { return receta; }

    // ── Getters y Setters ──────────────────────────────────────
    public String getNombre()            { return nombre; }
    public void   setNombre(String n)    { this.nombre = n; }
    public float  getPrecio()            { return precio; }
    public void   setPrecio(float p)     { this.precio = p; }

    @Override
    public String toString() {
        return String.format("%-30s $%.2f", nombre, precio);
    }
}

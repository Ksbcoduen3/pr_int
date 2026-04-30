/**
 * Representa a un empleado del restaurante de sushi.
 * El sueldo semanal se calcula dinámicamente a partir de
 * sueldo diario × días trabajados por semana.
 */
public class Empleado {

    private String nombre;
    private String puesto;
    private float  sueldoDiario;
    private int    diasSemana;

    public Empleado(String nombre, String puesto, float sueldoDiario, int diasSemana) {
        this.nombre       = nombre;
        this.puesto       = puesto;
        this.sueldoDiario = sueldoDiario;
        this.diasSemana   = diasSemana;
    }

    /** Calcula el sueldo semanal en tiempo real. */
    public float getSueldoSemanal() {
        return sueldoDiario * diasSemana;
    }

    // ── Getters y Setters ──────────────────────────────────────
    public String getNombre()               { return nombre; }
    public void   setNombre(String n)       { this.nombre = n; }
    public String getPuesto()               { return puesto; }
    public void   setPuesto(String p)       { this.puesto = p; }
    public float  getSueldoDiario()         { return sueldoDiario; }
    public void   setSueldoDiario(float s)  { this.sueldoDiario = s; }
    public int    getDiasSemana()           { return diasSemana; }
    public void   setDiasSemana(int d)      { this.diasSemana = d; }

    @Override
    public String toString() {
        return String.format("%-15s %-20s $%-12.2f %-10d $%.2f",
                nombre, puesto, sueldoDiario, diasSemana, getSueldoSemanal());
    }
}

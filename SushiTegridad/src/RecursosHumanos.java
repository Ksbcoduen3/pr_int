import java.util.ArrayList;
import java.util.Scanner;

/**
 * Módulo de Recursos Humanos.
 * Gestiona el alta, baja, modificación y búsqueda de empleados.
 */
public class RecursosHumanos {

    private ArrayList<Empleado> empleados;

    public RecursosHumanos() {
        this.empleados = new ArrayList<>();
    }

    // ════════════════════════════════════════════════════════════
    //  MENÚ PRINCIPAL DEL MÓDULO
    // ════════════════════════════════════════════════════════════
    public void mostrarMenu(Scanner sc) {
        int opcion;
        do {
            Util.limpiarConsola();
            System.out.println("=====================================");
            System.out.println("  RECURSOS HUMANOS - SUSHI TEGRIDAD");
            System.out.println("=====================================");
            System.out.println("1. Ver empleados");
            System.out.println("2. Agregar empleado");
            System.out.println("3. Dar de baja");
            System.out.println("4. Modificar empleado");
            System.out.println("5. Buscar empleado");
            System.out.println("6. Regresar");
            System.out.print("Seleccione una opcion: ");
            opcion = Util.leerInt(sc);

            switch (opcion) {
                case 1: verEmpleados(sc);      break;
                case 2: agregarEmpleado(sc);   break;
                case 3: darDeBaja(sc);         break;
                case 4: modificarEmpleado(sc); break;
                case 5: buscarEmpleado(sc);    break;
                case 6:                        break;
                default:
                    System.out.println("Opcion invalida.");
                    Util.pausa(sc);
                    break;
            }
        } while (opcion != 6);
    }

    // ════════════════════════════════════════════════════════════
    //  OPERACIONES CRUD
    // ════════════════════════════════════════════════════════════

    /** Muestra la tabla completa de empleados. */
    private void verEmpleados(Scanner sc) {
        Util.limpiarConsola();
        System.out.println("--- LISTA DE EMPLEADOS ---");
        System.out.printf("%-5s %-15s %-20s %-12s %-10s %-12s%n",
                "ID", "NOMBRE", "PUESTO", "SUELDO/DÍA", "DÍAS/SEM", "SUELDO/SEM");
        Util.separador(78);
        for (int i = 0; i < empleados.size(); i++) {
            Empleado e = empleados.get(i);
            System.out.printf("%-5d %-15s %-20s $%-11.2f %-10d $%.2f%n",
                    i + 1, e.getNombre(), e.getPuesto(),
                    e.getSueldoDiario(), e.getDiasSemana(), e.getSueldoSemanal());
        }
        if (empleados.isEmpty()) System.out.println("  (sin empleados registrados)");
        Util.pausa(sc);
    }

    /** Agrega un nuevo empleado. */
    private void agregarEmpleado(Scanner sc) {
        System.out.print("Nombre (sin espacios): ");
        String nombre = Util.leerString(sc);
        System.out.print("Puesto (sin espacios): ");
        String puesto = Util.leerString(sc);
        System.out.print("Sueldo diario ($): ");
        float sueldoDiario = Util.leerFloat(sc);
        System.out.print("Dias trabajados por semana: ");
        int diasSemana = Util.leerInt(sc);

        empleados.add(new Empleado(nombre, puesto, sueldoDiario, diasSemana));
        System.out.printf("  ✔ Empleado '%s' agregado. Sueldo semanal: $%.2f%n",
                nombre, sueldoDiario * diasSemana);
        Util.pausa(sc);
    }

    /** Da de baja (elimina) a un empleado por número de lista. */
    private void darDeBaja(Scanner sc) {
        verListaCompacta();
        System.out.print("Numero de empleado a dar de baja: ");
        int pos = Util.leerInt(sc) - 1;

        if (pos >= 0 && pos < empleados.size()) {
            String nombre = empleados.get(pos).getNombre();
            empleados.remove(pos);
            System.out.printf("  ✔ Empleado '%s' dado de baja.%n", nombre);
        } else {
            System.out.println("Numero invalido.");
        }
        Util.pausa(sc);
    }

    /** Modifica los datos de un empleado existente. */
    private void modificarEmpleado(Scanner sc) {
        verListaCompacta();
        System.out.print("Numero de empleado a modificar: ");
        int pos = Util.leerInt(sc) - 1;

        if (pos >= 0 && pos < empleados.size()) {
            Empleado e = empleados.get(pos);
            System.out.print("Nombre: ");        e.setNombre(Util.leerString(sc));
            System.out.print("Puesto: ");        e.setPuesto(Util.leerString(sc));
            System.out.print("Sueldo diario: "); e.setSueldoDiario(Util.leerFloat(sc));
            System.out.print("Dias/semana: ");   e.setDiasSemana(Util.leerInt(sc));
            System.out.printf("  ✔ Datos de '%s' actualizados.%n", e.getNombre());
        } else {
            System.out.println("Numero invalido.");
        }
        Util.pausa(sc);
    }

    /** Busca un empleado por nombre exacto e imprime su ficha. */
    private void buscarEmpleado(Scanner sc) {
        System.out.print("Nombre del empleado a buscar: ");
        String buscar = Util.leerString(sc);
        boolean encontrado = false;
        for (Empleado e : empleados) {
            if (e.getNombre().equalsIgnoreCase(buscar)) {
                System.out.printf("%n  Nombre:        %s%n", e.getNombre());
                System.out.printf("  Puesto:        %s%n", e.getPuesto());
                System.out.printf("  Sueldo diario: $%.2f%n", e.getSueldoDiario());
                System.out.printf("  Dias/semana:   %d%n", e.getDiasSemana());
                System.out.printf("  Sueldo semanal:$%.2f%n", e.getSueldoSemanal());
                encontrado = true;
                break;
            }
        }
        if (!encontrado) System.out.println("  Empleado no encontrado.");
        Util.pausa(sc);
    }

    // ════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════

    /** Lista compacta sin pausa (para selección interna). */
    private void verListaCompacta() {
        System.out.printf("%-5s %-15s %-20s%n", "ID", "NOMBRE", "PUESTO");
        Util.separador(42);
        for (int i = 0; i < empleados.size(); i++) {
            Empleado e = empleados.get(i);
            System.out.printf("%-5d %-15s %-20s%n", i + 1, e.getNombre(), e.getPuesto());
        }
    }

    public ArrayList<Empleado> getEmpleados() { return empleados; }

    // ════════════════════════════════════════════════════════════
    //  DATOS POR DEFECTO
    // ════════════════════════════════════════════════════════════
    /** Carga tres empleados iniciales del restaurante. */
    public void inicializarDefecto() {
        empleados.add(new Empleado("Kenji",   "Chef_Sushi",     350f, 5));
        empleados.add(new Empleado("Maria",   "Mesera",         180f, 6));
        empleados.add(new Empleado("Roberto", "Administrativo", 280f, 5));
    }
}

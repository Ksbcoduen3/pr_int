import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Módulo de Almacén.
 * Gestiona la lista de ingredientes disponibles en el restaurante.
 * Incluye control de caducidad para ingredientes perecederos.
 */
public class Almacen {

    private ArrayList<Ingrediente> ingredientes;
    private static final int MAX_ING = 100;

    public Almacen() {
        this.ingredientes = new ArrayList<>();
    }

    // ════════════════════════════════════════════════════════════
    //  MENÚ PRINCIPAL DEL MÓDULO
    // ════════════════════════════════════════════════════════════
    public void mostrarMenu(Scanner sc) {
        int opcion;
        do {
            Util.limpiarConsola();
            System.out.println("=====================================");
            System.out.println("     ALMACÉN - SUSHI TEGRIDAD");
            System.out.println("=====================================");
            System.out.println("1. Ver inventario");
            System.out.println("2. Agregar ingrediente");
            System.out.println("3. Modificar cantidad");
            System.out.println("4. Eliminar ingrediente");
            System.out.println("5. Revisar caducidad");
            System.out.println("6. Regresar");
            System.out.print("Seleccione una opcion: ");
            opcion = Util.leerInt(sc);

            switch (opcion) {
                case 1: verInventario(sc);          break;
                case 2: agregarIngrediente(sc);     break;
                case 3: modificarCantidad(sc);      break;
                case 4: eliminarIngrediente(sc);    break;
                case 5: revisarCaducidad(sc);       break;
                case 6:                             break;
                default:
                    System.out.println("Opcion invalida.");
                    Util.pausa(sc);
                    break;
            }
        } while (opcion != 6);
    }

    // ════════════════════════════════════════════════════════════
    //  OPERACIONES
    // ════════════════════════════════════════════════════════════

    /** Muestra la tabla completa de ingredientes con estado de caducidad. */
    private void verInventario(Scanner sc) {
        Util.limpiarConsola();
        System.out.println("--- INVENTARIO DE INGREDIENTES ---");
        System.out.printf("%-4s %-22s %-12s %-6s %-12s %-18s%n",
                "ID", "NOMBRE", "CANT(Kg/Lt)", "CADUC", "REGISTRO", "ESTADO");
        Util.separador(78);
        for (int i = 0; i < ingredientes.size(); i++) {
            Ingrediente ing = ingredientes.get(i);
            System.out.printf("%-4d %-22s %10.3f %5dd  %-12s %-18s%n",
                    i + 1,
                    ing.getNombre(),
                    ing.getCantidad(),
                    ing.getDiasCaducidad(),
                    ing.getFechaRegistro(),
                    ing.estadoCaducidad());
        }
        if (ingredientes.isEmpty()) System.out.println("  (sin ingredientes registrados)");
        Util.pausa(sc);
    }

    /** Agrega un solo ingrediente pidiendo nombre, cantidad y días de caducidad. */
    private void agregarIngrediente(Scanner sc) {
        if (ingredientes.size() >= MAX_ING) {
            System.out.println("Capacidad maxima de ingredientes alcanzada.");
            Util.pausa(sc);
            return;
        }
        Util.limpiarConsola();
        System.out.println("=== Agregar ingrediente ===");
        System.out.print("Nombre (sin espacios): ");
        String nombre = Util.leerString(sc);
        System.out.print("Cantidad (Kg/Lt): ");
        float cant = Util.leerFloat(sc);
        System.out.print("Dias de caducidad (0 = no caduca): ");
        int dias = Util.leerInt(sc);
        if (dias < 0) dias = 0;

        ingredientes.add(new Ingrediente(nombre, cant, dias, LocalDate.now().toString()));
        System.out.println("  ✔ Ingrediente agregado.");
        Util.pausa(sc);
    }

    /** Modifica la cantidad disponible de un ingrediente. */
    private void modificarCantidad(Scanner sc) {
        Util.limpiarConsola();
        verListaCompacta();
        System.out.print("Numero de ingrediente a modificar: ");
        int pos = Util.leerInt(sc) - 1;

        if (pos >= 0 && pos < ingredientes.size()) {
            Ingrediente ing = ingredientes.get(pos);
            System.out.printf("Ingrediente: %s  |  Cantidad actual: %.3f%n",
                    ing.getNombre(), ing.getCantidad());
            System.out.print("Nueva cantidad: ");
            float nueva = Util.leerFloat(sc);
            ing.setCantidad(nueva);
            System.out.println("  ✔ Cantidad actualizada.");
        } else {
            System.out.println("Numero invalido.");
        }
        Util.pausa(sc);
    }

    /** Elimina un ingrediente por posición. */
    private void eliminarIngrediente(Scanner sc) {
        Util.limpiarConsola();
        verListaCompacta();
        System.out.print("Numero de ingrediente a eliminar: ");
        int pos = Util.leerInt(sc) - 1;

        if (pos >= 0 && pos < ingredientes.size()) {
            String nombre = ingredientes.get(pos).getNombre();
            ingredientes.remove(pos);
            System.out.printf("  ✔ Ingrediente '%s' eliminado.%n", nombre);
            System.out.println("  AVISO: Verifique que las recetas no usen este ingrediente.");
        } else {
            System.out.println("Numero invalido.");
        }
        Util.pausa(sc);
    }

    /** Muestra ingredientes caducados o próximos a caducar. */
    private void revisarCaducidad(Scanner sc) {
        Util.limpiarConsola();
        System.out.println("=== REVISIÓN DE CADUCIDAD ===");
        System.out.printf("%-4s %-22s %-12s %-6s %-12s %-18s%n",
                "ID", "NOMBRE", "CANT(Kg/Lt)", "CADUC", "REGISTRO", "ESTADO");
        Util.separador(78);

        boolean hayAlerta = false;
        for (int i = 0; i < ingredientes.size(); i++) {
            Ingrediente ing = ingredientes.get(i);
            if (ing.getDiasCaducidad() == 0) continue; // ignorar los que no caducan
            long restantes = ing.diasRestantes();
            if (restantes <= 3) { // mostrar si le quedan 3 días o menos
                hayAlerta = true;
                System.out.printf("%-4d %-22s %10.3f %5dd  %-12s %-18s%n",
                        i + 1,
                        ing.getNombre(),
                        ing.getCantidad(),
                        ing.getDiasCaducidad(),
                        ing.getFechaRegistro(),
                        ing.estadoCaducidad());
            }
        }
        if (!hayAlerta) {
            System.out.println("\n  ✔ No hay ingredientes caducados ni por caducar.");
        } else {
            System.out.println("\n  ⚠ Los ingredientes listados requieren atencion.");
        }
        Util.pausa(sc);
    }

    // ════════════════════════════════════════════════════════════
    //  MÉTODOS DE APOYO (usados por otros módulos)
    // ════════════════════════════════════════════════════════════

    /** Busca un ingrediente por nombre (ignorando mayúsculas). */
    public Ingrediente buscarPorNombre(String nombre) {
        for (Ingrediente ing : ingredientes) {
            if (ing.getNombre().equalsIgnoreCase(nombre)) return ing;
        }
        return null;
    }

    /** Imprime lista compacta sin pausa (para uso interno de menús). */
    public void verListaCompacta() {
        System.out.printf("%-4s %-22s %-12s%n", "ID", "NOMBRE", "CANT(Kg/Lt)");
        Util.separador(40);
        for (int i = 0; i < ingredientes.size(); i++) {
            Ingrediente ing = ingredientes.get(i);
            System.out.printf("%-4d %-22s %10.3f%n", i + 1, ing.getNombre(), ing.getCantidad());
        }
    }

    public ArrayList<Ingrediente> getIngredientes() { return ingredientes; }

    // ════════════════════════════════════════════════════════════
    //  DATOS POR DEFECTO
    // ════════════════════════════════════════════════════════════

    /** Carga un inventario inicial de ingredientes de sushi con caducidad realista. */
    public void inicializarDefecto() {
        String hoy = LocalDate.now().toString();
        // nombre, cantidad, días caducidad
        Object[][] datos = {
            {"Arroz_sushi",       20f,  2},   // arroz preparado
            {"Vinagre_de_arroz",   5f,  0},   // larga duración
            {"Salmon",            10f,  3},   // proteína fresca
            {"Atun",              10f,  3},
            {"Pepino",             5f,  5},   // vegetal fresco
            {"Aguacate",           8f,  5},
            {"Alga_nori",         10f,  0},   // seco, larga duración
            {"Queso_crema",        5f, 14},   // refrigerado
            {"Camaron",            8f,  3},   // proteína fresca
            {"Salsa_soya",         5f,  0},   // larga duración
            {"Wasabi",             2f, 30},   // conservado
            {"Jengibre",           2f, 30},
            {"Maiz",               3f,  0},   // enlatado/seco
            {"Zanahoria",          5f,  5},   // vegetal fresco
            {"Surimi",             5f,  5},
            {"Mayo",               3f, 14},   // refrigerado
            {"Sesamo",             2f,  0},   // seco
            {"Tobiko",             1f,  5},
            {"Mango",              5f,  5},   // fruta fresca
            {"Tempura",            3f,  0},   // mezcla seca
        };
        for (Object[] d : datos) {
            ingredientes.add(new Ingrediente(
                    (String) d[0], (float) d[1], (int) d[2], hoy));
        }
    }
}

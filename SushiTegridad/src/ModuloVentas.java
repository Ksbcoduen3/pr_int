import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/**
 * Módulo de Ventas.
 * Administra el catálogo de platillos (con sus recetas) y el historial de ventas.
 * Al registrar una venta, descuenta automáticamente los ingredientes del Almacén.
 */
public class ModuloVentas {

    private ArrayList<Platillo> platillos;
    private ArrayList<Venta>    ventas;
    private Almacen             almacen; // referencia para descontar ingredientes

    private static final int MAX_PLATILLOS = 50;

    public ModuloVentas(Almacen almacen) {
        this.almacen   = almacen;
        this.platillos = new ArrayList<>();
        this.ventas    = new ArrayList<>();
    }

    // ════════════════════════════════════════════════════════════
    //  MENÚ PRINCIPAL DEL MÓDULO
    // ════════════════════════════════════════════════════════════
    public void mostrarMenu(Scanner sc) {
        int opcion;
        do {
            Util.limpiarConsola();
            System.out.println("=====================================");
            System.out.println("   MÓDULO VENTAS - SUSHI TEGRIDAD");
            System.out.println("=====================================");
            System.out.println("1. Generar venta");
            System.out.println("2. Eliminar venta");
            System.out.println("3. Informe de ventas");
            System.out.println("4. Ver carta de platillos");
            System.out.println("5. Registrar nuevo platillo (receta)");
            System.out.println("6. Regresar");
            System.out.print("Seleccione una opcion: ");
            opcion = Util.leerInt(sc);

            switch (opcion) {
                case 1: generarVenta(sc);          break;
                case 2: eliminarVenta(sc);         break;
                case 3: informeVentas(sc);         break;
                case 4: verCarta(sc);              break;
                case 5: registrarPlatillo(sc);     break;
                case 6:                            break;
                default:
                    System.out.println("Opcion invalida.");
                    Util.pausa(sc);
                    break;
            }
        } while (opcion != 6);
    }

    // ════════════════════════════════════════════════════════════
    //  OPERACIONES DE VENTA
    // ════════════════════════════════════════════════════════════

    /** Genera una nueva venta: verifica stock, descuenta ingredientes y registra. */
    private void generarVenta(Scanner sc) {
        Util.limpiarConsola();
        if (platillos.isEmpty()) {
            System.out.println("No hay platillos en el menú.");
            Util.pausa(sc);
            return;
        }

        imprimirMenuPlatillos();
        System.out.print("\nSeleccione platillo (1-" + platillos.size() + "): ");
        int sel = Util.leerInt(sc) - 1;

        if (sel < 0 || sel >= platillos.size()) {
            System.out.println("Opcion invalida.");
            Util.pausa(sc);
            return;
        }

        System.out.print("Cantidad a vender: ");
        int cantPedido = Util.leerInt(sc);
        if (cantPedido <= 0) {
            System.out.println("Cantidad invalida.");
            Util.pausa(sc);
            return;
        }

        Platillo platillo = platillos.get(sel);

        // ── Verificar stock suficiente ──────────────────────────
        boolean suficiente = true;
        for (Map.Entry<String, Float> entry : platillo.getReceta().entrySet()) {
            Ingrediente ing = almacen.buscarPorNombre(entry.getKey());
            float requerido = entry.getValue() * cantPedido;
            if (ing == null) {
                System.out.printf("  [AVISO] Ingrediente '%s' no encontrado en almacen.%n", entry.getKey());
                suficiente = false;
            } else if (ing.getCantidad() < requerido) {
                System.out.printf("  [FALTA] %s: necesita %.3f, disponible %.3f%n",
                        ing.getNombre(), requerido, ing.getCantidad());
                suficiente = false;
            }
        }

        if (!suficiente) {
            System.out.println("\nNo es posible realizar la venta por falta de ingredientes.");
            Util.pausa(sc);
            return;
        }

        // ── Descontar ingredientes y registrar venta ────────────
        for (Map.Entry<String, Float> entry : platillo.getReceta().entrySet()) {
            Ingrediente ing = almacen.buscarPorNombre(entry.getKey());
            if (ing != null) {
                ing.setCantidad(ing.getCantidad() - entry.getValue() * cantPedido);
            }
        }

        float total = platillo.getPrecio() * cantPedido;
        ventas.add(new Venta(platillo.getNombre(), cantPedido, total));
        System.out.printf("%n  ✔ Venta registrada: %s x%d  →  Total: $%.2f%n",
                platillo.getNombre(), cantPedido, total);
        Util.pausa(sc);
    }

    /** Muestra las ventas y permite eliminar una por número. */
    private void eliminarVenta(Scanner sc) {
        Util.limpiarConsola();
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas.");
            Util.pausa(sc);
            return;
        }
        imprimirListaVentas();
        System.out.print("\nNumero de venta a eliminar: ");
        int pos = Util.leerInt(sc) - 1;

        if (pos >= 0 && pos < ventas.size()) {
            ventas.remove(pos);
            System.out.println("  ✔ Venta eliminada. (Stock no revertido)");
        } else {
            System.out.println("Numero invalido.");
        }
        Util.pausa(sc);
    }

    /** Muestra el informe total de ventas e ingresos. */
    private void informeVentas(Scanner sc) {
        Util.limpiarConsola();
        System.out.println("--- INFORME DE VENTAS ---");
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas.");
        } else {
            System.out.printf("%-5s %-30s %-8s %-10s%n", "No.", "PLATILLO", "CANT.", "TOTAL");
            Util.separador(55);
            float totalIngresos = 0;
            for (int i = 0; i < ventas.size(); i++) {
                Venta v = ventas.get(i);
                System.out.printf("%-5d %-30s %-8d $%.2f%n",
                        i + 1, v.getNombrePlatillo(), v.getCantidad(), v.getTotal());
                totalIngresos += v.getTotal();
            }
            Util.separador(55);
            System.out.printf("TOTAL INGRESOS: $%.2f%n", totalIngresos);
        }
        Util.pausa(sc);
    }

    /** Muestra la carta completa con ingredientes de cada receta. */
    private void verCarta(Scanner sc) {
        Util.limpiarConsola();
        System.out.println("════════ CARTA DE SUSHI TEGRIDAD ════════");
        if (platillos.isEmpty()) {
            System.out.println("  (sin platillos registrados)");
        }
        for (int i = 0; i < platillos.size(); i++) {
            Platillo p = platillos.get(i);
            System.out.printf("%n%d. %-30s $%.2f%n", i + 1, p.getNombre(), p.getPrecio());
            System.out.println("   Ingredientes por unidad:");
            if (p.getReceta().isEmpty()) {
                System.out.println("     (sin receta definida)");
            } else {
                for (Map.Entry<String, Float> e : p.getReceta().entrySet()) {
                    System.out.printf("     • %-25s %.3f kg/Lt%n", e.getKey(), e.getValue());
                }
            }
        }
        Util.pausa(sc);
    }

    /** Registra un nuevo platillo con su receta. */
    private void registrarPlatillo(Scanner sc) {
        if (platillos.size() >= MAX_PLATILLOS) {
            System.out.println("Capacidad maxima de platillos alcanzada.");
            Util.pausa(sc);
            return;
        }
        Util.limpiarConsola();
        System.out.println("=== Registrar nuevo platillo ===");
        System.out.print("Nombre del platillo (sin espacios): ");
        String nombre = Util.leerString(sc);
        System.out.print("Precio: $");
        float precio = Util.leerFloat(sc);

        Platillo nuevo = new Platillo(nombre, precio);

        System.out.print("¿Cuantos ingredientes usa este platillo? ");
        int numIng = Util.leerInt(sc);

        for (int n = 0; n < numIng; n++) {
            System.out.println("\n--- Ingredientes disponibles en Almacén ---");
            almacen.verListaCompacta();
            System.out.print("Seleccione numero de ingrediente: ");
            int idx = Util.leerInt(sc) - 1;

            if (idx < 0 || idx >= almacen.getIngredientes().size()) {
                System.out.println("  Indice invalido. Se omitira este ingrediente.");
                continue;
            }
            System.out.print("Cantidad por unidad del platillo (kg/Lt): ");
            float qty = Util.leerFloat(sc);
            String nombreIng = almacen.getIngredientes().get(idx).getNombre();
            nuevo.agregarIngredienteReceta(nombreIng, qty);
            System.out.printf("  ✔ '%s' agregado a la receta.%n", nombreIng);
        }

        platillos.add(nuevo);
        System.out.println("\n  ✔ Platillo registrado correctamente.");
        Util.pausa(sc);
    }

    // ════════════════════════════════════════════════════════════
    //  HELPERS DE IMPRESIÓN
    // ════════════════════════════════════════════════════════════

    private void imprimirMenuPlatillos() {
        System.out.println("--- MENÚ DE PLATILLOS ---");
        for (int i = 0; i < platillos.size(); i++) {
            System.out.printf("%2d. %-30s $%.2f%n",
                    i + 1, platillos.get(i).getNombre(), platillos.get(i).getPrecio());
        }
    }

    private void imprimirListaVentas() {
        System.out.println("--- VENTAS REGISTRADAS ---");
        for (int i = 0; i < ventas.size(); i++) {
            Venta v = ventas.get(i);
            System.out.printf("%2d. %-30s x%-5d $%.2f%n",
                    i + 1, v.getNombrePlatillo(), v.getCantidad(), v.getTotal());
        }
    }

    // ════════════════════════════════════════════════════════════
    //  GETTERS (para GestorArchivos y Main)
    // ════════════════════════════════════════════════════════════
    public ArrayList<Platillo> getPlatillos() { return platillos; }
    public ArrayList<Venta>    getVentas()    { return ventas; }

    // ════════════════════════════════════════════════════════════
    //  DATOS POR DEFECTO
    // ════════════════════════════════════════════════════════════
    /** Carga el menú inicial de sushi con 10 platillos y sus recetas. */
    public void inicializarDefecto(Almacen almacen) {
        // Nigiri Salmon
        Platillo p1 = new Platillo("Nigiri_Salmon", 65f);
        p1.agregarIngredienteReceta("Arroz_sushi",       0.08f);
        p1.agregarIngredienteReceta("Salmon",             0.05f);
        p1.agregarIngredienteReceta("Vinagre_de_arroz",  0.01f);
        p1.agregarIngredienteReceta("Sesamo",             0.005f);
        platillos.add(p1);

        // Nigiri Atun
        Platillo p2 = new Platillo("Nigiri_Atun", 70f);
        p2.agregarIngredienteReceta("Arroz_sushi",       0.08f);
        p2.agregarIngredienteReceta("Atun",              0.05f);
        p2.agregarIngredienteReceta("Vinagre_de_arroz",  0.01f);
        p2.agregarIngredienteReceta("Sesamo",             0.005f);
        platillos.add(p2);

        // Maki Pepino
        Platillo p3 = new Platillo("Maki_Pepino", 55f);
        p3.agregarIngredienteReceta("Arroz_sushi",       0.10f);
        p3.agregarIngredienteReceta("Alga_nori",          0.05f);
        p3.agregarIngredienteReceta("Pepino",             0.04f);
        p3.agregarIngredienteReceta("Vinagre_de_arroz",  0.01f);
        platillos.add(p3);

        // California Roll
        Platillo p4 = new Platillo("California_Roll", 85f);
        p4.agregarIngredienteReceta("Arroz_sushi",       0.12f);
        p4.agregarIngredienteReceta("Alga_nori",          0.05f);
        p4.agregarIngredienteReceta("Aguacate",           0.04f);
        p4.agregarIngredienteReceta("Surimi",             0.05f);
        p4.agregarIngredienteReceta("Maiz",               0.02f);
        p4.agregarIngredienteReceta("Mayo",               0.02f);
        p4.agregarIngredienteReceta("Sesamo",             0.005f);
        platillos.add(p4);

        // Spicy Tuna Roll
        Platillo p5 = new Platillo("Spicy_Tuna_Roll", 90f);
        p5.agregarIngredienteReceta("Arroz_sushi",       0.12f);
        p5.agregarIngredienteReceta("Alga_nori",          0.05f);
        p5.agregarIngredienteReceta("Atun",              0.06f);
        p5.agregarIngredienteReceta("Pepino",             0.02f);
        p5.agregarIngredienteReceta("Mayo",               0.02f);
        p5.agregarIngredienteReceta("Wasabi",             0.005f);
        platillos.add(p5);

        // Dragon Roll
        Platillo p6 = new Platillo("Dragon_Roll", 110f);
        p6.agregarIngredienteReceta("Arroz_sushi",       0.14f);
        p6.agregarIngredienteReceta("Alga_nori",          0.05f);
        p6.agregarIngredienteReceta("Camaron",            0.06f);
        p6.agregarIngredienteReceta("Aguacate",           0.05f);
        p6.agregarIngredienteReceta("Tempura",            0.03f);
        p6.agregarIngredienteReceta("Tobiko",             0.01f);
        p6.agregarIngredienteReceta("Mayo",               0.02f);
        platillos.add(p6);

        // Philadelphia Roll
        Platillo p7 = new Platillo("Philadelphia_Roll", 95f);
        p7.agregarIngredienteReceta("Arroz_sushi",       0.12f);
        p7.agregarIngredienteReceta("Alga_nori",          0.05f);
        p7.agregarIngredienteReceta("Salmon",             0.06f);
        p7.agregarIngredienteReceta("Queso_crema",        0.04f);
        p7.agregarIngredienteReceta("Pepino",             0.02f);
        platillos.add(p7);

        // Temaki Camaron
        Platillo p8 = new Platillo("Temaki_Camaron", 80f);
        p8.agregarIngredienteReceta("Arroz_sushi",       0.10f);
        p8.agregarIngredienteReceta("Alga_nori",          0.06f);
        p8.agregarIngredienteReceta("Camaron",            0.07f);
        p8.agregarIngredienteReceta("Aguacate",           0.03f);
        p8.agregarIngredienteReceta("Mayo",               0.02f);
        platillos.add(p8);

        // Sashimi Mix
        Platillo p9 = new Platillo("Sashimi_Mix", 120f);
        p9.agregarIngredienteReceta("Salmon",             0.08f);
        p9.agregarIngredienteReceta("Atun",              0.08f);
        p9.agregarIngredienteReceta("Camaron",            0.05f);
        p9.agregarIngredienteReceta("Salsa_soya",         0.02f);
        p9.agregarIngredienteReceta("Wasabi",             0.005f);
        p9.agregarIngredienteReceta("Jengibre",           0.005f);
        platillos.add(p9);

        // Uramaki Tropical
        Platillo p10 = new Platillo("Uramaki_Tropical", 100f);
        p10.agregarIngredienteReceta("Arroz_sushi",       0.12f);
        p10.agregarIngredienteReceta("Alga_nori",          0.05f);
        p10.agregarIngredienteReceta("Camaron",            0.05f);
        p10.agregarIngredienteReceta("Mango",              0.04f);
        p10.agregarIngredienteReceta("Aguacate",           0.03f);
        p10.agregarIngredienteReceta("Tobiko",             0.01f);
        p10.agregarIngredienteReceta("Sesamo",             0.005f);
        platillos.add(p10);
    }
}

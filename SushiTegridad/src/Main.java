import java.util.Scanner;

/**
 * ╔══════════════════════════════════════════════╗
 *   SUSHI TEGRIDAD — Sistema de Gestión
 *   Punto de entrada principal de la aplicación.
 * ╚══════════════════════════════════════════════╝
 *
 * Responsabilidades de esta clase:
 *  • Inicializar los módulos y cargar datos persistidos.
 *  • Gestionar el flujo de login.
 *  • Orquestar el menú principal y delegar a cada módulo.
 *  • Guardar datos al salir.
 */
public class Main {

    // Único usuario del sistema (no se persiste, sólo la contraseña).
    private static final String USUARIO_CORRECTO = "admin";

    // Contraseña mutable: se usa un arreglo de un elemento para poder
    // modificarla desde métodos sin retornar el valor.
    private static String[] config = {"1234"};

    private static Scanner sc = new Scanner(System.in);

    // ════════════════════════════════════════════════════════════
    //  PUNTO DE ENTRADA
    // ════════════════════════════════════════════════════════════
    public static void main(String[] args) {

        // ── 1. Crear módulos ─────────────────────────────────────
        Almacen         almacen = new Almacen();
        ModuloVentas    ventas  = new ModuloVentas(almacen);
        RecursosHumanos rrhh    = new RecursosHumanos();

        // ── 2. Cargar datos persistidos ──────────────────────────
        String contrasenaGuardada = GestorArchivos.cargarDatos(almacen, ventas, rrhh);
        if (contrasenaGuardada != null) {
            config[0] = contrasenaGuardada;
        }

        // ── 3. Inicializar con datos de fábrica si es necesario ──
        if (almacen.getIngredientes().isEmpty()) {
            System.out.println("[INFO] Sin ingredientes. Cargando inventario inicial de sushi.");
            almacen.inicializarDefecto();
        }
        if (rrhh.getEmpleados().isEmpty()) {
            System.out.println("[INFO] Sin empleados. Cargando plantilla inicial.");
            rrhh.inicializarDefecto();
        }
        if (ventas.getPlatillos().isEmpty()) {
            System.out.println("[INFO] Sin platillos. Cargando carta inicial de sushi.");
            ventas.inicializarDefecto(almacen);
        }

        Util.pausa(sc);

        // ── 4. Login ─────────────────────────────────────────────
        if (!login()) {
            sc.close();
            return;
        }

        // ── 5. Menú principal ────────────────────────────────────
        menuPrincipal(almacen, ventas, rrhh);

        sc.close();
    }

    // ════════════════════════════════════════════════════════════
    //  LOGIN
    // ════════════════════════════════════════════════════════════
    /**
     * Muestra la pantalla de login.
     * @return {@code true} si el acceso fue concedido, {@code false} si el usuario eligió salir.
     */
    private static boolean login() {
        int opcion;
        do {
            Util.limpiarConsola();
            System.out.println("╔═══════════════════════════════════╗");
            System.out.println("║         🍣 SUSHI TEGRIDAD          ║");
            System.out.println("╚═══════════════════════════════════╝");
            System.out.println("1. Iniciar sesion");
            System.out.println("2. Salir");
            System.out.print("Seleccione una opcion: ");
            opcion = Util.leerInt(sc);

            switch (opcion) {
                case 1:
                    System.out.print("Usuario: ");
                    String usuario   = Util.leerString(sc);
                    System.out.print("Contrasena: ");
                    String contrasena = Util.leerString(sc);

                    if (usuario.equals(USUARIO_CORRECTO) && contrasena.equals(config[0])) {
                        System.out.println("\n  ¡Bienvenido a SUSHI TEGRIDAD! 🍣");
                        Util.pausa(sc);
                        return true;
                    } else {
                        System.out.println("\n  Usuario o contrasena incorrecta.");
                        Util.pausa(sc);
                    }
                    break;

                case 2:
                    System.out.println("Hasta luego.");
                    return false;

                default:
                    System.out.println("Opcion invalida.");
                    Util.pausa(sc);
                    break;
            }
        } while (true);
    }

    // ════════════════════════════════════════════════════════════
    //  MENÚ PRINCIPAL
    // ════════════════════════════════════════════════════════════
    private static void menuPrincipal(Almacen almacen, ModuloVentas ventas, RecursosHumanos rrhh) {
        int opcion;
        do {
            Util.limpiarConsola();
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║   MENÚ PRINCIPAL - SUSHI TEGRIDAD    ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.println("1. Almacen");
            System.out.println("2. Ventas");
            System.out.println("3. Recursos Humanos");
            System.out.println("4. Configuracion");
            System.out.println("5. Salir (y Guardar)");
            System.out.print("Seleccione una opcion: ");
            opcion = Util.leerInt(sc);

            switch (opcion) {
                case 1:
                    almacen.mostrarMenu(sc);
                    break;
                case 2:
                    ventas.mostrarMenu(sc);
                    break;
                case 3:
                    rrhh.mostrarMenu(sc);
                    break;
                case 4:
                    configuracion();
                    break;
                case 5:
                    GestorArchivos.guardarDatos(config[0], almacen, ventas, rrhh);
                    System.out.println("\n  ¡Gracias por usar SUSHI TEGRIDAD! 🍣");
                    Util.pausa(sc);
                    break;
                default:
                    System.out.println("Opcion invalida.");
                    Util.pausa(sc);
                    break;
            }
        } while (opcion != 5);
    }

    // ════════════════════════════════════════════════════════════
    //  CONFIGURACIÓN
    // ════════════════════════════════════════════════════════════
    private static void configuracion() {
        Util.limpiarConsola();
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║  CONFIGURACION - SUSHI TEGRIDAD    ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("1. Cambiar contrasena del Admin");
        System.out.println("2. Regresar");
        System.out.print("Seleccione una opcion: ");
        int opc = Util.leerInt(sc);

        if (opc == 1) {
            System.out.print("Nueva contrasena (sin espacios): ");
            String nueva = Util.leerString(sc);
            if (!nueva.isEmpty()) {
                config[0] = nueva;
                System.out.println("  ✔ Contrasena actualizada.");
            } else {
                System.out.println("  Contrasena invalida. No se realizaron cambios.");
            }
        }
        Util.pausa(sc);
    }
}

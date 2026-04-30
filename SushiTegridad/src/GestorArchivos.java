import java.io.*;
import java.util.Map;

/**
 * Gestiona la persistencia de datos del sistema.
 * Usa un script Python (excel_manager.py) para leer/escribir
 * directamente al archivo Excel (SushiTegridad_Analytics.xlsx).
 *
 * Flujo de guardado:  Java → pipe stdin → Python → Excel
 * Flujo de carga:     Excel → Python → pipe stdout → Java
 */
public class GestorArchivos {

    // ════════════════════════════════════════════════════════════
    //  GUARDAR DATOS → EXCEL
    // ════════════════════════════════════════════════════════════
    public static void guardarDatos(String contrasena, Almacen almacen,
                                    ModuloVentas ventas, RecursosHumanos rrhh) {
        String scriptPath = buscarScript();
        if (scriptPath == null) return;
        String pythonExe = detectarPython();

        System.out.println("\n  Guardando datos en Excel...");

        try {
            String userDir = System.getProperty("user.dir");
            ProcessBuilder pb = new ProcessBuilder(pythonExe, scriptPath, "save");
            pb.directory(new File(userDir));
            pb.redirectErrorStream(false);
            Process proc = pb.start();

            // ── Escribir datos al stdin de Python ────────────────
            try (PrintWriter pw = new PrintWriter(
                    new OutputStreamWriter(proc.getOutputStream(), "UTF-8"), true)) {
                // Contraseña
                pw.println(contrasena);

                // Ingredientes
                pw.println(almacen.getIngredientes().size());
                for (Ingrediente ing : almacen.getIngredientes()) {
                    pw.printf("%s %.6f %d %s%n",
                            ing.getNombre(), ing.getCantidad(),
                            ing.getDiasCaducidad(), ing.getFechaRegistro());
                }

                // Platillos
                pw.println(ventas.getPlatillos().size());
                for (Platillo p : ventas.getPlatillos()) {
                    Map<String, Float> receta = p.getReceta();
                    pw.printf("%s %.2f %d%n", p.getNombre(), p.getPrecio(), receta.size());
                    for (Map.Entry<String, Float> e : receta.entrySet()) {
                        pw.printf("%s %.6f%n", e.getKey(), e.getValue());
                    }
                }

                // Ventas
                pw.println(ventas.getVentas().size());
                for (Venta v : ventas.getVentas()) {
                    pw.printf("%s %d %.2f%n", v.getNombrePlatillo(), v.getCantidad(), v.getTotal());
                }

                // Empleados
                pw.println(rrhh.getEmpleados().size());
                for (Empleado e : rrhh.getEmpleados()) {
                    pw.printf("%s %s %.2f %d%n",
                            e.getNombre(), e.getPuesto(), e.getSueldoDiario(), e.getDiasSemana());
                }
            }

            // ── Leer salida del script ───────────────────────────
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(proc.getInputStream(), "UTF-8"));
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println("  " + linea);
            }

            // Leer errores si los hay
            BufferedReader errBr = new BufferedReader(
                    new InputStreamReader(proc.getErrorStream(), "UTF-8"));
            while ((linea = errBr.readLine()) != null) {
                System.err.println("  [Python] " + linea);
            }

            int exit = proc.waitFor();
            if (exit == 0) {
                System.out.println("  [INFO] Datos guardados exitosamente en Excel.");
            } else {
                System.out.println("  [ERROR] excel_manager.py termino con codigo " + exit);
            }
        } catch (Exception ex) {
            System.err.println("  [ERROR] No se pudo guardar en Excel: " + ex.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════
    //  CARGAR DATOS ← EXCEL
    // ════════════════════════════════════════════════════════════
    public static String cargarDatos(Almacen almacen, ModuloVentas ventas, RecursosHumanos rrhh) {
        String scriptPath = buscarScript();
        if (scriptPath == null) {
            System.out.println("[INFO] Script excel_manager.py no encontrado. Primera ejecucion.");
            return null;
        }
        String pythonExe = detectarPython();

        try {
            String userDir = System.getProperty("user.dir");
            ProcessBuilder pb = new ProcessBuilder(pythonExe, scriptPath, "load");
            pb.directory(new File(userDir));
            pb.redirectErrorStream(false);
            Process proc = pb.start();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(proc.getInputStream(), "UTF-8"));

            String primeraLinea = br.readLine();
            if (primeraLinea == null || primeraLinea.startsWith("NO_EXISTE")) {
                System.out.println("[INFO] Archivo Excel no encontrado. Primera ejecucion.");
                proc.waitFor();
                return null;
            }
            if (primeraLinea.startsWith("ERROR:")) {
                System.err.println("[ERROR] " + primeraLinea);
                proc.waitFor();
                return null;
            }

            String contrasena = primeraLinea.trim();

            // ── Ingredientes ─────────────────────────────────────
            int numIng = Integer.parseInt(br.readLine().trim());
            for (int i = 0; i < numIng; i++) {
                String[] partes = br.readLine().trim().split(" ");
                String nombre = partes[0];
                float cantidad = Float.parseFloat(partes[1]);
                int diasCad = partes.length > 2 ? Integer.parseInt(partes[2]) : 0;
                String fechaReg = partes.length > 3 ? partes[3] : java.time.LocalDate.now().toString();
                almacen.getIngredientes().add(
                        new Ingrediente(nombre, cantidad, diasCad, fechaReg));
            }

            // ── Platillos ────────────────────────────────────────
            int numPlat = Integer.parseInt(br.readLine().trim());
            for (int i = 0; i < numPlat; i++) {
                String[] header = br.readLine().trim().split(" ");
                Platillo p = new Platillo(header[0], Float.parseFloat(header[1]));
                int numReceta = Integer.parseInt(header[2]);
                for (int r = 0; r < numReceta; r++) {
                    String[] rp = br.readLine().trim().split(" ");
                    p.agregarIngredienteReceta(rp[0], Float.parseFloat(rp[1]));
                }
                ventas.getPlatillos().add(p);
            }

            // ── Ventas ───────────────────────────────────────────
            int numVentas = Integer.parseInt(br.readLine().trim());
            for (int i = 0; i < numVentas; i++) {
                String[] vp = br.readLine().trim().split(" ");
                ventas.getVentas().add(
                        new Venta(vp[0], Integer.parseInt(vp[1]), Float.parseFloat(vp[2])));
            }

            // ── Empleados ────────────────────────────────────────
            int numEmp = Integer.parseInt(br.readLine().trim());
            for (int i = 0; i < numEmp; i++) {
                String[] ep = br.readLine().trim().split(" ");
                rrhh.getEmpleados().add(
                        new Empleado(ep[0], ep[1], Float.parseFloat(ep[2]), Integer.parseInt(ep[3])));
            }

            // Leer errores si los hay
            BufferedReader errBr = new BufferedReader(
                    new InputStreamReader(proc.getErrorStream(), "UTF-8"));
            String errLinea;
            while ((errLinea = errBr.readLine()) != null) {
                System.err.println("  [Python] " + errLinea);
            }

            proc.waitFor();
            System.out.println("[INFO] Datos cargados exitosamente desde Excel.");
            return contrasena;

        } catch (Exception ex) {
            System.err.println("[ERROR] Fallo al cargar datos desde Excel: " + ex.getMessage());
            almacen.getIngredientes().clear();
            ventas.getPlatillos().clear();
            ventas.getVentas().clear();
            rrhh.getEmpleados().clear();
            return null;
        }
    }

    // ════════════════════════════════════════════════════════════
    //  UTILIDADES INTERNAS
    // ════════════════════════════════════════════════════════════

    /** Busca el script excel_manager.py en varias ubicaciones. */
    private static String buscarScript() {
        String userDir = System.getProperty("user.dir");
        String[] candidatos = {
            "excel_manager.py",
            userDir + File.separator + "excel_manager.py",
            userDir + File.separator + ".." + File.separator + "excel_manager.py"
        };
        for (String ruta : candidatos) {
            if (new File(ruta).exists()) return ruta;
        }
        System.out.println("[AVISO] excel_manager.py no encontrado. Coloca el script junto al programa.");
        return null;
    }

    /** Detecta el ejecutable de Python disponible en el sistema. */
    private static String detectarPython() {
        for (String py : new String[]{"py", "python", "python3"}) {
            try {
                Process check = Runtime.getRuntime().exec(new String[]{py, "--version"});
                if (check.waitFor() == 0) return py;
            } catch (Exception ignored) {
                // Ignorado intencionalmente
            }
        }
        return "python";
    }
}
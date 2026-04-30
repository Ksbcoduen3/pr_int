import java.util.Scanner;

/**
 * Clase utilitaria con métodos estáticos de apoyo para la consola.
 */
public class Util {

    /** Limpia la consola usando secuencia ANSI. */
    public static void limpiarConsola() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Pausa hasta que el usuario presione ENTER. */
    public static void pausa(Scanner sc) {
        System.out.print("\nPresione ENTER para continuar...");
        sc.nextLine();
    }

    /** Lee una línea y la convierte a entero; retorna -1 si falla. */
    public static int leerInt(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Lee una línea y la convierte a float; retorna -1 si falla. */
    public static float leerFloat(Scanner sc) {
        try {
            return Float.parseFloat(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1f;
        }
    }

    /** Lee y retorna una línea de texto. */
    public static String leerString(Scanner sc) {
        return sc.nextLine().trim();
    }

    /** Imprime una línea separadora de guiones. */
    public static void separador(int largo) {
        System.out.println("-".repeat(largo));
    }
}

package Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    // ANSI escapes (works on modern Windows terminals; jansi improves compatibility)
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";

    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[94m";
    public static final String WHITE = "\u001B[37m";
    public static final String BRIGHT_BLACK = "\u001B[90m";
    public static final String LIGHT_BLUE = "\u001B[94m";
    public static final String BG_GRAY = "\u001B[47m";

    private final Scanner scanner = new Scanner(System.in);

    public static void init() {

        try {
            Class<?> ansi = Class.forName("org.fusesource.jansi.AnsiConsole");
            ansi.getMethod("systemInstall").invoke(null);
        } catch (Exception ignored) {

        }
    }

    public String printBanner(String title) {
        StringBuilder result = new StringBuilder();
        String t = " " + title.trim() + " ";
        int width = Math.max(40, t.length() + 10);
        String line = new String(new char[width]).replace('\0', '*');
        System.out.println(BOLD + YELLOW + line + RESET);
        result.append(BOLD + YELLOW + line + RESET).append("\n");
        int pad = (width - t.length()) / 2;
        String leftPad = new String(new char[Math.max(0, pad - 1)]).replace('\0', ' ');
        String rightPad = new String(new char[Math.max(0, width - pad - t.length() - 1)]).replace('\0', ' ');
        System.out.println(BOLD + YELLOW + "*" + leftPad +CYAN + BOLD + t + rightPad +YELLOW+ "*" + RESET);
        result.append(BOLD + YELLOW + "*" + leftPad +CYAN + BOLD + t + rightPad +YELLOW+ "*" + RESET).append("\n");
        System.out.println(BOLD + YELLOW + line + RESET);
        result.append(BOLD + YELLOW + line + RESET).append("\n");
        return result.toString();
    }

    public void printMenu(List<String> options) {
        for (int i = 0; i < options.size(); i++) {
            String idx = String.format("%2d", i + 1);
            System.out.println(BOLD + GREEN + "[" + idx + "] " + RESET + options.get(i));
        }
    }

    public String prompt(String message) {
        StringBuilder promptBuilder = new StringBuilder();
        System.out.print(BOLD + BLUE + "» " + MAGENTA + message + " " +RESET);
        promptBuilder.append(BOLD + BLUE + "» " + MAGENTA + message + " " +RESET);
        return promptBuilder.toString();
    }

    public String printlnInfo(String msg) {
        StringBuilder promptBuilder = new StringBuilder();
        System.out.println(BOLD + BRIGHT_BLACK + "-> " + CYAN + msg+ RESET);
        promptBuilder.append(BOLD + BRIGHT_BLACK + "-> " + CYAN + msg+ RESET);
        return promptBuilder.toString();
    }

    public String printlnSuccess(String msg) {
        StringBuilder promptBuilder = new StringBuilder();
        System.out.println(BOLD + GREEN + "✔ " + GREEN + msg + RESET);
        promptBuilder.append(BOLD + GREEN + "✔ " + GREEN + msg + RESET);
        return promptBuilder.toString();
    }

    public String printlnError(String msg) {
        StringBuilder promptBuilder = new StringBuilder();
        System.out.println(BOLD + RED + "✖ " + RED + msg + RESET);
        promptBuilder.append(BOLD + RED + "✖ " + RED + msg + RESET);
        return promptBuilder.toString();
    }

    public String printTable(String[] headers, List<String[]> rows) {
        StringBuilder result = new StringBuilder();
        int cols = headers.length;
        int[] widths = new int[cols];
        for (int i = 0; i < cols; i++) widths[i] = headers[i].length();
        for (String[] row : rows) {
            for (int i = 0; i < cols && i < row.length; i++) {
                widths[i] = Math.max(widths[i], row[i] == null ? 0 : row[i].length());
            }
        }
        // header
        StringBuilder sep = new StringBuilder();
        for (int w : widths) sep.append("+").append(new String(new char[w + 2]).replace('\0', '-'));
        sep.append("+");
        System.out.println(BOLD + LIGHT_BLUE + sep.toString() + RESET);
        result.append(BOLD + LIGHT_BLUE + sep.toString() + RESET).append("\n");

        StringBuilder hdr = new StringBuilder();
        for (int i = 0; i < cols; i++) {
            hdr.append(LIGHT_BLUE).append("| ").append(RESET).append(BOLD).append(UNDERLINE).append(headers[i]).append(RESET)
               .append(new String(new char[widths[i] - headers[i].length()]).replace('\0', ' ')).append(" ");
        }
        hdr.append(LIGHT_BLUE).append("|").append(RESET);
        System.out.println(hdr.toString());
        result.append(hdr.toString()).append("\n");

        System.out.println(BOLD + LIGHT_BLUE + sep.toString() + RESET);
        result.append(BOLD + LIGHT_BLUE + sep.toString() + RESET).append("\n");
        // rows
        boolean alt = false;
        for (String[] row : rows) {
            StringBuilder r = new StringBuilder();
            for (int i = 0; i < cols; i++) {
                String cell = i < row.length && row[i] != null ? row[i] : "";
                r.append(LIGHT_BLUE).append("| ").append(RESET).append(cell).append(new String(new char[widths[i] - cell.length()]).replace('\0', ' ')).append(" ");
            }
            r.append(LIGHT_BLUE).append("|").append(RESET);
            System.out.println(r.toString());
            result.append(r.toString()).append("\n");
            System.out.println(BOLD + LIGHT_BLUE + sep.toString() + RESET);
            result.append(BOLD + LIGHT_BLUE + sep.toString() + RESET).append("\n");
        }
        return result.toString();
    }
    public void close() {
        try { scanner.close(); } catch (Exception ignored) {}

        try {
            Class<?> ansi = Class.forName("org.fusesource.jansi.AnsiConsole");
            ansi.getMethod("systemUninstall").invoke(null);
        } catch (Exception ignored) {}
    }

    public static List<String[]> rowsFromObjects(List<Object[]> list) {
        List<String[]> out = new ArrayList<>();
        for (Object[] r : list) {
            String[] s = new String[r.length];
            for (int i = 0; i < r.length; i++) s[i] = r[i] == null ? "" : String.valueOf(r[i]);
            out.add(s);
        }
        return out;
    }
}


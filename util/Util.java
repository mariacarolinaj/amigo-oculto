package util;

public class Util {
    public static void limparTela() {
        try {
            String so = System.getProperty("os.name");

            if (so.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("/bin/bash", "-c", "clear").inheritIO().start().waitFor();;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}

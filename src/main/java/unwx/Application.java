package unwx;

import java.util.Scanner;

public final class Application {
    public static void main(final String[] args) {
        Logger.info("Press enter to start, and enter to stop");

        final Scanner scanner = new Scanner(System.in);
        final MouseMover mover = new MouseMover();
        boolean flag = false;

        while (true) {
            scanner.nextLine();
            if (flag) {
                mover.stop();
                flag = false;
            } else {
                mover.start();
                flag = true;
            }
        }
    }
}

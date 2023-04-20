package unwx;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class Logger {
    private Logger() {
    }

    public static void info(final String msg) {
        log("INFO", msg);
    }

    public static void warn(final String msg) {
        log("WARN", msg);
    }

    public static void log(final String level,
                           final String msg) {

        System.out.printf("[%s] [%s]: (%s)%n", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS), level, msg);
    }
}

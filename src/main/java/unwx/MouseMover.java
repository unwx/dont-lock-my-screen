package unwx;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public final class MouseMover {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private volatile boolean started = false;
    private volatile Future<?> moveTask;
    private final ReentrantLock lock = new ReentrantLock();

    public void start() {
        try {
            lock.lock();
            if (started) {
                Logger.warn("Mouse mover is already started");
                return;
            }

            Logger.info("Mouse mover started");
            started = true;
            moveTask = executor.submit(MouseMover::moveMouseLoop);
        } finally {
            lock.unlock();
        }
    }

    public void stop() {
        try {
            lock.lock();
            if (!started) {
                Logger.warn("Mouse mover is already stopped");
                return;
            }

            Logger.info("Mouse mover stopped");
            started = false;
            moveTask.cancel(true);
        } finally {
            lock.unlock();
        }
    }

    private static void moveMouseLoop() {
        final Robot robot;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        final int yOffset = 1;
        final int delay = 5000;

        waitForUserActivityToStop();

        while (true) {
            if (Thread.interrupted()) {
                Logger.info("Thread interrupted, stopped");
                return;
            }

            final Point point1 = getMousePos();
            Logger.info("Moving mouse");
            robot.mouseMove(point1.x, point1.y + yOffset);
            robot.delay(delay);

            final Point point2 = getMousePos();
            if (point2.x == point1.x && point2.y == point1.y + yOffset) {
                Logger.info("Returning mouse back");
                robot.mouseMove(point1.x, point1.y);
                robot.delay(delay);
            } else {
                waitForUserActivityToStop();
            }
        }
    }

    private static void waitForUserActivityToStop() {
        Logger.info("Waiting for user activity to stop");
        final long delay = 7500;

        while (true) {
            final Point point1 = getMousePos();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Logger.info("Waiting was interrupted");
                Thread.currentThread().interrupt();
                return;
            }

            final Point point2 = getMousePos();
            if (point1.x == point2.x && point1.y == point2.y) {
                Logger.info("User activity ended");
                return;
            }
        }
    }

    private static Point getMousePos() {
        return MouseInfo.getPointerInfo().getLocation();
    }
}

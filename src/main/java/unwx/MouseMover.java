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
        final int delay = 1000;

        while (true) {
            if (Thread.interrupted()) {
                Logger.info("Thread interrupted, stopped");
                return;
            }

            final Point mousePoint = MouseInfo.getPointerInfo().getLocation();
            Logger.info("Moving mouse up");
            robot.mouseMove(mousePoint.x, mousePoint.y + yOffset);
            robot.delay(delay);

            final Point afterDelayPoint = MouseInfo.getPointerInfo().getLocation();
            if (afterDelayPoint.x == mousePoint.x && afterDelayPoint.y == mousePoint.y + yOffset) {
                Logger.info("Returning mouse back");
                robot.mouseMove(mousePoint.x, mousePoint.y);
                robot.delay(delay);
            }
        }
    }
}

package org.example.ostheo_projet.utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SystemStateMonitor {
    private static ScheduledExecutorService scheduler;

    public static void startMonitoring(Runnable onScreenLocked) {
        scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (isScreenLockedOrInactivity()) {
                    onScreenLocked.run();
                    stopMonitoring();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);  // Vérifie toutes les 5 secondes
    }

    public static void stopMonitoring() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    public static boolean isScreenLockedOrInactivity() throws Exception {
        Process process = Runtime.getRuntime().exec(
                new String[]{"osascript", "-e", "tell application \"System Events\" to get locked of security preferences"}
        );
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = reader.readLine();
        reader.close();
        return "true".equalsIgnoreCase(result);
    }
}
package cz.craftmania.craftlibs.utils.task;

import cz.craftmania.craftlibs.CraftLibs;
import org.bukkit.scheduler.BukkitRunnable;

public class RunnableHelper {

    private static boolean isStopping;

    public static void setServerStopping() {
        isStopping = true;
    }

    public static void runTask(Runnable runnable) {
        if(isStopping) {
            runnable.run();
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(CraftLibs.getInstance());
    }

    public static void runTaskAsynchronously(Runnable runnable) {
        if(isStopping) {
            runnable.run();
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(CraftLibs.getInstance());
    }

    public static void runTaskLater(Runnable runnable, long delay) {
        if(isStopping) {
            runnable.run();
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(CraftLibs.getInstance(), delay);
    }

    public static void runTaskLater(Runnable runnable) {
        runTaskLater(runnable, 1);
    }

    public static void runTaskLaterAsynchronously(Runnable runnable, long delay) {
        if(isStopping) {
            runnable.run();
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLaterAsynchronously(CraftLibs.getInstance(), delay);
    }

    public static void runTaskLaterAsynchronously(Runnable runnable) {
        runTaskLaterAsynchronously(runnable, 1);
    }

    public static void runTaskTimer(Runnable runnable, long delay, long period) {
        if(isStopping) {
            runnable.run();
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskTimer(CraftLibs.getInstance(), (period <= 20 * 10) ? (delay + RunnableDelay.getDelay()) : delay, period);
    }

    public static void runTaskTimer(Runnable runnable, long period) {
        runTaskTimer(runnable, 0, period);
    }

    public static void runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        if(isStopping) {
            runnable.run();
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskTimerAsynchronously(CraftLibs.getInstance(), (period <= 20 * 10) ? (delay + RunnableDelay.getDelay()) : delay, period);
    }

    public static void runTaskTimerAsynchronously(Runnable runnable, long period) {
        runTaskTimerAsynchronously(runnable, 0, period);
    }


}

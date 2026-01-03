package rimon.bossBarControls;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BossBarControls extends JavaPlugin implements Listener {

    private BossBar activeBossBar;
    private BukkitTask activeTask;

    @Override
    public void onEnable() {
        if (getCommand("bossbar") != null) {
            getCommand("bossbar").setExecutor(new BBCommand(this));
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("BossBarControls enabled!");
    }

    @Override
    public void onDisable() {
        stopTimer();
    }

    public void startTimer(int totalSeconds, String commandToRun, String titlePrefix) {
        stopTimer();

        activeBossBar = BossBar.bossBar(
                LegacyComponentSerializer.legacyAmpersand().deserialize(titlePrefix + " " + totalSeconds + "s"),
                1.0f,
                BossBar.Color.RED,
                BossBar.Overlay.PROGRESS
        );

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showBossBar(activeBossBar);
        }

        activeTask = new BukkitRunnable() {
            int timeLeft = totalSeconds;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandToRun);
                    stopTimer();
                    cancel();
                    return;
                }

                Component updatedTitle = LegacyComponentSerializer.legacyAmpersand().deserialize(titlePrefix + " " + timeLeft + "s");
                activeBossBar.name(updatedTitle);

                float progress = (float) timeLeft / totalSeconds;
                activeBossBar.progress(Math.max(0.0f, Math.min(1.0f, progress)));

                timeLeft--;
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    public void stopTimer() {
        if (activeTask != null && !activeTask.isCancelled()) {
            activeTask.cancel();
            activeTask = null;
        }
        if (activeBossBar != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hideBossBar(activeBossBar);
            }
            activeBossBar = null;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (activeBossBar != null) {
            event.getPlayer().showBossBar(activeBossBar);
        }
    }
}

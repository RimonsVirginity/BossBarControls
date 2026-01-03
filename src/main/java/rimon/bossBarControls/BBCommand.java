package rimon.bossBarControls;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BBCommand implements CommandExecutor {

    private final BossBarControls plugin;

    public BBCommand(BossBarControls plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("bossbarcontrols.use")) {
            sender.sendMessage(Component.text("No permission.", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /bb <seconds> <command> [title...]", NamedTextColor.RED));
            return true;
        }

        int seconds;
        try {
            seconds = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid time.", NamedTextColor.RED));
            return true;
        }

        StringBuilder rawArgsBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            rawArgsBuilder.append(args[i]).append(" ");
        }
        String rawArgs = rawArgsBuilder.toString().trim();

        String commandToRun;
        String title;

        if (rawArgs.contains("|")) {

            String[] parts = rawArgs.split("\\|", 2);
            commandToRun = parts[0].trim();
            title = (parts.length > 1) ? parts[1].trim() : "Timer:";
        } else {

            int firstSpaceIndex = rawArgs.indexOf(' ');
            if (firstSpaceIndex == -1) {
                commandToRun = rawArgs;
                title = "Executing command in:";
            } else {
                commandToRun = rawArgs.substring(0, firstSpaceIndex).trim();
                title = rawArgs.substring(firstSpaceIndex + 1).trim();
            }
        }

        plugin.startTimer(seconds, commandToRun, title);
        sender.sendMessage(Component.text("Timer started!", NamedTextColor.GREEN));

        return true;
    }
}
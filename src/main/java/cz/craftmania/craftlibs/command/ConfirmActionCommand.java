package cz.craftmania.craftlibs.command;

import cz.craftmania.craftlibs.utils.actions.ConfirmAction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ConfirmActionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (args.length < 1) return true;
        String ID = args[0];

        Optional<ConfirmAction.Action> optionalAction = ConfirmAction.getAction(player, ID);
        if (!optionalAction.isPresent()) return true;
        ConfirmAction.Action action = optionalAction.get();

        action.run();
        return true;
    }
}

package ca.nightfury.minecraft.plugin.player.hearth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HearthCommandHandler implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        System.out.println(sender.getName());
        System.out.println(command.getName());
        return false;
    }
}

package me.simmi.MobsterHits;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin implements Listener
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		String prefix = ChatColor.BLUE.toString()+ChatColor.BOLD.toString()
		+"Hits"+ChatColor.DARK_GRAY.toString()+ChatColor.BOLD.toString()+"> ";

		World world = Bukkit.getWorld("world");
		
		if (label.equals("hit") && args.length == 1 && sender.hasPermission("hits.hit"))
		{
			String targetName = args[0];
			Player target = Bukkit.getServer().getPlayer(targetName);
			Player cmdsender = (Player) sender;
			if (target != null)
			{
				world.spawnEntity(target.getLocation(), EntityType.WITHER_SKELETON);
	            world.spawnEntity(target.getLocation(), EntityType.WITHER_SKELETON);
	            world.spawnEntity(target.getLocation(), EntityType.WITHER_SKELETON);
	            target.sendMessage(prefix+ChatColor.WHITE+"You've been hit by "+sender.getName().toString());	
	        
			}
			else 
			{
	            cmdsender.sendMessage(prefix+ChatColor.RED+"Player not online");			
			}
            
		}
		
		return true;
	}
}

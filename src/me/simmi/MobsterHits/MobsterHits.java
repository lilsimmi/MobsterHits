package me.simmi.MobsterHits;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class MobsterHits extends JavaPlugin
{
    // This is a variable that holds the `key` we use to get who owns
    // a 'hit' using NBT data.
    // We're supplying `this` here, referring to this class, MobsterHits,
    // so that Bukkit will be able to distinguish our "HitOwner" data from
    // another plugin's "HitOwner" data (if another plugin coincidentally
    // uses the same key of "HitOwner".)
    public final NamespacedKey ownerKey = new NamespacedKey(this, "HitOwner");
    // This will store the economy provider that we get through Vault.
    // For how we get this, see setupEconomy()
    public Economy econ;
    @Override
    public void onEnable()
    {
        // This sets a "default config value" so that
        // if there is no entry for "Cash per strike",
        // we use this value instead.
        getConfig().addDefault("Cash per strike", 5);
        // This sets it to write any default values back
        // to the config if they aren't already present.
        // In this case, that's just "Cash per strike: 5"
        getConfig().options().copyDefaults(true);
        // This actually writes the config, now that all
        // the settings are correct.
        saveConfig();
        // Now we need to attempt to hook into Vault.
        // If we failed to get an instance of the economy,
        // (which we know by the return value of setupEconomy() )
        // print an error message.
        if (!setupEconomy())
        {
            getLogger().severe("Couldn't register with Vault. Economy features will be disabled.");
            // Stop here.
            return;
        }
        // If we got to this point, the economy was registered successfully
        // and we can register the damage listener that pays out money.
        getServer().getPluginManager().registerEvents(new HitListener(this), this);
    }
    
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		String prefix = ChatColor.BLUE.toString() + ChatColor.BOLD.toString()
		 + "Hits" + ChatColor.DARK_GRAY.toString() + ChatColor.BOLD.toString() + "> ";
		
		if (args.length == 0) {
		    sender.sendMessage(prefix + ChatColor.RED + "/hit <player>");
		}
		else if (args.length == 1 && sender.hasPermission("hits.hit"))
		{
			String targetName = args[0];
			Player target = Bukkit.getServer().getPlayer(targetName);
			if (target != null)
			{
			    // Make sure to only get the target's world
			    // after we've confirmed the target isn't null.
			    World world = target.getWorld();
			    // Loops only while `i` is less than 3, and starting with 0.
			    // This means it loops 3 times, with the values 0, 1, and 2.
			    // We don't actually use the `i` variable except in the loop,
			    // it just helps us loop exactly 3 times.
			    for (int i = 0; i < 3; i++)
			    {
			        // This just stores a reference to the spawned wither skeleton into a variable.
			        // We could store it as a WitherSkeleton type, but we don't need to, so just skip it.
			        Entity witherSkeleton = world.spawnEntity(target.getLocation(), EntityType.WITHER_SKELETON);
			        
			        // Only store owner data if the command was run by a Player,
			        // because if the console or a command block ran the command,
			        // we don't have a Vault account to give money to.
			        if (sender instanceof Player)
			        {
			            // This sets a piece of NBT data on the wither skeleton that we can read later in HitListener.
	                    // `ownerKey` is the variable at the top of this file that holds the name we're storing
			            // the NBT data under.
			            // PersistentDataType.STRING says we're storing, well, a string.
			            // It would probably be better to store the sender's account ID (UUID) instead
			            // of their account name, but storing their account name is easier.
			            witherSkeleton.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, sender.getName());
			        }
			    }
	            target.sendMessage(prefix + ChatColor.WHITE + "You've been hit by " + sender.getName());
			}
			else 
			{
	            sender.sendMessage(prefix + ChatColor.RED + "Player not online");			
			}
            
		}
		
		return true;
	}
	
	// This code is taken from the Vault API example at:
	// https://github.com/MilkBowl/VaultAPI
	// with additional comments added.
	private boolean setupEconomy()
	{
	    // If Bukkit says there is no plugin named "Vault" loaded,
	    // then we can't set get the economy.
        if (getServer().getPluginManager().getPlugin("Vault") == null)
        {
            // If we couldn't find Vault, stop code execution.
            // We specify `false` because that's how we're telling
            // the caller of the function that we weren't successful.
            return false;
        }
        // This attempts to get a RegisteredServiceProvider for the Vault economy
        // through Bukkit's services API.
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        // If the Bukkit API didn't find a provider for Vault's economy,
        // stop here, and return `false` because we couldn't find an economy.
        if (rsp == null)
        {
            return false;
        }
        // This "unwraps" the Economy instance Bukkit found for us
        // and stores it in the class-level variable `econ` that
        // we declared earlier.
        econ = rsp.getProvider();
        // This is shorthand for returning true if `econ` is not null,
        // or returning false if it is null, because if `econ` is null,
        // we didn't succeed on our quest to find Vault's Economy.
        return econ != null;
    }
}

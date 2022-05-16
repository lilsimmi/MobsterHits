package me.simmi.MobsterHits;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import net.milkbowl.vault.economy.EconomyResponse;

public class HitListener implements Listener
{
    // A class-level variable that stores a reference to the main plugin.
    // This has no value until it's assigned in the constructor below.
    // The `final` modifier is here for a couple reasons, most notably
    // that the code won't compile if you don't set it to something
    // within the constructor.
    private final MobsterHits plugin;
    // This means that whenever a HitListener is created,
    // it must be provided an instance of a MobsterHits,
    // i.e. the plugin.
    public HitListener(MobsterHits plugin)
    {
        // We use `this` here to differentiate between the parameter named `plugin`
        // and the class-level variable that we're storing the parameter in, also named `plugin`.
        // In other words, this stores the value of the parameter `plugin`
        // into the class variable `plugin`.
        this.plugin = plugin;
    }
    
    // The `@EventHandler` tag lets Bukkit know that it should pass
    // some kind of event to this method. It doesn't matter what
    // the method is actually named; if it's tagged with `@EventHandler`,
    // Bukkit will know to find it when there's an event.
    // Note that Bukkit will only be able to find this method if
    // we register the whole class as an event "listener", which
    // we have done in onEnable().
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event)
    {
        // If the entity damaged by this event was NOT a Player,
        // return from the function (which stops execution of this code.)
        if (event.getEntity().getType() != EntityType.PLAYER)
        {
            return;
        }
        // Similarly, if the entity that caused the damage
        // was NOT a wither skeleton, we can stop here because
        // the only "hit mobs" we spawn are wither skeletons.
        if (event.getDamager().getType() != EntityType.WITHER_SKELETON)
        {
            return;
        }
        // Here we're declaring a variable `target` that holds a player,
        // and we're assigning it the value provided by the event as the
        // entity that was damaged.
        // `(Player)` here means that even though `event.getEntity()`
        // returns a base Entity, we are sure that the underlying class
        // here is a Player, and to store it as one.
        Player target = (Player) event.getEntity();
        // This attempts to get the NBT value we stored on the skeleton earlier
        // using ownerKey from the plugin class. We can reference it through the
        // class-level variable `plugin` we created earlier.
        // We're using PersistentDataType.STRING because that's the data
        // type we stored in it earlier (a string.)
        String hitterName = event.getDamager().getPersistentDataContainer().get(plugin.ownerKey, PersistentDataType.STRING);
        // If we didn't find any NBT on the skeleton, stop here,
        // because it must not be a skeleton we spawned.
        if (hitterName == null) {
            return;
        }
        // Try to get the player by name.
        Player hitter = Bukkit.getPlayer(hitterName);
        // If we couldn't find the player, they probably
        // went offline since they called the hit.
        // So, no need to give them money.
        if (hitter == null) {
            return;
        }
        // If we got this far, we need to take money from the player
        // that got damaged, and send money to the player who called
        // the hit.
        // It's stored in a `double` so it can store decimal places.
        // This gets the amount set in the config for "Cash per strike".
        // In the event there's no entry in the config for "Cash per strike",
        // it will instead return the default value we set in onEnable().
        double amount = plugin.getConfig().getDouble("Cash per strike");
        // The withdrawPlayer method returns some information about
        // whether or not the transaction was successful, so we store
        // that so we know how much money to award the player who
        // called the hit.
        EconomyResponse er = plugin.econ.withdrawPlayer(target, amount);
        // Then, we deposit however much money we got from the target
        // into the account of the player who called the hit.
        // No need to check whether or not it succeeded, there's
        // nothing we can do about a failure anyway.
        plugin.econ.depositPlayer(hitter, er.amount);
    }
}

package me.Jonnyfant.BukkitConsumableStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerConsumptionListener implements Listener {
    private BukkitConsumableStorage plugin;

    public PlayerConsumptionListener(BukkitConsumableStorage p) {
        plugin = p;
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Player p = event.getPlayer();
        DoubleChestInventory storageInv = plugin.getPlayerConsumablesStorageFromConfig(p);

        if (storageInv.containsAtLeast(event.getItem(), 1)) {
            storageInv.remove(new ItemStack(event.getItem().getType(), 1));
            plugin.writePlayerConsumablesStorageToConfig(storageInv);
            int task = 0;
            task = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    p.getInventory().addItem(new ItemStack(event.getItem().getType(), 1));
                }
            }, 1L);
        }
    }
}

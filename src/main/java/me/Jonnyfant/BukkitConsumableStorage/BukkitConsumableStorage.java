package me.Jonnyfant.BukkitConsumableStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitConsumableStorage extends JavaPlugin {
    private final String CFG_ALLOWED_ITEM_KEY = "Items that are allowed in the players consumable storage";
    private final List<String> CFG_ALLOWED_ITEM_DEFAULT = new LinkedList<String>(Arrays.asList(
            Material.GOLDEN_APPLE.name(),
            Material.GOLDEN_CARROT.name(),
            Material.APPLE.name()
    ));


    @Override
    public void onEnable() {
        loadConfig();
        getServer().getPluginManager().registerEvents(new PlayerConsumptionListener(this), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName();
        if (sender.hasPermission("consumablestorage.use") && sender instanceof Player) {
            switch (commandName.toLowerCase()) {
                case "consumablestorage":
                    ((Player) sender).openInventory(getPlayerConsumablesStorageFromConfig((Player) sender));
                default:
                    return false;
            }
        }
        return false;
    }

    public void loadConfig() {
        getConfig().addDefault(CFG_ALLOWED_ITEM_KEY, CFG_ALLOWED_ITEM_DEFAULT);
        getConfig().options().copyDefaults(true);
        saveConfig();
        File playerFolder = new File(getDataFolder(), "playerdata");
        if (!playerFolder.exists()) {
            playerFolder.mkdirs();
        }
    }

    public DoubleChestInventory getPlayerConsumablesStorageFromConfig(Player p) {
        DoubleChestInventory storageInv;
        File playerFolder = new File(getDataFolder(), "playerdata");
        File playerInvFile = new File(playerFolder, p.getUniqueId() + ".serialized");

        if (!playerInvFile.exists()) {
            storageInv = (DoubleChestInventory) Bukkit.createInventory(p, 6);
            try {
                FileOutputStream f = new FileOutputStream(playerInvFile);
                ObjectOutputStream o = new ObjectOutputStream(f);

                o.writeObject(storageInv);
                o.close();
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(playerInvFile));
            storageInv = (DoubleChestInventory) ois.readObject();
            ois.close();
            return storageInv;

        } catch (Exception e) {
            p.sendMessage("ERROR reading storage");
        }
        return null;
    }

    public boolean writePlayerConsumablesStorageToConfig(DoubleChestInventory dci) {
        File playerFolder = new File(getDataFolder(), "playerdata");
        File playerInvFile = new File(playerFolder, ((Player) dci.getHolder()).getUniqueId() + ".serialized");
        try {
            FileOutputStream f = new FileOutputStream(playerInvFile);
            ObjectOutputStream o = new ObjectOutputStream(f);

            o.writeObject(dci);
            o.close();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

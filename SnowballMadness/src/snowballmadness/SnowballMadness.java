package snowballmadness;

import com.google.common.base.Charsets;
import com.google.common.collect.*;
import com.google.common.io.Files;
import java.io.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * This is the plug-in class for this mod; it handles events and forwards them
 * to logic objects.
 *
 * @author DanJ
 */
public class SnowballMadness extends JavaPlugin implements Listener {

    private BukkitRunnable ticker;
    private boolean shouldLogSnowballs;

    /**
     * This returns true if we should be logging snowball activity.
     *
     * @return True to log snowball activity messages.
     */
    public boolean shouldLogSnowballs() {
        return shouldLogSnowballs;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        FileConfiguration config = getConfig();
        shouldLogSnowballs = config.getBoolean("logsnowballs", false);
        List<String> toNuke = config.getStringList("nuke");

        for (String victim : toNuke) {
            File file = new File(victim);

            if (file.exists()) {
                if (file.isDirectory()) {
                    getLogger().info(String.format("Deleting directory %s", victim));
                    deleteRecursively(file);
                } else if (getFileExtension(file).equalsIgnoreCase("json")) {
                    getLogger().info(String.format("Clearing file %s", victim));
                    clearJsonFile(file);
                } else {
                    getLogger().info(String.format("Deleting file %s", victim));
                    file.delete();
                }
            }
        }
    }

    /**
     * This deletes a directory and all its contents, because Java does not
     * provide that. Stupid Java!
     *
     * @param directory The directory (or file) to delete.
     */
    private static void deleteRecursively(File directory) {
        String[] listedFiles = directory.list();

        if (listedFiles != null) {
            for (String subfile : listedFiles) {
                File sf = new File(directory, subfile);
                deleteRecursively(sf);
            }
        }

        directory.delete();
    }

    /**
     * This method removes the content of a JSON file, which we need to do
     * because when we are loading, it's too late for Minecraft to recreate such
     * a file. So we just empty it before it is read.
     *
     * @param file The JSON file to overwrite with empty content.
     */
    private static void clearJsonFile(File file) {
        try {
            Files.write("[]", file, Charsets.US_ASCII);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * This extracts the file extension from the file given. The extension
     * returned does not include the '.' preceeding it. If the file has no
     * extension, this method returns "".
     *
     * @param file The file whose extension is wanted.
     * @return The extension, without the '.'.
     */
    private static String getFileExtension(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf(".");

        if (pos < 0) {
            return "";
        } else {
            return name.substring(pos + 1);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();

        // This creates the config file if missing
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);

        if (ticker != null) {
            ticker.cancel();
        }

        ticker = new BukkitRunnable() {
            private long tickCount = 0;
            
            @Override
            public void run() {
                SnowballLogic.onTick(tickCount++);
            }
        };

        ticker.runTaskTimer(this, 4, 4);
    }

    @Override
    public void onDisable() {

        if (ticker != null) {
            ticker.cancel();
            ticker = null;
        }

        HandlerList.unregisterAll((JavaPlugin) this);
        super.onDisable();
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        SnowballLogic.onProjectileLaunch(this, e);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        SnowballLogic.onProjectileHit(e);
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        SnowballLogic.onEntityDamageByEntityEvent(e);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        bestowSnowball(e.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        bestowSnowball(player);
        RespawnInfo.checkRespawn(player, this);
    }

    /**
     * This method gives a player a snowball in a designated snowball slot,
     * provided this slot is empty.
     *
     * @param player The player to be gifted with snow!
     */
    @SuppressWarnings("deprecation")
    private void bestowSnowball(Player player) {
        PlayerInventory inventory = player.getInventory();
        final int slotNumber = 8;

        ItemStack oldStack = inventory.getItem(slotNumber);

        if (oldStack == null || oldStack.getType() == Material.SNOW_BALL) {
            inventory.setItem(slotNumber, new ItemStack(Material.SNOW_BALL, 16));
            player.updateInventory();
        }
    }
}

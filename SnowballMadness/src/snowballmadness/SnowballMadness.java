package snowballmadness;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This is the plug-in class for this mod; it handles events and forwards them to logic objects.
 *
 * @author DanJ
 */
public class SnowballMadness extends JavaPlugin implements Listener {

    private BukkitRunnable ticker;
    private boolean shouldLogSnowballs;
    private boolean unbanPlayers;

    /**
     * This returns true if we should be logging snowball activity.
     *
     * @return True to log snowball activity messages.
     */
    public boolean shouldLogSnowballs() {
        return shouldLogSnowballs;
    }

    /**
     * This returns true if we should unban all players on startup.
     *
     * @return True to unban all playerss.
     */
    public boolean unbanPlayers() {
        return unbanPlayers;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        FileConfiguration config = getConfig();
        shouldLogSnowballs = config.getBoolean("logsnowballs", false);
        unbanPlayers = config.getBoolean("unbanPlayers", false);

        List<String> toNuke = config.getStringList("nuke");
        //This mechanic permits a list of region files to be deleted upon startup. It's for
        //use with servers that restart periodically and want to have regenerating terrain,
        //but leave some more distant areas persistent. The idea is for it to be low maintenance.

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
        //we have completed nuking the files, now we want to reset our list to blank
        toNuke.clear();
        getConfig().set("nuke", toNuke);
        saveConfig();
        //now we start afresh and players unloading chunks can flag what ought to be cleared
    }

    /**
     * This deletes a directory and all its contents, because Java does not provide that. Stupid Java!
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
     * This method removes the content of a JSON file, which we need to do because when we are loading, it's too late for
     * Minecraft to recreate such a file. So we just empty it before it is read.
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
     * This extracts the file extension from the file given. The extension returned does not include the '.' preceeding it. If the
     * file has no extension, this method returns "".
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
    public void onChunkUnload(ChunkUnloadEvent e) {
        FileConfiguration config = getConfig();
        Chunk chunk = e.getChunk();
        String unloadingWorld = "world";
        if (chunk.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            unloadingWorld = "world_nether";
        } else if (chunk.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            unloadingWorld = "world_the_end";
        } //we will also try to prune nether and end, which will be even tougher to place diamond blocks in

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        if (((chunkX - 16) % 32 == 0) && ((chunkZ - 16) % 32 == 0)) {
            //we are 16, 48, 80, 112, 144, 176, 208 etc (negative or positive) on both axes
            boolean protectRegion = false;
            //by default, expect to regenerate/nuke the region this is in
            for (int height = 1; height < 8; height++) {
                if (chunk.getBlock(16, height, 16).getType() == Material.DIAMOND_BLOCK) {
                    protectRegion = true;
                }
                //if any of these blocks in the chunk being unloaded (which is the center chunk
                //of the region) are diamond block, we will make sure that region's not in the nuke list
            }
            chunkX = (chunkX - 16) / 32;
            chunkZ = (chunkZ - 16) / 32;
            //convert these to what the region values will be
            String target = new StringBuilder().append(unloadingWorld).append("/region/r.").append(chunkX).append(".").append(chunkZ).append(".mca").toString();
            if (protectRegion) {
                List<String> toNuke = config.getStringList("nuke");
                Set<String> noDupes = new HashSet();
                noDupes.addAll(toNuke);
                toNuke.clear();
                toNuke.addAll(noDupes);
                //we've done this to ensure every item exists only once
                //do it BEFORE the remove
                toNuke.remove(target);
                getConfig().set("nuke", toNuke);
                saveConfig();
                //make sure the region is removed from nuke list
            } else {
                List<String> toNuke = config.getStringList("nuke");
                toNuke.add(target);
                Set<String> noDupes = new HashSet();
                noDupes.addAll(toNuke);
                toNuke.clear();
                toNuke.addAll(noDupes);
                //we've done this to ensure every item exists only once
                //do it AFTER the add
                getConfig().set("nuke", toNuke);
                saveConfig();
                //make sure the region is IN the nuke list
            }
        } //if it's not the center chunk of the region, we fall through and nothing happens
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
    public void onEntityTargetPlayer(EntityTargetLivingEntityEvent e) {
        SnowballLogic.onEntityTargetPlayer(e);
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        SnowballLogic.onEntityDamageByEntityEvent(e);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        bestowSnowball(player);
        RespawnInfo.checkRespawn(player, this);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        bestowSnowball(player);
        RespawnInfo.checkRespawn(player, this);
    }

    /**
     * This method gives a player a snowball in a designated snowball slot, provided this slot is empty.
     *
     * @param player The player to be gifted with snow!
     */
    @SuppressWarnings("deprecation")
    private void bestowSnowball(Player player) {

        PlayerInventory inventory = player.getInventory();
        ItemStack oldStack = inventory.getItem(8);
        if (oldStack == null || oldStack.getType() == Material.SNOW_BALL) {
            inventory.setItem(8, new ItemStack(Material.SNOW_BALL, 1));
            player.updateInventory();
        }

    }
}

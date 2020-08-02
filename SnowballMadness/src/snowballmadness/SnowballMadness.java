package snowballmadness;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
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
    private boolean nukeRegions;

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
    public boolean nukeRegions() {
        return nukeRegions;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        FileConfiguration config = getConfig();
        shouldLogSnowballs = config.getBoolean("logsnowballs", false);
        nukeRegions = config.getBoolean("nukeRegions", false);

        /*
        List<String> toNuke = config.getStringList("nuke");
        //This mechanic permits a list of region files to be deleted upon startup. It's for
        //use with servers that restart periodically and want to have regenerating terrain,
        //but leave some more distant areas persistent. The idea is for it to be low maintenance.

        if (!nukeRegions) {
            toNuke.clear(); //the moon server shouldn't be nuking all the locations
        }
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
         */
    }

    /**
     * This deletes a directory and all its contents, because Java does not provide that. Stupid Java!
     *
     * @param directory The directory (or file) to delete. private static void deleteRecursively(File directory) { String[]
     * listedFiles = directory.list();
     *
     * if (listedFiles != null) { for (String subfile : listedFiles) { File sf = new File(directory, subfile);
     * deleteRecursively(sf); } }
     *
     * directory.delete(); }
     */
    /**
     * This method removes the content of a JSON file, which we need to do because when we are loading, it's too late for
     * Minecraft to recreate such a file. So we just empty it before it is read.
     *
     * @param file The JSON file to overwrite with empty content. private static void clearJsonFile(File file) { try {
     * Files.write("[]", file, Charsets.US_ASCII); } catch (IOException ex) { throw new RuntimeException(ex); } }
     */
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

    /*
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        FileConfiguration config = getConfig();
        Chunk chunk = e.getChunk();
        boolean protectRegion = false;
        //by default, expect to regenerate/nuke the region this is in
        String unloadingWorld = "world";
        if (chunk.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            unloadingWorld = "world_nether";
            protectRegion = true;
        } else if (chunk.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            unloadingWorld = "world_the_end";
            protectRegion = true;
        } //we will not try to prune nether or end

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        if (((chunkX - 16) % 32 == 0) && ((chunkZ - 16) % 32 == 0)) {
            //we are 16, 48, 80, 112, 144, 176, 208 etc (negative or positive) on both axes
            for (int height = 1; height < 257; height++) {
                if (chunk.getBlock(16, height, 16).getType() == Material.REDSTONE_TORCH_ON || chunk.getBlock(16, height, 16).getType() == Material.OBSIDIAN) {
                    protectRegion = true;
                }
                //if any of these blocks in the chunk being unloaded (which is the center chunk
                //of the region) are redstone torch OR obsidian, we will make sure that region's not in the nuke list
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
        }//if it's not the center chunk of the region, we fall through and nothing happens
    }
    //all this assigns 'nuke' to all chunks that HAVE been visited, that then unloaded, and when unloading they didn't have
    //a diamond block in the key spot. Blank config files should not nuke anything upon launch. You've got to unload the chunk
    //to engage the regen functionality.
     */
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        SnowballLogic.onProjectileLaunch(this, e);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        SnowballLogic.onProjectileHit(e);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        if (block.getRelative(BlockFace.UP).getType() == Material.FIRE) {
            block.getRelative(BlockFace.UP).setType(Material.AIR);
        }
        if (block.getRelative(BlockFace.NORTH).getType() == Material.FIRE) {
            block.getRelative(BlockFace.NORTH).setType(Material.AIR);
        }
        if (block.getRelative(BlockFace.SOUTH).getType() == Material.FIRE) {
            block.getRelative(BlockFace.SOUTH).setType(Material.AIR);
        }
        if (block.getRelative(BlockFace.WEST).getType() == Material.FIRE) {
            block.getRelative(BlockFace.WEST).setType(Material.AIR);
        }
        if (block.getRelative(BlockFace.EAST).getType() == Material.FIRE) {
            block.getRelative(BlockFace.EAST).setType(Material.AIR);
        }
        //rather than disabling fire spread, we make it burn itself out by targeting fire blocks and removing them
        //This is very aggressive, if you're using it you might consider a random factor. This event doesn't fire as often
        //so it can be more intelligent.
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerFall(EntityDamageEvent e) {
        if (!(e == null)) {
            if (e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();
                if (e.getCause() == DamageCause.FALL) {
                    PlayerInventory inv = player.getInventory();
                    int heldSlot = inv.getHeldItemSlot();
                    ItemStack sourceStack = inv.getItem(heldSlot);
                    if (sourceStack == null || sourceStack.getType() == Material.SNOW_BALL) {
                        InventorySlice slice = InventorySlice.fromSlot(player, heldSlot).skip(1);
                        if (slice.getBottomItem().getType() == Material.FIREWORK) {
                            player.setFallDistance(0);
                            e.setCancelled(true);
                            //if we have the jetpack engaged, we're free from any fall damage.
                        }
                    }
                }
                return;
            } else {
                return;
            }
        }
    }

    /*
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.REDSTONE_TORCH_ON || event.getBlockPlaced().getType() == Material.OBSIDIAN) {
            Chunk chunk = event.getBlockPlaced().getChunk();
            int chunkX = chunk.getX();
            int chunkZ = chunk.getZ();
            boolean makeSound = false;
            for (int height = 1; height < 257; height++) {
                if (chunk.getBlock(16, height, 16).getType() == Material.REDSTONE_TORCH_ON || chunk.getBlock(16, height, 16).getType() == Material.OBSIDIAN) {
                    makeSound = true;
                }//this is entirely a player cue, to show player they've correctly placed the protection redstone torch.
            }
            if (makeSound) {
                if (((chunkX - 16) % 32 == 0) && ((chunkZ - 16) % 32 == 0)) {
                    event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_WITHER_DEATH, 1f, 0.1f);
                    event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_LAVA_AMBIENT, 1f, 0.1f);
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getChunk() != event.getTo().getChunk()) {
            Player player = event.getPlayer();
            Location playerLocation = player.getLocation();
            Location startLocation = player.getCompassTarget();

            int playerX = playerLocation.getBlockX();
            int playerY = playerLocation.getBlockY();
            int playerZ = playerLocation.getBlockZ();

            int offsetX = Math.floorDiv(playerX, 512);
            int offsetZ = Math.floorDiv(playerZ, 512);

            playerX = (offsetX * 512) + 256;
            playerZ = (offsetZ * 512) + 256;

            Location destLocation = playerLocation;
            destLocation.setX(playerX);
            destLocation.setY(playerY);
            destLocation.setZ(playerZ);
            player.setCompassTarget(destLocation);
            //every transition to a new chunk, we recalculate what the compass is pointing to
        }
    }
    

    @EventHandler
    public void onEntityTargetPlayer(EntityTargetLivingEntityEvent e) {
        SnowballLogic.onEntityTargetPlayer(e);
    }
    */

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        SnowballLogic.onEntityDamageByEntityEvent(e);
    }

    /*
    @EventHandler
    public void explodeEvent(EntityExplodeEvent event) {
        for (Entity entity : event.getEntity().getNearbyEntities(1.1, 10.0, 1.1)) {
            entity.remove();
        }
        //if there still appear to be drops, relog: the client sometimes still sees them after
        //they have been removed. The 10 should be a vertical slice inside which drops are removed.
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        for (Entity entity : event.getEntity().getNearbyEntities(1.1, 1.1, 1.1)) {
            entity.remove();
        }
    }
    //These operate on the same princimple of the fire spread: if we have absurd densities of TNT spam,
    //we indiscriminately begin killing entities to de-lag the server while still allowing a ridiculous
    //amount of mayhem. Much noise and fury, but it burns itself out unnaturally fast.
    */
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        /*Location playerLocation = player.getLocation();
        Location startLocation = player.getCompassTarget();

        int playerX = playerLocation.getBlockX();
        int playerY = playerLocation.getBlockY();
        int playerZ = playerLocation.getBlockZ();

        int offsetX = Math.floorDiv(playerX, 512);
        int offsetZ = Math.floorDiv(playerZ, 512);

        playerX = (offsetX * 512) + 256;
        playerZ = (offsetZ * 512) + 256;

        Location destLocation = playerLocation;
        destLocation.setX(playerX);
        destLocation.setY(playerY);
        destLocation.setZ(playerZ);
        player.setCompassTarget(destLocation);
        //every new login, we recalculate what the compass is pointing to*/

        PlayerInventory inventory = player.getInventory();
        ItemStack oldStack = inventory.getItem(8);
        if (oldStack == null) {
            inventory.setItem(8, new ItemStack(Material.SNOW_BALL, 16));
            player.updateInventory();
        } //only upon join do we give only one base snowball, only if slot 8 is empty.
        RespawnInfo.checkRespawn(player, this);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        /*Location playerLocation = player.getLocation();
        Location startLocation = player.getCompassTarget();

        int playerX = playerLocation.getBlockX();
        int playerY = playerLocation.getBlockY();
        int playerZ = playerLocation.getBlockZ();

        int offsetX = Math.floorDiv(playerX, 512);
        int offsetZ = Math.floorDiv(playerZ, 512);

        playerX = (offsetX * 512) + 256;
        playerZ = (offsetZ * 512) + 256;

        Location destLocation = playerLocation;
        destLocation.setX(playerX);
        destLocation.setY(playerY);
        destLocation.setZ(playerZ);
        player.setCompassTarget(destLocation);
        //every respawn, we recalculate what the compass is pointing to*/

        PlayerInventory inventory = player.getInventory();
        ItemStack oldStack = inventory.getItem(8);
        if (oldStack == null) {
            inventory.setItem(8, new ItemStack(Material.SNOW_BALL, 16));
            player.updateInventory();
        } //only upon join do we give only one base snowball, only if slot 8 is empty.
        RespawnInfo.checkRespawn(player, this);
    }
}

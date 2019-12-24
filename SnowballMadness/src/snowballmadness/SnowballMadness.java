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
        for (int height = 1; height < 257; height++) {
            if (chunk.getBlock(16, height, 16).getType() == Material.REDSTONE_TORCH_ON) {
                protectRegion = true;
            }
            //if any of these blocks in the chunk being unloaded (which is the center chunk
            //of the region) are diamond block, we will make sure that region's not in the nuke list
        }

        chunkX = chunkX / 32;
        chunkZ = chunkZ / 32;
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
    }
    //all this assigns 'nuke' to all chunks that HAVE been visited, that then unloaded, and when unloading they didn't have
    //a diamond block in the key spot. Blank config files should not nuke anything upon launch. You've got to unload the chunk
    //to engage the regen functionality.

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
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.REDSTONE_TORCH_ON) {
            Chunk chunk = event.getBlockPlaced().getChunk();
            boolean makeSound = false;
            for (int height = 1; height < 257; height++) {
                if (chunk.getBlock(16, height, 16).getType() == Material.REDSTONE_TORCH_ON) {
                    makeSound = true;
                }//this is entirely a player cue, to show player they've correctly placed the protection redstone torch.
            }
            if (makeSound) {
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_WITHER_DEATH, 1f, 0.1f);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_LAVA_AMBIENT, 1f, 0.1f);
            }
        }
    }

    /*
   @EventHandler
     public void jumpOnGrass(PlayerInteractEvent event) {
     if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.GRASS) {
     event.getClickedBlock().setType(Material.GRASS_PATH);
     }
     //if you jump on the grass, you wear it down to path.
     }
     
     @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            Player player = event.getPlayer();
            if (player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_HOE) {
                Block block = player.getLocation().getBlock(); //block at our feet
                block.getRelative(-1, 0, 0).setType(Material.AIR);
                block.getRelative(1, 0, 0).setType(Material.AIR);
                block.getRelative(0, 0, -1).setType(Material.AIR);
                block.getRelative(0, 0, 1).setType(Material.AIR);
                block.getRelative(-1, 1, 0).setType(Material.AIR);
                block.getRelative(1, 1, 0).setType(Material.AIR);
                block.getRelative(0, 1, -1).setType(Material.AIR);
                block.getRelative(0, 1, 1).setType(Material.AIR);
                block.getRelative(-1, 2, 0).setType(Material.AIR);
                block.getRelative(1, 2, 0).setType(Material.AIR);
                block.getRelative(0, 2, -1).setType(Material.AIR);
                block.getRelative(0, 2, 1).setType(Material.AIR);
                block.getRelative(-1, 3, 0).setType(Material.AIR);
                block.getRelative(1, 3, 0).setType(Material.AIR);
                block.getRelative(0, 3, -1).setType(Material.AIR);
                block.getRelative(0, 3, 1).setType(Material.AIR);
                block.getRelative(-1, 4, 0).setType(Material.AIR);
                block.getRelative(1, 4, 0).setType(Material.AIR);
                block.getRelative(0, 4, -1).setType(Material.AIR);
                block.getRelative(0, 4, 1).setType(Material.AIR);
                //main column: if moving NSEW, will make continuous hole
                block.getRelative(-2, 1, 0).setType(Material.AIR);
                block.getRelative(2, 1, 0).setType(Material.AIR);
                block.getRelative(0, 1, -2).setType(Material.AIR);
                block.getRelative(0, 1, 2).setType(Material.AIR);
                block.getRelative(-2, 2, 0).setType(Material.AIR);
                block.getRelative(2, 2, 0).setType(Material.AIR);
                block.getRelative(0, 2, -2).setType(Material.AIR);
                block.getRelative(0, 2, 2).setType(Material.AIR);
                block.getRelative(-2, 3, 0).setType(Material.AIR);
                block.getRelative(2, 3, 0).setType(Material.AIR);
                block.getRelative(0, 3, -2).setType(Material.AIR);
                block.getRelative(0, 3, 2).setType(Material.AIR);
                //wings: if moving NSEW will widen hole

                double rotation = player.getLocation().getYaw() % 360;
                if (rotation < 0) {
                    rotation += 360.0;
                }
                if (0 <= rotation && rotation < 22.5) {
                    block.getRelative(0, 0, 2).setType(Material.AIR);
                } else if (67.5 <= rotation && rotation < 112.5) {
                    block.getRelative(-2, 0, 0).setType(Material.AIR);
                } else if (157.5 <= rotation && rotation < 202.5) {
                    block.getRelative(0, 0, -2).setType(Material.AIR);
                } else if (247.5 <= rotation && rotation < 292.5) {
                    block.getRelative(2, 0, 0).setType(Material.AIR);
                } else if (337.5 <= rotation && rotation < 360.0) {
                    block.getRelative(0, 0, 2).setType(Material.AIR);
                }
                //only does cardinal directions. Gives you ability to move forward because your feets are clear of obstacles!
                int lightingX = block.getX();
                int lightingZ = block.getZ();
                if (lightingX < 0) {
                    lightingX += 1;
                }
                if (lightingZ < 0) {
                    lightingZ += 1;
                }
                //this corrects negative numbers so it still has zeroes for the lighting spacing.
                if (block.getRelative(0, 5, 0).getType() == Material.STONE && lightingX % 10 == 0 && lightingZ % 10 == 0) {
                    block.getRelative(0, 5, 0).setType(Material.SEA_LANTERN);
                }
                //put in ceiling strip lighting, but ONLY if you are aligned with tens on X, Y and Z. x0.x, y0.y, z0.z is the pattern: must have the zeroes in all ones places
                //So make tunnels 10 apart on every axis (the ones column must be zero) on height and your lateral positioning.
                //if you want them to be lit. Otherwise, it's rogue tunneling and not part of the lighting project!

                /*
                Block block = player.getLocation().subtract(0, 1, 0).getBlock();
                if (block.getType() == Material.GRASS) {
                    block.setType(Material.GRASS_PATH);
                }
                block = block.getRelative(BlockFace.DOWN);
                if (block.getType() == Material.GRASS) {
                    block.setType(Material.GRASS_PATH);
                } */
 /*
            } else if (player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_BLOCK) {
                Block block = player.getLocation().getBlock(); //block at our feet
                double scaleFactor = player.getInventory().getItemInMainHand().getAmount() * 0.03125;
                //let's do everything based on an amount of 32 for a huge-ass tunnel
                int beginX = block.getX() - (int)(13*scaleFactor);
                int beginY = block.getY();
                int beginZ = block.getZ() - (int)(13*scaleFactor);
                int endX = beginX + (int)(26*scaleFactor);
                int endY = beginY + (int)(16*scaleFactor);
                int endZ = beginZ + (int)(26*scaleFactor);
                World world = player.getWorld();
                Location locationBuffer = new Location(player.getWorld(), 0, 0, 0);
                Location playerLocation = new Location(player.getWorld(), 0, 0, 0);
                playerLocation = player.getLocation().add(0, (int)(5*scaleFactor), 0);

                // no worries- all this executes before Minecraft can send anything
                // back to the client, so we can set the blocks in any order. This one
                // is convenient!
                int maxDistance = (int)(12*scaleFactor);
                for (int x = beginX; x <= endX; ++x) {
                    for (int z = beginZ; z <= endZ; ++z) {
                        for (int y = beginY; y <= endY; ++y) {
                            locationBuffer.setX(x);
                            locationBuffer.setY(y);
                            locationBuffer.setZ(z);
                            if (playerLocation.distance(locationBuffer) < maxDistance) {
                                Block target = world.getBlockAt(x, y, z);
                                target.setType(Material.AIR);
                            }
                        }
                    }
                }

                beginX = block.getX() - (int)(1.49*scaleFactor);
                beginZ = block.getZ() - (int)(1.49*scaleFactor);
                endX = beginX + (int)(3*scaleFactor);
                endY = block.getY() + (int)(17*scaleFactor);
                endZ = beginZ + (int)(3*scaleFactor);
                for (int x = beginX; x <= endX; ++x) {
                    for (int z = beginZ; z <= endZ; ++z) {
                        Block target = world.getBlockAt(x, endY, z);
                        if (target.getType() == Material.STONE) {
                            target.setType(Material.SEA_LANTERN);
                        }
                    }
                }
                //Large light panels up top for big tunnels

                beginX = block.getX() - (int)(9*scaleFactor);
                beginZ = block.getZ() - (int)(9*scaleFactor);
                endX = beginX + (int)(18*scaleFactor);
                endY = block.getY() - 1;
                endZ = beginZ + (int)(18*scaleFactor);
                for (int x = beginX; x <= endX; ++x) {
                    for (int z = beginZ; z <= endZ; ++z) {
                        Block target = world.getBlockAt(x, endY, z);
                        if (target.getType() == Material.STONE) {
                            target.setType(Material.SMOOTH_BRICK);
                        }
                    }
                }
                //Large light panels up top for big tunnels

                /*
                Block block = player.getLocation().subtract(0, 1, 0).getBlock();
                if (block.getType() == Material.GRASS) {
                    block.setType(Material.GRASS_PATH);
                }
                block = block.getRelative(BlockFace.DOWN);
                if (block.getType() == Material.GRASS) {
                    block.setType(Material.GRASS_PATH);
                } */ /*
            }
        }
        //even walking on the grass turns it to path, if you're carrying a diamond hoe
    } */

    @EventHandler
    public void onEntityTargetPlayer(EntityTargetLivingEntityEvent e) {
        SnowballLogic.onEntityTargetPlayer(e);
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        SnowballLogic.onEntityDamageByEntityEvent(e);
    }

    @EventHandler
    public void explodeEvent(EntityExplodeEvent event) {
        for (Entity entity : event.getEntity().getNearbyEntities(1.1, 10.0, 1.1)) {
            entity.remove();
        }
        //if there still appear to be drops, relog: the client sometimes still sees them after
        //they have been removed. The 10 should be a vertical slice inside which drops are removed.
    }

    /*
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

 /*
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack oldStack = inventory.getItem(8);
        if (oldStack == null) {
            inventory.setItem(8, new ItemStack(Material.SNOW_BALL, 5));
            player.updateInventory();
        } //only upon join do we give only one base snowball, only if slot 8 is empty.
        RespawnInfo.checkRespawn(player, this);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack oldStack = inventory.getItem(8);
        if (oldStack == null) {
            inventory.setItem(8, new ItemStack(Material.SNOW_BALL, 5));
            player.updateInventory();
        } //only upon join do we give only one base snowball, only if slot 8 is empty.
        RespawnInfo.checkRespawn(player, this);
    }
    
    //for the current version, we're not going to just give people snowballs willy nilly
    //instead, you gotta go and get some and manage that as a resource, slowing the roll of
    //random trolls. You might call it a toll on that. Slow-troll-roll toll :)
     */
}

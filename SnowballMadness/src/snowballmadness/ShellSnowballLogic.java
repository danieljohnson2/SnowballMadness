package snowballmadness;

import com.google.common.base.Preconditions;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.projectiles.ProjectileSource;

/**
 * This logic makes a shell out of the material you give it, varying the construction method by type. You can specify the material
 * for the walls, but by default this class will only replace air and liquid blocks.
 *
 * @author christopherjohnson
 */
public class ShellSnowballLogic extends SnowballLogic {

    private final Material helmetType;
    private final InventorySlice inventory;

    public ShellSnowballLogic(Material helmetType, InventorySlice inventory) {
        this.helmetType = helmetType;
        this.inventory = Preconditions.checkNotNull(inventory);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        ProjectileSource shooter = snowball.getShooter();
        int expLevel = 1;
        if (shooter instanceof Player) {
            Player player = (Player) shooter;
            expLevel = player.getLevel();
        }
        final Material wallMaterial = inventory.getBottomItem().getType();

        int baseTool = 0; //no effect
        switch (helmetType) {
            case CHAINMAIL_HELMET:
                baseTool = 32;
                break;
            case DIAMOND_HELMET:
                baseTool = 16;
                break;
            case GOLD_HELMET:
                baseTool = 8;
                break;
            case IRON_HELMET:
                baseTool = 4;
                break;
            case LEATHER_HELMET:
                baseTool = 2;
                break;
        }

        final int radius = (int) (Math.sqrt(expLevel * baseTool));
        final int diameter = radius * 2;
        final double distanceSquaredLimit = (radius * (double) radius) + 1.0;

        World world = snowball.getWorld();
        Location snowballLoc = snowball.getLocation().clone();

        // while in theory x anx z are unlimited, we want to keep y
        // within the normal world.
        final int beginX = snowballLoc.getBlockX() - radius;
        final int beginY = Math.max(1, snowballLoc.getBlockY() - radius);
        final int beginZ = snowballLoc.getBlockZ() - radius;
        final int endX = beginX + diameter;
        final int endY = Math.min(world.getMaxHeight(), beginY + diameter);
        final int endZ = beginZ + diameter;
        final Location locationBuffer = new Location(world, 0, 0, 0);
        double distanceSquared = 0;

        // no worries- all this executes before Minecraft can send anything
        // back to the client, so we can set the blocks in any order. This one
        // is convenient!
        for (int x = beginX; x <= endX; ++x) {
            for (int z = beginZ; z <= endZ; ++z) {
                for (int y = beginY; y <= endY; ++y) {
                    locationBuffer.setX(x);
                    locationBuffer.setY(y);
                    locationBuffer.setZ(z);
                    distanceSquared = snowballLoc.distanceSquared(locationBuffer);
                    if (distanceSquared > ((distanceSquaredLimit * 0.9) - 9.0)
                            && distanceSquared <= distanceSquaredLimit) {
                        Block target = world.getBlockAt(x, y, z);
                        Material material = target.getType();
                        if (material == Material.AIR
                                || material == Material.WATER
                                || material == Material.STATIONARY_WATER
                                || material == Material.LAVA
                                || material == Material.STATIONARY_LAVA
                                || material == Material.WEB
                                || material == Material.TNT
                                || material == Material.MONSTER_EGGS
                                || material == Material.FIRE
                                || material == Material.LONG_GRASS
                                || material == Material.RED_ROSE
                                || material == Material.YELLOW_FLOWER) {
                            target.setType(wallMaterial);
                        }
                    }
                }
            }
        }
    }
}

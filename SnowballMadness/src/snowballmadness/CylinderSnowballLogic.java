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
public class CylinderSnowballLogic extends SnowballLogic {

    private final Material chestplateType;
    private final InventorySlice inventory;

    public CylinderSnowballLogic(Material chestplateType, InventorySlice inventory) {
        this.chestplateType = chestplateType;
        this.inventory = Preconditions.checkNotNull(inventory);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        ProjectileSource shooter = snowball.getShooter();
        Material wallMaterial = inventory.getBottomItem().getType();
        if (wallMaterial == Material.CHEST) {
            return;
        }

        int baseTool = 0; //no effect
        switch (chestplateType) {
            case CHAINMAIL_CHESTPLATE:
                baseTool = 64;
                break;
            case DIAMOND_CHESTPLATE:
                baseTool = 32;
                break;
            case GOLD_CHESTPLATE:
                baseTool = 16;
                break;
            case IRON_CHESTPLATE:
                baseTool = 8;
                break;
            case LEATHER_CHESTPLATE:
                baseTool = 4;
                break;
        }

        final int radius = baseTool;
        final int diameter = radius * 2;
        final double distanceSquaredLimit = (radius * (double) radius) + 1.0;

        World world = snowball.getWorld();
        Location snowballLoc = snowball.getLocation().clone();

        // while in theory x anx z are unlimited, we want to keep y
        // within the normal world.
        final int beginX = snowballLoc.getBlockX() - radius;
        final int beginY = Math.max(1, snowballLoc.getBlockY() - (int) (radius * 0.66666f));
        final int beginZ = snowballLoc.getBlockZ() - radius;
        final int endX = beginX + diameter;
        final int endY = Math.min(world.getMaxHeight(), beginY + radius);
        final int endZ = beginZ + diameter;
        final Location movingYBuffer = new Location(world, 0, 0, 0);
        double distanceSquared = 0;

        // no worries- all this executes before Minecraft can send anything
        // back to the client, so we can set the blocks in any order. This one
        // is convenient!
        for (int x = beginX; x <= endX; ++x) {
            for (int z = beginZ; z <= endZ; ++z) {
                for (int y = beginY; y <= endY; ++y) {
                    movingYBuffer.setX(x);
                    movingYBuffer.setY(snowballLoc.getY());
                    movingYBuffer.setZ(z);
                    distanceSquared = snowballLoc.distanceSquared(movingYBuffer);
                    if (distanceSquared > ((distanceSquaredLimit * 0.9) - 9.0)
                            && distanceSquared <= distanceSquaredLimit) {
                        Block target = world.getBlockAt(x, y, z);
                        Material material = target.getType();
                        target.setType(wallMaterial);
                    }
                }
            }
        }
    }
}

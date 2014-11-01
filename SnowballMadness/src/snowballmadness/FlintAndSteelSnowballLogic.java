/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;

/**
 * This logic sets the block or entity at the point of impact on fire
 *
 * @author DanJ
 */
public class FlintAndSteelSnowballLogic extends SnowballLogic {

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        Location loc = snowball.getLocation().clone();
        Block block = loc.getBlock();

        if (block.getType() == Material.AIR && block.getY() > 1) {
            loc.setY(loc.getY() - 1);
            block = loc.getBlock();
        }

        if (block.getType() != Material.AIR) {
            loc.setY(loc.getY() + 1);
            block = loc.getBlock();
        }

        if (block.getType() == Material.AIR) {
            block.setType(Material.FIRE);
        }
    }

    @Override
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        int extraTicks = (int) (20 * info.amplification);
        target.setFireTicks(target.getFireTicks() + extraTicks);

        return super.damage(snowball, info, target, proposedDamage);
    }
}

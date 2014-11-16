package snowballmadness;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;

/**
 * This logic drops anvils on the point of impact. It works by placing an anvil
 * block up to 64 blocks above the impact. Minecraft gravity does the rest.
 *
 * @author DanJ
 */
public class AnvilSnowballLogic extends SnowballLogic {

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        Location start = snowball.getLocation();
        Location loc = start.clone();

        int maxHeight = Math.min(loc.getWorld().getMaxHeight(), (int) loc.getY() + 64);
       
        while (loc.getBlock().getType() == Material.AIR && loc.getBlockY() < maxHeight) {
            loc.setY(loc.getY() + 1);
        }

        if (loc.getY() > start.getY() && loc.getBlock().getType() != Material.AIR) {
            loc.setY(loc.getY() - 1);
        }

        Block target = loc.getBlock();
        if (target.getType() == Material.AIR) {
            target.setType(Material.ANVIL);
        }
    }
}

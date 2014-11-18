/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;

/**
 * This is a specialty snowball logic. If you use a daylight sensor on the
 * snowball, it will place a daylight sensor. Then it will track upward, placing
 * bombs until all ceiling surfaces are blasted away, allowing the sensor to see
 * daylight. Not really a useful way to get to the surface, but arguably a sort
 * of trap for mining the surface? You could try to hit the ground under where a
 * player is running.
 *
 * @author christopherjohnson
 */
public class DaylightFinderSnowballLogic extends SnowballLogic {

    public DaylightFinderSnowballLogic() {
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        Location loc = snowball.getLocation().clone();

        if (loc.getBlock().getType() == Material.AIR && loc.getY() > 1) {
            loc.setY(loc.getY() - 1);
        }

        if (loc.getBlock().getType() != Material.AIR) {
            loc.setY(loc.getY() + 1);
        }

        Block target;

        while (loc.getY() < 240) {
            if (loc.getBlock().getType() == Material.AIR) {
                loc.setY(loc.getY() + 1);
                //scan up until a non-air block is reached or maxHeight is
            } else {
                //we have a solid block and we aren't past maxHeight minus space to place bombs
                target = loc.getBlock();
                target.setType(Material.REDSTONE_BLOCK);
                loc.setY(loc.getY() + 1);
                target = loc.getBlock();
                target.setType(Material.TNT);
                loc.setY(loc.getY() + 2);
            }
            //this scans up, placing lots of redstone-triggered TNT, skipping hollow places
            //we SHALL see daylight with our detector, dammit! :D
            //it'll probably get blowed up, though. But it tried!
        }
    }
}

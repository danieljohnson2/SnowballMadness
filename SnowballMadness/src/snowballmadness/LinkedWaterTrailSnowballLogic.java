/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import static snowballmadness.SnowballLogic.getGroundUnderneath;

/**
 * This logic mimics LinkedTrail, but it's for placing lily pads or other blocks
 * on water source blocks. Perhaps some of the halfslabs?
 *
 * @author christopherjohnson
 */
public class LinkedWaterTrailSnowballLogic extends SnowballLogic {

    private final Material toPlace;
    private Location previousLocation;
    private Location firstLocation;

    public LinkedWaterTrailSnowballLogic(Material toPlace) {
        this.toPlace = Preconditions.checkNotNull(toPlace);
        //we won't need a 'hit' method as we won't care
        //however, this should compile now as it's a real class
        //and does a thing.
    }

    @Override
    public void launch(Snowball snowball, SnowballInfo info) {
        super.launch(snowball, info);
        firstLocation = snowball.getLocation();
    }

    @Override
    public void tick(Snowball snowball, SnowballInfo info) {
        super.tick(snowball, info);

        Location currentLocation = snowball.getLocation().clone();

        // want to avoid doing anything until we get far enough from
        // the thrower.
        if (firstLocation != null) {
            if (firstLocation.distance(currentLocation) < 0.5) {
                return;
            } else {
                firstLocation = null;
            }
        }

        if (previousLocation == null) {
            previousLocation = currentLocation;
        } else {
            double dist = previousLocation.distance(currentLocation);

            if (dist >= 1.0) {
                Location delta = currentLocation.clone();
                delta.subtract(previousLocation);

                for (double offset = 0; offset <= dist; ++offset) {
                    Location target = delta.clone();
                    target.multiply(offset / dist);
                    target.add(previousLocation);

                   while (target.getBlock().getType() == Material.AIR) {
                        target.add(0, -1, 0);
                    }
                    //chase down in the most primitive way possible
                    if ((target.getBlock().getType() == Material.STATIONARY_WATER)) {
                        target.add(0, 1, 0);
                        target.getBlock().setType(toPlace);
                    }
                }
                previousLocation = currentLocation;
            }
        }
    }
}

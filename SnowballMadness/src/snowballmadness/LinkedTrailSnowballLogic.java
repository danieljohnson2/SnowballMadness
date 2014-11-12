package snowballmadness;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import static snowballmadness.SnowballLogic.getGroundUnderneath;

/**
 * This logic tracks the snowball in flight, and when it has left an air block,
 * the block is replaced with a material. It might do this on a chunk by chunk
 * basis, tracking each block it's in within a chunk and updating those blocks
 * once the snowball has left that chunk.
 *
 * Does not apply to the chunk snowball author is in.
 *
 * When it hits anything, it just stops. Only works while traveling through air
 * blocks. I'm thinking, minecart for 'girders', fencepost for ground fences.
 * Gravel for a not-updated gravel in the air bridge? LinkedTrail should fill in
 * the gaps that would exist in a 'fence' placement, in the event of a
 * corner-to-corner transition. The idea is, linked is ones that should be
 * continuous (girders).
 *
 * Also passed is whether the trail is in the air, on, or under the surface
 * (lowest air block available under each placement point, or replace the
 * ground). The 'surface' variations are fence for a wood fence, packed ice for
 * an ice trail, dead bush for coal blocks embedded in the ground with fire on
 * top of them (scorching the earth). The scan for this should skip past air and
 * leaf blocks so the effect goes under tree canopies.
 *
 * @author christopherjohnson
 */
public class LinkedTrailSnowballLogic extends SnowballLogic {

    private final Material toPlace;
    private Location previousLocation;
    private Location firstLocation;

    public LinkedTrailSnowballLogic(Material toPlace) {
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

                    target = getGroundUnderneath(target);
                    if  ((target.getBlock().getType() != toPlace))
                            {
                        target.add(0, 1, 0);
                        target.getBlock().setType(toPlace);
                    }
                    //suggestion from NetBeans. This means place if the underlying block isn't same as the one being placed,
                    //also place the water lily if the underlying block is Stationary Water. Yay machine logic!
                }

                previousLocation = currentLocation;
            }
        }
    }
}

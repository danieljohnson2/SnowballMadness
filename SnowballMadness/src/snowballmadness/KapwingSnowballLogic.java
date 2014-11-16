package snowballmadness;

import com.google.common.base.Preconditions;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.*;

/**
 * This logic provides a bouncing snowball; it can be used to 'skip' snowballs
 * long distances, or just to bounce in place for a long time. The snowball gets
 * as many bounces as there quartz block triggering this logic.
 *
 * NOTE: If I remember my Klingon, this should be spelled "Qapwing".
 *
 * @author christopherjohnson
 */
public class KapwingSnowballLogic extends SnowballLogic {

    private final int numberOfCueballs;
    private final InventorySlice inventory;

    public KapwingSnowballLogic(int numberOfCueballs, InventorySlice inventory) {
        this.numberOfCueballs = numberOfCueballs;
        this.inventory = Preconditions.checkNotNull(inventory);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        World world = snowball.getWorld();
        LivingEntity shooter = snowball.getShooter();
        Location source = snowball.getLocation().clone();
        source.setY(source.getY() + 0.25);

        Vector bounce = snowball.getVelocity().clone();
        bounce.setY(-(bounce.getY()));
        bounce.multiply(numberOfCueballs);
        //we radically multiply the speed of the thing
        //This can send glowstone TNT bombs very far away. It is
        //the artillery mechanic, you have to skip your bombs
        //off a flat surface or they'll just go up and out of sight.

        Snowball skipper = world.spawn(source, Snowball.class);
        skipper.setShooter(shooter);
        skipper.setVelocity(bounce);

        performLaunch(inventory, skipper, info);
    }

    @Override
    public String toString() {
        return String.format("%s -> (x%d) %s",
                super.toString(),
                numberOfCueballs,
                createLogic(inventory));
    }
}

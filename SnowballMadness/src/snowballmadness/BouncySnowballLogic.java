/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.Preconditions;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.*;

/**
 * This snowball logic spawns
 *
 * @author christopherjohnson
 */
public class BouncySnowballLogic extends SnowballLogic {

    private final int numberOfSlimeballs;
    private final InventorySlice inventory;

    public BouncySnowballLogic(int numberOfSlimeballs, InventorySlice inventory) {
        this.numberOfSlimeballs = numberOfSlimeballs;
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
        //bounce.multiply(numberOfSlimeballs);
        //alternate, we can to multiply the bounce by the number of slimeballs.
        //This can send glowstone TNT bombs into the stratosphere!

        Snowball skipper = world.spawn(source, Snowball.class);
        skipper.setShooter(shooter);
        skipper.setVelocity(bounce);

        if (numberOfSlimeballs <= 1) {
            performLaunch(inventory, skipper, info);
        } else {
            // notice we reuse the same inventory here, but we reduce the number of slimeballs
            // by one. This way next bounce carries the same behavior as this one, until
            // the slimeballs run out.

            SnowballLogic nextLogic = new BouncySnowballLogic(numberOfSlimeballs - 1, inventory);
            performLaunch(nextLogic, skipper, info);
        }

        //this should produce one bounce without any trouble
        //two possibilities for completing it.
        //One, it bounces forever until it hits somewhere with a solid block over where it hit
        //Two, it bounces the number of slimeballs you have in there, and then goes off.
        //I'm not sure quite how to make it 'launch itself' rather than a payload snowball,
        //but it's probably trivial.

    }

    @Override
    public String toString() {
        return String.format("%s -> (x%d) %s",
                super.toString(),
                numberOfSlimeballs,
                createLogic(inventory));
    }
}

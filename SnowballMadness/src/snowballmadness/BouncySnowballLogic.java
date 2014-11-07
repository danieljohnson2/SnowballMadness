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
        //we are not going to amplify the bounce because the initial velocity should
        //be what's amplified. Thus we needn't amplify it again.

        Snowball skipper = world.spawn(source, Snowball.class);
        skipper.setShooter(shooter);
        skipper.setVelocity(bounce);

        performLaunch(inventory, skipper, info);
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
                super.toString());
    }
}

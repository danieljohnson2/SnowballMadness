/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.*;

/**
 * This logic attracts entities to the snowball; more power makes the
 * attractions stronger and longer ranged.
 *
 * @author DanJ
 */
public class DeathVortexSnowballLogic extends SnowballLogic {

    private Vector previousTarget;

    @Override
    public void tick(Snowball snowball, SnowballInfo info) {
        super.tick(snowball, info);

        World world = snowball.getWorld();
        Vector target = snowball.getLocation().toVector();

        // on the first tick, we approximate the previous location by
        // using the shooter's present location. Close enough!
        if (previousTarget == null) {
            Location shooterLoc = snowball.getShooter().getLocation();

            if (shooterLoc.getWorld() == world) {
                previousTarget = shooterLoc.toVector();
            } else {
                previousTarget = target;
            }
        }

        for (Entity victim : world.getEntities()) {
            if (!(victim instanceof Snowball)) {
                accelerate(victim, target, info.power);
                accelerate(victim, previousTarget, info.power);
            } else {
                accelerate(victim, target, (0.005 * info.power));
                //victim is another snowball, we make them slightly interactive
            }
        }
        previousTarget = target;
    }

    /**
     * This method applies acceleration of a victim towards a target. We a
     * vector for 'target' instead of a Location because the methods we need are
     * found on it; semantically this is a location.
     *
     * @param victim The entity to accelerate.
     * @param target The target to move the entity towards.
     * @param power The snowball power; accelerate speed depends on this.
     */
    private void accelerate(Entity victim, Vector target, double power) {
        Vector eLoc = victim.getLocation().toVector();
        double dist = target.distance(eLoc);

        if (dist > 0.0) {
            double factor = power / dist;
            if (factor > 0.0001) {
                Vector vel = victim.getVelocity().clone();
                double restraint = 1.0 / ((Math.pow(vel.length(), 3)) + 1.0);
                factor *= restraint;

                Vector d = target.clone();
                d.subtract(eLoc);
                d.normalize();
                d.multiply(factor);
                vel.add(d);
                victim.setVelocity(vel);
            }
        }
    }
}

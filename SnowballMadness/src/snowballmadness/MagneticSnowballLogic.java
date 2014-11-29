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
 * This does not apply an attraction to the shooter; this creates an unpleasant
 * glitchy effect.
 *
 * @author DanJ
 */
public class MagneticSnowballLogic extends SnowballLogic {

    private Vector previousTarget;

    @Override
    public void tick(Snowball snowball, SnowballInfo info) {
        super.tick(snowball, info);

        World world = snowball.getWorld();
        Vector target = snowball.getLocation().toVector();

        // on the first tick, we approximate the previous locaiton by
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
            if (canAttract(victim, snowball, info)) {
                accelerate(victim, target, info.power);
                accelerate(victim, target.getMidpoint(previousTarget), info.power);
                accelerate(victim, previousTarget, info.power);
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
        double distSq = target.distanceSquared(eLoc);
        double factor = (power * 2) / distSq;
        if (factor > 0.001) {
            Vector d = target.clone();
            d.subtract(eLoc);
            d.normalize();
            d.multiply(factor);

            Vector vel = victim.getVelocity().clone().add(d);
            victim.setVelocity(vel);
        }
    }

    /**
     * This method decides whether to attract 'entity' to the snowball given. If
     * this returns false, the entity is skipped.
     *
     * @param entity The entity that might be attracted.
     * @param snowball The snowball to attract the entity to.
     * @param info The info about the snowball.
     * @return true to move accelerate entity towards the snowball, false to do
     * nothing to it.
     */
    protected boolean canAttract(Entity victim, Snowball snowball, SnowballInfo info) {
        if (victim == snowball.getShooter()) {
            return false;
        }

        if (victim instanceof Snowball) {
            return false;
        }

        return true;
    }
}

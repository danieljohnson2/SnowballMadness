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
        Vector momentum = snowball.getVelocity().normalize();

        // on the first tick, we approximate the previous location by
        // using the snowball's present location. Close enough!
        if (previousTarget == null) {
            Location shooterLoc = snowball.getLocation();

            if (shooterLoc.getWorld() == world) {
                previousTarget = shooterLoc.toVector();
            } else {
                previousTarget = target;
            }
        }

        for (Entity victim : world.getEntities()) {
            if (canAttract(victim, snowball, info)) {
                accelerate(victim, target, info.power);
                if (!(victim instanceof Snowball)) {
                    accelerate(victim, momentum, info.speed * 2.0);
                }
                accelerate(victim, target.getMidpoint(previousTarget), info.power);
                accelerate(victim, previousTarget, info.power);
            }
        }

        previousTarget = target;
    }

    @Override
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        target.teleport(target.getLocation().add(target.getVelocity()));
        //if you hit things with the magnet snowball, you knock 'em away from you somewhat
        //if there are many snowballs the velocity will be instantly clamped
        return 0;
    }

    /**
     * This method applies acceleration of a victim towards a target. We a
     * vector for 'target' instead of a Location because the methods we need are
     * found on it; semantically this is a location.
     *
     * To get a funny 'hang in the air' effect, this method actually repels when
     * the victim is within a block of the target.
     *
     * @param victim The entity to accelerate.
     * @param target The target to move the entity towards.
     * @param power The snowball power; accelerate speed depends on this.
     */
    private void accelerate(Entity victim, Vector target, double power) {
        Vector eLoc = victim.getLocation().toVector();
        double dist = target.distance(eLoc);
        double distSq = target.distanceSquared(eLoc);
        double distFactor = (1.0 - (1.0 / (dist + 1)));
        distSq -= 1.0;

        if (distSq != 0.0) {
            double factor = (power * 2) / distSq;
            if (factor > 0.001) {
                Vector d = target.clone();
                d.subtract(eLoc);
                d.normalize();
                d.multiply(factor);
                if (d.getY() < 0) {
                    d.setY(d.getY() * distFactor);
                    //levitate factor: if we're near the snowball we tend not to fall
                }
                Vector vel = victim.getVelocity().clone().add(d);
                if (vel.length() > (dist / 8)) {
                    if (vel.length() > 1) {
                        vel.normalize();
                        //avoid normalizing speeds up
                    }
                    //max speed is related to closeness to snowball
                }
                victim.setVelocity(vel);
                victim.setFallDistance(0); //also stop damage while under magnetism
            }
        }
        if (victim instanceof Snowball && dist < 2 && !(victim == target)) {
            victim.remove(); //we will cut down on the clustering slightly this way
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

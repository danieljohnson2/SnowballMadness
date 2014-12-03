/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.*;
import org.bukkit.Location;

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
            if ((victim instanceof Snowball) || (victim == snowball.getShooter())) {
                accelerate(victim, target, (0.03 * info.power));
                //victim is another snowball or the shooter, we make them less interactive
            } else {
                accelerate(victim, target, info.power);
                accelerate(victim, previousTarget, info.power);
                if (victim instanceof ExperienceOrb) {
                    victim.remove();
                }
            }
        }
        previousTarget = target;
    }

    @Override
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        target.teleport(target.getLocation().add(target.getVelocity()));
        //if you hit things with the death vortex snowball, you knock 'em away from you
        return 0;
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

                if (eLoc.getY() < (target.getY() + 4.0)) {
                    d.setY(Math.abs(d.getY())); //vortex tends to make victims go up!
                }
                vel.add(d);
                victim.setVelocity(vel);
                victim.setFallDistance(0); //stop damage while going up
            }
        }
    }
}

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

    @Override
    public void tick(Snowball snowball, SnowballInfo info) {
        super.tick(snowball, info);

        World world = snowball.getWorld();
        Vector target = snowball.getLocation().toVector();

        for (Entity e : world.getEntities()) {
            if (canAttract(e, snowball, info)) {
                Vector eLoc = e.getLocation().toVector();
                double distSq = target.distanceSquared(eLoc);
                double factor = (info.power * 5) / distSq;

                if (factor > 0.001) {
                    Vector d = target.clone().subtract(eLoc);
                    d.normalize();
                    d.multiply(factor);

                    Vector vel = e.getVelocity().clone().add(d);
                    e.setVelocity(vel);
                }
            }
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
    protected boolean canAttract(Entity entity, Snowball snowball, SnowballInfo info) {
        if (entity == snowball.getShooter()) {
            return false;
        }

        if (entity instanceof Snowball) {
            return false;
        }

        return true;
    }
}

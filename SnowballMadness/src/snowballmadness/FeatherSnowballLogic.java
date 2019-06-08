/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.entity.*;
import org.bukkit.scheduler.*;
import org.bukkit.util.*;

/**
 * This logic lifts a creature that you hit into the air briefly, applying an
 * upwards force that diminishes over time.
 *
 * @author DanJ
 */
public class FeatherSnowballLogic extends LingeringSnowballLogic<Entity> {

    @Override
    public double damage(Snowball snowball, final SnowballInfo info, final Entity target, double proposedDamage) {
        beginLinger(info, 2, 100, target);

        return super.damage(snowball, info, target, proposedDamage);
    }

    @Override
    protected boolean linger(SnowballInfo info, int counter, Entity victim) {

        Vector vel = victim.getVelocity().clone();
        vel.add(new Vector(0, (Math.sqrt(info.power)) / (Math.pow(counter,2)+1), 0));
        victim.setVelocity(vel);
        victim.setFallDistance(0);

        return true;
    }
}
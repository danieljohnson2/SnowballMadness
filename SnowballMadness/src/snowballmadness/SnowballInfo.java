/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.*;
import org.bukkit.entity.*;

/**
 * This class holds onto auxiliary information about a snowball; we pass this to
 * all the SnowballLogic methods along with the snowball itself; it's
 * essentially is a paramter block.
 *
 * @author DanJ
 */
public final class SnowballInfo {

    /**
     * This is an info object that contains default values.
     */
    public static final SnowballInfo EMPTY = new SnowballInfo(null, 1.0);
    /**
     * This is the shooter of the snowball. We can create additional snowballs
     * that don't have a natural shooter; their getShooter() method returns
     * null, so use this instead.
     */
    public final LivingEntity shooter;
    /**
     * This is a modifier on the 'power' of a snowball; different snowballs
     * treat this differently.
     */
    public final double amplification;

    public SnowballInfo(Snowball snowball) {
        this.shooter = snowball.getShooter();
        this.amplification = 1.0;
    }

    public SnowballInfo(LivingEntity shooter, double amplification) {
        this.shooter = shooter;
        this.amplification = amplification;
    }

    /**
     * This returns a new info whose amplification has been scaled by the factor
     * given.
     *
     * @param factor The factor by which we adjust the amplification.
     * @return A new info object.
     */
    public SnowballInfo getAmplified(double factor) {
        return new SnowballInfo(shooter, amplification * factor);
    }
}

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
 * essentially is a parameter block.
 *
 * @author DanJ
 */
public final class SnowballInfo {

    /**
     * This is the plugin being run.
     */
    public final SnowballMadness plugin;
    /**
     * This is a modifier on the speed of a snowball; it makes snowballs fly
     * faster.
     */
    public final double speed;
    /**
     * This is a modifier on the power of a snowball; different snowballs treat
     * this differently.
     */
    public final double power;

    public SnowballInfo(SnowballMadness plugin) {
        this(plugin, 1.0, 1.0);
    }

    public SnowballInfo(SnowballMadness plugin, double speed, double power) {
        this.plugin = Preconditions.checkNotNull(plugin);
        this.speed = speed;
        this.power = power;
    }

    /**
     * This returns a new info whose speed has been scaled by the factor given.
     *
     * @param factor The factor by which we adjust the speed.
     * @return A new info object.
     */
    public SnowballInfo speeded(double factor) {
        return new SnowballInfo(plugin, speed * factor, power);
    }

    /**
     * This returns a new info whose power has been scaled by the factor given.
     *
     * @param factor The factor by which we adjust the power.
     * @return A new info object.
     */
    public SnowballInfo powered(double factor) {
        return new SnowballInfo(plugin, speed, power * factor);
    }
}

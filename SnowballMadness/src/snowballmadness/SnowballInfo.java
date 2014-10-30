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
     * This is an info object that contains default values.
     */
    public static final SnowballInfo EMPTY = new SnowballInfo(1.0);
    /**
     * This is a modifier on the 'power' of a snowball; different snowballs
     * treat this differently.
     */
    public final double amplification;

    public SnowballInfo() {
        this.amplification = 1.0;
    }

    public SnowballInfo(double amplification) {
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
        return new SnowballInfo(amplification * factor);
    }
}

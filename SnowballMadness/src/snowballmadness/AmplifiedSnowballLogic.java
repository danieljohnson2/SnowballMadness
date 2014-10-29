/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

/**
 * This is a snowball that flies faster than nomrla, and can apply a second
 * logic that will be amplified too.
 *
 * @author DanJ
 */
public class AmplifiedSnowballLogic extends ChainableSnowballLogic {

    private final double amplification;

    public AmplifiedSnowballLogic(double amplification, InventorySlice nextSlice) {
        super(nextSlice);
        this.amplification = amplification;
    }

    @Override
    protected SnowballInfo adjustInfo(Snowball snowball, SnowballInfo info) {
        return info.getAmplified(amplification);
    }

    @Override
    public String toString() {
        return String.format("%s -> (x%d) %s",
                super.toString(),
                amplification,
                nextLogic);
    }
}

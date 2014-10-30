/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.entity.Entity;
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
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        double damage = super.damage(snowball, info, target, proposedDamage);

        if (amplification > 1.0 && damage == 0.0) {
            damage = 1.0;
        }
        return damage * amplification;
    }

    @Override
    protected SnowballInfo adjustInfo(Snowball snowball, SnowballInfo info) {
        return info.getAmplified(amplification);
    }

    @Override
    public String toString() {
        return String.format("%s -> (x%f) %s",
                getClass().getSimpleName(),
                amplification,
                nextLogic);
    }
}

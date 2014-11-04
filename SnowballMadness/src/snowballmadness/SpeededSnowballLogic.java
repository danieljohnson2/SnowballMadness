/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

/**
 * This is a snowball that flies faster than normal, and can apply a second
 * logic that will be given the increased speed.
 *
 * @author DanJ
 */
public class SpeededSnowballLogic extends ChainableSnowballLogic {

    private final double factor;

    public SpeededSnowballLogic(double factor, InventorySlice nextSlice) {
        super(nextSlice);
        this.factor = factor;
    }

    @Override
    public void launch(Snowball snowball, SnowballInfo info) {
        Vector v = snowball.getVelocity().clone();
        v.multiply(factor * info.speed);
        snowball.setVelocity(v);

        super.launch(snowball, info);
    }

    @Override
    protected SnowballInfo adjustInfo(Snowball snowball, SnowballInfo info) {
        return info.speeded(factor);
    }

    @Override
    public String toString() {
        return String.format("%s -> (x%f) %s",
                getClass().getSimpleName(),
                factor,
                nextLogic);
    }
}

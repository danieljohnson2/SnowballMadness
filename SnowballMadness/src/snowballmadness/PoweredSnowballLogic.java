package snowballmadness;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

/**
 * This is a snowball that has an increased effect; it increases the power level
 * for the next logic, and makes the snowball damaging.
 *
 * @author DanJ
 */
public class PoweredSnowballLogic extends ChainableSnowballLogic {

    private final double factor;

    public PoweredSnowballLogic(double factor, InventorySlice nextSlice) {
        super(nextSlice);
        this.factor = factor;
    }

    @Override
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        double damage = super.damage(snowball, info, target, proposedDamage);

        if (factor > 1.0 && damage == 0.0) {
            damage = 1.0;
        }
        
        return damage * factor;
    }

    @Override
    protected SnowballInfo adjustInfo(Snowball snowball, SnowballInfo info) {
        return info.powered(factor);
    }

    @Override
    public String toString() {
        return String.format("%s -> (x%f) %s",
                getClass().getSimpleName(),
                factor,
                nextLogic);
    }
}

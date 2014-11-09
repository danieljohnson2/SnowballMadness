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

    /**
     * This returns the factor applied to speed, sp the power slows the
     * snowball.
     *
     * @return The factor to apply to the speed.
     */
    private double getSpeedFactor() {
        return 1.0 - (
                factor / 18.0);
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
    public void launch(Snowball snowball, SnowballInfo info) {
        Vector v = snowball.getVelocity().clone();
        v.multiply(info.speed * getSpeedFactor());
        snowball.setVelocity(v);

        super.launch(snowball, info);
    }

    @Override
    protected SnowballInfo adjustInfo(Snowball snowball, SnowballInfo info) {
        return info.powered(factor).speeded(getSpeedFactor());
    }

    @Override
    public String toString() {
        return String.format("%s -> (x%f) %s",
                getClass().getSimpleName(),
                factor,
                nextLogic);
    }
}

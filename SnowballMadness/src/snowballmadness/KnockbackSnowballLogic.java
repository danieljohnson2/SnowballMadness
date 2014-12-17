package snowballmadness;

import java.util.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

/**
 *
 * Entity attacker, attacked; float knockback; attacked.setVelocity(
 * attacked.getVelocity().add(attacked.getLocation().toVector().subtract(
 *
 * attacker.getLocation().toVector()).normalize().multiply(knockback)));
 *
 * @author christopherjohnson
 */
public class KnockbackSnowballLogic extends LingeringSnowballLogic<Entity> {

    private final double strength, poweredStrength;
    private Vector bounce;

    public KnockbackSnowballLogic(double strength) {
        this(strength, strength);
    }

    public KnockbackSnowballLogic(double strength, double poweredStrength) {
        this.strength = strength;
        this.poweredStrength = poweredStrength;
    }

    /**
     * This creates a knockback snowball logic given the material; we sometimes
     * use a subclass for special effects.
     *
     * @param weaponUsed The item being used with the snowball.
     * @return The new logic.
     */
    public static KnockbackSnowballLogic fromMaterial(Material weaponUsed) {
        switch (weaponUsed) {
            case BONE:
                return new KnockbackSnowballLogic(8);

            case FENCE:
                return new KnockbackSnowballLogic(10);

            case COBBLE_WALL:
                return new KnockbackSnowballLogic(16);

            case NETHER_FENCE:
                return new KnockbackSnowballLogic(24);

            default:
                return new KnockbackSnowballLogic(4);
        }
    }

    @Override
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        double effectiveStrength = info.power > 8 ? poweredStrength : strength;
        effectiveStrength *= info.power;

        // setuip 'velocity' field at the top, so linger can use it.
        bounce = target.getVelocity().clone();
        bounce.add(target.getLocation().toVector());
        bounce.subtract(snowball.getLocation().toVector());
        bounce.normalize();
        bounce.multiply(effectiveStrength);

        //power also determines how much air time you're getting. No power means little air time,
        double boost = Math.abs(bounce.getX()) + Math.abs(bounce.getZ());
        bounce.setY(boost * info.power);

        // lets lower this so it doesn't send things into the stratosphere!
        bounce.multiply(0.1);
        
        beginLinger(info, 2, 3, target);
        return super.damage(snowball, info, target, proposedDamage);
    }

    @Override
    protected boolean linger(SnowballInfo info, int counter, Entity target) {

        target.setVelocity(bounce);
        target.setFallDistance(0); // <-- sacrilege!
        return true;

    }
}

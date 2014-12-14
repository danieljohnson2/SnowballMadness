package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

/**
 * This logic handles snowballs that knock stuff back at you, forward like
 * bowling, and yanking back useful mobs only.
 *
 * @author christopherjohnson
 */
public class ComeAlongSnowballLogic extends SnowballLogic {

    private final Material trigger;

    public ComeAlongSnowballLogic(Material trigger) {
        this.trigger = Preconditions.checkNotNull(trigger);
    }

    @Override
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        Vector bounce = snowball.getVelocity().clone();
        double force = Math.sqrt(4.0 + info.power + info.speed);
        bounce.multiply(force);
        switch (trigger) {
            case OBSIDIAN:
                //everything bounces away
                break;
            case FISHING_ROD:
                bounce.setX(-(bounce.getX()));
                bounce.setZ(-(bounce.getZ()));
                //everything comes back atcha
                break;
            case CARROT_STICK:
                if (target instanceof Animals) {
                    bounce.setX(-(bounce.getX()));
                    bounce.setZ(-(bounce.getZ()));
                    //only animals come back atcha
                }
                break;
        }
        bounce.setY(0.999); //having a real problem getting anything to bounce UP much.
        //specifically, no matter how high I set this it ignores it.
        target.setVelocity(bounce);
        target.setFallDistance(0);
        return 0;
    }
}

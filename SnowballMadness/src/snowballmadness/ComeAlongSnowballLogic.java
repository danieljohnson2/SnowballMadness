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
public class ComeAlongSnowballLogic extends LingeringSnowballLogic<Entity> {

    private final Material trigger;
    private Vector bounce;

    public ComeAlongSnowballLogic(Material trigger) {
        this.trigger = Preconditions.checkNotNull(trigger);
    }

    @Override
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        bounce = snowball.getVelocity().clone();
        bounce.multiply(Math.pow(info.power,2) / 6.0);
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
        bounce.setY(Math.abs(bounce.getY())); //having a real problem getting anything to bounce UP much.
        //specifically, no matter how high I set this it ignores it.
        beginLinger(info, 2, 3, target);
        return super.damage(snowball, info, target, proposedDamage);
    }

    @Override
    protected boolean linger(SnowballInfo info, int counter, Entity target) {

        target.setVelocity(bounce.clone().add(target.getVelocity()));
        target.setFallDistance(0);
        return true;

    }
}

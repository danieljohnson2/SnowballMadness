package snowballmadness;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.util.*;

/**
 * This logic replaces the snowball itself with a different object that gets the
 * same velocity and position.
 *
 * @author DanJ
 */
public class ProjectileSnowballLogic extends SnowballLogic {

    private final Material trigger;

    public ProjectileSnowballLogic(Material trigger) {
        this.trigger = Preconditions.checkNotNull(trigger);
    }

    @Override
    public void launch(Snowball snowball, SnowballInfo info) {
        super.launch(snowball, info);

        World world = snowball.getWorld();
        Vector velocity = snowball.getVelocity();
        Location location = snowball.getLocation().clone();
        location.add(velocity.normalize().multiply(2));
        float speed = (float) (info.speed);
        float power = (float) (info.power);

        switch (trigger) {
            case BLAZE_ROD:
                power = power + 2;
                //default blaze rod more powerful than firecharge,
                //quicker to produce fires, one glowstone block will do it
                //plain firework charge not very damaging without boosting
            case FIREWORK_CHARGE: {
                Fireball fireball = world.spawn(location, Fireball.class);
                fireball.setShooter(snowball.getShooter());
                fireball.setYield(power);
                if (power > 3) {
                    fireball.setIsIncendiary(true);
                } else {
                    fireball.setIsIncendiary(false);
                }
                velocity.multiply(speed);
                fireball.setVelocity(velocity);
                break; //the fun one, fireball casting
            }
            case EGG: {
                Projectile projectile = world.spawn(location, Egg.class);
                projectile.setShooter(snowball.getShooter());
                velocity.normalize();
                velocity.multiply(0.1);
                projectile.setVelocity(velocity);
                break; //eggs, endless eggs!

            }
            case EXP_BOTTLE: {
                Projectile projectile = world.spawn(location, ThrownExpBottle.class);
                projectile.setShooter(snowball.getShooter());
                velocity.normalize();
                velocity.multiply(0.1);
                projectile.setVelocity(velocity);
                break; //exp bottles forever
            }
        }
        snowball.remove();
    }
}

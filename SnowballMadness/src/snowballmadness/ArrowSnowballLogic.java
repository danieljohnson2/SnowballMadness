package snowballmadness;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.*;
import org.bukkit.projectiles.*;

/**
 *
 * @author DanJ
 */
public class ArrowSnowballLogic extends SnowballLogic {

    @Override
    public void launch(Snowball snowball, SnowballInfo info) {
        super.launch(snowball, info);
        World world = snowball.getWorld();
        Location location = snowball.getLocation().clone();
        Vector velocity = snowball.getVelocity();

        location.add(velocity.normalize().multiply(1.5));
        //adjusting shot position so you can't run into it while firing
        float speed = (float) Math.sqrt(info.power) + 2.0f;
        snowball.getWorld().playEffect(location, Effect.BOW_FIRE, null, 128);
        if (speed > 4.0) {
            snowball.getWorld().playEffect(location, Effect.BOW_FIRE, null, 128);
            if (speed > 8.0) {
                snowball.getWorld().playEffect(location, Effect.BOW_FIRE, null, 128);
            }
        } //by doing it this way, we are stacking bow shots on top of each other to make it louder

        Arrow arrow = world.spawnArrow(location, velocity, speed, 0);
        arrow.setShooter((ProjectileSource) snowball.getShooter());
        snowball.remove();
    }
}

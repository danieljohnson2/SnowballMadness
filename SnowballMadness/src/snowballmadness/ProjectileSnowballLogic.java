package snowballmadness;

import com.google.common.base.Preconditions;
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

    private final Class<? extends Projectile> projectileClass;

    public ProjectileSnowballLogic(Class<? extends Projectile> projectileClass) {
        this.projectileClass = Preconditions.checkNotNull(projectileClass);
    }

    @Override
    public void launch(Snowball snowball, SnowballInfo info) {
        super.launch(snowball, info);

        World world = snowball.getWorld();
        Vector velocity = snowball.getVelocity();
        float speed = (float) (info.speed);
        velocity.multiply(speed);

        Projectile projectile = world.spawn(snowball.getLocation(), projectileClass);
        projectile.setShooter(snowball.getShooter());
        projectile.setVelocity(velocity);

        snowball.remove();
    }
}

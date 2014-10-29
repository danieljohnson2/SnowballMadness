/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.Preconditions;
import org.bukkit.World;
import org.bukkit.entity.*;

/**
 *
 * @author DanJ
 */
public class ProjectileSnowballLogic extends SnowballLogic {

    private final Class<? extends Projectile> projectileClass;

    public ProjectileSnowballLogic(Class<? extends Projectile> projectileClass) {
        this.projectileClass = Preconditions.checkNotNull(projectileClass);
    }

    @Override
    public void launch() {
        super.launch();

        World world = getWorld();
        Snowball snowball = getSnowball();

        Projectile arrow = world.spawn(snowball.getLocation(), projectileClass);
        arrow.setVelocity(snowball.getVelocity());

        snowball.remove();
    }
}

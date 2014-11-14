package snowballmadness;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.util.*;

/**
 *
 * @author DanJ
 */
public class ArrowSnowballLogic extends SnowballLogic {

    private final int arrowCount;

    public ArrowSnowballLogic(ItemStack arrow) {
        this.arrowCount = Math.max(1, arrow.getAmount() / 8);
    }

    @Override
    public void launch(Snowball snowball, SnowballInfo info) {
        super.launch(snowball, info);
        World world = snowball.getWorld();

        Location location = snowball.getLocation().clone();
        Vector velocity = snowball.getVelocity();

        // little tweak- move the arrow a little futher out so it
        // does not interact with the shooter's hitbox on the client
        // side. If that happens, the client sees the arrow drop to
        // the ground.

        location.add(velocity);

        float speed = (float) (info.speed);

        if (info.power > 1.0) {
            for (int i = 0; i < arrowCount; ++i) {
                float spread = (float) ((arrowCount * 8) / info.power);
                Arrow arrow = world.spawnArrow(location, velocity, speed, spread);
                arrow.setShooter(snowball.getShooter());
                arrow.setFireTicks((int) (info.power * 50));
            }
        } else {
            Arrow arrow = world.spawnArrow(location, velocity, speed, 0);
            arrow.setShooter(snowball.getShooter());
        }
        snowball.remove();
    }
}

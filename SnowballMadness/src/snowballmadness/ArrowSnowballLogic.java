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

        Vector velocity = snowball.getVelocity();
        float speed = (float) (info.speed);

        if (info.power > 1.0) {
            for (int i = 0; i < arrowCount; ++i) {
                float spread = (float) ((arrowCount * 8) / info.power);
                Arrow arrow = world.spawnArrow(snowball.getLocation(), velocity, speed, spread);
                arrow.setShooter(snowball.getShooter());
                arrow.setFireTicks((int) (info.power * 50));
            }
        } else {
            Arrow arrow = world.spawnArrow(snowball.getLocation(), velocity, speed, 0);
            arrow.setShooter(snowball.getShooter());
        }
        snowball.remove();
    }
}

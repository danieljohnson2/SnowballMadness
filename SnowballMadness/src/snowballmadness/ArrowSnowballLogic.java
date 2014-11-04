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
        float speed = (float) (2.5 * info.speed);
        float spread = (float) ((arrowCount * 8) / info.power);

        for (int i = 0; i < arrowCount; ++i) {
            Arrow arrow = world.spawnArrow(snowball.getLocation(), velocity, speed, spread);
            arrow.setShooter(snowball.getShooter());

            if (info.power > 1.0) {
                arrow.setFireTicks((int) (info.power * 50));
            }
        }

        snowball.remove();
    }
}

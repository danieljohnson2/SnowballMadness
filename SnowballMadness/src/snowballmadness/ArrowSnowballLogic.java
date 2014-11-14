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

        location.add(velocity.normalize().multiply(1.5));
        //adjusting shot position so you can't run into it while firing

        float speed = (float) (info.speed);

        if (speed > 1.7) {
            snowball.getWorld().playEffect(location, Effect.BOW_FIRE, null, 128);
            if (speed > 3.2) {
                snowball.getWorld().playEffect(location, Effect.BOW_FIRE, null, 128);
                if (speed > 4.7) {
                    snowball.getWorld().playEffect(location, Effect.BOW_FIRE, null, 128);
                    if (speed > 8.9) {
                        snowball.getWorld().playEffect(location, Effect.BOW_FIRE, null, 128);
                    }
                }
            }
            //bow or better. Sound effect.
            //by doing it this way, we are stacking bow shots on top of each other.
            //that overrides the limitations on how loud it can be in Minecraft, producing
            //a gunshot-like effect.
        }

        if (info.power > 1) {
            snowball.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, null, 128);
            //any power that causes arrows to be on fire
        }
        //special effects give feedback when the arrow is extreme: fast, or highpowered

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
/*   @Override
 public void onProjectileHit(ProjectileHitEvent event) {
 Entity entity = event.getEntity();
 if (entity.getType() == EntityType.ARROW) {
 entity.remove();
 }
 }  //this is outside what I understand, but was somebody's code for
 // removing arrows stuck in the ground. I'd like our arrows to not
 // leave tile entities stuck to blocks. More anti-lag cleanup.
 }
 } */

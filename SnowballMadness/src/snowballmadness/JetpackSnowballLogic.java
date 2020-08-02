package snowballmadness;

import org.bukkit.Effect;
import org.bukkit.entity.*;
import org.bukkit.util.*;
import org.bukkit.Location;
import org.bukkit.projectiles.*;
import org.bukkit.potion.*;

/**
 * This class provides a 'jetpack' effect, where the snowball goes down, and the player who throws it is boosted up.
 *
 * @author DanJ
 */
public class JetpackSnowballLogic extends SnowballLogic {

    @Override
    public void launch(Snowball snowball, SnowballInfo info) {
        super.launch(snowball, info);

        ProjectileSource psource = snowball.getShooter();
        if (psource instanceof LivingEntity) {
            LivingEntity shooter = (LivingEntity) psource;
            double delta = Math.sqrt(info.power) + 1.0;
             //any form of powering will crank this up
            snowball.remove();
            Vector velocity = shooter.getVelocity().clone();
            velocity.add(shooter.getLocation().getDirection().multiply(delta));
            //this gives us a kick in the direction we're looking           
            shooter.setVelocity(shooter.getVelocity().getMidpoint(velocity));
            shooter.setFallDistance(0);
            //we will also keep resetting the player's fall distance. This should help
            //minimize accumulated damage, and is a key part of the mechanic.
        }
    }
}

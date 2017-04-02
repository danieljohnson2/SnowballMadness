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
            Location location = shooter.getLocation();

            double delta = info.power + 1.0;
            double ground;
            //any form of powering will crank this up
            snowball.remove();

            double groundEffect = location.getY();
            double groundLevel = shooter.getWorld().getHighestBlockYAt(location);
            groundEffect = groundEffect - groundLevel;
            //now we have a value that's either positive (we are flying) or negative (underground)
            if (groundEffect < 1) {
                groundEffect = 1;
            }
            //so it will always at least be one but can become a lot higher
            ground = delta / groundEffect;
            //and we divide delta by that so we won't rocket off into the sky too wildly
            ground = Math.sqrt(ground) + 2.0;
            //and cut back the intensity of near-ground boosts to avoid catapulting upwards.

            Vector velocity = shooter.getVelocity().clone();
            velocity.add(shooter.getLocation().getDirection().multiply(delta));
            //this gives us a kick in the direction we're looking

            double speedY = velocity.getY();
            if (speedY > 0) {
                speedY = Math.sqrt(speedY) * 0.5;
            }
           if (speedY < 0) {
                speedY = -Math.cbrt(-speedY) * 0.5;
            }
            velocity.setY(Math.max(0.0, speedY + ground));
            //we can take off pointing down, or hover: to go down, jetpack less.

            shooter.setVelocity(shooter.getVelocity().getMidpoint(shooter.getVelocity().getMidpoint(velocity)));
            //smooth the abrupt change of direction. This also helps landings.
            //We could almost restore the fall damage at this rate!
            
            shooter.setFallDistance(0);
            //we will also keep resetting the player's fall distance. This should help
            //minimize accumulated damage, and is a key part of the mechanic.
        }
    }
}

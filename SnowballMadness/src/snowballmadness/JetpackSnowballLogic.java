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
 
            double delta = info.power;
            double ground;
            //any form of powering will crank this up
            snowball.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, null, 128);
            snowball.remove();
            //never mind the snowballs, we rocket on trails of fire!
            //to improve this, fill in all blocks traversed with a string of mobspawner flames

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
            ground = Math.sqrt(ground);
            //and cut back the intensity of near-ground boosts to avoid catapulting upwards.

            Vector velocity = shooter.getVelocity().clone();
            velocity.setY(Math.min(8, velocity.getY() + ground));
            //to go up more than that, aim up. however we will tend to pull out of dives well
            //this way. All about creating the easy cruise experience in the jetpack.
            velocity.add(shooter.getLocation().getDirection().multiply(delta));
            //this gives us a kick in the direction we're looking, plus the up-boost
            //increasing power also increases the velocity with which we race about
            //more power means more possible velocity, but also more difficulty handling
            //the overpowered jetpack. You can always fire just when about to hit, but
            //if it flings you into the air again that's not much help.
            double dexpLevel = Math.sqrt(delta);
            if (velocity.length() > dexpLevel) {
                velocity = velocity.normalize().multiply(dexpLevel);
            }
            //bailout for exponential velocity madness

            shooter.removePotionEffect(PotionEffectType.JUMP);
            shooter.setVelocity(velocity);
            if (shooter.getFallDistance() > 0.001) {
                shooter.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, (int)(delta * delta * 10.0), (int)(delta * delta * 10.0)));
            } //only if we're actually in air do we have the jump boost
            shooter.setFallDistance(0);
            //we will also keep resetting the player's fall distance. This should help
            //minimize accumulated damage, and is a key part of the mechanic.
        }
    }
}

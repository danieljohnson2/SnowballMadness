/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.*;

/**
 * This logic throws the player instead of the snowball.
 *
 * @author DanJ
 */
public class ReversedSnowballLogic extends SnowballLogic {

    @Override
    public void launch(Snowball snowball, SnowballInfo info) {
        super.launch(snowball, info);

        LivingEntity shooter = snowball.getShooter();
        
        Location snowballLoc = snowball.getLocation().clone();
        snowballLoc.setY(snowballLoc.getY() - 1);
        snowballLoc.setDirection(shooter.getLocation().getDirection());

        Vector velocity = snowball.getVelocity().clone();
        velocity = velocity.multiply(info.amplification);

        if (snowballLoc.distance(shooter.getLocation()) < 2) {
            shooter.teleport(snowballLoc);
        }

        shooter.setVelocity(velocity);

        snowball.remove();
    }
}

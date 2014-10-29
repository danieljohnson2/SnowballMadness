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
        super.launch(snowball, info); //To change body of generated methods, choose Tools | Templates.

        Location snowballLoc = snowball.getLocation().clone();
        snowballLoc.setY(snowballLoc.getY() - 1);
        snowballLoc.setDirection(info.shooter.getLocation().getDirection());

        Vector velocity = snowball.getVelocity().clone();
        velocity = velocity.multiply(info.amplification);

        if (snowballLoc.distance(info.shooter.getLocation()) < 2) {
            info.shooter.teleport(snowballLoc);
        }

        info.shooter.setVelocity(velocity);

        snowball.remove();
    }
}

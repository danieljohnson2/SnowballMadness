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
public class InvertedSnowballLogic extends SnowballLogic {

    public InvertedSnowballLogic(Snowball snowball) {
        super(snowball);
    }

    @Override
    public void launch() {
        super.launch();

        Snowball sb = getSnowball();
        LivingEntity shooter = sb.getShooter();

        Location snowballLoc = sb.getLocation().clone();
        snowballLoc.setY(snowballLoc.getY() - 1);
        snowballLoc.setDirection(shooter.getLocation().getDirection());

        Vector velocity = sb.getVelocity().clone();
        velocity.multiply(2);

        shooter.teleport(snowballLoc);
        shooter.setVelocity(velocity);
    }
}

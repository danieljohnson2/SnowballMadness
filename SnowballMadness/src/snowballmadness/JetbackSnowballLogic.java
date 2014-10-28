/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.entity.*;
import org.bukkit.util.*;

/**
 * This class provides a 'jetpack' effect, where the snowball goes down, and the
 * player who throws it is boosted up.
 *
 * @author DanJ
 */
public class JetbackSnowballLogic extends SnowballLogic {

    @Override
    public void launch() {
        super.launch();

        Snowball sb = getSnowball();
        LivingEntity shooter = sb.getShooter();

        sb.setVelocity(new Vector(0, -0.5, 0));

        Vector v = shooter.getVelocity().clone();
        v.setY(v.getY() + 0.5);
        shooter.setVelocity(v);
    }
}

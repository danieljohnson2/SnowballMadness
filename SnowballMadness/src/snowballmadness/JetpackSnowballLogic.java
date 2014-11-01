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
public class JetpackSnowballLogic extends SnowballLogic {

    @Override
    public void launch(Snowball snowball, SnowballInfo info) {
        super.launch(snowball, info);
    
        LivingEntity shooter = snowball.getShooter();

        double delta = info.amplification / 2.0;

        snowball.setVelocity(new Vector(0, -delta, 0));

        Vector v = shooter.getVelocity().clone();
        v.setY(Math.min(64, v.getY() + delta));
        shooter.setVelocity(v);
    }
}

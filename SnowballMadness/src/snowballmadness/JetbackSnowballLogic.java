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

    private double amplification = 1.0;

    @Override
    protected void applyAmplification(double amplification) {
        super.applyAmplification(amplification);
        this.amplification *= amplification;
    }

    @Override
    public void launch() {
        super.launch();

        Snowball sb = getSnowball();
        LivingEntity shooter = getShooter();

        double delta = amplification / 2.0;

        sb.setVelocity(new Vector(0, -delta, 0));

        Vector v = shooter.getVelocity().clone();
        v.setY(Math.min(64, v.getY() + delta));
        shooter.setVelocity(v);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.*;
import org.bukkit.entity.*;

/**
 * This logic stops the rain. That's all it does.
 *
 * @author christopherjohnson
 */
public class StopRainLogic extends SnowballLogic {

    private final static CooldownTimer<Object> cooldown = new CooldownTimer<Object>(100);

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        if (cooldown.check("")) {
            super.hit(snowball, info);

            World world = snowball.getWorld();

            world.setWeatherDuration(1);
            world.setThunderDuration(1);
            world.setStorm(false);
            world.setThundering(false);
            world.setWeatherDuration(2400);
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.*;
import org.bukkit.entity.*;

/**
 * This logic kicks the server time forward an amount based on how powered the
 * snowball is. That's all it does.
 *
 * @author christopherjohnson
 */
public class WatchSnowballLogic extends SnowballLogic {

    private final static CooldownTimer<Object> cooldown = new CooldownTimer<Object>(100);

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        if (cooldown.check("")) {
            World world = snowball.getWorld();

            world.setTime((long) (world.getTime() + (1500.0 * info.power)));
            //powering the clock lets you set it quicker.
        }
    }
}

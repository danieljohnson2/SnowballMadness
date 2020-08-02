package snowballmadness;

import org.bukkit.*;
import org.bukkit.entity.*;

/**
 * This logic kicks the server time forward an amount based on how powered the snowball is. That's all it does.
 *
 * @author christopherjohnson
 */
public class WatchSnowballLogic extends SnowballLogic {

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        World world = snowball.getWorld();
        world.setTime((long) (world.getTime() + (100.0 * Math.sqrt(info.power))));
    }
}

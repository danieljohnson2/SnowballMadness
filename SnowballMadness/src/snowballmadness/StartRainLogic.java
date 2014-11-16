package snowballmadness;

import org.bukkit.*;
import org.bukkit.entity.*;

/**
 * This logic starts rain. If powered, it starts thunder and extends its
 * duration. That's all it does.
 *
 * @author christopherjohnson
 */
public class StartRainLogic extends SnowballLogic {

    private final static CooldownTimer<Object> cooldown = new CooldownTimer<Object>(100);

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        if (cooldown.check("")) {
            super.hit(snowball, info);
        }

        World world = snowball.getWorld();

        world.setWeatherDuration(1);
        world.setStorm(true);
        world.setWeatherDuration(1200);
        
        if (info.power > 8) {
            world.setThundering(true);
            world.setThunderDuration(1200);
        }
    }
}

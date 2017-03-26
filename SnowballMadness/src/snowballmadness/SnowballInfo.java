package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Player;

/**
 * This class holds onto auxiliary information about a snowball; we pass this to all the SnowballLogic methods along with the
 * snowball itself; it's essentially is a parameter block.
 *
 * @author DanJ
 */
public final class SnowballInfo {

    /**
     * This is the plugin being run.
     */
    public final SnowballMadness plugin;
    /**
     * This is the place where the snowball was launched from.
     */
    public final Location launchLocation;
    /**
     * This is the player who launched it.
     */
    public final Player shooter;
    /**
     * This gives the logic your EXP. Used to scale many magic effects
     */
    public final double power;

    public SnowballInfo(SnowballMadness plugin, Location launchLocation, Player shooter) {
        this.plugin = Preconditions.checkNotNull(plugin);
        this.launchLocation = launchLocation.clone();
        this.shooter = shooter.getPlayer();
        this.power = Math.max(1.0, Math.sqrt(shooter.getLevel()));
    }

    private SnowballInfo(double speed, double power, SnowballInfo original) {
        this(speed, power, original.launchLocation, original.shooter, original);
    }

    private SnowballInfo(double speed, double power, Location launchLocation, Player shooter, SnowballInfo original) {
        this.plugin = original.plugin;
        this.launchLocation = launchLocation.clone();
        this.shooter = shooter.getPlayer();
        this.power = Math.max(1.0, Math.sqrt(shooter.getLevel()));
    }
}

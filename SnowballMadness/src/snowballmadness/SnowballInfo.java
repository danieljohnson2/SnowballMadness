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
     * This is a modifier on the speed of a snowball; it makes snowballs fly faster.
     */
    public final double speed;
    /**
     * This is a modifier on the power of a snowball; different snowballs treat this differently.
     */
    public final double power;
    /**
     * This is set to true if this snowball should log activity messages.
     */
    public final boolean shouldLogMessages;

    public SnowballInfo(SnowballMadness plugin, Location launchLocation, Player shooter) {
        this.plugin = Preconditions.checkNotNull(plugin);
        this.shouldLogMessages = plugin.shouldLogSnowballs();
        this.speed = 1.0;
        this.power = 1.0;
        this.launchLocation = launchLocation.clone();
        this.shooter = shooter.getPlayer();
    }

    private SnowballInfo(double speed, double power, SnowballInfo original) {
        this(speed, power, original.launchLocation, original.shooter, original);
    }

    private SnowballInfo(double speed, double power, Location launchLocation, Player shooter, SnowballInfo original) {
        this.plugin = original.plugin;
        this.shouldLogMessages = original.shouldLogMessages;
        this.speed = speed;
        this.power = power;
        this.launchLocation = launchLocation.clone();
        this.shooter = shooter.getPlayer();


    }

    /**
     * This returns a new info whose speed has been scaled by the factor given.
     *
     * @param factor The factor by which we adjust the speed.
     * @return A new info object.
     */
    public SnowballInfo speeded(double factor) {
        return new SnowballInfo(speed * factor, power, this);
    }

    /**
     * This returns a new info whose power has been scaled by the factor given.
     *
     * @param factor The factor by which we adjust the power.
     * @return A new info object.
     */
    public SnowballInfo powered(double factor) {
        return new SnowballInfo(speed, power * factor, this);
    }
}

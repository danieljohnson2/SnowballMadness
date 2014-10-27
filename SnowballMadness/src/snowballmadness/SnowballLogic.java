/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import java.util.*;
import com.google.common.base.*;
import com.google.common.collect.*;

import org.bukkit.*;
import org.bukkit.entity.*;

/**
 *
 * @author DanJ
 */
public abstract class SnowballLogic {

    private final static Map<Snowball, SnowballLogic> inFlight = Maps.newHashMap();
    private final Snowball snowball;

    public SnowballLogic(Snowball snowball) {
        this.snowball = Preconditions.checkNotNull(snowball);
    }

    public final World getWorld() {
        return snowball.getWorld();
    }

    public final Snowball getSnowball() {
        return snowball;
    }

    public void hit() {
    }

    public static SnowballLogic getLogic(Snowball snowball) {
        return inFlight.get(snowball);
    }

    public void start() {
        inFlight.put(getSnowball(), this);
        Bukkit.getLogger().info(String.format("Snowball launched: %d", inFlight.size()));
    }

    public void end() {
        inFlight.remove(getSnowball());
        Bukkit.getLogger().info(String.format("Snowball hit: %d", inFlight.size()));
    }

    public static SnowballLogic createLogic(Snowball snowball, Material hint) {
        switch (hint) {
            case TNT:
                return new TNTSnowballLogic(snowball);

            default:
                return null;
        }
    }
}

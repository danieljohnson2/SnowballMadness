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
import org.bukkit.inventory.ItemStack;

/**
 * This class is the base class that hosts the logic that triggers when a
 * snowball hits a target.
 *
 * @author DanJ
 */
public abstract class SnowballLogic {

    private final static Map<Snowball, SnowballLogic> inFlight = Maps.newHashMap();
    private final Snowball snowball;

    public SnowballLogic(Snowball snowball) {
        this.snowball = Preconditions.checkNotNull(snowball);
    }

    /**
     * This provides the world the snowball is in.
     *
     * @return The world the snowball is in.
     */
    public final World getWorld() {
        return snowball.getWorld();
    }

    /**
     * This returns the snowball being tracked.
     *
     * @return The snowball whose logic this is.
     */
    public final Snowball getSnowball() {
        return snowball;
    }

    /**
     * This is called when the snowball hits something.
     */
    public void hit() {
    }

    /**
     * This returns the logic for a snowball that has one.
     *
     * @param snowball The snowball of interest.
     * @return The logic of the snowball, or none if it is an illogical
     * snowball.
     */
    public static SnowballLogic getLogic(Snowball snowball) {
        return inFlight.get(snowball);
    }

    /**
     * This method registers the logic so getLogic() can find it. Logics only
     * work once started.
     */
    public void start() {
        inFlight.put(getSnowball(), this);
        Bukkit.getLogger().info(String.format("Snowball launched: %d", inFlight.size()));
    }

    /**
     * This method unregisters this logic so it is no longer invoked; this is
     * done when snowball hits something.
     */
    public void end() {
        inFlight.remove(getSnowball());
        Bukkit.getLogger().info(String.format("Snowball hit: %d", inFlight.size()));
    }

    /**
     * This method creates a new logic, but does not start it. It chooses the
     * logic based on 'hint', which is the stack immediately above the snowball
     * being thrown.
     *
     * @param snowball The snowball whose logic this will be.
     * @param hint The stack above the snowball in the inventory; may be null.
     * @return The new logic, not yet started, or null if the snowball will be
     * illogical.
     */
    public static SnowballLogic createLogic(Snowball snowball, ItemStack hint) {
        if (hint == null) {
            return null;
        }

        switch (hint.getType()) {
            case TNT:
                return new TNTSnowballLogic(snowball);

            default:
                return null;
        }
    }
}

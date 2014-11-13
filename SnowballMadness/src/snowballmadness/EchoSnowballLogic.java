package snowballmadness;

import com.google.common.base.Preconditions;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.*;

/**
 * This is just wrong. Attempts are made to moderate the brutal savagery. Echo
 * snowballs are what you get with fermented spider eyes. They are like
 * multiplier snowballs, except they reflect right back at you. AND, they stack
 * to 64... and can take a double stack. That's 4096 from ONE shot. You pretty
 * much have to be in creative mode. I figure anyone who makes that many
 * survival fermented spider eyes deserves to take the server down, and if they
 * are using anything destructive they will certainly take themselves out with
 * it, most likely.
 *
 * @author christopherjohnson
 */
public class EchoSnowballLogic extends SnowballLogic {

    private final static CooldownTimer<Object> cooldown = new CooldownTimer<Object>(8);
    private final static int inFlightSnowballLimit = 256;
    private final int numberOfSnowballs;
    private final InventorySlice inventory;

    public EchoSnowballLogic(int numberOfSnowballs, InventorySlice inventory) {
        this.numberOfSnowballs = numberOfSnowballs;
        this.inventory = Preconditions.checkNotNull(inventory);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        World world = snowball.getWorld();
        ProjectileSource shooter = snowball.getShooter();
        Location source = snowball.getLocation().clone();
        source.setY(source.getY() + 0.25);

        Vector bounce = snowball.getVelocity().clone();
        bounce.setX(-(bounce.getX()));
        bounce.setY(-(bounce.getY()));
        bounce.setZ(-(bounce.getZ()));
        //comin' back atcha!

        bounce.multiply(1.2);
        //slight boost in case the person is trying to stay at a safe distance
        //could also be a large boost for insanity purposes?

        Snowball skipper = world.spawn(source, Snowball.class);
        skipper.setShooter(shooter);
        skipper.setVelocity(bounce);

        performLaunch(inventory, skipper, info);
        //the purpose of this change is to make the first one in the stack always
        //bounce like a skipping rock, for better distance shots and ICBMs
        //successive snowballs will be directed increasingly randomly

        for (int i = 1; i < numberOfSnowballs; ++i) {
            if ((source.getY() <= i) || (getInFlightCount() >= inFlightSnowballLimit)) {
                //bail, it's already beyond redonkulous. Useless going over 1000
                //though I have seen 6000/7000 uncontrolled
                //also note, if we have loads of snowballs down around bedrock
                //we start not bothering with them, what more can they do?
            } else {
                Snowball secondary = world.spawn(source, Snowball.class);

                Vector vector = Vector.getRandom();
                vector.setX(vector.getX() - 0.5);
                vector.setY(vector.getY() - 0.5);
                vector.setZ(vector.getZ() - 0.5);

                vector.multiply(new Vector(info.speed * 8, info.speed * 8, info.speed * 8));

                //now we will interpolate between that and bounce.
                double highvalues = i / 256.0; //if this doesn't return a fractional value it won't work
                double lowvalues = 1.0 - highvalues;
                vector.multiply(highvalues);
                //we've just scaled back our randomness based on how near the snowball number
                //is to 16: lower i numbers make the random component low
                vector.add(bounce.multiply(lowvalues));
                //and we add bounce scaled to the inverse of that amount. Lower i numbers make the
                //bounce component high. as you keep adding more i you get more randomness and scatter.

                secondary.setShooter(shooter);
                secondary.setVelocity(vector);

                performLaunch(inventory, secondary, info);
            }
        }

    }

    @Override
    public String toString() {
        return String.format("%s -> (x%d) %s",
                super.toString(),
                numberOfSnowballs,
                createLogic(inventory));
    }
}

package snowballmadness;

import com.google.common.base.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;

/**
 * This logic drops an item at the point of impact; you can provide several
 * materials, and it chooses randomly between them. You can also provide a
 * chance to spawn, a number for 0 to 1.0; this is the chance that you get
 * anything, where 1.0 is 100%.
 *
 * You can further subclass this to provide an entity spawn as well, which is
 * awkward in itself but keeps the constructors clean.
 *
 * @author DanJ
 */
public class ItemDropSnowballLogic extends SnowballLogic {

    private static final Random itemPickRandom = new Random();
    private final Material[] dropabbleItems;
    private final double chanceToDrop;

    public ItemDropSnowballLogic(Material... dropabbleItems) {
        this(1.0, dropabbleItems);
    }

    public ItemDropSnowballLogic(double chanceToDrop, Material... dropabbleItems) {
        this.chanceToDrop = chanceToDrop;
        this.dropabbleItems = Preconditions.checkNotNull(dropabbleItems);
    }

    /**
     * This method decides what entity to spawn; by default it returns null and
     * we spawn none. however, this lets you tack on an entity with your item
     * when desired.
     *
     * @param snowball The snowball whose impact spawns things.
     * @param info Info about the snowball.
     * @return The type of entity to spawn, or null for nothing.
     */
    protected EntityType getEntityToSpawn(Snowball snowball, SnowballInfo info) {
        return null;
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        if (chanceToDrop < 1.0 && itemPickRandom.nextDouble() > chanceToDrop) {
            // you failed your die role, you get nothing.
            return;
        }

        World world = snowball.getWorld();
        Location location = snowball.getLocation();

        int randomIndex = itemPickRandom.nextInt(dropabbleItems.length);
        Material itemDropped = dropabbleItems[randomIndex];

        if (itemDropped != null) {
            ItemStack stack = new ItemStack(itemDropped);
            int numberDropped = (int) Math.min(
                    info.power * info.power,
                    stack.getMaxStackSize());

            stack.setAmount(numberDropped);
            world.dropItem(location, stack);
        }

        EntityType entityDropped = getEntityToSpawn(snowball, info);

        if (entityDropped != null) {
            world.spawnEntity(location, entityDropped);
        }
    }
}

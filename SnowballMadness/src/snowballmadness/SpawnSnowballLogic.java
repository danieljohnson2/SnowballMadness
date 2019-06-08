package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This logic creates an entity at the point of impact. We can create a different, better entity for powered snowballs (to get
 * cave spiders instead of spiders for instance). You can specify the 'upgradePower'; the pumped up entity is spawned if the
 * snowball is more powerful than this; use 1.0 to indicate an upgrade for a snowball that is at all powered.
 *
 * We also have special handling for skulls; we can get powered creepers and such this way.
 *
 * @author DanJ
 */
public class SpawnSnowballLogic<TEntity extends Entity> extends SnowballLogic {

    private final Class<? extends TEntity> entityClass;

    public SpawnSnowballLogic(Class<? extends TEntity> entityClass) {
        this.entityClass = Preconditions.checkNotNull(entityClass);
    }

    /**
     * This method creates an instance of this logic; the use of a static method like this means the type argument T is inferred,
     * so you don't have to give the type twice.
     */
    public static <T extends Entity> SpawnSnowballLogic<T> fromEntityClass(Class<T> entityClass) {
        return new SpawnSnowballLogic<T>(entityClass);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        if (canSpawnAt(snowball.getLocation(), info)) {
            spawnEntity(snowball.getLocation(), info);
        } //resume nerfing smother traps
    }

    /**
     * This method actually creates the entity; it calls the overridable methods canSpawnAt() pickSpawnClass(), and
     * initializeEntity().
     *
     * @param location The place to spawn at.
     * @param info The snowball info in effect, if you need it.
     */
    private void spawnEntity(Location location, final SnowballInfo info) {
        World world = location.getWorld();
        Location adjusted = location.clone();
        Class<? extends TEntity> spawnClass = pickSpawnClass(adjusted, info);

        if (spawnClass != null) {
            if (world.getLivingEntities().size() > 1024) {
                //we check that once, not constantly
                int bailout = (int)Math.sqrt(world.getEntitiesByClass(spawnClass).size());
                //our bailout is more intense when the particular type being spammed is saturated
                for (Entity e : world.getEntitiesByClass(spawnClass)) {
                    e.remove();
                    bailout--;
                    if (bailout < 4) {
                        break;
                    } //every spawn is capable of removing many entities of the type being spawned
                } //from an overloaded pool of such entities. We don't try to delete the whole pool
            } //and we don't try to do anything intelligent because by definition we're lagged already.
            
            final TEntity spawned = world.spawn(adjusted, spawnClass);
            initializeEntity(spawned, info);
            new BukkitRunnable() {
                @Override
                public void run() {
                    equipEntity(spawned, info);
                }
            }.runTaskLater(info.plugin, 1);
        } //we will always spawn one, even if we've had to remove eight
    }

    /**
     * This method decides whether we can spawn the entity at the location given.
     *
     * @param location The location we proposed to spawn at.
     * @param info The snowball info of the snowball.
     * @return True to allow the spawn; false to spawn nothing.
     */
    protected boolean canSpawnAt(Location location, SnowballInfo info) {
        //we're only going to spawn an entity if the block is breathable space
        //nerfing too-trivially-easy suffocation mob farms
        Block above = location.getBlock().getRelative(BlockFace.UP);
        return above.isEmpty() || above.isLiquid();
    }

    /**
     * This method returns the entity type to spawn; we pick the powered version if the snowball is sufficiently powered.
     *
     * This method is also allowed to alter the location (it's a copy); this will change where the entity spawns.
     *
     * This method can return null to not spawn anything.
     *
     * @param location The place the snowball hit. This method may modify the location.
     * @param info The info for the snowball; tell us if the snowball was powered.
     * @return The entity type to spawn.
     */
    protected Class<? extends TEntity> pickSpawnClass(Location location, SnowballInfo info) {
        return entityClass;
    }

    /**
     * This method is called on the spawned entity to initialize it; if we decide not to spawn the entity, this method is never
     * called.
     *
     * @param spawned The newly spawned thing.
     * @param info The info of the snowball that spawned it.
     */
    protected void initializeEntity(TEntity spawned, SnowballInfo info) {
    }

    /**
     * This method is called on the spawned entity to populate it with equipment, if appropriate. If decide not to spawn the
     * entity, this method is never called. Unlike initializeEntity(), this method is called only after the entity has entered the
     * world (it is delayed by one tick)- we can't get equipment to 'stick' without this delay.
     *
     * @param spawned The newly spawned thing.
     * @param info The info of the snowball that spawned it.
     */
    protected void equipEntity(TEntity spawned, SnowballInfo info) {
    }
}
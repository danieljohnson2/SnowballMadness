package snowballmadness;

import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Snowball;

/**
 * This class detonates a TNT explosion at the point of impact of a snwoabll.
 *
 * @author DanJ
 */
public class TNTSnowballLogic extends SnowballLogic {

    private final float snowballSize;

    public TNTSnowballLogic(float snowballSize) {
        this.snowballSize = snowballSize;
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        snowball.getWorld().createExplosion(snowball.getLocation(), snowballSize);
        //boom!
        Entity[] entList = snowball.getWorld().getChunkAt(snowball.getLocation()).getEntities();
        for (Entity drop : entList) {
            if (drop instanceof Item) {
                //on explosion, nuke drops (and item frames?) in the chunk hit.
                //with massive multiplier TNT balls, we don't want to check the whole world
                //every single time. That's super wasteful. We reserve it for the high power explosives.
                drop.remove();
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%f)", super.toString(), snowballSize);
    }
}

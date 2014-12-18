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
        float size = (float) (snowballSize * info.power);
        snowball.getWorld().createExplosion(snowball.getLocation(), size);
        //boom!
        if (size < 8)  {
           Entity[] entList = snowball.getWorld().getChunkAt(snowball.getLocation()).getEntities();
            for (Entity drop : entList) {
                if (drop instanceof Item) {
                    //on explosion, nuke drops (and item frames?) in the chunk hit.
                    //with massive multiplier TNT balls, we don't want to check the whole world
                    //every single time. That's super wasteful. We reserve it for the high power explosives.
                    drop.remove();
                }
            }
        } else {
            List<Entity> entList = snowball.getWorld().getEntities();
            for (Entity drop : entList) {
                if ((drop instanceof Item) && (drop.getLocation().distance(snowball.getLocation()) < Math.pow(info.power, 3))) {
                    //on explosion, nuke drops (and item frames?) within a distance of the burst.
                    drop.remove();
                    //this is scaled so that the insane explosives tend to wipe all the drops in the area,
                    //but smaller levels and especially unpowered TNT still give drops. As you amp it up,
                    //the lag-boosting increasingly kicks in on the assumption that drops are no longer
                    //important.
                }
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%f)", super.toString(), snowballSize);
    }
}

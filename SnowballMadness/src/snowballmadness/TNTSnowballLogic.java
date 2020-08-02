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

    private final int boomSize;

    public TNTSnowballLogic(int boomSize) {
        this.boomSize = boomSize;
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        int scaled = (int) Math.sqrt(boomSize) + 1;
        for (int x = 0; x < boomSize; ++x) {
            snowball.getWorld().createExplosion(snowball.getLocation().add(0, x/8, 0), scaled);
            //move them so we can get rid of singleton blocks
        }
        //boom!

        /* List<Entity> entList = snowball.getWorld().getEntities();
        for (Entity drop : entList) {
            if ((drop instanceof Item) && (drop.getLocation().distance(snowball.getLocation()) < Math.pow(info.power, 3))) {
                //on explosion, nuke drops (and item frames?) within a distance of the burst.
                drop.remove();
                //this is scaled so that the insane explosives tend to wipe all the drops in the area,
                //but smaller levels and especially unpowered TNT still give drops. As you amp it up,
                //the lag-boosting increasingly kicks in on the assumption that drops are no longer
                //important.
            }
        }*/
    }

    @Override
    public String toString() {
        return String.format("%s (%f)", super.toString(), boomSize);
    }
}

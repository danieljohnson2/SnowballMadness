package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.*;

/**
 * This logic spawns a firework from various triggers such as flower types
 *
 * @author DanJ
 */
public class FireworkSnowballLogic extends SnowballLogic {

    private final Material baseItem;
    private final int variation;

    public FireworkSnowballLogic(ItemStack trigger) {
        this.baseItem = Preconditions.checkNotNull(trigger.getType());
        this.variation = Preconditions.checkNotNull(trigger.getDurability());
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        Location loc = snowball.getLocation().clone();
        Firework fw = (Firework) snowball.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta().clone();
        meta.setPower((int) Math.sqrt(info.speed));
        //height of firework relates to how fast you've fired it. Double beacon is
        //enough to go way up and curve over somewhat.

        meta.addEffect(FireworkEffect.builder().withColor(Color.RED, Color.ORANGE).
                with(FireworkEffect.Type.BALL).build());
        //example of a firework type to be fired depending on flower variation

        fw.setFireworkMeta(meta);
    }
}

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

        if (baseItem == Material.RED_ROSE) {
            switch (variation) {
                case 0: //poppy
                    meta.addEffect(FireworkEffect.builder().withColor(Color.RED).
                            with(FireworkEffect.Type.BALL_LARGE).build());
                    break;

                case 1: //blue orchid
                    meta.addEffect(FireworkEffect.builder().withColor(Color.BLUE).
                            with(FireworkEffect.Type.BURST).build());
                    break;

                case 2: //pink allium
                    meta.addEffect(FireworkEffect.builder().withColor(Color.FUCHSIA).
                            with(FireworkEffect.Type.BALL).build());
                    break;

                case 3: //white azure bluet
                    meta.addEffect(FireworkEffect.builder().withColor(Color.WHITE).
                            with(FireworkEffect.Type.BURST).build());
                    break;

                case 4: //red tulip
                    meta.addEffect(FireworkEffect.builder().withColor(Color.RED).
                            with(FireworkEffect.Type.BURST).build());
                    break;

                case 5: //orange tulip
                    meta.addEffect(FireworkEffect.builder().withColor(Color.ORANGE).
                            with(FireworkEffect.Type.BURST).build());
                    break;

                case 6: //white tulip
                    meta.addEffect(FireworkEffect.builder().withColor(Color.WHITE).
                            with(FireworkEffect.Type.BURST).build());
                    break;

                case 7: //pink tulip
                    meta.addEffect(FireworkEffect.builder().withColor(Color.FUCHSIA).
                            with(FireworkEffect.Type.BURST).build());
                    break;

                case 8: //oxeye daisy
                    meta.addEffect(FireworkEffect.builder().withColor(Color.WHITE).
                            with(FireworkEffect.Type.BALL).build());
                    break;
            }
        }
        if (baseItem == Material.YELLOW_FLOWER) {
            //dandelion
            meta.addEffect(FireworkEffect.builder().withColor(Color.YELLOW).
                    with(FireworkEffect.Type.BALL_LARGE).build());
        }
        fw.setFireworkMeta(meta);
    }
}

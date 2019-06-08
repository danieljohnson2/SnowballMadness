package snowballmadness;

import com.google.common.base.Preconditions;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.*;
import org.bukkit.projectiles.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author DanJ
 */
public class TippedArrowSnowballLogic extends SnowballLogic {

    private final ItemStack arrowUsed;

    public TippedArrowSnowballLogic(ItemStack arrowUsed) {
        this.arrowUsed = Preconditions.checkNotNull(arrowUsed);
    }

    @Override
    public void launch(Snowball snowball, SnowballInfo info) {
        super.launch(snowball, info);
        World world = snowball.getWorld();
        Location location = snowball.getLocation().clone();
        Vector velocity = snowball.getVelocity().multiply(info.power);


        location.add(velocity.normalize().multiply(1.5));
        //adjusting shot position so you can't run into it while firing
        float speed = (float) info.power;
        if (speed > 1.7) {
            snowball.getWorld().playEffect(location, Effect.BOW_FIRE, null, 128);
            if (speed > 3.2) {
                snowball.getWorld().playEffect(location, Effect.BOW_FIRE, null, 128);
                if (speed > 4.7) {
                    snowball.getWorld().playEffect(location, Effect.BOW_FIRE, null, 128);
                }
            } //by doing it this way, we are stacking bow shots on top of each other to make it louder
        }

        TippedArrow arrow = (TippedArrow) snowball.getWorld().spawnEntity(location, EntityType.TIPPED_ARROW);
        arrow.setVelocity(velocity);
        arrow.setShooter((ProjectileSource) snowball.getShooter());
        PotionMeta meta;
        PotionEffectType type;

        if (arrowUsed.getType() == Material.TIPPED_ARROW) {
            meta = (PotionMeta) arrowUsed.getItemMeta();
            type = meta.getBasePotionData().getType().getEffectType();
        } else {
            type = PotionEffectType.GLOWING;
            //spectral arrows can't use getItemMeta() or cast it to PotionMeta
        }

        arrow.addCustomEffect(new PotionEffect(type, 60 * 20, 1), true);
        //snowball tipped arrows are very badass and last for a minute

        snowball.remove();
    }
}

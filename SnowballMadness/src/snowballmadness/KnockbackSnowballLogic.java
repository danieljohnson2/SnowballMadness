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
import org.bukkit.util.Vector;

/**
 *
 * Entity attacker, attacked; float knockback; attacked.setVelocity(
 * attacked.getVelocity().add(attacked.getLocation().toVector().subtract(
 *
 * attacker.getLocation().toVector()).normalize().multiply(knockback)));
 *
 * @author christopherjohnson
 */
public class KnockbackSnowballLogic extends SnowballLogic {

    private final Material weaponUsed;

    public KnockbackSnowballLogic(Material weaponUsed) {
        this.weaponUsed = Preconditions.checkNotNull(weaponUsed);
        //we won't need a 'hit' method as we are overriding damage
    }

    @Override
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        if (target instanceof Entity) {


            double weapon = 4; //STICK is default

            if (weaponUsed == Material.BONE) {
                weapon = 8;
            }
            if (weaponUsed == Material.FENCE) {
                weapon = 10;
            }
            if (weaponUsed == Material.BLAZE_ROD) {
                weapon = 12;
                target.setFireTicks(target.getFireTicks() + (int) (100 * info.power));
            }
            if (weaponUsed == Material.COBBLE_WALL) {
                weapon = 16;
            }
            if (weaponUsed == Material.NETHER_FENCE) {
                weapon = 24;
            }
            if (weaponUsed == Material.FEATHER) {
                weapon = 2;
                if (info.power > 8) {
                    weapon = 32;
                    //you could have knocked me over with a feather!
                }
            }

            weapon = weapon * info.power;
            
            target.setVelocity(target.getVelocity().add(target.getLocation().toVector().subtract(snowball.getLocation().toVector()).normalize().multiply(weapon)));
            //this is ugly, I need to de-obfuscate it. Some guy on the internet likes making enormous composite calculations.
            //Initial attempts to de-obfuscate started to get unwanted results
           
            Vector lift = target.getVelocity().clone();
            double boost = Math.abs(lift.getX()) + Math.abs(lift.getZ());
            lift.setY(boost * info.power);
            target.setVelocity(lift);
            //power also determines how much air time you're getting. No power means little air time,

        }

        return 0;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;

/**
 * This is a base class for a logic that carries a second logic, which it
 * applies as will as any effects the subclass provides.
 *
 * @author DanJ
 */
public abstract class ChainableSnowballLogic extends SnowballLogic {

    public final SnowballLogic nextLogic;

    public ChainableSnowballLogic(InventorySlice nextSlice) {
        this.nextLogic = createLogic(nextSlice);
    }

    @Override
    public void launch(Snowball snowball, SnowballInfo info) {
        super.launch(snowball, info);

        if (nextLogic != null) {
            nextLogic.launch(snowball, adjustInfo(snowball, info));
        }
    }

    @Override
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        proposedDamage = super.damage(snowball, info, target, proposedDamage); //To change body of generated methods, choose Tools | Templates.

        if (nextLogic != null) {
            proposedDamage = nextLogic.damage(snowball, adjustInfo(snowball, info), target, proposedDamage);
        }

        return proposedDamage;
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        if (nextLogic != null) {
            nextLogic.hit(snowball, adjustInfo(snowball, info));
        }
    }

    protected SnowballInfo adjustInfo(Snowball snowball, SnowballInfo info) {
        return info;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", super.toString(), nextLogic);
    }
}

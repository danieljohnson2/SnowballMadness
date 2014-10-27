/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.entity.Snowball;

/**
 *
 * @author DanJ
 */
public class TNTSnowballLogic extends SnowballLogic {

    public TNTSnowballLogic(Snowball snowball) {
        super(snowball);
    }

    @Override
    public void hit() {
        super.hit();
        getWorld().createExplosion(getSnowball().getLocation(), 4);
    }
}

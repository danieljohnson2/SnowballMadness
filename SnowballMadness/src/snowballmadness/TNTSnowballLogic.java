/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.entity.Snowball;

/**
 * This class detonates a TNT explosion at the point of impact of a snwoabll.
 *
 * @author DanJ
 */
public class TNTSnowballLogic extends SnowballLogic {

    private final float snowballSize;

    public TNTSnowballLogic(Snowball snowball, float snowballSize) {
        super(snowball);
        this.snowballSize = snowballSize;
    }

    @Override
    public void hit() {
        super.hit();
        getWorld().createExplosion(getSnowball().getLocation(), snowballSize);
    }
}

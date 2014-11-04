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

    public TNTSnowballLogic(float snowballSize) {
        this.snowballSize = snowballSize;
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        float size = (float) (snowballSize * info.power);
        snowball.getWorld().createExplosion(snowball.getLocation(), size);
    }

    @Override
    public String toString() {
        return String.format("%s (%f)", super.toString(), snowballSize);
    }
}

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

    private float snowballSize;

    public TNTSnowballLogic(float snowballSize) {
        this.snowballSize = snowballSize;
    }

    @Override
    protected void applyAmplification(double amplification) {
        super.applyAmplification(amplification);

        this.snowballSize *= amplification;
    }

    @Override
    public void hit() {
        super.hit();
        getWorld().createExplosion(getSnowball().getLocation(), snowballSize);
    }

    @Override
    public String toString() {
        return String.format("%s (%f)", super.toString(), snowballSize);
    }
}

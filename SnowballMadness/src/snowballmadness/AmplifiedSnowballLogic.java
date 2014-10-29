/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

/**
 * This is a snowball that flies faster than nomrla, and can apply a second
 * logic that will be amplified too.
 *
 * @author DanJ
 */
public class AmplifiedSnowballLogic extends ChainableSnowballLogic {

    private double amplification;

    public AmplifiedSnowballLogic(double amplification, InventorySlice nextSlice) {
        super(makeAmplifiedSnowball(amplification, nextSlice));
        this.amplification = amplification;
    }

    @Override
    protected void applyAmplification(double amplification) {
        super.applyAmplification(amplification);
        this.amplification *= amplification;
    }

    @Override
    public void launch() {
        Snowball snowball = getSnowball();
        Vector vector = snowball.getVelocity().clone();
        vector.multiply(amplification);
        snowball.setVelocity(vector);

        super.launch();
    }

    private static SnowballLogic makeAmplifiedSnowball(double amplification, InventorySlice nextSlice) {
        SnowballLogic nextLogic = createLogic(nextSlice);

        if (nextLogic != null) {
            nextLogic.applyAmplification(amplification);
        }

        return nextLogic;
    }
}

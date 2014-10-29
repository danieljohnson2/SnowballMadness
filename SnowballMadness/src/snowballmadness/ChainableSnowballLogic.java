/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.Preconditions;

/**
 * This is a base class for a logic that carries a second logic, which it applies
 * as will as any effects the subclass provides.
 *
 * @author DanJ
 */
public abstract class ChainableSnowballLogic extends SnowballLogic {

    private final SnowballLogic nextLogic;

    public ChainableSnowballLogic(InventorySlice nextSlice) {
        this.nextLogic = createLogic(nextSlice);
    }

    public ChainableSnowballLogic(SnowballLogic nextLogic) {
        this.nextLogic = Preconditions.checkNotNull(nextLogic);
    }

    @Override
    protected void applyAmplification(double amplification) {
        super.applyAmplification(amplification);
        nextLogic.applyAmplification(amplification);
    }

    @Override
    public void launch() {
        super.launch();

        if (nextLogic != null) {
            try {
                nextLogic.setSnowball(getSnowball());
                nextLogic.setShooter(getShooter());
                nextLogic.launch();
            } finally {
                nextLogic.setSnowball(null);
            }
        }
    }

    @Override
    public void hit() {
        super.hit();

        if (nextLogic != null) {
            try {
                nextLogic.setSnowball(getSnowball());
                nextLogic.setShooter(getShooter());
                nextLogic.hit();
            } finally {
                nextLogic.setSnowball(null);
            }
        }
    }
}

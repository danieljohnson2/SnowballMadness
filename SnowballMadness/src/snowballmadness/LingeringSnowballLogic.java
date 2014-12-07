/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This is a logic used for snowballs that affect their target for a short time
 * after impact.
 *
 * The TData type parameter is the type of the data object that you pass to
 * beginLinger(), and it passed to linger(). Use Object here if you don't care
 * to use this feature.
 *
 * @author DanJ
 */
public abstract class LingeringSnowballLogic<TData> extends SnowballLogic {

    /**
     * This is called by the subclass to start the 'lingering' process; you
     * indicate how often linger() below is called; it is called at a rate
     * given, and a specific number of times.
     *
     * @param info The info that is passed to the linger() method.
     * @param tickRate The rate, in ticks (1/20ths of a second), at which to
     * call linger().
     * @param lingerCount The number of times to call linger().
     * @param data An additional object that is passed to linger().
     */
    protected final void beginLinger(final SnowballInfo info, int tickRate, final int lingerCount, final TData data) {
        if (lingerCount <= 0) {
            return;
        }

        new BukkitRunnable() {
            private int counter = 0;

            @Override
            public void run() {
                if (!linger(info, counter, data)) {
                    cancel();
                } else {
                    ++counter;

                    if (counter >= lingerCount) {
                        cancel();
                    }
                }
            }
        }.runTaskTimer(info.plugin, tickRate, tickRate);
    }

    /**
     * This method is called periodically after beginLinger() has been called.
     * This does not get a Snowball object because the snowball should be gone
     * by now.
     *
     * This method can return false to stop the lingering process early.
     *
     * @param info The info of the snowball that triggered this.
     * @param counter A counter that is 0 on the first call, 1 on the second,
     * etc.
     * @param data An additional data object given to beginLinger().
     * @return True to continue lingering, false to stop.
     */
    protected abstract boolean linger(SnowballInfo info, int counter, TData data);
}

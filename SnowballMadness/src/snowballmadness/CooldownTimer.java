/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import java.util.*;

/**
 *
 * @author DanJ
 */
public final class CooldownTimer<TKey> {

    private final HashMap<TKey, Long> regenTimeouts = new HashMap<TKey, Long>();
    private final long timeoutMillis;

    public CooldownTimer(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    /**
     * This method checks to see if we can safely regenerate a chunk. We keep a
     * weak map of timeouts, and we must be after this time to do so. This
     * method also updates that map with a new time, to be 8 seconds from now.
     *
     * @param chunk The chunk to check.
     * @return True if we should regenerate the chunk.
     */
    public boolean check(TKey key) {
        synchronized (regenTimeouts) {
            long now = System.currentTimeMillis();

            Long time = regenTimeouts.get(key);

            Iterator<Map.Entry<TKey, Long>> iter = regenTimeouts.entrySet().iterator();

            // remove any expired entries so we don't leak memory forever

            while (iter.hasNext()) {
                if (iter.next().getValue() <= now) {
                    iter.remove();
                }
            }

            if (time == null || time <= now) {
                regenTimeouts.put(key, now + timeoutMillis);
                return true;
            }

            return false;
        }
    }
}

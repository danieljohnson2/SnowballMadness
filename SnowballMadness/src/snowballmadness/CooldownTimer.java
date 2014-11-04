/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import java.util.*;

/**
 * This class tracks cooldowns, to prevent some effects from firing off too
 * often. For each key value, it keeps a deadline when the cooldown expires, and
 * until then the check() methods will return false.
 *
 * The intent here is that you keep a static field with one of these, so this
 * method is thread-safe.
 *
 * @author DanJ
 */
public final class CooldownTimer<TKey> {

    private final HashMap<TKey, Long> expirations = new HashMap<TKey, Long>();
    private final long timeoutMillis;

    public CooldownTimer(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    /**
     * This method checks to see if the cooldown has expired. This returns true
     * if it has, or if no cooldown is in effect at all- you can then do
     * whatever it is you wanted to do. If there's an outstanding cooldown, this
     * returns false and you should not do it.
     *
     * If this method returns true, it also starts a new cooldown; a second
     * consecutive call will return false because of this.
     *
     * @param key The key identifies which cooldown you want to check.
     * @return True if the cooldown has expired (or there was no cooldown).
     */
    public boolean check(TKey key) {
        synchronized (expirations) {
            long now = System.currentTimeMillis();

            Long time = expirations.get(key);

            if (time == null || time <= now) {
                // we know 'key' is no longer in cooldown, so we remove
                // it, and any other expired keys we can detect.
                
                expirations.remove(key);
                cleanup();

                expirations.put(key, now + timeoutMillis);
                return true;
            }

            return false;
        }
    }

    /**
     * This method removes any cooldowns that have expired, reclaiming memory.
     * We do this whenever we set up a new cooldown automatically.
     */
    public void cleanup() {
        long now = System.currentTimeMillis();

        synchronized (expirations) {
            if (!expirations.isEmpty()) {
                Iterator<Map.Entry<TKey, Long>> iter = expirations.entrySet().iterator();
                while (iter.hasNext()) {
                    if (iter.next().getValue() <= now) {
                        iter.remove();
                    }
                }
            }
        }
    }
}

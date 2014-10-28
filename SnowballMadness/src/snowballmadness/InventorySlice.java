/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.*;
import java.util.AbstractList;
import org.bukkit.inventory.*;

/**
 * This class represents a segment of a player's inventory, a vertical slice
 * that we read starting at the bottom. We can decide what logic to use by
 * examining the very first item of the slice, but use the skip() method to
 * generate a logic from the next.
 *
 * @author DanJ
 */
public final class InventorySlice extends AbstractList<ItemStack> {

    private static final InventorySlice EMPTY = new InventorySlice();
    private final PlayerInventory inventory;
    private final int x, y;

    private InventorySlice() {
        this.inventory = null;
        this.x = 0;
        this.y = 0;
    }

    private InventorySlice(PlayerInventory inventory, int x, int y) {
        this.inventory = Preconditions.checkNotNull(inventory);
        this.x = x;
        this.y = y;
    }

    /**
     * This returns an empty slice, that contains no items at all.
     *
     * @return A singleton empty slice.
     */
    public static InventorySlice empty() {
        return EMPTY;
    }

    /**
     * This produces a slice that starts that the inventory slot indicated by
     * the index, and proceeds up to the top of the inventory.
     *
     * Indexes 0-8 represent hotbar slots, and they include the slots above the
     * hotbar in the main inventory.
     *
     * @param inventory The player inventory to examine.
     * @param index The index to start at.
     * @return A new slice that starts the the indicated slot.
     */
    public static InventorySlice fromSLot(PlayerInventory inventory, int index) {
        if (index <= 8) {
            return new InventorySlice(inventory, index, 3);
        } else {
            int i = index - 9;
            return new InventorySlice(inventory, i % 9, i / 9);
        }
    }

    /**
     * This returns a slice that includes all the items in this one, but not the
     * bottommost 'count'. This can return an empty slice.
     *
     * If 'count' is 0 or negative, this method returns this slot unchanged. If
     * it is very large, you can get the singleton empty slice.
     *
     * @param count The number of item slots to skip.
     * @return A slice that does not include these slots.
     */
    public InventorySlice skip(int count) {
        if (count <= 0 || inventory == null) {
            return this;
        }

        int offsetY = y - count;

        if (offsetY >= 0) {
            return new InventorySlice(inventory, x, offsetY);
        } else {
            return empty();
        }
    }

    @Override
    public ItemStack get(int index) {
        if (inventory != null) {
            int offsetY = y - index;
            return inventory.getItem(getSlotIndex(x, offsetY));
        }

        throw new IllegalArgumentException("Index out of range.");
    }

    @Override
    public int size() {
        if (inventory == null) {
            return 0;
        }

        return y + 1;
    }

    /**
     * This method returns the Bukkit inventory slot number given the visual
     * position. y=3 gives the hotbar; y=0 the top of the main inventory.
     *
     * @param x The column index in the inventory; 0-9.
     * @param y The row index in the inventory; 0-3.
     * @return The Bukkit slot number.
     */
    private static int getSlotIndex(int x, int y) {
        if (y == 3) {
            return x;
        } else if (y >= 0 && y < 3) {
            return (y + 1) * 9 + x;
        }

        throw new IllegalArgumentException("Index out of range.");
    }
}

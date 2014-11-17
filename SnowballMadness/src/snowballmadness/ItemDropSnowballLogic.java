package snowballmadness;

import com.google.common.base.Preconditions;
import java.util.Random;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

/**
 * This logic drops an item at the point of impact.
 *
 * @author DanJ
 */
public class ItemDropSnowballLogic extends SnowballLogic {

    private final Material itemUsed;

    public ItemDropSnowballLogic(Material itemUsed) {
        this.itemUsed = Preconditions.checkNotNull(itemUsed);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);
        Material itemDropped = null; //sparse cases don't touch this and we skip the drop
        int numberDropped = (int) info.power; //special cases can specify this
        Random rand = new Random();
        int randomSelect = rand.nextInt();
        //inside the switch we will mod this to the desired range

        switch (itemUsed) {
            case GOLD_NUGGET:
                itemDropped = Material.PORK;
                if (info.power > 8) {
                    itemDropped = null;
                    World world = snowball.getWorld();
                    Location location = snowball.getLocation();
                    //world.spawnCreature(location, EntityType.PIG_ZOMBIE);
                }
                break;
            //existing case. Gold nuggets drop a porkchop.
            // double glowstone gets you zombie pigs

            case SADDLE:
                itemDropped = Material.LEASH;
                World world = snowball.getWorld();
                Location location = snowball.getLocation();
                //world.spawnCreature(location, EntityType.HORSE);
                break;
            //saddle gets you horses

            case LEATHER:
                randomSelect = randomSelect % 13; //one more than total number of outputs
                switch (randomSelect) {
                    case 0:
                        itemDropped = Material.BOOK;
                        break;
                    case 1:
                        itemDropped = Material.LEATHER_HELMET;
                        break;
                    case 2:
                        itemDropped = Material.LEATHER_CHESTPLATE;
                        break;
                    case 3:
                        itemDropped = Material.LEATHER_LEGGINGS;
                        break;
                    case 4:
                        itemDropped = Material.LEATHER_BOOTS;
                        break;
                    case 5:
                        itemDropped = Material.STICK;
                        break;
                    case 6:
                        itemDropped = Material.WOOD_SWORD;
                        break;
                    case 7:
                        itemDropped = Material.WOOD_PICKAXE;
                        break;
                    case 8:
                        itemDropped = Material.WOOD_AXE;
                        break;
                    case 9:
                        itemDropped = Material.WOOD_SPADE;
                        break;
                    case 10:
                        itemDropped = Material.WOOD_HOE;
                        break;
                    case 11:
                        itemDropped = Material.WORKBENCH;
                        break;
                    case 12:
                        itemDropped = Material.SADDLE;
                        break;
                }
                break;
            //way to quickly generate leather gear, also shortcut to book

            case IRON_INGOT:
                randomSelect = randomSelect % 1000;
                switch (randomSelect) {
                    case 999:
                        itemDropped = Material.IRON_BLOCK;
                        numberDropped = 4;
                    //you won the anvil lottery, woot!
                }
                break;
            //example of a sparse use case, to test out

            case IRON_BLOCK:
                randomSelect = randomSelect % 10; //one more than total number of outputs
                switch (randomSelect) {
                    case 1:
                        itemDropped = Material.IRON_HELMET;
                        break;
                    case 2:
                        itemDropped = Material.IRON_CHESTPLATE;
                        break;
                    case 3:
                        itemDropped = Material.IRON_LEGGINGS;
                        break;
                    case 4:
                        itemDropped = Material.IRON_BOOTS;
                        break;
                    case 5:
                        itemDropped = Material.IRON_SWORD;
                        break;
                    case 6:
                        itemDropped = Material.IRON_PICKAXE;
                        break;
                    case 7:
                        itemDropped = Material.IRON_AXE;
                        break;
                    case 8:
                        itemDropped = Material.IRON_SPADE;
                        break;
                    case 9:
                        itemDropped = Material.IRON_HOE;
                        break;
                }
                break;
            //if you have nine iron we can assume you'll get more, go ahead and iron up quick

            case GOLD_BLOCK:
                randomSelect = randomSelect % 10; //one more than total number of outputs
                switch (randomSelect) {
                    case 1:
                        itemDropped = Material.GOLD_HELMET;
                        break;
                    case 2:
                        itemDropped = Material.GOLD_CHESTPLATE;
                        break;
                    case 3:
                        itemDropped = Material.GOLD_LEGGINGS;
                        break;
                    case 4:
                        itemDropped = Material.GOLD_BOOTS;
                        break;
                    case 5:
                        itemDropped = Material.GOLD_SWORD;
                        break;
                    case 6:
                        itemDropped = Material.GOLD_PICKAXE;
                        break;
                    case 7:
                        itemDropped = Material.GOLD_AXE;
                        break;
                    case 8:
                        itemDropped = Material.GOLD_SPADE;
                        break;
                    case 9:
                        itemDropped = Material.GOLD_HOE;
                        break;
                }
                break;
            //we are not going to do likewise for diamond.

            case BEDROCK:
            //end of the list
        }
        //end of item specialcases with their individual code


        if (itemDropped != null) {
            ItemStack stack = new ItemStack(itemDropped);
            numberDropped = (int) (numberDropped * info.power);
            if (numberDropped > stack.getMaxStackSize()) {
                numberDropped = stack.getMaxStackSize();
            }
            stack.setAmount(numberDropped);
            snowball.getWorld().dropItem(snowball.getLocation(), stack.clone());
        }
    }
}

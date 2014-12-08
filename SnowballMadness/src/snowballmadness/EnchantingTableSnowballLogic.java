/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;

/**
 *
 * @author DanJ
 */
public class EnchantingTableSnowballLogic extends SpawnSnowballLogic2<Entity> {

    public EnchantingTableSnowballLogic() {
        super(Witch.class, EnderCrystal.class, 8.0);
    }

    @Override
    protected Class<? extends Entity> pickSpawnClass(Location location, SnowballInfo info) {
        Material target = location.getBlock().getRelative(BlockFace.DOWN).getType();

        //We always return here to create an entity.
        //quickly check that above block is clear
        //we're only here if the block is breathable space
        //nerfing too-trivially-easy suffocation mob farms
        //here's where we will do the special case for enchanting tables. The basic
        //case is Witch, because that's what's sent by Enchanting Table. So if we
        //fall through, we get Witch.
        switch (target) {
            case STATIONARY_LAVA:
            case LAVA:
            case FIRE:
                return Blaze.class;
            //lava and fire produce blazes out of the lava/fire

            case NETHERRACK:
                return PigZombie.class;
            //netherrack gives you zombie pigmen

            case QUARTZ_ORE:
                location.add(0, 16, 0);
                return Ghast.class;
            //to be annoying with ghasts, mine nether quartz ore and place it
            //and fire enchantment table snowballs at it

            case DIAMOND_ORE:
                return Wither.class;
            //if spawning on diamond ore, you get wither. This is a dangerous way
            //to get nether stars w/o wither skelly grinding.

            case ENDER_PORTAL:
            case ENDER_PORTAL_FRAME:
            //if we are actually spawning it off the portal it's sent to the End.
            //this will make the End more ridiculous, but odds of the dragon appearing there
            //and not portaling out are very slim. May be possible if you break the portal.
            //you'll get another (w. egg) from killing the dragon.
            case DRAGON_EGG:
                return EnderDragon.class;
            //you get a dragon right there in your face. As it appears,
            //it will very likely take out the egg it came from, so you fight them
            //more one at a time, starting right where the egg was.
            //For best spawning, you have to make a 3x2 wall behind the egg and hit just over it.

            case GRASS:
            case DIRT:
            case GRAVEL:
            case SAND:
                return TNTPrimed.class;
            //if we hit certain very ordinary blocks, simple boom. Best to be in a rocky cave.

            case RED_ROSE:
            case YELLOW_FLOWER:
                return Horse.class;
            //horses are cool, spawn them off flowers with an enchanting table snowball

            case BEDROCK:
                location.add(0, 2, 0);
                if (location.getBlock().isEmpty()) {
                    return Enderman.class;
                } else {
                    return null;
                }
            //down in those bedrock basements, you get endermen.
            //Three high space required to not suffocate them.
        }

        return super.pickSpawnClass(location, info);
    }
}
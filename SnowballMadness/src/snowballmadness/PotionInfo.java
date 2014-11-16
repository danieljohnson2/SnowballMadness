/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.inventory.*;

/**
 * This class decodes the potion ID into its parts, allowing potion
 * identification. It even knows about weird water bottle potions that do
 * nothing, unlike the Bukkit stuff. Also, its less buggy that the Bukkit stuff.
 *
 * @author DanJ
 */
public final class PotionInfo {

    /**
     * This is the basic effect the potion has.
     */
    public final Effect effect;
    /**
     * This indicates if the potion has been boosted to greater power; not all
     * potions can be.
     */
    public final Tier tier;
    /**
     * This indicates what the potion is called; for anything but Effect.NONE,
     * this is Alias.NONE, but it identifies mundane potions, awkward potions
     * and such.
     */
    public final Alias alias;
    /**
     * This flag, when true, indicates that the potion has extended duration.
     */
    public final boolean hasExtendedDuration;
    /**
     * This flag, when true, indicates that this is a splash potion.
     */
    public final boolean isSplash;

    ////////////////////////////////////////////////////////////////
    // Construction
    public PotionInfo(Effect effect, Tier tier, Alias alias, boolean hasExtendedDuration, boolean isSplash) {
        this.effect = Preconditions.checkNotNull(effect);
        this.tier = Preconditions.checkNotNull(tier);
        this.alias = Preconditions.checkNotNull(alias);
        this.hasExtendedDuration = hasExtendedDuration;
        this.isSplash = isSplash;
    }

    /**
     * This constructs the potion info for an item stack;
     *
     * @param itemStack The item stack to analyze; must be a potion.
     * @return The potion info describing the potion.
     */
    public static PotionInfo fromItemStack(ItemStack itemStack) {
        if (itemStack.getType() != Material.POTION) {
            throw new IllegalArgumentException(String.format(
                    "PotionInfo cannot be derived from $s, but only from potions.",
                    itemStack.getType()));
        }

        return fromID(itemStack.getDurability());
    }

    /**
     * This derives a new PotionInfo from the potionID, which is the
     * 'durability' or 'damage' value of a potion item.
     *
     * @param potionID The ID to analyze.
     * @return The potion info describing the potion.
     */
    public static PotionInfo fromID(int potionID) {
        int effectID = potionID & 0x000F;
        boolean isTierII = (potionID & 0x0020) != 0;
        boolean hasExtendedDuration = (potionID & 0x0040) != 0;
        boolean isSplash = (potionID & 0x4000) != 0;

        Effect effect = Effect.values()[effectID];
        Tier tier = isTierII ? Tier.II : Tier.I;

        Alias alias = Alias.NONE;

        for (Alias a : Alias.values()) {
            if (a.matchesPotionID(potionID)) {
                alias = a;
                break;
            }
        }

        return new PotionInfo(effect, tier, alias, hasExtendedDuration, isSplash);
    }

    /**
     * This method creates the snowball logic for a potion described by this
     * info object.
     *
     * @return The snowball logic created, or null if none could be found.
     */
    public SnowballLogic createPotionLogic() {
        switch (effect) {
            case NONE:
                return alias.createWaterBottleLogic();

            case REGENERATION:
                //regen 0:45 requires ghast tear, rare
                return new RegenerationSnowballLogic();
            case SWIFTNESS:
                if (tier == Tier.II) {
                    //swiftness II (sugar, glowstone) absurd speeds
                    return new ReversedSnowballLogic(12);
                } else if (!hasExtendedDuration) {
                    //swiftness 3:00 (sugar) like spider eye, but more so
                    //WITCH FARMABLE. Spawn them 11 blocks away and they will drink these to get closer.
                    return new ReversedSnowballLogic(3);
                } else {
                    //swiftness 8:00 (sugar, redstone)
                    return new ReversedSnowballLogic(4);
                }
            case FIRE_RESISTANCE:
                if (!hasExtendedDuration) {
                    //fire resist 3:00 (magma cream) gives you a tiny lava box.
                    //Will set delayed fires, glass doesn't replace leaves so they catch.
                    //WITCH FARMABLE. Spawn them in lava or over fire, they will drink these to survive.
                    return new BoxSnowballLogic(Material.GLASS, Material.STATIONARY_LAVA);
                } else {
                    //fire resist 8:00 (magma cream, redstone) gives you a massive fireball!
                    //We have to manage fire entities, I think, this is another server killer
                    //but it is AWESOME. You can literally burn lakes dry with this thing.
                    return new SphereSnowballLogic(Material.FIRE, Material.FIRE);
                }
            case POISON:
                // This would be a great place for a 'replace stone with monster egg' bomb.
                // I'll see what I can do. Done properly, it will replace all 'simulatable' blocks
                // with feesh, that being stone, smooth brick variants and I think cobblestone.
                // Radius will expand with strength of potion, always sphere. High power equals
                // VERY LARGE AREA turned entirely to feesh. Ideally we also spawn one and then hit
                // the feesh in the area with poison effect, setting off the trap (for high levels)
                // There is also an argument for leaving it untriggered. 
                if (tier == Tier.II) {
                    //poison II (+glowstone) gives you bigger globe feeeshapocalypse!
                    return new SphereSnowballLogic(Material.MONSTER_EGG, Material.MONSTER_EGG);
                } else if (!hasExtendedDuration) {
                    //poison gives you feeeshapocalypse! Box is smaller, 3x3 for unpowered
                    return new SphereSnowballLogic(Material.MONSTER_EGG, Material.MONSTER_EGG);
                } else {
                    //poison 2:00 (+redstone) gives you bigger globe feeeshapocalypse!
                    return new SphereSnowballLogic(Material.MONSTER_EGG, Material.MONSTER_EGG);
                }
            case HEALING:
                if (tier == Tier.I) {
                    //instant health gives you a tiny fish tank to relax you
                    //WITCH FARMABLE, quick spawn/kill in a one block high space will
                    //produce lots of these. You have to be quick, of course.
                    //Saves you finding watermelons and gold!
                    return new BoxSnowballLogic(Material.GLASS, Material.STATIONARY_WATER);
                } else {
                    //instant health II gives you a big fish bowl to relax you
                    return new SphereSnowballLogic(Material.GLASS, Material.STATIONARY_WATER);
                }
            case NIGHT_VISION:
                if (!hasExtendedDuration) {
                    //night vision 3:00 (golden carrot) gives you a obsidian cube
                    return new BoxSnowballLogic(Material.OBSIDIAN, Material.OBSIDIAN);
                } else {
                    //night vision 8:00 (golden carrot, redstone) gives you a obsidian sphere
                    return new SphereSnowballLogic(Material.OBSIDIAN, Material.AIR);
                }
            case WEAKNESS:
                if (!hasExtendedDuration) {
                    //weakness 1:30 (strength/regen+fermented spider eye) Gold ball
                    return new SphereSnowballLogic(Material.GOLD_BLOCK, Material.GOLD_ORE);
                } else {
                    //weakness 4:00 (those extended w. redstone + fermented spider eye) Diamond ball
                    return new SphereSnowballLogic(Material.DIAMOND_BLOCK, Material.DIAMOND_ORE);
                }
            case STRENGTH:
                if (tier == Tier.II) {
                    //strength II (blaze powder, glowstone) gives you the death star!
                    return new SphereSnowballLogic(Material.OBSIDIAN, Material.SMOOTH_BRICK);
                } else if (!hasExtendedDuration) {
                    ///strength 3:00 (blaze powder) gives you a stone fort to carve up
                    return new SphereSnowballLogic(Material.SMOOTH_BRICK, Material.SMOOTH_BRICK);
                } else {
                    //strength 8:00 (blaze powder, redstone) gives you super death star!
                    return new SphereSnowballLogic(Material.BEDROCK, Material.SMOOTH_BRICK);
                }
            case SLOWNESS:
                if (!hasExtendedDuration) {
                    //slowness 1:30 (swiftness/fireresist+fermented spider eye) makes a web 3x3
                    return new BoxSnowballLogic(Material.WEB, Material.WEB);
                } else {
                    //slowness 4:00 makes a web sphere, hollow (spam to encase)
                    return new SphereSnowballLogic(Material.WEB, Material.AIR);
                    //webs are good effects
                }
            case HARMING:
                if (tier == Tier.I) {
                    //harming tries to imprison you in obsidian! Will not make complete sphere
                    //unless target is a block in midair. Surfaces/solids not replaced.
                    return new SphereSnowballLogic(Material.OBSIDIAN, Material.AIR);
                } else {
                    //harming II tries to imprison you in bedrock! Will not make complete sphere
                    //unless target is a block in midair. Surfaces/solids not replaced.
                    return new SphereSnowballLogic(Material.BEDROCK, Material.AIR);
                }
            case WATER_BREATHING:
                if (!hasExtendedDuration) {
                    //water breathing 3:00 gives you a hollow sphere for now.
                    //WITCH FARMABLE potion, easily. Spawn them and kill them while drowning them.
                    return new SphereSnowballLogic(Material.GLASS, Material.AIR);
                } else {
                    //water breathing 8:00 gives you a hollow sphere for now, will be different
                    return new SphereSnowballLogic(Material.GLASS, Material.AIR);
                }
            case INVISIBILITY:
                if (!hasExtendedDuration) {
                    //invisibility 3:00 (night vision + spider eye) is a crystal ball
                    return new SphereSnowballLogic(Material.GLASS, Material.GLASS);
                } else {
                    //invisibility 8:00 (that plus redstone) is a wood sphere filled with books
                    return new SphereSnowballLogic(Material.WOOD, Material.BOOKSHELF);
                }
        }

        return null;
    }

    /**
     * This lists the possible effects for potions; equivalent to Bukkit's
     * PotionEffectType, but this is a real enum so we can switch on it.
     */
    public enum Effect {

        NONE(0) {
            @Override
            public boolean matchesEffectID(int effectID) {
                // Three effect IDs have no effect, but they have
                // different aliases instead.
                return effectID == 0 || effectID == 7 || effectID == 15;
            }
        },
        REGENERATION(1),
        SWIFTNESS(2),
        FIRE_RESISTANCE(3),
        POISON(4),
        HEALING(5),
        NIGHT_VISION(6),
        // #7 - 'clear' potion series 
        WEAKNESS(8),
        STRENGTH(9),
        SLOWNESS(10),
        LEAPING(11),
        HARMING(12),
        WATER_BREATHING(13),
        INVISIBILITY(14);
        // #15 - 'thin' potion series 
        private final int id;

        Effect(int id) {
            this.id = id;
        }

        public boolean matchesEffectID(int effectID) {
            return id == effectID;
        }
    }

    public enum Tier {

        I, II
    }

    public enum Alias {

        NONE(0x00) {
            @Override
            public boolean matchesPotionID(int potionID) {
                // 'none' is for all-bits zero; anything else
                // is 'mundane'.
                return potionID == 0;
            }
        },
        MUNDANE(0x00) {
            @Override
            public boolean matchesPotionID(int potionID) {
                // 'mundane' is the same as 'NONE', except any bit outside
                // the low nybble is set.
                return super.matchesPotionID(potionID) && potionID != 0;
            }
        },
        AWKWARD(0x10),
        THICK(0x20),
        POTENT(0x30),
        CLEAR(0x07),
        CHARMING(0x17),
        BUNGLING(0x27),
        RANK(0x37),
        THIN(0x0F),
        DEBONAIR(0x1F),
        SPARKLING(0x2F),
        STINKY(0x3F);
        private final int id;

        Alias(int id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }

        public boolean matchesPotionID(int potionID) {
            return (potionID & 0xFF) == id;
        }

        /**
         * This returns the logic that we use when a potion with this alias is
         * combined with a snowball, or null if no logic applies.
         *
         * @return The new logic to use, or null if none applies.
         */
        public SnowballLogic createWaterBottleLogic() {
            //these are the only ways to get glowstone, redstone or TNT
            //tnt is a gag. awk—warrrd! If you have the Nether you should be able
            //to get that much gunpowder anyway. Hollow to minimize the amount.

            switch (this) {
                case NONE:
                    //water bottle gives you water sphere.
                    return new SphereSnowballLogic(Material.GLASS, Material.STATIONARY_WATER);

                case AWKWARD:
                    //awkward potion made with netherwart gives you TNT
                    return new SphereSnowballLogic(Material.TNT, Material.AIR);

                case THICK:
                    //thick potion made with glowstone dust gives you glowstone (potency)
                    return new SphereSnowballLogic(Material.GLOWSTONE, Material.AIR);

                case MUNDANE:
                    //mundane potion (extended) made with redstone gives you redstone (duration)
                    return new SphereSnowballLogic(Material.REDSTONE_BLOCK, Material.AIR);

                default:
                    return null;
            }
        }
    }
}

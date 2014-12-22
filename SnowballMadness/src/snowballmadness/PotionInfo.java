/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.*;
import com.google.common.collect.ImmutableMap;
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
        boolean isTierII = (potionID & 0x0020) != 0;
        boolean hasExtendedDuration = (potionID & 0x0040) != 0;
        boolean isSplash = (potionID & 0x4000) != 0;

        Effect effect = Effect.fromPotionID(potionID);
        Tier tier = isTierII ? Tier.II : Tier.I;
        Alias alias = effect == Effect.NONE
                ? Alias.fromPotionID(potionID)
                : Alias.NONE;

        return new PotionInfo(effect, tier, alias, hasExtendedDuration, isSplash);
    }

    /**
     * This method returns a number that indicates 'how good' this potion is, to
     * simplify some tests. The awesomeness is determined by tier and duration;
     * you get 0 for a default potion, 1 for extended, 2 for tier-II, and 3 for
     * both.
     *
     * @return A score from 0-3 for how awesome this potion is.
     */
    public int getAwesomeness() {
        switch (tier) {
            case I:
                return hasExtendedDuration ? 1 : 0;

            case II:
                return hasExtendedDuration ? 3 : 2;

            default:
                return 0;
        }
    }

    /**
     * This method creates the snowball logic for a potion described by this
     * info object.
     *
     * @return The snowball logic created, or null if none could be found.
     */
    public SnowballLogic createPotionLogic() {
        return effect.createLogic(this);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(effect);

        if (effect == Effect.NONE && alias != Alias.NONE) {
            b.append("[");
            b.append(alias);
            b.append("]");
        }

        b.append(" Tier ");
        b.append(tier);

        if (isSplash) {
            b.append(" splash");
        }

        if (hasExtendedDuration) {
            b.append(" extended");
        }

        return b.toString();
    }

    /**
     * This lists the possible effects for potions; equivalent to Bukkit's
     * PotionEffectType, but this is a real enum so we can switch on it.
     * However, the key logic to generate a snowball is provided as a method
     * that we override in each case.
     */
    public enum Effect {

        NONE(0) {
            // we use this when the ID is not known, which makes stuff
            // like clear and sparking potions map to NONE.
            @Override
            public SnowballLogic createLogic(PotionInfo info) {
                return info.alias.createWaterBottleLogic();
            }
        },
        REGENERATION(1) {
            @Override
            public SnowballLogic createLogic(PotionInfo info) {
                //regen 0:45 requires ghast tear, rare
                return new RegenerationSnowballLogic();
            }
        },
        SWIFTNESS(2) {
            @Override
            public SnowballLogic createLogic(PotionInfo info) {
                switch (info.getAwesomeness()) {
                    case 0:
                        //swiftness 3:00 (sugar) like spider eye, but more so
                        //WITCH FARMABLE. Spawn them 11 blocks away and they will drink these to get closer.
                        return new ReversedSnowballLogic(3);

                    case 1:
                        //swiftness 8:00 (sugar, redstone)
                        return new ReversedSnowballLogic(4);

                    default:
                        //swiftness II (sugar, glowstone) absurd speeds
                        return new ReversedSnowballLogic(12);
                }
            }
        },
        FIRE_RESISTANCE(3) {
            @Override
            public SnowballLogic createLogic(PotionInfo info) {
                if (!info.hasExtendedDuration) {
                    //fire resist 3:00 (magma cream) 
                    //WITCH FARMABLE. Spawn them in lava or over fire, they will drink these to survive.
                    return new SphereSnowballLogic(Material.FIRE, Material.AIR);
                } else {
                    //fire resist 8:00 (magma cream, redstone) gives you a massive fireball!
                    //We have to manage fire entities, I think, this is another server killer
                    //but it is AWESOME. You can literally burn lakes dry with this thing.
                    return new SphereSnowballLogic(Material.FIRE, Material.FIRE);
                }
            }
        },
        POISON(4) {
            @Override
            public SnowballLogic createLogic(PotionInfo info) {  
                ItemStack payload;
                payload = new ItemStack(Material.EMERALD_ORE, 1);
                switch (info.getAwesomeness()) {
                    case 0:
                        //poison gives feesh sphere trap
                        return new FeeshVariationsSnowballLogic(payload);
                    case 1:
                        //poison 2:00 (+redstone) gives feesh sphere trap
                        return new FeeshVariationsSnowballLogic(payload);
                    default:
                        //poison II (+glowstone) gives you feesh sphere trap
                        return new FeeshVariationsSnowballLogic(payload);
                }
            }
        },
        HEALING(5) {
            @Override
            public SnowballLogic createLogic(PotionInfo info) {
                if (info.tier == Tier.I) {
                    //instant health gives you a tiny fish tank to relax you
                    //WITCH FARMABLE, quick spawn/kill in a one block high space will
                    //produce lots of these. You have to be quick, of course.
                    //Saves you finding watermelons and gold!
                    return new BoxSnowballLogic(Material.GLASS, Material.STATIONARY_WATER);
                } else {
                    //instant health II gives you a big fish bowl to relax you
                    return new SphereSnowballLogic(Material.GLASS, Material.STATIONARY_WATER);
                }
            }
        },
        NIGHT_VISION(6) {
            @Override
            public SnowballLogic createLogic(PotionInfo info) {
                if (!info.hasExtendedDuration) {
                    //night vision 3:00 (golden carrot) gives you a obsidian cube
                    return new BoxSnowballLogic(Material.OBSIDIAN, Material.AIR);
                } else {
                    //night vision 8:00 (golden carrot, redstone) gives you a obsidian sphere
                    return new SphereSnowballLogic(Material.OBSIDIAN, Material.AIR);
                }
            }
        },
        // #7 - 'clear' potion series 
        WEAKNESS(8) {
            @Override
            public SnowballLogic createLogic(PotionInfo info) {
                if (!info.hasExtendedDuration) {
                    //weakness 1:30 (strength/regen+fermented spider eye) Gold ball
                    return new SphereSnowballLogic(Material.GOLD_BLOCK, Material.GOLD_ORE);
                } else {
                    //weakness 4:00 (those extended w. redstone + fermented spider eye) Diamond ball
                    return new SphereSnowballLogic(Material.DIAMOND_BLOCK, Material.DIAMOND_ORE);
                }
            }
        },
        STRENGTH(9) {
            @Override
            public SnowballLogic createLogic(PotionInfo info) {
                switch (info.getAwesomeness()) {
                    case 0:
                        ///strength 3:00 (blaze powder) gives you a stone fort to carve up
                        return new SphereSnowballLogic(Material.SMOOTH_BRICK, Material.SMOOTH_BRICK);
                    case 1:
                        //strength 8:00 (blaze powder, redstone) gives you super death star!
                        return new SphereSnowballLogic(Material.BEDROCK, Material.SMOOTH_BRICK);
                    default:
                        //strength II (blaze powder, glowstone) gives you the death star!
                        return new SphereSnowballLogic(Material.OBSIDIAN, Material.SMOOTH_BRICK);
                }
            }
        },
        SLOWNESS(10) {
            @Override
            public SnowballLogic createLogic(PotionInfo info) {
                if (!info.hasExtendedDuration) {
                    //slowness 1:30 (swiftness/fireresist+fermented spider eye) makes a web sphere, hollow
                    return new RingSnowballLogic(Material.WEB, Material.AIR);
                } else {
                    //slowness 4:00 makes a web sphere, hollow (spam to encase)
                    return new RingSnowballLogic(Material.WEB, Material.AIR);
                    //webs are good effects
                }
            }
        },
        LEAPING(11) {
            @Override
            public SnowballLogic createLogic(PotionInfo info) {
                // this is to be added in Minecraft 1.8.1; we can't use it.
                return null;
            }
        },
        HARMING(12) {
            @Override
            public SnowballLogic createLogic(PotionInfo info) {
                if (info.tier == Tier.I) {
                    //harming tries to imprison you in obsidian! Will not make complete sphere
                    //unless target is a block in midair. Surfaces/solids not replaced.
                    return new SphereSnowballLogic(Material.OBSIDIAN, Material.AIR);
                } else {
                    //harming II tries to imprison you in bedrock! Will not make complete sphere
                    //unless target is a block in midair. Surfaces/solids not replaced.
                    return new SphereSnowballLogic(Material.BEDROCK, Material.AIR);
                }
            }
        },
        WATER_BREATHING(13) {
            @Override
            public SnowballLogic createLogic(PotionInfo info) {
                if (!info.hasExtendedDuration) {
                    //water breathing 3:00 gives you a hollow sphere for now.
                    //WITCH FARMABLE potion, easily. Spawn them and kill them while drowning them.
                    return new SphereSnowballLogic(Material.GLASS, Material.AIR);
                } else {
                    //water breathing 8:00 gives you a hollow sphere for now, will be different
                    return new SphereSnowballLogic(Material.GLASS, Material.AIR);
                }
            }
        },
        INVISIBILITY(14) {
            @Override
            public SnowballLogic createLogic(PotionInfo info) {
                if (!info.hasExtendedDuration) {
                    //invisibility 3:00 (night vision + spider eye) is a crystal ball
                    return new SphereSnowballLogic(Material.GLASS, Material.GLASS);
                } else {
                    //invisibility 8:00 (that plus redstone) is a wood sphere filled with books
                    return new SphereSnowballLogic(Material.WOOD, Material.BOOKSHELF);
                }
            }
        };
        // #15 - 'thin' potion series 
        private final int id;
        private static final ImmutableMap<Integer, Effect> effectsByID;

        Effect(int id) {
            this.id = id;
        }

        static {
            ImmutableMap.Builder<Integer, Effect> b = ImmutableMap.builder();

            for (Effect effect : values()) {
                b.put(effect.id, effect);
            }

            effectsByID = b.build();
        }

        /**
         * This method obtains the effect that comes from a potion with the
         * given ID. We just look it up from the low four bits; if the potionID
         * has an invalid effect, this returns NONE.
         *
         * @param potionID The potion ID to analyze.
         * @return The effect of the potion, or NONE if it has none.
         */
        public static Effect fromPotionID(int potionID) {
            Effect effect = effectsByID.get(potionID & 0x000F);

            if (effect == null) {
                return NONE;
            } else {
                return effect;
            }
        }

        /**
         * This creates the logic appropriate for the effect; may return null to
         * produce no special logic.
         *
         * @param info The info describing the potion.
         * @return The new logic for the snowball, or null for no new logic.
         */
        public abstract SnowballLogic createLogic(PotionInfo info);
    }

    /**
     * Tier indicates whether this potion is 'powered up'; we use an enum to
     * avoid confusing 0 based numbering.
     */
    public enum Tier {

        I, II
    }

    /**
     * This enum lists the special names that water bottles can have; they have
     * no real effect, but we can still recognize them. The 'NONE' alias is used
     * for water bottles, but also 'real' potions that have real effects.
     */
    public enum Alias {

        NONE(0x00) {
            @Override
            public SnowballLogic createWaterBottleLogic() {
                //water bottle gives you the glass rings as it is so easy to get
                //nonreal potions don't give you spheres or boxes
                return new RingSnowballLogic(Material.GLASS);
            }
        },
        MUNDANE(0x00) {
            @Override
            public SnowballLogic createWaterBottleLogic() {
                //mundane potion (extended) made with redstone gives you redstone (duration)
                return new RingSnowballLogic(Material.REDSTONE_BLOCK);
            }
        },
        AWKWARD(0x10) {
            @Override
            public SnowballLogic createWaterBottleLogic() {
                //awkward potion made with netherwart gives you TNT rings
                return new RingSnowballLogic(Material.TNT);

            }
        },
        THICK(0x20) {
            @Override
            public SnowballLogic createWaterBottleLogic() {
                //thick potion made with glowstone dust gives you glowstone (potency)
                return new RingSnowballLogic(Material.GLOWSTONE);
            }
        },
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
        private static final ImmutableMap<Integer, Alias> aliasesByID;

        Alias(int id) {
            this.id = id;
        }

        static {
            ImmutableMap.Builder<Integer, Alias> b = ImmutableMap.builder();

            for (Alias alias : values()) {
                // NONE is a special case for when the whole potion ID is 0!

                if (alias != NONE) {
                    b.put(alias.id, alias);
                }
            }

            aliasesByID = b.build();
        }

        /**
         * This returns the alias that applies for the potion given; potionID 0
         * returns NONE. Any potion with a normal effect will also return NONE.
         *
         * @param potionID The potion ID to analyze.
         * @return True if this alias describes the potion.
         */
        public static Alias fromPotionID(int potionID) {
            if (potionID == 0) {
                return NONE;
            }

            Alias alias = aliasesByID.get(potionID & 0x3F);

            if (alias == null) {
                return NONE;
            } else {
                return alias;
            }
        }

        /**
         * This returns the logic that we use when a potion with this alias is
         * combined with a snowball, or null if no logic applies.
         *
         * The default implementation only returns null, but partical enum
         * values override this.
         *
         * @return The new logic to use, or null if none applies.
         */
        public SnowballLogic createWaterBottleLogic() {
            return null;

            //these are the only ways to get glowstone, redstone or TNT
            //tnt is a gag. awk—warrrd!
        }
    }
}

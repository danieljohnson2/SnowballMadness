package snowballmadness;

import java.util.*;
import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.projectiles.*;

/**
 * This class is the base class that hosts the logic that triggers when a snowball hits a target.
 *
 * We keep these in a weak hash map, so it is important that this object (and all subclasses) not hold onto a reference to a
 * Snowball, or that snowball may never be collected.
 *
 * @author DanJ
 */
public abstract class SnowballLogic {

    ////////////////////////////////////////////////////////////////
    // Logic
    //
    /**
     * This is called when the snowball is launched.
     *
     * @param snowball The snowball being launched.
     * @param info Other information about the snowball.
     */
    public void launch(Snowball snowball, SnowballInfo info) {
    }

    /**
     * this is called every many times every second.
     *
     * @param snowball A snowball that gets a chance to do something.
     * @param info Other information about the snowball.
     */
    public void tick(Snowball snowball, SnowballInfo info) {
    }

    /**
     * This is called when the snowball hits something and returns teh damange to be done (which can be 0).
     *
     * @param snowball The snowball hitting something.
     * @param info Other information about the snowball.
     * @param target The entity that was hit.
     * @param proposedDamage The damage the snowball is expected to do..
     * @return The damage teh snowball will do.
     */
    public double damage(Snowball snowball, SnowballInfo info, Entity target, double proposedDamage) {
        return proposedDamage;
    }

    /**
     * This is called when the snowball hits something.
     *
     * @param snowball The snowball hitting something.
     * @param info Other information about the snowball.
     */
    public void hit(Snowball snowball, SnowballInfo info) {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    ////////////////////////////////////////////////////////////////
    // Creation
    //

    /**
     * This method creates a new logic, but does not start it. It chooses the logic based on 'hint', which is the stack
     * immediately above the snowball being thrown.
     *
     * @param slice The inventory slice above the snowball in the inventory.
     * @return The new logic, not yet started or attached to a snowball, or null if the snowball will be illogical.
     */
    public static SnowballLogic createLogic(InventorySlice slice) {
        ItemStack hint = slice.getBottomItem();

        if (hint == null) {
            return null;
        }

        if (hint.getType().isBlock()) {
            if (hint.getType() == Material.DRAGON_EGG) {
                return new DeathVortexSnowballLogic();
            } else if (hint.getType() == Material.RED_ROSE) {
                return new FireworkSnowballLogic(hint);
            } else if (hint.getType() == Material.YELLOW_FLOWER) {
                return new FireworkSnowballLogic(hint);
            } else if (hint.getType() == Material.TORCH) {
                return new TorchPlaceSnowballLogic();
            } else if (hint.getType() == Material.SAPLING) {
                return new ArboristSnowballLogic(hint);
            } else if (hint.getType() == Material.REDSTONE_TORCH_ON) {
                return BlockEmbedSnowballLogic.fromMaterial(hint.getType());
            } else if (hint.getType() == Material.LADDER) {
                return BlockEmbedSnowballLogic.fromMaterial(hint.getType());
            } else if (hint.getType() == Material.TNT) {
                return new TNTSnowballLogic(hint.getAmount());
            } else if (hint.getType() == Material.STONE) {
                return new WallPaintingSnowballLogic(Material.STONE, hint.getDurability(), hint.getAmount());
            } else if (hint.getType() == Material.CONCRETE) {
                return new WallPaintingSnowballLogic(Material.CONCRETE, hint.getDurability(), hint.getAmount());
            } else if (hint.getType() == Material.QUARTZ_BLOCK) {
                return new WallPaintingSnowballLogic(Material.QUARTZ_BLOCK, hint.getDurability(), hint.getAmount());
            } else if (hint.getType() == Material.HARD_CLAY) {
                return new WallPaintingSnowballLogic(Material.HARD_CLAY, hint.getDurability(), hint.getAmount());
            } else if (hint.getType() == Material.STAINED_CLAY) {
                return new WallPaintingSnowballLogic(Material.STAINED_CLAY, hint.getDurability(), hint.getAmount());
            } else if (hint.getType() == Material.VINE) {
                return BlockEmbedSnowballLogic.fromMaterial(hint.getType());
                //TNT, Ladders and Vines are special blocks
            } else {
                return new BlockPlacementSnowballLogic(hint.getType(), hint.getDurability());
                //everything else is just cloned one at a time, because magic. Up to and including beacons.
            }
        } else {
            switch (hint.getType()) {
                case GLASS_BOTTLE:
                    return new SphereSnowballLogic(Material.GLASS, Material.AIR, hint.getAmount());

                case LEATHER:
                    return new ItemDropSnowballLogic(
                            Material.BOOK,
                            Material.LEATHER_HELMET,
                            Material.LEATHER_CHESTPLATE,
                            Material.LEATHER_LEGGINGS,
                            Material.LEATHER_BOOTS,
                            Material.SADDLE);

                case WATER_BUCKET:
                    return new BlockPlacementSnowballLogic(Material.WATER, hint.getDurability());
                    //return new RefillSnowballLogic(Material.WATER_BUCKET, 32);

                case LAVA_BUCKET:
                    return new BlockPlacementSnowballLogic(Material.LAVA, hint.getDurability());
                    //return new RefillSnowballLogic(Material.LAVA_BUCKET, 32);

                case WOOD_SPADE:
                    return BlockEmbedSnowballLogic.fromMaterial(hint.getType());

                case BUCKET:
                    return new RefillSnowballLogic(Material.BUCKET, 64);

                case FEATHER:
                    return new FeatherSnowballLogic();

                case FLINT_AND_STEEL:
                    return new SphereSnowballLogic(Material.FIRE, Material.FIRE, 16);

                case FIREWORK:
                    return new JetpackSnowballLogic();

                case FISHING_ROD:
                    return new ComeAlongSnowballLogic(Material.FISHING_ROD);

                case WATCH:
                    return new WatchSnowballLogic();

                case IRON_INGOT:
                    return new MagneticSnowballLogic();

                case ARROW:
                    return new ArrowSnowballLogic();

                case TIPPED_ARROW:
                case SPECTRAL_ARROW:
                    return new TippedArrowSnowballLogic(hint);

                case STONE_PICKAXE:
                case IRON_PICKAXE:
                case GOLD_PICKAXE:
                case DIAMOND_PICKAXE:
                    return new PickaxeSnowballLogic(hint.getType());

                case STONE_AXE:
                case IRON_AXE:
                case GOLD_AXE:
                case DIAMOND_AXE:
                    return new AxeSnowballLogic(hint.getType());

                case STONE_SPADE:
                case IRON_SPADE:
                case GOLD_SPADE:
                case DIAMOND_SPADE:
                    return new SpadeSnowballLogic(hint.getType());

                case STONE_HOE:
                case IRON_HOE:
                case GOLD_HOE:
                case DIAMOND_HOE:
                    return new HoeSnowballLogic(hint.getType());

                case SHEARS:
                    return new ShearsSnowballLogic();

                /*case LEATHER_HELMET:
                case IRON_HELMET:
                case GOLD_HELMET:
                case DIAMOND_HELMET:
                case CHAINMAIL_HELMET:
                    return new ShellSnowballLogic(hint.getType(), slice.skip(1));

                case LEATHER_CHESTPLATE:
                case IRON_CHESTPLATE:
                case GOLD_CHESTPLATE:
                case DIAMOND_CHESTPLATE:
                case CHAINMAIL_CHESTPLATE:
                    return new CylinderSnowballLogic(hint.getType(), slice.skip(1));*/

                case SNOW_BALL:
                    String targetName = hint.getItemMeta().getDisplayName();
                    return new MultiplierSnowballLogic(hint.getAmount(), targetName, slice.skip(1));
//________________________________________________________________________________________________________________________________
//Food Animals Spawn As Babies

                case EGG:
                    return new SpawnSnowballLogic<Chicken>(Chicken.class) {
                        @Override
                        protected void equipEntity(final Chicken spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    spawned.setBaby();
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

                case PORK:
                    return new SpawnSnowballLogic<Pig>(Pig.class) {
                        @Override
                        protected void equipEntity(final Pig spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    spawned.setBaby();
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

                case RABBIT_FOOT:
                case RABBIT_HIDE:
                    return new SpawnSnowballLogic<Rabbit>(Rabbit.class) {
                        @Override
                        protected void equipEntity(final Rabbit spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    spawned.setBaby();
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

                case MILK_BUCKET:
                    return new SpawnSnowballLogic<Cow>(Cow.class) {
                        @Override
                        protected void equipEntity(final Cow spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    spawned.setBaby();
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

                case MUSHROOM_SOUP:
                    return new SpawnSnowballLogic<MushroomCow>(MushroomCow.class) {
                        @Override
                        protected void equipEntity(final MushroomCow spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    spawned.setBaby();
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

//________________________________________________________________________________________________________________________________
//Tame Interesting Animals
                case SADDLE:
                    return new SpawnSnowballLogic<Horse>(Horse.class) {
                        @Override
                        protected void initializeEntity(Horse spawned, SnowballInfo info) {
                            super.initializeEntity(spawned, info);
                        }

                        @Override
                        protected void equipEntity(final Horse spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (info.shooter != null) {
                                        spawned.setCustomName(info.shooter.getName() + "'s Horse");
                                        spawned.setCustomNameVisible(false);
                                        spawned.setRemoveWhenFarAway(false);
                                        spawned.setMaxDomestication(1);
                                        spawned.setJumpStrength(2.0); //default 0.7, max 2.0
                                        AttributeInstance speedAttribute = spawned.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                                        speedAttribute.setBaseValue(info.power);
                                        //spawned.setTamed(true);
                                        //spawned.setOwner(info.shooter);
                                        spawned.setAdult();
                                        //spawned.setBaby();
                                    }
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

                case STRING:
                    return new SpawnSnowballLogic<Ocelot>(Ocelot.class) {
                        @Override
                        protected void initializeEntity(Ocelot spawned, SnowballInfo info) {
                            super.initializeEntity(spawned, info);
                        }

                        @Override
                        protected void equipEntity(final Ocelot spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (info.shooter != null) {
                                        spawned.setCustomName(info.shooter.getName() + "'s Cat");
                                        spawned.setCustomNameVisible(false);
                                        spawned.setRemoveWhenFarAway(false);
                                        int kitty = (int) Math.floor(Math.random() * 3.0);
                                        if (kitty == 0) {
                                            spawned.setCatType(Ocelot.Type.BLACK_CAT);
                                        } else if (kitty == 1) {
                                            spawned.setCatType(Ocelot.Type.RED_CAT);
                                        } else if (kitty == 2) {
                                            spawned.setCatType(Ocelot.Type.SIAMESE_CAT);
                                        } else {
                                            spawned.setCatType(Ocelot.Type.WILD_OCELOT);
                                        }
                                        spawned.setBaby();
                                    }
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

                case LEASH:
                    return new SpawnSnowballLogic<Wolf>(Wolf.class) {
                        @Override
                        protected void initializeEntity(Wolf spawned, SnowballInfo info) {
                            super.initializeEntity(spawned, info);
                        }

                        @Override
                        protected void equipEntity(final Wolf spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (info.shooter != null) {
                                        spawned.setCustomName(info.shooter.getName() + "'s Dog");
                                        spawned.setCustomNameVisible(false);
                                        spawned.setRemoveWhenFarAway(false);
                                        spawned.setTamed(true);
                                        spawned.setOwner(info.shooter);
                                        spawned.setBaby();
                                    }
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

                case CARROT_ITEM:
                    return new SpawnSnowballLogic<Snowman>(Snowman.class) {
                        @Override
                        protected void initializeEntity(Snowman spawned, SnowballInfo info) {
                            super.initializeEntity(spawned, info);
                            spawned.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, Integer.MAX_VALUE), true);
                        }

                        @Override
                        protected void equipEntity(final Snowman spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    spawned.setCustomName(info.shooter.getName() + "'s Snow Bank");
                                    spawned.setCustomNameVisible(false);
                                    //spawned.setRemoveWhenFarAway(false);
                                    //AttributeInstance followAttribute = spawned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
                                    //followAttribute.setBaseValue((info.power * info.power) + 16f); //default 16 + caster level
                                    //AttributeInstance speedAttribute = spawned.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                                    //speedAttribute.setBaseValue(info.power / 4.0f);
                                } //the magic snowman is an all-biome source that doesn't wander. Still works as a turret.
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

//________________________________________________________________________________________________________________________________
//Hostile Mobs But Still Relatively Normal
                case PRISMARINE_SHARD:
                case PRISMARINE_CRYSTALS:
                    return new SpawnSnowballLogic<Guardian>(Guardian.class) {
                    };

                case POTION:
                    return new SpawnSnowballLogic<Witch>(Witch.class) {
                    };

                case GOLD_NUGGET:
                    return new SpawnSnowballLogic<PigZombie>(PigZombie.class) {
                    };
                case GHAST_TEAR:
                    return new SpawnSnowballLogic<Ghast>(Ghast.class) {
                    };

                case ENDER_PEARL:
                    return new SpawnSnowballLogic<Enderman>(Enderman.class) {
                    };

                case SPIDER_EYE:
                    return new SpawnSnowballLogic<Spider>(Spider.class) {
                    };

//________________________________________________________________________________________________________________________________
//Horrifyingly Hostile Mobs
                case SLIME_BALL:
                    return new SpawnSnowballLogic<Slime>(Slime.class) {
                        @Override
                        protected void initializeEntity(Slime spawned, final SnowballInfo info) {
                            super.initializeEntity(spawned, info);
                            spawned.setSize((int) Math.floor(info.power));
                        }

                        @Override
                        protected void equipEntity(final Slime spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (info.shooter != null) {
                                        spawned.setCustomName(info.shooter.getName() + "'s Jello");
                                        spawned.setCustomNameVisible(false);
                                        //AttributeInstance followAttribute = spawned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
                                        //followAttribute.setBaseValue((info.power * info.power) + 16f); //default 16 + caster level
                                        //spawned.setRemoveWhenFarAway(false);
                                    }
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

                case MAGMA_CREAM:
                    return new SpawnSnowballLogic<MagmaCube>(MagmaCube.class) {
                        @Override
                        protected void initializeEntity(MagmaCube spawned, final SnowballInfo info) {
                            super.initializeEntity(spawned, info);
                            spawned.setSize((int) Math.floor(info.power));
                        }

                        @Override
                        protected void equipEntity(final MagmaCube spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (info.shooter != null) {
                                        spawned.setCustomName(info.shooter.getName() + "'s Jello");
                                        spawned.setCustomNameVisible(false);
                                        //AttributeInstance followAttribute = spawned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
                                        //followAttribute.setBaseValue((info.power * info.power) + 16f); //default 16 + caster level
                                        //spawned.setRemoveWhenFarAway(false);
                                    }
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

                case FERMENTED_SPIDER_EYE:
                    return new SpawnSnowballLogic<CaveSpider>(CaveSpider.class) {
                        @Override
                        protected void initializeEntity(CaveSpider spawned, final SnowballInfo info) {
                            super.initializeEntity(spawned, info);
                        }

                        @Override
                        protected void equipEntity(final CaveSpider spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (info.shooter != null) {
                                        spawned.setCustomName(info.shooter.getName() + "'s Fault");
                                        spawned.setCustomNameVisible(false);
                                        //spawned.setRemoveWhenFarAway(false);
                                        //AttributeInstance followAttribute = spawned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
                                        //followAttribute.setBaseValue((info.power * info.power) + 16f); //default 16 + caster level
                                        //AttributeInstance speedAttribute = spawned.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                                        //speedAttribute.setBaseValue(info.power / 4.0f);
                                    }
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

                case BLAZE_POWDER:
                case BLAZE_ROD:
                    return new SpawnSnowballLogic<Blaze>(Blaze.class) {
                        @Override
                        protected void initializeEntity(Blaze spawned, final SnowballInfo info) {
                            super.initializeEntity(spawned, info);
                        }

                        @Override
                        protected void equipEntity(final Blaze spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (info.shooter != null) {
                                        spawned.setCustomName(info.shooter.getName() + "'s Air Force");
                                        spawned.setCustomNameVisible(false);
                                        //spawned.setRemoveWhenFarAway(false);
                                        //AttributeInstance followAttribute = spawned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
                                        //followAttribute.setBaseValue((info.power * info.power) + 16f); //default 16 + caster level
                                        //AttributeInstance speedAttribute = spawned.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                                        //speedAttribute.setBaseValue(info.power);
                                    }
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

                case POISONOUS_POTATO:
                    return new SpawnSnowballLogic<Giant>(Giant.class) {
                        @Override
                        protected void equipEntity(final Giant spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (info.shooter != null) {
                                        spawned.setCustomName(info.shooter.getName() + "'s Tank");
                                        spawned.setCustomNameVisible(false);
                                        //spawned.setRemoveWhenFarAway(false);
                                        //AttributeInstance followAttribute = spawned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
                                        //followAttribute.setBaseValue((info.power * info.power) + 40f); //default 16 + caster level
                                        //AttributeInstance damageAttribute = spawned.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                                        //damageAttribute.setBaseValue(info.power);
                                        //AttributeInstance healthAttribute = spawned.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                                        //healthAttribute.setBaseValue(info.power * info.power); //HP in half-hearts
                                    }
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

//________________________________________________________________________________________________________________________________
//Ninjas and Armies In Uniforms
                case BONE:
                    return new SpawnSnowballLogic<Skeleton>(Skeleton.class) {
                        @Override
                        protected void equipEntity(final Skeleton spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (info.shooter != null) {
                                        ItemStack gear;

                                        /*LeatherArmorMeta dye;
                                        Color belt = Color.WHITE;
                                        if (info.power < 2) {
                                            belt = Color.WHITE; //noob
                                        } else if (info.power < 3) {
                                            belt = Color.YELLOW; //4
                                        } else if (info.power < 4) {
                                            belt = Color.ORANGE; //9
                                        } else if (info.power < 5) {
                                            belt = Color.LIME; //16
                                        } else if (info.power < 6) {
                                            belt = Color.BLUE; //25
                                        } else if (info.power < 7) {
                                            belt = Color.PURPLE; //36
                                        } else if (info.power < 8) {
                                            belt = Color.GRAY; //49
                                        } else if (info.power < 9) {
                                            belt = Color.RED; //64
                                        } else if (info.power < 10) {
                                            belt = Color.BLACK; //81
                                        }*/

                                        gear = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                                        SkullMeta meta = (SkullMeta) gear.getItemMeta();
                                        meta.setOwner(info.shooter.getName());
                                        gear.setItemMeta(meta);
                                        // OH GOD IT HAS MY FAAAAAAACE!
                                        spawned.getEquipment().setHelmet(gear);
                                        spawned.getEquipment().setHelmetDropChance(0.0001f);

                                        /*gear = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                                        dye = (LeatherArmorMeta) gear.getItemMeta();
                                        dye.setColor(belt);
                                        gear.setItemMeta(dye);
                                        spawned.getEquipment().setChestplate(gear);
                                        spawned.getEquipment().setChestplateDropChance(0.0f);

                                        gear = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                                        dye = (LeatherArmorMeta) gear.getItemMeta();
                                        dye.setColor(belt);
                                        gear.setItemMeta(dye);
                                        spawned.getEquipment().setLeggings(gear);
                                        spawned.getEquipment().setLeggingsDropChance(0.0f);

                                        gear = new ItemStack(Material.LEATHER_BOOTS, 1);
                                        dye = (LeatherArmorMeta) gear.getItemMeta();
                                        dye.setColor(belt);
                                        gear.setItemMeta(dye);
                                        spawned.getEquipment().setBoots(gear);
                                        spawned.getEquipment().setBootsDropChance(0.0f);*/

                                        gear = info.shooter.getInventory().getItem(0);
                                        if (gear != null) {
                                            gear = info.shooter.getInventory().getItem(0).clone();
                                            //we are altering the itemStack, must clone or we alter it right in our inventory!
                                            //gear.setAmount(1); nerfs item stack duplication
                                            spawned.getEquipment().setItemInMainHand(gear);
                                            spawned.getEquipment().setItemInMainHandDropChance(1.0f);
                                            //max grindage for minion stuffmaking
                                        }

                                        spawned.setCustomName(info.shooter.getName() + "'s Ninja");
                                        spawned.setCustomNameVisible(false);
                                        /*AttributeInstance followAttribute = spawned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
                                        followAttribute.setBaseValue((info.power * info.power) + 16f); //default 16 + caster level
                                        AttributeInstance healthAttribute = spawned.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                                        healthAttribute.setBaseValue(20 + info.power); //HP down
                                        AttributeInstance speedAttribute = spawned.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                                        speedAttribute.setBaseValue(info.power / 5.0f); //speed == caster level*/
                                        spawned.setRemoveWhenFarAway(false);
                                        spawned.setHealth(spawned.getMaxHealth());
                                    }
                                }
                            }
                                    .runTaskLater(info.plugin,
                                            1L);
                        }
                    };

                case ROTTEN_FLESH:
                    return new SpawnSnowballLogic<Zombie>(Zombie.class) {
                        @Override
                        protected void equipEntity(final Zombie spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (info.shooter != null) {
                                        ItemStack gear;
                                        /*LeatherArmorMeta dye;
                                        Color belt = Color.WHITE;
                                        if (info.power < 2) {
                                            belt = Color.WHITE; //noob
                                        } else if (info.power < 3) {
                                            belt = Color.YELLOW; //4
                                        } else if (info.power < 4) {
                                            belt = Color.ORANGE; //9
                                        } else if (info.power < 5) {
                                            belt = Color.LIME; //16
                                        } else if (info.power < 6) {
                                            belt = Color.BLUE; //25
                                        } else if (info.power < 7) {
                                            belt = Color.PURPLE; //36
                                        } else if (info.power < 8) {
                                            belt = Color.GRAY; //49
                                        } else if (info.power < 9) {
                                            belt = Color.RED; //64
                                        } else if (info.power < 10) {
                                            belt = Color.BLACK; //81
                                        }*/

                                        gear = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                                        SkullMeta meta = (SkullMeta) gear.getItemMeta();
                                        meta.setOwner(info.shooter.getName());
                                        gear.setItemMeta(meta);
                                        // OH GOD IT HAS MY FAAAAAAACE!
                                        spawned.getEquipment().setHelmet(gear);
                                        spawned.getEquipment().setHelmetDropChance(0.0001f);

                                        /*gear = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                                        dye = (LeatherArmorMeta) gear.getItemMeta();
                                        dye.setColor(belt);
                                        gear.setItemMeta(dye);
                                        spawned.getEquipment().setChestplate(gear);
                                        spawned.getEquipment().setChestplateDropChance(0.0f);

                                        gear = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                                        dye = (LeatherArmorMeta) gear.getItemMeta();
                                        dye.setColor(belt);
                                        gear.setItemMeta(dye);
                                        spawned.getEquipment().setLeggings(gear);
                                        spawned.getEquipment().setLeggingsDropChance(0.0f);

                                        gear = new ItemStack(Material.LEATHER_BOOTS, 1);
                                        dye = (LeatherArmorMeta) gear.getItemMeta();
                                        dye.setColor(belt);
                                        gear.setItemMeta(dye);
                                        spawned.getEquipment().setBoots(gear);
                                        spawned.getEquipment().setBootsDropChance(0.0f);*/

                                        gear = info.shooter.getInventory().getItem(0);
                                        if (gear != null) {
                                            gear = info.shooter.getInventory().getItem(0).clone();
                                            //we are altering the itemStack, must clone or we alter it right in our inventory!
                                            //gear.setAmount(1); nerfs item stack duplication
                                            spawned.getEquipment().setItemInMainHand(gear);
                                            spawned.getEquipment().setItemInMainHandDropChance(1.0f);
                                            //max grindage for minion stuffmaking
                                        }
                                        spawned.setCustomName(info.shooter.getName() + "'s Army");
                                        spawned.setCustomNameVisible(false);
                                        spawned.setRemoveWhenFarAway(false);
                                        /*AttributeInstance followAttribute = spawned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
                                        followAttribute.setBaseValue((info.power * info.power) + 40f); //default 40 + caster level
                                        AttributeInstance healthAttribute = spawned.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                                        healthAttribute.setBaseValue(20 + (info.power * info.power)); //HP equal to your level + 20
                                        AttributeInstance speedAttribute = spawned.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                                        speedAttribute.setBaseValue((info.power / 50.0f) + 0.23); //speed == moderate fast*/
                                        AttributeInstance zomAttribute = spawned.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS);
                                        zomAttribute.setBaseValue(0.0f); //army zoms don't hire civilians!
                                        /*spawned.setHealth(spawned.getMaxHealth());
                                        if (info.power > 11) {
                                            spawned.setBaby(true);
                                        } else {
                                            spawned.setBaby(false);
                                        } // at level 100, all the zombie ninjas are BABY NINJAS*/
                                    }
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };
                case SULPHUR:
                    return new SpawnSnowballLogic<Creeper>(Creeper.class) {
                        @Override
                        protected void initializeEntity(Creeper spawned, SnowballInfo info) {
                            super.initializeEntity(spawned, info);
                        }

                        @Override
                        protected void equipEntity(final Creeper spawned, final SnowballInfo info) {
                            super.equipEntity(spawned, info);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (info.shooter != null) {
                                        if (info.power > 64) {
                                            spawned.setPowered(true);
                                            spawned.setCustomName(info.shooter.getName() + "'s Nightmare");
                                        } else {
                                            spawned.setCustomName(info.shooter.getName() + "'s Mistake");
                                        }
                                        spawned.setCustomNameVisible(false);
                                        spawned.setRemoveWhenFarAway(false);
                                        /*AttributeInstance followAttribute = spawned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
                                        followAttribute.setBaseValue((info.power * info.power) + 60f); //default 60 + caster level*/
                                    }
                                }
                            }.runTaskLater(info.plugin, 1L);
                        }
                    };

                default:
                    return null;
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    // Event Handling
    //
    /**
     * This method processes a new snowball, executing its launch() method and also recording it so the hit() method can be called
     * later.
     *
     * The shooter may be provided as well; this allows us to launch snowballs from places that are not a player, but associated
     * it with a player anyway.
     *
     * @param inventory The inventory slice that determines the logic type.
     * @param snowball The snowball to be launched.
     * @param info The info record that describes the snowball.
     * @return The logic associated with the snowball; may be null.
     */
    public static SnowballLogic performLaunch(InventorySlice inventory, Snowball snowball, SnowballInfo info) {
        SnowballLogic logic = createLogic(inventory);

        if (logic != null) {
            performLaunch(logic, snowball, info);
        }

        return logic;
    }

    /**
     * This overload of performLaunch takes the logic to associate with the snowball instead of an inventory.
     *
     * @param logic The logic to apply to the snowball; can't be null.
     * @param snowball The snowball to be launched.
     * @param info The info record that describes the snowball.
     */
    public static void performLaunch(SnowballLogic logic, Snowball snowball, SnowballInfo info) {
        inFlight.put(snowball, new SnowballLogicData(logic, info));
        logic.launch(snowball, info);
    }

    /**
     * This method processes the impact of a snowball, and invokes the hit() method on its logic object, if it has one.
     *
     * @param snowball The impacting snowball.
     */
    public static void performHit(Snowball snowball) {
        SnowballLogicData data = getData(Preconditions.checkNotNull(snowball));

        if (data != null) {
            try {
                data.logic.hit(snowball, data.info);
            } finally {
                inFlight.remove(snowball);
            }
        }
    }

    public static double performDamage(Snowball snowball, Entity target, double damage) {
        SnowballLogicData data = getData(Preconditions.checkNotNull(snowball));

        if (data != null) {
            return data.logic.damage(snowball, data.info, target, damage);
        }
        return damage;
    }

    /**
     * This method handles a projectile launch; it selects a logic and runs its launch method.
     *
     * @param e The event data.
     */
    public static void onProjectileLaunch(SnowballMadness plugin, ProjectileLaunchEvent e) {
        Projectile proj = e.getEntity();
        ProjectileSource psource = proj.getShooter();
        if (psource instanceof LivingEntity) {
            LivingEntity shooter = (LivingEntity) psource;

            if (proj instanceof Snowball && shooter instanceof Player) {
                Snowball snowball = (Snowball) proj;
                Player player = (Player) shooter;
                PlayerInventory inv = player.getInventory();
                int heldSlot = inv.getHeldItemSlot();
                ItemStack sourceStack = inv.getItem(heldSlot);
                if (sourceStack == null || sourceStack.getType() == Material.SNOW_BALL) {
                    InventorySlice slice = InventorySlice.fromSlot(player, heldSlot).skip(1);
                    SnowballLogic logic = performLaunch(slice, snowball,
                            new SnowballInfo(plugin, snowball.getLocation(), player));
                    replenishSnowball(plugin, inv, heldSlot);
                }
            }
        }
    }

    /**
     * This method calls tick() on each snowball that has any logic.
     */
    public static void onTick(long tickCount) {
        for (Map.Entry<Snowball, SnowballLogicData> e : inFlight.entrySet()) {
            Snowball snowball = e.getKey();
            SnowballLogic logic = e.getValue().logic;
            SnowballInfo info = e.getValue().info;
            logic.tick(snowball, info);
        }
    }

    /**
     * This method handles the damage a snowball does on impact, and can adjust that damage.
     *
     * @param e The damage event.
     */
    public static void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        Entity damagee = e.getEntity();
        Entity damager = e.getDamager();
        double damage = e.getDamage();

        if (damager instanceof Snowball) {
            double newDamage = performDamage((Snowball) damager, damagee, damage);

            if (newDamage != damage) {
                e.setDamage(newDamage);
            }
        }
    }

    /**
     * If the indicated slot contains something that is not a snowball, we don't update it. If it is empty, we put one snowball in
     * there.
     *
     * @param plugin The plugin, used to schedule the update.
     * @param inventory The inventory to update.
     * @param slotIndex The slot to update.
     */
    private static void replenishSnowball(Plugin plugin, final PlayerInventory inventory, final int slotIndex) {

        // ugh. We must delay the inventory update or it won't take.
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack replacing = inventory.getItem(slotIndex);

                if (replacing == null) {
                    inventory.setItem(slotIndex, new ItemStack(Material.SNOW_BALL));
                }
            }
        }.runTaskLater(plugin, 1);
    }

    /*
    public static void onEntityTargetPlayer(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (event.getEntity() instanceof LivingEntity) {
                LivingEntity attacker = (LivingEntity) event.getEntity();

                if ((event.getEntity().getCustomName() != null) && (player.getName() != null)) {
                    if (event.getEntity().getCustomName().startsWith(player.getName())) {
                        //we are attacking our creator
                        //good!
                    } else {
                        //we're attacking a player that's not our creator
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    */

    /**
     * This method handles a projectile hit event, and runs the hit method.
     *
     * @param e The event data.
     */
    public static void onProjectileHit(ProjectileHitEvent e) {
        Projectile proj = e.getEntity();
        if (proj instanceof Snowball) {
            performHit((Snowball) proj);
        }
    }
    ////////////////////////////////////////////////////////////////
    // Logic Association
    //
    final static WeakHashMap<Snowball, SnowballLogicData> inFlight = new WeakHashMap<Snowball, SnowballLogicData>();
    static int approximateInFlightCount = 0;

    /**
     * this class just holds the snowball logic and info for a snowball; the snowball itself must not be kept here, as this is the
     * value of a weak-hash-map keyed on the snowballs. We don't want to keep them alive.
     */
    private final static class SnowballLogicData {

        public final SnowballLogic logic;
        public final SnowballInfo info;

        public SnowballLogicData(SnowballLogic logic, SnowballInfo info) {
            this.logic = logic;
            this.info = info;
        }
    }

    /**
     * This returns the logic and shooter for a snowball that has one.
     *
     * @param snowball The snowball of interest; can be null.
     * @return The logic and info of the snowball, or null if it is an illogical snowball or it was null.
     */
    private static SnowballLogicData getData(Snowball snowball) {
        if (snowball != null) {
            return inFlight.get(snowball);
        } else {
            return null;
        }
    }
    /*     Templates for all the Attribute stuff

     AttributeInstance healthAttribute = spawned.getAttribute(Attribute.GENERIC_MAX_HEALTH);
     healthAttribute.setBaseValue(20); //HP in half-hearts

     AttributeInstance followAttribute = spawned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
     followAttribute.setBaseValue((16); //default 16, zom 40, absolute max 2048

     AttributeInstance knockAttribute = spawned.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
     knockAttribute.setBaseValue(1.0f); //default 0.0, max 1.0

     AttributeInstance speedAttribute = spawned.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
     speedAttribute.setBaseValue(0.25); //skele 0.25, zom 0.23, wither 0.6, dragon 0.7

     AttributeInstance damageAttribute = spawned.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
     //knockAttribute.setBaseValue(2.0); //damage in half-hearts

     AttributeInstance zomAttribute = spawned.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS);
     zomAttribute.setBaseValue(1.0f); //0.0-1.0 chance of spawning other zoms, zom only

     */
 /* original  army/ninja armoring code: lets you put player head on your minions

     float dropChance = info.shooter.getLevel() * 0.01f;
     ItemStack gear = info.shooter.getInventory().getHelmet();
     if (gear != null) {
     spawned.getEquipment().setHelmet(gear);
     spawned.getEquipment().setHelmetDropChance(dropChance);
     } else {
     gear = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
     SkullMeta meta = (SkullMeta) gear.getItemMeta();
     meta.setOwner(info.shooter.getName());
     gear.setItemMeta(meta);
     // OH GOD IT HAS MY FAAAAAAACE!
     spawned.getEquipment().setHelmet(gear);
     spawned.getEquipment().setHelmetDropChance(dropChance);
     }
     gear = info.shooter.getInventory().getChestplate();
     if (gear != null) {
     spawned.getEquipment().setChestplate(gear);
     spawned.getEquipment().setChestplateDropChance(dropChance);
     }
     gear = info.shooter.getInventory().getLeggings();
     if (gear != null) {
     spawned.getEquipment().setLeggings(gear);
     spawned.getEquipment().setLeggingsDropChance(dropChance);
     }
     gear = info.shooter.getInventory().getBoots();
     if (gear != null) {
     spawned.getEquipment().setBoots(gear);
     spawned.getEquipment().setBootsDropChance(dropChance);
     }
     gear = info.shooter.getInventory().getItem(0);
     if (gear != null) {
     gear = info.shooter.getInventory().getItem(0).clone();
     //we are altering the itemStack, must clone or we alter it right in our inventory!
     gear.setAmount(1);
     spawned.getEquipment().setItemInMainHand(gear);
     spawned.getEquipment().setItemInMainHandDropChance(dropChance);
     }
    
    
    the wicked blocks
    
    else if (hint.getType() == Material.IRON_BLOCK) {
                return new ItemDropSnowballLogic(
                        Material.IRON_HELMET,
                        Material.IRON_CHESTPLATE,
                        Material.IRON_LEGGINGS,
                        Material.IRON_BOOTS,
                        Material.IRON_SWORD,
                        Material.IRON_PICKAXE,
                        Material.IRON_AXE,
                        Material.IRON_SPADE,
                        Material.IRON_HOE);
            } else if (hint.getType() == Material.GOLD_BLOCK) {
                return new ItemDropSnowballLogic(
                        Material.GOLD_HELMET,
                        Material.GOLD_CHESTPLATE,
                        Material.GOLD_LEGGINGS,
                        Material.GOLD_BOOTS,
                        Material.GOLD_SWORD,
                        Material.GOLD_PICKAXE,
                        Material.GOLD_AXE,
                        Material.GOLD_SPADE,
                        Material.GOLD_HOE);
            } else if (hint.getType() == Material.DIAMOND_BLOCK) {
                return new ItemDropSnowballLogic(
                        Material.DIAMOND_HELMET,
                        Material.DIAMOND_CHESTPLATE,
                        Material.DIAMOND_LEGGINGS,
                        Material.DIAMOND_BOOTS,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_AXE,
                        Material.DIAMOND_SPADE,
                        Material.DIAMOND_HOE);
            } else if (hint.getType() == Material.EMERALD_BLOCK) {
                return new FeeshVariationsSnowballLogic(hint); //emerald block curses with silverfish
            } else if (hint.getType() == Material.REDSTONE_BLOCK) {
                return BlockEmbedSnowballLogic.fromMaterial(hint.getType()); //redstone block fires lightning
            } else if (hint.getType() == Material.OBSIDIAN) {
                return new ComeAlongSnowballLogic(Material.OBSIDIAN);
            } 
    
    
    else if (hint.getType() == Material.WHITE_SHULKER_BOX
                    || hint.getType() == Material.ORANGE_SHULKER_BOX
                    || hint.getType() == Material.MAGENTA_SHULKER_BOX
                    || hint.getType() == Material.LIGHT_BLUE_SHULKER_BOX
                    || hint.getType() == Material.YELLOW_SHULKER_BOX
                    || hint.getType() == Material.LIME_SHULKER_BOX
                    || hint.getType() == Material.PINK_SHULKER_BOX
                    || hint.getType() == Material.GRAY_SHULKER_BOX
                    || hint.getType() == Material.SILVER_SHULKER_BOX
                    || hint.getType() == Material.CYAN_SHULKER_BOX
                    || hint.getType() == Material.PURPLE_SHULKER_BOX
                    || hint.getType() == Material.BLUE_SHULKER_BOX
                    || hint.getType() == Material.BROWN_SHULKER_BOX
                    || hint.getType() == Material.GREEN_SHULKER_BOX
                    || hint.getType() == Material.RED_SHULKER_BOX
                    || hint.getType() == Material.BLACK_SHULKER_BOX) {
                return new ShulkerBoxSnowballLogic(hint);
            } 
     */
}

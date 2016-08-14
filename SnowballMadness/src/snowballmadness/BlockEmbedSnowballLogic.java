package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;

/**
 * This logic takes in a material to place and possibly a second material (for placing in the air block just above the material)
 * and a number specifying how many blocks down the embedding is to go. If nothing is specified, we assume 1 block. Higher numbers
 * attempt to place that many blocks under the point of impact. -1 means we go down to void.
 *
 *
 * @author christopherjohnson, somewhat
 */
public class BlockEmbedSnowballLogic extends SnowballLogic {

    private final Material toPlace;
    private final Material toCap;
    private final float embedDepth;

    public BlockEmbedSnowballLogic(Material toPlace, Material toCap, float embedDepth) {
        this.toPlace = Preconditions.checkNotNull(toPlace);
        this.toCap = Preconditions.checkNotNull(toCap);
        this.embedDepth = embedDepth;
    }

    /**
     * This method creates a logic for a material that you supply; we have many special cases here!
     *
     * @param material The material being used with the snowball.
     * @return The new logic.
     */
    public static BlockEmbedSnowballLogic fromMaterial(Material material) {
        switch (material) {
            case WOOD_SPADE:
                return new BlockEmbedSnowballLogic(Material.GRASS_PATH, Material.AIR, 1);

            case LADDER:
                return new BlockEmbedSnowballLogic(material, material, 256) {
                    @Override
                    protected void placeShaftBlock(Block block) {
                        if (!(block.getType().isSolid())) {
                            if (block.getRelative(BlockFace.SOUTH).getType().isSolid()) {
                                super.placeShaftBlock(block);
                                block.setData((byte) 2);
                            }
                            if (block.getRelative(BlockFace.NORTH).getType().isSolid()) {
                                super.placeShaftBlock(block);
                                block.setData((byte) 3);
                            }
                            if (block.getRelative(BlockFace.EAST).getType().isSolid()) {
                                super.placeShaftBlock(block);
                                block.setData((byte) 4);
                            }
                            if (block.getRelative(BlockFace.WEST).getType().isSolid()) {
                                super.placeShaftBlock(block);
                                block.setData((byte) 5);
                            }
                        }
                    }

                    protected void placeCapBlock(Block block) {

                        if (block.getRelative(BlockFace.SOUTH).getType().isSolid()) {
                            super.placeCapBlock(block);
                            block.setData((byte) 2);
                        }
                        if (block.getRelative(BlockFace.NORTH).getType().isSolid()) {
                            super.placeCapBlock(block);
                            block.setData((byte) 3);
                        }
                        if (block.getRelative(BlockFace.EAST).getType().isSolid()) {
                            super.placeCapBlock(block);
                            block.setData((byte) 4);
                        }
                        if (block.getRelative(BlockFace.WEST).getType().isSolid()) {
                            super.placeCapBlock(block);
                            block.setData((byte) 5);
                        }
                        //if none of the above, we haven't placed a block
                        //worst case we repeatedly place it, which is probably fine
                        // ladders need a special case to place them on the side of
                        // the shaft! 2 = north, 3 = south, 4 = west, 5 = east.
                        // There has to be a better way than this!
                    }
                };


            case VINE:
                return new BlockEmbedSnowballLogic(material, material, 256) {
                    @Override
                    protected void placeShaftBlock(Block block) {
                        if (!(block.getType().isSolid())) {
                            byte vinemask = 0;
                            if (block.getRelative(BlockFace.SOUTH).getType().isSolid()) {
                                super.placeShaftBlock(block);
                                vinemask = (byte) (vinemask + 1);
                                block.setData(vinemask);
                            }
                            if (block.getRelative(BlockFace.NORTH).getType().isSolid()) {
                                super.placeShaftBlock(block);
                                vinemask = (byte) (vinemask + 4);
                                block.setData(vinemask);
                            }
                            if (block.getRelative(BlockFace.EAST).getType().isSolid()) {
                                super.placeShaftBlock(block);
                                vinemask = (byte) (vinemask + 8);
                                block.setData(vinemask);
                            }
                            if (block.getRelative(BlockFace.WEST).getType().isSolid()) {
                                super.placeShaftBlock(block);
                                vinemask = (byte) (vinemask + 2);
                                block.setData(vinemask);
                            }
                        }
                    }

                    protected void placeCapBlock(Block block) {
                        byte vinemask = 0;
                        if (block.getRelative(BlockFace.SOUTH).getType().isSolid()) {
                            super.placeCapBlock(block);
                            vinemask = (byte) (vinemask + 1);
                            block.setData(vinemask);
                        }
                        if (block.getRelative(BlockFace.NORTH).getType().isSolid()) {
                            super.placeCapBlock(block);
                            vinemask = (byte) (vinemask + 4);
                            block.setData(vinemask);
                        }
                        if (block.getRelative(BlockFace.EAST).getType().isSolid()) {
                            super.placeCapBlock(block);
                            vinemask = (byte) (vinemask + 8);
                            block.setData(vinemask);
                        }
                        if (block.getRelative(BlockFace.WEST).getType().isSolid()) {
                            super.placeCapBlock(block);
                            vinemask = (byte) (vinemask + 2);
                            block.setData(vinemask);
                        }
                    }
                };

            case COAL_ORE:
            case IRON_ORE:
                return new BlockEmbedSnowballLogic(Material.AIR, Material.AIR, 1);
            case REDSTONE_ORE:
                return new BlockEmbedSnowballLogic(Material.AIR, Material.AIR, 2);
            case LAPIS_ORE:
                return new BlockEmbedSnowballLogic(Material.AIR, Material.AIR, 3);
            case EMERALD_ORE:
                return new BlockEmbedSnowballLogic(Material.AIR, Material.AIR, 4);
            case DIAMOND_ORE:
                return new BlockEmbedSnowballLogic(Material.AIR, Material.AIR, 8);
            //new version is just a trap rather than an OP deathtrap

            case REDSTONE_TORCH_ON:
            case REDSTONE_TORCH_OFF:
                return new BlockEmbedSnowballLogic(Material.COAL_BLOCK, Material.AIR, 1) {
                    @Override
                    protected void placeCapBlock(Block block) {
                        block.getWorld().strikeLightning(block.getLocation());
                    }
                };

            default:
                return new BlockEmbedSnowballLogic(material, material, 1);
        }
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        Location loc = snowball.getLocation().clone();
        Block block = loc.getBlock();

        if (block.getType() == Material.LONG_GRASS) {
            block.setType(Material.AIR);
        } else {
            if (block.getType() == Material.AIR && block.getY() > 1) {
                loc.setY(loc.getY() - 1);
                block = loc.getBlock();
            }

            if (block.getType() != Material.AIR) {
                loc.setY(loc.getY() + 1);
                block = loc.getBlock();
            }

            if (block.getType() == Material.AIR) {
                placeCapBlock(block);//we set the cap and prepare to step downward
                loc.setY(loc.getY() - 1);
                block = loc.getBlock();
            }

            float decrement = embedDepth;

            while (!((loc.getY() < 0) || (decrement == 0) || (decrement > 0 && block.getType() == Material.BEDROCK))) {
                placeShaftBlock(block);
                //just stepped down, it's above zero and a replaceable block
                //or bedrock with embedDepth negative. place that sucker!            
                decrement = decrement - 1;
                loc.setY(loc.getY() - 1);
                block = loc.getBlock();
                //step down and go back to re-check the while
            }
        }
    }

    /* //this is the invocation for lightning strike */
    /**
     * This method applies the cap block; you can override it do something different though, like strike with lightning.
     *
     * @param block The block to be updated.
     */
    protected void placeCapBlock(Block block) {
        block.setType(toCap);
    }

    /**
     * This method applies the shaft block; you can override it to do something fancier.
     *
     * @param block The block to be updated.
     */
    protected void placeShaftBlock(Block block) {
        block.setType(toPlace);
    }
}

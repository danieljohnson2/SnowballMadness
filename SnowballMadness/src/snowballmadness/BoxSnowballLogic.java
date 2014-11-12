/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snowballmadness;

import com.google.common.base.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;

/**
 * This logic makes a box out of the material you give it, varying the
 * construction method by type. For starters, it just makes a box in open
 * air blocks, replacing all water or lava inside it with air.
 * 
 * @author christopherjohnson
 */
public class BoxSnowballLogic extends SnowballLogic {

    private final Material toPlace;

    public BoxSnowballLogic(Material toPlace) {
        this.toPlace = Preconditions.checkNotNull(toPlace);
    }

    @Override
    public void hit(Snowball snowball, SnowballInfo info) {
        super.hit(snowball, info);

        Location loc = snowball.getLocation().clone();
        int dimensions = 1;
        dimensions = (int) (dimensions * info.power);
        //we're gonna loop and make a solid box, where possible in vertical strips
        //in case Minecraft sends stuff piecemeal: not to repeatedly update multiple
        //chunks for each layer.

        int beginX = loc.getBlockX() - dimensions;
        int beginY = loc.getBlockY() - dimensions;
        int beginZ = loc.getBlockZ() - dimensions;
        int endX = beginX + (dimensions * 2);
        int endY = beginY + (dimensions * 2);
        int endZ = beginZ + (dimensions * 2);
        if (beginY < 1) {
            beginY = 1;
        }
        if (endY > 256) {
            endY = 256;
        }
        //Y can go out of bounds, clamp it to permissible heights
        //these are common to all box models, we can have some special cases

        //CASE, hollow box whose shell does not replace anything but air
        //air inside displaces water and lava
        int iterateX = beginX;
        while (iterateX <= endX) {
            loc.setX(iterateX);
            int iterateZ = beginZ;
            while (iterateZ <= endZ) {
                loc.setZ(iterateZ);
                int iterateY = beginY;
                if (iterateX == beginX || iterateX == endX || iterateZ == beginZ || iterateZ == endZ) {
                    //we are doing a wall, hence we fill in all Y
                    while (iterateY <= endY) {
                        loc.setY(iterateY);
                        Block target = loc.getBlock();
                        if ((target.getType() == Material.AIR)||(target.getType() == Material.WATER)||(target.getType() == Material.STATIONARY_WATER)||(target.getType() == Material.LAVA)) {
                            target.setType(toPlace);
                        }
                        iterateY = iterateY + 1;
                    }
                } else {
                    //we aren't doing a wall, hence we fill in top and bottom blocks only
                    //however, boxes can make air inside water or lava
                    loc.setY(iterateY);
                    Block target = loc.getBlock();
                    if ((target.getType() == Material.AIR)||(target.getType() == Material.WATER)||(target.getType() == Material.STATIONARY_WATER)||(target.getType() == Material.LAVA)) {
                        target.setType(toPlace);
                    }
                    iterateY = iterateY + 1;
                    //bottom plane
                    while (iterateY < endY) {
                        loc.setY(iterateY);
                        target = loc.getBlock();
                        if ((target.getType() == Material.AIR)||(target.getType() == Material.WATER)||(target.getType() == Material.STATIONARY_WATER)||(target.getType() == Material.LAVA)) {
                            target.setType(Material.AIR);
                        }
                        iterateY = iterateY + 1;
                    }
                    //hollow space, replaces water and lava, exits when top plane is reached
                    loc.setY(iterateY);
                    target = loc.getBlock();
                    if ((target.getType() == Material.AIR)||(target.getType() == Material.WATER)||(target.getType() == Material.STATIONARY_WATER)||(target.getType() == Material.LAVA)) {
                        target.setType(toPlace);
                    }
                    //top plane and done with this vertical slice
                }
                iterateZ = iterateZ + 1;
                //done with a Z row
            }
            iterateX = iterateX + 1;
            //done with an X row
        }
        //end of hollow box case
        
    }
}

//public boolean isFluid(Material toTest){
//this.Material = target.getType();
//return ((target.getType() == Material.AIR)||(target.getType() == Material.WATER)||(target.getType() == Material.STATIONARY_WATER)||(target.getType() == Material.LAVA))
//}
//nope, but you can see what I'm going for here
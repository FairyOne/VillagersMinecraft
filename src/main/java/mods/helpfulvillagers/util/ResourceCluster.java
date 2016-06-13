package mods.helpfulvillagers.util;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ResourceCluster
{
  public World world;
  public ChunkCoordinates coords;
  public ChunkCoordinates lowestCoords;
  public Block startBlock;
  public ArrayList blockCluster;
  public boolean builtFlag;
  
  public ResourceCluster(World world)
  {
    this.world = world;
  }
  
  public ResourceCluster(World world, ChunkCoordinates coords)
  {
    this.world = world;
    this.coords = coords;
    this.lowestCoords = coords;
    this.startBlock = world.func_147439_a(coords.field_71574_a, coords.field_71572_b, coords.field_71573_c);
    this.blockCluster = new ArrayList();
    this.builtFlag = false;
  }
  
  public ArrayList getAdjacent()
  {
    ArrayList blocks = new ArrayList();
    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        for (int z = -1; z <= 1; z++)
        {
          Block block = this.world.func_147439_a(this.coords.field_71574_a + x, this.coords.field_71572_b + y, this.coords.field_71573_c + z);
          blocks.add(block);
        }
      }
    }
    return blocks;
  }
  
  private ArrayList getAdjacentCoords(ChunkCoordinates coords)
  {
    ArrayList adjacent = new ArrayList();
    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        for (int z = -1; z <= 1; z++)
        {
          ChunkCoordinates coord = new ChunkCoordinates(coords.field_71574_a + x, coords.field_71572_b + y, coords.field_71573_c + z);
          adjacent.add(coord);
        }
      }
    }
    return adjacent;
  }
  
  public void buildCluster()
  {
    if (!this.startBlock.equals(Blocks.field_150350_a)) {
      buildCluster(this.coords);
    }
  }
  
  private void buildCluster(ChunkCoordinates coords)
  {
    Block currentBlock = this.world.func_147439_a(coords.field_71574_a, coords.field_71572_b, coords.field_71573_c);
    if ((!this.blockCluster.contains(coords)) && (Block.func_149682_b(currentBlock) == Block.func_149682_b(this.startBlock)))
    {
      if (coords.field_71572_b < this.lowestCoords.field_71572_b) {
        this.lowestCoords = coords;
      }
      this.blockCluster.add(coords);
      ArrayList adjacent = getAdjacentCoords(coords);
      for (int i = 0; i < adjacent.size(); i++) {
        if (!adjacent.get(i).equals(coords)) {
          buildCluster((ChunkCoordinates)adjacent.get(i));
        }
      }
    }
  }
  
  public boolean matchesCluster(ResourceCluster cluster)
  {
    ArrayList otherCluster = cluster.blockCluster;
    if (this.blockCluster.size() != otherCluster.size()) {
      return false;
    }
    for (int i = 0; i < otherCluster.size(); i++)
    {
      ChunkCoordinates otherCoords = (ChunkCoordinates)otherCluster.get(i);
      if (!this.blockCluster.contains(otherCoords)) {
        return false;
      }
    }
    return true;
  }
  
  public NBTTagList writeToNBT(NBTTagList par1NBTTagList)
  {
    NBTTagCompound nbttagcompound = new NBTTagCompound();
    int[] coords = { this.coords.field_71574_a, this.coords.field_71572_b, this.coords.field_71573_c };
    nbttagcompound.func_74783_a("Coords", coords);
    par1NBTTagList.func_74742_a(nbttagcompound);
    
    return par1NBTTagList;
  }
  
  public void readFromNBT(NBTTagList par1NBTTagList)
  {
    NBTTagCompound nbttagcompound = par1NBTTagList.func_150305_b(0);
    int[] coords = nbttagcompound.func_74759_k("Coords");
    this.coords = new ChunkCoordinates(coords[0], coords[1], coords[2]);
    this.lowestCoords = this.coords;
    
    this.blockCluster = new ArrayList();
    this.builtFlag = false;
  }
}

package mods.helpfulvillagers.ai;

import java.util.ArrayList;
import mods.helpfulvillagers.entity.EntityFarmer;
import mods.helpfulvillagers.inventory.InventoryVillager;
import mods.helpfulvillagers.util.AIHelper;
import mods.helpfulvillagers.util.ResourceCluster;
import mods.helpfulvillagers.village.GuildHall;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public class EntityAIFarmer
  extends EntityAIWorker
{
  private EntityFarmer farmer;
  private ArrayList<ChunkCoordinates> farmCoords = new ArrayList();
  
  public EntityAIFarmer(EntityFarmer farmer)
  {
    super(farmer);
    this.farmer = farmer;
    this.target = null;
  }
  
  protected boolean gather()
  {
    if (this.farmer.homeGuildHall == null) {
      return idle();
    }
    if (this.farmer.insideHall())
    {
      ChunkCoordinates exit = this.farmer.homeGuildHall.entranceCoords;
      if (exit == null) {
        exit = AIHelper.getRandInsideCoords(this.farmer);
      }
      this.farmer.moveTo(exit, this.speed);
    }
    else if (this.farmer.currentResource == null)
    {
      findFarm();
    }
    else
    {
      this.target = ((ChunkCoordinates)this.farmCoords.get(0));
      int distX = AIHelper.findDistance((int)this.farmer.field_70165_t, this.target.field_71574_a);
      int distY = AIHelper.findDistance((int)this.farmer.field_70163_u, this.target.field_71572_b);
      int distZ = AIHelper.findDistance((int)this.farmer.field_70161_v, this.target.field_71573_c);
      if ((distX > 3) || (distY > 3) || (distZ > 3)) {
        this.farmer.moveTo(this.target, this.speed);
      } else {
        harvestCrops();
      }
    }
    return idle();
  }
  
  private void findFarm()
  {
    if (this.target == null) {
      this.target = AIHelper.getRandInsideCoords(this.farmer);
    }
    if (this.target != null) {
      this.farmer.moveTo(this.target, this.speed);
    }
    this.farmer.updateBoxes();
    if ((this.farmer.searchBox != null) && (this.farmer.field_70170_p.func_72875_a(this.farmer.searchBox, Material.field_151578_c)))
    {
      this.farmer.currentResource = getNewResource();
      if (this.farmer.currentResource != null)
      {
        this.farmCoords.addAll(this.farmer.currentResource.blockCluster);
        this.farmer.func_70661_as().func_75499_g();
      }
    }
    if ((Math.abs(this.farmer.field_70165_t - this.target.field_71574_a) <= 5.0D) && (Math.abs(this.farmer.field_70161_v - this.target.field_71573_c) <= 5.0D)) {
      this.target = null;
    }
  }
  
  private ResourceCluster getNewResource()
  {
    ArrayList boxCoords = this.farmer.getValidCoords();
    double closestDist = Double.MAX_VALUE;
    ResourceCluster closestValidCluster = null;
    for (int i = 0; i < boxCoords.size(); i++)
    {
      ChunkCoordinates currCoords = (ChunkCoordinates)boxCoords.get(i);
      ResourceCluster currentCluster = new ResourceCluster(this.farmer.field_70170_p, currCoords);
      currentCluster.buildCluster();
      boolean visited = false;
      if (this.farmer.visitedFarms != null) {
        for (int j = 0; j < this.farmer.visitedFarms.size(); j++)
        {
          ResourceCluster farm = (ResourceCluster)this.farmer.visitedFarms.get(j);
          if (farm.matchesCluster(currentCluster))
          {
            visited = true;
            break;
          }
        }
      }
      if (!visited)
      {
        double dist = this.farmer.func_70011_f(currCoords.field_71574_a, currCoords.field_71572_b, currCoords.field_71573_c);
        if (dist < closestDist)
        {
          closestDist = dist;
          closestValidCluster = currentCluster;
        }
      }
    }
    return closestValidCluster;
  }
  
  private void harvestCrops()
  {
    boolean shouldSwing = false;
    ChunkCoordinates aboveCoords = new ChunkCoordinates(this.target.field_71574_a, this.target.field_71572_b + 1, this.target.field_71573_c);
    Block currentCrop = this.farmer.field_70170_p.func_147439_a(aboveCoords.field_71574_a, aboveCoords.field_71572_b, aboveCoords.field_71573_c);
    if (!canHarvest(aboveCoords))
    {
      this.farmCoords.remove(0);
      if (this.farmCoords.isEmpty())
      {
        this.farmer.visitedFarms.add(this.farmer.currentResource);
        this.farmer.currentResource = null;
        this.target = null;
        this.previousTime = 0;
        this.currentTime = 0;
      }
      return;
    }
    if (this.farmer.func_70661_as().func_75500_f())
    {
      this.farmer.func_70671_ap().func_75650_a(this.farmer.currentResource.lowestCoords.field_71574_a, this.farmer.currentResource.lowestCoords.field_71572_b, this.farmer.currentResource.lowestCoords.field_71573_c, 10.0F, 10.0F);
      shouldSwing = true;
      if (this.previousTime <= 0)
      {
        this.previousTime = this.farmer.field_70173_aa;
        this.harvestTime = this.farmer.getHarvestTime();
      }
    }
    else
    {
      shouldSwing = false;
    }
    if (this.previousTime > 0)
    {
      this.currentTime = this.farmer.field_70173_aa;
      if (!this.farmCoords.isEmpty())
      {
        if (this.currentTime - this.previousTime >= this.harvestTime)
        {
          this.previousTime = this.currentTime;
          this.harvestTime = this.farmer.getHarvestTime();
          if (canHarvest(aboveCoords)) {
            if (!(currentCrop instanceof BlockStem))
            {
              ArrayList<ItemStack> cropDrops = currentCrop.getDrops(this.farmer.field_70170_p, aboveCoords.field_71574_a, aboveCoords.field_71572_b, aboveCoords.field_71573_c, 7, 0);
              ItemStack foundSeed = null;
              for (ItemStack i : cropDrops) {
                if ((i.func_77973_b() instanceof IPlantable))
                {
                  foundSeed = i;
                  break;
                }
              }
              if (foundSeed != null) {
                this.farmer.seedToPlant = ((IPlantable)foundSeed.func_77973_b());
              }
              AIHelper.breakBlock(aboveCoords, this.farmer);
              plantCrop(aboveCoords);
            }
            else
            {
              BlockStem stem = (BlockStem)currentCrop;
              int dir = stem.func_149873_e(this.farmer.field_70170_p, aboveCoords.field_71574_a, aboveCoords.field_71572_b, aboveCoords.field_71573_c);
              switch (dir)
              {
              case 0: 
                aboveCoords.field_71574_a -= 1;
                break;
              case 1: 
                aboveCoords.field_71574_a += 1;
                break;
              case 2: 
                aboveCoords.field_71573_c -= 1;
                break;
              case 3: 
                aboveCoords.field_71573_c += 1;
              }
              AIHelper.breakBlock(aboveCoords, this.farmer);
            }
          }
          this.farmCoords.remove(0);
          if (this.farmCoords.isEmpty())
          {
            this.farmer.visitedFarms.add(this.farmer.currentResource);
            this.farmer.currentResource = null;
            this.target = null;
            this.previousTime = 0;
            this.currentTime = 0;
          }
        }
      }
      else
      {
        this.farmer.visitedFarms.add(this.farmer.currentResource);
        this.farmer.currentResource = null;
        this.target = null;
        this.previousTime = 0;
        this.currentTime = 0;
      }
    }
    if (shouldSwing) {
      this.farmer.func_71038_i();
    } else {
      this.previousTime = 0;
    }
  }
  
  private boolean canHarvest(ChunkCoordinates coords)
  {
    Block block = this.farmer.field_70170_p.func_147439_a(coords.field_71574_a, coords.field_71572_b, coords.field_71573_c);
    if ((block instanceof BlockCrops)) {
      return this.farmer.field_70170_p.func_72805_g(coords.field_71574_a, coords.field_71572_b, coords.field_71573_c) >= 7;
    }
    if ((block instanceof BlockReed)) {
      return true;
    }
    if ((block instanceof BlockNetherWart))
    {
      BlockNetherWart wart = (BlockNetherWart)block;
      return wart.getPlantMetadata(this.farmer.field_70170_p, coords.field_71574_a, coords.field_71572_b, coords.field_71573_c) >= 3;
    }
    if ((block instanceof BlockStem))
    {
      BlockStem stem = (BlockStem)block;
      return stem.func_149873_e(this.farmer.field_70170_p, coords.field_71574_a, coords.field_71572_b, coords.field_71573_c) >= 0;
    }
    return false;
  }
  
  private void plantCrop(ChunkCoordinates coords)
  {
    if (!this.farmer.field_70170_p.func_147437_c(coords.field_71574_a, coords.field_71572_b, coords.field_71573_c)) {
      return;
    }
    if (this.farmer.seedToPlant != null)
    {
      EnumPlantType plantType = this.farmer.seedToPlant.getPlantType(this.farmer.field_70170_p, coords.field_71574_a, coords.field_71572_b, coords.field_71573_c);
      if ((plantType == EnumPlantType.Nether) && (this.farmer.field_70170_p.func_147439_a(coords.field_71574_a, coords.field_71572_b - 1, coords.field_71573_c) != Blocks.field_150425_aM)) {
        return;
      }
      if ((plantType == EnumPlantType.Crop) && (this.farmer.field_70170_p.func_147439_a(coords.field_71574_a, coords.field_71572_b - 1, coords.field_71573_c) != Blocks.field_150458_ak)) {
        return;
      }
      Item plantItem = (Item)this.farmer.seedToPlant;
      Block newPlant = this.farmer.seedToPlant.getPlant(this.farmer.field_70170_p, coords.field_71574_a, coords.field_71572_b, coords.field_71573_c);
      if ((this.farmer.lastSeedIndex < 0) || (this.farmer.inventory.func_70301_a(this.farmer.lastSeedIndex) == null) || (!this.farmer.inventory.func_70301_a(this.farmer.lastSeedIndex).func_77973_b().equals(plantItem))) {
        this.farmer.lastSeedIndex = this.farmer.inventory.containsItem(new ItemStack(plantItem));
      }
      if (this.farmer.lastSeedIndex >= 0)
      {
        this.farmer.inventory.decrementSlot(this.farmer.lastSeedIndex);
        this.farmer.field_70170_p.func_147449_b(coords.field_71574_a, coords.field_71572_b, coords.field_71573_c, newPlant);
      }
    }
  }
}

package mods.helpfulvillagers.ai;

import java.util.ArrayList;
import mods.helpfulvillagers.entity.EntityLumberjack;
import mods.helpfulvillagers.util.AIHelper;
import mods.helpfulvillagers.util.ResourceCluster;
import mods.helpfulvillagers.village.GuildHall;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class EntityAILumberjack
  extends EntityAIWorker
{
  private EntityLumberjack lumberjack;
  private int searchLimit;
  
  public EntityAILumberjack(EntityLumberjack lumberjack)
  {
    super(lumberjack);
    this.lumberjack = lumberjack;
    this.currentTime = 0;
    this.previousTime = 0;
    this.harvestTime = 0.0F;
    this.searchLimit = 20;
  }
  
  protected boolean gather()
  {
    if (this.lumberjack.homeGuildHall == null) {
      return idle();
    }
    if (this.lumberjack.insideHall())
    {
      ChunkCoordinates exit = this.lumberjack.homeGuildHall.entranceCoords;
      if (exit == null) {
        exit = AIHelper.getRandInsideCoords(this.lumberjack);
      }
      this.lumberjack.moveTo(exit, this.speed);
    }
    else if (this.lumberjack.currentResource == null)
    {
      findTree();
    }
    else
    {
      int distX = AIHelper.findDistance((int)this.lumberjack.field_70165_t, this.lumberjack.currentResource.coords.field_71574_a);
      int distZ = AIHelper.findDistance((int)this.lumberjack.field_70161_v, this.lumberjack.currentResource.coords.field_71573_c);
      if ((distX > 5) || (distZ > 5)) {
        moveToTree();
      } else {
        chopTree();
      }
    }
    return idle();
  }
  
  private void findTree()
  {
    if (this.target == null) {
      this.target = AIHelper.getRandOutsideCoords(this.lumberjack, this.searchLimit);
    }
    if (this.target != null) {
      this.lumberjack.moveTo(this.target, this.speed);
    }
    if ((this.lumberjack.searchBox != null) && (this.lumberjack.field_70170_p.func_72875_a(this.lumberjack.searchBox, Material.field_151575_d)) && 
      (!AIHelper.isInRangeOfAnyVillage(this.lumberjack.field_70165_t, this.lumberjack.field_70163_u, this.lumberjack.field_70161_v)))
    {
      this.lumberjack.currentResource = getNewResource();
      if (this.lumberjack.currentResource != null)
      {
        this.searchLimit = 20;
        this.lumberjack.foundTree = true;
        this.lumberjack.func_70661_as().func_75499_g();
      }
    }
    if ((Math.abs(this.lumberjack.field_70165_t - this.target.field_71574_a) <= 5.0D) && (Math.abs(this.lumberjack.field_70161_v - this.target.field_71573_c) <= 5.0D))
    {
      this.target = null;
      this.searchLimit += 10;
    }
  }
  
  private void moveToTree()
  {
    this.target = this.lumberjack.currentResource.lowestCoords;
    this.lumberjack.moveTo(this.target, this.speed);
  }
  
  private ResourceCluster getNewResource()
  {
    ArrayList boxCoords = this.lumberjack.getValidCoords();
    double closestDist = Double.MAX_VALUE;
    ResourceCluster closestValidCluster = null;
    for (int i = 0; i < boxCoords.size(); i++)
    {
      ChunkCoordinates currCoords = (ChunkCoordinates)boxCoords.get(i);
      double dist = this.lumberjack.func_70011_f(currCoords.field_71574_a, currCoords.field_71572_b, currCoords.field_71573_c);
      if (dist < closestDist)
      {
        ResourceCluster currentCluster = new ResourceCluster(this.lumberjack.field_70170_p, (ChunkCoordinates)boxCoords.get(i));
        ArrayList sideBlocks = currentCluster.getAdjacent();
        for (int j = 0; j < sideBlocks.size(); j++)
        {
          Block currentBlock = (Block)sideBlocks.get(j);
          if ((currentBlock.equals(Blocks.field_150347_e)) || (currentBlock.equals(Blocks.field_150344_f)) || (currentBlock.equals(Blocks.field_150458_ak)) || 
            (currentBlock.equals(Blocks.field_150422_aJ)) || (currentBlock.equals(Blocks.field_150396_be)) || (currentBlock.equals(Blocks.field_150466_ao)) || 
            (currentBlock.equals(Blocks.field_150454_av)) || (currentBlock.equals(Blocks.field_150342_X)) || (currentBlock.equals(Blocks.field_150486_ae)) || 
            (currentBlock.equals(Blocks.field_150462_ai)) || ((currentBlock instanceof BlockStairs)))
          {
            currentCluster = null;
            break;
          }
        }
        if (currentCluster != null)
        {
          closestValidCluster = currentCluster;
          closestDist = dist;
        }
      }
    }
    return closestValidCluster;
  }
  
  private void chopTree()
  {
    this.lumberjack.moveTo(this.lumberjack.currentResource.lowestCoords, this.speed);
    boolean shouldSwing = false;
    if (!this.lumberjack.currentResource.builtFlag)
    {
      this.lumberjack.currentResource.buildCluster();
      this.lumberjack.currentResource.builtFlag = true;
    }
    if (this.lumberjack.func_70661_as().func_75500_f())
    {
      this.lumberjack.func_70671_ap().func_75650_a(this.lumberjack.currentResource.lowestCoords.field_71574_a, this.lumberjack.currentResource.lowestCoords.field_71572_b, this.lumberjack.currentResource.lowestCoords.field_71573_c, 10.0F, 10.0F);
      shouldSwing = true;
      if (this.previousTime <= 0)
      {
        this.previousTime = this.lumberjack.field_70173_aa;
        this.harvestTime = (60.0F / this.lumberjack.getCurrentItem().func_77973_b().getDigSpeed(this.lumberjack.getCurrentItem(), this.lumberjack.currentResource.startBlock, this.lumberjack.getCurrentItem().func_77973_b().func_77647_b(0)));
      }
    }
    else
    {
      shouldSwing = false;
    }
    if (this.previousTime > 0)
    {
      this.currentTime = this.lumberjack.field_70173_aa;
      if (!this.lumberjack.currentResource.blockCluster.isEmpty())
      {
        if (this.currentTime - this.previousTime >= this.harvestTime)
        {
          this.previousTime = this.currentTime;
          this.harvestTime = (60.0F / this.lumberjack.getCurrentItem().func_77973_b().getDigSpeed(this.lumberjack.getCurrentItem(), this.lumberjack.currentResource.startBlock, this.lumberjack.getCurrentItem().func_77973_b().func_77647_b(0)));
          ChunkCoordinates currentCoords = (ChunkCoordinates)this.lumberjack.currentResource.blockCluster.get(0);
          Block currentBlock = this.lumberjack.field_70170_p.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c);
          int metadata = this.lumberjack.field_70170_p.func_72805_g(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c);
          if (Block.func_149682_b(currentBlock) == Block.func_149682_b(this.lumberjack.currentResource.startBlock))
          {
            this.lumberjack.currentResource.blockCluster.remove(0);
            AIHelper.breakBlock(currentCoords, this.lumberjack);
          }
        }
      }
      else
      {
        this.lumberjack.lastResource = this.lumberjack.currentResource;
        this.lumberjack.currentResource = null;
        this.target = null;
        this.previousTime = 0;
        this.currentTime = 0;
      }
    }
    if (shouldSwing) {
      this.lumberjack.func_71038_i();
    } else {
      this.previousTime = 0;
    }
  }
}

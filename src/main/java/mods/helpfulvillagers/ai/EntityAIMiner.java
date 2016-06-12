package mods.helpfulvillagers.ai;

import java.util.ArrayList;
import java.util.Iterator;
import mods.helpfulvillagers.crafting.CraftItem;
import mods.helpfulvillagers.econ.VillageEconomy;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.entity.EntityMiner;
import mods.helpfulvillagers.enums.EnumActivity;
import mods.helpfulvillagers.inventory.InventoryVillager;
import mods.helpfulvillagers.util.AIHelper;
import mods.helpfulvillagers.util.ResourceCluster;
import mods.helpfulvillagers.village.GuildHall;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

public class EntityAIMiner
  extends EntityAIWorker
{
  private EntityMiner miner;
  private final int WAIT_TIME = 40;
  private int previousWait;
  private Block lastBlock;
  private Block lastAbove;
  private Block lastAbove2;
  private ArrayList surroundingCoords;
  private boolean reset;
  private boolean skipResource;
  
  public EntityAIMiner(EntityMiner miner)
  {
    super(miner);
    this.miner = miner;
    miner.shaftIndex = miner.nearestShaftCoord();
    this.previousWait = 0;
    this.lastBlock = null;
    this.lastAbove = null;
    this.lastAbove2 = null;
    this.surroundingCoords = new ArrayList();
    this.reset = true;
    this.skipResource = false;
    func_75248_a(1);
  }
  
  protected boolean idle()
  {
    this.villager.currentActivity = EnumActivity.IDLE;
    if ((!this.villager.field_70170_p.field_72995_K) && (this.villager.homeVillage == null)) {
      return false;
    }
    this.villager.checkGuildHall();
    if (this.villager.homeGuildHall == null) {
      return false;
    }
    if ((this.villager.currentCraftItem != null) && (this.villager.currentCraftItem.getPriority() >= 1))
    {
      if (((this.readyToCraft) || (this.readyToSmelt)) && (!this.villager.nearHall()))
      {
        mReturn();
        return false;
      }
      if (((this.readyToCraft) || (this.readyToSmelt)) && (this.villager.nearHall()))
      {
        this.villager.currentActivity = EnumActivity.CRAFT;
        return true;
      }
      if (!this.craftCheck)
      {
        this.villager.currentActivity = EnumActivity.CRAFT;
        this.craftCheck = true;
        return true;
      }
    }
    if ((this.villager.inventory.isFull()) || (!this.villager.hasTool))
    {
      if (this.villager.nearHall())
      {
        if ((this.villager.currentCraftItem != null) && (!this.craftCheck))
        {
          this.villager.currentActivity = EnumActivity.CRAFT;
          this.craftCheck = true;
          return true;
        }
        if ((!this.villager.inventory.isEmpty()) || (!this.villager.hasTool))
        {
          this.villager.currentActivity = EnumActivity.STORE;
          this.craftCheck = false;
          return true;
        }
        this.craftCheck = false;
        return false;
      }
      mReturn();
      this.craftCheck = false;
      return false;
    }
    if (this.villager.field_70170_p.func_72935_r())
    {
      this.villager.currentActivity = EnumActivity.GATHER;
      this.craftCheck = false;
      return true;
    }
    if (!this.villager.nearHall())
    {
      mReturn();
      this.craftCheck = false;
      return false;
    }
    if ((this.villager.currentCraftItem != null) && (!this.craftCheck))
    {
      this.villager.currentActivity = EnumActivity.CRAFT;
      this.craftCheck = true;
      return true;
    }
    if ((!this.villager.inventory.isEmpty()) || (!this.villager.hasTool))
    {
      this.villager.currentActivity = EnumActivity.STORE;
      this.craftCheck = false;
      return true;
    }
    this.craftCheck = false;
    return true;
  }
  
  protected boolean gather()
  {
    if (this.miner.topCoords == null)
    {
      findMine();
    }
    else if (this.miner.shaftCoords.isEmpty())
    {
      buildStairs(this.miner.topCoords, this.miner.topDir);
    }
    else if (!this.skipResource)
    {
      if (this.miner.currentResource == null) {
        getNewResource();
      }
      if (this.miner.currentResource == null)
      {
        if (this.miner.returnPath.isEmpty()) {
          digSection(this.miner.shaftIndex, true);
        } else {
          digTunnel(true);
        }
      }
      else if (!this.miner.tunnelCoords.isEmpty()) {
        digTunnel(false);
      } else {
        mineResource();
      }
    }
    else
    {
      if (this.miner.returnPath.isEmpty()) {
        digSection(this.miner.shaftIndex, true);
      } else {
        digTunnel(true);
      }
      if ((int)this.miner.field_70163_u < (int)this.miner.field_70137_T) {
        this.skipResource = false;
      }
    }
    return idle();
  }
  
  protected boolean store()
  {
    if (this.miner.homeGuildHall == null) {
      return idle();
    }
    TileEntityChest chest = this.miner.homeGuildHall.getAvailableChest();
    if ((!this.miner.inventory.isEmpty()) || (!this.miner.hasTool))
    {
      if (chest != null) {
        this.miner.moveTo(new ChunkCoordinates(chest.field_145851_c, chest.field_145848_d, chest.field_145849_e), this.speed);
      } else {
        this.miner.changeGuildHall = true;
      }
      if ((chest != null) && (AIHelper.findDistance((int)this.miner.field_70165_t, chest.field_145851_c) <= 2) && (AIHelper.findDistance((int)this.miner.field_70163_u, chest.field_145848_d) <= 2) && (AIHelper.findDistance((int)this.miner.field_70161_v, chest.field_145849_e) <= 2))
      {
        int solidIndex = this.miner.inventory.findSolidBlock(this.miner.excludeBlocks);
        ItemStack temp = null;
        if (solidIndex >= 0)
        {
          temp = this.miner.inventory.func_70301_a(solidIndex);
          this.miner.inventory.setMainContents(solidIndex, null);
        }
        try
        {
          this.miner.inventory.dumpInventory(chest);
        }
        catch (NullPointerException e)
        {
          chest.func_70305_f();
        }
        if (solidIndex >= 0) {
          this.miner.inventory.addItem(temp);
        }
        if (!this.miner.hasTool)
        {
          Iterator iterator = this.miner.homeGuildHall.guildChests.iterator();
          while (iterator.hasNext())
          {
            chest = (TileEntityChest)iterator.next();
            int index = AIHelper.chestContains(chest, this.miner);
            if (index >= 0) {
              this.miner.inventory.swapEquipment(chest, index, 0);
            }
          }
        }
      }
    }
    if ((!this.miner.hasTool) && (this.miner.queuedTool == null))
    {
      int lowestPrice = Integer.MAX_VALUE;
      ItemStack lowestItem = null;
      for (int i = 0; i < this.miner.getValidTools().length; i++)
      {
        ItemStack item = this.miner.getValidTools()[i];
        int price = this.miner.homeVillage.economy.getPrice(item.func_82833_r());
        if ((price < lowestPrice) || (lowestItem == null))
        {
          lowestPrice = price;
          lowestItem = item;
        }
      }
      this.miner.addCraftItem(new CraftItem(lowestItem, this.miner));
      this.miner.queuedTool = lowestItem;
    }
    else if (this.miner.hasTool)
    {
      this.miner.queuedTool = null;
    }
    return idle();
  }
  
  private void mReturn()
  {
    if (this.miner.currentResource != null) {
      this.miner.currentResource = null;
    }
    this.miner.tunnelCoords.clear();
    if ((this.miner.topCoords != null) && (this.miner.shaftIndex > 0))
    {
      this.miner.func_70012_b(this.miner.topCoords.field_71574_a, this.miner.topCoords.field_71572_b, this.miner.topCoords.field_71573_c, 0.0F, 0.0F);
      this.miner.shaftIndex = 0;
      this.miner.dugSection = false;
      this.miner.digCoords.clear();
    }
    this.miner.currentActivity = EnumActivity.RETURN;
  }
  
  private void findMine()
  {
    if (this.miner.target == null) {
      if (this.miner.lastResource == null) {
        this.miner.target = AIHelper.getRandOutsideCoords(this.miner, 30);
      } else {
        this.miner.target = AIHelper.getRandOutsideCoords(this.miner, 60);
      }
    }
    if (this.miner.target != null) {
      this.miner.moveTo(this.miner.target, this.speed);
    }
    if (!AIHelper.isInRangeOfAnyVillage(this.miner.field_70165_t, this.miner.field_70163_u, this.miner.field_70161_v))
    {
      ChunkCoordinates currCoords = new ChunkCoordinates((int)this.miner.field_70165_t, (int)this.miner.field_70163_u - 1, (int)this.miner.field_70161_v);
      Block currBlock = this.miner.field_70170_p.func_147439_a(currCoords.field_71574_a, currCoords.field_71572_b, currCoords.field_71573_c);
      if (currBlock.equals(Blocks.field_150348_b))
      {
        this.miner.topCoords = new ChunkCoordinates(currCoords.field_71574_a, currCoords.field_71572_b, currCoords.field_71573_c);
        this.miner.topDir = this.miner.getDirection();
        this.miner.lastResource = new ResourceCluster(this.miner.field_70170_p, this.miner.topCoords);
      }
      else
      {
        int[] oreDictIDs = OreDictionary.getOreIDs(new ItemStack(currBlock));
        for (int j = 0; j < oreDictIDs.length; j++)
        {
          String name = OreDictionary.getOreName(oreDictIDs[j]);
          if (name.contains("ore"))
          {
            this.miner.topCoords = new ChunkCoordinates(currCoords.field_71574_a, currCoords.field_71572_b, currCoords.field_71573_c);
            this.miner.topDir = this.miner.getDirection();
            this.miner.lastResource = new ResourceCluster(this.miner.field_70170_p, this.miner.topCoords);
            
            break;
          }
        }
      }
    }
    if ((Math.abs(this.miner.field_70165_t - this.miner.target.field_71574_a) <= 2.0D) && (Math.abs(this.miner.field_70161_v - this.miner.target.field_71573_c) <= 2.0D))
    {
      this.miner.lastResource = null;
      
      this.surroundingCoords = this.miner.getSurroundingCoords();
      for (int i = 0; i < this.surroundingCoords.size(); i++)
      {
        ChunkCoordinates coord = (ChunkCoordinates)this.surroundingCoords.get(i);
        Block block = this.miner.field_70170_p.func_147439_a(coord.field_71574_a, coord.field_71572_b, coord.field_71573_c);
        if ((block.equals(Blocks.field_150355_j)) || (block.equals(Blocks.field_150353_l)))
        {
          this.miner.target = null;
          return;
        }
      }
      ChunkCoordinates topCoords = this.miner.getCoords();
      for (;;)
      {
        Block topBlock = this.miner.field_70170_p.func_147439_a(topCoords.field_71574_a, topCoords.field_71572_b, topCoords.field_71573_c);
        if (!topBlock.equals(Blocks.field_150350_a)) {
          break;
        }
        topCoords.field_71572_b -= 1;
      }
      this.miner.topCoords = topCoords;
      this.miner.topDir = this.miner.getDirection();
    }
  }
  
  private void buildStairs(ChunkCoordinates coords, int direction)
  {
    Block currentBlock = this.miner.field_70170_p.func_147439_a(coords.field_71574_a, coords.field_71572_b, coords.field_71573_c);
    if ((coords.field_71572_b <= 0) || (currentBlock.equals(Blocks.field_150357_h)) || (currentBlock.equals(Blocks.field_150353_l)) || (currentBlock.equals(Blocks.field_150355_j))) {
      return;
    }
    this.miner.shaftCoords.add(coords);
    
    ChunkCoordinates nextCoords = new ChunkCoordinates(coords.field_71574_a, coords.field_71572_b - 1, coords.field_71573_c);
    switch (direction)
    {
    case 0: 
      nextCoords.field_71573_c += 1;
      break;
    case 1: 
      nextCoords.field_71574_a -= 1;
      break;
    case 2: 
      nextCoords.field_71573_c -= 1;
      break;
    case 3: 
      nextCoords.field_71574_a += 1;
    }
    direction++;
    if (direction > 3) {
      direction = 0;
    }
    buildStairs(nextCoords, direction);
  }
  
  private void digSection(int index, boolean down)
  {
    if (index >= this.miner.shaftCoords.size()) {
      return;
    }
    if (index < 0)
    {
      this.miner.shaftIndex = 0;
      return;
    }
    boolean shouldSwing = false;
    ChunkCoordinates stairCoords = (ChunkCoordinates)this.miner.shaftCoords.get(index);
    
    this.miner.moveTo(stairCoords, this.speed);
    if ((AIHelper.findDistance((int)this.miner.field_70165_t, stairCoords.field_71574_a) <= 3) && (AIHelper.findDistance((int)this.miner.field_70163_u, stairCoords.field_71572_b) <= 3) && (AIHelper.findDistance((int)this.miner.field_70161_v, stairCoords.field_71573_c) <= 3))
    {
      if (!this.miner.dugSection)
      {
        for (int i = 3; i >= 1; i--)
        {
          ChunkCoordinates aboveCoords = new ChunkCoordinates(stairCoords.field_71574_a, stairCoords.field_71572_b + i, stairCoords.field_71573_c);
          Block currentBlock = this.miner.field_70170_p.func_147439_a(aboveCoords.field_71574_a, aboveCoords.field_71572_b, aboveCoords.field_71573_c);
          if ((currentBlock.equals(Blocks.field_150357_h)) || (currentBlock.equals(Blocks.field_150355_j)) || (currentBlock.equals(Blocks.field_150353_l))) {
            return;
          }
          if (currentBlock.isSideSolid(this.miner.field_70170_p, aboveCoords.field_71574_a, aboveCoords.field_71572_b, aboveCoords.field_71573_c, ForgeDirection.UP)) {
            this.miner.digCoords.add(aboveCoords);
          }
        }
        this.miner.dugSection = true;
      }
      else if (!this.miner.digCoords.isEmpty())
      {
        ChunkCoordinates currentCoords = (ChunkCoordinates)this.miner.digCoords.get(0);
        Block currentBlock = this.miner.field_70170_p.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c);
        if (this.previousTime <= 0)
        {
          this.previousTime = this.miner.field_70173_aa;
          if (this.miner.getCurrentItem() != null) {
            this.harvestTime = (60.0F / this.miner.getCurrentItem().func_77973_b().getDigSpeed(this.miner.getCurrentItem(), currentBlock, this.miner.getCurrentItem().func_77973_b().func_77647_b(0)));
          }
          shouldSwing = true;
        }
        else
        {
          this.currentTime = this.miner.field_70173_aa;
          if (this.currentTime - this.previousTime >= this.harvestTime)
          {
            this.previousTime = 0;
            shouldSwing = false;
            this.miner.digCoords.remove(0);
            AIHelper.breakBlock(currentCoords, this.miner);
          }
          else
          {
            shouldSwing = true;
          }
        }
      }
      else
      {
        Block stairBlock = this.miner.field_70170_p.func_147439_a(stairCoords.field_71574_a, stairCoords.field_71572_b, stairCoords.field_71573_c);
        if ((!stairBlock.isSideSolid(this.miner.field_70170_p, stairCoords.field_71574_a, stairCoords.field_71572_b, stairCoords.field_71573_c, ForgeDirection.UP)) || (stairBlock.equals(Blocks.field_150351_n)) || (stairBlock.equals(Blocks.field_150354_m)))
        {
          int solidIndex = this.miner.inventory.findSolidBlock(this.miner.excludeBlocks);
          if (solidIndex >= 0)
          {
            Block newBlock = Block.func_149634_a(this.miner.inventory.func_70301_a(solidIndex).func_77973_b());
            if ((!stairBlock.equals(Blocks.field_150350_a)) && (!stairBlock.equals(Blocks.field_150355_j)) && (!stairBlock.equals(Blocks.field_150353_l)))
            {
              int metadata = this.miner.field_70170_p.func_72805_g(stairCoords.field_71574_a, stairCoords.field_71572_b, stairCoords.field_71573_c);
              ItemStack item = new ItemStack(stairBlock, 1, metadata);
              try
              {
                this.miner.inventory.addItem(item);
              }
              catch (NullPointerException e) {}
            }
            this.miner.field_70170_p.func_147449_b(stairCoords.field_71574_a, stairCoords.field_71572_b, stairCoords.field_71573_c, newBlock);
            this.miner.inventory.decrementSlot(solidIndex);
          }
          else
          {
            return;
          }
        }
        if (this.miner.func_70661_as().func_75500_f())
        {
          ChunkCoordinates upCoords = new ChunkCoordinates(stairCoords.field_71574_a, stairCoords.field_71572_b + 2, stairCoords.field_71573_c);
          if ((!this.miner.beingFollowed) && (upCoords.field_71572_b < this.miner.topCoords.field_71572_b))
          {
            ArrayList adjacentCoords = AIHelper.getAdjacentCoords(upCoords);
            for (int i = 0; i < adjacentCoords.size(); i++)
            {
              ChunkCoordinates aCoords = (ChunkCoordinates)adjacentCoords.get(i);
              Block aBlock = this.miner.field_70170_p.func_147439_a(aCoords.field_71574_a, aCoords.field_71572_b, aCoords.field_71573_c);
              if ((this.miner.isInMine()) && (!aBlock.isSideSolid(this.miner.field_70170_p, aCoords.field_71574_a, aCoords.field_71572_b, aCoords.field_71573_c, ForgeDirection.UP))) {
                replaceBlock((ChunkCoordinates)adjacentCoords.get(i));
              }
            }
          }
          if (down) {
            this.miner.shaftIndex += 1;
          } else {
            this.miner.shaftIndex -= 1;
          }
          this.miner.dugSection = false;
        }
      }
    }
    else if ((AIHelper.findDistance((int)this.miner.field_70165_t, stairCoords.field_71574_a) > 10) || (AIHelper.findDistance((int)this.miner.field_70161_v, stairCoords.field_71573_c) > 10)) {
      this.miner.moveTo(stairCoords, this.speed);
    } else {
      this.miner.shaftIndex = this.miner.nearestShaftCoord();
    }
    if (shouldSwing) {
      this.miner.func_71038_i();
    }
    this.miner.swingingPickaxe = shouldSwing;
  }
  
  private boolean replaceBlock(ChunkCoordinates coords)
  {
    for (int i = 0; i < 4; i++)
    {
      ChunkCoordinates checkCoords = (ChunkCoordinates)this.miner.shaftCoords.get(i);
      if ((checkCoords.field_71574_a == coords.field_71574_a) && (checkCoords.field_71573_c == coords.field_71573_c)) {
        return false;
      }
    }
    Block block = this.miner.field_70170_p.func_147439_a(coords.field_71574_a, coords.field_71572_b, coords.field_71573_c);
    
    int solidIndex = this.miner.inventory.findSolidBlock(this.miner.excludeBlocks);
    if (solidIndex >= 0)
    {
      Block newBlock = Block.func_149634_a(this.miner.inventory.func_70301_a(solidIndex).func_77973_b());
      if ((!block.equals(Blocks.field_150350_a)) && (!block.equals(Blocks.field_150355_j)) && (!block.equals(Blocks.field_150353_l))) {
        AIHelper.breakBlock(coords, this.miner);
      }
      this.miner.field_70170_p.func_147449_b(coords.field_71574_a, coords.field_71572_b, coords.field_71573_c, newBlock);
      this.miner.inventory.decrementSlot(solidIndex);
    }
    return true;
  }
  
  private void getNewResource()
  {
    ArrayList boxCoords = this.miner.getValidCoords();
    double closestDist = Double.MAX_VALUE;
    ResourceCluster closestValidCluster = null;
    for (int i = 0; i < boxCoords.size(); i++)
    {
      ChunkCoordinates currentCoords = (ChunkCoordinates)boxCoords.get(i);
      int dist = (int)currentCoords.func_82371_e(this.miner.getCoords());
      if (dist < closestDist)
      {
        closestValidCluster = new ResourceCluster(this.miner.field_70170_p, currentCoords);
        closestDist = dist;
      }
    }
    if (closestValidCluster != null)
    {
      this.miner.currentResource = closestValidCluster;
      this.miner.currentResource.buildCluster();
      buildTunnel();
    }
  }
  
  private void buildTunnel()
  {
    ChunkCoordinates destCoords = this.miner.currentResource.coords;
    ChunkCoordinates currentCoords = new ChunkCoordinates((int)this.miner.field_70165_t, destCoords.field_71572_b, (int)this.miner.field_70161_v);
    for (;;)
    {
      this.miner.tunnelCoords.add(new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c));
      if (currentCoords.field_71574_a < destCoords.field_71574_a)
      {
        currentCoords.field_71574_a += 1;
      }
      else
      {
        if (currentCoords.field_71574_a <= destCoords.field_71574_a) {
          break;
        }
        currentCoords.field_71574_a -= 1;
      }
    }
    for (;;)
    {
      this.miner.tunnelCoords.add(new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c));
      if (currentCoords.field_71573_c < destCoords.field_71573_c)
      {
        currentCoords.field_71573_c += 1;
      }
      else
      {
        if (currentCoords.field_71573_c <= destCoords.field_71573_c) {
          break;
        }
        currentCoords.field_71573_c -= 1;
      }
    }
    this.miner.tunnelCoords.remove(this.miner.tunnelCoords.size() - 1);
    this.miner.shaftIndex -= 1;
  }
  
  private void digTunnel(boolean returning)
  {
    boolean shouldSwing = false;
    ChunkCoordinates currentCoords;
    ChunkCoordinates currentCoords;
    if (returning) {
      currentCoords = (ChunkCoordinates)this.miner.returnPath.get(this.miner.returnPath.size() - 1);
    } else {
      currentCoords = (ChunkCoordinates)this.miner.tunnelCoords.get(0);
    }
    ArrayList adjacent = AIHelper.getAdjacentCoords(currentCoords);
    for (int i = 0; i < adjacent.size(); i++)
    {
      ChunkCoordinates check = (ChunkCoordinates)adjacent.get(i);
      Block checkBlock = this.miner.field_70170_p.func_147439_a(check.field_71574_a, check.field_71572_b, check.field_71573_c);
      if (checkBlock.equals(Blocks.field_150353_l))
      {
        this.miner.tunnelCoords.clear();
        this.skipResource = true;
        this.miner.currentResource = null;
        return;
      }
    }
    if ((this.lastBlock != null) && ((this.lastBlock.equals(Blocks.field_150354_m)) || (this.lastBlock.equals(Blocks.field_150351_n))))
    {
      if (this.previousWait <= 0)
      {
        this.previousWait = this.miner.field_70173_aa;
        return;
      }
      this.currentTime = this.miner.field_70173_aa;
      if (this.currentTime - this.previousWait >= 40)
      {
        this.previousWait = 0;
        this.lastBlock = null;
      }
      else
      {
        this.miner.func_71038_i();
        return;
      }
    }
    if ((this.lastAbove != null) && ((this.lastAbove.equals(Blocks.field_150354_m)) || (this.lastAbove.equals(Blocks.field_150351_n))))
    {
      if (this.previousWait <= 0)
      {
        this.previousWait = this.miner.field_70173_aa;
        return;
      }
      this.currentTime = this.miner.field_70173_aa;
      if (this.currentTime - this.previousWait >= 40)
      {
        this.previousWait = 0;
        this.lastAbove = null;
      }
      else
      {
        this.miner.func_71038_i();
        return;
      }
    }
    if ((this.lastAbove2 != null) && ((this.lastAbove2.equals(Blocks.field_150354_m)) || (this.lastAbove2.equals(Blocks.field_150351_n))))
    {
      if (this.previousWait <= 0)
      {
        this.previousWait = this.miner.field_70173_aa;
        return;
      }
      this.currentTime = this.miner.field_70173_aa;
      if (this.currentTime - this.previousWait >= 40)
      {
        this.previousWait = 0;
        this.lastAbove2 = null;
      }
      else
      {
        this.miner.func_71038_i();
        return;
      }
    }
    ChunkCoordinates aboveCoords = new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b + 1, currentCoords.field_71573_c);
    ChunkCoordinates aboveCoords2 = new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b + 2, currentCoords.field_71573_c);
    ChunkCoordinates belowCoords = new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b - 1, currentCoords.field_71573_c);
    Block currentBlock = this.miner.field_70170_p.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c);
    Block aboveBlock = this.miner.field_70170_p.func_147439_a(aboveCoords.field_71574_a, aboveCoords.field_71572_b, aboveCoords.field_71573_c);
    Block aboveBlock2 = this.miner.field_70170_p.func_147439_a(aboveCoords2.field_71574_a, aboveCoords2.field_71572_b, aboveCoords2.field_71573_c);
    Block belowBlock = this.miner.field_70170_p.func_147439_a(belowCoords.field_71574_a, belowCoords.field_71572_b, belowCoords.field_71573_c);
    
    this.miner.moveTo(currentCoords, this.speed);
    if ((AIHelper.findDistance((int)this.miner.field_70165_t, currentCoords.field_71574_a) <= 3) && (AIHelper.findDistance((int)this.miner.field_70163_u, currentCoords.field_71572_b) <= 3) && (AIHelper.findDistance((int)this.miner.field_70161_v, currentCoords.field_71573_c) <= 3))
    {
      this.miner.func_70661_as().func_75499_g();
      if ((!currentBlock.equals(Blocks.field_150350_a)) && (!currentBlock.equals(Blocks.field_150355_j)) && (!currentBlock.equals(Blocks.field_150353_l)))
      {
        if (this.previousTime <= 0)
        {
          this.previousTime = this.miner.field_70173_aa;
          if (this.miner.getCurrentItem() != null) {
            this.harvestTime = (60.0F / this.miner.getCurrentItem().func_77973_b().getDigSpeed(this.miner.getCurrentItem(), currentBlock, this.miner.getCurrentItem().func_77973_b().func_77647_b(0)));
          }
          shouldSwing = true;
        }
        else
        {
          this.currentTime = this.miner.field_70173_aa;
          if (this.currentTime - this.previousTime >= this.harvestTime)
          {
            this.previousTime = 0;
            shouldSwing = false;
            AIHelper.breakBlock(currentCoords, this.miner);
            this.lastBlock = currentBlock;
          }
          else
          {
            shouldSwing = true;
          }
        }
      }
      else if ((!aboveBlock.equals(Blocks.field_150350_a)) && (!aboveBlock.equals(Blocks.field_150355_j)) && (!aboveBlock.equals(Blocks.field_150353_l)))
      {
        if (this.previousTime <= 0)
        {
          this.previousTime = this.miner.field_70173_aa;
          if (this.miner.getCurrentItem() != null) {
            this.harvestTime = (60.0F / this.miner.getCurrentItem().func_77973_b().getDigSpeed(this.miner.getCurrentItem(), aboveBlock, this.miner.getCurrentItem().func_77973_b().func_77647_b(0)));
          }
          shouldSwing = true;
        }
        else
        {
          this.currentTime = this.miner.field_70173_aa;
          if (this.currentTime - this.previousTime >= this.harvestTime)
          {
            this.previousTime = 0;
            shouldSwing = false;
            AIHelper.breakBlock(aboveCoords, this.miner);
            this.lastAbove = aboveBlock;
          }
          else
          {
            shouldSwing = true;
          }
        }
      }
      else if ((!aboveBlock2.equals(Blocks.field_150350_a)) && (!aboveBlock2.equals(Blocks.field_150355_j)) && (!aboveBlock2.equals(Blocks.field_150353_l)))
      {
        if (this.previousTime <= 0)
        {
          this.previousTime = this.miner.field_70173_aa;
          if (this.miner.getCurrentItem() != null) {
            this.harvestTime = (60.0F / this.miner.getCurrentItem().func_77973_b().getDigSpeed(this.miner.getCurrentItem(), aboveBlock2, this.miner.getCurrentItem().func_77973_b().func_77647_b(0)));
          }
          shouldSwing = true;
        }
        else
        {
          this.currentTime = this.miner.field_70173_aa;
          if (this.currentTime - this.previousTime >= this.harvestTime)
          {
            this.previousTime = 0;
            shouldSwing = false;
            AIHelper.breakBlock(aboveCoords2, this.miner);
            this.lastAbove2 = aboveBlock2;
          }
          else
          {
            shouldSwing = true;
          }
        }
      }
      else if ((!belowBlock.isSideSolid(this.miner.field_70170_p, belowCoords.field_71574_a, belowCoords.field_71572_b, belowCoords.field_71573_c, ForgeDirection.UP)) || (belowBlock.equals(Blocks.field_150351_n)) || (belowBlock.equals(Blocks.field_150354_m)))
      {
        boolean replaced = replaceBlock(belowCoords);
        if (!replaced) {
          try
          {
            if (returning)
            {
              this.miner.returnPath.remove(this.miner.returnPath.size() - 1);
            }
            else
            {
              this.miner.returnPath.add(currentCoords);
              this.miner.tunnelCoords.remove(0);
            }
          }
          catch (Exception e) {}
        }
      }
      else if (returning)
      {
        this.miner.returnPath.remove(this.miner.returnPath.size() - 1);
      }
      else
      {
        this.miner.returnPath.add(currentCoords);
        this.miner.tunnelCoords.remove(0);
      }
    }
    else if ((AIHelper.findDistance((int)this.miner.field_70165_t, currentCoords.field_71574_a) > 10) || (AIHelper.findDistance((int)this.miner.field_70161_v, currentCoords.field_71573_c) > 10))
    {
      this.miner.moveTo(currentCoords, this.speed);
    }
    if (shouldSwing) {
      this.miner.func_71038_i();
    }
    this.miner.swingingPickaxe = shouldSwing;
  }
  
  private void mineResource()
  {
    boolean shouldSwing = false;
    ChunkCoordinates currentCoords = this.miner.currentResource.coords;
    
    this.miner.moveTo(currentCoords, this.speed);
    if ((AIHelper.findDistance((int)this.miner.field_70165_t, currentCoords.field_71574_a) <= 3) && (AIHelper.findDistance((int)this.miner.field_70163_u, currentCoords.field_71572_b) <= 3) && (AIHelper.findDistance((int)this.miner.field_70161_v, currentCoords.field_71573_c) <= 3))
    {
      shouldSwing = true;
      if (this.previousTime <= 0)
      {
        this.previousTime = this.miner.field_70173_aa;
        this.harvestTime = (60.0F / this.miner.getCurrentItem().func_77973_b().getDigSpeed(this.miner.getCurrentItem(), this.miner.currentResource.startBlock, this.miner.getCurrentItem().func_77973_b().func_77647_b(0)));
      }
      if (this.previousTime > 0)
      {
        this.currentTime = this.miner.field_70173_aa;
        if (!this.miner.currentResource.blockCluster.isEmpty())
        {
          if (this.currentTime - this.previousTime >= this.harvestTime)
          {
            this.previousTime = this.currentTime;
            currentCoords = (ChunkCoordinates)this.miner.currentResource.blockCluster.get(0);
            Block currentBlock = this.miner.field_70170_p.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c);
            if (Block.func_149682_b(currentBlock) == Block.func_149682_b(this.miner.currentResource.startBlock)) {
              AIHelper.breakBlock(currentCoords, this.miner);
            }
            this.miner.currentResource.blockCluster.remove(0);
            if (currentCoords.field_71572_b == (int)this.miner.field_70163_u - 1) {
              replaceBlock(currentCoords);
            }
          }
        }
        else
        {
          this.miner.currentResource = null;
          this.previousTime = 0;
          this.currentTime = 0;
        }
      }
    }
    else if ((AIHelper.findDistance((int)this.miner.field_70165_t, currentCoords.field_71574_a) > 10) || (AIHelper.findDistance((int)this.miner.field_70161_v, currentCoords.field_71573_c) > 10))
    {
      this.miner.moveTo(currentCoords, this.speed);
      shouldSwing = false;
    }
    if (shouldSwing) {
      this.miner.func_71038_i();
    }
    this.miner.swingingPickaxe = shouldSwing;
  }
}

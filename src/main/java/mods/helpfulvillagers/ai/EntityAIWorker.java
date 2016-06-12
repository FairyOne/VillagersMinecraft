package mods.helpfulvillagers.ai;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import mods.helpfulvillagers.crafting.CraftItem;
import mods.helpfulvillagers.crafting.CraftTree;
import mods.helpfulvillagers.crafting.CraftTree.Node;
import mods.helpfulvillagers.econ.VillageEconomy;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.enums.EnumActivity;
import mods.helpfulvillagers.inventory.InventoryVillager;
import mods.helpfulvillagers.util.AIHelper;
import mods.helpfulvillagers.village.GuildHall;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public abstract class EntityAIWorker
  extends EntityAIBase
{
  protected AbstractVillager villager;
  protected ChunkCoordinates target;
  protected float speed;
  protected int previousTime;
  protected int currentTime;
  protected float harvestTime;
  protected Random gen;
  private boolean craftInit;
  protected boolean craftCheck;
  protected boolean readyToSmelt;
  protected boolean readyToCraft;
  private CraftTree craftTree;
  
  public EntityAIWorker(AbstractVillager villager)
  {
    this.villager = villager;
    this.target = null;
    this.speed = 0.5F;
    this.currentTime = 0;
    this.previousTime = 0;
    this.harvestTime = 0.0F;
    this.gen = new Random();
    this.craftInit = false;
    this.craftCheck = false;
    this.readyToSmelt = false;
    this.readyToCraft = false;
    this.craftTree = null;
    func_75248_a(1);
  }
  
  public boolean func_75250_a()
  {
    switch (this.villager.currentActivity)
    {
    case GATHER: 
      return true;
    case RETURN: 
      return false;
    case CRAFT: 
      return true;
    case STORE: 
      return true;
    case IDLE: 
      return idle();
    }
    return false;
  }
  
  public boolean func_75253_b()
  {
    switch (this.villager.currentActivity)
    {
    case GATHER: 
      return gather();
    case RETURN: 
      return false;
    case CRAFT: 
      return craft();
    case STORE: 
      return store();
    case IDLE: 
      return idle();
    }
    return false;
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
        this.villager.currentActivity = EnumActivity.RETURN;
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
      this.villager.currentActivity = EnumActivity.RETURN;
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
      this.villager.currentActivity = EnumActivity.RETURN;
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
  
  protected boolean store()
  {
    if (this.villager.homeGuildHall == null) {
      return idle();
    }
    if ((!this.villager.inventory.isEmpty()) || (!this.villager.hasTool))
    {
      TileEntityChest chest = this.villager.homeGuildHall.getAvailableChest();
      if (chest != null) {
        this.villager.moveTo(new ChunkCoordinates(chest.field_145851_c, chest.field_145848_d, chest.field_145849_e), this.speed);
      } else {
        this.villager.changeGuildHall = true;
      }
      if ((chest != null) && (AIHelper.findDistance((int)this.villager.field_70165_t, chest.field_145851_c) <= 2) && (AIHelper.findDistance((int)this.villager.field_70163_u, chest.field_145848_d) <= 2) && (AIHelper.findDistance((int)this.villager.field_70161_v, chest.field_145849_e) <= 2))
      {
        try
        {
          this.villager.inventory.dumpInventory(chest);
        }
        catch (NullPointerException e)
        {
          chest.func_70305_f();
        }
        if (!this.villager.hasTool)
        {
          Iterator iterator = this.villager.homeGuildHall.guildChests.iterator();
          while (iterator.hasNext())
          {
            chest = (TileEntityChest)iterator.next();
            int index = AIHelper.chestContains(chest, this.villager);
            if (index >= 0) {
              this.villager.inventory.swapEquipment(chest, index, 0);
            }
          }
        }
      }
    }
    if ((!this.villager.hasTool) && (this.villager.queuedTool == null))
    {
      int lowestPrice = Integer.MAX_VALUE;
      ItemStack lowestItem = null;
      for (int i = 0; i < this.villager.getValidTools().length; i++)
      {
        ItemStack item = this.villager.getValidTools()[i];
        int price = this.villager.homeVillage.economy.getPrice(item.func_82833_r());
        if ((price < lowestPrice) || (lowestItem == null))
        {
          lowestPrice = price;
          lowestItem = item;
        }
      }
      this.villager.addCraftItem(new CraftItem(lowestItem, this.villager));
      this.villager.queuedTool = lowestItem;
    }
    else if (this.villager.hasTool)
    {
      this.villager.queuedTool = null;
    }
    return idle();
  }
  
  protected boolean craft()
  {
    if (this.villager.currentCraftItem == null) {
      return idle();
    }
    if (!this.craftInit)
    {
      this.craftTree = new CraftTree(this.villager.currentCraftItem.getItem(), this.villager);
      this.craftInit = true;
    }
    if (!this.readyToSmelt)
    {
      if (!this.villager.smeltablesNeeded.isEmpty())
      {
        Iterator iterator = this.villager.smeltablesNeeded.iterator();
        while (iterator.hasNext())
        {
          ItemStack currItem = (ItemStack)iterator.next();
          this.villager.inventory.storeAsCollected(currItem, true);
          if (currItem.field_77994_a <= 0)
          {
            iterator.remove();
          }
          else
          {
            this.villager.lookForItem(currItem);
            if (this.villager.inventory.getTotalAmount(currItem) >= currItem.field_77994_a)
            {
              this.villager.inventory.storeAsCollected(currItem, true);
              if (currItem.field_77994_a <= 0) {
                iterator.remove();
              }
            }
          }
        }
      }
      else if (!this.villager.inventory.smeltablesCollected.isEmpty())
      {
        this.readyToSmelt = true;
        return idle();
      }
    }
    else if (!this.villager.inventory.smeltablesCollected.isEmpty())
    {
      if (this.villager.nearHall())
      {
        TileEntityFurnace furnace = this.villager.homeGuildHall.getAvailableFurnace();
        if (furnace != null)
        {
          if (furnace.func_70301_a(2) != null)
          {
            this.villager.inventory.addItem(furnace.func_70301_a(2));
            furnace.func_70299_a(2, null);
          }
          if (!TileEntityFurnace.func_145954_b(furnace.func_70301_a(1)))
          {
            int burnTime = ((ItemStack)this.villager.inventory.smeltablesCollected.get(0)).field_77994_a * 200;
            AIHelper.addFuelToFurnace(this.villager.homeVillage, furnace, burnTime);
          }
          else
          {
            ItemStack item = (ItemStack)this.villager.inventory.smeltablesCollected.remove(0);
            furnace.func_70299_a(0, item);
          }
        }
        else
        {
          this.villager.changeGuildHall = true;
        }
      }
    }
    else
    {
      this.readyToSmelt = false;
      if ((this.villager.materialsNeeded.isEmpty()) && (this.villager.inventory.materialsCollected.isEmpty()))
      {
        this.villager.resetCraftItem();
        this.craftInit = false;
        return idle();
      }
    }
    if (!this.readyToCraft)
    {
      if (!this.villager.materialsNeeded.isEmpty())
      {
        Iterator iterator = this.villager.materialsNeeded.iterator();
        while (iterator.hasNext())
        {
          ItemStack currItem = (ItemStack)iterator.next();
          this.villager.inventory.storeAsCollected(currItem, false);
          if (currItem.field_77994_a <= 0)
          {
            iterator.remove();
          }
          else
          {
            this.villager.lookForItem(currItem);
            if (this.villager.inventory.getTotalAmount(currItem) >= currItem.field_77994_a)
            {
              this.villager.inventory.storeAsCollected(currItem, false);
              if (currItem.field_77994_a <= 0) {
                iterator.remove();
              }
            }
          }
        }
      }
      else if (!this.villager.inventory.materialsCollected.isEmpty())
      {
        this.readyToCraft = true;
        return idle();
      }
    }
    else if ((this.villager.nearHall()) && 
      (this.villager.homeGuildHall.hasWorkbench()))
    {
      Iterator iterator = this.villager.craftChain.iterator();
      while (iterator.hasNext())
      {
        CraftTree.Node currNode = (CraftTree.Node)iterator.next();
        if (!currNode.isSmelted())
        {
          for (ItemStack i : currNode.getInputs()) {
            if (!AIHelper.removeItemStack(i, this.villager.inventory.materialsCollected)) {
              System.out.println("MATERIALS NOT COLLECTED: " + i);
            }
          }
          int amountProduced = currNode.getItemStack().field_77994_a + currNode.getLeftover();
          ItemStack craftedItem = new ItemStack(currNode.getItemStack().func_77973_b(), amountProduced, currNode.getItemStack().func_77960_j());
          AIHelper.mergeItemStackArrays(craftedItem, this.villager.inventory.materialsCollected);
          iterator.remove();
        }
      }
      this.villager.inventory.dumpCollected(false);
      this.villager.resetCraftItem();
      this.craftInit = false;
      this.readyToCraft = false;
      
      return store();
    }
    return idle();
  }
  
  protected abstract boolean gather();
}

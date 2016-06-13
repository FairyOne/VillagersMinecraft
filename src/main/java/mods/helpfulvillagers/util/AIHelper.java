package mods.helpfulvillagers.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import mods.helpfulvillagers.crafting.CraftItem;
import mods.helpfulvillagers.crafting.CraftTree.Node;
import mods.helpfulvillagers.econ.VillageEconomy;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.inventory.InventoryVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.village.GuildHall;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class AIHelper
{
  public static ChunkCoordinates getRandOutsideCoords(AbstractVillager villager, int limit)
  {
    Random gen = new Random();
    HelpfulVillage village = villager.homeVillage;
    if (villager.lastResource != null)
    {
      ChunkCoordinates center = villager.lastResource.coords;
      int x = center.field_71574_a;
      int y = center.field_71572_b;
      int z = center.field_71573_c;
      int newX;
      int newX;
      if (gen.nextBoolean()) {
        newX = x + gen.nextInt(limit / 2);
      } else {
        newX = x - gen.nextInt(limit / 2);
      }
      int newZ;
      int newZ;
      if (gen.nextBoolean()) {
        newZ = z + gen.nextInt(limit / 2);
      } else {
        newZ = z - gen.nextInt(limit / 2);
      }
      return new ChunkCoordinates(newX, y, newZ);
    }
    if (village != null)
    {
      ChunkCoordinates center = village.getActualCenter();
      int x = center.field_71574_a;
      int y = center.field_71572_b;
      int z = center.field_71573_c;
      int newX;
      if (gen.nextBoolean())
      {
        int newX = x - (village.getActualRadius() + 10);
        newX -= gen.nextInt(limit / 2);
      }
      else
      {
        newX = x + (village.getActualRadius() + 10);
        newX += gen.nextInt(limit / 2);
      }
      int newZ;
      if (gen.nextBoolean())
      {
        int newZ = z - (village.getActualRadius() + 10);
        newZ -= gen.nextInt(limit / 2);
      }
      else
      {
        newZ = z + (village.getActualRadius() + 10);
        newZ += gen.nextInt(limit / 2);
      }
      return new ChunkCoordinates(newX, y, newZ);
    }
    return null;
  }
  
  public static ChunkCoordinates getRandInsideCoords(AbstractVillager villager)
  {
    Random gen = new Random();
    HelpfulVillage village = villager.homeVillage;
    if (village != null)
    {
      ChunkCoordinates center = village.getActualCenter();
      int xRange = (int)(Math.abs(village.actualBounds.field_72336_d - village.actualBounds.field_72340_a) + 5.0D);
      int zRange = (int)(Math.abs(village.actualBounds.field_72334_f - village.actualBounds.field_72339_c) + 5.0D);
      
      int x = (int)(village.actualBounds.field_72340_a + gen.nextInt(xRange));
      int y = center.field_71572_b;
      int z = (int)(village.actualBounds.field_72339_c + gen.nextInt(zRange));
      return new ChunkCoordinates(x, y, z);
    }
    return null;
  }
  
  public static int findDistance(int par1, int par2)
  {
    int temp1 = Math.abs(par1);
    int temp2 = Math.abs(par2);
    int temp3 = temp1 - temp2;
    return Math.abs(temp3);
  }
  
  public static int chestContains(TileEntityChest chest, ItemStack item)
  {
    for (int i = 0; i < chest.func_70302_i_(); i++)
    {
      ItemStack chestItem = chest.func_70301_a(i);
      if ((chestItem != null) && (chestItem.func_82833_r().equals(item.func_82833_r()))) {
        return i;
      }
    }
    return -1;
  }
  
  public static int chestContains(TileEntityChest chest, AbstractVillager villager)
  {
    for (int i = 0; i < chest.func_70302_i_(); i++)
    {
      ItemStack chestItem = chest.func_70301_a(i);
      if ((chestItem != null) && (villager.isValidTool(chestItem))) {
        return i;
      }
    }
    return -1;
  }
  
  public static boolean takeItemsFromChest(ItemStack item, TileEntityChest chest, AbstractVillager villager)
  {
    for (int i = 0; i < chest.func_70302_i_(); i++)
    {
      ItemStack chestItem = chest.func_70301_a(i);
      Block block = Block.func_149634_a(item.func_77973_b());
      Block chestBlock = Block.func_149634_a(chestItem != null ? chestItem.func_77973_b() : null);
      if ((chestItem != null) && (chestItem.field_77994_a > 0) && (((villager.currentCraftItem.isSensitive()) && (chestItem.func_82833_r().equals(item.func_82833_r()))) || ((!villager.currentCraftItem.isSensitive()) && ((chestItem.func_77973_b().equals(item.func_77973_b())) || (((block instanceof BlockLog)) && ((chestBlock instanceof BlockLog)))))))
      {
        if (chestItem.field_77994_a >= item.field_77994_a)
        {
          chestItem.field_77994_a -= item.field_77994_a;
          villager.inventory.addItem(item);
          
          villager.homeVillage.economy.decreaseItemSupply(villager, item);
          if (chestItem.field_77994_a <= 0) {
            chestItem = null;
          }
          chest.func_70299_a(i, chestItem);
          return true;
        }
        villager.inventory.addItem(chestItem);
        villager.homeVillage.economy.decreaseItemSupply(villager, chestItem);
        chest.func_70299_a(i, null);
        item.field_77994_a -= chestItem.field_77994_a;
      }
    }
    return false;
  }
  
  public static boolean takeItemFromFurnace(ItemStack item, TileEntityFurnace furnace, AbstractVillager villager)
  {
    ItemStack furnaceItem = furnace.func_70301_a(2);
    Block block = Block.func_149634_a(item.func_77973_b());
    Block furnaceBlock = Block.func_149634_a(furnaceItem != null ? furnaceItem.func_77973_b() : null);
    if ((furnaceItem != null) && (furnaceItem.field_77994_a > 0) && (((villager.currentCraftItem.isSensitive()) && (furnaceItem.func_82833_r().equals(item.func_82833_r()))) || ((!villager.currentCraftItem.isSensitive()) && ((furnaceItem.func_77973_b().equals(item.func_77973_b())) || (((block instanceof BlockLog)) && ((furnaceBlock instanceof BlockLog)))))))
    {
      if (furnaceItem.field_77994_a >= item.field_77994_a)
      {
        furnaceItem.field_77994_a -= item.field_77994_a;
        villager.inventory.addItem(item);
        if (furnaceItem.field_77994_a <= 0) {
          furnaceItem = null;
        }
        furnace.func_70299_a(2, furnaceItem);
        return true;
      }
      villager.inventory.addItem(furnaceItem);
      furnace.func_70299_a(2, null);
      item.field_77994_a -= furnaceItem.field_77994_a;
    }
    return false;
  }
  
  public static void addFuelToFurnace(HelpfulVillage village, TileEntityFurnace furnace, int burnTime)
  {
    int totalTime = 0;
    for (int i = 0; i < village.guildHallList.size(); i++)
    {
      GuildHall hall = (GuildHall)village.guildHallList.get(i);
      hall.checkChests();
      ArrayList chests = hall.guildChests;
      for (int j = 0; j < chests.size(); j++)
      {
        TileEntityChest chest = (TileEntityChest)chests.get(j);
        for (int k = 0; k < chest.func_70302_i_(); k++)
        {
          ItemStack item = chest.func_70301_a(k);
          if ((item != null) && (item.func_77973_b().equals(Items.field_151044_h)))
          {
            int currTime = TileEntityFurnace.func_145952_a(item) * item.field_77994_a;
            totalTime += currTime;
            if (furnace.func_70301_a(1) == null)
            {
              furnace.func_70299_a(1, item);
              chest.func_70299_a(k, null);
            }
            else
            {
              int size = item.field_77994_a + furnace.func_70301_a(1).field_77994_a;
              if (size > 64)
              {
                int removeAmount = item.field_77994_a - (64 - furnace.func_70301_a(1).field_77994_a);
                furnace.func_70299_a(1, new ItemStack(item.func_77973_b(), 64));
                if (removeAmount <= 0) {
                  chest.func_70299_a(k, null);
                } else {
                  chest.func_70299_a(k, new ItemStack(item.func_77973_b(), removeAmount));
                }
                return;
              }
              furnace.func_70299_a(1, new ItemStack(item.func_77973_b(), size));
              chest.func_70299_a(k, null);
            }
            if (totalTime >= burnTime) {
              return;
            }
          }
        }
      }
    }
  }
  
  public static ArrayList getAdjacentCoords(ChunkCoordinates coords)
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
  
  public static void breakBlock(ChunkCoordinates currentCoords, AbstractVillager villager)
  {
    Block currentBlock = villager.field_70170_p.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c);
    if ((currentBlock != null) && (!currentBlock.equals(Blocks.field_150350_a)) && (!currentBlock.equals(Blocks.field_150355_j)) && (!currentBlock.equals(Blocks.field_150353_l)) && (!currentBlock.equals(Blocks.field_150357_h)) && (!currentBlock.equals(Blocks.field_150329_H)))
    {
      int metadata = villager.field_70170_p.func_72805_g(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c);
      ArrayList<ItemStack> items = currentBlock.getDrops(villager.field_70170_p, currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c, metadata, 0);
      for (ItemStack i : items) {
        try
        {
          villager.inventory.addItem(i);
          villager.damageItem();
        }
        catch (NullPointerException e) {}
      }
    }
    villager.field_70170_p.func_147468_f(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c);
  }
  
  public static boolean isinsideAnyVillage(double x, double y, double z)
  {
    for (HelpfulVillage i : HelpfulVillagers.villages) {
      if (i.isInsideVillage(x, y, z)) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isInRangeOfAnyVillage(double x, double y, double z)
  {
    for (HelpfulVillage i : HelpfulVillagers.villages) {
      if (i.isInRange(x, y, z)) {
        return true;
      }
    }
    return false;
  }
  
  public static void mergeItemStackArrays(ArrayList<ItemStack> from, ArrayList<ItemStack> to)
  {
    Iterator iterator = from.iterator();
    while (iterator.hasNext()) {
      if (iterator.next() == null) {
        iterator.remove();
      }
    }
    iterator = to.iterator();
    while (iterator.hasNext()) {
      if (iterator.next() == null) {
        iterator.remove();
      }
    }
    for (ItemStack i : from)
    {
      for (Iterator localIterator2 = to.iterator(); localIterator2.hasNext(); goto 159)
      {
        ItemStack j = (ItemStack)localIterator2.next();
        if ((i != null) && (i.func_82833_r() != null) && (j != null) && (j.func_82833_r() != null) && (i.func_82833_r().equals(j.func_82833_r()))) {
          if ((i.field_77994_a > 0) && 
            (j.field_77994_a < j.func_77976_d()))
          {
            i.field_77994_a -= 1;
            j.field_77994_a += 1;
          }
        }
      }
      if (i.field_77994_a > 0) {
        to.add(i);
      }
    }
  }
  
  public static void mergeItemStackArrays(ItemStack from, ArrayList<ItemStack> to)
  {
    ArrayList<ItemStack> temp = new ArrayList();
    temp.add(from);
    mergeItemStackArrays(temp, to);
  }
  
  public static boolean removeItemStack(ItemStack item, ArrayList<ItemStack> array)
  {
    Iterator iterator = array.iterator();
    while (iterator.hasNext())
    {
      ItemStack currItem = (ItemStack)iterator.next();
      if ((item.func_77973_b().equals(currItem.func_77973_b())) && (currItem.func_77960_j() == item.func_77960_j()))
      {
        if (currItem.field_77994_a >= item.field_77994_a)
        {
          int itemSize = item.field_77994_a;
          currItem.field_77994_a -= itemSize;
          if (currItem.field_77994_a <= 0) {
            iterator.remove();
          }
          item.field_77994_a = 0;
          return true;
        }
        item.field_77994_a -= currItem.field_77994_a;
        iterator.remove();
      }
    }
    return false;
  }
  
  public static void removeNodeBranch(ArrayList<CraftTree.Node> nodes, CraftTree.Node parent)
  {
    Iterator i = nodes.iterator();
    while (i.hasNext())
    {
      CraftTree.Node node = (CraftTree.Node)i.next();
      if ((node.equals(parent)) || (node.getParent().equals(parent))) {
        i.remove();
      }
    }
  }
}

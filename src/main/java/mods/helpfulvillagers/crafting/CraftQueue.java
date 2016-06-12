package mods.helpfulvillagers.crafting;

import java.io.PrintStream;
import java.util.ArrayList;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.village.HelpfulVillageCollection;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class CraftQueue
{
  private ArrayList<CraftItem> playerItems = new ArrayList();
  private ArrayList<CraftItem> villagerItems = new ArrayList();
  
  public CraftQueue() {}
  
  public CraftQueue(ArrayList<CraftItem> items)
  {
    for (CraftItem i : items) {
      if (i != null) {
        if (i.getPriority() >= 1) {
          this.playerItems.add(i);
        } else {
          this.villagerItems.add(i);
        }
      }
    }
  }
  
  public void getCraftItem(AbstractVillager villager)
  {
    for (int i = 0; i < this.playerItems.size(); i++)
    {
      CraftItem item = (CraftItem)this.playerItems.get(i);
      if (villager.canCraft(item))
      {
        villager.currentCraftItem = ((CraftItem)this.playerItems.remove(i));
        if (HelpfulVillagers.villageCollection != null) {
          HelpfulVillagers.villageCollection.func_76185_a();
        }
        return;
      }
    }
    for (int i = 0; i < this.villagerItems.size(); i++)
    {
      CraftItem item = (CraftItem)this.villagerItems.get(i);
      if (villager.canCraft(item))
      {
        villager.currentCraftItem = ((CraftItem)this.villagerItems.remove(i));
        if (HelpfulVillagers.villageCollection != null) {
          HelpfulVillagers.villageCollection.func_76185_a();
        }
        return;
      }
    }
  }
  
  public CraftItem getItemStackAt(int index)
  {
    ArrayList<CraftItem> temp = new ArrayList();
    temp.addAll(this.playerItems);
    temp.addAll(this.villagerItems);
    if (index >= temp.size()) {
      return null;
    }
    return (CraftItem)temp.get(index);
  }
  
  public void removeItemStackAt(int index)
  {
    if (index >= this.playerItems.size())
    {
      index -= this.playerItems.size();
      if (index >= this.villagerItems.size()) {
        System.out.println("ERROR: Index Too Large");
      } else {
        this.villagerItems.remove(index);
      }
    }
    else
    {
      this.playerItems.remove(index);
    }
  }
  
  public void addPlayerItem(CraftItem item)
  {
    if (item != null)
    {
      this.playerItems.add(item);
      if (HelpfulVillagers.villageCollection != null) {
        HelpfulVillagers.villageCollection.func_76185_a();
      }
    }
  }
  
  public void addVillagerItem(CraftItem item)
  {
    if (item != null)
    {
      this.villagerItems.add(item);
      if (HelpfulVillagers.villageCollection != null) {
        HelpfulVillagers.villageCollection.func_76185_a();
      }
    }
  }
  
  public int getSize()
  {
    return this.playerItems.size() + this.villagerItems.size();
  }
  
  public ArrayList<CraftItem> getPlayerQueue()
  {
    ArrayList<CraftItem> temp = new ArrayList();
    temp.addAll(this.playerItems);
    return temp;
  }
  
  public ArrayList<CraftItem> getVillagerQueue()
  {
    ArrayList<CraftItem> temp = new ArrayList();
    temp.addAll(this.villagerItems);
    return temp;
  }
  
  public ArrayList<CraftItem> getAll()
  {
    ArrayList<CraftItem> temp = new ArrayList();
    temp.addAll(this.playerItems);
    temp.addAll(this.villagerItems);
    return temp;
  }
  
  public NBTBase writeToNBT(NBTTagList nbtTagList)
  {
    for (CraftItem i : this.playerItems)
    {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.func_74757_a("Player", true);
      nbttagcompound.func_74782_a("Item", i.writeToNBT(new NBTTagCompound()));
      nbtTagList.func_74742_a(nbttagcompound);
    }
    for (CraftItem i : this.villagerItems)
    {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.func_74757_a("Player", false);
      nbttagcompound.func_74782_a("Item", i.writeToNBT(new NBTTagCompound()));
      nbtTagList.func_74742_a(nbttagcompound);
    }
    return nbtTagList;
  }
  
  public void readFromNBT(NBTTagList nbttaglist)
  {
    for (int i = 0; i < nbttaglist.func_74745_c(); i++)
    {
      NBTTagCompound nbttagcompound = nbttaglist.func_150305_b(i);
      boolean player = nbttagcompound.func_74767_n("Player");
      NBTTagCompound craftCompound = nbttagcompound.func_74775_l("Item");
      CraftItem craftItem = CraftItem.loadCraftItemFromNBT(craftCompound);
      if (player) {
        this.playerItems.add(craftItem);
      } else {
        this.villagerItems.add(craftItem);
      }
    }
  }
  
  public void clear()
  {
    this.playerItems.clear();
    this.villagerItems.clear();
    if (HelpfulVillagers.villageCollection != null) {
      HelpfulVillagers.villageCollection.func_76185_a();
    }
  }
  
  public void mergeQueue(CraftQueue otherQueue)
  {
    this.playerItems.addAll(otherQueue.getPlayerQueue());
    this.villagerItems.addAll(otherQueue.getVillagerQueue());
  }
  
  public String toString()
  {
    return this.playerItems.toString() + " " + this.villagerItems.toString();
  }
}

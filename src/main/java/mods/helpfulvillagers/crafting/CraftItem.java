package mods.helpfulvillagers.crafting;

import java.util.ArrayList;
import java.util.List;
import mods.helpfulvillagers.entity.AbstractVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class CraftItem
{
  private ItemStack item;
  private String name;
  private int priority;
  private boolean metadataSensitive;
  
  private CraftItem() {}
  
  public CraftItem(ItemStack item, String name, int priority)
  {
    this.item = item;
    this.name = name;
    this.priority = priority;
    this.metadataSensitive = false;
  }
  
  public CraftItem(ItemStack item, EntityPlayer player)
  {
    this(item, player.getDisplayName(), 1);
  }
  
  public CraftItem(ItemStack item, AbstractVillager villager)
  {
    this(item, villager.func_70005_c_() + " the " + villager.profName, 0);
  }
  
  public ItemStack getItem()
  {
    return this.item;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public int getPriority()
  {
    return this.priority;
  }
  
  public boolean isSensitive()
  {
    return this.metadataSensitive;
  }
  
  public void setSensitivity(boolean b)
  {
    this.metadataSensitive = b;
  }
  
  public List getTooltip()
  {
    List<String> list = new ArrayList();
    list.add(this.item.func_82833_r() + " x" + this.item.field_77994_a);
    if (this.priority >= 1) {
      list.add("Requested by Player:");
    } else {
      list.add("Requested by Villager:");
    }
    list.add(this.name);
    return list;
  }
  
  public NBTBase writeToNBT(NBTTagCompound compound)
  {
    this.item.func_77955_b(compound);
    compound.func_74778_a("Name", this.name);
    compound.func_74768_a("Priority", this.priority);
    compound.func_74757_a("Metadata", this.metadataSensitive);
    return compound;
  }
  
  public void readFromNBT(NBTTagCompound compound)
  {
    this.item = ItemStack.func_77949_a(compound);
    
    this.name = compound.func_74779_i("Name");
    
    this.priority = compound.func_74762_e("Priority");
    
    this.metadataSensitive = compound.func_74767_n("Metadata");
  }
  
  public static CraftItem loadCraftItemFromNBT(NBTTagCompound compound)
  {
    CraftItem item = new CraftItem();
    item.readFromNBT(compound);
    return item.getItem() != null ? item : null;
  }
}

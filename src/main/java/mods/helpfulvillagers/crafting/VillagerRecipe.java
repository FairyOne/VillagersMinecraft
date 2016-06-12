package mods.helpfulvillagers.crafting;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mods.helpfulvillagers.util.AIHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class VillagerRecipe
  implements Comparable
{
  private ArrayList<ItemStack> inputItems = new ArrayList();
  private ArrayList<ItemStack> totalInputs = new ArrayList();
  private ItemStack outputItem;
  private boolean smeltable;
  private boolean metadataSensitive = false;
  
  public VillagerRecipe() {}
  
  public VillagerRecipe(IRecipe recipe, boolean smeltable)
  {
    this.smeltable = smeltable;
    this.metadataSensitive = false;
    if ((recipe instanceof ShapedRecipes))
    {
      this.inputItems = new ArrayList();
      ShapedRecipes shaped = (ShapedRecipes)recipe;
      for (int i = 0; i < shaped.field_77574_d.length; i++) {
        if (shaped.field_77574_d[i] != null) {
          this.inputItems.add(shaped.field_77574_d[i]);
        }
      }
      this.outputItem = recipe.func_77571_b();
    }
    else if ((recipe instanceof ShapelessRecipes))
    {
      this.inputItems = ((ArrayList)((ShapelessRecipes)recipe).field_77579_b);
      this.outputItem = recipe.func_77571_b();
    }
    else if ((recipe instanceof ShapedOreRecipe))
    {
      this.inputItems = new ArrayList();
      ShapedOreRecipe shapedOre = (ShapedOreRecipe)recipe;
      for (int i = 0; i < shapedOre.getInput().length; i++) {
        if (shapedOre.getInput()[i] != null) {
          if ((shapedOre.getInput()[i] instanceof ItemStack))
          {
            this.inputItems.add((ItemStack)shapedOre.getInput()[i]);
          }
          else if ((shapedOre.getInput()[i] instanceof ArrayList))
          {
            ArrayList array = (ArrayList)shapedOre.getInput()[i];
            if (array.size() > 0) {
              this.inputItems.add((ItemStack)array.get(0));
            }
          }
          else
          {
            System.out.println("ERROR: Unknown Input Item");
          }
        }
      }
      this.outputItem = recipe.func_77571_b();
    }
    else if ((recipe instanceof ShapelessOreRecipe))
    {
      ShapelessOreRecipe shapelessOre = (ShapelessOreRecipe)recipe;
      for (int i = 0; i < shapelessOre.getInput().size(); i++) {
        if (shapelessOre.getInput().get(i) != null) {
          if ((shapelessOre.getInput().get(i) instanceof ItemStack))
          {
            this.inputItems.add((ItemStack)shapelessOre.getInput().get(i));
          }
          else if ((shapelessOre.getInput().get(i) instanceof ArrayList))
          {
            ArrayList array = (ArrayList)shapelessOre.getInput().get(i);
            for (int j = 0; j < array.size(); j++) {
              if ((array.get(j) instanceof ItemStack)) {
                this.inputItems.add((ItemStack)array.get(j));
              }
            }
          }
          else
          {
            System.out.println("ERROR: Unknown Input Item");
          }
        }
      }
      this.outputItem = recipe.func_77571_b();
    }
    else
    {
      this.inputItems = null;
      this.outputItem = null;
      
      return;
    }
    this.totalInputs = initTotalInputs();
    initMetadata();
  }
  
  public VillagerRecipe(ArrayList<ItemStack> inputs, ItemStack output, boolean smeltable)
  {
    this.smeltable = smeltable;
    this.outputItem = output.func_77946_l();
    this.inputItems.addAll(inputs);
    this.totalInputs.addAll(inputs);
    initMetadata();
  }
  
  public VillagerRecipe(ItemStack input, ItemStack output, boolean smeltable)
  {
    this.smeltable = smeltable;
    this.outputItem = output.func_77946_l();
    this.inputItems.add(input.func_77946_l());
    this.totalInputs.add(input.func_77946_l());
    initMetadata();
  }
  
  private void initMetadata()
  {
    this.metadataSensitive = this.outputItem.func_77981_g();
  }
  
  private ArrayList<ItemStack> initTotalInputs()
  {
    ArrayList<ItemStack> totals = new ArrayList();
    ArrayList<ItemStack> temp = new ArrayList();
    for (ItemStack i : this.inputItems)
    {
      ItemStack newItem = i.func_77946_l();
      newItem.field_77994_a = 1;
      temp.add(newItem);
    }
    AIHelper.mergeItemStackArrays(temp, totals);
    return totals;
  }
  
  public ArrayList<ItemStack> getInputs()
  {
    return this.inputItems;
  }
  
  public ArrayList<ItemStack> getTotalInputs()
  {
    return new ArrayList(this.totalInputs);
  }
  
  public ItemStack getOutput()
  {
    return this.outputItem;
  }
  
  public boolean getMetadataSensitivity()
  {
    return this.metadataSensitive;
  }
  
  public boolean isSmelted()
  {
    return this.smeltable;
  }
  
  public List getTooltip()
  {
    List list = new ArrayList();
    String s = this.outputItem.func_82833_r();
    if (this.smeltable) {
      s = s + " (Smelt)";
    }
    list.add(s);
    list.add("");
    for (ItemStack i : getTotalInputs())
    {
      s = i.func_82833_r() + " x" + i.field_77994_a;
      list.add(s);
    }
    return list;
  }
  
  public String toString()
  {
    String s = "";
    if (this.outputItem == null) {
      s = s + "null";
    } else {
      s = s + this.outputItem.toString();
    }
    s = s + " <-";
    
    s = s + getTotalInputs().toString();
    return s;
  }
  
  public int compareTo(Object o)
  {
    if ((o instanceof VillagerRecipe))
    {
      VillagerRecipe v = (VillagerRecipe)o;
      return getOutput().func_82833_r().compareTo(v.getOutput().func_82833_r());
    }
    return Integer.MAX_VALUE;
  }
  
  public boolean equals(Object o)
  {
    if (o == null) {
      return false;
    }
    if ((o instanceof VillagerRecipe))
    {
      VillagerRecipe v = (VillagerRecipe)o;
      if (this.smeltable == v.smeltable)
      {
        if ((this.outputItem != null) && (v.outputItem != null) && (this.outputItem.func_82833_r().equals(v.outputItem.func_82833_r())))
        {
          ArrayList<ItemStack> temp1 = new ArrayList(this.totalInputs);
          ArrayList<ItemStack> temp2 = new ArrayList(v.totalInputs);
          if (temp1.size() == temp2.size())
          {
            Iterator i = temp1.iterator();
            while (i.hasNext())
            {
              ItemStack itemI = (ItemStack)i.next();
              for (int j = 0; j < temp2.size(); j++)
              {
                ItemStack itemJ = (ItemStack)temp2.get(j);
                if ((itemI.func_82833_r().equals(itemJ.func_82833_r())) && (itemI.field_77994_a == itemJ.field_77994_a))
                {
                  i.remove();
                  temp2.remove(j);
                  break;
                }
              }
            }
            return (temp1.size() <= 0) && (temp2.size() <= 0);
          }
          return false;
        }
        return false;
      }
      return false;
    }
    return false;
  }
  
  public NBTTagList writeToNBT(NBTTagList par1NBTTagList)
  {
    NBTTagCompound nbttagcompound = new NBTTagCompound();
    nbttagcompound.func_74757_a("Smelt", this.smeltable);
    this.outputItem.func_77955_b(nbttagcompound);
    par1NBTTagList.func_74742_a(nbttagcompound);
    for (ItemStack i : this.totalInputs)
    {
      nbttagcompound = new NBTTagCompound();
      i.func_77955_b(nbttagcompound);
      par1NBTTagList.func_74742_a(nbttagcompound);
    }
    return par1NBTTagList;
  }
  
  public void readFromNBT(NBTTagList par1NBTTagList)
  {
    for (int i = 0; i < par1NBTTagList.func_74745_c(); i++)
    {
      NBTTagCompound nbttagcompound = par1NBTTagList.func_150305_b(i);
      if (i == 0)
      {
        this.smeltable = nbttagcompound.func_74767_n("Smelt");
        this.outputItem = ItemStack.func_77949_a(nbttagcompound);
      }
      else
      {
        this.totalInputs.add(ItemStack.func_77949_a(nbttagcompound));
      }
    }
    initMetadata();
  }
}

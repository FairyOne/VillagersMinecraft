package mods.helpfulvillagers.econ;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class ItemPrice
{
  private ItemStack item;
  private int price;
  private double supply = 1.0D;
  private double demand = 1.0D;
  
  public ItemPrice() {}
  
  public ItemPrice(ItemStack item, int price)
  {
    this.item = item;
    this.price = price;
  }
  
  public ItemPrice(ItemStack item, int price, double supply, double demand)
  {
    this.item = item;
    this.price = price;
    this.supply = supply;
    this.demand = demand;
  }
  
  public void changeSupply(double val)
  {
    this.supply += val;
    if (this.supply <= 0.0D)
    {
      this.supply -= val;
      return;
    }
  }
  
  public void changeDemand(double val)
  {
    this.demand += val;
    if (this.demand <= 0.0D)
    {
      this.demand -= val;
      return;
    }
  }
  
  public void increaseSupply(double amount)
  {
    changeSupply(1.0D / this.item.func_77976_d() * amount);
  }
  
  public void decreaseSupply(double amount)
  {
    changeSupply(-1.0D / this.item.func_77976_d() * amount);
  }
  
  public void increaseDemand(double amount)
  {
    changeDemand(1.0D / this.item.func_77976_d() * amount);
  }
  
  public void decreaseDemand(double amount)
  {
    changeDemand(-1.0D / this.item.func_77976_d() * amount);
  }
  
  public ItemStack getItem()
  {
    return this.item;
  }
  
  public int getOriginalPrice()
  {
    return this.price;
  }
  
  public int getPrice()
  {
    int newPrice = (int)(this.price * (this.demand / this.supply));
    if (newPrice <= 0) {
      return 1;
    }
    return newPrice;
  }
  
  public double getSupply()
  {
    return this.supply;
  }
  
  public double getDemand()
  {
    return this.demand;
  }
  
  public NBTBase writeToNBT(NBTTagCompound compound)
  {
    this.item.func_77955_b(compound);
    compound.func_74768_a("Price", this.price);
    compound.func_74780_a("Supply", this.supply);
    compound.func_74780_a("Demand", this.demand);
    return compound;
  }
  
  public void readFromNBT(NBTTagCompound compound)
  {
    this.item = ItemStack.func_77949_a(compound);
    
    this.price = compound.func_74762_e("Price");
    
    this.supply = compound.func_74769_h("Supply");
    
    this.demand = compound.func_74769_h("Demand");
  }
  
  public static ItemPrice loadCraftItemFromNBT(NBTTagCompound compound)
  {
    ItemPrice itemPrice = new ItemPrice();
    itemPrice.readFromNBT(compound);
    return itemPrice.getItem() != null ? itemPrice : null;
  }
}

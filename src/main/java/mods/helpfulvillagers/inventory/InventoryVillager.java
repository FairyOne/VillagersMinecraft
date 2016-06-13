package mods.helpfulvillagers.inventory;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import mods.helpfulvillagers.crafting.CraftItem;
import mods.helpfulvillagers.econ.VillageEconomy;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.network.InventoryPacket;
import mods.helpfulvillagers.util.AIHelper;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

public class InventoryVillager
  implements IInventory, Serializable
{
  private ItemStack[] mainInventory;
  private ItemStack[] equipmentInventory;
  public ArrayList<ItemStack> materialsCollected = new ArrayList();
  public ArrayList<ItemStack> smeltablesCollected = new ArrayList();
  String inventoryTitle;
  public AbstractVillager owner;
  
  public InventoryVillager(AbstractVillager abstractEntity)
  {
    this.mainInventory = new ItemStack[func_70302_i_()];
    this.equipmentInventory = new ItemStack[getSizeEquipment()];
    this.owner = abstractEntity;
  }
  
  public int func_70302_i_()
  {
    return 27;
  }
  
  public int getSizeEquipment()
  {
    return 5;
  }
  
  public ItemStack func_70301_a(int index)
  {
    if (index < 0) {
      return null;
    }
    ItemStack[] aitemstack = this.mainInventory;
    if (index >= aitemstack.length)
    {
      index -= aitemstack.length;
      aitemstack = this.equipmentInventory;
    }
    if (index < aitemstack.length) {
      return aitemstack[index];
    }
    return null;
  }
  
  public ItemStack func_70298_a(int index, int amount)
  {
    ItemStack[] aitemstack = this.mainInventory;
    if (index >= this.mainInventory.length)
    {
      aitemstack = this.equipmentInventory;
      index -= this.mainInventory.length;
    }
    if (index < aitemstack.length)
    {
      if (aitemstack[index].field_77994_a <= amount)
      {
        ItemStack itemstack = aitemstack[index];
        aitemstack[index] = null;
        return itemstack;
      }
      ItemStack itemstack = aitemstack[index].func_77979_a(amount);
      if (aitemstack[index].field_77994_a == 0) {
        aitemstack[index] = null;
      }
      return itemstack;
    }
    return null;
  }
  
  public void func_70299_a(int index, ItemStack itemStack)
  {
    if (index < 0) {
      return;
    }
    if (index >= this.mainInventory.length)
    {
      index -= this.mainInventory.length;
      if (index >= this.equipmentInventory.length) {
        return;
      }
      this.equipmentInventory[index] = itemStack;
    }
    else
    {
      this.mainInventory[index] = itemStack;
    }
  }
  
  public void setMainContents(int index, ItemStack itemStack)
  {
    this.mainInventory[index] = itemStack;
  }
  
  public void setEquipmentContents(int index, ItemStack itemStack)
  {
    this.equipmentInventory[index] = itemStack;
  }
  
  public ItemStack func_70304_b(int i)
  {
    return null;
  }
  
  public String func_145825_b()
  {
    return this.inventoryTitle;
  }
  
  public int func_70297_j_()
  {
    return 64;
  }
  
  public boolean func_70300_a(EntityPlayer par1EntityPlayer)
  {
    return true;
  }
  
  public boolean func_94041_b(int par1, ItemStack par2ItemStack)
  {
    if (par2ItemStack == null) {
      return true;
    }
    if (par1 == 27) {
      return this.owner.isValidTool(par2ItemStack);
    }
    if (par1 == 28)
    {
      if ((par2ItemStack.func_77973_b() instanceof ItemArmor))
      {
        ItemArmor armor = (ItemArmor)par2ItemStack.func_77973_b();
        return armor.field_77881_a == 0;
      }
      return false;
    }
    if (par1 == 29)
    {
      if ((par2ItemStack.func_77973_b() instanceof ItemArmor))
      {
        ItemArmor armor = (ItemArmor)par2ItemStack.func_77973_b();
        return armor.field_77881_a == 1;
      }
      return false;
    }
    if (par1 == 30)
    {
      if ((par2ItemStack.func_77973_b() instanceof ItemArmor))
      {
        ItemArmor armor = (ItemArmor)par2ItemStack.func_77973_b();
        return armor.field_77881_a == 2;
      }
      return false;
    }
    if (par1 == 31)
    {
      if ((par2ItemStack.func_77973_b() instanceof ItemArmor))
      {
        ItemArmor armor = (ItemArmor)par2ItemStack.func_77973_b();
        return armor.field_77881_a == 3;
      }
      return false;
    }
    return true;
  }
  
  public boolean isEmpty()
  {
    for (ItemStack i : this.mainInventory) {
      if (i != null) {
        return false;
      }
    }
    return true;
  }
  
  public boolean isFull()
  {
    for (ItemStack i : this.mainInventory) {
      if (i == null) {
        return false;
      }
    }
    return true;
  }
  
  public int containsItem(ItemStack item)
  {
    for (int i = 0; i < this.mainInventory.length; i++) {
      if ((this.mainInventory[i] != null) && (this.mainInventory[i].func_77973_b() == item.func_77973_b())) {
        return i;
      }
    }
    return -1;
  }
  
  public int containsItem()
  {
    for (int i = 0; i < this.mainInventory.length; i++) {
      if ((this.mainInventory[i] != null) && (this.owner.isValidTool(this.mainInventory[i]))) {
        return i;
      }
    }
    return -1;
  }
  
  public int containsItemWithMetadata(ItemStack item)
  {
    for (int i = 0; i < this.mainInventory.length; i++) {
      if ((this.mainInventory[i] != null) && (this.mainInventory[i].func_77973_b() == item.func_77973_b()) && (this.mainInventory[i].func_77960_j() == item.func_77960_j())) {
        return i;
      }
    }
    return -1;
  }
  
  public int getTotalAmount(ItemStack item)
  {
    int count = 0;
    for (int i = 0; i < this.mainInventory.length; i++) {
      if ((this.mainInventory[i] != null) && (this.mainInventory[i].func_77973_b().equals(item.func_77973_b()))) {
        count += this.mainInventory[i].field_77994_a;
      }
    }
    return count;
  }
  
  public int findSolidBlock(ArrayList exlude)
  {
    for (int i = 0; i < this.mainInventory.length; i++) {
      if (this.mainInventory[i] != null)
      {
        Block block = Block.func_149634_a(this.mainInventory[i].func_77973_b());
        if ((!exlude.contains(block)) && (block.func_149721_r())) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public void swapItems(int index1, int index2)
  {
    if ((index1 >= 0) && (index1 < this.mainInventory.length) && (index2 >= 0) && (index2 < this.mainInventory.length))
    {
      ItemStack item1 = this.mainInventory[index1];
      ItemStack item2 = this.mainInventory[index2];
      
      this.mainInventory[index1] = item2;
      this.mainInventory[index2] = item1;
      syncInventory();
    }
  }
  
  public void swapItems(TileEntityChest chest, int chestIndex, int invIndex)
  {
    if ((chestIndex >= 0) && (chestIndex < chest.func_70302_i_()) && (invIndex >= 0) && (invIndex < this.mainInventory.length))
    {
      ItemStack item1 = chest.func_70301_a(chestIndex);
      ItemStack item2 = this.mainInventory[invIndex];
      
      chest.func_70299_a(chestIndex, item2);
      this.mainInventory[invIndex] = item1;
      syncInventory();
    }
  }
  
  public void swapEquipment(int index1, int index2)
  {
    if ((index1 >= 0) && (index1 < this.mainInventory.length) && (index2 >= 0) && (index2 < this.equipmentInventory.length))
    {
      ItemStack item1 = this.mainInventory[index1];
      ItemStack item2 = this.equipmentInventory[index2];
      
      this.mainInventory[index1] = item2;
      this.equipmentInventory[index2] = item1;
      syncInventory();
    }
  }
  
  public void swapEquipment(TileEntityChest chest, int chestIndex, int invIndex)
  {
    if ((chestIndex >= 0) && (chestIndex < chest.func_70302_i_()) && (invIndex >= 0) && (invIndex < this.equipmentInventory.length))
    {
      ItemStack item1 = chest.func_70301_a(chestIndex);
      ItemStack item2 = this.equipmentInventory[invIndex];
      
      chest.func_70299_a(chestIndex, item2);
      this.equipmentInventory[invIndex] = item1;
      syncInventory();
    }
  }
  
  public void addItem(ItemStack item)
  {
    for (int i = 0; i < this.equipmentInventory.length; i++) {
      if ((this.equipmentInventory[i] == null) && (func_94041_b(this.mainInventory.length + i, item)))
      {
        this.equipmentInventory[i] = item;
        return;
      }
    }
    for (int i = 0; i < this.mainInventory.length; i++) {
      if ((this.mainInventory[i] != null) && (this.mainInventory[i].func_82833_r().equals(item.func_82833_r())))
      {
        int temp = item.field_77994_a;
        while (this.mainInventory[i].field_77994_a < this.mainInventory[i].func_77976_d())
        {
          this.mainInventory[i].field_77994_a += 1;
          temp--;
          if (temp <= 0)
          {
            item = null;
            return;
          }
        }
        item.field_77994_a = temp;
      }
    }
    for (int i = 0; i < this.mainInventory.length; i++) {
      if (this.mainInventory[i] == null)
      {
        this.mainInventory[i] = item.func_77946_l();
        return;
      }
    }
    if (this.owner.field_70170_p.field_72995_K) {
      dropItem(item);
    }
  }
  
  private void dropItem(ItemStack item)
  {
    if (item != null)
    {
      EntityItem worldItem = new EntityItem(this.owner.field_70170_p, this.owner.field_70165_t, this.owner.field_70163_u, this.owner.field_70161_v, item);
      this.owner.field_70170_p.func_72838_d(worldItem);
    }
  }
  
  public void dropFromInventory(int i)
  {
    if (!this.owner.field_70170_p.field_72995_K)
    {
      ItemStack stack = func_70301_a(i);
      if (stack != null)
      {
        dropItem(stack);
        func_70299_a(i, null);
      }
    }
  }
  
  public void dumpInventory(TileEntityChest chest)
  {
    syncInventory();
    chest.func_70295_k_();
    for (int i = 0; i < this.mainInventory.length; i++) {
      if (this.mainInventory[i] != null) {
        for (int j = 0; j < chest.func_70302_i_(); j++)
        {
          ItemStack chestItem = chest.func_70301_a(j);
          if (chestItem == null)
          {
            chest.func_70299_a(j, this.mainInventory[i]);
            this.owner.homeVillage.economy.increaseItemSupply(this.owner, this.mainInventory[i]);
            this.mainInventory[i] = null;
            break;
          }
          if (chestItem.field_77994_a < chestItem.func_77976_d()) {
            if (this.mainInventory[i].func_82833_r().equals(chestItem.func_82833_r()))
            {
              int chestStackSize = chestItem.field_77994_a;
              int invStackSize = this.mainInventory[i].field_77994_a;
              chestStackSize += invStackSize;
              if (chestStackSize > chestItem.func_77976_d())
              {
                invStackSize = chestStackSize - chestItem.func_77976_d();
                chestStackSize = chestItem.func_77976_d();
              }
              else
              {
                invStackSize = 0;
              }
              this.mainInventory[i].field_77994_a = invStackSize;
              if (this.mainInventory[i].field_77994_a <= 0) {
                this.mainInventory[i] = null;
              }
              chestItem.field_77994_a = chestStackSize;
              chest.func_70299_a(j, chestItem);
              this.owner.homeVillage.economy.increaseItemSupply(this.owner, chestItem);
            }
          }
        }
      }
    }
    chest.func_70305_f();
  }
  
  public void dumpInventory()
  {
    for (int i = 0; i < this.mainInventory.length + this.equipmentInventory.length; i++) {
      dropFromInventory(i);
    }
  }
  
  public void storeAsCollected(ItemStack item, boolean smelt)
  {
    if (smelt) {
      for (int i = 0; i < this.mainInventory.length; i++)
      {
        ItemStack invItem = this.mainInventory[i];
        Block block = Block.func_149634_a(item.func_77973_b());
        Block invBlock = Block.func_149634_a(invItem != null ? invItem.func_77973_b() : null);
        if ((invItem != null) && (invItem.field_77994_a > 0) && (((this.owner.currentCraftItem.isSensitive()) && (invItem.func_82833_r().equals(item.func_82833_r()))) || ((!this.owner.currentCraftItem.isSensitive()) && ((invItem.func_77973_b().equals(item.func_77973_b())) || (((block instanceof BlockLog)) && ((invBlock instanceof BlockLog)))))))
        {
          if (invItem.field_77994_a >= item.field_77994_a)
          {
            int itemSize = item.field_77994_a;
            invItem.field_77994_a -= itemSize;
            AIHelper.mergeItemStackArrays(new ItemStack(item.func_77973_b(), itemSize), this.smeltablesCollected);
            if (invItem.field_77994_a <= 0) {
              invItem = null;
            }
            this.mainInventory[i] = invItem;
            item.field_77994_a = 0;
            return;
          }
          int itemSize = invItem.field_77994_a;
          item.field_77994_a -= invItem.field_77994_a;
          AIHelper.mergeItemStackArrays(new ItemStack(invItem.func_77973_b(), itemSize), this.smeltablesCollected);
          this.mainInventory[i] = null;
        }
      }
    } else {
      for (int i = 0; i < this.mainInventory.length; i++)
      {
        ItemStack invItem = this.mainInventory[i];
        Block block = Block.func_149634_a(item.func_77973_b());
        Block invBlock = Block.func_149634_a(invItem != null ? invItem.func_77973_b() : null);
        if ((invItem != null) && (invItem.field_77994_a > 0) && (((this.owner.currentCraftItem.isSensitive()) && (invItem.func_82833_r().equals(item.func_82833_r()))) || ((!this.owner.currentCraftItem.isSensitive()) && ((invItem.func_77973_b().equals(item.func_77973_b())) || (((block instanceof BlockLog)) && ((invBlock instanceof BlockLog)))))))
        {
          if (invItem.field_77994_a >= item.field_77994_a)
          {
            int itemSize = item.field_77994_a;
            invItem.field_77994_a -= itemSize;
            AIHelper.mergeItemStackArrays(new ItemStack(item.func_77973_b(), itemSize), this.materialsCollected);
            if (invItem.field_77994_a <= 0) {
              invItem = null;
            }
            this.mainInventory[i] = invItem;
            item.field_77994_a = 0;
            return;
          }
          int itemSize = invItem.field_77994_a;
          item.field_77994_a -= invItem.field_77994_a;
          AIHelper.mergeItemStackArrays(new ItemStack(invItem.func_77973_b(), itemSize), this.materialsCollected);
          this.mainInventory[i] = null;
        }
      }
    }
  }
  
  public void dumpCollected(boolean smelt)
  {
    ArrayList<ItemStack> tempList = new ArrayList();
    int slot = 0;
    if (smelt) {
      for (ItemStack i : this.smeltablesCollected)
      {
        if (this.mainInventory[slot] != null) {
          tempList.add(this.mainInventory[slot].func_77946_l());
        }
        this.mainInventory[slot] = i;
        slot++;
      }
    } else {
      for (ItemStack i : this.materialsCollected) {
        if (i.func_82833_r().equals(this.owner.currentCraftItem.getItem().func_82833_r()))
        {
          boolean stored = this.owner.storeCraftedItem();
          if (stored) {}
        }
        else
        {
          if (this.mainInventory[slot] != null) {
            tempList.add(this.mainInventory[slot].func_77946_l());
          }
          this.mainInventory[slot] = i;
          slot++;
        }
      }
    }
    for (ItemStack i : tempList) {
      addItem(i);
    }
  }
  
  public void decrementSlot(int index)
  {
    this.mainInventory[index].field_77994_a -= 1;
    if (this.mainInventory[index].field_77994_a <= 0) {
      this.mainInventory[index] = null;
    }
  }
  
  public ItemStack getCurrentItem()
  {
    return this.equipmentInventory[0];
  }
  
  public void setCurrentItem(ItemStack itemStack)
  {
    this.equipmentInventory[0] = itemStack;
  }
  
  public void printInventory()
  {
    if (this.owner.field_70170_p.field_72995_K) {
      System.out.println("Client: ");
    } else {
      System.out.println("Server: ");
    }
    for (int i = 0; i < this.mainInventory.length; i++) {
      if (this.mainInventory[i] != null) {
        System.out.println("Item in slot " + i + ": " + this.mainInventory[i].func_82833_r() + " Amount: " + this.mainInventory[i].field_77994_a);
      } else {
        System.out.println("Item in slot " + i + ": nothing");
      }
    }
    for (int i = 0; i < this.equipmentInventory.length; i++) {
      if (this.equipmentInventory[i] != null) {
        System.out.println("Armor in slot " + i + ": " + this.equipmentInventory[i].func_82833_r());
      } else {
        System.out.println("Armor in slot " + i + ": nothing");
      }
    }
    System.out.println();
  }
  
  public void syncInventory()
  {
    if (!this.owner.field_70170_p.field_72995_K) {
      HelpfulVillagers.network.sendToAll(new InventoryPacket(this.owner.func_145782_y(), this.mainInventory, this.equipmentInventory));
    }
  }
  
  public void syncEquipment()
  {
    if (!this.owner.field_70170_p.field_72995_K) {
      HelpfulVillagers.network.sendToAll(new InventoryPacket(this.owner.func_145782_y(), null, this.equipmentInventory));
    }
  }
  
  public NBTTagList writeToNBT(NBTTagList par1NBTTagList)
  {
    for (int i = 0; i < this.mainInventory.length; i++) {
      if (this.mainInventory[i] != null)
      {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.func_74774_a("Slot", (byte)i);
        this.mainInventory[i].func_77955_b(nbttagcompound);
        par1NBTTagList.func_74742_a(nbttagcompound);
      }
    }
    for (int i = 0; i < this.equipmentInventory.length; i++) {
      if (this.equipmentInventory[i] != null)
      {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.func_74774_a("Slot", (byte)(i + this.mainInventory.length));
        this.equipmentInventory[i].func_77955_b(nbttagcompound);
        par1NBTTagList.func_74742_a(nbttagcompound);
      }
    }
    for (ItemStack i : this.materialsCollected)
    {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.func_74774_a("Slot", (byte)-1);
      i.func_77955_b(nbttagcompound);
      par1NBTTagList.func_74742_a(nbttagcompound);
    }
    for (ItemStack i : this.smeltablesCollected)
    {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.func_74774_a("Slot", (byte)-1);
      i.func_77955_b(nbttagcompound);
      par1NBTTagList.func_74742_a(nbttagcompound);
    }
    return par1NBTTagList;
  }
  
  public void readFromNBT(NBTTagList par1NBTTagList)
  {
    for (int i = 0; i < par1NBTTagList.func_74745_c(); i++) {
      if (par1NBTTagList.func_74745_c() > 0)
      {
        NBTTagCompound nbttagcompound = par1NBTTagList.func_150305_b(i);
        byte slot = nbttagcompound.func_74771_c("Slot");
        ItemStack itemstack = ItemStack.func_77949_a(nbttagcompound);
        if (slot >= 0) {
          func_70299_a(slot, itemstack);
        } else {
          addItem(itemstack);
        }
      }
    }
  }
  
  public boolean func_145818_k_()
  {
    return false;
  }
  
  public void func_70295_k_() {}
  
  public void func_70305_f() {}
  
  public void func_70296_d() {}
}

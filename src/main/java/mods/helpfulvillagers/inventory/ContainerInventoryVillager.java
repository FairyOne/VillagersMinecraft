package mods.helpfulvillagers.inventory;

import java.util.List;
import mods.helpfulvillagers.entity.AbstractVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerInventoryVillager
  extends Container
{
  private AbstractVillager villager;
  
  public ContainerInventoryVillager(IInventory inventoryPlayer, IInventory inventoryVillager, AbstractVillager villager)
  {
    try
    {
      this.villager = villager;
      int slotIndex = 0;
      for (int height = 0; height < 3; height++) {
        for (int width = 0; width < 9; width++)
        {
          func_75146_a(new Slot(inventoryVillager, slotIndex, width * 18 + 8, height * 18 + 9));
          slotIndex++;
        }
      }
      for (int slot = 0; slot < 5; slot++)
      {
        func_75146_a(new Slot(inventoryVillager, slotIndex, slot * 18 + 43, 68));
        slotIndex++;
      }
      slotIndex = 0;
      for (int height = 0; height < 3; height++) {
        for (int width = 0; width < 9; width++)
        {
          func_75146_a(new Slot(inventoryPlayer, slotIndex + 9, width * 18 + 8, height * 18 + 93));
          slotIndex++;
        }
      }
      for (int i = 0; i < 9; i++) {
        func_75146_a(new Slot(inventoryPlayer, i, i * 18 + 8, 151));
      }
    }
    catch (ArrayIndexOutOfBoundsException e) {}
  }
  
  public boolean func_75145_c(EntityPlayer var1)
  {
    return true;
  }
  
  public ItemStack func_82846_b(EntityPlayer player, int slotId)
  {
    try
    {
      Slot slot = (Slot)this.field_75151_b.get(slotId);
      ItemStack transferStack = null;
      if ((slot != null) && (slot.func_75216_d()))
      {
        ItemStack slotStack = slot.func_75211_c();
        transferStack = slotStack.func_77946_l();
        if (slotId < 32)
        {
          if (!func_75135_a(slotStack, 32, this.field_75151_b.size(), true)) {
            return null;
          }
        }
        else if (!func_75135_a(slotStack, 0, 32, false)) {
          return null;
        }
        if (slotStack.field_77994_a <= 0) {
          slot.func_75215_d(null);
        } else {
          slot.func_75218_e();
        }
      }
      return transferStack;
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public void func_75134_a(EntityPlayer par1EntityPlayer)
  {
    for (int i = 27; i < 32; i++) {
      if (!this.villager.inventory.func_94041_b(i, func_75139_a(i).func_75211_c()))
      {
        this.villager.inventory.dropFromInventory(i);
        func_75139_a(i).func_75215_d(null);
      }
    }
  }
}

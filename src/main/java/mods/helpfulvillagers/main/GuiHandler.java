package mods.helpfulvillagers.main;

import cpw.mods.fml.common.network.IGuiHandler;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.gui.GuiBarter;
import mods.helpfulvillagers.gui.GuiCraftStats;
import mods.helpfulvillagers.gui.GuiCraftingMenu;
import mods.helpfulvillagers.gui.GuiNickname;
import mods.helpfulvillagers.gui.GuiProfessionDialog;
import mods.helpfulvillagers.gui.GuiTeachRecipe;
import mods.helpfulvillagers.gui.GuiTeachRecipe.VillagerContainerWorkbench;
import mods.helpfulvillagers.gui.GuiVillagerDialog;
import mods.helpfulvillagers.gui.GuiVillagerInventory;
import mods.helpfulvillagers.inventory.ContainerInventoryVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler
  implements IGuiHandler
{
  public Object getServerGuiElement(int guiId, EntityPlayer player, World world, int posX, int posY, int posZ)
  {
    AbstractVillager villager = (AbstractVillager)world.func_73045_a(posX);
    if (villager != null) {
      switch (guiId)
      {
      case 0: 
        break;
      case 1: 
        break;
      case 2: 
        return new ContainerInventoryVillager(player.field_71071_by, villager.inventory, villager);
      case 3: 
        break;
      case 4: 
        break;
      case 5: 
        break;
      case 6: 
        return new GuiTeachRecipe.VillagerContainerWorkbench(player);
      }
    }
    return null;
  }
  
  public Object getClientGuiElement(int guiId, EntityPlayer player, World world, int posX, int posY, int posZ)
  {
    AbstractVillager villager = (AbstractVillager)world.func_73045_a(posX);
    if (villager != null) {
      switch (guiId)
      {
      case 0: 
        return new GuiVillagerDialog(player, villager);
      case 1: 
        return new GuiProfessionDialog(player, villager);
      case 2: 
        return new GuiVillagerInventory(villager, player.field_71071_by, villager.inventory);
      case 3: 
        return new GuiNickname(player, villager);
      case 4: 
        return new GuiCraftingMenu(player, villager);
      case 5: 
        return new GuiCraftStats(player, villager);
      case 6: 
        return new GuiTeachRecipe(player, villager);
      case 7: 
        return new GuiBarter(player, villager);
      }
    }
    return null;
  }
}

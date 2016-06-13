package mods.helpfulvillagers.gui;

import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.entity.EntityArcher;
import mods.helpfulvillagers.entity.EntityFarmer;
import mods.helpfulvillagers.entity.EntityLumberjack;
import mods.helpfulvillagers.entity.EntityMiner;
import mods.helpfulvillagers.entity.EntitySoldier;
import mods.helpfulvillagers.inventory.ContainerInventoryVillager;
import mods.helpfulvillagers.inventory.InventoryVillager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiVillagerInventory
  extends GuiContainer
{
  private final int xSizeOfTexture = this.field_146999_f;
  private final int ySizeOfTexture = this.field_147000_g + 7;
  private AbstractVillager villager;
  
  public GuiVillagerInventory(AbstractVillager villager, IInventory playerInventory, IInventory villagerInventory)
  {
    super(new ContainerInventoryVillager(playerInventory, villagerInventory, villager));
    this.villager = villager;
  }
  
  protected void func_146979_b(int par1, int par2) {}
  
  protected void func_146976_a(float var1, int var2, int var3)
  {
    this.field_146297_k.field_71446_o.func_110577_a(new ResourceLocation("helpfulvillagers", "textures/gui/villager_trade.png"));
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    int x = (this.field_146294_l - this.field_146999_f) / 2;
    int y = (this.field_146295_m - this.field_147000_g) / 2;
    func_73729_b(x, y, 0, 0, this.xSizeOfTexture, this.ySizeOfTexture);
    if (this.villager.inventory.func_70301_a(28) == null) {
      func_73729_b(x + 64, y + 72, 176, 0, 10, 9);
    }
    if (this.villager.inventory.func_70301_a(29) == null) {
      func_73729_b(x + 80, y + 70, 176, 9, 14, 13);
    }
    if (this.villager.inventory.func_70301_a(30) == null) {
      func_73729_b(x + 100, y + 70, 176, 22, 14, 13);
    }
    if (this.villager.inventory.func_70301_a(31) == null) {
      func_73729_b(x + 116, y + 71, 176, 35, 14, 10);
    }
    if (this.villager.getCurrentItem() == null) {
      if ((this.villager instanceof EntityLumberjack)) {
        func_73729_b(x + 43, y + 69, 176, 45, 14, 14);
      } else if ((this.villager instanceof EntityMiner)) {
        func_73729_b(x + 44, y + 70, 176, 59, 14, 14);
      } else if ((this.villager instanceof EntityFarmer)) {
        func_73729_b(x + 43, y + 69, 176, 73, 14, 14);
      } else if ((this.villager instanceof EntitySoldier)) {
        func_73729_b(x + 43, y + 68, 176, 88, 16, 16);
      } else if ((this.villager instanceof EntityArcher)) {
        func_73729_b(x + 44, y + 69, 176, 105, 16, 16);
      }
    }
  }
}

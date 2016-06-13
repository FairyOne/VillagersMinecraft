package mods.helpfulvillagers.gui;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import java.util.ArrayList;
import java.util.List;
import mods.helpfulvillagers.crafting.CraftItem;
import mods.helpfulvillagers.crafting.CraftQueue;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.inventory.InventoryVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.network.CraftQueueServerPacket;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiCraftStats
  extends GuiContainer
{
  private static final int MAX_SLOTS = 9;
  public final int xSizeOfTexture = 195;
  public final int ySizeOfTexture = 136;
  private EntityPlayer player;
  private static AbstractVillager villager;
  private static InventoryBasic craftQueueInv = new InventoryBasic("Craft Queue", true, 9);
  private static InventoryBasic materialsNeededInv = new InventoryBasic("Materials Needed", true, 9);
  private static InventoryBasic materialsCollectedInv = new InventoryBasic("Materials Collected", true, 9);
  private static int index1;
  private static int index2;
  private static int index3;
  private ScrollButton leftButton1;
  private ScrollButton leftButton2;
  private ScrollButton leftButton3;
  private ScrollButton rightButton1;
  private ScrollButton rightButton2;
  private ScrollButton rightButton3;
  private GuiButton backButton;
  private GuiButton removeButton;
  private static int selectedCraftIndex;
  private static CraftItem selectedCraftItem;
  
  public GuiCraftStats(EntityPlayer player, AbstractVillager villager)
  {
    super(new StatsContainer());
    this.player = player;
    villager = villager;
    index1 = 0;
    index2 = 0;
    index3 = 0;
    selectedCraftIndex = -1;
  }
  
  public void func_73866_w_()
  {
    super.func_73866_w_();
    int posX = (this.field_146294_l - 195) / 2;
    int posY = (this.field_146295_m - 136) / 2;
    
    this.leftButton1 = new ScrollButton(0, posX + 4, posY + 22, true);
    this.rightButton1 = new ScrollButton(1, posX + 180, posY + 22, false);
    this.field_146292_n.add(this.leftButton1);
    this.field_146292_n.add(this.rightButton1);
    this.leftButton1.field_146124_l = false;
    this.rightButton1.field_146124_l = false;
    
    this.leftButton2 = new ScrollButton(2, posX + 4, posY + 58, true);
    this.rightButton2 = new ScrollButton(3, posX + 180, posY + 58, false);
    this.field_146292_n.add(this.leftButton2);
    this.field_146292_n.add(this.rightButton2);
    this.leftButton2.field_146124_l = false;
    this.rightButton2.field_146124_l = false;
    
    this.leftButton3 = new ScrollButton(4, posX + 4, posY + 93, true);
    this.rightButton3 = new ScrollButton(5, posX + 180, posY + 93, false);
    this.field_146292_n.add(this.leftButton3);
    this.field_146292_n.add(this.rightButton3);
    this.leftButton3.field_146124_l = false;
    this.rightButton3.field_146124_l = false;
    
    this.backButton = new GuiButton(6, posX + 20, posY + 112, 60, 20, "Back");
    this.field_146292_n.add(this.backButton);
    
    this.removeButton = new GuiButton(7, posX + 120, posY + 112, 60, 20, "Remove");
    this.field_146292_n.add(this.removeButton);
    this.removeButton.field_146124_l = false;
  }
  
  protected void func_146976_a(float var1, int var2, int var3)
  {
    func_146276_q_();
    
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.field_146297_k.field_71446_o.func_110577_a(new ResourceLocation("helpfulvillagers", "textures/gui/craft_details.png"));
    
    int posX = (this.field_146294_l - 195) / 2;
    int posY = (this.field_146295_m - 136) / 2;
    
    func_73729_b(posX, posY, 0, 0, 195, 136);
    
    func_73731_b(this.field_146289_q, "Village Craft Queue:", posX + 18, posY + 10, 16777215);
    func_73731_b(this.field_146289_q, "Materials Needed:", posX + 18, posY + 45, 16777215);
    func_73731_b(this.field_146289_q, "Materials Collected:", posX + 18, posY + 80, 16777215);
    
    this.field_146297_k.field_71446_o.func_110577_a(new ResourceLocation("helpfulvillagers", "textures/gui/craft_details.png"));
    
    int indexDiff = selectedCraftIndex - index1;
    if ((indexDiff >= 0) && (indexDiff <= 8)) {
      func_73729_b(indexDiff * 18 + posX + 16, posY + 17, 232, 0, 24, 22);
    }
  }
  
  public void func_73876_c()
  {
    super.func_73876_c();
    this.leftButton1.field_146124_l = (index1 > 0);
    this.rightButton1.field_146124_l = (index1 < villager.homeVillage.craftQueue.getSize() - 9);
    
    this.leftButton2.field_146124_l = (index2 > 0);
    this.rightButton2.field_146124_l = (index2 < villager.materialsNeeded.size() - 9);
    
    this.leftButton3.field_146124_l = (index3 > 0);
    this.rightButton3.field_146124_l = (index3 < villager.inventory.materialsCollected.size() - 9);
    
    int indexDiff = selectedCraftIndex - index1;
    this.removeButton.field_146124_l = ((selectedCraftItem != null) && ((selectedCraftItem.getPriority() <= 0) || (selectedCraftItem.getName().equals(this.player.getDisplayName()))) && (indexDiff >= 0) && (indexDiff <= 8));
  }
  
  public void func_73863_a(int x, int y, float f)
  {
    StatsContainer.scrollTo(index1, index2, index3);
    super.func_73863_a(x, y, f);
  }
  
  protected void func_146285_a(ItemStack item, int x, int y)
  {
    if (item != null)
    {
      CraftItem craftItem = getCraftItemAt(item, x, y);
      if (craftItem != null)
      {
        List list = craftItem.getTooltip();
        func_146283_a(list, x, y);
      }
    }
  }
  
  private CraftItem getCraftItemAt(ItemStack item, int x, int y)
  {
    int posX = (this.field_146294_l - 195) / 2;
    int posY = (this.field_146295_m - 136) / 2;
    if (y > posY + 40) {
      return null;
    }
    int n = x - posX;
    n = (n + 4) / 5 * 5;
    n /= 17;
    n--;
    if (n > index1 + 9) {
      n = index1 + 9;
    }
    try
    {
      CraftItem craftItem = villager.homeVillage.craftQueue.getItemStackAt(index1 + n);
      if (craftItem.getItem().func_82833_r().equals(item.func_82833_r())) {
        return craftItem;
      }
      return villager.homeVillage.craftQueue.getItemStackAt(index1 + n - 1);
    }
    catch (NullPointerException e) {}
    return null;
  }
  
  public static void setSelectedCraftItem(int slot)
  {
    if ((slot >= 0) && (slot <= 8))
    {
      selectedCraftIndex = index1 + slot;
      if ((selectedCraftIndex >= 0) && (selectedCraftIndex < villager.homeVillage.craftQueue.getSize())) {
        selectedCraftItem = villager.homeVillage.craftQueue.getItemStackAt(selectedCraftIndex);
      } else {
        selectedCraftItem = null;
      }
    }
  }
  
  protected void func_146284_a(GuiButton button)
  {
    switch (button.field_146127_k)
    {
    case 0: 
      index1 -= 1;
      if (index1 < 0) {
        index1 = 0;
      }
      break;
    case 1: 
      index1 += 1;
      if (index1 > villager.homeVillage.craftQueue.getSize()) {
        index1 = villager.homeVillage.craftQueue.getSize();
      }
      break;
    case 2: 
      index2 -= 1;
      if (index2 < 0) {
        index2 = 0;
      }
      break;
    case 3: 
      index2 += 1;
      if (index2 > villager.materialsNeeded.size()) {
        index2 = villager.materialsNeeded.size();
      }
      break;
    case 4: 
      index3 -= 1;
      if (index3 < 0) {
        index3 = 0;
      }
      break;
    case 5: 
      index3 += 1;
      if (index3 > villager.inventory.materialsCollected.size()) {
        index3 = villager.inventory.materialsCollected.size();
      }
      break;
    case 6: 
      this.player.openGui(HelpfulVillagers.instance, 4, villager.field_70170_p, villager.func_145782_y(), 0, 0);
      break;
    case 7: 
      villager.homeVillage.craftQueue.removeItemStackAt(selectedCraftIndex);
      HelpfulVillagers.network.sendToServer(new CraftQueueServerPacket(villager.func_145782_y(), villager.homeVillage.craftQueue.getAll()));
    }
  }
  
  private static class StatsContainer
    extends Container
  {
    public StatsContainer()
    {
      for (int i = 0; i < 9; i++) {
        func_75146_a(new Slot(GuiCraftStats.craftQueueInv, i, i * 18 + 9, 36));
      }
      for (int i = 0; i < 9; i++) {
        func_75146_a(new Slot(GuiCraftStats.materialsNeededInv, i, i * 18 + 9, 72));
      }
      for (int i = 0; i < 9; i++) {
        func_75146_a(new Slot(GuiCraftStats.materialsCollectedInv, i, i * 18 + 9, 108));
      }
    }
    
    public static void scrollTo(int index1, int index2, int index3)
    {
      for (int i = 0; i < 9; i++)
      {
        if (i + index1 >= GuiCraftStats.villager.homeVillage.craftQueue.getSize()) {
          GuiCraftStats.craftQueueInv.func_70299_a(i, null);
        } else {
          GuiCraftStats.craftQueueInv.func_70299_a(i, GuiCraftStats.villager.homeVillage.craftQueue.getItemStackAt(i + index1).getItem());
        }
        if (i + index2 >= GuiCraftStats.villager.materialsNeeded.size()) {
          GuiCraftStats.materialsNeededInv.func_70299_a(i, null);
        } else {
          GuiCraftStats.materialsNeededInv.func_70299_a(i, (ItemStack)GuiCraftStats.villager.materialsNeeded.get(i + index2));
        }
        if (i + index3 >= GuiCraftStats.villager.inventory.materialsCollected.size()) {
          GuiCraftStats.materialsCollectedInv.func_70299_a(i, null);
        } else {
          GuiCraftStats.materialsCollectedInv.func_70299_a(i, (ItemStack)GuiCraftStats.villager.inventory.materialsCollected.get(i + index3));
        }
      }
    }
    
    public boolean func_75145_c(EntityPlayer player)
    {
      return true;
    }
    
    public ItemStack func_75144_a(int p_75144_1_, int p_75144_2_, int p_75144_3_, EntityPlayer p_75144_4_)
    {
      try
      {
        Slot slot = func_75139_a(p_75144_1_);
        if (slot.field_75224_c.func_145825_b().equals("Craft Queue")) {
          GuiCraftStats.setSelectedCraftItem(p_75144_1_);
        }
        return null;
      }
      catch (ArrayIndexOutOfBoundsException e) {}
      return null;
    }
    
    public ItemStack func_82846_b(EntityPlayer p_82846_1_, int p_82846_2_)
    {
      return null;
    }
  }
  
  private static class ScrollButton
    extends GuiButton
  {
    private boolean left;
    
    public ScrollButton(int id, int x, int y, boolean flip)
    {
      super(x, y, 12, 19, "");
      this.left = flip;
    }
    
    public void func_146112_a(Minecraft mc, int x, int y)
    {
      if (this.field_146125_m)
      {
        mc.func_110434_K().func_110577_a(new ResourceLocation("helpfulvillagers", "textures/gui/craft_details.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        boolean flag = (x >= this.field_146128_h) && (y >= this.field_146129_i) && (x < this.field_146128_h + this.field_146120_f) && (y < this.field_146129_i + this.field_146121_g);
        int u = 195;
        int v = 0;
        if (this.left) {
          v += this.field_146121_g;
        }
        if (!this.field_146124_l) {
          u += this.field_146120_f * 2;
        } else if (flag) {
          u += this.field_146120_f;
        }
        func_73729_b(this.field_146128_h, this.field_146129_i, u, v, this.field_146120_f, this.field_146121_g);
      }
    }
  }
}

package mods.helpfulvillagers.gui;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import mods.helpfulvillagers.crafting.CraftItem;
import mods.helpfulvillagers.crafting.VillagerRecipe;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.network.CraftItemServerPacket;
import mods.helpfulvillagers.network.GUICommandPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
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
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiCraftingMenu
  extends GuiContainer
{
  public final int xSizeOfTexture = 195;
  public final int ySizeOfTexture = 136;
  public static final int MAX_ROWS = 3;
  public static final int MAX_COLS = 9;
  private EntityPlayer player;
  private static AbstractVillager villager;
  private static InventoryBasic selectedItemInv = new InventoryBasic("Selected Item", true, 1);
  private static InventoryBasic currentCraftInv = new InventoryBasic("Current Craft", true, 1);
  private static InventoryBasic recipesInv = new InventoryBasic("Recipes", true, 27);
  private AmountButton addButton;
  private AmountButton subButton;
  private static AmountButton trigger;
  private GuiButton addCraftButton;
  private TeachButton teachButton;
  private StatsButton statsButton;
  private static List itemList;
  private float currentScroll;
  private boolean wasClicking;
  private boolean isScrolling;
  private static int lowestStackSize;
  
  public GuiCraftingMenu(EntityPlayer player, AbstractVillager villager)
  {
    super(new CraftingContainer());
    this.player = player;
    villager = villager;
    this.currentScroll = 0.0F;
    initItemList();
  }
  
  private void initItemList()
  {
    itemList = new ArrayList();
    for (VillagerRecipe i : villager.knownRecipes) {
      itemList.add(i.getOutput());
    }
  }
  
  public void func_73866_w_()
  {
    super.func_73866_w_();
    int posX = (this.field_146294_l - 195) / 2;
    int posY = (this.field_146295_m - 136) / 2;
    
    this.addButton = new AmountButton(0, posX + 58, posY + 70, true);
    this.subButton = new AmountButton(1, posX + 58, posY + 102, false);
    this.field_146292_n.add(this.addButton);
    this.field_146292_n.add(this.subButton);
    this.addButton.field_146124_l = false;
    this.subButton.field_146124_l = false;
    
    this.addCraftButton = new GuiButton(3, posX + 83, posY + 110, 60, 20, "");
    this.field_146292_n.add(this.addCraftButton);
    this.addCraftButton.field_146124_l = false;
    
    this.teachButton = new TeachButton(4, posX + 173, posY + 90);
    this.field_146292_n.add(this.teachButton);
    
    this.statsButton = new StatsButton(5, posX + 172, posY + 110);
    this.field_146292_n.add(this.statsButton);
  }
  
  public boolean func_73868_f()
  {
    return false;
  }
  
  public void func_73876_c()
  {
    super.func_73876_c();
    this.addButton.field_146124_l = ((selectedItemInv.func_70301_a(0) != null) && (selectedItemInv.func_70301_a(0).field_77994_a < selectedItemInv.func_70301_a(0).func_77976_d()));
    this.subButton.field_146124_l = ((selectedItemInv.func_70301_a(0) != null) && (selectedItemInv.func_70301_a(0).field_77994_a > lowestStackSize));
    this.addCraftButton.field_146124_l = (selectedItemInv.func_70301_a(0) != null);
    if (villager.currentCraftItem == null) {
      this.addCraftButton.field_146126_j = "Craft Item";
    } else {
      this.addCraftButton.field_146126_j = "Queue Item";
    }
    triggerButton();
  }
  
  private void triggerButton()
  {
    if (trigger != null)
    {
      func_146284_a(trigger);
      trigger = null;
    }
  }
  
  public void func_73863_a(int x, int y, float f)
  {
    boolean flag = Mouse.isButtonDown(0);
    int k = (this.field_146294_l - 195) / 2;
    int l = (this.field_146295_m - 136) / 2;
    int i1 = k + 174;
    int j1 = l + 8;
    int k1 = i1 + 14;
    int l1 = j1 + 52;
    if ((!this.wasClicking) && (flag) && (x >= i1) && (y >= j1) && (x < k1) && (y < l1)) {
      this.isScrolling = needsScrollBars();
    }
    if (!flag) {
      this.isScrolling = false;
    }
    this.wasClicking = flag;
    if (this.isScrolling)
    {
      this.currentScroll = ((y - j1 - 7.5F) / (l1 - j1 - 15.0F));
      if (this.currentScroll < 0.0F) {
        this.currentScroll = 0.0F;
      }
      if (this.currentScroll > 1.0F) {
        this.currentScroll = 1.0F;
      }
    }
    CraftingContainer.scrollTo(this.currentScroll);
    super.func_73863_a(x, y, f);
  }
  
  private boolean needsScrollBars()
  {
    return itemList.size() > 27;
  }
  
  protected void func_146976_a(float var1, int var2, int var3)
  {
    func_146276_q_();
    
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.field_146297_k.field_71446_o.func_110577_a(new ResourceLocation("helpfulvillagers", "textures/gui/craft_menu.png"));
    
    int posX = (this.field_146294_l - 195) / 2;
    int posY = (this.field_146295_m - 136) / 2;
    
    func_73729_b(posX, posY, 0, 0, 195, 136);
    
    func_73729_b(posX + 174, (int)(posY + 37.0F * this.currentScroll + 8.0F), needsScrollBars() ? 0 : 12, 241, 12, 15);
  }
  
  public void func_146274_d()
  {
    super.func_146274_d();
    int i = Mouse.getEventDWheel();
    if ((i != 0) && (needsScrollBars()))
    {
      int j = itemList.size() / 9 - 5 + 1;
      if (i > 0) {
        i = 1;
      }
      if (i < 0) {
        i = -1;
      }
      this.currentScroll = ((float)(this.currentScroll - i / j));
      if (this.currentScroll < 0.0F) {
        this.currentScroll = 0.0F;
      }
      if (this.currentScroll > 1.0F) {
        this.currentScroll = 1.0F;
      }
    }
  }
  
  protected void func_146285_a(ItemStack item, int x, int y)
  {
    if (item != null)
    {
      VillagerRecipe recipe = villager.getRecipe(item);
      if (recipe != null)
      {
        List list = recipe.getTooltip();
        func_146283_a(list, x, y);
      }
      else
      {
        super.func_146285_a(item, x, y);
      }
    }
    else
    {
      super.func_146285_a(item, x, y);
    }
  }
  
  protected void func_146284_a(GuiButton button)
  {
    switch (button.field_146127_k)
    {
    case 0: 
      ItemStack item = selectedItemInv.func_70301_a(0);
      item.field_77994_a += lowestStackSize;
      if (item.field_77994_a > item.func_77976_d()) {
        item.field_77994_a = item.func_77976_d();
      }
      selectedItemInv.func_70299_a(0, item);
      break;
    case 1: 
      ItemStack item = selectedItemInv.func_70301_a(0);
      item.field_77994_a -= lowestStackSize;
      if (item.field_77994_a < lowestStackSize) {
        item.field_77994_a = lowestStackSize;
      }
      selectedItemInv.func_70299_a(0, item);
      break;
    case 3: 
      ItemStack item = selectedItemInv.func_70301_a(0).func_77946_l();
      CraftItem craftItem = new CraftItem(item, this.player);
      if (villager.currentCraftItem == null)
      {
        villager.currentCraftItem = craftItem;
        HelpfulVillagers.network.sendToServer(new CraftItemServerPacket(villager.func_145782_y(), craftItem, true));
      }
      else
      {
        HelpfulVillagers.network.sendToServer(new CraftItemServerPacket(villager.func_145782_y(), craftItem, false));
      }
      selectedItemInv.func_70299_a(0, null);
      break;
    case 4: 
      this.field_146297_k.field_71439_g.func_71053_j();
      if (villager.field_70170_p.field_72995_K)
      {
        villager.guiCommand = 7;
        HelpfulVillagers.network.sendToServer(new GUICommandPacket(villager.func_145782_y(), 7));
      }
      break;
    case 5: 
      this.player.openGui(HelpfulVillagers.instance, 5, villager.field_70170_p, villager.func_145782_y(), 0, 0);
    }
  }
  
  public void func_146281_b()
  {
    super.func_146281_b();
    selectedItemInv.func_70299_a(0, null);
  }
  
  private static class CraftingContainer
    extends Container
  {
    public CraftingContainer()
    {
      func_75146_a(new Slot(GuiCraftingMenu.selectedItemInv, 0, 49, 99));
      
      func_75146_a(new Slot(GuiCraftingMenu.currentCraftInv, 0, 107, 99));
      
      int slotIndex = 0;
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 9; j++)
        {
          func_75146_a(new Slot(GuiCraftingMenu.recipesInv, slotIndex, j * 18 - 2, i * 18 + 23));
          slotIndex++;
        }
      }
    }
    
    public static void scrollTo(float amount)
    {
      int index = (int)(GuiCraftingMenu.itemList.size() * amount);
      if (index > GuiCraftingMenu.itemList.size() - 27) {
        index = GuiCraftingMenu.itemList.size() - 27;
      }
      if (index < 0) {
        index = 0;
      }
      if (GuiCraftingMenu.villager.currentCraftItem != null) {
        GuiCraftingMenu.currentCraftInv.func_70299_a(0, GuiCraftingMenu.villager.currentCraftItem.getItem());
      } else {
        GuiCraftingMenu.currentCraftInv.func_70299_a(0, null);
      }
      int slotIndex = 0;
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 9; j++)
        {
          if (index >= GuiCraftingMenu.itemList.size()) {
            GuiCraftingMenu.recipesInv.func_70299_a(slotIndex, null);
          } else {
            GuiCraftingMenu.recipesInv.func_70299_a(slotIndex, (ItemStack)GuiCraftingMenu.itemList.get(index));
          }
          slotIndex++;
          index++;
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
        if ((slot == null) || (!slot.func_75216_d())) {
          return null;
        }
        if (slot.field_75224_c.func_145825_b().equals("Recipes"))
        {
          GuiCraftingMenu.access$502(slot.func_75211_c().field_77994_a);
          GuiCraftingMenu.selectedItemInv.func_70299_a(0, slot.func_75211_c().func_77946_l());
        }
      }
      catch (ArrayIndexOutOfBoundsException e) {}
      return null;
    }
    
    public ItemStack func_82846_b(EntityPlayer p_82846_1_, int p_82846_2_)
    {
      return null;
    }
  }
  
  private static class AmountButton
    extends GuiButton
  {
    private final int MAX_PRESS = 10;
    private boolean up;
    private boolean beingPressed;
    private int pressCount;
    
    public AmountButton(int id, int x, int y, boolean flip)
    {
      super(x, y, 19, 12, "");
      this.up = flip;
      this.beingPressed = false;
      this.pressCount = 0;
    }
    
    public void func_146112_a(Minecraft mc, int x, int y)
    {
      if (this.field_146125_m)
      {
        mc.func_110434_K().func_110577_a(new ResourceLocation("helpfulvillagers", "textures/gui/craft_menu.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        boolean flag = (x >= this.field_146128_h) && (y >= this.field_146129_i) && (x < this.field_146128_h + this.field_146120_f) && (y < this.field_146129_i + this.field_146121_g);
        int u = 0;
        int v = 206;
        if (!this.up) {
          u += this.field_146120_f;
        }
        if (!this.field_146124_l)
        {
          v += this.field_146121_g * 2;
          
          this.beingPressed = false;
          this.pressCount = 0;
        }
        else if (flag)
        {
          v += this.field_146121_g;
        }
        else
        {
          this.beingPressed = false;
          this.pressCount = 0;
        }
        func_73729_b(this.field_146128_h, this.field_146129_i, u, v, this.field_146120_f, this.field_146121_g);
        if (this.beingPressed) {
          if (this.pressCount >= 10)
          {
            System.out.println("TEST");
            GuiCraftingMenu.access$602(this);
            this.pressCount = 0;
          }
          else
          {
            this.pressCount += 1;
          }
        }
      }
    }
    
    public void func_146118_a(int p_146118_1_, int p_146118_2_)
    {
      this.beingPressed = false;
      this.pressCount = 0;
      super.func_146118_a(p_146118_1_, p_146118_2_);
    }
    
    public boolean func_146116_c(Minecraft p_146116_1_, int p_146116_2_, int p_146116_3_)
    {
      if (this.field_146124_l) {
        this.beingPressed = true;
      }
      return super.func_146116_c(p_146116_1_, p_146116_2_, p_146116_3_);
    }
  }
  
  private static class TeachButton
    extends GuiButton
  {
    public TeachButton(int id, int x, int y)
    {
      super(x, y, 14, 16, "");
    }
    
    public void func_146112_a(Minecraft mc, int x, int y)
    {
      mc.func_110434_K().func_110577_a(new ResourceLocation("helpfulvillagers", "textures/gui/craft_menu.png"));
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      boolean flag = (x >= this.field_146128_h) && (y >= this.field_146129_i) && (x < this.field_146128_h + this.field_146120_f) && (y < this.field_146129_i + this.field_146121_g);
      int v;
      int u;
      int v;
      if (flag)
      {
        this.field_146120_f = 16;
        this.field_146121_g = 18;
        int u = 0;
        v = 188;
      }
      else
      {
        this.field_146120_f = 14;
        this.field_146121_g = 16;
        u = 1;
        v = 189;
      }
      func_73729_b(this.field_146128_h, this.field_146129_i, u, v, this.field_146120_f, this.field_146121_g);
    }
  }
  
  private static class StatsButton
    extends GuiButton
  {
    public StatsButton(int id, int x, int y)
    {
      super(x, y, 16, 15, "");
    }
    
    public void func_146112_a(Minecraft mc, int x, int y)
    {
      mc.func_110434_K().func_110577_a(new ResourceLocation("helpfulvillagers", "textures/gui/craft_menu.png"));
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      boolean flag = (x >= this.field_146128_h) && (y >= this.field_146129_i) && (x < this.field_146128_h + this.field_146120_f) && (y < this.field_146129_i + this.field_146121_g);
      int v;
      int u;
      int v;
      if (flag)
      {
        this.field_146120_f = 18;
        this.field_146121_g = 17;
        int u = 17;
        v = 189;
      }
      else
      {
        this.field_146120_f = 16;
        this.field_146121_g = 15;
        u = 18;
        v = 190;
      }
      func_73729_b(this.field_146128_h, this.field_146129_i, u, v, this.field_146120_f, this.field_146121_g);
    }
  }
}

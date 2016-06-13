package mods.helpfulvillagers.gui;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import mods.helpfulvillagers.econ.ItemPrice;
import mods.helpfulvillagers.econ.VillageEconomy;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.network.PlayerAccountServerPacket;
import mods.helpfulvillagers.network.PlayerCraftMatrixResetPacket;
import mods.helpfulvillagers.network.PlayerInventoryPacket;
import mods.helpfulvillagers.network.PlayerItemStackPacket;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiBarter
  extends GuiContainer
{
  private static final ResourceLocation field_147061_u = new ResourceLocation("helpfulvillagers", "textures/gui/barter_inventory/tabs.png");
  private static InventoryBasic barterItems = new InventoryBasic("Barter", true, 45);
  private static InventoryBasic buyItemInv = new InventoryBasic("Buy Item", true, 1);
  private static InventoryBasic currencyInputInv = new InventoryBasic("Currency Input", true, 1);
  private static InventoryBasic sellItemInv = new InventoryBasic("Sell Item", true, 1);
  private static InventoryBasic currencyOutputInv = new InventoryBasic("Currency Output", true, 1);
  private Slot sellItem;
  private Slot currencyOutput;
  private static int selectedTabIndex = CreativeTabs.field_78030_b.func_78021_a();
  private float currentScroll;
  private boolean isScrolling;
  private boolean wasClicking;
  private GuiTextField searchField;
  private List field_147063_B;
  private boolean field_147057_D;
  private static final String __OBFID = "CL_00000752";
  private static int tabPage = 0;
  private int maxPages = 0;
  private EntityPlayer player;
  private AbstractVillager villager;
  private GuiButton creditBuyButton;
  private GuiButton creditSellButton;
  private GuiButton creditWithdrawButton;
  private ItemStack dragItem;
  
  public GuiBarter(EntityPlayer player, AbstractVillager villager)
  {
    super(new ContainerBarter(player, villager));
    
    this.field_146291_p = true;
    this.field_147000_g = 136;
    this.field_146999_f = 195;
    
    this.player = player;
    this.villager = villager;
  }
  
  public void func_73866_w_()
  {
    super.func_73866_w_();
    this.field_146292_n.clear();
    Keyboard.enableRepeatEvents(true);
    this.searchField = new GuiTextField(this.field_146289_q, this.field_147003_i + 82, this.field_147009_r + 6, 89, this.field_146289_q.field_78288_b);
    this.searchField.func_146203_f(15);
    this.searchField.func_146185_a(false);
    this.searchField.func_146189_e(false);
    this.searchField.func_146193_g(16777215);
    int i = selectedTabIndex;
    selectedTabIndex = -1;
    setCurrentCreativeTab(CreativeTabs.field_78032_a[i]);
    int tabCount = CreativeTabs.field_78032_a.length;
    if (tabCount > 12)
    {
      this.field_146292_n.add(new GuiButton(101, this.field_147003_i, this.field_147009_r - 50, 20, 20, "<"));
      this.field_146292_n.add(new GuiButton(102, this.field_147003_i + this.field_146999_f - 20, this.field_147009_r - 50, 20, 20, ">"));
      this.maxPages = ((tabCount - 12) / 10 + 1);
    }
    this.creditBuyButton = new GuiButton(10, this.field_147003_i + 10, this.field_147009_r + 110, 40, 20, "Buy");
    this.field_146292_n.add(this.creditBuyButton);
    this.creditBuyButton.field_146124_l = false;
    this.creditBuyButton.field_146125_m = (selectedTabIndex != CreativeTabs.field_78036_m.func_78021_a());
    
    this.creditSellButton = new GuiButton(11, this.field_147003_i + 85, this.field_147009_r + 32, 42, 20, "Sell");
    this.field_146292_n.add(this.creditSellButton);
    this.creditSellButton.field_146124_l = false;
    this.creditSellButton.field_146125_m = (selectedTabIndex == CreativeTabs.field_78036_m.func_78021_a());
    
    this.creditWithdrawButton = new GuiButton(12, this.field_147003_i + 135, this.field_147009_r + 32, 50, 20, "Withdraw");
    this.field_146292_n.add(this.creditWithdrawButton);
    this.creditWithdrawButton.field_146124_l = false;
    this.creditWithdrawButton.field_146125_m = (selectedTabIndex == CreativeTabs.field_78036_m.func_78021_a());
  }
  
  public void func_73876_c()
  {
    HelpfulVillagers.network.sendToServer(new PlayerCraftMatrixResetPacket(this.player.func_145782_y()));
    HelpfulVillagers.network.sendToServer(new PlayerInventoryPacket(this.player.func_145782_y(), this.player.field_71071_by.field_70462_a, this.player.field_71071_by.field_70460_b));
    
    this.creditBuyButton.field_146125_m = (selectedTabIndex != CreativeTabs.field_78036_m.func_78021_a());
    this.creditSellButton.field_146125_m = (selectedTabIndex == CreativeTabs.field_78036_m.func_78021_a());
    this.creditWithdrawButton.field_146125_m = (selectedTabIndex == CreativeTabs.field_78036_m.func_78021_a());
    
    this.dragItem = this.player.field_71071_by.func_70445_o();
  }
  
  protected void func_146984_a(Slot slot, int x, int y, int z)
  {
    boolean flag = z == 1;
    z = (x == 64537) && (z == 0) ? 4 : z;
    
    InventoryPlayer inventoryplayer = this.player.field_71071_by;
    if (slot == null)
    {
      super.func_146984_a(slot, x, y, z);
      return;
    }
    System.out.println(slot.field_75224_c);
    if (this.creditSellButton.field_146125_m)
    {
      if (slot == this.sellItem)
      {
        if (slot.func_75216_d())
        {
          if (inventoryplayer.func_70445_o() == null)
          {
            ItemStack temp = slot.func_75211_c().func_77946_l();
            inventoryplayer.func_70437_b(temp);
            HelpfulVillagers.network.sendToServer(new PlayerItemStackPacket(this.player.func_145782_y(), temp));
            slot.func_75215_d(null);
          }
        }
        else if (inventoryplayer.func_70445_o() != null)
        {
          ItemStack temp = inventoryplayer.func_70445_o();
          inventoryplayer.func_70437_b(null);
          slot.func_75215_d(temp);
          HelpfulVillagers.network.sendToServer(new PlayerItemStackPacket(this.player.func_145782_y(), null));
        }
        calculateCurrencyOutput();
      }
      else if (slot == this.currencyOutput)
      {
        if ((slot.func_75216_d()) && 
          (inventoryplayer.func_70445_o() == null))
        {
          int amount = slot.func_75211_c().field_77994_a;
          inventoryplayer.func_70437_b(slot.func_75211_c());
          slot.func_75215_d(null);
          HelpfulVillagers.network.sendToServer(new PlayerItemStackPacket(this.player.func_145782_y(), slot.func_75211_c()));
          HelpfulVillagers.network.sendToServer(new PlayerCraftMatrixResetPacket(this.player.func_145782_y()));
          
          ItemStack item = sellItemInv.func_70301_a(0);
          if (item != null)
          {
            this.villager.homeVillage.economy.getItemPrice(item.func_82833_r()).increaseSupply(item.field_77994_a);
            this.villager.homeVillage.economy.itemSyncServer(this.villager, item);
            sellItemInv.func_70299_a(0, null);
          }
          else
          {
            this.villager.homeVillage.economy.accountWithdraw(this.player, amount);
          }
        }
      }
      else
      {
        super.func_146984_a(slot, x, y, z);
      }
    }
    else {
      this.field_147002_h.func_75144_a(slot.field_75222_d, x, y, this.player);
    }
  }
  
  private void calculateCurrencyOutput()
  {
    ItemStack item = sellItemInv.func_70301_a(0);
    if (item == null)
    {
      currencyOutputInv.func_70299_a(0, null);
    }
    else
    {
      int price = this.villager.homeVillage.economy.getPrice(item.func_82833_r()) * item.field_77994_a;
      if (price > 0) {
        if (price <= 64) {
          currencyOutputInv.func_70299_a(0, new ItemStack(Items.field_151166_bC, price));
        }
      }
    }
  }
  
  public void func_146281_b()
  {
    HelpfulVillagers.network.sendToServer(new PlayerInventoryPacket(this.player.func_145782_y(), this.player.field_71071_by.field_70462_a, this.player.field_71071_by.field_70460_b));
    this.player.field_71069_bz = new ContainerPlayer(this.player.field_71071_by, true, this.player);
    
    buyItemInv.func_70299_a(0, null);
    
    ItemStack stack = currencyInputInv.func_70301_a(0);
    if (stack != null)
    {
      this.player.func_71019_a(stack, true);
      this.field_146297_k.field_71442_b.func_78752_a(stack);
      currencyInputInv.func_70299_a(0, null);
    }
    stack = sellItemInv.func_70301_a(0);
    if (stack != null)
    {
      this.player.func_71019_a(stack, true);
      this.field_146297_k.field_71442_b.func_78752_a(stack);
      sellItemInv.func_70299_a(0, null);
    }
    stack = currencyOutputInv.func_70301_a(0);
    if (stack != null) {
      currencyOutputInv.func_70299_a(0, null);
    }
    if (this.dragItem != null)
    {
      this.player.func_71019_a(this.dragItem, true);
      this.field_146297_k.field_71442_b.func_78752_a(this.dragItem);
      this.player.field_71071_by.func_70437_b(null);
      this.dragItem = null;
    }
    Keyboard.enableRepeatEvents(false);
  }
  
  protected void func_73869_a(char p_73869_1_, int p_73869_2_)
  {
    if (!CreativeTabs.field_78032_a[selectedTabIndex].hasSearchBar())
    {
      if (GameSettings.func_100015_a(this.field_146297_k.field_71474_y.field_74310_D)) {
        setCurrentCreativeTab(CreativeTabs.field_78027_g);
      } else {
        super.func_73869_a(p_73869_1_, p_73869_2_);
      }
    }
    else
    {
      if (this.field_147057_D)
      {
        this.field_147057_D = false;
        this.searchField.func_146180_a("");
      }
      if (!func_146983_a(p_73869_2_)) {
        if (this.searchField.func_146201_a(p_73869_1_, p_73869_2_)) {
          updateCreativeSearch();
        } else {
          super.func_73869_a(p_73869_1_, p_73869_2_);
        }
      }
    }
  }
  
  private void updateCreativeSearch()
  {
    ContainerBarter containercreative = (ContainerBarter)this.field_147002_h;
    containercreative.itemList.clear();
    
    CreativeTabs tab = CreativeTabs.field_78032_a[selectedTabIndex];
    if ((tab.hasSearchBar()) && (tab != CreativeTabs.field_78027_g))
    {
      tab.func_78018_a(containercreative.itemList);
      updateFilteredItems(containercreative);
      return;
    }
    Iterator iterator = Item.field_150901_e.iterator();
    while (iterator.hasNext())
    {
      Item item = (Item)iterator.next();
      if ((item != null) && (item.func_77640_w() != null)) {
        item.func_150895_a(item, (CreativeTabs)null, containercreative.itemList);
      }
    }
    updateFilteredItems(containercreative);
  }
  
  private void updateFilteredItems(ContainerBarter containercreative)
  {
    Enchantment[] aenchantment = Enchantment.field_77331_b;
    int j = aenchantment.length;
    for (int i = 0; i < j; i++)
    {
      Enchantment enchantment = aenchantment[i];
      if ((enchantment != null) && (enchantment.field_77351_y != null)) {
        Items.field_151134_bR.func_92113_a(enchantment, containercreative.itemList);
      }
    }
    Iterator iterator = containercreative.itemList.iterator();
    String s1 = this.searchField.func_146179_b().toLowerCase();
    while (iterator.hasNext())
    {
      ItemStack itemstack = (ItemStack)iterator.next();
      boolean flag = false;
      Iterator iterator1 = itemstack.func_82840_a(this.field_146297_k.field_71439_g, this.field_146297_k.field_71474_y.field_82882_x).iterator();
      while (iterator1.hasNext())
      {
        String s = (String)iterator1.next();
        if (s.toLowerCase().contains(s1)) {
          flag = true;
        }
      }
      if (!flag) {
        iterator.remove();
      }
    }
    this.currentScroll = 0.0F;
    containercreative.scrollTo(0.0F);
  }
  
  protected void func_146979_b(int p_146979_1_, int p_146979_2_)
  {
    CreativeTabs creativetabs = CreativeTabs.field_78032_a[selectedTabIndex];
    if ((creativetabs != null) && (creativetabs.func_78019_g()))
    {
      GL11.glDisable(3042);
      this.field_146289_q.func_78276_b(I18n.func_135052_a(creativetabs.func_78024_c(), new Object[0]), 8, 6, 4210752);
    }
  }
  
  protected void func_73864_a(int p_73864_1_, int p_73864_2_, int p_73864_3_)
  {
    if (p_73864_3_ == 0)
    {
      int l = p_73864_1_ - this.field_147003_i;
      int i1 = p_73864_2_ - this.field_147009_r;
      CreativeTabs[] acreativetabs = CreativeTabs.field_78032_a;
      int j1 = acreativetabs.length;
      for (int k1 = 0; k1 < j1; k1++)
      {
        CreativeTabs creativetabs = acreativetabs[k1];
        if ((creativetabs != null) && (func_147049_a(creativetabs, l, i1))) {
          return;
        }
      }
    }
    super.func_73864_a(p_73864_1_, p_73864_2_, p_73864_3_);
  }
  
  protected void func_146286_b(int p_146286_1_, int p_146286_2_, int p_146286_3_)
  {
    if (p_146286_3_ == 0)
    {
      int l = p_146286_1_ - this.field_147003_i;
      int i1 = p_146286_2_ - this.field_147009_r;
      CreativeTabs[] acreativetabs = CreativeTabs.field_78032_a;
      int j1 = acreativetabs.length;
      for (int k1 = 0; k1 < j1; k1++)
      {
        CreativeTabs creativetabs = acreativetabs[k1];
        if ((creativetabs != null) && (func_147049_a(creativetabs, l, i1)))
        {
          setCurrentCreativeTab(creativetabs);
          return;
        }
      }
    }
    super.func_146286_b(p_146286_1_, p_146286_2_, p_146286_3_);
  }
  
  private boolean needsScrollBars()
  {
    if (CreativeTabs.field_78032_a[selectedTabIndex] == null) {
      return false;
    }
    return (selectedTabIndex != CreativeTabs.field_78036_m.func_78021_a()) && (CreativeTabs.field_78032_a[selectedTabIndex].func_78017_i()) && (((ContainerBarter)this.field_147002_h).func_148328_e());
  }
  
  private void setCurrentCreativeTab(CreativeTabs p_147050_1_)
  {
    if (p_147050_1_ == null) {
      return;
    }
    int i = selectedTabIndex;
    selectedTabIndex = p_147050_1_.func_78021_a();
    ContainerBarter containerbarter = (ContainerBarter)this.field_147002_h;
    this.field_147008_s.clear();
    containerbarter.itemList.clear();
    p_147050_1_.func_78018_a(containerbarter.itemList);
    if (p_147050_1_ == CreativeTabs.field_78036_m)
    {
      Container container = this.field_146297_k.field_71439_g.field_71069_bz;
      if (this.field_147063_B == null) {
        this.field_147063_B = containerbarter.field_75151_b;
      }
      containerbarter.field_75151_b = new ArrayList();
      for (int j = 0; j < container.field_75151_b.size(); j++)
      {
        Slot creativeslot = (Slot)container.field_75151_b.get(j);
        containerbarter.field_75151_b.add(creativeslot);
        if ((j >= 5) && (j < 9))
        {
          int k = j - 5;
          int l = k / 2;
          int i1 = k % 2;
          creativeslot.field_75223_e = (9 + l * 54);
          creativeslot.field_75221_f = (6 + i1 * 27);
        }
        else if ((j >= 0) && (j < 5))
        {
          creativeslot.field_75221_f = 63536;
          creativeslot.field_75223_e = 63536;
        }
        else if (j < container.field_75151_b.size())
        {
          int k = j - 9;
          int l = k % 9;
          int i1 = k / 9;
          creativeslot.field_75223_e = (9 + l * 18);
          if (j >= 36) {
            creativeslot.field_75221_f = 112;
          } else {
            creativeslot.field_75221_f = (54 + i1 * 18);
          }
        }
      }
      this.sellItem = new Slot(sellItemInv, 0, 99, 12);
      containerbarter.field_75151_b.add(this.sellItem);
      
      this.currencyOutput = new Slot(currencyOutputInv, 0, 153, 12);
      containerbarter.field_75151_b.add(this.currencyOutput);
    }
    else if (i == CreativeTabs.field_78036_m.func_78021_a())
    {
      containerbarter.field_75151_b = this.field_147063_B;
      this.field_147063_B = null;
    }
    if (this.searchField != null) {
      if (p_147050_1_.hasSearchBar())
      {
        this.searchField.func_146189_e(true);
        this.searchField.func_146205_d(false);
        this.searchField.func_146195_b(true);
        this.searchField.func_146180_a("");
        updateCreativeSearch();
      }
      else
      {
        this.searchField.func_146189_e(false);
        this.searchField.func_146205_d(true);
        this.searchField.func_146195_b(false);
      }
    }
    this.currentScroll = 0.0F;
    containerbarter.scrollTo(0.0F);
  }
  
  public void func_146274_d()
  {
    super.func_146274_d();
    int i = Mouse.getEventDWheel();
    if ((i != 0) && (needsScrollBars()))
    {
      int j = ((ContainerBarter)this.field_147002_h).itemList.size() / 9 - 5 + 1;
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
      ((ContainerBarter)this.field_147002_h).scrollTo(this.currentScroll);
    }
  }
  
  public void func_73863_a(int x, int y, float f)
  {
    if ((this.villager.homeVillage.economy == null) || (!this.villager.homeVillage.pricesCalculated) || (this.villager.homeVillage.economy.getItemPrices().isEmpty()))
    {
      int xSizeOfTexture = 140;
      int ySizeOfTexture = 40;
      
      func_146276_q_();
      
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.field_71446_o.func_110577_a(new ResourceLocation("helpfulvillagers", "textures/gui/dialog_background.png"));
      
      int posX = (this.field_146294_l - xSizeOfTexture) / 2;
      int posY = (this.field_146295_m - ySizeOfTexture) / 2;
      
      func_73731_b(this.field_146289_q, "Calculating Village Prices...", posX + 5, posY + 15, 16777215);
    }
    else
    {
      boolean flag = Mouse.isButtonDown(0);
      int k = this.field_147003_i;
      int l = this.field_147009_r;
      int i1 = k + 175;
      int j1 = l + 18;
      int k1 = i1 + 14;
      int l1 = j1 + 112;
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
        ((ContainerBarter)this.field_147002_h).scrollTo(this.currentScroll);
      }
      super.func_73863_a(x, y, f);
      CreativeTabs[] acreativetabs = CreativeTabs.field_78032_a;
      int start = tabPage * 10;
      int i2 = Math.min(acreativetabs.length, (tabPage + 1) * 10 + 2);
      if (tabPage != 0) {
        start += 2;
      }
      boolean rendered = false;
      for (int j2 = start; j2 < i2; j2++)
      {
        CreativeTabs creativetabs = acreativetabs[j2];
        if ((creativetabs != null) && 
          (renderCreativeInventoryHoveringText(creativetabs, x, y)))
        {
          rendered = true;
          break;
        }
      }
      if ((!rendered) && (renderCreativeInventoryHoveringText(CreativeTabs.field_78027_g, x, y))) {
        renderCreativeInventoryHoveringText(CreativeTabs.field_78036_m, x, y);
      }
      if (this.maxPages != 0)
      {
        String page = String.format("%d / %d", new Object[] { Integer.valueOf(tabPage + 1), Integer.valueOf(this.maxPages + 1) });
        int width = this.field_146289_q.func_78256_a(page);
        GL11.glDisable(2896);
        this.field_73735_i = 300.0F;
        field_146296_j.field_77023_b = 300.0F;
        this.field_146289_q.func_78276_b(page, this.field_147003_i + this.field_146999_f / 2 - width / 2, this.field_147009_r - 44, -1);
        this.field_73735_i = 0.0F;
        field_146296_j.field_77023_b = 0.0F;
      }
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(2896);
      
      func_73731_b(this.field_146289_q, "Credits: $" + this.villager.homeVillage.economy.getAccount(this.player), this.field_147003_i, this.field_147009_r + 165, 16777215);
      if (this.creditBuyButton.field_146125_m)
      {
        int price = -1;
        if (buyItemInv.func_70301_a(0) != null)
        {
          ItemStack item = buyItemInv.func_70301_a(0);
          price = this.villager.homeVillage.economy.getPrice(item.func_82833_r()) * item.field_77994_a;
          
          func_73731_b(this.field_146289_q, "$" + price, this.field_147003_i + 145, this.field_147009_r + 115, 16777215);
          if (price >= 0)
          {
            if (this.villager.homeVillage.economy.getAccount(this.player) >= price)
            {
              this.creditBuyButton.field_146124_l = true;
            }
            else
            {
              this.creditBuyButton.field_146124_l = false;
              if (this.creditBuyButton.func_146115_a())
              {
                String[] desc = { "Insufficient Credits" };
                
                List temp = Arrays.asList(desc);
                drawHoveringText(temp, x, y, this.field_146289_q);
              }
            }
          }
          else
          {
            this.creditBuyButton.field_146124_l = false;
            if (this.creditBuyButton.func_146115_a())
            {
              String[] desc = { "ERROR: Invalid Item Price" };
              
              List temp = Arrays.asList(desc);
              drawHoveringText(temp, x, y, this.field_146289_q);
            }
          }
        }
        else
        {
          this.creditBuyButton.field_146124_l = false;
          if (this.creditBuyButton.func_146115_a())
          {
            String[] desc = { "No Item Selected" };
            
            List temp = Arrays.asList(desc);
            drawHoveringText(temp, x, y, this.field_146289_q);
          }
        }
      }
      else
      {
        this.creditBuyButton.field_146124_l = false;
      }
      this.creditSellButton.field_146126_j = "Sell";
      if (this.creditSellButton.field_146125_m)
      {
        if (sellItemInv.func_70301_a(0) != null)
        {
          if (sellItemInv.func_70301_a(0).func_77973_b().equals(Items.field_151166_bC))
          {
            func_73731_b(this.field_146289_q, "$" + sellItemInv.func_70301_a(0).field_77994_a, this.field_147003_i + 175, this.field_147009_r + 15, 16777215);
            this.creditSellButton.field_146124_l = true;
            this.creditSellButton.field_146126_j = "Deposit";
          }
          else if (this.villager.homeVillage.economy.getPrice(sellItemInv.func_70301_a(0).func_82833_r()) > 0)
          {
            int price = this.villager.homeVillage.economy.getPrice(sellItemInv.func_70301_a(0).func_82833_r()) * sellItemInv.func_70301_a(0).field_77994_a;
            func_73731_b(this.field_146289_q, "$" + price, this.field_147003_i + 175, this.field_147009_r + 15, 16777215);
            this.creditSellButton.field_146124_l = true;
          }
          else
          {
            this.creditSellButton.field_146124_l = false;
            if (this.creditSellButton.func_146115_a())
            {
              String[] desc = { "Item Has No Price" };
              
              List temp = Arrays.asList(desc);
              drawHoveringText(temp, x, y, this.field_146289_q);
            }
          }
        }
        else
        {
          this.creditSellButton.field_146124_l = false;
          if (this.creditSellButton.func_146115_a())
          {
            String[] desc = { "No Item Selected" };
            
            List temp = Arrays.asList(desc);
            drawHoveringText(temp, x, y, this.field_146289_q);
          }
        }
        if (this.villager.homeVillage.economy.getAccount(this.player) > 0)
        {
          if (!this.creditSellButton.field_146124_l)
          {
            this.creditWithdrawButton.field_146124_l = true;
          }
          else
          {
            this.creditWithdrawButton.field_146124_l = false;
            if (this.creditWithdrawButton.func_146115_a())
            {
              String[] desc = { "Sell Item Selected" };
              
              List temp = Arrays.asList(desc);
              drawHoveringText(temp, x, y, this.field_146289_q);
            }
          }
        }
        else
        {
          this.creditWithdrawButton.field_146124_l = false;
          if (this.creditWithdrawButton.func_146115_a())
          {
            String[] desc = { "Insufficient Credits" };
            
            List temp = Arrays.asList(desc);
            drawHoveringText(temp, x, y, this.field_146289_q);
          }
        }
      }
      else
      {
        this.creditSellButton.field_146124_l = false;
      }
    }
  }
  
  protected void func_146285_a(ItemStack inputItem, int x, int y)
  {
    if ((this.villager.homeVillage != null) && (this.villager.homeVillage.economy != null) && (!this.villager.homeVillage.economy.getItemPrices().isEmpty()))
    {
      int price = this.villager.homeVillage.economy.getPrice(inputItem.func_82833_r());
      if (price >= 0)
      {
        List list = new ArrayList();
        list.add("$" + price + " - " + inputItem.func_82833_r());
        func_146283_a(list, x, y);
      }
      else
      {
        super.func_146285_a(inputItem, x, y);
      }
    }
    else
    {
      super.func_146285_a(inputItem, x, y);
    }
  }
  
  protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_)
  {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderHelper.func_74520_c();
    CreativeTabs creativetabs = CreativeTabs.field_78032_a[selectedTabIndex];
    CreativeTabs[] acreativetabs = CreativeTabs.field_78032_a;
    int k = acreativetabs.length;
    
    int start = tabPage * 10;
    k = Math.min(acreativetabs.length, (tabPage + 1) * 10 + 2);
    if (tabPage != 0) {
      start += 2;
    }
    for (int l = start; l < k; l++)
    {
      CreativeTabs creativetabs1 = acreativetabs[l];
      this.field_146297_k.func_110434_K().func_110577_a(field_147061_u);
      if (creativetabs1 != null) {
        if (creativetabs1.func_78021_a() != selectedTabIndex) {
          func_147051_a(creativetabs1);
        }
      }
    }
    if (tabPage != 0)
    {
      if (creativetabs != CreativeTabs.field_78027_g)
      {
        this.field_146297_k.func_110434_K().func_110577_a(field_147061_u);
        func_147051_a(CreativeTabs.field_78027_g);
      }
      if (creativetabs != CreativeTabs.field_78036_m)
      {
        this.field_146297_k.func_110434_K().func_110577_a(field_147061_u);
        func_147051_a(CreativeTabs.field_78036_m);
      }
    }
    this.field_146297_k.func_110434_K().func_110577_a(new ResourceLocation("helpfulvillagers", "textures/gui/barter_inventory/tab_" + creativetabs.func_78015_f()));
    func_73729_b(this.field_147003_i, this.field_147009_r, 0, 0, this.field_146999_f, this.field_147000_g);
    this.searchField.func_146194_f();
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    int i1 = this.field_147003_i + 175;
    k = this.field_147009_r + 18;
    l = k + 112;
    this.field_146297_k.func_110434_K().func_110577_a(field_147061_u);
    if (creativetabs.func_78017_i()) {
      func_73729_b(i1, k + (int)((l - k - 17) * this.currentScroll), '�' + (needsScrollBars() ? 0 : 12), 0, 12, 15);
    }
    if ((creativetabs == null) || (creativetabs.getTabPage() != tabPage)) {
      if ((creativetabs != CreativeTabs.field_78027_g) && (creativetabs != CreativeTabs.field_78036_m)) {
        return;
      }
    }
    func_147051_a(creativetabs);
    if (creativetabs == CreativeTabs.field_78036_m) {
      GuiInventory.func_147046_a(this.field_147003_i + 43, this.field_147009_r + 45, 20, this.field_147003_i + 43 - p_146976_2_, this.field_147009_r + 45 - 30 - p_146976_3_, this.field_146297_k.field_71439_g);
    }
  }
  
  protected boolean func_147049_a(CreativeTabs p_147049_1_, int p_147049_2_, int p_147049_3_)
  {
    if (p_147049_1_.getTabPage() != tabPage) {
      if ((p_147049_1_ != CreativeTabs.field_78027_g) && (p_147049_1_ != CreativeTabs.field_78036_m)) {
        return false;
      }
    }
    int k = p_147049_1_.func_78020_k();
    int l = 28 * k;
    byte b0 = 0;
    if (k == 5) {
      l = this.field_146999_f - 28 + 2;
    } else if (k > 0) {
      l += k;
    }
    int i1;
    int i1;
    if (p_147049_1_.func_78023_l()) {
      i1 = b0 - 32;
    } else {
      i1 = b0 + this.field_147000_g;
    }
    return (p_147049_2_ >= l) && (p_147049_2_ <= l + 28) && (p_147049_3_ >= i1) && (p_147049_3_ <= i1 + 32);
  }
  
  protected boolean renderCreativeInventoryHoveringText(CreativeTabs p_147052_1_, int p_147052_2_, int p_147052_3_)
  {
    int k = p_147052_1_.func_78020_k();
    int l = 28 * k;
    byte b0 = 0;
    if (k == 5) {
      l = this.field_146999_f - 28 + 2;
    } else if (k > 0) {
      l += k;
    }
    int i1;
    int i1;
    if (p_147052_1_.func_78023_l()) {
      i1 = b0 - 32;
    } else {
      i1 = b0 + this.field_147000_g;
    }
    if (func_146978_c(l + 3, i1 + 3, 23, 27, p_147052_2_, p_147052_3_))
    {
      func_146279_a(I18n.func_135052_a(p_147052_1_.func_78024_c(), new Object[0]), p_147052_2_, p_147052_3_);
      return true;
    }
    return false;
  }
  
  protected void func_147051_a(CreativeTabs p_147051_1_)
  {
    boolean flag = p_147051_1_.func_78021_a() == selectedTabIndex;
    boolean flag1 = p_147051_1_.func_78023_l();
    int i = p_147051_1_.func_78020_k();
    int j = i * 28;
    int k = 0;
    int l = this.field_147003_i + 28 * i;
    int i1 = this.field_147009_r;
    byte b0 = 32;
    if (flag) {
      k += 32;
    }
    if (i == 5) {
      l = this.field_147003_i + this.field_146999_f - 28;
    } else if (i > 0) {
      l += i;
    }
    if (flag1)
    {
      i1 -= 28;
    }
    else
    {
      k += 64;
      i1 += this.field_147000_g - 4;
    }
    GL11.glDisable(2896);
    GL11.glColor3f(1.0F, 1.0F, 1.0F);
    GL11.glEnable(3042);
    func_73729_b(l, i1, j, k, 28, b0);
    this.field_73735_i = 100.0F;
    field_146296_j.field_77023_b = 100.0F;
    l += 6;
    i1 += 8 + (flag1 ? 1 : -1);
    GL11.glEnable(2896);
    GL11.glEnable(32826);
    ItemStack itemstack = p_147051_1_.func_151244_d();
    field_146296_j.func_82406_b(this.field_146289_q, this.field_146297_k.func_110434_K(), itemstack, l, i1);
    field_146296_j.func_77021_b(this.field_146289_q, this.field_146297_k.func_110434_K(), itemstack, l, i1);
    GL11.glDisable(2896);
    field_146296_j.field_77023_b = 0.0F;
    this.field_73735_i = 0.0F;
  }
  
  protected void func_146284_a(GuiButton button)
  {
    if (button.field_146127_k == 0) {
      this.field_146297_k.func_147108_a(new GuiAchievements(this, this.field_146297_k.field_71439_g.func_146107_m()));
    }
    if (button.field_146127_k == 1) {
      this.field_146297_k.func_147108_a(new GuiStats(this, this.field_146297_k.field_71439_g.func_146107_m()));
    }
    if (button.field_146127_k == 101) {
      tabPage = Math.max(tabPage - 1, 0);
    } else if (button.field_146127_k == 102) {
      tabPage = Math.min(tabPage + 1, this.maxPages);
    }
    if (button.field_146127_k == 10)
    {
      InventoryPlayer inventoryplayer = this.player.field_71071_by;
      ItemStack buyItem = buyItemInv.func_70301_a(0);
      if (buyItem != null)
      {
        inventoryplayer.func_70437_b(buyItem);
        buyItemInv.func_70299_a(0, null);
        int amount = this.villager.homeVillage.economy.getPrice(buyItem.func_82833_r()) * buyItem.field_77994_a;
        this.villager.homeVillage.economy.accountWithdraw(this.player, amount);
        HelpfulVillagers.network.sendToServer(new PlayerAccountServerPacket(this.player, this.villager));
        this.villager.homeVillage.economy.getItemPrice(buyItem.func_82833_r()).increaseDemand(buyItem.field_77994_a);
        this.villager.homeVillage.economy.itemSyncServer(this.villager, buyItem);
      }
    }
    if (button.field_146127_k == 11)
    {
      ItemStack sellItem = sellItemInv.func_70301_a(0);
      if (sellItem != null) {
        if (this.creditSellButton.field_146126_j.equals("Deposit"))
        {
          int amount = sellItem.field_77994_a;
          this.villager.homeVillage.economy.accountDeposit(this.player, amount);
          sellItemInv.func_70299_a(0, null);
        }
        else if (this.creditSellButton.field_146126_j.equals("Sell"))
        {
          ItemStack currencyOutput = currencyOutputInv.func_70301_a(0);
          if (currencyOutput != null)
          {
            int amount = currencyOutput.field_77994_a;
            this.villager.homeVillage.economy.accountDeposit(this.player, amount);
            currencyOutputInv.func_70299_a(0, null);
            sellItemInv.func_70299_a(0, null);
          }
          else
          {
            int amount = this.villager.homeVillage.economy.getPrice(sellItem.func_82833_r()) * sellItem.field_77994_a;
            this.villager.homeVillage.economy.accountDeposit(this.player, amount);
            sellItemInv.func_70299_a(0, null);
          }
          this.villager.homeVillage.economy.getItemPrice(sellItem.func_82833_r()).increaseSupply(sellItem.field_77994_a);
          this.villager.homeVillage.economy.itemSyncServer(this.villager, sellItem);
        }
      }
      HelpfulVillagers.network.sendToServer(new PlayerAccountServerPacket(this.player, this.villager));
    }
    if (button.field_146127_k == 12) {
      if ((this.currencyOutput.func_75216_d()) && (this.currencyOutput.func_75211_c().field_77994_a < this.currencyOutput.func_75211_c().func_77976_d()) && (this.currencyOutput.func_75211_c().field_77994_a < this.villager.homeVillage.economy.getAccount(this.player))) {
        this.currencyOutput.func_75211_c().field_77994_a += 1;
      } else if (!this.currencyOutput.func_75216_d()) {
        this.currencyOutput.func_75215_d(new ItemStack(Items.field_151166_bC));
      }
    }
  }
  
  public int func_147056_g()
  {
    return selectedTabIndex;
  }
  
  @SideOnly(Side.CLIENT)
  static class ContainerBarter
    extends Container
  {
    public List itemList = new ArrayList();
    private static final String __OBFID = "CL_00000753";
    private AbstractVillager villager;
    private EntityPlayer player;
    
    public ContainerBarter(EntityPlayer player, AbstractVillager villager)
    {
      this.villager = villager;
      this.player = player;
      InventoryPlayer inventoryplayer = player.field_71071_by;
      
      func_75146_a(new Slot(GuiBarter.buyItemInv, 0, 117, 113));
      
      func_75146_a(new Slot(GuiBarter.currencyInputInv, 0, 63, 113));
      for (int i = 0; i < 5; i++) {
        for (int j = 0; j < 9; j++) {
          func_75146_a(new Slot(GuiBarter.barterItems, i * 9 + j, 9 + j * 18, 18 + i * 18));
        }
      }
      scrollTo(0.0F);
    }
    
    public boolean func_75145_c(EntityPlayer p_75145_1_)
    {
      return true;
    }
    
    public void scrollTo(float p_148329_1_)
    {
      Iterator iterator = this.itemList.iterator();
      while (iterator.hasNext())
      {
        ItemStack item = (ItemStack)iterator.next();
        if ((!this.villager.homeVillage.economy.hasItem(item)) || (this.villager.homeVillage.economy.getPrice(item.func_82833_r()) < 0)) {
          iterator.remove();
        }
      }
      int i = this.itemList.size() / 9 - 5 + 1;
      int j = (int)(p_148329_1_ * i + 0.5D);
      if (j < 0) {
        j = 0;
      }
      for (int k = 0; k < 5; k++) {
        for (int l = 0; l < 9; l++)
        {
          int i1 = l + (k + j) * 9;
          if ((i1 >= 0) && (i1 < this.itemList.size())) {
            GuiBarter.barterItems.func_70299_a(l + k * 9, (ItemStack)this.itemList.get(i1));
          } else {
            GuiBarter.barterItems.func_70299_a(l + k * 9, (ItemStack)null);
          }
        }
      }
    }
    
    public boolean func_148328_e()
    {
      return this.itemList.size() > 45;
    }
    
    public ItemStack func_75144_a(int p_75144_1_, int p_75144_2_, int p_75144_3_, EntityPlayer p_75144_4_)
    {
      InventoryPlayer inventoryplayer = p_75144_4_.field_71071_by;
      try
      {
        Slot slot = func_75139_a(p_75144_1_);
        System.out.println(slot.func_75211_c());
        if (slot.field_75224_c.func_145825_b().equals(GuiBarter.barterItems.func_145825_b()))
        {
          if ((GuiBarter.buyItemInv.func_70301_a(0) != null) && (GuiBarter.buyItemInv.func_70301_a(0).func_82833_r().equals(slot.func_75211_c().func_82833_r())))
          {
            ItemStack selectedStack = GuiBarter.buyItemInv.func_70301_a(0);
            if (selectedStack.field_77994_a < selectedStack.func_77976_d()) {
              func_75141_a(0, new ItemStack(selectedStack.func_77973_b(), selectedStack.field_77994_a + 1, selectedStack.func_77960_j()));
            }
          }
          else
          {
            func_75141_a(0, new ItemStack(slot.func_75211_c().func_77973_b(), 1, slot.func_75211_c().func_77960_j()));
          }
        }
        else
        {
          if (slot.field_75224_c.equals(p_75144_4_.field_71071_by)) {
            return super.func_75144_a(p_75144_1_, p_75144_2_, p_75144_3_, p_75144_4_);
          }
          if (slot.field_75224_c.equals(GuiBarter.currencyInputInv))
          {
            if (slot.func_75216_d())
            {
              if (inventoryplayer.func_70445_o() == null)
              {
                ItemStack temp = slot.func_75211_c().func_77946_l();
                inventoryplayer.func_70437_b(temp);
                HelpfulVillagers.network.sendToServer(new PlayerItemStackPacket(p_75144_4_.func_145782_y(), temp));
                slot.func_75215_d(null);
              }
            }
            else if (inventoryplayer.func_70445_o() != null)
            {
              ItemStack temp = inventoryplayer.func_70445_o().func_77946_l();
              slot.func_75215_d(temp);
              inventoryplayer.func_70437_b(null);
              HelpfulVillagers.network.sendToServer(new PlayerItemStackPacket(p_75144_4_.func_145782_y(), null));
            }
          }
          else if (slot.field_75224_c.equals(GuiBarter.buyItemInv))
          {
            int price = this.villager.homeVillage.economy.getPrice(GuiBarter.buyItemInv.func_70301_a(0).func_82833_r());
            price *= GuiBarter.buyItemInv.func_70301_a(0).field_77994_a;
            if ((GuiBarter.currencyInputInv.func_70301_a(0).func_77973_b().equals(Items.field_151166_bC)) && (GuiBarter.currencyInputInv.func_70301_a(0).field_77994_a >= price))
            {
              int size = GuiBarter.currencyInputInv.func_70301_a(0).field_77994_a - price;
              ItemStack currency = GuiBarter.currencyInputInv.func_70301_a(0).func_77946_l();
              if (size <= 0) {
                func_75141_a(1, null);
              } else {
                func_75141_a(1, new ItemStack(currency.func_77973_b(), size, currency.func_77960_j()));
              }
              this.villager.homeVillage.economy.getItemPrice(GuiBarter.buyItemInv.func_70301_a(0).func_82833_r()).increaseDemand(GuiBarter.buyItemInv.func_70301_a(0).field_77994_a);
              this.villager.homeVillage.economy.itemSyncServer(this.villager, GuiBarter.buyItemInv.func_70301_a(0));
              super.func_75144_a(p_75144_1_, p_75144_2_, p_75144_3_, p_75144_4_);
            }
          }
        }
      }
      catch (Exception e) {}
      return null;
    }
    
    public ItemStack func_82846_b(EntityPlayer p_82846_1_, int p_82846_2_)
    {
      return null;
    }
    
    public boolean func_94530_a(ItemStack p_94530_1_, Slot p_94530_2_)
    {
      return p_94530_2_.field_75221_f > 90;
    }
    
    public boolean func_94531_b(Slot p_94531_1_)
    {
      return false;
    }
    
    public void func_75134_a(EntityPlayer player) {}
  }
}

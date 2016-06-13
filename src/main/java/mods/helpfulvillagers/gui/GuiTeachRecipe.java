package mods.helpfulvillagers.gui;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import mods.helpfulvillagers.crafting.VillagerRecipe;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.network.AddRecipePacket;
import mods.helpfulvillagers.network.ResetRecipesPacket;
import mods.helpfulvillagers.util.AIHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class GuiTeachRecipe
  extends GuiContainer
{
  private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("textures/gui/container/crafting_table.png");
  private final int xSizeOfTexture = 176;
  private final int ySizeOfTexture = 166;
  private final int MAX_DISPLAY_TIME = 120;
  private GuiButton teachButton;
  private GuiButton resetButton;
  private GuiButton backButton;
  private GuiButton yesButton;
  private GuiButton noButton;
  private GuiButton replaceButton;
  private GuiButton deleteButton;
  private GuiButton cancelButton;
  private EntityPlayer player;
  private AbstractVillager villager;
  private String displayText;
  private int displayTime;
  private static int popupNum;
  private List tempSlots = new ArrayList();
  
  public GuiTeachRecipe(EntityPlayer player, AbstractVillager villager)
  {
    super(new VillagerContainerWorkbench(player));
    this.player = player;
    this.villager = villager;
    this.displayText = null;
    this.displayTime = 0;
    popupNum = -1;
  }
  
  public void func_73866_w_()
  {
    super.func_73866_w_();
    int posX = (this.field_146294_l - 176) / 2;
    int posY = (this.field_146295_m - 166) / 2;
    
    this.teachButton = new GuiButton(0, posX + 90, posY + 60, 80, 20, "Teach Recipe");
    this.field_146292_n.add(this.teachButton);
    this.teachButton.field_146124_l = false;
    
    this.resetButton = new GuiButton(1, posX + 90, posY + 5, 80, 20, "Reset Recipes");
    this.field_146292_n.add(this.resetButton);
    
    this.backButton = new GuiButton(2, posX + 6, posY + 33, 20, 20, "<-");
    this.field_146292_n.add(this.backButton);
    
    this.yesButton = new GuiButton(3, posX + 45, posY + 115, 40, 20, "Yes");
    this.field_146292_n.add(this.yesButton);
    this.yesButton.field_146125_m = false;
    this.yesButton.field_146124_l = false;
    
    this.noButton = new GuiButton(4, posX + 95, posY + 115, 40, 20, "No");
    this.field_146292_n.add(this.noButton);
    this.noButton.field_146125_m = false;
    this.noButton.field_146124_l = false;
    
    this.replaceButton = new GuiButton(5, posX + 15, posY + 115, 45, 20, "Replace");
    this.field_146292_n.add(this.replaceButton);
    this.replaceButton.field_146125_m = false;
    this.replaceButton.field_146124_l = false;
    
    this.deleteButton = new GuiButton(6, posX + 65, posY + 115, 45, 20, "Delete");
    this.field_146292_n.add(this.deleteButton);
    this.deleteButton.field_146125_m = false;
    this.deleteButton.field_146124_l = false;
    
    this.cancelButton = new GuiButton(7, posX + 115, posY + 115, 45, 20, "Cancel");
    this.field_146292_n.add(this.cancelButton);
    this.cancelButton.field_146125_m = false;
    this.cancelButton.field_146124_l = false;
  }
  
  public void func_73876_c()
  {
    super.func_73876_c();
    if (VillagerContainerWorkbench.output != null)
    {
      VillagerRecipe newRecipe = new VillagerRecipe(VillagerContainerWorkbench.inputs, VillagerContainerWorkbench.output, false);
      if ((this.villager.knownRecipes.contains(newRecipe)) && (!this.villager.customRecipes.contains(newRecipe))) {
        this.teachButton.field_146124_l = false;
      } else {
        this.teachButton.field_146124_l = (popupNum < 0);
      }
    }
    else
    {
      this.teachButton.field_146124_l = false;
    }
    this.resetButton.field_146124_l = ((popupNum < 0) && (this.villager.customRecipes.size() > 0));
    this.backButton.field_146124_l = (popupNum < 0);
    
    this.yesButton.field_146125_m = (popupNum == 0);
    this.yesButton.field_146124_l = (popupNum == 0);
    this.noButton.field_146125_m = (popupNum == 0);
    this.noButton.field_146124_l = (popupNum == 0);
    
    this.replaceButton.field_146125_m = (popupNum == 1);
    this.replaceButton.field_146124_l = (popupNum == 1);
    this.deleteButton.field_146125_m = (popupNum == 1);
    this.deleteButton.field_146124_l = (popupNum == 1);
    this.cancelButton.field_146125_m = (popupNum == 1);
    this.cancelButton.field_146124_l = (popupNum == 1);
  }
  
  public void func_73863_a(int x, int y, float f)
  {
    super.func_73863_a(x, y, f);
    for (int i = 0; i < this.field_146292_n.size(); i++) {
      if ((this.field_146292_n.get(i) instanceof GuiButton))
      {
        GuiButton btn = (GuiButton)this.field_146292_n.get(i);
        if ((btn.func_146115_a()) && (!btn.field_146124_l)) {
          if ((btn.field_146127_k == 0) && (VillagerContainerWorkbench.output != null) && (popupNum < 0))
          {
            String[] desc = { "Cannot Change Profession Recipe" };
            
            List temp = Arrays.asList(desc);
            drawHoveringText(temp, x, y, this.field_146289_q);
          }
          else if ((btn.field_146127_k == 1) && (popupNum < 0))
          {
            String[] desc = { "No Custom Recipes Found" };
            
            List temp = Arrays.asList(desc);
            drawHoveringText(temp, x, y, this.field_146289_q);
          }
        }
      }
    }
  }
  
  protected void func_146284_a(GuiButton button)
  {
    Iterator i;
    switch (button.field_146127_k)
    {
    case 0: 
      VillagerRecipe newRecipe = new VillagerRecipe(VillagerContainerWorkbench.inputs, VillagerContainerWorkbench.output, false);
      if (!this.villager.canCraft(VillagerContainerWorkbench.output))
      {
        this.villager.addCustomRecipe(newRecipe);
        HelpfulVillagers.network.sendToServer(new AddRecipePacket(this.villager.func_145782_y(), newRecipe, 0));
        displayText("Recipe Added");
      }
      else
      {
        popupNum = 1;
        this.tempSlots.clear();
        this.tempSlots.addAll(this.field_147002_h.field_75151_b);
        Iterator i = this.field_147002_h.field_75151_b.iterator();
        while (i.hasNext())
        {
          Slot slot = (Slot)i.next();
          if ((slot.field_75222_d >= 10) && (slot.field_75222_d <= 36)) {
            i.remove();
          }
        }
      }
      break;
    case 1: 
      popupNum = 0;
      this.tempSlots.clear();
      this.tempSlots.addAll(this.field_147002_h.field_75151_b);
      i = this.field_147002_h.field_75151_b.iterator();
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
      while (i.hasNext())
      {
        Slot slot = (Slot)i.next();
        if ((slot.field_75222_d >= 10) && (slot.field_75222_d <= 36)) {
          i.remove();
        }
        continue;
        
        this.player.openGui(HelpfulVillagers.instance, 4, this.villager.field_70170_p, this.villager.func_145782_y(), 0, 0);
        break;
        
        this.villager.resetRecipes();
        HelpfulVillagers.network.sendToServer(new ResetRecipesPacket(this.villager.func_145782_y()));
        displayText("Recipes Reset");
        popupNum = -1;
        this.field_147002_h.field_75151_b.clear();
        this.field_147002_h.field_75151_b.addAll(this.tempSlots);
        break;
        
        popupNum = -1;
        this.field_147002_h.field_75151_b.clear();
        this.field_147002_h.field_75151_b.addAll(this.tempSlots);
        break;
        
        VillagerRecipe newRecipe = new VillagerRecipe(VillagerContainerWorkbench.inputs, VillagerContainerWorkbench.output, false);
        this.villager.replaceCustomRecipe(newRecipe);
        HelpfulVillagers.network.sendToServer(new AddRecipePacket(this.villager.func_145782_y(), newRecipe, 1));
        displayText("Recipe Replaced");
        
        popupNum = -1;
        this.field_147002_h.field_75151_b.clear();
        this.field_147002_h.field_75151_b.addAll(this.tempSlots);
        break;
        
        VillagerRecipe newRecipe = new VillagerRecipe(VillagerContainerWorkbench.inputs, VillagerContainerWorkbench.output, false);
        this.villager.deleteCustomRecipe(newRecipe);
        HelpfulVillagers.network.sendToServer(new AddRecipePacket(this.villager.func_145782_y(), newRecipe, 2));
        displayText("Recipe Deleted");
        
        popupNum = -1;
        this.field_147002_h.field_75151_b.clear();
        this.field_147002_h.field_75151_b.addAll(this.tempSlots);
        break;
        
        popupNum = -1;
        this.field_147002_h.field_75151_b.clear();
        this.field_147002_h.field_75151_b.addAll(this.tempSlots);
      }
    }
  }
  
  private void displayText(String text)
  {
    this.displayText = text;
    this.displayTime = 0;
  }
  
  protected void func_146979_b(int p_146979_1_, int p_146979_2_)
  {
    this.field_146289_q.func_78276_b(I18n.func_135052_a("container.crafting", new Object[0]), 28, 6, 4210752);
    this.field_146289_q.func_78276_b(I18n.func_135052_a("container.inventory", new Object[0]), 8, this.field_147000_g - 96 + 2, 4210752);
    if (this.displayText != null)
    {
      func_73732_a(this.field_146289_q, this.displayText, 90, -10, 16777215);
      this.displayTime += 1;
      if (this.displayTime > 120)
      {
        this.displayText = null;
        this.displayTime = 0;
      }
    }
    if (popupNum == 0)
    {
      func_73732_a(this.field_146289_q, "Delete All Custom Recipes?", 90, 90, 16777215);
    }
    else if (popupNum == 1)
    {
      func_73732_a(this.field_146289_q, "Recipe Already Known", 90, 90, 16777215);
      func_73732_a(this.field_146289_q, "Replace Recipe?", 90, 100, 16777215);
    }
  }
  
  protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_)
  {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.field_146297_k.func_110434_K().func_110577_a(craftingTableGuiTextures);
    int posX = (this.field_146294_l - this.field_146999_f) / 2;
    int posY = (this.field_146295_m - this.field_147000_g) / 2;
    func_73729_b(posX, posY, 0, 0, this.field_146999_f, this.field_147000_g);
    if (popupNum >= 0)
    {
      this.field_146297_k.field_71446_o.func_110577_a(new ResourceLocation("helpfulvillagers", "textures/gui/dialog_background.png"));
      func_73729_b(posX + 7, posY + 83, 10, 10, 162, 54);
    }
  }
  
  public static class VillagerContainerWorkbench
    extends ContainerWorkbench
  {
    private World worldObj;
    private static ItemStack output;
    private static ArrayList<ItemStack> inputs = new ArrayList();
    
    public VillagerContainerWorkbench(EntityPlayer player)
    {
      super(player.field_70170_p, 0, 0, 0);
      this.worldObj = player.field_70170_p;
    }
    
    public boolean func_75145_c(EntityPlayer player)
    {
      return true;
    }
    
    public void func_75130_a(IInventory p_75130_1_)
    {
      this.field_75160_f.func_70299_a(0, CraftingManager.func_77594_a().func_82787_a(this.field_75162_e, this.worldObj));
      output = this.field_75160_f.func_70301_a(0);
      inputs.clear();
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++)
        {
          ItemStack item = this.field_75162_e.func_70463_b(i, j);
          if (item != null) {
            AIHelper.mergeItemStackArrays(new ItemStack(item.func_77973_b(), 1), inputs);
          }
        }
      }
    }
    
    public ItemStack func_75144_a(int p_75144_1_, int p_75144_2_, int p_75144_3_, EntityPlayer p_75144_4_)
    {
      if (GuiTeachRecipe.popupNum >= 0) {
        return null;
      }
      return super.func_75144_a(p_75144_1_, p_75144_2_, p_75144_3_, p_75144_4_);
    }
  }
}

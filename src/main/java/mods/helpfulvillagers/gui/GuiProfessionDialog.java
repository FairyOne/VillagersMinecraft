package mods.helpfulvillagers.gui;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import java.util.Arrays;
import java.util.List;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.network.ProfessionChangePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiProfessionDialog
  extends GuiScreen
{
  private EntityPlayer player;
  private AbstractVillager villager;
  public final int xSizeOfTexture = 120;
  public final int ySizeOfTexture = 180;
  
  public GuiProfessionDialog(EntityPlayer player, AbstractVillager villager)
  {
    this.player = player;
    this.villager = villager;
  }
  
  public void func_73863_a(int x, int y, float f)
  {
    for (int i = 1; i < this.field_146292_n.size(); i++) {
      if ((this.field_146292_n.get(i) instanceof GuiButton))
      {
        GuiButton btn = (GuiButton)this.field_146292_n.get(i);
        try
        {
          btn.field_146124_l = this.villager.homeVillage.unlockedHalls[(btn.field_146127_k - 1)];
        }
        catch (NullPointerException e)
        {
          btn.field_146124_l = false;
        }
      }
    }
    func_146276_q_();
    
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.field_146297_k.field_71446_o.func_110577_a(new ResourceLocation("helpfulvillagers", "textures/gui/dialog_background.png"));
    
    int posX = (this.field_146294_l - 120) / 2;
    int posY = (this.field_146295_m - 180) / 2;
    
    func_73729_b(posX, posY, 0, 0, 120, 180);
    super.func_73863_a(x, y, f);
    for (int i = 0; i < this.field_146292_n.size(); i++) {
      if ((this.field_146292_n.get(i) instanceof GuiButton))
      {
        GuiButton btn = (GuiButton)this.field_146292_n.get(i);
        if ((btn.func_146115_a()) && (!btn.field_146124_l))
        {
          String[] desc = { "Build Guild Hall To Unlock" };
          
          List temp = Arrays.asList(desc);
          drawHoveringText(temp, x, y, this.field_146289_q);
        }
      }
    }
  }
  
  public void func_73866_w_()
  {
    this.field_146292_n.clear();
    
    int posX = (this.field_146294_l - 120) / 2;
    int posY = (this.field_146295_m - 180) / 2;
    
    this.field_146292_n.add(new GuiButton(0, posX + 10, posY + 5, 100, 20, "Villager"));
    this.field_146292_n.add(new GuiButton(1, posX + 10, posY + 30, 100, 20, "Lumberjack"));
    this.field_146292_n.add(new GuiButton(2, posX + 10, posY + 55, 100, 20, "Miner"));
    this.field_146292_n.add(new GuiButton(3, posX + 10, posY + 80, 100, 20, "Farmer"));
    this.field_146292_n.add(new GuiButton(4, posX + 10, posY + 105, 100, 20, "Soldier"));
    this.field_146292_n.add(new GuiButton(5, posX + 10, posY + 130, 100, 20, "Archer"));
    this.field_146292_n.add(new GuiButton(6, posX + 10, posY + 155, 100, 20, "Merchant"));
  }
  
  public boolean func_73868_f()
  {
    return false;
  }
  
  public void func_146284_a(GuiButton button)
  {
    this.field_146297_k.field_71439_g.func_71053_j();
    this.villager.func_70938_b(button.field_146127_k);
    HelpfulVillagers.network.sendToServer(new ProfessionChangePacket(this.villager.func_145782_y(), button.field_146127_k));
  }
}

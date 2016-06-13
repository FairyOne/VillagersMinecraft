package mods.helpfulvillagers.gui;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import java.util.List;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.network.GUICommandPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class GuiVillagerDialog
  extends GuiScreen
{
  private EntityPlayer player;
  private AbstractVillager villager;
  public final int xSizeOfTexture;
  public final int ySizeOfTexture;
  
  public GuiVillagerDialog(EntityPlayer player, AbstractVillager villager)
  {
    this.player = player;
    this.villager = villager;
    this.xSizeOfTexture = 120;
    if (villager.profession != 0) {
      this.ySizeOfTexture = 130;
    } else {
      this.ySizeOfTexture = 105;
    }
  }
  
  public void func_73863_a(int x, int y, float f)
  {
    func_146276_q_();
    
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.field_146297_k.field_71446_o.func_110577_a(new ResourceLocation("helpfulvillagers", "textures/gui/dialog_background.png"));
    
    int posX = (this.field_146294_l - this.xSizeOfTexture) / 2;
    int posY = (this.field_146295_m - this.ySizeOfTexture) / 2;
    
    func_73729_b(posX, posY, 10, 10, this.xSizeOfTexture, this.ySizeOfTexture);
    super.func_73863_a(x, y, f);
  }
  
  public void func_73866_w_()
  {
    this.field_146292_n.clear();
    
    int posX = (this.field_146294_l - this.xSizeOfTexture) / 2;
    int posY = (this.field_146295_m - this.ySizeOfTexture) / 2;
    if (this.villager.leader == null) {
      this.field_146292_n.add(new GuiButton(0, posX + 10, posY + 5, 100, 20, "Follow Me"));
    } else {
      this.field_146292_n.add(new GuiButton(1, posX + 10, posY + 5, 100, 20, "Stop Following"));
    }
    this.field_146292_n.add(new GuiButton(2, posX + 10, posY + 30, 100, 20, "Trade"));
    
    this.field_146292_n.add(new GuiButton(3, posX + 10, posY + 55, 100, 20, "Change Profession"));
    
    this.field_146292_n.add(new GuiButton(4, posX + 10, posY + 80, 100, 20, "Give Nickname"));
    if ((this.villager.profession == 4) || (this.villager.profession == 5)) {
      this.field_146292_n.add(new GuiButton(5, posX + 10, posY + 105, 100, 20, "Guard Villager"));
    } else if (this.villager.profession == 6) {
      this.field_146292_n.add(new GuiButton(8, posX + 10, posY + 105, 100, 20, "Barter"));
    } else if (this.villager.profession != 0) {
      this.field_146292_n.add(new GuiButton(6, posX + 10, posY + 105, 100, 20, "Crafting"));
    }
  }
  
  public boolean func_73868_f()
  {
    return false;
  }
  
  public void func_146284_a(GuiButton button)
  {
    int guiCommand = -1;
    guiCommand = button.field_146127_k;
    this.field_146297_k.field_71439_g.func_71053_j();
    if (this.villager.field_70170_p.field_72995_K)
    {
      this.villager.guiCommand = guiCommand;
      HelpfulVillagers.network.sendToServer(new GUICommandPacket(this.villager.func_145782_y(), guiCommand));
    }
  }
}

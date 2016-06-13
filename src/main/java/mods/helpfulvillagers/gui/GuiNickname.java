package mods.helpfulvillagers.gui;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.network.NicknamePacket;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

public class GuiNickname
  extends GuiScreen
{
  private EntityPlayer player;
  private AbstractVillager villager;
  private final int boxWidth = 150;
  private final int boxHeight = 20;
  private GuiTextField textField;
  private String name;
  private final String START_NAME;
  private boolean changeName;
  
  public GuiNickname(EntityPlayer player, AbstractVillager villager)
  {
    this.player = player;
    this.villager = villager;
    this.name = villager.nickname;
    this.START_NAME = this.name;
    this.changeName = false;
  }
  
  public void func_73866_w_()
  {
    this.changeName = false;
    int posX = (this.field_146294_l - 150) / 2;
    int posY = (this.field_146295_m - 20) / 2;
    this.textField = new GuiTextField(this.field_146289_q, posX, posY, 150, 20);
    this.textField.func_146195_b(true);
    this.textField.func_146203_f(20);
    String name = this.villager.func_94057_bL();
    this.textField.func_146180_a(name);
  }
  
  public void func_73863_a(int x, int y, float f)
  {
    int posX = (this.field_146294_l - 150) / 2;
    int posY = (this.field_146295_m - 20) / 2;
    func_146276_q_();
    func_73731_b(this.field_146289_q, "Enter Nickname:", posX, posY - 20, 16777215);
    this.textField.func_146194_f();
    super.func_73863_a(x, y, f);
  }
  
  public void func_73869_a(char c, int i)
  {
    super.func_73869_a(c, i);
    if (i == 28)
    {
      this.changeName = true;
      super.func_73869_a(c, 1);
    }
    this.textField.func_146201_a(c, i);
  }
  
  public void func_73876_c()
  {
    this.name = this.textField.func_146179_b();
  }
  
  public void func_146281_b()
  {
    if ((this.changeName) && (this.name != this.START_NAME))
    {
      this.villager.nickname = this.name;
      HelpfulVillagers.network.sendToServer(new NicknamePacket(this.villager.func_145782_y(), this.name));
    }
  }
  
  public boolean func_73868_f()
  {
    return false;
  }
}

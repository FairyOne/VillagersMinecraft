package mods.helpfulvillagers.command;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import java.util.ArrayList;
import java.util.List;
import mods.helpfulvillagers.enums.EnumMessage;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.network.MessageOptionsPacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class VillagerMessagesCommand
  extends CommandBase
{
  private List aliases;
  
  public VillagerMessagesCommand()
  {
    this.aliases = new ArrayList();
    this.aliases.add("villagermessages");
  }
  
  public String func_71517_b()
  {
    return "villagermessages";
  }
  
  public String func_71518_a(ICommandSender icommandsender)
  {
    return "/villagermessages <death:birth:all> <on:off:verbose>";
  }
  
  public List func_71514_a()
  {
    return this.aliases;
  }
  
  public void func_71515_b(ICommandSender icommandsender, String[] astring)
  {
    ChatComponentText errorText = new ChatComponentText(func_71518_a(icommandsender));
    errorText.func_150256_b().func_150238_a(EnumChatFormatting.RED);
    if (astring.length == 2)
    {
      if (astring[0].equals("birth"))
      {
        if (astring[1].equals("on"))
        {
          HelpfulVillagers.network.sendTo(new MessageOptionsPacket(EnumMessage.BIRTH, 1), (EntityPlayerMP)icommandsender);
          icommandsender.func_145747_a(new ChatComponentText("Birth messages set: on"));
        }
        else if (astring[1].equals("off"))
        {
          HelpfulVillagers.network.sendTo(new MessageOptionsPacket(EnumMessage.BIRTH, 0), (EntityPlayerMP)icommandsender);
          icommandsender.func_145747_a(new ChatComponentText("Birth messages set: off"));
        }
        else if (astring[1].equals("verbose"))
        {
          HelpfulVillagers.network.sendTo(new MessageOptionsPacket(EnumMessage.BIRTH, 2), (EntityPlayerMP)icommandsender);
          icommandsender.func_145747_a(new ChatComponentText("Birth messages set: verbose"));
        }
        else
        {
          icommandsender.func_145747_a(errorText);
        }
      }
      else if (astring[0].equals("death"))
      {
        if (astring[1].equals("on"))
        {
          HelpfulVillagers.network.sendTo(new MessageOptionsPacket(EnumMessage.DEATH, 1), (EntityPlayerMP)icommandsender);
          icommandsender.func_145747_a(new ChatComponentText("Death messages set: on"));
        }
        else if (astring[1].equals("off"))
        {
          HelpfulVillagers.network.sendTo(new MessageOptionsPacket(EnumMessage.DEATH, 0), (EntityPlayerMP)icommandsender);
          icommandsender.func_145747_a(new ChatComponentText("Death messages set: off"));
        }
        else if (astring[1].equals("verbose"))
        {
          HelpfulVillagers.network.sendTo(new MessageOptionsPacket(EnumMessage.DEATH, 2), (EntityPlayerMP)icommandsender);
          icommandsender.func_145747_a(new ChatComponentText("Death messages set: verbose"));
        }
        else
        {
          icommandsender.func_145747_a(errorText);
        }
      }
      else if (astring[0].equals("all"))
      {
        if (astring[1].equals("on"))
        {
          HelpfulVillagers.network.sendTo(new MessageOptionsPacket(EnumMessage.BIRTH, 1), (EntityPlayerMP)icommandsender);
          HelpfulVillagers.network.sendTo(new MessageOptionsPacket(EnumMessage.DEATH, 1), (EntityPlayerMP)icommandsender);
          icommandsender.func_145747_a(new ChatComponentText("All messages set: on"));
        }
        else if (astring[1].equals("off"))
        {
          HelpfulVillagers.network.sendTo(new MessageOptionsPacket(EnumMessage.BIRTH, 0), (EntityPlayerMP)icommandsender);
          HelpfulVillagers.network.sendTo(new MessageOptionsPacket(EnumMessage.DEATH, 0), (EntityPlayerMP)icommandsender);
          icommandsender.func_145747_a(new ChatComponentText("All messages set: off"));
        }
        else if (astring[1].equals("verbose"))
        {
          HelpfulVillagers.network.sendTo(new MessageOptionsPacket(EnumMessage.BIRTH, 2), (EntityPlayerMP)icommandsender);
          HelpfulVillagers.network.sendTo(new MessageOptionsPacket(EnumMessage.DEATH, 2), (EntityPlayerMP)icommandsender);
          icommandsender.func_145747_a(new ChatComponentText("All messages set: verbose"));
        }
        else
        {
          icommandsender.func_145747_a(errorText);
        }
      }
      else {
        icommandsender.func_145747_a(errorText);
      }
    }
    else {
      icommandsender.func_145747_a(errorText);
    }
  }
  
  public boolean func_71519_b(ICommandSender icommandsender)
  {
    if ((icommandsender instanceof EntityPlayer)) {
      return true;
    }
    return false;
  }
  
  public List func_71516_a(ICommandSender icommandsender, String[] astring)
  {
    return astring.length == 2 ? func_71530_a(astring, new String[] { "on", "off", "verbose" }) : astring.length == 1 ? func_71530_a(astring, new String[] { "death", "birth", "all" }) : null;
  }
  
  public boolean func_82358_a(String[] astring, int i)
  {
    return false;
  }
  
  public int compareTo(Object o)
  {
    return 0;
  }
}

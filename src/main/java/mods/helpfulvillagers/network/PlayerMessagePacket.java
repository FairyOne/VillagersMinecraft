package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.enums.EnumMessage;
import mods.helpfulvillagers.main.HelpfulVillagers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ChatComponentText;

public class PlayerMessagePacket
  implements IMessage
{
  private String message;
  private int messageType;
  private int senderID;
  
  public PlayerMessagePacket() {}
  
  public PlayerMessagePacket(String message, EnumMessage messageType, int senderID)
  {
    this.message = message;
    this.senderID = senderID;
    switch (messageType)
    {
    case DEATH: 
      this.messageType = 0;
      break;
    case BIRTH: 
      this.messageType = 1;
      break;
    default: 
      this.messageType = -1;
    }
  }
  
  public void toBytes(ByteBuf buffer)
  {
    ByteBufUtils.writeUTF8String(buffer, this.message);
    buffer.writeInt(this.messageType);
    buffer.writeInt(this.senderID);
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.message = ByteBufUtils.readUTF8String(buffer);
    this.messageType = buffer.readInt();
    this.senderID = buffer.readInt();
  }
  
  public static class Handler
    implements IMessageHandler<PlayerMessagePacket, IMessage>
  {
    public IMessage onMessage(PlayerMessagePacket packet, MessageContext ctx)
    {
      String message = null;
      try
      {
        if (ctx.side == Side.CLIENT)
        {
          Minecraft mc = Minecraft.func_71410_x();
          AbstractVillager sender = (AbstractVillager)mc.field_71441_e.func_73045_a(packet.senderID);
          String senderLoc = (int)sender.field_70165_t + ", " + (int)sender.field_70163_u + ", " + (int)sender.field_70161_v;
          switch (packet.messageType)
          {
          case 0: 
            int option = HelpfulVillagers.deathMessageOption;
            if (option == 1) {
              message = packet.message;
            } else if (option == 2) {
              message = packet.message + " at " + senderLoc;
            }
            break;
          case 1: 
            int option = HelpfulVillagers.birthMessageOption;
            if (option == 1) {
              message = packet.message;
            } else if (option == 2) {
              message = packet.message + " at " + senderLoc;
            }
            break;
          }
          if (message != null) {
            mc.field_71439_g.func_145747_a(new ChatComponentText(message));
          }
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

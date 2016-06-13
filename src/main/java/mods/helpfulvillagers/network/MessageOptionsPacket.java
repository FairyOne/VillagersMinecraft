package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.enums.EnumMessage;

public class MessageOptionsPacket
  implements IMessage
{
  private int messageType;
  private int option;
  
  public MessageOptionsPacket() {}
  
  public MessageOptionsPacket(EnumMessage messageType, int option)
  {
    this.option = option;
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
    buffer.writeInt(this.messageType);
    buffer.writeInt(this.option);
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.messageType = buffer.readInt();
    this.option = buffer.readInt();
  }
  
  public static class Handler
    implements IMessageHandler<MessageOptionsPacket, IMessage>
  {
    public IMessage onMessage(MessageOptionsPacket packet, MessageContext ctx)
    {
      try
      {
        if (ctx.side == Side.CLIENT) {
          switch (packet.messageType)
          {
          case 0: 
            mods.helpfulvillagers.main.HelpfulVillagers.deathMessageOption = packet.option;
            break;
          case 1: 
            mods.helpfulvillagers.main.HelpfulVillagers.birthMessageOption = packet.option;
          }
        }
      }
      catch (NullPointerException e)
      {
        e.printStackTrace();
      }
      return null;
    }
  }
}

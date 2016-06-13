package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.entity.AbstractVillager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public class SwingPacket
  implements IMessage
{
  private int id;
  
  public SwingPacket() {}
  
  public SwingPacket(int id)
  {
    this.id = id;
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
  }
  
  public static class Handler
    implements IMessageHandler<SwingPacket, IMessage>
  {
    public IMessage onMessage(SwingPacket message, MessageContext ctx)
    {
      try
      {
        if (ctx.side == Side.CLIENT)
        {
          Minecraft mc = Minecraft.func_71410_x();
          WorldClient world = mc.field_71441_e;
          AbstractVillager entity = (AbstractVillager)world.func_73045_a(message.id);
          entity.func_71038_i();
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

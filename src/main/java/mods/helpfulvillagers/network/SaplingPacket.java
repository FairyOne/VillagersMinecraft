package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.entity.EntityLumberjack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public class SaplingPacket
  implements IMessage
{
  private int id;
  private boolean shouldPlant;
  
  public SaplingPacket() {}
  
  public SaplingPacket(int id, boolean shouldPlant)
  {
    this.id = id;
    this.shouldPlant = shouldPlant;
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
    buffer.writeBoolean(this.shouldPlant);
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
    this.shouldPlant = buffer.readBoolean();
  }
  
  public static class Handler
    implements IMessageHandler<SaplingPacket, IMessage>
  {
    public IMessage onMessage(SaplingPacket message, MessageContext ctx)
    {
      try
      {
        if (ctx.side == Side.CLIENT)
        {
          Minecraft mc = Minecraft.func_71410_x();
          WorldClient world = mc.field_71441_e;
          EntityLumberjack entity = (EntityLumberjack)world.func_73045_a(message.id);
          entity.shouldPlant = message.shouldPlant;
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

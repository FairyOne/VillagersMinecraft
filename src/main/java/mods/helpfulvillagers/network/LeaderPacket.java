package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.entity.AbstractVillager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;

public class LeaderPacket
  implements IMessage
{
  private int id;
  private int leaderID;
  
  public LeaderPacket() {}
  
  public LeaderPacket(int id, int leaderID)
  {
    this.id = id;
    this.leaderID = leaderID;
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
    buffer.writeInt(this.leaderID);
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
    this.leaderID = buffer.readInt();
  }
  
  public static class Handler
    implements IMessageHandler<LeaderPacket, IMessage>
  {
    public IMessage onMessage(LeaderPacket message, MessageContext ctx)
    {
      try
      {
        if (ctx.side == Side.CLIENT)
        {
          Minecraft mc = Minecraft.func_71410_x();
          WorldClient world = mc.field_71441_e;
          AbstractVillager entity = (AbstractVillager)world.func_73045_a(message.id);
          if (message.leaderID < 0)
          {
            entity.leader = null;
          }
          else
          {
            EntityLivingBase leader = (EntityLivingBase)world.func_73045_a(message.leaderID);
            entity.leader = leader;
          }
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

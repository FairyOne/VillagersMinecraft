package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.entity.AbstractVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public class ResetRecipesPacket
  implements IMessage
{
  private int id;
  
  public ResetRecipesPacket() {}
  
  public ResetRecipesPacket(int id)
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
    implements IMessageHandler<ResetRecipesPacket, IMessage>
  {
    public IMessage onMessage(ResetRecipesPacket message, MessageContext ctx)
    {
      try
      {
        if (ctx.side == Side.SERVER)
        {
          World world = ctx.getServerHandler().field_147369_b.field_70170_p;
          AbstractVillager entity = (AbstractVillager)world.func_73045_a(message.id);
          if (entity == null) {
            return null;
          }
          entity.resetRecipes();
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

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

public class ProfessionChangePacket
  implements IMessage
{
  private int id;
  private int newProf;
  
  public ProfessionChangePacket() {}
  
  public ProfessionChangePacket(int id, int newProf)
  {
    this.id = id;
    this.newProf = newProf;
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
    buffer.writeInt(this.newProf);
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
    this.newProf = buffer.readInt();
  }
  
  public static class Handler
    implements IMessageHandler<ProfessionChangePacket, IMessage>
  {
    public IMessage onMessage(ProfessionChangePacket message, MessageContext ctx)
    {
      try
      {
        if (ctx.side == Side.SERVER)
        {
          EntityPlayerMP player = ctx.getServerHandler().field_147369_b;
          World world = player.field_70170_p;
          AbstractVillager entity = (AbstractVillager)world.func_73045_a(message.id);
          entity.func_70938_b(message.newProf);
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

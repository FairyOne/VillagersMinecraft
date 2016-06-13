package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.entity.AbstractVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public class NicknamePacket
  implements IMessage
{
  private int id;
  private String name;
  
  public NicknamePacket() {}
  
  public NicknamePacket(int id, String name)
  {
    this.id = id;
    this.name = name;
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
    ByteBufUtils.writeUTF8String(buffer, this.name);
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
    this.name = ByteBufUtils.readUTF8String(buffer);
  }
  
  public static class Handler
    implements IMessageHandler<NicknamePacket, IMessage>
  {
    public IMessage onMessage(NicknamePacket message, MessageContext ctx)
    {
      try
      {
        if (ctx.side == Side.SERVER)
        {
          EntityPlayerMP player = ctx.getServerHandler().field_147369_b;
          World world = player.field_70170_p;
          AbstractVillager entity = (AbstractVillager)world.func_73045_a(message.id);
          entity.func_94058_c(message.name);
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

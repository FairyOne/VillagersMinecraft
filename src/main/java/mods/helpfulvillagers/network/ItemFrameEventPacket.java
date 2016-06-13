package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import mods.helpfulvillagers.main.HelpfulVillagers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public class ItemFrameEventPacket
  implements IMessage
{
  private int id;
  
  public ItemFrameEventPacket() {}
  
  public ItemFrameEventPacket(int id)
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
    implements IMessageHandler<ItemFrameEventPacket, IMessage>
  {
    public IMessage onMessage(ItemFrameEventPacket message, MessageContext ctx)
    {
      try
      {
        if (ctx.side == Side.SERVER)
        {
          EntityPlayerMP player = ctx.getServerHandler().field_147369_b;
          World world = player.field_70170_p;
          Entity entity = world.func_73045_a(message.id);
          if ((entity instanceof EntityItemFrame))
          {
            EntityItemFrame frame = (EntityItemFrame)entity;
            if (!HelpfulVillagers.checkedFrames.contains(frame)) {
              HelpfulVillagers.checkedFrames.add(frame);
            }
          }
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

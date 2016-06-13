package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import java.io.PrintStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public class PlayerItemStackPacket
  implements IMessage
{
  private int id;
  private ItemStack stack;
  
  public PlayerItemStackPacket() {}
  
  public PlayerItemStackPacket(int id, ItemStack stack)
  {
    this.id = id;
    this.stack = stack;
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
    ByteBufUtils.writeItemStack(buffer, this.stack);
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
    this.stack = ByteBufUtils.readItemStack(buffer);
  }
  
  public static class Handler
    implements IMessageHandler<PlayerItemStackPacket, IMessage>
  {
    public IMessage onMessage(PlayerItemStackPacket message, MessageContext ctx)
    {
      try
      {
        if (ctx.side == Side.SERVER)
        {
          World world = ctx.getServerHandler().field_147369_b.field_70170_p;
          EntityPlayer entity = (EntityPlayer)world.func_73045_a(message.id);
          if (entity == null) {
            return null;
          }
          entity.field_71071_by.func_70437_b(message.stack);
        }
      }
      catch (NullPointerException e)
      {
        System.out.println("ERROR");
      }
      return null;
    }
  }
}

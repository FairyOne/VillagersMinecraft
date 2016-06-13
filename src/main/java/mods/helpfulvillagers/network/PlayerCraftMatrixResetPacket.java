package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import java.io.PrintStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public class PlayerCraftMatrixResetPacket
  implements IMessage
{
  private int id;
  
  public PlayerCraftMatrixResetPacket() {}
  
  public PlayerCraftMatrixResetPacket(int id)
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
    implements IMessageHandler<PlayerCraftMatrixResetPacket, IMessage>
  {
    public IMessage onMessage(PlayerCraftMatrixResetPacket message, MessageContext ctx)
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
          if ((entity.field_71070_bA instanceof ContainerPlayer))
          {
            ContainerPlayer container = (ContainerPlayer)entity.field_71070_bA;
            for (int i = 0; i < container.field_75181_e.func_70302_i_(); i++) {
              container.field_75181_e.func_70299_a(i, null);
            }
          }
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

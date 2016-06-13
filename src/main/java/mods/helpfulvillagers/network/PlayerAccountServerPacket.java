package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.econ.VillageEconomy;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public class PlayerAccountServerPacket
  implements IMessage
{
  private int playerID;
  private int villagerID;
  private int amount;
  
  public PlayerAccountServerPacket() {}
  
  public PlayerAccountServerPacket(EntityPlayer player, AbstractVillager villager)
  {
    this.playerID = player.func_145782_y();
    this.villagerID = villager.func_145782_y();
    this.amount = villager.homeVillage.economy.getAccount(player);
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.playerID);
    buffer.writeInt(this.villagerID);
    buffer.writeInt(this.amount);
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.playerID = buffer.readInt();
    this.villagerID = buffer.readInt();
    this.amount = buffer.readInt();
  }
  
  public static class Handler
    implements IMessageHandler<PlayerAccountServerPacket, IMessage>
  {
    public IMessage onMessage(PlayerAccountServerPacket message, MessageContext ctx)
    {
      try
      {
        if (ctx.side == Side.SERVER)
        {
          World world = ctx.getServerHandler().field_147369_b.field_70170_p;
          
          EntityPlayer player = (EntityPlayer)world.func_73045_a(message.playerID);
          if (player == null) {
            return null;
          }
          AbstractVillager villager = (AbstractVillager)world.func_73045_a(message.villagerID);
          if (villager == null) {
            return null;
          }
          villager.homeVillage.economy.setAccount(player, message.amount);
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

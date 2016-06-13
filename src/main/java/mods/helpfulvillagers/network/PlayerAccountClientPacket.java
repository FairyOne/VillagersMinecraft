package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.econ.VillageEconomy;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerAccountClientPacket
  implements IMessage
{
  private int playerID;
  private int villagerID;
  private int amount;
  
  public PlayerAccountClientPacket() {}
  
  public PlayerAccountClientPacket(EntityPlayer player, AbstractVillager villager)
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
    implements IMessageHandler<PlayerAccountClientPacket, IMessage>
  {
    public IMessage onMessage(PlayerAccountClientPacket message, MessageContext ctx)
    {
      try
      {
        if (ctx.side == Side.CLIENT)
        {
          Minecraft mc = Minecraft.func_71410_x();
          WorldClient world = mc.field_71441_e;
          
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

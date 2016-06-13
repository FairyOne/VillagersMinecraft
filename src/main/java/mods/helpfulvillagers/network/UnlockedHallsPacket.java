package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public class UnlockedHallsPacket
  implements IMessage
{
  private int id;
  private boolean[] unlockedHalls = new boolean[13];
  
  public UnlockedHallsPacket() {}
  
  public UnlockedHallsPacket(int id, boolean[] unlockedHalls)
  {
    this.id = id;
    System.arraycopy(unlockedHalls, 0, this.unlockedHalls, 0, this.unlockedHalls.length);
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
    for (int i = 0; i < this.unlockedHalls.length; i++) {
      buffer.writeBoolean(this.unlockedHalls[i]);
    }
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
    for (int i = 0; i < this.unlockedHalls.length; i++) {
      this.unlockedHalls[i] = buffer.readBoolean();
    }
  }
  
  public static class Handler
    implements IMessageHandler<UnlockedHallsPacket, IMessage>
  {
    public IMessage onMessage(UnlockedHallsPacket message, MessageContext ctx)
    {
      try
      {
        if (ctx.side == Side.CLIENT)
        {
          Minecraft mc = Minecraft.func_71410_x();
          WorldClient world = mc.field_71441_e;
          AbstractVillager entity = (AbstractVillager)world.func_73045_a(message.id);
          if (entity == null) {
            return null;
          }
          entity.homeVillage = new HelpfulVillage();
          System.arraycopy(message.unlockedHalls, 0, entity.homeVillage.unlockedHalls, 0, message.unlockedHalls.length);
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

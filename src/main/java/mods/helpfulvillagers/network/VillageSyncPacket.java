package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ChunkCoordinates;

public class VillageSyncPacket
  implements IMessage
{
  private int[] coords = new int[3];
  private int id;
  
  public VillageSyncPacket() {}
  
  public VillageSyncPacket(HelpfulVillage village, AbstractVillager villager)
  {
    this.coords[0] = village.initialCenter.field_71574_a;
    this.coords[1] = village.initialCenter.field_71572_b;
    this.coords[2] = village.initialCenter.field_71573_c;
    this.id = villager.func_145782_y();
  }
  
  public void toBytes(ByteBuf buffer)
  {
    for (int i = 0; i < 3; i++) {
      buffer.writeInt(this.coords[i]);
    }
    buffer.writeInt(this.id);
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    for (int i = 0; i < 3; i++) {
      this.coords[i] = buffer.readInt();
    }
    this.id = buffer.readInt();
  }
  
  public static class Handler
    implements IMessageHandler<VillageSyncPacket, IMessage>
  {
    public IMessage onMessage(VillageSyncPacket message, MessageContext ctx)
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
          ChunkCoordinates coords = new ChunkCoordinates(message.coords[0], message.coords[1], message.coords[2]);
          for (HelpfulVillage village : HelpfulVillagers.villages) {
            if (village.initialCenter.equals(entity.homeVillage.initialCenter)) {
              village.initialCenter = coords;
            }
          }
          entity.homeVillage.initialCenter = coords;
          entity.villageCenter = coords;
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

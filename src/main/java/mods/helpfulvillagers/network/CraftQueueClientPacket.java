package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import mods.helpfulvillagers.crafting.CraftItem;
import mods.helpfulvillagers.crafting.CraftQueue;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;

public class CraftQueueClientPacket
  implements IMessage
{
  int id;
  private ArrayList<CraftItem> craftQueue = new ArrayList();
  private int queueSize;
  
  public CraftQueueClientPacket() {}
  
  public CraftQueueClientPacket(int id, ArrayList<CraftItem> craftQueue)
  {
    this.id = id;
    
    this.craftQueue.addAll(craftQueue);
    this.queueSize = craftQueue.size();
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
    
    buffer.writeInt(this.queueSize);
    for (CraftItem i : this.craftQueue)
    {
      ByteBufUtils.writeItemStack(buffer, i.getItem());
      ByteBufUtils.writeUTF8String(buffer, i.getName());
      buffer.writeInt(i.getPriority());
    }
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
    
    this.queueSize = buffer.readInt();
    for (int i = 0; i < this.queueSize; i++)
    {
      ItemStack item = ByteBufUtils.readItemStack(buffer);
      String name = ByteBufUtils.readUTF8String(buffer);
      int priority = buffer.readInt();
      this.craftQueue.add(new CraftItem(item, name, priority));
    }
  }
  
  public static class Handler
    implements IMessageHandler<CraftQueueClientPacket, IMessage>
  {
    public IMessage onMessage(CraftQueueClientPacket message, MessageContext ctx)
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
          if (entity.homeVillage == null) {
            entity.homeVillage = new HelpfulVillage();
          }
          entity.homeVillage.craftQueue = new CraftQueue(message.craftQueue);
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.crafting.CraftItem;
import mods.helpfulvillagers.crafting.CraftQueue;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public class CraftItemServerPacket
  implements IMessage
{
  private int id;
  private CraftItem craftItem;
  private boolean craftNow;
  
  public CraftItemServerPacket() {}
  
  public CraftItemServerPacket(int id, CraftItem craftItem, boolean craftNow)
  {
    this.id = id;
    this.craftItem = craftItem;
    this.craftNow = craftNow;
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
    
    ByteBufUtils.writeItemStack(buffer, this.craftItem.getItem());
    ByteBufUtils.writeUTF8String(buffer, this.craftItem.getName());
    buffer.writeInt(this.craftItem.getPriority());
    
    buffer.writeBoolean(this.craftNow);
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
    
    ItemStack item = ByteBufUtils.readItemStack(buffer);
    String name = ByteBufUtils.readUTF8String(buffer);
    int priority = buffer.readInt();
    this.craftItem = new CraftItem(item, name, priority);
    
    this.craftNow = buffer.readBoolean();
  }
  
  public static class Handler
    implements IMessageHandler<CraftItemServerPacket, IMessage>
  {
    public IMessage onMessage(CraftItemServerPacket message, MessageContext ctx)
    {
      try
      {
        if (ctx.side == Side.SERVER)
        {
          World world = ctx.getServerHandler().field_147369_b.field_70170_p;
          AbstractVillager entity = (AbstractVillager)world.func_73045_a(message.id);
          if (entity == null) {
            return null;
          }
          if (message.craftNow)
          {
            entity.currentCraftItem = message.craftItem;
          }
          else
          {
            entity.addCraftItem(message.craftItem);
            HelpfulVillagers.network.sendToAll(new CraftQueueClientPacket(message.id, entity.homeVillage.craftQueue.getAll()));
          }
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

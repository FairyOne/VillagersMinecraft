package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.econ.ItemPrice;
import mods.helpfulvillagers.econ.VillageEconomy;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;

public class ItemPriceClientPacket
  implements IMessage
{
  private int id;
  private ItemStack item;
  private int price;
  private double supply;
  private double demand;
  
  public ItemPriceClientPacket() {}
  
  public ItemPriceClientPacket(AbstractVillager villager, ItemPrice itemPrice)
  {
    this.id = villager.func_145782_y();
    this.item = itemPrice.getItem();
    this.price = itemPrice.getOriginalPrice();
    this.supply = itemPrice.getSupply();
    this.demand = itemPrice.getDemand();
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
    
    buffer.writeInt(this.price);
    buffer.writeDouble(this.supply);
    buffer.writeDouble(this.demand);
    ByteBufUtils.writeItemStack(buffer, this.item);
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
    
    this.price = buffer.readInt();
    this.supply = buffer.readDouble();
    this.demand = buffer.readDouble();
    this.item = ByteBufUtils.readItemStack(buffer);
  }
  
  public static class Handler
    implements IMessageHandler<ItemPriceClientPacket, IMessage>
  {
    public IMessage onMessage(ItemPriceClientPacket message, MessageContext ctx)
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
          ItemPrice itemPrice = new ItemPrice(message.item, message.price, message.supply, message.demand);
          entity.homeVillage.economy.putItemPrice(itemPrice);
        }
      }
      catch (NullPointerException e)
      {
        e.printStackTrace();
      }
      return null;
    }
  }
}

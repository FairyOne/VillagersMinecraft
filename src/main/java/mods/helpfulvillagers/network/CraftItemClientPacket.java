package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import mods.helpfulvillagers.crafting.CraftItem;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.inventory.InventoryVillager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;

public class CraftItemClientPacket
  implements IMessage
{
  private int id;
  private CraftItem currentCraftItem;
  private ArrayList<ItemStack> materialsCollected = new ArrayList();
  private int collectedSize;
  private ArrayList<ItemStack> materialsNeeded = new ArrayList();
  private int neededSize;
  
  public CraftItemClientPacket() {}
  
  public CraftItemClientPacket(int id, CraftItem craftItem, ArrayList<ItemStack> materialsCollected, ArrayList<ItemStack> materialsNeeded)
  {
    this.id = id;
    this.currentCraftItem = craftItem;
    
    this.materialsCollected.addAll(materialsCollected);
    this.collectedSize = materialsCollected.size();
    
    this.materialsNeeded.addAll(materialsNeeded);
    this.neededSize = materialsNeeded.size();
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
    if (this.currentCraftItem != null)
    {
      buffer.writeBoolean(true);
      ByteBufUtils.writeItemStack(buffer, this.currentCraftItem.getItem());
      ByteBufUtils.writeUTF8String(buffer, this.currentCraftItem.getName());
      buffer.writeInt(this.currentCraftItem.getPriority());
    }
    else
    {
      buffer.writeBoolean(false);
    }
    buffer.writeInt(this.collectedSize);
    for (ItemStack i : this.materialsCollected) {
      ByteBufUtils.writeItemStack(buffer, i);
    }
    buffer.writeInt(this.neededSize);
    for (ItemStack i : this.materialsNeeded) {
      ByteBufUtils.writeItemStack(buffer, i);
    }
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
    
    boolean read = buffer.readBoolean();
    if (read)
    {
      ItemStack item = ByteBufUtils.readItemStack(buffer);
      String name = ByteBufUtils.readUTF8String(buffer);
      int priority = buffer.readInt();
      this.currentCraftItem = new CraftItem(item, name, priority);
    }
    else
    {
      this.currentCraftItem = null;
    }
    this.collectedSize = buffer.readInt();
    for (int i = 0; i < this.collectedSize; i++) {
      this.materialsCollected.add(ByteBufUtils.readItemStack(buffer));
    }
    this.neededSize = buffer.readInt();
    for (int i = 0; i < this.neededSize; i++) {
      this.materialsNeeded.add(ByteBufUtils.readItemStack(buffer));
    }
  }
  
  public static class Handler
    implements IMessageHandler<CraftItemClientPacket, IMessage>
  {
    public IMessage onMessage(CraftItemClientPacket message, MessageContext ctx)
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
          entity.currentCraftItem = message.currentCraftItem;
          
          entity.inventory.materialsCollected.clear();
          entity.inventory.materialsCollected.addAll(message.materialsCollected);
          
          entity.materialsNeeded.clear();
          entity.materialsNeeded.addAll(message.materialsNeeded);
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

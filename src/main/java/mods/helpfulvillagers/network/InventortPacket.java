package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.inventory.InventoryVillager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;

public class InventoryPacket
  implements IMessage
{
  private int id;
  private ItemStack[] main = new ItemStack[27];
  private boolean sendMain;
  private ItemStack[] equipment = new ItemStack[5];
  private boolean sendEquip;
  
  public InventoryPacket() {}
  
  public InventoryPacket(int id, ItemStack[] main, ItemStack[] equipment)
  {
    this.id = id;
    if (main != null)
    {
      System.arraycopy(main, 0, this.main, 0, main.length);
      this.sendMain = true;
    }
    else
    {
      this.sendMain = false;
    }
    if (equipment != null)
    {
      System.arraycopy(equipment, 0, this.equipment, 0, equipment.length);
      this.sendEquip = true;
    }
    else
    {
      this.sendEquip = false;
    }
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
    buffer.writeBoolean(this.sendMain);
    buffer.writeBoolean(this.sendEquip);
    if (this.sendMain) {
      for (int i = 0; i < this.main.length; i++) {
        ByteBufUtils.writeItemStack(buffer, this.main[i]);
      }
    }
    if (this.sendEquip) {
      for (int i = 0; i < this.equipment.length; i++) {
        ByteBufUtils.writeItemStack(buffer, this.equipment[i]);
      }
    }
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
    this.sendMain = buffer.readBoolean();
    this.sendEquip = buffer.readBoolean();
    if (this.sendMain) {
      for (int i = 0; i < this.main.length; i++) {
        this.main[i] = ByteBufUtils.readItemStack(buffer);
      }
    }
    if (this.sendEquip) {
      for (int i = 0; i < this.equipment.length; i++) {
        this.equipment[i] = ByteBufUtils.readItemStack(buffer);
      }
    }
  }
  
  public static class Handler
    implements IMessageHandler<InventoryPacket, IMessage>
  {
    public IMessage onMessage(InventoryPacket message, MessageContext ctx)
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
          if (message.sendMain) {
            for (int i = 0; i < message.main.length; i++) {
              entity.inventory.setMainContents(i, message.main[i]);
            }
          }
          if (message.sendEquip) {
            for (int i = 0; i < message.equipment.length; i++) {
              entity.inventory.setEquipmentContents(i, message.equipment[i]);
            }
          }
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

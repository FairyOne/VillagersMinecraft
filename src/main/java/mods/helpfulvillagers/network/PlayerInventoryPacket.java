package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public class PlayerInventoryPacket
  implements IMessage
{
  private int id;
  private ItemStack[] main = new ItemStack[36];
  private boolean sendMain;
  private ItemStack[] equipment = new ItemStack[4];
  private boolean sendEquip;
  
  public PlayerInventoryPacket() {}
  
  public PlayerInventoryPacket(int id, ItemStack[] main, ItemStack[] equipment)
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
    implements IMessageHandler<PlayerInventoryPacket, IMessage>
  {
    public IMessage onMessage(PlayerInventoryPacket message, MessageContext ctx)
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
          for (int i = 0; i < message.main.length; i++) {
            entity.field_71071_by.func_70299_a(i, message.main[i]);
          }
          for (int i = 0; i < message.equipment.length; i++) {
            entity.field_71071_by.func_70299_a(i + message.main.length, message.equipment[i]);
          }
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

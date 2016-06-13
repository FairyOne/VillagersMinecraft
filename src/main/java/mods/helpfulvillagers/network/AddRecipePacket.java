package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import mods.helpfulvillagers.crafting.VillagerRecipe;
import mods.helpfulvillagers.entity.AbstractVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;

public class AddRecipePacket
  implements IMessage
{
  private int id;
  private VillagerRecipe recipe;
  private int flag;
  
  public AddRecipePacket() {}
  
  public AddRecipePacket(int id, VillagerRecipe recipe, int flag)
  {
    this.id = id;
    this.recipe = recipe;
    this.flag = flag;
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
    
    int length = this.recipe.getTotalInputs().size();
    buffer.writeInt(length);
    for (int j = 0; j < length; j++)
    {
      ItemStack input = (ItemStack)this.recipe.getTotalInputs().get(j);
      ByteBufUtils.writeItemStack(buffer, input);
    }
    ByteBufUtils.writeItemStack(buffer, this.recipe.getOutput());
    buffer.writeBoolean(this.recipe.isSmelted());
    
    buffer.writeInt(this.flag);
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
    
    ArrayList<ItemStack> inputs = new ArrayList();
    
    int length = buffer.readInt();
    for (int j = 0; j < length; j++)
    {
      ItemStack input = ByteBufUtils.readItemStack(buffer);
      inputs.add(input);
    }
    ItemStack output = ByteBufUtils.readItemStack(buffer);
    boolean smelt = buffer.readBoolean();
    
    this.recipe = new VillagerRecipe(inputs, output, smelt);
    
    this.flag = buffer.readInt();
  }
  
  public static class Handler
    implements IMessageHandler<AddRecipePacket, IMessage>
  {
    public IMessage onMessage(AddRecipePacket message, MessageContext ctx)
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
          switch (message.flag)
          {
          case 0: 
            entity.addCustomRecipe(message.recipe);
            break;
          case 1: 
            entity.replaceCustomRecipe(message.recipe);
            break;
          case 2: 
            entity.deleteCustomRecipe(message.recipe);
          }
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

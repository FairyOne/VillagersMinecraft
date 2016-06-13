package mods.helpfulvillagers.network;

public class CustomRecipesPacket {

}
package mods.helpfulvillagers.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collections;
import mods.helpfulvillagers.crafting.VillagerRecipe;
import mods.helpfulvillagers.entity.AbstractVillager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;

public class CustomRecipesPacket
  implements IMessage
{
  private int id;
  private int size;
  private ArrayList<VillagerRecipe> recipes = new ArrayList();
  
  public CustomRecipesPacket() {}
  
  public CustomRecipesPacket(int id, ArrayList<VillagerRecipe> recipes)
  {
    this.id = id;
    this.size = recipes.size();
    this.recipes.addAll(recipes);
  }
  
  public void toBytes(ByteBuf buffer)
  {
    buffer.writeInt(this.id);
    
    buffer.writeInt(this.size);
    for (VillagerRecipe i : this.recipes)
    {
      int length = i.getTotalInputs().size();
      buffer.writeInt(length);
      for (int j = 0; j < length; j++)
      {
        ItemStack input = (ItemStack)i.getTotalInputs().get(j);
        ByteBufUtils.writeItemStack(buffer, input);
      }
      ByteBufUtils.writeItemStack(buffer, i.getOutput());
    }
  }
  
  public void fromBytes(ByteBuf buffer)
  {
    this.id = buffer.readInt();
    
    this.size = buffer.readInt();
    
    ArrayList<ItemStack> inputs = new ArrayList();
    for (int i = 0; i < this.size; i++)
    {
      int length = buffer.readInt();
      for (int j = 0; j < length; j++)
      {
        ItemStack input = ByteBufUtils.readItemStack(buffer);
        inputs.add(input);
      }
      ItemStack output = ByteBufUtils.readItemStack(buffer);
      this.recipes.add(new VillagerRecipe(inputs, output, false));
      inputs.clear();
    }
  }
  
  public static class Handler
    implements IMessageHandler<CustomRecipesPacket, IMessage>
  {
    public IMessage onMessage(CustomRecipesPacket message, MessageContext ctx)
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
          entity.resetRecipes();
          entity.customRecipes.addAll(message.recipes);
          entity.knownRecipes.addAll(message.recipes);
          Collections.sort(entity.knownRecipes);
        }
      }
      catch (NullPointerException e) {}
      return null;
    }
  }
}

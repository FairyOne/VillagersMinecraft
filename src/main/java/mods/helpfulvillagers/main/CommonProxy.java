package mods.helpfulvillagers.main;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy
  implements IGuiHandler
{
  public void registerRenderers() {}
  
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
  {
    return null;
  }
  
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
  {
    return null;
  }
  
  public void registerHooks()
  {
    MinecraftForge.EVENT_BUS.register(new CommonHooks());
    FMLCommonHandler.instance().bus().register(new CommonHooks());
  }
}
package mods.helpfulvillagers.main;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.renderer.RenderVillagerCustom;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy
  extends CommonProxy
{
  public void registerRenderers()
  {
    RenderingRegistry.registerEntityRenderingHandler(AbstractVillager.class, new RenderVillagerCustom());
  }
  
  public void registerHooks()
  {
    MinecraftForge.EVENT_BUS.register(new ClientHooks());
    FMLCommonHandler.instance().bus().register(new ClientHooks());
  }
}

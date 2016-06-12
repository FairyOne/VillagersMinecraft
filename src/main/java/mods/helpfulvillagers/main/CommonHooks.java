package mods.helpfulvillagers.main;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import mods.helpfulvillagers.econ.VillageEconomy;
import mods.helpfulvillagers.entity.EntityRegularVillager;
import mods.helpfulvillagers.village.HelpfulVillage;
import mods.helpfulvillagers.village.HelpfulVillageCollection;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;

public class CommonHooks
{
  private static final int VILLAGE_UPDATE = 1200;
  public static int villageTicks = 1200;
  private int prevFrameSize = 0;
  private boolean dayReset = true;
  private boolean nightReset = true;
  
  @SubscribeEvent
  public void entityJoinedWorldEventHandler(EntityJoinWorldEvent event)
    throws UnsupportedEncodingException
  {
    if (!event.world.field_72995_K) {
      if (event.entity.getClass() == EntityVillager.class)
      {
        EntityVillager villager = (EntityVillager)event.entity;
        if ((villager.func_70946_n() > -1) && (villager.func_70946_n() < 6)) {
          if (villager.func_70631_g_())
          {
            EntityRegularVillager newVillager = new EntityRegularVillager(event.world);
            newVillager.func_70080_a(villager.field_70165_t, villager.field_70163_u, villager.field_70161_v, villager.field_70177_z, villager.field_70125_A);
            newVillager.func_70873_a(villager.func_70874_b());
            event.setCanceled(true);
            villager.func_70106_y();
            event.world.func_72838_d(newVillager);
          }
          else
          {
            EntityRegularVillager newVillager = new EntityRegularVillager(event.world);
            event.setCanceled(true);
            newVillager.func_70080_a(villager.field_70165_t, villager.field_70163_u, villager.field_70161_v, villager.field_70177_z, villager.field_70125_A);
            villager.func_70106_y();
            event.world.func_72838_d(newVillager);
          }
        }
      }
      else if (event.entity.getClass() == EntityItemFrame.class)
      {
        EntityItemFrame itemFrame = (EntityItemFrame)event.entity;
        if (itemFrame.func_82335_i() != null) {
          villageTicks = 1200;
        }
      }
    }
  }
  
  @SubscribeEvent
  public void serverTickEventHandler(TickEvent.ServerTickEvent event)
  {
    try
    {
      Iterator iterator = HelpfulVillagers.checkedFrames.iterator();
      while (iterator.hasNext())
      {
        EntityItemFrame frame = (EntityItemFrame)iterator.next();
        if ((frame == null) || (!frame.func_70089_S()) || (frame.func_82335_i() == null)) {
          iterator.remove();
        }
      }
      if (HelpfulVillagers.checkedFrames.size() != this.prevFrameSize)
      {
        villageTicks = 1200;
        this.prevFrameSize = HelpfulVillagers.checkedFrames.size();
      }
      for (int i = 0; i < HelpfulVillagers.villages.size(); i++)
      {
        World world = ((HelpfulVillage)HelpfulVillagers.villages.get(i)).world;
        if ((this.dayReset) && (world.func_72935_r()))
        {
          villageTicks = 1300;
          this.dayReset = false;
          break;
        }
        if ((!this.dayReset) && (!world.func_72935_r())) {
          this.dayReset = true;
        }
        if ((this.nightReset) && (!world.func_72935_r()))
        {
          villageTicks = 1200;
          this.nightReset = false;
          break;
        }
        if ((!this.nightReset) && (world.func_72935_r())) {
          this.nightReset = true;
        }
      }
      if (villageTicks >= 1200)
      {
        ArrayList<Integer> removeVillages = new ArrayList();
        
        iterator = HelpfulVillagers.villages.iterator();
        while (iterator.hasNext())
        {
          HelpfulVillage village = (HelpfulVillage)iterator.next();
          if ((village.isAnnihilated) || ((village.isFullyLoaded()) && (village.getPopulation() <= 0) && (village.getTotalAdded() >= village.getTotalVillagers()))) {
            iterator.remove();
          }
        }
        for (int i = 0; i < HelpfulVillagers.villages.size(); i++) {
          for (int j = 0; j < HelpfulVillagers.villages.size(); j++) {
            if ((i != j) && 
              (!removeVillages.contains(Integer.valueOf(i))))
            {
              HelpfulVillage currentVillage = (HelpfulVillage)HelpfulVillagers.villages.get(i);
              HelpfulVillage otherVillage = (HelpfulVillage)HelpfulVillagers.villages.get(j);
              if (currentVillage.actualBounds.func_72326_a(otherVillage.actualBounds))
              {
                currentVillage.mergeVillage(otherVillage);
                removeVillages.add(Integer.valueOf(j));
              }
            }
          }
        }
        for (int i = 0; i < removeVillages.size(); i++)
        {
          int removeIndex = ((Integer)removeVillages.get(i)).intValue();
          HelpfulVillagers.villages.remove(removeIndex);
        }
        removeVillages.clear();
        for (int i = 0; i < HelpfulVillagers.villages.size(); i++)
        {
          ((HelpfulVillage)HelpfulVillagers.villages.get(i)).updateVillageBox();
          ((HelpfulVillage)HelpfulVillagers.villages.get(i)).findHalls();
          ((HelpfulVillage)HelpfulVillagers.villages.get(i)).checkHalls();
          if ((!((HelpfulVillage)HelpfulVillagers.villages.get(i)).pricesCalculated) && (!((HelpfulVillage)HelpfulVillagers.villages.get(i)).priceCalcStarted)) {
            ((HelpfulVillage)HelpfulVillagers.villages.get(i)).economy = new VillageEconomy((HelpfulVillage)HelpfulVillagers.villages.get(i), true);
          }
          if (villageTicks == 1300) {
            ((HelpfulVillage)HelpfulVillagers.villages.get(i)).economy.decreaseAllDemand();
          }
        }
        HelpfulVillagers.villageCollection.setVillages(HelpfulVillagers.villages);
        
        villageTicks = 0;
      }
      else
      {
        villageTicks += 1;
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      villageTicks = 0;
    }
  }
  
  @SubscribeEvent
  public void clientDisconnectEventHandler(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
  {
    HelpfulVillagers.config.load();
    HelpfulVillagers.config.removeCategory(HelpfulVillagers.config.getCategory("messages"));
    HelpfulVillagers.config.getInt("deathMessage", "messages", HelpfulVillagers.deathMessageOption, 0, 2, "0 - Off, 1 - On, 2 - Verbose");
    HelpfulVillagers.config.getInt("birthMessage", "messages", HelpfulVillagers.birthMessageOption, 0, 2, "0 - Off, 1 - On, 2 - Verbose");
    HelpfulVillagers.config.save();
  }
  
  @SubscribeEvent
  public void worldLoadEventHandler(WorldEvent.Load event)
  {
    if ((event.world.field_72995_K) || (event.world.field_73011_w.field_76574_g != 0)) {
      return;
    }
    HelpfulVillagers.villageCollection = HelpfulVillageCollection.forWorld(event.world);
    if ((HelpfulVillagers.villageCollection != null) && (!HelpfulVillagers.villageCollection.isEmpty()))
    {
      HelpfulVillagers.villages.clear();
      HelpfulVillagers.villages.addAll(HelpfulVillagers.villageCollection.getVillages());
    }
  }
}

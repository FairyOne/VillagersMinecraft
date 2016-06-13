package mods.helpfulvillagers.main;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import mods.helpfulvillagers.command.VillagerMessagesCommand;
import mods.helpfulvillagers.crafting.VillagerRecipe;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.entity.EntityArcher;
import mods.helpfulvillagers.entity.EntityFarmer;
import mods.helpfulvillagers.entity.EntityLumberjack;
import mods.helpfulvillagers.entity.EntityMerchant;
import mods.helpfulvillagers.entity.EntityMiner;
import mods.helpfulvillagers.entity.EntityRegularVillager;
import mods.helpfulvillagers.entity.EntitySoldier;
import mods.helpfulvillagers.network.AddRecipePacket;
import mods.helpfulvillagers.network.AddRecipePacket.Handler;
import mods.helpfulvillagers.network.CraftItemClientPacket;
import mods.helpfulvillagers.network.CraftItemClientPacket.Handler;
import mods.helpfulvillagers.network.CraftItemServerPacket;
import mods.helpfulvillagers.network.CraftItemServerPacket.Handler;
import mods.helpfulvillagers.network.CraftQueueClientPacket;
import mods.helpfulvillagers.network.CraftQueueClientPacket.Handler;
import mods.helpfulvillagers.network.CraftQueueServerPacket;
import mods.helpfulvillagers.network.CraftQueueServerPacket.Handler;
import mods.helpfulvillagers.network.CustomRecipesPacket;
import mods.helpfulvillagers.network.CustomRecipesPacket.Handler;
import mods.helpfulvillagers.network.GUICommandPacket;
import mods.helpfulvillagers.network.GUICommandPacket.Handler;
import mods.helpfulvillagers.network.InventoryPacket;
import mods.helpfulvillagers.network.InventoryPacket.Handler;
import mods.helpfulvillagers.network.ItemFrameEventPacket;
import mods.helpfulvillagers.network.ItemFrameEventPacket.Handler;
import mods.helpfulvillagers.network.ItemPriceClientPacket;
import mods.helpfulvillagers.network.ItemPriceClientPacket.Handler;
import mods.helpfulvillagers.network.ItemPriceServerPacket;
import mods.helpfulvillagers.network.ItemPriceServerPacket.Handler;
import mods.helpfulvillagers.network.LeaderPacket;
import mods.helpfulvillagers.network.LeaderPacket.Handler;
import mods.helpfulvillagers.network.MessageOptionsPacket;
import mods.helpfulvillagers.network.MessageOptionsPacket.Handler;
import mods.helpfulvillagers.network.NicknamePacket;
import mods.helpfulvillagers.network.NicknamePacket.Handler;
import mods.helpfulvillagers.network.PlayerAccountClientPacket;
import mods.helpfulvillagers.network.PlayerAccountClientPacket.Handler;
import mods.helpfulvillagers.network.PlayerAccountServerPacket;
import mods.helpfulvillagers.network.PlayerAccountServerPacket.Handler;
import mods.helpfulvillagers.network.PlayerCraftMatrixResetPacket;
import mods.helpfulvillagers.network.PlayerCraftMatrixResetPacket.Handler;
import mods.helpfulvillagers.network.PlayerInventoryPacket;
import mods.helpfulvillagers.network.PlayerInventoryPacket.Handler;
import mods.helpfulvillagers.network.PlayerItemStackPacket;
import mods.helpfulvillagers.network.PlayerItemStackPacket.Handler;
import mods.helpfulvillagers.network.PlayerMessagePacket;
import mods.helpfulvillagers.network.PlayerMessagePacket.Handler;
import mods.helpfulvillagers.network.ProfessionChangePacket;
import mods.helpfulvillagers.network.ProfessionChangePacket.Handler;
import mods.helpfulvillagers.network.ResetRecipesPacket;
import mods.helpfulvillagers.network.ResetRecipesPacket.Handler;
import mods.helpfulvillagers.network.SaplingPacket;
import mods.helpfulvillagers.network.SaplingPacket.Handler;
import mods.helpfulvillagers.network.SwingPacket;
import mods.helpfulvillagers.network.SwingPacket.Handler;
import mods.helpfulvillagers.network.UnlockedHallsPacket;
import mods.helpfulvillagers.network.UnlockedHallsPacket.Handler;
import mods.helpfulvillagers.network.VillageSyncPacket;
import mods.helpfulvillagers.network.VillageSyncPacket.Handler;
import mods.helpfulvillagers.village.HelpfulVillage;
import mods.helpfulvillagers.village.HelpfulVillageCollection;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;

@Mod(modid="helpfulvillagers", name="Helpful Villagers", version="1.2.4")
public class HelpfulVillagers
{
  public static SimpleNetworkWrapper network;
  public static HelpfulVillageCollection villageCollection;
  public static ArrayList<HelpfulVillage> villages = new ArrayList();
  public static ArrayList<EntityItemFrame> checkedFrames = new ArrayList();
  public static HashMap<EntityPlayer, AbstractVillager> player_guard = new HashMap();
  public static HashMap<Integer, AbstractVillager> villager_id = new HashMap();
  public static Configuration config;
  public static int deathMessageOption = 1;
  public static int birthMessageOption = 1;
  public static boolean infiniteArrows = false;
  public static ArrayList<VillagerRecipe> allCrafting = new ArrayList();
  public static ArrayList<VillagerRecipe> allSmelting = new ArrayList();
  public static ArrayList<VillagerRecipe> lumberjackRecipes = new ArrayList();
  public static ArrayList<VillagerRecipe> farmerRecipes = new ArrayList();
  public static ArrayList<VillagerRecipe> minerRecipes = new ArrayList();
  @Mod.Instance("HelpfulVillagers")
  public static HelpfulVillagers instance;
  @SidedProxy(clientSide="mods.helpfulvillagers.main.ClientProxy", serverSide="mods.helpfulvillagers.main.CommonProxy")
  public static CommonProxy proxy;
  
  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event)
  {
    config = new Configuration(event.getSuggestedConfigurationFile());
    config.load();
    deathMessageOption = config.getInt("deathMessage", "messages", 1, 0, 2, "0 - Off, 1 - On, 2 - Verbose");
    birthMessageOption = config.getInt("birthMessage", "messages", 1, 0, 2, "0 - Off, 1 - On, 2 - Verbose");
    infiniteArrows = config.getBoolean("infiniteArrows", "archer", false, "Set to true to allow Archers to shoot without using arrows");
    config.save();
    initVillagerRecipes();
  }
  
  @Mod.EventHandler
  public void init(FMLInitializationEvent event)
  {
    proxy.registerRenderers();
    
    proxy.registerHooks();
    
    instance = this;
    NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    
    network = NetworkRegistry.INSTANCE.newSimpleChannel("HV");
    network.registerMessage(SaplingPacket.Handler.class, SaplingPacket.class, 0, Side.CLIENT);
    network.registerMessage(SwingPacket.Handler.class, SwingPacket.class, 1, Side.CLIENT);
    network.registerMessage(ProfessionChangePacket.Handler.class, ProfessionChangePacket.class, 2, Side.SERVER);
    network.registerMessage(LeaderPacket.Handler.class, LeaderPacket.class, 3, Side.CLIENT);
    network.registerMessage(GUICommandPacket.Handler.class, GUICommandPacket.class, 4, Side.SERVER);
    network.registerMessage(UnlockedHallsPacket.Handler.class, UnlockedHallsPacket.class, 5, Side.CLIENT);
    network.registerMessage(InventoryPacket.Handler.class, InventoryPacket.class, 6, Side.CLIENT);
    network.registerMessage(NicknamePacket.Handler.class, NicknamePacket.class, 7, Side.SERVER);
    network.registerMessage(PlayerMessagePacket.Handler.class, PlayerMessagePacket.class, 8, Side.CLIENT);
    network.registerMessage(MessageOptionsPacket.Handler.class, MessageOptionsPacket.class, 9, Side.CLIENT);
    network.registerMessage(ItemFrameEventPacket.Handler.class, ItemFrameEventPacket.class, 10, Side.SERVER);
    network.registerMessage(CraftItemServerPacket.Handler.class, CraftItemServerPacket.class, 11, Side.SERVER);
    network.registerMessage(CraftItemClientPacket.Handler.class, CraftItemClientPacket.class, 12, Side.CLIENT);
    network.registerMessage(CraftQueueServerPacket.Handler.class, CraftQueueServerPacket.class, 13, Side.SERVER);
    network.registerMessage(CraftQueueClientPacket.Handler.class, CraftQueueClientPacket.class, 14, Side.CLIENT);
    network.registerMessage(CustomRecipesPacket.Handler.class, CustomRecipesPacket.class, 15, Side.CLIENT);
    network.registerMessage(AddRecipePacket.Handler.class, AddRecipePacket.class, 16, Side.SERVER);
    network.registerMessage(ResetRecipesPacket.Handler.class, ResetRecipesPacket.class, 17, Side.SERVER);
    network.registerMessage(ItemPriceClientPacket.Handler.class, ItemPriceClientPacket.class, 18, Side.CLIENT);
    network.registerMessage(ItemPriceServerPacket.Handler.class, ItemPriceServerPacket.class, 19, Side.SERVER);
    network.registerMessage(VillageSyncPacket.Handler.class, VillageSyncPacket.class, 20, Side.CLIENT);
    network.registerMessage(PlayerInventoryPacket.Handler.class, PlayerInventoryPacket.class, 21, Side.SERVER);
    network.registerMessage(PlayerItemStackPacket.Handler.class, PlayerItemStackPacket.class, 22, Side.SERVER);
    network.registerMessage(PlayerCraftMatrixResetPacket.Handler.class, PlayerCraftMatrixResetPacket.class, 23, Side.SERVER);
    network.registerMessage(PlayerAccountClientPacket.Handler.class, PlayerAccountClientPacket.class, 24, Side.CLIENT);
    network.registerMessage(PlayerAccountServerPacket.Handler.class, PlayerAccountServerPacket.class, 25, Side.SERVER);
    
    EntityRegistry.registerModEntity(EntityRegularVillager.class, "Villager", 0, this, 50, 2, true);
    EntityRegistry.registerModEntity(EntityLumberjack.class, "Lumberjack", 1, this, 50, 2, true);
    EntityRegistry.registerModEntity(EntityMiner.class, "Miner", 2, this, 50, 2, true);
    EntityRegistry.registerModEntity(EntityFarmer.class, "Farmer", 3, this, 50, 2, true);
    EntityRegistry.registerModEntity(EntitySoldier.class, "Soldier", 4, this, 50, 2, true);
    EntityRegistry.registerModEntity(EntityArcher.class, "Archer", 5, this, 50, 2, true);
    EntityRegistry.registerModEntity(EntityMerchant.class, "Merchant", 6, this, 50, 2, true);
  }
  
  @Mod.EventHandler
  public void serverStart(FMLServerStartingEvent event)
  {
    event.registerServerCommand(new VillagerMessagesCommand());
  }
  
  @Mod.EventHandler
  public void serverStop(FMLServerStoppingEvent event)
  {
    villages.clear();
    villageCollection = null;
  }
  
  private void initVillagerRecipes()
  {
    List<IRecipe> recipes = CraftingManager.func_77594_a().func_77592_b();
    VillagerRecipe recipe;
    for (int i = 0; i < recipes.size(); i++)
    {
      ItemStack outputItem = ((IRecipe)recipes.get(i)).func_77571_b();
      if (outputItem != null)
      {
        recipe = new VillagerRecipe((IRecipe)recipes.get(i), false);
        allCrafting.add(recipe);
        for (int j = 0; j < EntityLumberjack.lumberjackCraftables.length; j++)
        {
          ItemStack currItem = EntityLumberjack.lumberjackCraftables[j];
          if ((currItem.func_77973_b().equals(outputItem.func_77973_b())) && 
            (recipe.getOutput() != null) && (!lumberjackRecipes.contains(recipe)))
          {
            lumberjackRecipes.add(recipe);
            break;
          }
        }
        for (int j = 0; j < EntityFarmer.farmerCraftables.length; j++)
        {
          ItemStack currItem = EntityFarmer.farmerCraftables[j];
          if ((currItem.func_77973_b().equals(outputItem.func_77973_b())) && 
            (recipe.getOutput() != null) && (!farmerRecipes.contains(recipe)))
          {
            farmerRecipes.add(recipe);
            break;
          }
        }
        for (int j = 0; j < EntityMiner.minerCraftables.length; j++)
        {
          ItemStack currItem = EntityMiner.minerCraftables[j];
          if ((currItem.func_77973_b().equals(outputItem.func_77973_b())) && 
            (recipe.getOutput() != null) && (!minerRecipes.contains(recipe)))
          {
            minerRecipes.add(recipe);
            break;
          }
        }
      }
    }
    Map map = FurnaceRecipes.func_77602_a().func_77599_b();
    Set<Map.Entry> entrySet = map.entrySet();
    for (Map.Entry entry : entrySet)
    {
      ItemStack outputItem = (ItemStack)entry.getValue();
      
      VillagerRecipe recipe = new VillagerRecipe((ItemStack)entry.getKey(), outputItem, true);
      allSmelting.add(recipe);
      for (int i = 0; i < EntityFarmer.farmerSmeltables.length; i++)
      {
        ItemStack currItem = EntityFarmer.farmerSmeltables[i];
        if ((currItem.func_77973_b().equals(outputItem.func_77973_b())) && 
          (recipe.getOutput() != null) && (!farmerRecipes.contains(recipe)))
        {
          farmerRecipes.add(recipe);
          break;
        }
      }
      for (int i = 0; i < EntityMiner.minerSmeltables.length; i++)
      {
        ItemStack currItem = EntityMiner.minerSmeltables[i];
        if ((currItem.func_77973_b().equals(outputItem.func_77973_b())) && 
          (recipe.getOutput() != null) && (!minerRecipes.contains(recipe)))
        {
          minerRecipes.add(recipe);
          break;
        }
      }
    }
    Collections.sort(lumberjackRecipes);
    Collections.sort(farmerRecipes);
    Collections.sort(minerRecipes);
  }
}

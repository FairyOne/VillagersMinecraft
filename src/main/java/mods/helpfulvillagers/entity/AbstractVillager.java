package mods.helpfulvillagers.entity;

import com.google.common.collect.Multimap;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import mods.helpfulvillagers.ai.EntityAIFollowLeader;
import mods.helpfulvillagers.ai.EntityAIMoveIndoorsCustom;
import mods.helpfulvillagers.ai.EntityAIVillagerMateCustom;
import mods.helpfulvillagers.crafting.CraftItem;
import mods.helpfulvillagers.crafting.CraftQueue;
import mods.helpfulvillagers.crafting.CraftTree.Node;
import mods.helpfulvillagers.crafting.VillagerRecipe;
import mods.helpfulvillagers.econ.VillageEconomy;
import mods.helpfulvillagers.enums.EnumActivity;
import mods.helpfulvillagers.enums.EnumMessage;
import mods.helpfulvillagers.inventory.InventoryVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.network.CraftItemClientPacket;
import mods.helpfulvillagers.network.CraftQueueClientPacket;
import mods.helpfulvillagers.network.CustomRecipesPacket;
import mods.helpfulvillagers.network.LeaderPacket;
import mods.helpfulvillagers.network.PlayerAccountClientPacket;
import mods.helpfulvillagers.network.PlayerMessagePacket;
import mods.helpfulvillagers.network.SwingPacket;
import mods.helpfulvillagers.network.UnlockedHallsPacket;
import mods.helpfulvillagers.network.VillageSyncPacket;
import mods.helpfulvillagers.util.AIHelper;
import mods.helpfulvillagers.util.ResourceCluster;
import mods.helpfulvillagers.village.GuildHall;
import mods.helpfulvillagers.village.HelpfulVillage;
import mods.helpfulvillagers.village.HelpfulVillageCollection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

public abstract class AbstractVillager
  extends EntityVillager
{
  private final int REPRODUCE_TIME = 1000;
  public int profession;
  public String nickname;
  public String profName;
  public InventoryVillager inventory;
  public ChunkCoordinates villageCenter;
  public HelpfulVillage homeVillage;
  public GuildHall homeGuildHall;
  public EntityLivingBase leader;
  private int leaderID;
  public int guiCommand;
  public boolean hasTool;
  private boolean isSwinging;
  private int swingTicks;
  private int healthTicks;
  private boolean dayCheck;
  private boolean hasDied;
  public boolean changeGuildHall;
  protected ItemStack[] validTools = new ItemStack[0];
  public AxisAlignedBB searchBox;
  public AxisAlignedBB pickupBox;
  protected int searchRadius;
  protected int pickupRadius;
  private boolean canPickup;
  public ArrayList<VillagerRecipe> knownRecipes = new ArrayList();
  public ArrayList<VillagerRecipe> customRecipes = new ArrayList();
  public CraftItem currentCraftItem;
  public ArrayList<CraftTree.Node> craftChain = new ArrayList();
  public ArrayList<ItemStack> materialsNeeded = new ArrayList();
  public ArrayList<ItemStack> smeltablesNeeded = new ArrayList();
  public ItemStack queuedTool = null;
  public EnumActivity currentActivity;
  public ResourceCluster lastResource;
  public ResourceCluster currentResource;
  public int villagerID;
  
  public AbstractVillager()
  {
    super(null);
  }
  
  public AbstractVillager(World world)
  {
    super(world);
    this.field_70138_W = ((float)(this.field_70138_W + 0.5D));
    this.profession = 0;
    this.nickname = "";
    this.inventory = new InventoryVillager(this);
    this.villageCenter = null;
    this.homeVillage = null;
    this.homeGuildHall = null;
    this.leader = null;
    this.guiCommand = -1;
    this.isSwinging = false;
    this.healthTicks = 0;
    this.searchBox = this.field_70121_D.func_72329_c();
    this.pickupBox = this.field_70121_D.func_72329_c();
    this.searchRadius = 1;
    this.pickupRadius = 1;
    this.canPickup = true;
    this.currentActivity = EnumActivity.IDLE;
    this.lastResource = null;
    this.hasTool = false;
    this.villagerID = 0;
    this.leaderID = 0;
    this.dayCheck = true;
    this.changeGuildHall = false;
    this.hasDied = false;
    this.currentCraftItem = null;
    addAI();
  }
  
  public AbstractVillager(AbstractVillager villager)
  {
    super(villager.field_70170_p);
    this.field_70138_W = ((float)(this.field_70138_W + 0.5D));
    this.nickname = villager.nickname;
    villager.inventory.dumpCollected(true);
    villager.inventory.dumpCollected(false);
    this.inventory = villager.inventory;
    this.inventory.owner = this;
    this.villageCenter = villager.villageCenter;
    this.homeVillage = villager.homeVillage;
    this.homeGuildHall = null;
    this.leader = villager.leader;
    this.guiCommand = villager.guiCommand;
    this.isSwinging = false;
    this.healthTicks = 0;
    this.searchBox = this.field_70121_D.func_72329_c();
    this.pickupBox = this.field_70121_D.func_72329_c();
    this.searchRadius = 1;
    this.pickupRadius = 1;
    this.canPickup = true;
    this.currentActivity = EnumActivity.IDLE;
    this.lastResource = null;
    this.hasTool = false;
    this.villagerID = 0;
    this.leaderID = 0;
    this.dayCheck = true;
    this.changeGuildHall = false;
    this.hasDied = false;
    this.customRecipes.addAll(villager.customRecipes);
    villager.addCraftItem(villager.currentCraftItem);
    this.currentCraftItem = null;
    for (ItemStack i : villager.inventory.materialsCollected) {
      this.inventory.addItem(i);
    }
    this.inventory.materialsCollected.clear();
    func_94058_c(villager.func_94057_bL());
    addAI();
  }
  
  private void addAI()
  {
    this.field_70714_bg.field_75782_a.clear();
    
    func_70661_as().func_75498_b(true);
    func_70661_as().func_75491_a(true);
    
    this.field_70714_bg.func_75776_a(1, new EntityAIFollowLeader(this));
    this.field_70714_bg.func_75776_a(2, new EntityAIMoveIndoorsCustom(this));
    
    this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
    this.field_70714_bg.func_75776_a(1, new EntityAITradePlayer(this));
    this.field_70714_bg.func_75776_a(1, new EntityAILookAtTradePlayer(this));
    this.field_70714_bg.func_75776_a(2, new EntityAIVillagerMateCustom(this));
    this.field_70714_bg.func_75776_a(4, new EntityAIOpenDoor(this, true));
    this.field_70714_bg.func_75776_a(5, new EntityAIMoveTowardsRestriction(this, 0.30000001192092896D));
    this.field_70714_bg.func_75776_a(7, new EntityAIFollowGolem(this));
    this.field_70714_bg.func_75776_a(8, new EntityAIPlay(this, 0.3199999928474426D));
    this.field_70714_bg.func_75776_a(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
    this.field_70714_bg.func_75776_a(9, new EntityAIWatchClosest2(this, EntityVillager.class, 5.0F, 0.02F));
    this.field_70714_bg.func_75776_a(9, new EntityAIWander(this, 0.30000001192092896D));
    this.field_70714_bg.func_75776_a(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
  }
  
  public boolean func_70085_c(EntityPlayer player)
  {
    if (player.func_70093_af()) {
      return false;
    }
    if (HelpfulVillagers.player_guard.containsKey(player))
    {
      AbstractVillager guard = (AbstractVillager)HelpfulVillagers.player_guard.remove(player);
      if (guard.equals(this))
      {
        HelpfulVillagers.player_guard.put(player, guard);
      }
      else
      {
        guard.setLeader(this);
        if (this.field_70170_p.field_72995_K)
        {
          String guardName = guard.func_94057_bL();
          if ((guardName == null) || (guardName.equals("")) || (guardName.equals(" "))) {
            guardName = "A " + guard.profName;
          } else {
            guardName = guardName + " the " + guard.profName;
          }
          String leaderName = func_94057_bL();
          if ((leaderName == null) || (leaderName.equals("")) || (leaderName.equals(" "))) {
            leaderName = "this " + this.profName;
          } else {
            leaderName = leaderName + " the " + this.profName;
          }
          String message = guardName + " is now guarding " + leaderName;
          player.func_145747_a(new ChatComponentText(message));
        }
        return false;
      }
    }
    if (func_70631_g_()) {
      return false;
    }
    func_70932_a_(player);
    if ((!this.field_70170_p.field_72995_K) && (!this.customRecipes.isEmpty()))
    {
      if (!this.customRecipes.isEmpty()) {
        HelpfulVillagers.network.sendTo(new CustomRecipesPacket(func_145782_y(), this.customRecipes), (EntityPlayerMP)player);
      }
      if (this.leader == null) {
        HelpfulVillagers.network.sendTo(new LeaderPacket(func_145782_y(), -1), (EntityPlayerMP)player);
      } else {
        HelpfulVillagers.network.sendTo(new LeaderPacket(func_145782_y(), this.leader.func_145782_y()), (EntityPlayerMP)player);
      }
    }
    player.openGui(HelpfulVillagers.instance, 0, this.field_70170_p, func_145782_y(), 0, 0);
    return true;
  }
  
  private void handleGuiCommand()
  {
    EntityPlayer player = func_70931_l_();
    switch (this.guiCommand)
    {
    case 0: 
      setLeader(player);
      this.currentActivity = EnumActivity.FOLLOW;
      if (((this instanceof EntityLumberjack)) || ((this instanceof EntityFarmer)))
      {
        this.lastResource = this.currentResource;
        this.currentResource = null;
      }
      break;
    case 1: 
      if ((this.leader instanceof EntityMiner))
      {
        EntityMiner miner = (EntityMiner)this.leader;
        miner.beingFollowed = false;
      }
      setLeader(null);
      this.currentActivity = EnumActivity.IDLE;
      break;
    case 2: 
      if ((func_70089_S()) && (!func_70631_g_()) && (!player.func_70093_af()))
      {
        if (!this.field_70170_p.field_72995_K) {
          this.inventory.syncInventory();
        }
        player.openGui(HelpfulVillagers.instance, 2, this.field_70170_p, func_145782_y(), 0, 0);
      }
      break;
    case 3: 
      if (!this.field_70170_p.field_72995_K) {
        HelpfulVillagers.network.sendToAll(new UnlockedHallsPacket(func_145782_y(), this.homeVillage.unlockedHalls));
      }
      player.openGui(HelpfulVillagers.instance, 1, this.field_70170_p, func_145782_y(), 0, 0);
      break;
    case 4: 
      player.openGui(HelpfulVillagers.instance, 3, this.field_70170_p, func_145782_y(), 0, 0);
      break;
    case 5: 
      HelpfulVillagers.player_guard.put(player, this);
      if ((this.leader != null) && ((this.leader instanceof EntityMiner)))
      {
        EntityMiner miner = (EntityMiner)this.leader;
        miner.beingFollowed = false;
      }
      setLeader(player);
      this.currentActivity = EnumActivity.FOLLOW;
      break;
    case 6: 
      if (!this.field_70170_p.field_72995_K)
      {
        HelpfulVillagers.network.sendTo(new CraftItemClientPacket(func_145782_y(), this.currentCraftItem, this.inventory.materialsCollected, this.materialsNeeded), (EntityPlayerMP)player);
        try
        {
          HelpfulVillagers.network.sendTo(new CraftQueueClientPacket(func_145782_y(), this.homeVillage.craftQueue.getAll()), (EntityPlayerMP)player);
        }
        catch (Exception e)
        {
          System.out.println("ERROR");
          HelpfulVillagers.network.sendToAll(new CraftQueueClientPacket(func_145782_y(), this.homeVillage.craftQueue.getAll()));
        }
      }
      player.openGui(HelpfulVillagers.instance, 4, this.field_70170_p, func_145782_y(), 0, 0);
      break;
    case 7: 
      player.openGui(HelpfulVillagers.instance, 6, this.field_70170_p, func_145782_y(), 0, 0);
      break;
    case 8: 
      if (!this.field_70170_p.field_72995_K)
      {
        this.homeVillage.economy.fullSyncClient(this, player);
        int amount = this.homeVillage.economy.getAccount(player);
        if (amount < 0)
        {
          this.homeVillage.economy.setAccount(player, 0);
          amount = 0;
        }
        HelpfulVillagers.network.sendTo(new PlayerAccountClientPacket(player, this), (EntityPlayerMP)player);
      }
      player.openGui(HelpfulVillagers.instance, 7, this.field_70170_p, func_145782_y(), 0, 0);
      break;
    }
    this.guiCommand = -1;
  }
  
  public void func_70071_h_()
  {
    super.func_70071_h_();
    if (this.guiCommand >= 0) {
      try
      {
        handleGuiCommand();
      }
      catch (Exception e)
      {
        this.guiCommand = -1;
      }
    }
    if (this.leader != null) {
      this.currentActivity = EnumActivity.FOLLOW;
    }
    if ((this.dayCheck) && (this.field_70170_p.func_72935_r()))
    {
      this.dayCheck = false;
      dayCheck();
    }
    else if ((!this.dayCheck) && (!this.field_70170_p.func_72935_r()))
    {
      this.dayCheck = true;
    }
    getNewHomeVillage();
    syncVillage();
    getNewGuildHall();
    updateBoxes();
    updateArmor();
    updateSwing();
    updateHealth();
    updateID();
    updateLeader();
    pickupItems();
    resetTool();
    resetArmor();
    getCraftItem();
  }
  
  public void moveTo(ChunkCoordinates coords, float speed)
  {
    if (!func_70661_as().func_75492_a(coords.field_71574_a, coords.field_71572_b, coords.field_71573_c, speed))
    {
      Vec3 vector = Vec3.func_72443_a(coords.field_71574_a, coords.field_71572_b, coords.field_71573_c);
      Vec3 tempVec = RandomPositionGenerator.func_75464_a(this, 10, 3, vector);
      func_110171_b((int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v, 20);
      if (tempVec != null) {
        func_70661_as().func_75492_a(tempVec.field_72450_a, tempVec.field_72448_b, tempVec.field_72449_c, speed);
      }
    }
  }
  
  public void moveTo(Entity entity, float speed)
  {
    if (!func_70661_as().func_75497_a(entity, speed))
    {
      ChunkCoordinates coords = new ChunkCoordinates((int)entity.field_70165_t, (int)entity.field_70163_u, (int)entity.field_70161_v);
      Vec3 vector = Vec3.func_72443_a(coords.field_71574_a, coords.field_71572_b, coords.field_71573_c);
      Vec3 tempVec = RandomPositionGenerator.func_75464_a(this, 10, 3, vector);
      func_110171_b((int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v, 20);
      if (tempVec != null) {
        func_70661_as().func_75492_a(tempVec.field_72450_a, tempVec.field_72448_b, tempVec.field_72449_c, speed);
      }
    }
  }
  
  public ChunkCoordinates getCoords()
  {
    return new ChunkCoordinates((int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v);
  }
  
  public ArrayList getSurroundingCoords()
  {
    ArrayList coords = new ArrayList();
    for (int x = (int)this.field_70165_t - 1; x <= (int)this.field_70165_t + 1; x++) {
      for (int y = (int)this.field_70163_u; y <= (int)this.field_70163_u + 1; y++) {
        for (int z = (int)this.field_70161_v - 1; z <= (int)this.field_70161_v + 1; z++) {
          coords.add(new ChunkCoordinates(x, y, z));
        }
      }
    }
    return coords;
  }
  
  public int getDirection()
  {
    return MathHelper.func_76128_c(this.field_70177_z * 4.0F / 360.0F + 0.5D) & 0x3;
  }
  
  public void func_70938_b(int par1)
  {
    if (this.profession != par1) {
      switch (par1)
      {
      case 0: 
        EntityRegularVillager villager = new EntityRegularVillager(this);
        villager.func_70080_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
        if (!this.field_70170_p.field_72995_K)
        {
          this.field_70170_p.func_72838_d(villager);
          if ((villager.leader != null) && (!(villager.leader instanceof AbstractVillager))) {
            HelpfulVillagers.network.sendToAll(new LeaderPacket(villager.func_145782_y(), villager.leader.func_145782_y()));
          }
        }
        func_70106_y();
        break;
      case 1: 
        EntityLumberjack lumberjack = new EntityLumberjack(this);
        lumberjack.func_70080_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
        if (!this.field_70170_p.field_72995_K)
        {
          this.field_70170_p.func_72838_d(lumberjack);
          if ((lumberjack.leader != null) && (!(lumberjack.leader instanceof AbstractVillager))) {
            HelpfulVillagers.network.sendToAll(new LeaderPacket(lumberjack.func_145782_y(), lumberjack.leader.func_145782_y()));
          }
        }
        func_70106_y();
        break;
      case 2: 
        EntityMiner miner = new EntityMiner(this);
        miner.func_70080_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
        if (!this.field_70170_p.field_72995_K)
        {
          this.field_70170_p.func_72838_d(miner);
          if ((miner.leader != null) && (!(miner.leader instanceof AbstractVillager))) {
            HelpfulVillagers.network.sendToAll(new LeaderPacket(miner.func_145782_y(), miner.leader.func_145782_y()));
          }
        }
        func_70106_y();
        break;
      case 3: 
        EntityFarmer farmer = new EntityFarmer(this);
        farmer.func_70080_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
        if (!this.field_70170_p.field_72995_K)
        {
          this.field_70170_p.func_72838_d(farmer);
          if ((farmer.leader != null) && (!(farmer.leader instanceof AbstractVillager))) {
            HelpfulVillagers.network.sendToAll(new LeaderPacket(farmer.func_145782_y(), farmer.leader.func_145782_y()));
          }
        }
        func_70106_y();
        break;
      case 4: 
        EntitySoldier soldier = new EntitySoldier(this);
        soldier.func_70080_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
        if (!this.field_70170_p.field_72995_K)
        {
          this.field_70170_p.func_72838_d(soldier);
          if (soldier.leader != null) {
            HelpfulVillagers.network.sendToAll(new LeaderPacket(soldier.func_145782_y(), soldier.leader.func_145782_y()));
          }
        }
        func_70106_y();
        break;
      case 5: 
        EntityArcher archer = new EntityArcher(this);
        archer.func_70080_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
        if (!this.field_70170_p.field_72995_K)
        {
          this.field_70170_p.func_72838_d(archer);
          if (archer.leader != null) {
            HelpfulVillagers.network.sendToAll(new LeaderPacket(archer.func_145782_y(), archer.leader.func_145782_y()));
          }
        }
        func_70106_y();
        break;
      case 6: 
        EntityMerchant merchant = new EntityMerchant(this);
        merchant.func_70080_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
        if (!this.field_70170_p.field_72995_K)
        {
          this.field_70170_p.func_72838_d(merchant);
          if ((merchant.leader != null) && (!(merchant.leader instanceof AbstractVillager))) {
            HelpfulVillagers.network.sendToAll(new LeaderPacket(merchant.func_145782_y(), merchant.leader.func_145782_y()));
          }
        }
        func_70106_y();
      }
    }
  }
  
  public int func_70946_n()
  {
    return this.profession;
  }
  
  public void setLeader(EntityLivingBase leader)
  {
    this.leader = leader;
    if ((leader != null) && ((leader instanceof AbstractVillager)))
    {
      if ((this.profession == 4) || (this.profession == 5))
      {
        AbstractVillager villager = (AbstractVillager)leader;
        this.leaderID = villager.villagerID;
        if ((villager instanceof EntityMiner))
        {
          EntityMiner miner = (EntityMiner)villager;
          miner.beingFollowed = true;
        }
      }
      else
      {
        this.leader = null;
        this.leaderID = -1;
      }
    }
    else {
      this.leaderID = -1;
    }
    if ((!this.field_70170_p.field_72995_K) && 
      (this.leader == null)) {}
  }
  
  public EntityLivingBase getLeader()
  {
    return this.leader;
  }
  
  public MerchantRecipeList func_70934_b(EntityPlayer par1EntityPlayer)
  {
    return null;
  }
  
  public void takeItemFromPlayer(EntityPlayer player)
  {
    this.inventory.setCurrentItem(player.field_71071_by.func_70448_g());
    player.func_71028_bD();
  }
  
  public void giveItemToPlayer(EntityPlayer player)
  {
    if (this.inventory.getCurrentItem() != null)
    {
      player.field_71071_by.func_70441_a(this.inventory.getCurrentItem());
      this.inventory.setCurrentItem(null);
    }
  }
  
  public ItemStack[] getValidTools()
  {
    return this.validTools;
  }
  
  public abstract boolean isValidTool(ItemStack paramItemStack);
  
  protected void setTools(ItemStack[] items)
  {
    this.validTools = new ItemStack[items.length];
    System.arraycopy(items, 0, this.validTools, 0, this.validTools.length);
  }
  
  protected abstract boolean canCraft();
  
  public boolean canCraft(CraftItem craftItem)
  {
    ItemStack item = craftItem.getItem();
    if (item == null) {
      return false;
    }
    for (VillagerRecipe i : this.knownRecipes) {
      if (i.getOutput().func_77973_b().equals(item.func_77973_b())) {
        return true;
      }
    }
    return false;
  }
  
  public boolean canCraft(ItemStack item)
  {
    if (item == null) {
      return false;
    }
    for (VillagerRecipe i : this.knownRecipes) {
      if (i.getOutput().func_77973_b().equals(item.func_77973_b())) {
        return true;
      }
    }
    return false;
  }
  
  public VillagerRecipe getRecipe(ItemStack item)
  {
    if (item == null) {
      return null;
    }
    for (VillagerRecipe i : this.knownRecipes) {
      if (i.getOutput().func_82833_r().equals(item.func_82833_r())) {
        return i;
      }
    }
    return null;
  }
  
  public void addCustomRecipe(VillagerRecipe recipe)
  {
    if ((!this.customRecipes.contains(recipe)) && (!this.knownRecipes.contains(recipe)))
    {
      this.customRecipes.add(recipe);
      this.knownRecipes.add(recipe);
      Collections.sort(this.knownRecipes);
    }
  }
  
  public void replaceCustomRecipe(VillagerRecipe recipe)
  {
    int index = -1;
    for (int i = 0; i < this.customRecipes.size(); i++)
    {
      VillagerRecipe r = (VillagerRecipe)this.customRecipes.get(i);
      if (r.getOutput().func_82833_r().equals(recipe.getOutput().func_82833_r()))
      {
        index = i;
        break;
      }
    }
    if (index >= 0)
    {
      ArrayList<VillagerRecipe> temp = new ArrayList(this.customRecipes);
      resetRecipes();
      this.customRecipes.addAll(temp);
      this.customRecipes.set(index, recipe);
      this.knownRecipes.addAll(this.customRecipes);
      Collections.sort(this.knownRecipes);
    }
    else
    {
      System.out.println("Recipe Not Found: Replace");
      addCustomRecipe(recipe);
    }
  }
  
  public void deleteCustomRecipe(VillagerRecipe recipe)
  {
    int index = -1;
    for (int i = 0; i < this.customRecipes.size(); i++)
    {
      VillagerRecipe r = (VillagerRecipe)this.customRecipes.get(i);
      if (r.getOutput().func_82833_r().equals(recipe.getOutput().func_82833_r()))
      {
        index = i;
        break;
      }
    }
    if (index >= 0)
    {
      ArrayList<VillagerRecipe> temp = new ArrayList(this.customRecipes);
      resetRecipes();
      this.customRecipes.addAll(temp);
      this.customRecipes.remove(index);
      this.knownRecipes.addAll(this.customRecipes);
      Collections.sort(this.knownRecipes);
    }
    else
    {
      System.out.println("Recipe Not Found: Delete");
    }
  }
  
  public void resetRecipes()
  {
    this.customRecipes.clear();
    this.knownRecipes.clear();
    switch (this.profession)
    {
    case 1: 
      this.knownRecipes.addAll(HelpfulVillagers.lumberjackRecipes);
      break;
    case 2: 
      this.knownRecipes.addAll(HelpfulVillagers.minerRecipes);
      break;
    case 3: 
      this.knownRecipes.addAll(HelpfulVillagers.farmerRecipes);
    }
  }
  
  public boolean isMetadataSensitive(ItemStack item)
  {
    if (item == null) {
      return false;
    }
    VillagerRecipe recipe = getRecipe(item);
    if (recipe == null) {
      return false;
    }
    return recipe.getMetadataSensitivity();
  }
  
  private void resetTool()
  {
    if ((getCurrentItem() != null) && (!isValidTool(getCurrentItem())))
    {
      this.hasTool = false;
      if (!this.inventory.isFull())
      {
        this.inventory.addItem(this.inventory.getCurrentItem());
      }
      else
      {
        EntityItem worldItem = new EntityItem(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, getCurrentItem());
        this.field_70170_p.func_72838_d(worldItem);
      }
      this.inventory.setCurrentItem(null);
    }
    else if (getCurrentItem() == null)
    {
      this.hasTool = false;
      if (this.inventory.containsItem() >= 0)
      {
        int index = this.inventory.containsItem();
        this.inventory.swapEquipment(index, 0);
        this.hasTool = true;
      }
    }
    else
    {
      this.hasTool = true;
    }
  }
  
  private void resetArmor()
  {
    for (int i = 28; i < 32; i++)
    {
      ItemStack item = this.inventory.func_70301_a(i);
      if (!this.inventory.func_94041_b(i, item))
      {
        this.inventory.addItem(item);
        this.inventory.func_70299_a(i, null);
      }
    }
  }
  
  public ItemStack getCurrentItem()
  {
    return this.inventory.getCurrentItem();
  }
  
  public ItemStack func_70694_bm()
  {
    return getCurrentItem();
  }
  
  public void damageItem()
  {
    if (getCurrentItem() == null) {
      return;
    }
    getCurrentItem().func_77964_b(getCurrentItem().func_77960_j() + 1);
    if (getCurrentItem().func_77960_j() >= getCurrentItem().func_77958_k()) {
      this.inventory.setCurrentItem(null);
    }
  }
  
  public void func_71038_i()
  {
    if (this.field_70170_p.field_72995_K)
    {
      if ((!this.isSwinging) || (this.swingTicks >= 4) || (this.swingTicks < 0))
      {
        this.swingTicks = -1;
        this.isSwinging = true;
      }
    }
    else {
      HelpfulVillagers.network.sendToAll(new SwingPacket(func_145782_y()));
    }
  }
  
  private void updateSwing()
  {
    if (this.isSwinging)
    {
      this.swingTicks += 1;
      if (this.swingTicks >= 8)
      {
        this.swingTicks = 0;
        this.isSwinging = false;
      }
    }
    else
    {
      this.swingTicks = 0;
    }
    this.field_70733_aJ = (this.swingTicks / 8.0F);
  }
  
  public boolean isSwinging()
  {
    return this.isSwinging;
  }
  
  public void updateBoxes()
  {
    this.searchBox.func_72324_b(this.field_70165_t - this.searchRadius, this.field_70163_u - this.searchRadius, this.field_70161_v - this.searchRadius, this.field_70165_t + this.searchRadius, this.field_70163_u + this.searchRadius, this.field_70161_v + this.searchRadius);
    this.pickupBox.func_72324_b(this.field_70165_t - this.pickupRadius, this.field_70163_u - this.pickupRadius, this.field_70161_v - this.pickupRadius, this.field_70165_t + this.pickupRadius, this.field_70163_u + this.pickupRadius, this.field_70161_v + this.pickupRadius);
  }
  
  protected void pickupItems()
  {
    if ((func_70631_g_()) || (!this.canPickup)) {
      return;
    }
    if (this.inventory.isFull()) {
      return;
    }
    List items = this.field_70170_p.func_72872_a(EntityItem.class, this.pickupBox);
    Iterator iterator = items.iterator();
    while (iterator.hasNext())
    {
      EntityItem currentItem = (EntityItem)iterator.next();
      if (!currentItem.field_70128_L)
      {
        this.inventory.addItem(currentItem.func_92059_d());
        currentItem.func_70106_y();
      }
    }
  }
  
  private void updateArmor()
  {
    this.inventory.syncEquipment();
    func_70062_b(4, this.inventory.func_70301_a(28));
    func_70062_b(3, this.inventory.func_70301_a(29));
    func_70062_b(2, this.inventory.func_70301_a(30));
    func_70062_b(1, this.inventory.func_70301_a(31));
  }
  
  private void updateHealth()
  {
    this.healthTicks += 1;
    if (this.healthTicks == 60)
    {
      func_70691_i(0.5F);
      this.healthTicks = 0;
    }
  }
  
  private void updateLeader()
  {
    if (!this.field_70170_p.field_72995_K) {
      if (this.leader != null)
      {
        EntityLivingBase temp = (EntityLivingBase)this.field_70170_p.func_73045_a(this.leader.func_145782_y());
        setLeader(temp);
      }
      else if (this.leaderID > 0)
      {
        AbstractVillager temp = (AbstractVillager)HelpfulVillagers.villager_id.get(Integer.valueOf(this.leaderID));
        setLeader(temp);
      }
    }
  }
  
  private void updateID()
  {
    if ((!this.field_70170_p.field_72995_K) && (this.field_70173_aa > func_70681_au().nextInt(10)) && (this.villagerID <= 0))
    {
      int newKey = Math.abs(func_70681_au().nextInt());
      while (HelpfulVillagers.villager_id.containsKey(Integer.valueOf(newKey))) {
        newKey = Math.abs(func_70681_au().nextInt());
      }
      this.villagerID = newKey;
      HelpfulVillagers.villager_id.put(Integer.valueOf(this.villagerID), this);
    }
  }
  
  protected void dayCheck()
  {
    if (this.changeGuildHall)
    {
      this.homeGuildHall = null;
      this.changeGuildHall = false;
    }
    if (this.homeVillage != null)
    {
      ChunkCoordinates center = this.homeVillage.getActualCenter();
      func_110171_b(center.field_71574_a, center.field_71572_b, center.field_71573_c, this.homeVillage.getActualRadius());
    }
  }
  
  public boolean shouldReproduce()
  {
    if (this.homeVillage != null) {
      return Math.abs(this.field_70170_p.func_82737_E() - this.homeVillage.getLastAdded()) >= 1000L;
    }
    return false;
  }
  
  public boolean func_70097_a(DamageSource src, float par2)
  {
    for (int i = 28; i < 32; i++)
    {
      ItemStack armorPiece = this.inventory.func_70301_a(i);
      if (armorPiece != null)
      {
        armorPiece.func_77964_b((int)(armorPiece.func_77960_j() + par2));
        if (armorPiece.func_77960_j() >= armorPiece.func_77958_k()) {
          armorPiece = null;
        }
        this.inventory.func_70299_a(i, armorPiece);
      }
    }
    if ((!this.field_70170_p.field_72995_K) && (this.homeVillage != null) && (this.homeVillage.isInsideVillage(getCoords().field_71574_a, getCoords().field_71572_b, getCoords().field_71573_c)))
    {
      Entity entity = src.func_76346_g();
      if ((entity != null) && ((entity instanceof EntityLivingBase)))
      {
        EntityLivingBase attacker = (EntityLivingBase)entity;
        if (((attacker instanceof IMob)) && (attacker.func_70089_S())) {
          this.homeVillage.lastAggressor = attacker;
        }
      }
    }
    return super.func_70097_a(src, par2);
  }
  
  public void getNewHomeVillage()
  {
    if (this.hasDied) {
      return;
    }
    if ((this.homeVillage != null) && (this.homeVillage.isAnnihilated)) {
      this.homeVillage = null;
    }
    if (this.homeVillage == null)
    {
      if (this.villageCenter != null) {
        for (int i = 0; i < HelpfulVillagers.villages.size(); i++) {
          if ((!((HelpfulVillage)HelpfulVillagers.villages.get(i)).isAnnihilated) && (((HelpfulVillage)HelpfulVillagers.villages.get(i)).initialCenter.equals(this.villageCenter)))
          {
            this.homeVillage = ((HelpfulVillage)HelpfulVillagers.villages.get(i));
            this.homeVillage.addVillager();
            return;
          }
        }
      }
      double closestDist = 100.0D;
      HelpfulVillage closestVillage = null;
      for (int i = 0; i < HelpfulVillagers.villages.size(); i++)
      {
        HelpfulVillage currVillage = (HelpfulVillage)HelpfulVillagers.villages.get(i);
        if (!currVillage.isAnnihilated)
        {
          ChunkCoordinates center = currVillage.getActualCenter();
          double dist = func_70011_f(center.field_71574_a, center.field_71572_b, center.field_71573_c);
          if ((currVillage.isInsideVillage(this.field_70165_t, this.field_70163_u, this.field_70161_v)) || (dist < closestDist))
          {
            closestDist = dist;
            closestVillage = currVillage;
          }
        }
      }
      if (closestVillage != null)
      {
        this.homeVillage = closestVillage;
        this.villageCenter = this.homeVillage.initialCenter;
        this.homeVillage.addVillager();
      }
      else
      {
        int x = (int)this.field_70165_t;
        int z = (int)this.field_70161_v;
        int y = this.field_70170_p.func_72825_h(x, z);
        this.homeVillage = new HelpfulVillage(this.field_70170_p, new ChunkCoordinates(x, y, z));
        this.villageCenter = this.homeVillage.initialCenter;
        HelpfulVillagers.villages.add(this.homeVillage);
        this.homeVillage.addVillager();
      }
    }
  }
  
  private void syncVillage()
  {
    if (!this.field_70170_p.field_72995_K) {
      HelpfulVillagers.network.sendToAll(new VillageSyncPacket(this.homeVillage, this));
    }
  }
  
  public void returnToOrigin()
  {
    if (this.homeVillage == null) {
      func_70661_as().func_75492_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, 0.30000001192092896D);
    } else {
      func_70661_as().func_75492_a(this.homeVillage.getActualCenter().field_71574_a, this.homeVillage.getActualCenter().field_71572_b, this.homeVillage.getActualCenter().field_71573_c, 0.30000001192092896D);
    }
  }
  
  public void getNewGuildHall()
  {
    if ((!this.field_70170_p.field_72995_K) && (this.homeVillage != null)) {
      if (this.profession == 0) {
        this.homeGuildHall = null;
      } else if (this.homeGuildHall == null) {
        this.homeGuildHall = this.homeVillage.lookForExistingHall(this.profession);
      } else if (this.homeGuildHall.itemFrame == null) {
        this.homeGuildHall = this.homeVillage.lookForExistingHall(this.profession);
      }
    }
  }
  
  public void checkGuildHall()
  {
    if ((this.currentActivity == EnumActivity.IDLE) && (this.homeGuildHall != null))
    {
      if (!this.homeVillage.guildHallList.contains(this.homeGuildHall)) {
        this.homeGuildHall = null;
      }
      if ((this.homeGuildHall == null) || 
        (!nearHall())) {}
    }
  }
  
  public boolean nearHall()
  {
    ChunkCoordinates currentPosition = new ChunkCoordinates((int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v);
    if ((this.homeGuildHall != null) && (this.homeGuildHall.insideCoords.contains(currentPosition))) {
      return true;
    }
    ArrayList<ChunkCoordinates> adjacent = AIHelper.getAdjacentCoords(currentPosition);
    for (ChunkCoordinates i : adjacent) {
      if ((this.homeGuildHall != null) && (this.homeGuildHall.insideCoords.contains(i))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean insideHall()
  {
    ChunkCoordinates currentPosition = new ChunkCoordinates((int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v);
    if ((this.homeGuildHall != null) && (this.homeGuildHall.insideCoords.contains(currentPosition))) {
      return true;
    }
    return false;
  }
  
  public void func_70645_a(DamageSource src)
  {
    super.func_70645_a(src);
    if ((!this.field_70170_p.field_72995_K) && (this.homeVillage != null)) {
      this.homeVillage.removeVillager();
    }
    this.hasDied = true;
    this.canPickup = false;
    this.inventory.dumpInventory();
    if (this.currentCraftItem != null) {
      addCraftItem(this.currentCraftItem);
    }
    sendDeathMessage(src);
  }
  
  private void sendDeathMessage(DamageSource src)
  {
    if (!this.field_70170_p.field_72995_K)
    {
      String name = func_94057_bL();
      if ((name == null) || (name.equals("")) || (name.equals(" "))) {
        name = "A " + this.profName;
      } else {
        name = name + " the " + this.profName;
      }
      String cause = src.field_76373_n;
      Entity attacker = src.func_76346_g();
      Entity aiAttacker = func_70643_av();
      if (cause.equals("anvil"))
      {
        cause = " was squashed by an anvil";
      }
      else if (cause.equals("cactus"))
      {
        if ((aiAttacker != null) && (aiAttacker.func_70089_S())) {
          cause = " walked into a cactus whilst trying to escape " + aiAttacker.func_70005_c_();
        } else {
          cause = " was pricked to death";
        }
      }
      else if (cause.equals("arrow"))
      {
        if (attacker.func_70005_c_().equals("arrow")) {
          cause = " was shot by an arrow";
        } else {
          cause = " was shot by " + attacker.func_70005_c_();
        }
      }
      else if (cause.equals("drown"))
      {
        if ((aiAttacker != null) && (aiAttacker.func_70089_S())) {
          cause = " drowned whilst trying to escape " + aiAttacker.func_70005_c_();
        } else {
          cause = " drowned";
        }
      }
      else if (cause.equals("explosion"))
      {
        cause = " blew up";
      }
      else if (cause.equals("explosion.player"))
      {
        cause = " was blown up by " + attacker.func_70005_c_();
      }
      else if (cause.equals("fall"))
      {
        if ((aiAttacker != null) && (aiAttacker.func_70089_S())) {
          cause = " was doomed to fall by " + aiAttacker.func_70005_c_();
        } else {
          cause = " fell from a high place";
        }
      }
      else if (cause.equals("inFire"))
      {
        Entity lastTarget = func_110144_aD();
        if ((lastTarget != null) && (lastTarget.func_70089_S())) {
          cause = " walked into a fire whilst fighting " + lastTarget.func_70005_c_();
        } else if ((aiAttacker != null) && (aiAttacker.func_70089_S())) {
          cause = " walked into a fire whilst trying to escape " + aiAttacker.func_70005_c_();
        } else {
          cause = " went up in flames";
        }
      }
      else if (cause.equals("onFire"))
      {
        Entity lastTarget = func_110144_aD();
        if ((lastTarget != null) && (lastTarget.func_70089_S())) {
          cause = " was burnt to a crisp whilst fighting " + lastTarget.func_70005_c_();
        } else if ((aiAttacker != null) && (aiAttacker.func_70089_S())) {
          cause = " was burnt to a crisp whilst trying to escape " + aiAttacker.func_70005_c_();
        } else {
          cause = " burned to death";
        }
      }
      else if (cause.equals("mob"))
      {
        cause = " was slain by a " + attacker.func_70005_c_();
      }
      else if (cause.equals("player"))
      {
        cause = " was slain by " + attacker.func_70005_c_();
      }
      else if (cause.equals("fireball"))
      {
        cause = " was fireballed by a " + attacker.func_70005_c_();
      }
      else if (cause.equals("indirectMagic"))
      {
        cause = " was killed by " + attacker.func_70005_c_() + " using magic";
      }
      else if (cause.equals("magic"))
      {
        cause = " was killed by magic";
      }
      else if (cause.equals("inWall"))
      {
        cause = " suffocated in a wall";
      }
      else if (cause.equals("lava"))
      {
        if ((aiAttacker != null) && (aiAttacker.func_70089_S())) {
          cause = " tried to swim in lava while trying to escape " + aiAttacker.func_70005_c_();
        } else {
          cause = " tried to swim in lava";
        }
      }
      else if (cause.equals("outOfWorld"))
      {
        if ((aiAttacker != null) && (aiAttacker.func_70089_S())) {
          cause = " was knocked into the void by " + aiAttacker.func_70005_c_();
        } else {
          cause = " fell out of the world";
        }
      }
      else if (cause.equals("wither"))
      {
        cause = " withered away";
      }
      else if (cause.equals("fallingBlock"))
      {
        cause = " was squashed by a falling block";
      }
      else
      {
        cause = " died";
      }
      String message = name + cause;
      HelpfulVillagers.network.sendToAll(new PlayerMessagePacket(message, EnumMessage.DEATH, func_145782_y()));
    }
  }
  
  public boolean shouldReturn()
  {
    return (!this.inventory.isFull()) && (this.hasTool) && (this.field_70170_p.func_72935_r()) && (this.currentCraftItem == null);
  }
  
  public abstract ArrayList getValidCoords();
  
  public float getAttackDamage()
  {
    if ((getCurrentItem() != null) && ((getCurrentItem().func_77973_b() instanceof ItemSword)))
    {
      ItemSword sword = (ItemSword)getCurrentItem().func_77973_b();
      return sword.func_150931_i() + 4.0F;
    }
    float f;
    if ((getCurrentItem() != null) && ((getCurrentItem().func_77973_b() instanceof ItemTool)))
    {
      ItemTool tool = (ItemTool)getCurrentItem().func_77973_b();
      Multimap map = tool.func_111205_h();
      String s = map.get(SharedMonsterAttributes.field_111264_e.func_111108_a()).toString();
      String sAmount = "amount=";
      int i = s.indexOf(sAmount) + sAmount.length();
      int j = s.indexOf(",");
      sAmount = s.substring(i, j);
      f = Float.parseFloat(sAmount);
    }
    if ((getCurrentItem() != null) && ((getCurrentItem().func_77973_b() instanceof ItemBow))) {
      return 4.0F;
    }
    if ((this instanceof EntityFarmer))
    {
      EntityFarmer farmer = (EntityFarmer)this;
      return Math.abs(farmer.getHarvestTime() / 10 - 6);
    }
    return 1.0F;
  }
  
  public void addCraftItem(CraftItem item)
  {
    if ((this.homeVillage != null) && (item != null)) {
      if (item.getPriority() <= 0) {
        this.homeVillage.craftQueue.addVillagerItem(item);
      } else if (item.getPriority() >= 1) {
        this.homeVillage.craftQueue.addPlayerItem(item);
      }
    }
  }
  
  public void getCraftItem()
  {
    if ((!this.field_70170_p.field_72995_K) && (this.homeVillage != null) && (canCraft()) && (this.currentCraftItem == null)) {
      this.homeVillage.craftQueue.getCraftItem(this);
    }
  }
  
  public void resetCraftItem()
  {
    this.currentCraftItem = null;
    this.materialsNeeded.clear();
    this.smeltablesNeeded.clear();
    this.inventory.materialsCollected.clear();
    this.craftChain.clear();
  }
  
  public void lookForItem(ItemStack item)
  {
    ItemStack tempItem = item.func_77946_l();
    for (int i = 0; i < this.homeVillage.guildHallList.size(); i++)
    {
      GuildHall hall = (GuildHall)this.homeVillage.guildHallList.get(i);
      
      hall.checkChests();
      ArrayList chests = hall.guildChests;
      for (int j = 0; j < chests.size(); j++)
      {
        TileEntityChest chest = (TileEntityChest)chests.get(j);
        boolean stopSearch = AIHelper.takeItemsFromChest(tempItem, chest, this);
        if (stopSearch) {
          return;
        }
      }
      hall.checkFurnaces();
      ArrayList furnaces = hall.guildFurnaces;
      for (int j = 0; j < furnaces.size(); j++)
      {
        TileEntityFurnace furnace = (TileEntityFurnace)furnaces.get(j);
        boolean stopSearch = AIHelper.takeItemFromFurnace(tempItem, furnace, this);
        if (stopSearch) {
          return;
        }
      }
    }
  }
  
  public boolean storeCraftedItem()
  {
    if (this.currentCraftItem.getPriority() == 0)
    {
      Iterator iterator = this.homeVillage.guildHallList.iterator();
      while (iterator.hasNext())
      {
        GuildHall hall = (GuildHall)iterator.next();
        if (hall.typeMatchesName(this.currentCraftItem.getName()))
        {
          Iterator iterator1 = hall.guildChests.iterator();
          while (iterator1.hasNext())
          {
            TileEntityChest chest = (TileEntityChest)iterator1.next();
            for (int i = 0; i < chest.func_70302_i_(); i++) {
              if (chest.func_70301_a(i) == null)
              {
                chest.func_70299_a(i, this.currentCraftItem.getItem());
                return true;
              }
            }
          }
        }
      }
    }
    else if (this.currentCraftItem.getPriority() != 1) {}
    return false;
  }
  
  public void func_70014_b(NBTTagCompound compound)
  {
    super.func_70014_b(compound);
    if (this.villageCenter != null)
    {
      int[] villageCoords = new int[3];
      villageCoords[0] = this.villageCenter.field_71574_a;
      villageCoords[1] = this.villageCenter.field_71572_b;
      villageCoords[2] = this.villageCenter.field_71573_c;
      compound.func_74782_a("Village", new NBTTagIntArray(villageCoords));
    }
    compound.func_74782_a("Inventory", this.inventory.writeToNBT(new NBTTagList()));
    if (this.lastResource != null) {
      compound.func_74782_a("Resource", this.lastResource.writeToNBT(new NBTTagList()));
    }
    compound.func_74782_a("VillagerID", new NBTTagInt(this.villagerID));
    
    compound.func_74782_a("LeaderID", new NBTTagInt(this.leaderID));
    if (this.currentCraftItem != null)
    {
      NBTTagCompound craftCompound = new NBTTagCompound();
      this.currentCraftItem.writeToNBT(craftCompound);
      compound.func_74782_a("Craft Item", craftCompound);
    }
    compound.func_74782_a("CustomSize", new NBTTagInt(this.customRecipes.size()));
    for (int i = 0; i < this.customRecipes.size(); i++) {
      compound.func_74782_a("CustomRecipe" + i, ((VillagerRecipe)this.customRecipes.get(i)).writeToNBT(new NBTTagList()));
    }
    if (this.queuedTool != null)
    {
      NBTTagCompound queuedCompound = new NBTTagCompound();
      this.queuedTool.func_77955_b(queuedCompound);
      compound.func_74782_a("Queued Tool", queuedCompound);
    }
  }
  
  public void func_70037_a(NBTTagCompound compound)
  {
    super.func_70037_a(compound);
    
    int[] village = compound.func_74759_k("Village");
    if (village.length > 0)
    {
      this.villageCenter = new ChunkCoordinates(village[0], village[1], village[2]);
      if ((HelpfulVillagers.villageCollection == null) || (HelpfulVillagers.villageCollection.isEmpty()))
      {
        boolean addVillage = true;
        for (int i = 0; i < HelpfulVillagers.villages.size(); i++)
        {
          HelpfulVillage currVillage = (HelpfulVillage)HelpfulVillagers.villages.get(i);
          if (currVillage.initialCenter.equals(this.villageCenter))
          {
            this.homeVillage = currVillage;
            this.homeVillage.addVillager();
            addVillage = false;
            break;
          }
        }
        if (addVillage)
        {
          this.homeVillage = new HelpfulVillage(this.field_70170_p, this.villageCenter);
          HelpfulVillagers.villages.add(this.homeVillage);
          this.homeVillage.addVillager();
        }
      }
    }
    NBTTagList nbttaglist = compound.func_150295_c("Inventory", compound.func_74732_a());
    this.inventory.readFromNBT(nbttaglist);
    if (compound.func_74764_b("Resource"))
    {
      this.lastResource = new ResourceCluster(this.field_70170_p);
      nbttaglist = compound.func_150295_c("Resource", compound.func_74732_a());
      this.lastResource.readFromNBT(nbttaglist);
    }
    this.villagerID = compound.func_74762_e("VillagerID");
    HelpfulVillagers.villager_id.put(Integer.valueOf(this.villagerID), this);
    
    this.leaderID = compound.func_74762_e("LeaderID");
    
    NBTTagCompound craftCompound = (NBTTagCompound)compound.func_74781_a("Craft Item");
    if (craftCompound != null) {
      this.currentCraftItem = CraftItem.loadCraftItemFromNBT(craftCompound);
    }
    int size = compound.func_74762_e("CustomSize");
    for (int i = 0; i < size; i++)
    {
      nbttaglist = compound.func_150295_c("CustomRecipe" + i, compound.func_74732_a());
      VillagerRecipe recipe = new VillagerRecipe();
      recipe.readFromNBT(nbttaglist);
      this.customRecipes.add(recipe);
    }
    this.knownRecipes.addAll(this.customRecipes);
    Collections.sort(this.knownRecipes);
    
    NBTTagCompound queuedCompound = (NBTTagCompound)compound.func_74781_a("Queued Tool");
    if (queuedCompound != null) {
      this.queuedTool = ItemStack.func_77949_a(queuedCompound);
    }
  }
}

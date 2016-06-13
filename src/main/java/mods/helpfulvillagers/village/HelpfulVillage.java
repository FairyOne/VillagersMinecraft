package mods.helpfulvillagers.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import mods.helpfulvillagers.crafting.CraftQueue;
import mods.helpfulvillagers.econ.VillageEconomy;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.util.AIHelper;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

public class HelpfulVillage
{
  private final int NUM_PROFESSIONS = 13;
  private final int LOAD_TIME_MAX = 500;
  public World world;
  private long loadTime;
  public int dimension = Integer.MAX_VALUE;
  public ChunkCoordinates initialCenter;
  public ChunkCoordinates actualCenter = null;
  public int radius = 0;
  public ArrayList<GuildHall> guildHallList = new ArrayList();
  public boolean[] unlockedHalls = new boolean[13];
  private int numVillagers = 0;
  private static int totalAdded = 0;
  private long lastAddedVillager = 0L;
  public boolean isAnnihilated = false;
  public AxisAlignedBB villageBounds;
  public AxisAlignedBB actualBounds;
  public ArrayList<ChunkCoordinates> villageDoors = new ArrayList();
  public int minX;
  public int minY;
  public int minZ;
  public int maxX;
  public int maxY;
  public int maxZ;
  public boolean dayCheck;
  public EntityLivingBase lastAggressor;
  public CraftQueue craftQueue = new CraftQueue();
  public VillageEconomy economy = new VillageEconomy(this, false);
  public boolean priceCalcStarted = false;
  public boolean pricesCalculated = false;
  
  public HelpfulVillage()
  {
    this.world = null;
  }
  
  public HelpfulVillage(World world, ChunkCoordinates center)
  {
    this.world = world;
    this.dimension = world.field_73011_w.field_76574_g;
    this.initialCenter = center;
    init();
  }
  
  public HelpfulVillage(World world, Village village)
  {
    this.world = world;
    this.dimension = world.field_73011_w.field_76574_g;
    this.initialCenter = village.func_75577_a();
    this.radius = village.func_75568_b();
    init();
  }
  
  private void init()
  {
    if (this.world == null) {
      this.world = DimensionManager.getWorld(this.dimension);
    }
    this.loadTime = this.world.func_82737_E();
    if (this.radius <= 0) {
      this.radius = 32;
    }
    this.lastAggressor = null;
    this.dayCheck = true;
    initBounds();
  }
  
  private void initBounds()
  {
    if (this.actualCenter == null)
    {
      this.minX = this.initialCenter.field_71574_a;
      this.maxX = this.initialCenter.field_71574_a;
      this.minY = this.initialCenter.field_71572_b;
      this.maxY = this.initialCenter.field_71572_b;
      this.minZ = this.initialCenter.field_71573_c;
      this.maxZ = this.initialCenter.field_71573_c;
      this.villageBounds = AxisAlignedBB.func_72330_a(this.initialCenter.field_71574_a - this.radius, this.initialCenter.field_71572_b - this.radius, this.initialCenter.field_71573_c - this.radius, this.initialCenter.field_71574_a + this.radius, this.initialCenter.field_71572_b + this.radius, this.initialCenter.field_71573_c + this.radius);
      this.actualCenter = this.initialCenter;
    }
    else
    {
      this.minX = this.actualCenter.field_71574_a;
      this.maxX = this.actualCenter.field_71574_a;
      this.minY = this.actualCenter.field_71572_b;
      this.maxY = this.actualCenter.field_71572_b;
      this.minZ = this.actualCenter.field_71573_c;
      this.maxZ = this.actualCenter.field_71573_c;
      this.villageBounds = AxisAlignedBB.func_72330_a(this.actualCenter.field_71574_a - this.radius, this.actualCenter.field_71572_b - this.radius, this.actualCenter.field_71573_c - this.radius, this.actualCenter.field_71574_a + this.radius, this.actualCenter.field_71572_b + this.radius, this.actualCenter.field_71573_c + this.radius);
    }
    this.actualBounds = AxisAlignedBB.func_72330_a(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
  }
  
  public void updateVillageBox()
  {
    if (this.world == null) {
      return;
    }
    Iterator iterator = this.villageDoors.iterator();
    while (iterator.hasNext())
    {
      ChunkCoordinates currDoor = (ChunkCoordinates)iterator.next();
      if (!this.world.func_147439_a(currDoor.field_71574_a, currDoor.field_71572_b, currDoor.field_71573_c).equals(Blocks.field_150466_ao)) {
        iterator.remove();
      } else if (getDoorFromCoords(currDoor.field_71574_a, currDoor.field_71572_b, currDoor.field_71573_c) == null) {
        iterator.remove();
      }
    }
    for (int x = (int)this.villageBounds.field_72340_a; x <= this.villageBounds.field_72336_d; x++) {
      for (int y = (int)this.villageBounds.field_72338_b; y <= this.villageBounds.field_72337_e; y++) {
        for (int z = (int)this.villageBounds.field_72339_c; z <= this.villageBounds.field_72334_f; z++)
        {
          ChunkCoordinates currCoords = new ChunkCoordinates(x, y, z);
          if ((this.world.func_147439_a(x, y, z).equals(Blocks.field_150466_ao)) && (!this.villageDoors.contains(currCoords)))
          {
            ChunkCoordinates aboveCoords = new ChunkCoordinates(x, y + 1, z);
            ChunkCoordinates belowCoords = new ChunkCoordinates(x, y - 1, z);
            if (((!this.villageDoors.contains(aboveCoords)) || (!this.villageDoors.contains(belowCoords))) && 
              (getDoorFromCoords(currCoords.field_71574_a, currCoords.field_71572_b, currCoords.field_71573_c) != null)) {
              this.villageDoors.add(currCoords);
            }
          }
        }
      }
    }
    int dist = 0;
    initBounds();
    for (int i = 0; i < this.villageDoors.size(); i++)
    {
      ChunkCoordinates currDoor = (ChunkCoordinates)this.villageDoors.get(i);
      if (currDoor.field_71574_a < this.minX) {
        this.minX = (currDoor.field_71574_a - 5);
      } else if (currDoor.field_71574_a > this.maxX) {
        this.maxX = (currDoor.field_71574_a + 5);
      }
      if (currDoor.field_71572_b < this.minY) {
        this.minY = (currDoor.field_71572_b - 5);
      } else if (currDoor.field_71572_b > this.maxY) {
        this.maxY = (currDoor.field_71572_b + 5);
      }
      if (currDoor.field_71573_c < this.minZ) {
        this.minZ = (currDoor.field_71573_c - 5);
      } else if (currDoor.field_71573_c > this.maxZ) {
        this.maxZ = (currDoor.field_71573_c + 5);
      }
      dist = (int)Math.max(currDoor.func_82371_e(this.actualCenter), dist);
    }
    this.radius = Math.max(32, (int)Math.sqrt(dist) + 5);
    this.actualBounds = AxisAlignedBB.func_72330_a(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    this.actualCenter = getActualCenter();
    this.villageBounds = AxisAlignedBB.func_72330_a(this.actualCenter.field_71574_a - this.radius, this.actualCenter.field_71572_b - this.radius, this.actualCenter.field_71573_c - this.radius, this.actualCenter.field_71574_a + this.radius, this.actualCenter.field_71572_b + this.radius, this.actualCenter.field_71573_c + this.radius);
  }
  
  public ChunkCoordinates getInitialCenter()
  {
    return this.initialCenter;
  }
  
  public ChunkCoordinates getActualCenter()
  {
    int x = (this.minX + this.maxX) / 2;
    int y = (this.minY + this.maxY) / 2;
    int z = (this.minZ + this.maxZ) / 2;
    return new ChunkCoordinates(x, y, z);
  }
  
  public int getActualRadius()
  {
    int xRadius = Math.abs(this.maxX - this.minX) / 2;
    int zRadius = Math.abs(this.maxZ - this.minZ) / 2;
    return Math.max(xRadius, zRadius);
  }
  
  public int getPopulation()
  {
    return this.numVillagers;
  }
  
  public int getTotalVillagers()
  {
    return this.world.func_72907_a(AbstractVillager.class);
  }
  
  public int getTotalAdded()
  {
    return totalAdded;
  }
  
  public long getLastAdded()
  {
    return this.lastAddedVillager;
  }
  
  public boolean isFullyLoaded()
  {
    return this.world.func_82737_E() - this.loadTime > 500L;
  }
  
  public void addVillager()
  {
    if ((totalAdded < this.world.func_72907_a(AbstractVillager.class)) || (!isFullyLoaded()))
    {
      this.numVillagers += 1;
      totalAdded += 1;
      this.lastAddedVillager = this.world.func_82737_E();
    }
  }
  
  public void removeVillager()
  {
    this.numVillagers -= 1;
    totalAdded -= 1;
    if (this.numVillagers <= 0) {
      this.isAnnihilated = true;
    }
  }
  
  public boolean isInRange(double x, double y, double z)
  {
    if ((x < this.villageBounds.field_72336_d) && (x > this.villageBounds.field_72340_a) && (y < this.villageBounds.field_72337_e) && (y > this.villageBounds.field_72338_b)) {}
    return ((z < this.villageBounds.field_72334_f ? 1 : 0) & (z > this.villageBounds.field_72339_c ? 1 : 0)) != 0;
  }
  
  public boolean isInsideVillage(double x, double y, double z)
  {
    if (this.actualBounds == null) {
      return false;
    }
    if ((x < this.actualBounds.field_72336_d) && (x > this.actualBounds.field_72340_a) && (y < this.actualBounds.field_72337_e) && (y > this.actualBounds.field_72338_b)) {}
    return ((z < this.actualBounds.field_72334_f ? 1 : 0) & (z > this.actualBounds.field_72339_c ? 1 : 0)) != 0;
  }
  
  public VillageDoorInfo findNearestDoorUnrestricted(int x, int y, int z)
  {
    VillageDoorInfo targetDoor = null;
    ChunkCoordinates currCoords = new ChunkCoordinates(x, y, z);
    Iterator iterator = this.villageDoors.iterator();
    int dist = Integer.MAX_VALUE;
    while (iterator.hasNext())
    {
      ChunkCoordinates currDoor = (ChunkCoordinates)iterator.next();
      if ((int)Math.sqrt(currDoor.func_71569_e(x, y, z)) < dist)
      {
        dist = (int)Math.sqrt(currDoor.func_71569_e(x, y, z));
        targetDoor = getDoorFromCoords(currDoor.field_71574_a, currDoor.field_71572_b, currDoor.field_71573_c);
      }
    }
    return targetDoor;
  }
  
  private VillageDoorInfo getDoorFromCoords(int p_75542_1_, int p_75542_2_, int p_75542_3_)
  {
    int l = ((BlockDoor)Blocks.field_150466_ao).func_150013_e(this.world, p_75542_1_, p_75542_2_, p_75542_3_);
    if ((l != 0) && (l != 2))
    {
      int i1 = 0;
      for (int j1 = -5; j1 < 0; j1++) {
        if (this.world.func_72937_j(p_75542_1_, p_75542_2_, p_75542_3_ + j1)) {
          i1--;
        }
      }
      for (j1 = 1; j1 <= 5; j1++) {
        if (this.world.func_72937_j(p_75542_1_, p_75542_2_, p_75542_3_ + j1)) {
          i1++;
        }
      }
      if (i1 != 0) {
        return new VillageDoorInfo(p_75542_1_, p_75542_2_, p_75542_3_, 0, i1 > 0 ? -2 : 2, 0);
      }
    }
    else
    {
      int i1 = 0;
      for (int j1 = -5; j1 < 0; j1++) {
        if (this.world.func_72937_j(p_75542_1_ + j1, p_75542_2_, p_75542_3_)) {
          i1--;
        }
      }
      for (j1 = 1; j1 <= 5; j1++) {
        if (this.world.func_72937_j(p_75542_1_ + j1, p_75542_2_, p_75542_3_)) {
          i1++;
        }
      }
      if (i1 != 0) {
        return new VillageDoorInfo(p_75542_1_, p_75542_2_, p_75542_3_, i1 > 0 ? -2 : 2, 0, 0);
      }
    }
    return null;
  }
  
  public int getVillageRadius()
  {
    return this.radius;
  }
  
  public EntityLivingBase findNearestVillageAggressor(EntityLivingBase entity)
  {
    if (this.actualBounds == null) {
      return null;
    }
    if ((this.lastAggressor != null) && ((this.lastAggressor instanceof IMob & this.lastAggressor.func_70089_S()))) {
      return this.lastAggressor;
    }
    List entities = this.world.func_72872_a(IMob.class, this.actualBounds);
    Iterator iterator = entities.iterator();
    double d0 = Double.MAX_VALUE;
    EntityLivingBase target = null;
    while (iterator.hasNext())
    {
      Entity curr = (Entity)iterator.next();
      double d1 = entity.func_70068_e(curr);
      if (d1 < d0)
      {
        d0 = d1;
        target = (EntityLivingBase)curr;
      }
    }
    return target;
  }
  
  public GuildHall lookForExistingHall(int profession)
  {
    Iterator iterator = this.guildHallList.iterator();
    ArrayList<GuildHall> matchedHalls = new ArrayList();
    while (iterator.hasNext())
    {
      GuildHall guildHall = (GuildHall)iterator.next();
      if (guildHall.getTypeNum() == profession) {
        matchedHalls.add(guildHall);
      }
    }
    if (matchedHalls.size() > 0)
    {
      Random gen = new Random();
      int i = gen.nextInt(matchedHalls.size());
      return (GuildHall)matchedHalls.get(i);
    }
    return null;
  }
  
  public void checkHalls()
  {
    for (int i = 0; i < this.guildHallList.size(); i++)
    {
      GuildHall guildHall = (GuildHall)this.guildHallList.get(i);
      guildHall.checkFrame();
    }
  }
  
  public void findHalls()
  {
    if (this.world == null) {
      return;
    }
    for (int i = 1; i <= 13; i++)
    {
      GuildHall adder = new GuildHall(this.world, this);
      List itemFrames = this.world.func_72872_a(EntityItemFrame.class, this.villageBounds);
      adder.findCoords(i, itemFrames);
      if ((adder.typeNum > 0) && (!containsHall(adder)) && (adder.insideCoords.size() > 0))
      {
        this.guildHallList.add(adder);
        this.unlockedHalls[(adder.typeNum - 1)] = true;
      }
    }
  }
  
  public boolean containsHall(GuildHall hall)
  {
    for (int i = 0; i < this.guildHallList.size(); i++) {
      if (hall.equals(this.guildHallList.get(i))) {
        return true;
      }
    }
    return false;
  }
  
  public TileEntityChest searchHallsForItem(ItemStack item)
  {
    checkHalls();
    for (int i = 0; i < this.guildHallList.size(); i++)
    {
      GuildHall hall = (GuildHall)this.guildHallList.get(i);
      hall.checkChests();
      for (int j = 0; j < hall.guildChests.size(); j++)
      {
        TileEntityChest chest = (TileEntityChest)hall.guildChests.get(j);
        if (AIHelper.chestContains(chest, item) >= 0) {
          return chest;
        }
      }
    }
    return null;
  }
  
  public void mergeVillage(HelpfulVillage otherVillage)
  {
    this.villageDoors.addAll(otherVillage.villageDoors);
    
    int dist = 0;
    initBounds();
    for (int i = 0; i < this.villageDoors.size(); i++)
    {
      ChunkCoordinates currDoor = (ChunkCoordinates)this.villageDoors.get(i);
      if (currDoor.field_71574_a < this.minX) {
        this.minX = (currDoor.field_71574_a - 5);
      } else if (currDoor.field_71574_a > this.maxX) {
        this.maxX = (currDoor.field_71574_a + 5);
      }
      if (currDoor.field_71572_b < this.minY) {
        this.minY = (currDoor.field_71572_b - 5);
      } else if (currDoor.field_71572_b > this.maxY) {
        this.maxY = (currDoor.field_71572_b + 5);
      }
      if (currDoor.field_71573_c < this.minZ) {
        this.minZ = (currDoor.field_71573_c - 5);
      } else if (currDoor.field_71573_c > this.maxZ) {
        this.maxZ = (currDoor.field_71573_c + 5);
      }
      dist = (int)Math.max(currDoor.func_82371_e(this.actualCenter), dist);
    }
    this.radius = Math.max(32, (int)Math.sqrt(dist) + 5);
    this.actualBounds = AxisAlignedBB.func_72330_a(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    this.actualCenter = getActualCenter();
    this.villageBounds = AxisAlignedBB.func_72330_a(this.actualCenter.field_71574_a - this.radius, this.actualCenter.field_71572_b - this.radius, this.actualCenter.field_71573_c - this.radius, this.actualCenter.field_71574_a + this.radius, this.actualCenter.field_71572_b + this.radius, this.actualCenter.field_71573_c + this.radius);
    
    this.craftQueue.mergeQueue(otherVillage.craftQueue);
  }
  
  public void writeVillageDataToNBT(NBTTagCompound nbttagcompound)
  {
    nbttagcompound.func_74782_a("Dimension", new NBTTagInt(this.dimension));
    
    int[] villageCoords = new int[3];
    villageCoords[0] = this.initialCenter.field_71574_a;
    villageCoords[1] = this.initialCenter.field_71572_b;
    villageCoords[2] = this.initialCenter.field_71573_c;
    nbttagcompound.func_74782_a("Center", new NBTTagIntArray(villageCoords));
    
    nbttagcompound.func_74782_a("Crafts", this.craftQueue.writeToNBT(new NBTTagList()));
    if (this.pricesCalculated) {
      nbttagcompound.func_74782_a("Economy", this.economy.writeToNBT(new NBTTagList()));
    }
  }
  
  public void readVillageDataFromNBT(NBTTagCompound nbttagcompound1)
  {
    this.dimension = nbttagcompound1.func_74762_e("Dimension");
    
    int[] village = nbttagcompound1.func_74759_k("Center");
    this.initialCenter = new ChunkCoordinates(village[0], village[1], village[2]);
    
    init();
    
    NBTTagList nbttaglist = nbttagcompound1.func_150295_c("Crafts", nbttagcompound1.func_74732_a());
    this.craftQueue.readFromNBT(nbttaglist);
    if (nbttagcompound1.func_74764_b("Economy"))
    {
      this.priceCalcStarted = true;
      nbttaglist = nbttagcompound1.func_150295_c("Economy", nbttagcompound1.func_74732_a());
      this.economy.readFromNBT(nbttaglist);
      this.pricesCalculated = true;
    }
  }
}

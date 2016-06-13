package mods.helpfulvillagers.entity;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mods.helpfulvillagers.ai.EntityAILumberjack;
import mods.helpfulvillagers.enums.EnumActivity;
import mods.helpfulvillagers.inventory.InventoryVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.network.SaplingPacket;
import mods.helpfulvillagers.util.AIHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class EntityLumberjack
  extends AbstractVillager
{
  public boolean foundTree;
  public boolean shouldPlant;
  private int previousTime;
  private int currentTime;
  private final int SAPLING_TIME = 200;
  private final ItemStack[] lumberjackTools = { new ItemStack(Items.field_151053_p), new ItemStack(Items.field_151049_t), new ItemStack(Items.field_151036_c), new ItemStack(Items.field_151006_E), new ItemStack(Items.field_151056_x) };
  public static final ItemStack[] lumberjackCraftables = { new ItemStack(Blocks.field_150344_f), new ItemStack(Blocks.field_150462_ai), new ItemStack(Blocks.field_150486_ae), new ItemStack(Blocks.field_150468_ap), new ItemStack(Blocks.field_150422_aJ), new ItemStack(Blocks.field_150376_bx), new ItemStack(Blocks.field_150342_X), new ItemStack(Blocks.field_150323_B), new ItemStack(Blocks.field_150400_ck), new ItemStack(Blocks.field_150476_ad), new ItemStack(Blocks.field_150401_cl), new ItemStack(Blocks.field_150485_bF), new ItemStack(Blocks.field_150487_bG), new ItemStack(Blocks.field_150481_bH), new ItemStack(Blocks.field_150452_aw), new ItemStack(Blocks.field_150471_bO), new ItemStack(Blocks.field_150415_aT), new ItemStack(Blocks.field_150396_be), new ItemStack(Blocks.field_150421_aI), new ItemStack(Blocks.field_150453_bW), new ItemStack(Blocks.field_150479_bC), new ItemStack(Blocks.field_150447_bR), new ItemStack(Items.field_151055_y), new ItemStack(Items.field_151124_az), new ItemStack(Items.field_151155_ap), new ItemStack(Items.field_151159_an), new ItemStack(Items.field_151135_aq), new ItemStack(Items.field_151053_p), new ItemStack(Items.field_151017_I), new ItemStack(Items.field_151039_o), new ItemStack(Items.field_151038_n), new ItemStack(Items.field_151041_m), new ItemStack(Items.field_151031_f), new ItemStack(Items.field_151032_g), new ItemStack(Items.field_151054_z), new ItemStack(Items.field_151112_aM), new ItemStack(Items.field_151146_bM), new ItemStack(Items.field_151160_bD), new ItemStack(Items.field_151104_aV) };
  
  public EntityLumberjack(World world)
  {
    super(world);
    init();
  }
  
  public EntityLumberjack(AbstractVillager villager)
  {
    super(villager);
    init();
  }
  
  private void init()
  {
    this.profession = 1;
    this.profName = "Lumberjack";
    this.currentActivity = EnumActivity.IDLE;
    this.searchRadius = 10;
    this.foundTree = false;
    this.shouldPlant = false;
    this.previousTime = 0;
    this.currentTime = 0;
    this.knownRecipes.addAll(HelpfulVillagers.lumberjackRecipes);
    setTools(this.lumberjackTools);
    getNewGuildHall();
    addThisAI();
  }
  
  private void addThisAI()
  {
    func_70661_as().func_75491_a(false);
    this.field_70714_bg.func_75776_a(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.30000001192092896D, 0.3499999940395355D));
    this.field_70714_bg.func_75776_a(2, new EntityAILumberjack(this));
    this.field_70714_bg.func_75776_a(3, new EntityAIRestrictOpenDoor(this));
  }
  
  public void sync()
  {
    if (!this.field_70170_p.field_72995_K) {
      HelpfulVillagers.network.sendToAll(new SaplingPacket(func_145782_y(), this.shouldPlant));
    }
  }
  
  public void func_70071_h_()
  {
    super.func_70071_h_();
    pickupSaplings();
    shouldPlantSapling();
    sync();
    if (this.shouldPlant) {
      plantSapling();
    }
  }
  
  protected void dayCheck()
  {
    super.dayCheck();
    if (this.foundTree) {
      this.foundTree = false;
    } else {
      this.lastResource = null;
    }
  }
  
  private void shouldPlantSapling()
  {
    if ((this.homeVillage != null) && (!this.field_70170_p.field_72995_K)) {
      this.shouldPlant = ((!AIHelper.isInRangeOfAnyVillage(this.field_70165_t, this.field_70163_u, this.field_70161_v)) && (!nearHall()));
    }
  }
  
  private void plantSapling()
  {
    int index = this.inventory.containsItem(new ItemStack(Blocks.field_150345_g));
    if (this.previousTime <= 0) {
      this.previousTime = this.field_70173_aa;
    }
    this.currentTime = this.field_70173_aa;
    if (this.currentTime - this.previousTime >= 200)
    {
      this.previousTime = 0;
      if (index >= 0)
      {
        int y = (int)this.field_70163_u;
        for (;;)
        {
          Block air = this.field_70170_p.func_147439_a((int)this.field_70165_t, y, (int)this.field_70161_v);
          Block dirt = this.field_70170_p.func_147439_a((int)this.field_70165_t, y - 1, (int)this.field_70161_v);
          if ((air.equals(Blocks.field_150350_a)) && ((dirt.equals(Blocks.field_150349_c)) || (dirt.equals(Blocks.field_150346_d))))
          {
            ItemStack saplingItem = this.inventory.func_70301_a(index);
            int metadata = saplingItem.func_77960_j();
            this.field_70170_p.func_147465_d((int)this.field_70165_t, y, (int)this.field_70161_v, Block.func_149634_a(saplingItem.func_77973_b()), metadata, 2);
            this.inventory.decrementSlot(index);
            return;
          }
          if (!air.equals(Blocks.field_150350_a)) {
            return;
          }
          y--;
        }
      }
    }
  }
  
  private void pickupSaplings()
  {
    if (this.inventory.isFull()) {
      return;
    }
    List items = this.field_70170_p.func_72872_a(EntityItem.class, this.searchBox);
    Iterator iterator = items.iterator();
    while (iterator.hasNext())
    {
      EntityItem currentItem = (EntityItem)iterator.next();
      ItemStack currentStack = currentItem.func_92059_d();
      if ((currentStack.func_82833_r() != null) && (currentStack.func_82833_r().contains("Sapling")))
      {
        this.inventory.addItem(currentItem.func_92059_d());
        currentItem.func_70106_y();
      }
    }
  }
  
  public ArrayList getValidCoords()
  {
    updateBoxes();
    
    ArrayList coords = new ArrayList();
    AxisAlignedBB searchBox = this.searchBox;
    for (int x = (int)searchBox.field_72340_a; x <= searchBox.field_72336_d; x++) {
      for (int y = (int)searchBox.field_72338_b; y <= searchBox.field_72337_e; y++) {
        for (int z = (int)searchBox.field_72339_c; z <= searchBox.field_72334_f; z++)
        {
          Block block = this.field_70170_p.func_147439_a(x, y, z);
          if ((block instanceof BlockLog)) {
            coords.add(new ChunkCoordinates(x, y, z));
          }
        }
      }
    }
    return coords;
  }
  
  protected boolean canCraft()
  {
    return true;
  }
  
  public boolean isValidTool(ItemStack item)
  {
    return item.func_77973_b() instanceof ItemAxe;
  }
}

package mods.helpfulvillagers.entity;

import java.util.ArrayList;
import mods.helpfulvillagers.ai.EntityAIFarmer;
import mods.helpfulvillagers.enums.EnumActivity;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.util.ResourceCluster;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class EntityFarmer
  extends AbstractVillager
{
  private final ItemStack[] farmerTools = { new ItemStack(Items.field_151017_I), new ItemStack(Items.field_151018_J), new ItemStack(Items.field_151019_K), new ItemStack(Items.field_151013_M), new ItemStack(Items.field_151012_L) };
  public static final ItemStack[] farmerCraftables = { new ItemStack(Blocks.field_150428_aP), new ItemStack(Blocks.field_150407_cf), new ItemStack(Blocks.field_150440_ba), new ItemStack(Items.field_151105_aU), new ItemStack(Items.field_151025_P), new ItemStack(Items.field_151106_aX), new ItemStack(Items.field_151025_P), new ItemStack(Items.field_151009_A), new ItemStack(Items.field_151025_P), new ItemStack(Items.field_151080_bb), new ItemStack(Items.field_151081_bc), new ItemStack(Items.field_151102_aT), new ItemStack(Items.field_151158_bO), new ItemStack(Items.field_151121_aF), new ItemStack(Items.field_151122_aG) };
  public static final ItemStack[] farmerSmeltables = { new ItemStack(Items.field_151168_bH) };
  public ArrayList<ResourceCluster> visitedFarms = new ArrayList();
  public IPlantable seedToPlant;
  public int lastSeedIndex;
  
  public EntityFarmer(World world)
  {
    super(world);
    init();
  }
  
  public EntityFarmer(AbstractVillager villager)
  {
    super(villager);
    init();
  }
  
  private void init()
  {
    this.profession = 3;
    this.profName = "Farmer";
    this.currentActivity = EnumActivity.IDLE;
    this.searchRadius = 10;
    this.seedToPlant = null;
    this.lastSeedIndex = 0;
    this.knownRecipes.addAll(HelpfulVillagers.farmerRecipes);
    setTools(this.farmerTools);
    getNewGuildHall();
    addThisAI();
  }
  
  private void addThisAI()
  {
    func_70661_as().func_75491_a(false);
    
    this.field_70714_bg.func_75776_a(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.30000001192092896D, 0.3499999940395355D));
    this.field_70714_bg.func_75776_a(2, new EntityAIFarmer(this));
    this.field_70714_bg.func_75776_a(3, new EntityAIRestrictOpenDoor(this));
  }
  
  public int getHarvestTime()
  {
    if (getCurrentItem() == null) {
      return 60;
    }
    if (!(getCurrentItem().func_77973_b() instanceof ItemHoe)) {
      return 60;
    }
    ItemStack hoe = getCurrentItem();
    if (hoe.func_82833_r().equals(new ItemStack(Items.field_151017_I).func_82833_r())) {
      return 50;
    }
    if (hoe.func_82833_r().equals(new ItemStack(Items.field_151018_J).func_82833_r())) {
      return 40;
    }
    if (hoe.func_82833_r().equals(new ItemStack(Items.field_151019_K).func_82833_r())) {
      return 30;
    }
    if (hoe.func_82833_r().equals(new ItemStack(Items.field_151013_M).func_82833_r())) {
      return 20;
    }
    if (hoe.func_82833_r().equals(new ItemStack(Items.field_151012_L).func_82833_r())) {
      return 10;
    }
    return 50;
  }
  
  public void func_70071_h_()
  {
    super.func_70071_h_();
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
          if ((block == Blocks.field_150458_ak) || (block == Blocks.field_150425_aM) || (block == Blocks.field_150436_aH)) {
            coords.add(new ChunkCoordinates(x, y, z));
          }
        }
      }
    }
    return coords;
  }
  
  protected void dayCheck()
  {
    super.dayCheck();
    this.visitedFarms.clear();
  }
  
  protected boolean canCraft()
  {
    return true;
  }
  
  public boolean isValidTool(ItemStack item)
  {
    return item.func_77973_b() instanceof ItemHoe;
  }
}

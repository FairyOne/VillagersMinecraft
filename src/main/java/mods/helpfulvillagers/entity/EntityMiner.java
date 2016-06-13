package mods.helpfulvillagers.entity;

import java.util.ArrayList;
import java.util.Collections;
import mods.helpfulvillagers.ai.EntityAIMiner;
import mods.helpfulvillagers.crafting.VillagerRecipe;
import mods.helpfulvillagers.enums.EnumActivity;
import mods.helpfulvillagers.main.HelpfulVillagers;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class EntityMiner
  extends AbstractVillager
{
  private final ItemStack[] minerTools = { new ItemStack(Items.field_151039_o), new ItemStack(Items.field_151050_s), new ItemStack(Items.field_151035_b), new ItemStack(Items.field_151005_D), new ItemStack(Items.field_151046_w) };
  public static final ItemStack[] minerCraftables = { new ItemStack(Blocks.field_150460_al), new ItemStack(Blocks.field_150333_U), new ItemStack(Blocks.field_150348_b), new ItemStack(Blocks.field_150417_aV), new ItemStack(Blocks.field_150390_bg), new ItemStack(Blocks.field_150446_ar), new ItemStack(Blocks.field_150430_aB), new ItemStack(Blocks.field_150456_au), new ItemStack(Blocks.field_150445_bS), new ItemStack(Blocks.field_150443_bT), new ItemStack(Blocks.field_150442_at), new ItemStack(Blocks.field_150379_bu), new ItemStack(Blocks.field_150429_aA), new ItemStack(Blocks.field_150448_aq), new ItemStack(Blocks.field_150318_D), new ItemStack(Blocks.field_150319_E), new ItemStack(Blocks.field_150408_cc), new ItemStack(Blocks.field_150402_ci), new ItemStack(Blocks.field_150339_S), new ItemStack(Blocks.field_150340_R), new ItemStack(Blocks.field_150451_bX), new ItemStack(Blocks.field_150368_y), new ItemStack(Blocks.field_150475_bE), new ItemStack(Blocks.field_150484_ah), new ItemStack(Blocks.field_150463_bK), new ItemStack(Blocks.field_150331_J), new ItemStack(Blocks.field_150320_F), new ItemStack(Blocks.field_150367_z), new ItemStack(Blocks.field_150409_cd), new ItemStack(Blocks.field_150438_bZ), new ItemStack(Blocks.field_150411_aY), new ItemStack(Blocks.field_150381_bn), new ItemStack(Blocks.field_150467_bQ), new ItemStack(Blocks.field_150478_aa), new ItemStack(Items.field_151139_aw), new ItemStack(Items.field_151028_Y), new ItemStack(Items.field_151030_Z), new ItemStack(Items.field_151165_aa), new ItemStack(Items.field_151167_ab), new ItemStack(Items.field_151036_c), new ItemStack(Items.field_151019_K), new ItemStack(Items.field_151035_b), new ItemStack(Items.field_151037_a), new ItemStack(Items.field_151040_l), new ItemStack(Items.field_151169_ag), new ItemStack(Items.field_151171_ah), new ItemStack(Items.field_151149_ai), new ItemStack(Items.field_151151_aj), new ItemStack(Items.field_151006_E), new ItemStack(Items.field_151013_M), new ItemStack(Items.field_151005_D), new ItemStack(Items.field_151011_C), new ItemStack(Items.field_151010_B), new ItemStack(Items.field_151161_ac), new ItemStack(Items.field_151163_ad), new ItemStack(Items.field_151173_ae), new ItemStack(Items.field_151175_af), new ItemStack(Items.field_151056_x), new ItemStack(Items.field_151012_L), new ItemStack(Items.field_151046_w), new ItemStack(Items.field_151047_v), new ItemStack(Items.field_151048_u), new ItemStack(Items.field_151049_t), new ItemStack(Items.field_151018_J), new ItemStack(Items.field_151050_s), new ItemStack(Items.field_151051_r), new ItemStack(Items.field_151052_q), new ItemStack(Items.field_151033_d), new ItemStack(Items.field_151097_aZ), new ItemStack(Items.field_151133_ar), new ItemStack(Items.field_151113_aN), new ItemStack(Items.field_151111_aL), new ItemStack(Items.field_151107_aW), new ItemStack(Items.field_151132_bS), new ItemStack(Items.field_151143_au), new ItemStack(Items.field_151109_aJ), new ItemStack(Items.field_151108_aI), new ItemStack(Items.field_151140_bW), new ItemStack(Items.field_151142_bV), new ItemStack(Items.field_151066_bu), new ItemStack(Items.field_151067_bt) };
  public static final ItemStack[] minerSmeltables = { new ItemStack(Blocks.field_150348_b), new ItemStack(Items.field_151042_j), new ItemStack(Items.field_151043_k) };
  private final Block[] excludeBlocksArray = { Blocks.field_150350_a, Blocks.field_150354_m, Blocks.field_150351_n, Blocks.field_150352_o, Blocks.field_150366_p, Blocks.field_150365_q, Blocks.field_150369_x, Blocks.field_150482_ag, Blocks.field_150450_ax, Blocks.field_150412_bA };
  public ArrayList excludeBlocks = new ArrayList();
  public ChunkCoordinates target;
  public ArrayList shaftCoords = new ArrayList();
  public ChunkCoordinates topCoords;
  public int topDir;
  public int shaftIndex;
  public ArrayList digCoords = new ArrayList();
  public boolean dugSection;
  public ArrayList tunnelCoords = new ArrayList();
  public ArrayList returnPath = new ArrayList();
  public boolean beingFollowed;
  public boolean swingingPickaxe;
  private int suffocationCount;
  
  public EntityMiner(World world)
  {
    super(world);
    init();
  }
  
  public EntityMiner(AbstractVillager villager)
  {
    super(villager);
    init();
  }
  
  private void init()
  {
    this.target = null;
    this.topCoords = null;
    this.topDir = 0;
    this.shaftIndex = 0;
    this.dugSection = false;
    this.beingFollowed = false;
    this.swingingPickaxe = false;
    this.suffocationCount = 0;
    this.profession = 2;
    this.profName = "Miner";
    this.currentActivity = EnumActivity.IDLE;
    this.searchRadius = 5;
    this.knownRecipes.addAll(HelpfulVillagers.minerRecipes);
    addHorseArmorRecipes();
    setTools(this.minerTools);
    getNewGuildHall();
    addExcludeBlocks();
    addThisAI();
  }
  
  private void addHorseArmorRecipes()
  {
    ArrayList<ItemStack> inputs = new ArrayList();
    inputs.add(new ItemStack(Blocks.field_150325_L, 1));
    inputs.add(new ItemStack(Items.field_151042_j, 6));
    this.knownRecipes.add(new VillagerRecipe(inputs, new ItemStack(Items.field_151138_bX), false));
    
    inputs.set(1, new ItemStack(Items.field_151043_k, 6));
    this.knownRecipes.add(new VillagerRecipe(inputs, new ItemStack(Items.field_151136_bY), false));
    inputs.set(1, new ItemStack(Items.field_151045_i, 6));
    this.knownRecipes.add(new VillagerRecipe(inputs, new ItemStack(Items.field_151125_bZ), false));
    Collections.sort(this.knownRecipes);
  }
  
  private void addExcludeBlocks()
  {
    for (int i = 0; i < this.excludeBlocksArray.length; i++) {
      this.excludeBlocks.add(this.excludeBlocksArray[i]);
    }
  }
  
  private void addThisAI()
  {
    func_70661_as().func_75491_a(true);
    
    this.field_70714_bg.func_75776_a(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.30000001192092896D, 0.3499999940395355D));
    this.field_70714_bg.func_75776_a(2, new EntityAIMiner(this));
    this.field_70714_bg.func_75776_a(3, new EntityAIRestrictOpenDoor(this));
  }
  
  public void func_70071_h_()
  {
    super.func_70071_h_();
    if (func_110143_aJ() >= func_110138_aP()) {
      this.suffocationCount = 0;
    }
    if (this.field_70163_u <= 30.0D) {
      this.searchRadius = 8;
    } else {
      this.searchRadius = 5;
    }
  }
  
  public ArrayList getValidCoords()
  {
    updateBoxes();
    
    ArrayList coords = new ArrayList();
    AxisAlignedBB searchBox = this.searchBox;
    for (int x = (int)searchBox.field_72340_a; x <= searchBox.field_72336_d; x++) {
      for (int z = (int)searchBox.field_72339_c; z <= searchBox.field_72334_f; z++)
      {
        Block block = this.field_70170_p.func_147439_a(x, (int)this.field_70163_u, z);
        int[] oreDictIDs = OreDictionary.getOreIDs(new ItemStack(block));
        for (int j = 0; j < oreDictIDs.length; j++)
        {
          String name = OreDictionary.getOreName(oreDictIDs[j]);
          if (name.contains("ore"))
          {
            coords.add(new ChunkCoordinates(x, (int)this.field_70163_u, z));
            break;
          }
        }
      }
    }
    return coords;
  }
  
  public int nearestShaftCoord()
  {
    for (int i = 0; i < this.shaftCoords.size(); i++)
    {
      ChunkCoordinates currentCoords = (ChunkCoordinates)this.shaftCoords.get(i);
      if (currentCoords.field_71572_b == (int)this.field_70163_u) {
        return i;
      }
    }
    if ((this.topCoords != null) && (getCoords().field_71572_b >= this.topCoords.field_71572_b)) {
      return 0;
    }
    return this.shaftCoords.size() - 1;
  }
  
  public boolean areCoordsInMine(ChunkCoordinates coords)
  {
    for (int i = 0; i < 4; i++)
    {
      ChunkCoordinates check = (ChunkCoordinates)this.shaftCoords.get(i);
      if ((coords.field_71574_a == check.field_71574_a) && (coords.field_71573_c == check.field_71573_c)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isInMine()
  {
    return areCoordsInMine(getCoords());
  }
  
  public boolean func_70097_a(DamageSource src, float f)
  {
    if ((src.equals(DamageSource.field_76368_d)) && (this.shaftCoords.size() > 0))
    {
      this.suffocationCount += 1;
      if (this.suffocationCount > 2)
      {
        ChunkCoordinates dest;
        ChunkCoordinates dest;
        if (nearestShaftCoord() - 1 >= 0) {
          dest = (ChunkCoordinates)this.shaftCoords.get(nearestShaftCoord() - 1);
        } else {
          dest = (ChunkCoordinates)this.shaftCoords.get(nearestShaftCoord());
        }
        if (func_70011_f(dest.field_71574_a, dest.field_71572_b, dest.field_71573_c) <= 5.0D)
        {
          func_70012_b(dest.field_71574_a, dest.field_71572_b, dest.field_71573_c, this.field_70177_z, this.field_70125_A);
          this.suffocationCount = 0;
        }
      }
    }
    return super.func_70097_a(src, f);
  }
  
  public void func_70014_b(NBTTagCompound par1NBTTagCompound)
  {
    super.func_70014_b(par1NBTTagCompound);
    NBTTagList tagList = new NBTTagList();
    if (this.topCoords != null)
    {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      ChunkCoordinates topCoords = this.topCoords;
      int[] coords = { topCoords.field_71574_a, topCoords.field_71572_b, topCoords.field_71573_c };
      nbttagcompound.func_74783_a("Coords", coords);
      tagList.func_74742_a(nbttagcompound);
      
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      nbttagcompound1.func_74768_a("Direction", this.topDir);
      tagList.func_74742_a(nbttagcompound1);
      par1NBTTagCompound.func_74782_a("Mineshaft", tagList);
    }
  }
  
  public void func_70037_a(NBTTagCompound par1NBTTagCompound)
  {
    super.func_70037_a(par1NBTTagCompound);
    if (par1NBTTagCompound.func_74764_b("Mineshaft"))
    {
      NBTTagList nbttaglist = par1NBTTagCompound.func_150295_c("Mineshaft", par1NBTTagCompound.func_74732_a());
      NBTTagCompound nbttagcompound = nbttaglist.func_150305_b(0);
      int[] coords = nbttagcompound.func_74759_k("Coords");
      this.topCoords = new ChunkCoordinates(coords[0], coords[1], coords[2]);
      
      NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(1);
      this.topDir = nbttagcompound1.func_74762_e("Direction");
    }
  }
  
  protected boolean canCraft()
  {
    return true;
  }
  
  public boolean isValidTool(ItemStack item)
  {
    return item.func_77973_b() instanceof ItemPickaxe;
  }
}

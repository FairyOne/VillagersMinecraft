package mods.helpfulvillagers.entity;

import java.util.ArrayList;
import mods.helpfulvillagers.ai.EntityAIGuardVillageArcher;
import mods.helpfulvillagers.enums.EnumActivity;
import mods.helpfulvillagers.inventory.InventoryVillager;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;

public class EntityArcher
  extends AbstractVillager
{
  private final ItemStack[] archerTools = { new ItemStack(Items.field_151031_f) };
  public final int ARROW_TIME = 20;
  
  public EntityArcher(World world)
  {
    super(world);
    init();
  }
  
  public EntityArcher(AbstractVillager villager)
  {
    super(villager);
    init();
  }
  
  private void init()
  {
    this.profession = 5;
    this.profName = "Archer";
    this.currentActivity = EnumActivity.IDLE;
    this.searchRadius = 5;
    setTools(this.archerTools);
    getNewGuildHall();
    addThisAI();
  }
  
  public void addThisAI()
  {
    func_70661_as().func_75491_a(false);
    
    this.field_70714_bg.func_75776_a(2, new EntityAIGuardVillageArcher(this));
  }
  
  public boolean shouldReturn()
  {
    return false;
  }
  
  public void func_70071_h_()
  {
    super.func_70071_h_();
  }
  
  public boolean isFullyArmored()
  {
    for (int i = 28; i < 32; i++)
    {
      ItemStack armorPiece = this.inventory.func_70301_a(i);
      if (armorPiece == null) {
        return false;
      }
    }
    return true;
  }
  
  public ArrayList getValidCoords()
  {
    return null;
  }
  
  protected boolean canCraft()
  {
    return false;
  }
  
  public boolean isValidTool(ItemStack item)
  {
    return item.func_77973_b() instanceof ItemBow;
  }
}

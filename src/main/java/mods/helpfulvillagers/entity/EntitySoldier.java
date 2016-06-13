package mods.helpfulvillagers.entity;

import java.util.ArrayList;
import mods.helpfulvillagers.ai.EntityAIGuardVillageSoldier;
import mods.helpfulvillagers.enums.EnumActivity;
import mods.helpfulvillagers.inventory.InventoryVillager;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;

public class EntitySoldier
  extends AbstractVillager
{
  private final ItemStack[] soldierTools = { new ItemStack(Items.field_151041_m), new ItemStack(Items.field_151052_q), new ItemStack(Items.field_151040_l), new ItemStack(Items.field_151010_B), new ItemStack(Items.field_151048_u) };
  
  public EntitySoldier(World world)
  {
    super(world);
    init();
  }
  
  public EntitySoldier(AbstractVillager villager)
  {
    super(villager);
    init();
  }
  
  private void init()
  {
    this.profession = 4;
    this.profName = "Soldier";
    this.currentActivity = EnumActivity.IDLE;
    setTools(this.soldierTools);
    getNewGuildHall();
    addThisAI();
  }
  
  public void addThisAI()
  {
    func_70661_as().func_75491_a(false);
    
    this.field_70714_bg.func_75776_a(2, new EntityAIGuardVillageSoldier(this));
  }
  
  public boolean shouldReturn()
  {
    return false;
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
  
  public void func_70071_h_()
  {
    super.func_70071_h_();
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
    return item.func_77973_b() instanceof ItemSword;
  }
}

package mods.helpfulvillagers.entity;

import java.util.ArrayList;
import mods.helpfulvillagers.inventory.InventoryVillager;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class EntityRegularVillager
  extends AbstractVillager
{
  private boolean dropFlag;
  
  public EntityRegularVillager(World world)
  {
    super(world);
    init();
  }
  
  public EntityRegularVillager(AbstractVillager villager)
  {
    super(villager);
    init();
  }
  
  private void init()
  {
    this.profession = 0;
    this.profName = "Villager";
    this.homeGuildHall = null;
    this.dropFlag = false;
    if ((!this.field_70170_p.field_72995_K) && (this.homeVillage != null)) {
      func_110171_b(this.homeVillage.getActualCenter().field_71574_a, this.homeVillage.getActualCenter().field_71572_b, this.homeVillage.getActualCenter().field_71573_c, (int)(this.homeVillage.getVillageRadius() / 0.6F));
    }
  }
  
  private void addThisAI()
  {
    func_70661_as().func_75491_a(false);
    this.field_70714_bg.func_75776_a(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.30000001192092896D, 0.3499999940395355D));
    this.field_70714_bg.func_75776_a(3, new EntityAIRestrictOpenDoor(this));
  }
  
  public boolean shouldReturn()
  {
    return true;
  }
  
  public void func_70071_h_()
  {
    super.func_70071_h_();
    if (!this.dropFlag)
    {
      if (getCurrentItem() != null)
      {
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
      this.dropFlag = true;
    }
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
    return false;
  }
}

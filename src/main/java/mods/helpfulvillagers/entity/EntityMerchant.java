package mods.helpfulvillagers.entity;

import java.util.ArrayList;
import mods.helpfulvillagers.enums.EnumActivity;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;

public class EntityMerchant
  extends AbstractVillager
{
  public EntityMerchant(World world)
  {
    super(world);
    init();
  }
  
  public EntityMerchant(AbstractVillager villager)
  {
    super(villager);
    init();
  }
  
  private void init()
  {
    this.profession = 6;
    this.profName = "Merchant";
    this.currentActivity = EnumActivity.IDLE;
    this.searchRadius = 10;
    getNewGuildHall();
    addThisAI();
  }
  
  private void addThisAI()
  {
    func_70661_as().func_75491_a(false);
    
    this.field_70714_bg.func_75776_a(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.30000001192092896D, 0.3499999940395355D));
    this.field_70714_bg.func_75776_a(3, new EntityAIRestrictOpenDoor(this));
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

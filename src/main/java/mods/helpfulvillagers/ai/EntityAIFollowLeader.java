package mods.helpfulvillagers.ai;

import java.util.Random;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.entity.EntityArcher;
import mods.helpfulvillagers.entity.EntitySoldier;
import mods.helpfulvillagers.enums.EnumActivity;
import mods.helpfulvillagers.inventory.InventoryVillager;
import mods.helpfulvillagers.util.AIHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityAIFollowLeader
  extends EntityAIBase
{
  private AbstractVillager villager;
  private EntityLivingBase leader;
  private EntityLivingBase threatTarget;
  private int count;
  private float speed;
  private int previousTime;
  private int currentTime;
  
  public EntityAIFollowLeader(AbstractVillager abstractEntity)
  {
    this.villager = abstractEntity;
    this.speed = 0.8F;
    func_75248_a(1);
  }
  
  public boolean func_75250_a()
  {
    this.leader = this.villager.getLeader();
    return this.leader != null;
  }
  
  public void func_75249_e()
  {
    this.count = 0;
  }
  
  public void func_75246_d()
  {
    if ((AIHelper.findDistance((int)this.villager.field_70165_t, (int)this.leader.field_70165_t) <= 1) && (AIHelper.findDistance((int)this.villager.field_70163_u, (int)this.leader.field_70163_u) <= 1) && (AIHelper.findDistance((int)this.villager.field_70161_v, (int)this.leader.field_70161_v) <= 1))
    {
      this.villager.func_70661_as().func_75499_g();
    }
    else if (--this.count <= 0)
    {
      this.count = 10;
      this.speed = 0.8F;
      this.villager.moveTo(this.leader, this.speed);
    }
    if (((this.villager instanceof EntitySoldier)) || (((this.villager instanceof EntityArcher)) && (this.villager.inventory.containsItem(new ItemStack(Items.field_151032_g)) < 0))) {
      protectLeaderMelee();
    } else if ((this.villager instanceof EntityArcher)) {
      protectLeaderRanged();
    }
  }
  
  public boolean func_75253_b()
  {
    this.leader = this.villager.getLeader();
    if ((this.leader == null) || ((this.leader != null) && (!this.leader.func_70089_S()))) {
      return false;
    }
    return true;
  }
  
  public void func_75251_c()
  {
    this.speed = 0.8F;
    this.villager.func_70661_as().func_75492_a(this.villager.field_70165_t, this.villager.field_70163_u, this.villager.field_70161_v, 0.30000001192092896D);
    this.villager.setLeader(null);
    this.villager.currentActivity = EnumActivity.IDLE;
  }
  
  private void protectLeaderMelee()
  {
    if ((this.villager.func_70643_av() != null) && (this.villager.func_70643_av().func_70089_S()) && ((this.villager.func_70643_av() instanceof IMob))) {
      this.threatTarget = this.villager.func_70643_av();
    } else if ((this.leader.func_70643_av() != null) && (this.leader.func_70643_av().func_70089_S()) && ((this.leader.func_70643_av() instanceof IMob))) {
      this.threatTarget = this.leader.func_70643_av();
    } else {
      this.threatTarget = null;
    }
    if (this.threatTarget != null)
    {
      boolean canMove = this.villager.func_70661_as().func_75497_a(this.threatTarget, this.speed);
      if (!canMove) {
        this.villager.func_70661_as().func_75497_a(this.leader, this.speed);
      }
      if (this.villager.func_70068_e(this.threatTarget) <= 5.0D)
      {
        this.villager.func_70661_as().func_75499_g();
        this.villager.func_71038_i();
        boolean attackSuccess = this.threatTarget.func_70097_a(DamageSource.func_76358_a(this.villager), this.villager.getAttackDamage());
        if (attackSuccess) {
          this.villager.damageItem();
        }
      }
    }
  }
  
  private void protectLeaderRanged()
  {
    if ((this.villager.func_70643_av() != null) && (this.villager.func_70643_av().func_70089_S()) && ((this.villager.func_70643_av() instanceof IMob))) {
      this.threatTarget = this.villager.func_70643_av();
    } else if ((this.leader.func_70643_av() != null) && (this.leader.func_70643_av().func_70089_S()) && ((this.leader.func_70643_av() instanceof IMob))) {
      this.threatTarget = this.leader.func_70643_av();
    } else {
      this.threatTarget = null;
    }
    EntityArcher archer = (EntityArcher)this.villager;
    if (this.threatTarget != null)
    {
      boolean canMove = this.villager.func_70661_as().func_75497_a(this.threatTarget, this.speed);
      if (!canMove) {
        this.villager.func_70661_as().func_75497_a(this.leader, this.speed);
      }
      if (archer.func_70685_l(this.threatTarget))
      {
        archer.func_70661_as().func_75499_g();
        archer.func_70671_ap().func_75651_a(this.threatTarget, 30.0F, 30.0F);
        if (this.previousTime < 0)
        {
          this.previousTime = archer.field_70173_aa;
        }
        else
        {
          archer.getClass();
          if (this.currentTime - this.previousTime >= 20)
          {
            if (!archer.field_70170_p.field_72995_K)
            {
              EntityArrow arrow = new EntityArrow(archer.field_70170_p, archer, this.threatTarget, 1.6F, 2.0F);
              arrow.field_70251_a = 1;
              archer.field_70170_p.func_72838_d(arrow);
            }
            archer.field_70170_p.func_72956_a(archer, "random.bow", 1.0F, 1.0F / (archer.func_70681_au().nextFloat() * 0.4F + 0.8F));
            archer.damageItem();
            archer.inventory.decrementSlot(archer.inventory.containsItem(new ItemStack(Items.field_151032_g)));
            
            this.previousTime = -1;
          }
          else
          {
            this.currentTime = archer.field_70173_aa;
          }
        }
      }
    }
  }
}

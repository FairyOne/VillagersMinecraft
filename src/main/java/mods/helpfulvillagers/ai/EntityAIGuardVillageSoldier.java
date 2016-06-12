package mods.helpfulvillagers.ai;

import java.util.ArrayList;
import java.util.Iterator;
import mods.helpfulvillagers.crafting.CraftItem;
import mods.helpfulvillagers.econ.VillageEconomy;
import mods.helpfulvillagers.entity.EntitySoldier;
import mods.helpfulvillagers.enums.EnumActivity;
import mods.helpfulvillagers.inventory.InventoryVillager;
import mods.helpfulvillagers.util.AIHelper;
import mods.helpfulvillagers.village.GuildHall;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityAIGuardVillageSoldier
  extends EntityAITarget
{
  private EntitySoldier soldier;
  private EntityLivingBase villageAgressorTarget;
  private float speed;
  
  public EntityAIGuardVillageSoldier(EntitySoldier soldier)
  {
    super(soldier, false, false);
    this.soldier = soldier;
    this.speed = 0.75F;
    func_75248_a(2);
  }
  
  public boolean func_75250_a()
  {
    if ((this.soldier.currentActivity == EnumActivity.RETURN) || (this.soldier.currentActivity == EnumActivity.FOLLOW)) {
      return false;
    }
    if (this.soldier.func_110143_aJ() < this.soldier.func_110143_aJ() / 2.0F)
    {
      this.soldier.currentActivity = EnumActivity.STORE;
      return true;
    }
    if ((this.soldier.func_70643_av() != null) && (this.soldier.func_70643_av().func_70089_S()) && ((this.soldier.func_70643_av() instanceof IMob)))
    {
      this.villageAgressorTarget = this.soldier.func_70643_av();
      return true;
    }
    if ((!this.soldier.field_70170_p.field_72995_K) && (this.soldier.homeVillage != null))
    {
      this.villageAgressorTarget = this.soldier.homeVillage.findNearestVillageAggressor(this.soldier);
      if (this.villageAgressorTarget != null) {
        return true;
      }
    }
    if (!this.soldier.hasTool)
    {
      this.soldier.currentActivity = EnumActivity.STORE;
      return true;
    }
    return false;
  }
  
  public void func_75249_e()
  {
    this.soldier.func_70624_b(this.villageAgressorTarget);
    super.func_75249_e();
  }
  
  public boolean func_75253_b()
  {
    if ((this.soldier.currentActivity == EnumActivity.RETURN) || (this.soldier.currentActivity == EnumActivity.FOLLOW)) {
      return false;
    }
    if (this.soldier.currentActivity == EnumActivity.STORE)
    {
      if ((this.soldier.func_110143_aJ() < this.soldier.func_110143_aJ() / 2.0F) || (!this.soldier.hasTool)) {
        return true;
      }
      return false;
    }
    if ((this.villageAgressorTarget != null) && (this.villageAgressorTarget.func_70089_S())) {
      return true;
    }
    return false;
  }
  
  public void func_75246_d()
  {
    if (this.soldier.currentActivity == EnumActivity.STORE) {
      resupply();
    } else {
      attack();
    }
  }
  
  private void resupply()
  {
    if (this.soldier.homeGuildHall == null)
    {
      this.soldier.currentActivity = EnumActivity.IDLE;
      return;
    }
    if (!this.soldier.nearHall())
    {
      this.soldier.currentActivity = EnumActivity.RETURN;
      return;
    }
    this.villageAgressorTarget = this.soldier.homeVillage.findNearestVillageAggressor(this.soldier);
    if ((this.soldier.func_110143_aJ() >= this.soldier.func_110143_aJ() / 2.0F) && (this.villageAgressorTarget != null)) {
      this.soldier.currentActivity = EnumActivity.IDLE;
    }
    if ((!this.soldier.inventory.isEmpty()) || (!this.soldier.hasTool))
    {
      TileEntityChest chest = this.soldier.homeGuildHall.getAvailableChest();
      if (chest != null) {
        this.soldier.moveTo(new ChunkCoordinates(chest.field_145851_c, chest.field_145848_d, chest.field_145849_e), this.speed);
      } else {
        this.soldier.changeGuildHall = true;
      }
      if ((chest != null) && (AIHelper.findDistance((int)this.soldier.field_70165_t, chest.field_145851_c) <= 2) && (AIHelper.findDistance((int)this.soldier.field_70163_u, chest.field_145848_d) <= 2) && (AIHelper.findDistance((int)this.soldier.field_70161_v, chest.field_145849_e) <= 2))
      {
        try
        {
          this.soldier.inventory.dumpInventory(chest);
        }
        catch (NullPointerException e)
        {
          chest.func_70305_f();
        }
        if (!this.soldier.isFullyArmored())
        {
          Iterator iterator = this.soldier.homeGuildHall.guildChests.iterator();
          while ((iterator.hasNext()) && (!this.soldier.isFullyArmored()))
          {
            chest = (TileEntityChest)iterator.next();
            for (int i = 0; i < chest.func_70302_i_(); i++)
            {
              ItemStack chestItem = chest.func_70301_a(i);
              if ((chestItem != null) && ((chestItem.func_77973_b() instanceof ItemArmor)))
              {
                ItemArmor armor = (ItemArmor)chestItem.func_77973_b();
                switch (armor.field_77881_a)
                {
                case 0: 
                  if (this.soldier.inventory.func_70301_a(28) == null) {
                    this.soldier.inventory.swapEquipment(chest, i, 1);
                  }
                  break;
                case 1: 
                  if (this.soldier.inventory.func_70301_a(29) == null) {
                    this.soldier.inventory.swapEquipment(chest, i, 2);
                  }
                  break;
                case 2: 
                  if (this.soldier.inventory.func_70301_a(30) == null) {
                    this.soldier.inventory.swapEquipment(chest, i, 3);
                  }
                  break;
                case 3: 
                  if (this.soldier.inventory.func_70301_a(31) == null) {
                    this.soldier.inventory.swapEquipment(chest, i, 4);
                  }
                  break;
                }
              }
              if (this.soldier.isFullyArmored()) {
                break;
              }
            }
          }
        }
        if (!this.soldier.hasTool)
        {
          Iterator iterator = this.soldier.homeGuildHall.guildChests.iterator();
          while (iterator.hasNext())
          {
            chest = (TileEntityChest)iterator.next();
            int index = AIHelper.chestContains(chest, this.soldier);
            if (index >= 0) {
              this.soldier.inventory.swapEquipment(chest, index, 0);
            }
          }
        }
        if ((!this.soldier.hasTool) && (this.soldier.queuedTool == null))
        {
          int lowestPrice = Integer.MAX_VALUE;
          ItemStack lowestItem = null;
          for (int i = 0; i < this.soldier.getValidTools().length; i++)
          {
            ItemStack item = this.soldier.getValidTools()[i];
            int price = this.soldier.homeVillage.economy.getPrice(item.func_82833_r());
            if ((price < lowestPrice) || (lowestItem == null))
            {
              lowestPrice = price;
              lowestItem = item;
            }
          }
          this.soldier.addCraftItem(new CraftItem(lowestItem, this.soldier));
          this.soldier.queuedTool = lowestItem;
        }
        else if (this.soldier.hasTool)
        {
          this.soldier.queuedTool = null;
        }
      }
    }
  }
  
  private void attack()
  {
    if ((this.soldier.func_70643_av() != null) && (this.soldier.func_70643_av().func_70089_S()) && ((this.soldier.func_70643_av() instanceof IMob)))
    {
      if (this.villageAgressorTarget != this.soldier.func_70643_av()) {
        this.villageAgressorTarget = this.soldier.func_70643_av();
      }
    }
    else if ((this.soldier.homeVillage != null) && (this.soldier.homeVillage.lastAggressor != null) && 
      (this.villageAgressorTarget != this.soldier.homeVillage.lastAggressor) && (this.soldier.homeVillage.lastAggressor.func_70089_S()) && ((this.soldier.homeVillage.lastAggressor instanceof IMob))) {
      this.villageAgressorTarget = this.soldier.homeVillage.lastAggressor;
    }
    this.soldier.moveTo(this.villageAgressorTarget, this.speed);
    if (this.soldier.func_70068_e(this.villageAgressorTarget) <= 5.0D)
    {
      this.soldier.func_70661_as().func_75499_g();
      this.soldier.func_71038_i();
      if ((this.villageAgressorTarget instanceof EntityCreeper))
      {
        boolean attackSuccess = this.villageAgressorTarget.func_70097_a(DamageSource.func_76358_a(this.soldier), 20.0F);
        if (attackSuccess)
        {
          this.soldier.damageItem();
          this.soldier.damageItem();
          this.soldier.damageItem();
        }
      }
      else
      {
        boolean attackSuccess = this.villageAgressorTarget.func_70097_a(DamageSource.func_76358_a(this.soldier), this.soldier.getAttackDamage());
        if (attackSuccess) {
          this.soldier.damageItem();
        }
      }
    }
  }
}

package mods.helpfulvillagers.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import mods.helpfulvillagers.crafting.CraftItem;
import mods.helpfulvillagers.econ.VillageEconomy;
import mods.helpfulvillagers.entity.EntityArcher;
import mods.helpfulvillagers.enums.EnumActivity;
import mods.helpfulvillagers.inventory.InventoryVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.util.AIHelper;
import mods.helpfulvillagers.village.GuildHall;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityAIGuardVillageArcher
  extends EntityAITarget
{
  private EntityArcher archer;
  private EntityLivingBase villageAgressorTarget;
  private float speed;
  private int previousTime;
  private int currentTime;
  
  public EntityAIGuardVillageArcher(EntityArcher archer)
  {
    super(archer, false, false);
    this.archer = archer;
    this.speed = 0.75F;
    this.previousTime = -1;
    this.currentTime = 0;
    func_75248_a(2);
  }
  
  public boolean func_75250_a()
  {
    if ((this.archer.currentActivity == EnumActivity.RETURN) || (this.archer.currentActivity == EnumActivity.FOLLOW)) {
      return false;
    }
    if (this.archer.func_110143_aJ() < this.archer.func_110143_aJ() / 2.0F)
    {
      this.archer.currentActivity = EnumActivity.STORE;
      return true;
    }
    if ((this.archer.func_70643_av() != null) && (this.archer.func_70643_av().func_70089_S()) && ((this.archer.func_70643_av() instanceof IMob)))
    {
      this.villageAgressorTarget = this.archer.func_70643_av();
      return true;
    }
    if ((!this.archer.field_70170_p.field_72995_K) && (this.archer.homeVillage != null))
    {
      this.villageAgressorTarget = this.archer.homeVillage.findNearestVillageAggressor(this.archer);
      if (this.villageAgressorTarget != null) {
        return true;
      }
    }
    if ((!this.archer.hasTool) || (this.archer.inventory.containsItem(new ItemStack(Items.field_151032_g)) < 0))
    {
      this.archer.currentActivity = EnumActivity.STORE;
      return true;
    }
    return false;
  }
  
  public void func_75249_e()
  {
    this.archer.func_70624_b(this.villageAgressorTarget);
    super.func_75249_e();
  }
  
  public boolean func_75253_b()
  {
    if ((this.archer.currentActivity == EnumActivity.RETURN) || (this.archer.currentActivity == EnumActivity.FOLLOW)) {
      return false;
    }
    if (this.archer.currentActivity == EnumActivity.STORE)
    {
      if ((this.archer.func_110143_aJ() < this.archer.func_110143_aJ() / 2.0F) || (!this.archer.hasTool)) {
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
    if (this.archer.currentActivity == EnumActivity.STORE) {
      resupply();
    } else {
      attack();
    }
  }
  
  private void resupply()
  {
    if (this.archer.homeGuildHall == null)
    {
      this.archer.currentActivity = EnumActivity.IDLE;
      return;
    }
    if (!this.archer.nearHall())
    {
      this.archer.currentActivity = EnumActivity.RETURN;
      return;
    }
    this.villageAgressorTarget = this.archer.homeVillage.findNearestVillageAggressor(this.archer);
    if ((this.archer.func_110143_aJ() >= this.archer.func_110143_aJ() / 2.0F) && (this.villageAgressorTarget != null)) {
      this.archer.currentActivity = EnumActivity.IDLE;
    }
    int arrowIndex = this.archer.inventory.containsItem(new ItemStack(Items.field_151032_g));
    if ((!this.archer.inventory.isEmpty()) || (!this.archer.hasTool) || ((arrowIndex < 0) && (!HelpfulVillagers.infiniteArrows)))
    {
      TileEntityChest chest = this.archer.homeGuildHall.getAvailableChest();
      if (chest != null)
      {
        this.archer.moveTo(new ChunkCoordinates(chest.field_145851_c, chest.field_145848_d, chest.field_145849_e), this.speed);
        this.archer.changeGuildHall = false;
      }
      else
      {
        this.archer.changeGuildHall = true;
      }
      if ((chest != null) && (AIHelper.findDistance((int)this.archer.field_70165_t, chest.field_145851_c) <= 2) && (AIHelper.findDistance((int)this.archer.field_70163_u, chest.field_145848_d) <= 2) && (AIHelper.findDistance((int)this.archer.field_70161_v, chest.field_145849_e) <= 2))
      {
        ArrayList arrows = new ArrayList();
        if (!HelpfulVillagers.infiniteArrows) {
          for (int i = 0; i < this.archer.inventory.func_70302_i_(); i++) {
            if ((this.archer.inventory.func_70301_a(i) != null) && (this.archer.inventory.func_70301_a(i).equals(new ItemStack(Items.field_151032_g))))
            {
              arrows.add(this.archer.inventory.func_70301_a(i));
              this.archer.inventory.setMainContents(i, null);
            }
          }
        }
        try
        {
          this.archer.inventory.dumpInventory(chest);
        }
        catch (NullPointerException e)
        {
          chest.func_70305_f();
        }
        if (!HelpfulVillagers.infiniteArrows)
        {
          for (int i = 0; i < arrows.size(); i++) {
            this.archer.inventory.addItem((ItemStack)arrows.get(i));
          }
          arrows.clear();
        }
        if (!this.archer.isFullyArmored())
        {
          Iterator iterator = this.archer.homeGuildHall.guildChests.iterator();
          while ((iterator.hasNext()) && (!this.archer.isFullyArmored()))
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
                  if (this.archer.inventory.func_70301_a(28) == null) {
                    this.archer.inventory.swapEquipment(chest, i, 1);
                  }
                  break;
                case 1: 
                  if (this.archer.inventory.func_70301_a(29) == null) {
                    this.archer.inventory.swapEquipment(chest, i, 2);
                  }
                  break;
                case 2: 
                  if (this.archer.inventory.func_70301_a(30) == null) {
                    this.archer.inventory.swapEquipment(chest, i, 3);
                  }
                  break;
                case 3: 
                  if (this.archer.inventory.func_70301_a(31) == null) {
                    this.archer.inventory.swapEquipment(chest, i, 4);
                  }
                  break;
                }
              }
              if (this.archer.isFullyArmored()) {
                break;
              }
            }
          }
        }
        if (!this.archer.hasTool)
        {
          Iterator iterator = this.archer.homeGuildHall.guildChests.iterator();
          while (iterator.hasNext())
          {
            chest = (TileEntityChest)iterator.next();
            int index = AIHelper.chestContains(chest, this.archer);
            if (index >= 0) {
              this.archer.inventory.swapEquipment(chest, index, 0);
            }
          }
        }
        if ((!this.archer.hasTool) && (this.archer.queuedTool == null))
        {
          int lowestPrice = Integer.MAX_VALUE;
          ItemStack lowestItem = null;
          for (int i = 0; i < this.archer.getValidTools().length; i++)
          {
            ItemStack item = this.archer.getValidTools()[i];
            int price = this.archer.homeVillage.economy.getPrice(item.func_82833_r());
            if ((price < lowestPrice) || (lowestItem == null))
            {
              lowestPrice = price;
              lowestItem = item;
            }
          }
          this.archer.addCraftItem(new CraftItem(lowestItem, this.archer));
          this.archer.queuedTool = lowestItem;
        }
        else if (this.archer.hasTool)
        {
          this.archer.queuedTool = null;
        }
        if ((arrowIndex < 0) && (!HelpfulVillagers.infiniteArrows))
        {
          Iterator iterator = this.archer.homeGuildHall.guildChests.iterator();
          while (iterator.hasNext())
          {
            chest = (TileEntityChest)iterator.next();
            if (AIHelper.chestContains(chest, new ItemStack(Items.field_151032_g)) >= 0)
            {
              int index = AIHelper.chestContains(chest, new ItemStack(Items.field_151032_g));
              this.archer.inventory.swapEquipment(chest, index, 0);
              this.archer.inventory.addItem(chest.func_70301_a(index));
              chest.func_70299_a(index, null);
              this.archer.currentActivity = EnumActivity.IDLE;
            }
          }
        }
      }
    }
  }
  
  private void attack()
  {
    if ((this.archer.func_70643_av() != null) && (this.archer.func_70643_av().func_70089_S()) && ((this.archer.func_70643_av() instanceof IMob)))
    {
      if (this.villageAgressorTarget != this.archer.func_70643_av()) {
        this.villageAgressorTarget = this.archer.func_70643_av();
      }
    }
    else if ((this.archer.homeVillage != null) && (this.archer.homeVillage.lastAggressor != null) && 
      (this.villageAgressorTarget != this.archer.homeVillage.lastAggressor) && (this.archer.homeVillage.lastAggressor.func_70089_S()) && ((this.archer.homeVillage.lastAggressor instanceof IMob))) {
      this.villageAgressorTarget = this.archer.homeVillage.lastAggressor;
    }
    this.archer.moveTo(this.villageAgressorTarget, this.speed);
    if ((this.archer.hasTool) && ((this.archer.inventory.containsItem(new ItemStack(Items.field_151032_g)) >= 0) || (HelpfulVillagers.infiniteArrows)))
    {
      if (this.archer.func_70685_l(this.villageAgressorTarget))
      {
        this.archer.func_70661_as().func_75499_g();
        this.archer.func_70671_ap().func_75651_a(this.villageAgressorTarget, 30.0F, 30.0F);
        if (this.previousTime < 0)
        {
          this.previousTime = this.archer.field_70173_aa;
        }
        else
        {
          this.archer.getClass();
          if (this.currentTime - this.previousTime >= 20)
          {
            if (!this.archer.field_70170_p.field_72995_K)
            {
              EntityArrow arrow = new EntityArrow(this.archer.field_70170_p, this.archer, this.villageAgressorTarget, 1.6F, 2.0F);
              if (!HelpfulVillagers.infiniteArrows) {
                arrow.field_70251_a = 1;
              }
              this.archer.field_70170_p.func_72838_d(arrow);
            }
            this.archer.field_70170_p.func_72956_a(this.archer, "random.bow", 1.0F, 1.0F / (this.archer.func_70681_au().nextFloat() * 0.4F + 0.8F));
            this.archer.damageItem();
            if (!HelpfulVillagers.infiniteArrows) {
              this.archer.inventory.decrementSlot(this.archer.inventory.containsItem(new ItemStack(Items.field_151032_g)));
            }
            this.previousTime = -1;
          }
          else
          {
            this.currentTime = this.archer.field_70173_aa;
          }
        }
      }
    }
    else if (this.archer.func_70068_e(this.villageAgressorTarget) <= 5.0D)
    {
      this.archer.func_70661_as().func_75499_g();
      this.archer.func_71038_i();
      if ((this.villageAgressorTarget instanceof EntityCreeper))
      {
        boolean attackSuccess = this.villageAgressorTarget.func_70097_a(DamageSource.func_76358_a(this.archer), 20.0F);
        if (attackSuccess)
        {
          this.archer.damageItem();
          this.archer.damageItem();
          this.archer.damageItem();
        }
      }
      else
      {
        boolean attackSuccess = this.villageAgressorTarget.func_70097_a(DamageSource.func_76358_a(this.archer), this.archer.getAttackDamage());
        if (attackSuccess) {
          this.archer.damageItem();
        }
      }
    }
  }
}

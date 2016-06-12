package mods.helpfulvillagers.ai;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import java.util.ArrayList;
import java.util.Random;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.enums.EnumMessage;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.network.PlayerMessagePacket;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityAIVillagerMateCustom
  extends EntityAIBase
{
  private AbstractVillager villagerObj;
  private AbstractVillager mate;
  private World worldObj;
  private int matingTimeout;
  private Random gen = new Random();
  private HelpfulVillage villageObj;
  
  public EntityAIVillagerMateCustom(AbstractVillager villager)
  {
    this.villagerObj = villager;
    this.worldObj = villager.field_70170_p;
    func_75248_a(3);
  }
  
  public boolean func_75250_a()
  {
    if (!this.villagerObj.shouldReproduce()) {
      return false;
    }
    if (this.villagerObj.func_70874_b() != 0) {
      return false;
    }
    this.villageObj = this.villagerObj.homeVillage;
    if ((this.villageObj == null) || (this.worldObj.field_72995_K)) {
      return false;
    }
    if (!checkSufficientHallssPresentForNewVillager()) {
      return false;
    }
    Entity entity = this.worldObj.func_72857_a(AbstractVillager.class, this.villagerObj.field_70121_D.func_72314_b(8.0D, 3.0D, 8.0D), this.villagerObj);
    AbstractVillager entityV = (AbstractVillager)entity;
    if ((entity == null) || (!entity.func_70089_S()) || (entityV.func_70631_g_())) {
      return false;
    }
    this.mate = ((AbstractVillager)entity);
    return (this.mate.func_70874_b() == 0) && ((this.mate.leader == null) || (this.mate.leader == this.villagerObj));
  }
  
  public void func_75249_e()
  {
    this.matingTimeout = (this.gen.nextInt(50) + 300);
    this.villagerObj.func_70947_e(true);
    this.mate.func_70947_e(true);
  }
  
  public void func_75251_c()
  {
    this.villagerObj.func_70947_e(false);
    this.mate.func_70947_e(false);
    this.villageObj = null;
    this.mate = null;
  }
  
  public boolean func_75253_b()
  {
    return (this.villagerObj.shouldReproduce()) && (this.matingTimeout >= 0) && (checkSufficientHallssPresentForNewVillager()) && (this.villagerObj.func_70874_b() == 0) && (this.mate != null) && (this.mate.func_70089_S());
  }
  
  public void func_75246_d()
  {
    this.matingTimeout -= 1;
    this.villagerObj.func_70671_ap().func_75651_a(this.mate, 10.0F, 30.0F);
    if (this.villagerObj.func_70068_e(this.mate) > 2.25D) {
      this.villagerObj.func_70661_as().func_75497_a(this.mate, 0.25D);
    } else if ((this.matingTimeout == 0) && (this.mate.func_70941_o())) {
      giveBirth();
    }
    if (this.villagerObj.func_70681_au().nextInt(35) == 0) {
      this.worldObj.func_72960_a(this.villagerObj, (byte)12);
    }
  }
  
  private boolean checkSufficientHallssPresentForNewVillager()
  {
    int i;
    int i;
    if ((this.villageObj.guildHallList != null) && (this.villageObj.guildHallList.size() <= 0)) {
      i = 3;
    } else {
      i = this.villageObj.guildHallList.size() * 2 + 1;
    }
    return this.villageObj.getPopulation() < i;
  }
  
  private void giveBirth()
  {
    this.mate.func_70873_a(6000);
    this.villagerObj.func_70873_a(6000);
    if (!this.villagerObj.shouldReproduce()) {
      return;
    }
    int children = 1;
    int prob;
    int prob;
    if (bedCheck()) {
      prob = this.gen.nextInt(100);
    } else {
      prob = this.gen.nextInt(1000);
    }
    if (prob <= 20)
    {
      children++;
      if (prob <= 10) {
        children++;
      }
    }
    for (int i = 0; i < children; i++)
    {
      EntityVillager entityvillager = this.villagerObj.func_90011_a(this.mate);
      entityvillager.func_70873_a(41536);
      entityvillager.func_70012_b(this.villagerObj.field_70165_t, this.villagerObj.field_70163_u, this.villagerObj.field_70161_v, 0.0F, 0.0F);
      this.worldObj.func_72838_d(entityvillager);
      this.worldObj.func_72960_a(entityvillager, (byte)12);
    }
    String message;
    String message;
    String message;
    String message;
    switch (children)
    {
    case 1: 
      message = "A villager was born";
      break;
    case 2: 
      message = "Twin villagers were born";
      break;
    case 3: 
      message = "Triplet villagers were born";
      break;
    default: 
      message = "A villager was born";
    }
    HelpfulVillagers.network.sendToAll(new PlayerMessagePacket(message, EnumMessage.BIRTH, this.villagerObj.func_145782_y()));
  }
  
  private boolean bedCheck()
  {
    AxisAlignedBB box = this.villagerObj.field_70121_D.func_72314_b(3.0D, 3.0D, 3.0D);
    for (int i = (int)box.field_72340_a; i < box.field_72336_d; i++) {
      for (int j = (int)box.field_72338_b; j < box.field_72337_e; j++) {
        for (int k = (int)box.field_72339_c; k < box.field_72334_f; k++)
        {
          Block block = this.villagerObj.field_70170_p.func_147439_a(i, j, k);
          if ((block instanceof BlockBed)) {
            return true;
          }
        }
      }
    }
    return false;
  }
}

package mods.helpfulvillagers.ai;

import java.util.Random;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.entity.EntityArcher;
import mods.helpfulvillagers.entity.EntitySoldier;
import mods.helpfulvillagers.enums.EnumActivity;
import mods.helpfulvillagers.util.AIHelper;
import mods.helpfulvillagers.village.GuildHall;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

public class EntityAIMoveIndoorsCustom
  extends EntityAIBase
{
  private AbstractVillager entityObj;
  private VillageDoorInfo doorInfo;
  private int insidePosX = -1;
  private int insidePosZ = -1;
  protected BlockDoor targetDoor;
  private ChunkCoordinates destination;
  private Random gen;
  private float speed;
  
  public EntityAIMoveIndoorsCustom(AbstractVillager abstractEntity)
  {
    this.entityObj = abstractEntity;
    func_75248_a(1);
    this.gen = new Random();
    this.speed = 0.5F;
  }
  
  public boolean func_75250_a()
  {
    if ((this.entityObj.homeGuildHall != null) && (this.entityObj.currentActivity == EnumActivity.RETURN))
    {
      this.destination = new ChunkCoordinates(this.entityObj.homeGuildHall.doorCoords.field_71574_a, this.entityObj.homeGuildHall.doorCoords.field_71572_b, this.entityObj.homeGuildHall.doorCoords.field_71573_c);
      return !this.entityObj.nearHall();
    }
    if ((!(this.entityObj instanceof EntitySoldier)) && (!(this.entityObj instanceof EntityArcher)) && ((!this.entityObj.field_70170_p.func_72935_r()) || (this.entityObj.field_70170_p.func_72896_J())) && (!this.entityObj.field_70170_p.field_73011_w.field_76576_e))
    {
      if (this.entityObj.func_70681_au().nextInt(50) != 0) {
        return false;
      }
      if ((this.insidePosX != -1) && (this.entityObj.func_70092_e(this.insidePosX, this.entityObj.field_70163_u, this.insidePosZ) < 4.0D)) {
        return false;
      }
      if (this.entityObj.homeVillage == null) {
        return false;
      }
      this.doorInfo = this.entityObj.homeVillage.findNearestDoorUnrestricted(MathHelper.func_76128_c(this.entityObj.field_70165_t), MathHelper.func_76128_c(this.entityObj.field_70163_u), MathHelper.func_76128_c(this.entityObj.field_70161_v));
      return this.doorInfo != null;
    }
    return false;
  }
  
  public boolean func_75253_b()
  {
    if (!this.entityObj.homeVillage.isInsideVillage(this.entityObj.field_70165_t, this.entityObj.field_70163_u, this.entityObj.field_70161_v)) {
      this.speed = 0.75F;
    } else {
      this.speed = 0.5F;
    }
    if ((this.entityObj.homeGuildHall != null) && (this.entityObj.currentActivity == EnumActivity.RETURN))
    {
      if (this.entityObj.shouldReturn())
      {
        this.entityObj.currentActivity = EnumActivity.IDLE;
        return false;
      }
      ChunkCoordinates currentPosition = new ChunkCoordinates((int)this.entityObj.field_70165_t, (int)this.entityObj.field_70163_u, (int)this.entityObj.field_70161_v);
      
      this.entityObj.moveTo(this.destination, this.speed);
      if (this.entityObj.nearHall())
      {
        int distX = AIHelper.findDistance((int)this.entityObj.field_70165_t, this.destination.field_71574_a);
        int distZ = AIHelper.findDistance((int)this.entityObj.field_70161_v, this.destination.field_71573_c);
        if ((distX < 1) || (distZ < 1) || (this.entityObj.func_70661_as().func_75500_f()))
        {
          this.entityObj.currentActivity = EnumActivity.IDLE;
          return false;
        }
        return true;
      }
      return true;
    }
    return !this.entityObj.func_70661_as().func_75500_f();
  }
  
  public void func_75249_e()
  {
    this.insidePosX = -1;
    if ((this.entityObj.homeGuildHall != null) && (this.entityObj.currentActivity == EnumActivity.RETURN))
    {
      if (new ChunkCoordinates((int)this.entityObj.field_70165_t + 1, (int)this.entityObj.field_70163_u, (int)this.entityObj.field_70161_v).equals(this.entityObj.homeGuildHall.doorCoords)) {
        this.targetDoor = findUsableDoor((int)this.entityObj.field_70165_t + 1, (int)this.entityObj.field_70163_u, (int)this.entityObj.field_70161_v);
      } else if (new ChunkCoordinates((int)this.entityObj.field_70165_t - 1, (int)this.entityObj.field_70163_u, (int)this.entityObj.field_70161_v).equals(this.entityObj.homeGuildHall.doorCoords)) {
        this.targetDoor = findUsableDoor((int)this.entityObj.field_70165_t - 1, (int)this.entityObj.field_70163_u, (int)this.entityObj.field_70161_v);
      } else if (new ChunkCoordinates((int)this.entityObj.field_70165_t, (int)this.entityObj.field_70163_u, (int)this.entityObj.field_70161_v + 1).equals(this.entityObj.homeGuildHall.doorCoords)) {
        this.targetDoor = findUsableDoor((int)this.entityObj.field_70165_t, (int)this.entityObj.field_70163_u, (int)this.entityObj.field_70161_v + 1);
      } else if (new ChunkCoordinates((int)this.entityObj.field_70165_t, (int)this.entityObj.field_70163_u, (int)this.entityObj.field_70161_v - 1).equals(this.entityObj.homeGuildHall.doorCoords)) {
        this.targetDoor = findUsableDoor((int)this.entityObj.field_70165_t, (int)this.entityObj.field_70163_u, (int)this.entityObj.field_70161_v - 1);
      }
    }
    else if (this.entityObj.func_70092_e(this.doorInfo.func_75471_a(), this.doorInfo.field_75479_b, this.doorInfo.func_75472_c()) > 256.0D)
    {
      Vec3 vec3 = RandomPositionGenerator.func_75464_a(this.entityObj, 14, 3, Vec3.func_72443_a(this.doorInfo.func_75471_a() + 0.5D, this.doorInfo.func_75473_b(), this.doorInfo.func_75472_c() + 0.5D));
      if (vec3 != null) {
        this.entityObj.func_70661_as().func_75492_a(vec3.field_72450_a, vec3.field_72448_b, vec3.field_72449_c, this.speed);
      }
    }
    else
    {
      this.entityObj.func_70661_as().func_75492_a(this.doorInfo.func_75471_a() + 0.5D, this.doorInfo.func_75473_b(), this.doorInfo.func_75472_c() + 0.5D, this.speed);
    }
  }
  
  public void func_75251_c()
  {
    if (this.entityObj.homeGuildHall == null)
    {
      if ((!this.entityObj.field_70170_p.field_72995_K) && (this.entityObj.homeVillage != null))
      {
        this.doorInfo = this.entityObj.homeVillage.findNearestDoorUnrestricted(MathHelper.func_76128_c(this.entityObj.field_70165_t), MathHelper.func_76128_c(this.entityObj.field_70163_u), MathHelper.func_76128_c(this.entityObj.field_70161_v));
        if (this.doorInfo != null)
        {
          this.insidePosX = this.doorInfo.func_75471_a();
          this.insidePosZ = this.doorInfo.func_75472_c();
          this.doorInfo = null;
        }
      }
    }
    else {
      this.entityObj.func_70661_as().func_75492_a(this.entityObj.homeVillage.getActualCenter().field_71574_a, this.entityObj.homeVillage.getActualCenter().field_71572_b, this.entityObj.homeVillage.getActualCenter().field_71573_c, this.speed);
    }
  }
  
  private BlockDoor findUsableDoor(int par1, int par2, int par3)
  {
    Block l = this.entityObj.field_70170_p.func_147439_a(par1, par2, par3);
    return l != Blocks.field_150466_ao ? null : (BlockDoor)l;
  }
}

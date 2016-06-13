package mods.helpfulvillagers.village;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class GuildHall
{
  private HelpfulVillage village;
  public World worldObj;
  public EntityItemFrame itemFrame;
  public ChunkCoordinates doorCoords;
  public ChunkCoordinates entranceCoords;
  public ArrayList<ChunkCoordinates> insideCoords = new ArrayList();
  public int typeNum;
  public ArrayList guildChests = new ArrayList();
  public ArrayList guildFurnaces = new ArrayList();
  
  public GuildHall() {}
  
  public GuildHall(World world, HelpfulVillage village)
  {
    this.worldObj = world;
    this.village = village;
    this.itemFrame = null;
    this.doorCoords = null;
    this.entranceCoords = null;
    this.typeNum = -1;
  }
  
  public void findCoords(int profession, List itemFrames)
  {
    Iterator iterator = itemFrames.iterator();
    while (iterator.hasNext())
    {
      Entity entity = (Entity)iterator.next();
      if ((entity instanceof EntityItemFrame))
      {
        EntityItemFrame itemFrame = (EntityItemFrame)entity;
        if ((itemFrame.func_82335_i() != null) && 
          (matchesProfession(itemFrame, profession)) && 
          (isNextToDoor(itemFrame)))
        {
          this.itemFrame = itemFrame;
          this.typeNum = profession;
          try
          {
            fillInsideCoords();
            findEntranceCoords();
          }
          catch (StackOverflowError e)
          {
            this.insideCoords.clear();
          }
        }
      }
    }
  }
  
  private boolean matchesProfession(EntityItemFrame itemFrame, int profession)
  {
    ItemStack itemStack = itemFrame.func_82335_i();
    if (itemStack == null) {
      return false;
    }
    switch (profession)
    {
    case 1: 
      return itemStack.func_77973_b() instanceof ItemAxe;
    case 2: 
      return itemStack.func_77973_b() instanceof ItemPickaxe;
    case 3: 
      return itemStack.func_77973_b() instanceof ItemHoe;
    case 4: 
      return itemStack.func_77973_b() instanceof ItemSword;
    case 5: 
      return itemStack.func_77973_b() instanceof ItemBow;
    case 6: 
      return itemStack.func_77973_b().equals(Items.field_151166_bC);
    }
    return false;
  }
  
  private boolean isNextToDoor(Entity entity)
  {
    int posX = (int)entity.field_70165_t;
    int posY = (int)entity.field_70163_u;
    int posZ = (int)entity.field_70161_v;
    boolean doorFlag = false;
    
    this.doorCoords = new ChunkCoordinates(posX, posY, posZ);
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        for (int k = 0; k < 3; k++) {
          if (this.worldObj.func_147439_a(posX + i, posY + j, posZ + k) == Blocks.field_150466_ao)
          {
            this.doorCoords.func_71571_b(posX + i, posY + j, posZ + k);
            doorFlag = true;
          }
          else if (this.worldObj.func_147439_a(posX + i, posY + j, posZ - k) == Blocks.field_150466_ao)
          {
            this.doorCoords.func_71571_b(posX + i, posY + j, posZ - k);
            doorFlag = true;
          }
          else if (this.worldObj.func_147439_a(posX + i, posY - j, posZ - k) == Blocks.field_150466_ao)
          {
            this.doorCoords.func_71571_b(posX + i, posY - j, posZ - k);
            doorFlag = true;
          }
          else if (this.worldObj.func_147439_a(posX + i, posY - j, posZ + k) == Blocks.field_150466_ao)
          {
            this.doorCoords.func_71571_b(posX + i, posY - j, posZ + k);
            doorFlag = true;
          }
          else if (this.worldObj.func_147439_a(posX - i, posY + j, posZ + k) == Blocks.field_150466_ao)
          {
            this.doorCoords.func_71571_b(posX - i, posY + j, posZ + k);
            doorFlag = true;
          }
          else if (this.worldObj.func_147439_a(posX - i, posY + j, posZ - k) == Blocks.field_150466_ao)
          {
            this.doorCoords.func_71571_b(posX - i, posY + j, posZ - k);
            doorFlag = true;
          }
          else if (this.worldObj.func_147439_a(posX - i, posY - j, posZ + k) == Blocks.field_150466_ao)
          {
            this.doorCoords.func_71571_b(posX - i, posY - j, posZ + k);
            doorFlag = true;
          }
          else if (this.worldObj.func_147439_a(posX - i, posY - j, posZ - k) == Blocks.field_150466_ao)
          {
            this.doorCoords.func_71571_b(posX - i, posY - j, posZ - k);
            doorFlag = true;
          }
        }
      }
    }
    if (doorFlag)
    {
      if (entity.field_70170_p.func_147439_a(this.doorCoords.field_71574_a, this.doorCoords.field_71572_b - 1, this.doorCoords.field_71573_c) == Blocks.field_150466_ao) {
        this.doorCoords.func_71571_b(this.doorCoords.field_71574_a, this.doorCoords.field_71572_b - 1, this.doorCoords.field_71573_c);
      }
    }
    else {
      this.doorCoords = null;
    }
    return doorFlag;
  }
  
  private void fillInsideCoords()
  {
    this.insideCoords.add(this.doorCoords);
    switch (this.itemFrame.field_82332_a)
    {
    case 0: 
      ChunkCoordinates startCoords = new ChunkCoordinates(this.doorCoords.field_71574_a, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c - 1);
      ChunkCoordinates entranceCoords = new ChunkCoordinates(this.doorCoords.field_71574_a, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c + 1);
      this.insideCoords.add(entranceCoords);
      checkZDirection(startCoords, -1);
      break;
    case 1: 
      ChunkCoordinates startCoords = new ChunkCoordinates(this.doorCoords.field_71574_a + 1, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c);
      ChunkCoordinates entranceCoords = new ChunkCoordinates(this.doorCoords.field_71574_a - 1, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c);
      this.insideCoords.add(entranceCoords);
      checkXDirection(startCoords, 1);
      break;
    case 2: 
      ChunkCoordinates startCoords = new ChunkCoordinates(this.doorCoords.field_71574_a, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c + 1);
      ChunkCoordinates entranceCoords = new ChunkCoordinates(this.doorCoords.field_71574_a, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c - 1);
      this.insideCoords.add(entranceCoords);
      checkZDirection(startCoords, 1);
      break;
    case 3: 
      ChunkCoordinates startCoords = new ChunkCoordinates(this.doorCoords.field_71574_a - 1, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c);
      ChunkCoordinates entranceCoords = new ChunkCoordinates(this.doorCoords.field_71574_a + 1, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c);
      this.insideCoords.add(entranceCoords);
      checkXDirection(startCoords, -1);
      break;
    default: 
      ChunkCoordinates startCoords = null;
      System.out.println("START COORDS NULL");
    }
  }
  
  private void checkXDirection(ChunkCoordinates currentCoords, int direction)
  {
    boolean addFlag = false;
    if ((!this.insideCoords.contains(currentCoords)) && 
      (isInside(currentCoords))) {
      if (direction < 0)
      {
        if (((!this.worldObj.isSideSolid(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c, ForgeDirection.WEST)) && 
          (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150359_w) && 
          (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150410_aZ) && 
          (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150399_cn) && 
          (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150397_co)) || 
          ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) instanceof BlockWorkbench)) || 
          ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) instanceof BlockFurnace))) {
          addFlag = true;
        }
      }
      else if (((!this.worldObj.isSideSolid(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c, ForgeDirection.EAST)) && 
        (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150359_w) && 
        (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150410_aZ) && 
        (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150399_cn) && 
        (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150397_co)) || 
        ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) instanceof BlockWorkbench)) || 
        ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) instanceof BlockFurnace))) {
        addFlag = true;
      }
    }
    if (addFlag)
    {
      this.insideCoords.add(currentCoords);
      
      ChunkCoordinates nextXCoords = new ChunkCoordinates(currentCoords.field_71574_a + direction, currentCoords.field_71572_b, currentCoords.field_71573_c);
      ChunkCoordinates negYCoords = new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b - 1, currentCoords.field_71573_c);
      ChunkCoordinates posYCoords = new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b + 1, currentCoords.field_71573_c);
      ChunkCoordinates negZCoords = new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c - 1);
      ChunkCoordinates posZCoords = new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c + 1);
      checkXDirection(nextXCoords, direction);
      checkYDirection(negYCoords, -1);
      checkYDirection(posYCoords, 1);
      checkZDirection(negZCoords, -1);
      checkZDirection(posZCoords, 1);
    }
  }
  
  private void checkYDirection(ChunkCoordinates currentCoords, int direction)
  {
    boolean addFlag = false;
    if ((!this.insideCoords.contains(currentCoords)) && 
      (isInside(currentCoords))) {
      if (direction < 0)
      {
        if (((!this.worldObj.isSideSolid(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c, ForgeDirection.UP)) && 
          (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150359_w) && 
          (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150410_aZ) && 
          (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150399_cn) && 
          (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150397_co)) || 
          ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) instanceof BlockWorkbench)) || 
          ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) instanceof BlockFurnace))) {
          addFlag = true;
        }
      }
      else if (((!this.worldObj.isSideSolid(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c, ForgeDirection.DOWN)) && 
        (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150359_w) && 
        (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150410_aZ) && 
        (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150399_cn) && 
        (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150397_co)) || 
        ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) instanceof BlockWorkbench)) || 
        ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) instanceof BlockFurnace))) {
        addFlag = true;
      }
    }
    if (addFlag)
    {
      this.insideCoords.add(currentCoords);
      
      ChunkCoordinates nextYCoords = new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b + direction, currentCoords.field_71573_c);
      ChunkCoordinates negXCoords = new ChunkCoordinates(currentCoords.field_71574_a - 1, currentCoords.field_71572_b, currentCoords.field_71573_c);
      ChunkCoordinates posXCoords = new ChunkCoordinates(currentCoords.field_71574_a + 1, currentCoords.field_71572_b, currentCoords.field_71573_c + 1);
      ChunkCoordinates negZCoords = new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c - 1);
      ChunkCoordinates posZCoords = new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c + 1);
      checkYDirection(nextYCoords, direction);
      checkXDirection(negXCoords, -1);
      checkXDirection(posXCoords, 1);
      checkZDirection(negZCoords, -1);
      checkZDirection(posZCoords, 1);
    }
  }
  
  private void checkZDirection(ChunkCoordinates currentCoords, int direction)
  {
    boolean addFlag = false;
    if ((!this.insideCoords.contains(currentCoords)) && 
      (isInside(currentCoords))) {
      if (direction < 0)
      {
        if (((!this.worldObj.isSideSolid(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c, ForgeDirection.SOUTH)) && 
          (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150359_w) && 
          (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150410_aZ) && 
          (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150399_cn) && 
          (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150397_co)) || 
          ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) instanceof BlockWorkbench)) || 
          ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) instanceof BlockFurnace))) {
          addFlag = true;
        }
      }
      else if (((!this.worldObj.isSideSolid(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c, ForgeDirection.NORTH)) && 
        (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150359_w) && 
        (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150410_aZ) && 
        (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150399_cn) && 
        (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) != Blocks.field_150397_co)) || 
        ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) instanceof BlockWorkbench)) || 
        ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) instanceof BlockFurnace))) {
        addFlag = true;
      }
    }
    if (addFlag)
    {
      this.insideCoords.add(currentCoords);
      
      ChunkCoordinates nextZCoords = new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c + direction);
      ChunkCoordinates negXCoords = new ChunkCoordinates(currentCoords.field_71574_a - 1, currentCoords.field_71572_b, currentCoords.field_71573_c);
      ChunkCoordinates posXCoords = new ChunkCoordinates(currentCoords.field_71574_a + 1, currentCoords.field_71572_b, currentCoords.field_71573_c);
      ChunkCoordinates negYCoords = new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b - 1, currentCoords.field_71573_c);
      ChunkCoordinates posYCoords = new ChunkCoordinates(currentCoords.field_71574_a, currentCoords.field_71572_b + 1, currentCoords.field_71573_c);
      checkXDirection(nextZCoords, direction);
      checkYDirection(negYCoords, -1);
      checkYDirection(posYCoords, 1);
      checkZDirection(negXCoords, -1);
      checkZDirection(posXCoords, 1);
    }
  }
  
  private void findEntranceCoords()
  {
    if ((!isInside(new ChunkCoordinates(this.doorCoords.field_71574_a + 3, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c))) && (this.worldObj.func_147437_c(this.doorCoords.field_71574_a + 3, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c)))
    {
      this.entranceCoords = new ChunkCoordinates(this.doorCoords.field_71574_a + 3, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c);
      return;
    }
    if ((!isInside(new ChunkCoordinates(this.doorCoords.field_71574_a - 3, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c))) && (this.worldObj.func_147437_c(this.doorCoords.field_71574_a - 3, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c)))
    {
      this.entranceCoords = new ChunkCoordinates(this.doorCoords.field_71574_a - 3, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c);
      return;
    }
    if ((!isInside(new ChunkCoordinates(this.doorCoords.field_71574_a, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c + 3))) && (this.worldObj.func_147437_c(this.doorCoords.field_71574_a, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c + 3)))
    {
      this.entranceCoords = new ChunkCoordinates(this.doorCoords.field_71574_a, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c + 3);
      return;
    }
    if ((!isInside(new ChunkCoordinates(this.doorCoords.field_71574_a, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c - 3))) && (this.worldObj.func_147437_c(this.doorCoords.field_71574_a, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c - 3)))
    {
      this.entranceCoords = new ChunkCoordinates(this.doorCoords.field_71574_a, this.doorCoords.field_71572_b, this.doorCoords.field_71573_c - 3);
      return;
    }
  }
  
  private boolean isInside(ChunkCoordinates currentCoords)
  {
    if (!this.worldObj.func_72937_j(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c)) {
      return true;
    }
    for (int i = currentCoords.field_71572_b + 1; i < 256; i++) {
      if (!this.worldObj.func_147437_c(currentCoords.field_71574_a, i, currentCoords.field_71573_c)) {
        return true;
      }
    }
    return false;
  }
  
  public ChunkCoordinates getFrameCoords()
  {
    return new ChunkCoordinates((int)this.itemFrame.field_70165_t, (int)this.itemFrame.field_70163_u, (int)this.itemFrame.field_70161_v);
  }
  
  public void checkFrame()
  {
    if (!this.worldObj.field_72995_K) {
      if ((this.itemFrame != null) && ((!this.itemFrame.func_70089_S()) || (!matchesProfession(this.itemFrame, this.typeNum))))
      {
        this.itemFrame = null;
        this.village.guildHallList.remove(this);
        this.village.unlockedHalls[(this.typeNum - 1)] = false;
      }
      else if (this.itemFrame == null)
      {
        this.village.guildHallList.remove(this);
        this.village.unlockedHalls[(this.typeNum - 1)] = false;
      }
    }
  }
  
  public void checkChests()
  {
    this.guildChests.clear();
    Iterator iterator = this.insideCoords.iterator();
    while (iterator.hasNext())
    {
      ChunkCoordinates currentCoords = (ChunkCoordinates)iterator.next();
      if ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) == Blocks.field_150486_ae) || (this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) == Blocks.field_150447_bR))
      {
        TileEntityChest chest = (TileEntityChest)this.worldObj.func_147438_o(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c);
        if (!this.guildChests.contains(chest)) {
          this.guildChests.add(chest);
        }
      }
    }
  }
  
  public void checkFurnaces()
  {
    this.guildFurnaces.clear();
    Iterator iterator = this.insideCoords.iterator();
    while (iterator.hasNext())
    {
      ChunkCoordinates currentCoords = (ChunkCoordinates)iterator.next();
      if ((this.worldObj.func_147439_a(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c) instanceof BlockFurnace))
      {
        TileEntityFurnace furnace = (TileEntityFurnace)this.worldObj.func_147438_o(currentCoords.field_71574_a, currentCoords.field_71572_b, currentCoords.field_71573_c);
        if (!this.guildFurnaces.contains(furnace)) {
          this.guildFurnaces.add(furnace);
        }
      }
    }
  }
  
  public boolean hasWorkbench()
  {
    Iterator i = this.insideCoords.iterator();
    while (i.hasNext())
    {
      ChunkCoordinates coords = (ChunkCoordinates)i.next();
      Block block = this.worldObj.func_147439_a(coords.field_71574_a, coords.field_71572_b, coords.field_71573_c);
      if ((block instanceof BlockWorkbench)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean equals(Object object)
  {
    if ((object instanceof GuildHall))
    {
      GuildHall guildHall = (GuildHall)object;
      if ((this != null) && (guildHall != null) && 
        (this.typeNum == guildHall.typeNum) && 
        (this.doorCoords.equals(guildHall.doorCoords))) {
        return true;
      }
    }
    return false;
  }
  
  public int getTypeNum()
  {
    return this.typeNum;
  }
  
  public void setTypeNum(int typeNum)
  {
    this.typeNum = typeNum;
  }
  
  public boolean typeMatchesName(String name)
  {
    if (name.contains("Lumberjack")) {
      return this.typeNum == 1;
    }
    if (name.contains("Miner")) {
      return this.typeNum == 2;
    }
    if (name.contains("Farmer")) {
      return this.typeNum == 3;
    }
    if (name.contains("Soldier")) {
      return this.typeNum == 4;
    }
    if (name.contains("Archer")) {
      return this.typeNum == 5;
    }
    if (name.contains("Merchant")) {
      return this.typeNum == 6;
    }
    return false;
  }
  
  public TileEntityChest getAvailableChest()
  {
    checkChests();
    Iterator iterator = this.guildChests.iterator();
    while (iterator.hasNext())
    {
      TileEntityChest chest = (TileEntityChest)iterator.next();
      int size = chest.func_70302_i_();
      for (int i = 0; i < size; i++) {
        if (chest.func_70301_a(i) == null) {
          return chest;
        }
      }
    }
    return null;
  }
  
  public TileEntityFurnace getAvailableFurnace()
  {
    checkFurnaces();
    Iterator iterator = this.guildFurnaces.iterator();
    while (iterator.hasNext())
    {
      TileEntityFurnace furnace = (TileEntityFurnace)iterator.next();
      if (furnace.func_70301_a(0) == null) {
        return furnace;
      }
    }
    return null;
  }
}

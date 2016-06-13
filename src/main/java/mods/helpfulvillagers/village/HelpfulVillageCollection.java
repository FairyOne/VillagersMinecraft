package mods.helpfulvillagers.village;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class HelpfulVillageCollection
  extends WorldSavedData
{
  static final String key = "HelpfulVillageCollection";
  private ArrayList<HelpfulVillage> villageList = new ArrayList();
  
  public HelpfulVillageCollection(String tagName)
  {
    super("HelpfulVillageCollection");
  }
  
  public HelpfulVillageCollection()
  {
    super("HelpfulVillageCollection");
  }
  
  public static HelpfulVillageCollection forWorld(World world)
  {
    if (world.field_72995_K) {
      return null;
    }
    MapStorage storage = world.perWorldStorage;
    HelpfulVillageCollection result = (HelpfulVillageCollection)storage.func_75742_a(HelpfulVillageCollection.class, "HelpfulVillageCollection");
    if (result == null)
    {
      result = new HelpfulVillageCollection();
      storage.func_75745_a("HelpfulVillageCollection", result);
    }
    return result;
  }
  
  public ArrayList<HelpfulVillage> getVillages()
  {
    return this.villageList;
  }
  
  public void setVillages(ArrayList<HelpfulVillage> villages)
  {
    this.villageList.clear();
    this.villageList.addAll(villages);
    func_76185_a();
  }
  
  public boolean isEmpty()
  {
    return this.villageList.isEmpty();
  }
  
  public void func_76184_a(NBTTagCompound p_76184_1_)
  {
    NBTTagList nbttaglist = p_76184_1_.func_150295_c("Villages", 10);
    for (int i = 0; i < nbttaglist.func_74745_c(); i++)
    {
      NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
      HelpfulVillage village = new HelpfulVillage();
      village.readVillageDataFromNBT(nbttagcompound1);
      this.villageList.add(village);
    }
  }
  
  public void func_76187_b(NBTTagCompound p_76187_1_)
  {
    NBTTagList nbttaglist = new NBTTagList();
    Iterator iterator = this.villageList.iterator();
    while (iterator.hasNext())
    {
      HelpfulVillage village = (HelpfulVillage)iterator.next();
      if (!village.isAnnihilated)
      {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        village.writeVillageDataToNBT(nbttagcompound1);
        nbttaglist.func_74742_a(nbttagcompound1);
      }
    }
    p_76187_1_.func_74782_a("Villages", nbttaglist);
  }
}

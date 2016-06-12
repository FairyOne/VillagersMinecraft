package mods.helpfulvillagers.econ;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import mods.helpfulvillagers.crafting.VillagerRecipe;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.main.HelpfulVillagers;
import mods.helpfulvillagers.network.ItemPriceClientPacket;
import mods.helpfulvillagers.network.ItemPriceServerPacket;
import mods.helpfulvillagers.village.HelpfulVillage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class VillageEconomy
{
  private HelpfulVillage village;
  private HashMap<String, ArrayList<VillagerRecipe>> recipeMap = new HashMap();
  private HashMap<String, ItemStack> searchMap = new HashMap();
  private HashMap<String, ItemPrice> itemPrices = new HashMap();
  private HashMap<String, Integer> accountMap = new HashMap();
  private int lowestWoodPrice = Integer.MAX_VALUE;
  
  public VillageEconomy() {}
  
  public VillageEconomy(HelpfulVillage village, boolean init)
  {
    this.village = village;
    if (init) {
      initPrices();
    }
  }
  
  private void initPrices()
  {
    this.village.priceCalcStarted = true;
    final HashMap<ItemStack, Integer> itemMap = new HashMap();
    Thread pricesThread = new Thread()
    {
      public void run()
      {
        ArrayList<Item> outputs = new ArrayList();
        List<IRecipe> recipes = CraftingManager.func_77594_a().func_77592_b();
        for (int i = 0; i < recipes.size(); i++)
        {
          ItemStack outputStack = ((IRecipe)recipes.get(i)).func_77571_b();
          if (outputStack != null)
          {
            Item outputItem = outputStack.func_77973_b();
            if (!outputs.contains(outputItem)) {
              outputs.add(outputItem);
            }
          }
        }
        for (int y = 0; y < VillageEconomy.this.village.villageBounds.field_72337_e; y++) {
          for (int x = (int)VillageEconomy.this.village.villageBounds.field_72340_a; x < VillageEconomy.this.village.villageBounds.field_72336_d; x++)
          {
            ItemStack blockStack;
            for (int z = (int)VillageEconomy.this.village.villageBounds.field_72339_c; z < VillageEconomy.this.village.villageBounds.field_72334_f; z++)
            {
              try
              {
                block = VillageEconomy.this.village.world.func_147439_a(x, y, z);
              }
              catch (Exception e)
              {
                Block block;
                VillageEconomy.this.village.priceCalcStarted = false;
                return;
              }
              block = VillageEconomy.this.village.world.func_147439_a(x, y, z);
              ArrayList<ItemStack> items = block.getDrops(VillageEconomy.this.village.world, 0, 0, 0, VillageEconomy.this.village.world.func_72805_g(x, y, z), 0);
              if (block.canSilkHarvest(VillageEconomy.this.village.world, null, x, y, z, VillageEconomy.this.village.world.func_72805_g(x, y, z)))
              {
                blockStack = new ItemStack(Item.func_150898_a(block), 1, VillageEconomy.this.village.world.func_72805_g(x, y, z));
                items.add(blockStack);
              }
              for (ItemStack item : items)
              {
                try
                {
                  if (item.func_82833_r().contains("Bedrock")) {
                    continue;
                  }
                }
                catch (NullPointerException e) {}
                continue;
                if (!outputs.contains(item.func_77973_b()))
                {
                  boolean found = false;
                  for (ItemStack mapItem : itemMap.keySet()) {
                    if (ItemStack.func_77989_b(mapItem, item))
                    {
                      int val = ((Integer)itemMap.get(mapItem)).intValue();
                      itemMap.put(mapItem, Integer.valueOf(val + 1));
                      found = true;
                    }
                  }
                  if (!found) {
                    itemMap.put(item, Integer.valueOf(1));
                  }
                }
              }
            }
          }
        }
        int total = 0;
        int highestAmount = 0;
        int lowestAmount = Integer.MAX_VALUE;
        for (ItemStack item : itemMap.keySet())
        {
          int amount = ((Integer)itemMap.get(item)).intValue();
          if (amount > highestAmount) {
            highestAmount = amount;
          }
          if (amount < lowestAmount) {
            lowestAmount = amount;
          }
        }
        for (Block block = itemMap.entrySet().iterator(); block.hasNext();)
        {
          entry = (Map.Entry)block.next();
          int price = VillageEconomy.this.calcItemValue(highestAmount, lowestAmount, 100, 1, ((Integer)entry.getValue()).intValue());
          VillageEconomy.this.itemPrices.put(((ItemStack)entry.getKey()).func_82833_r(), new ItemPrice((ItemStack)entry.getKey(), price));
          
          Block block = Block.func_149634_a(((ItemStack)entry.getKey()).func_77973_b());
          if ((price > 0) && ((block instanceof BlockLog)) && 
            (price < VillageEconomy.this.lowestWoodPrice)) {
            VillageEconomy.this.lowestWoodPrice = price;
          }
        }
        Map.Entry<ItemStack, Integer> entry;
        ArrayList<ItemStack> outputStacks = new ArrayList();
        for (Item i : outputs) {
          if (i.func_77614_k())
          {
            ArrayList<String> names = new ArrayList();
            for (int j = 0; j < 64; j++)
            {
              ItemStack item = new ItemStack(i, 1, j);
              try
              {
                if (names.contains(item.func_82833_r())) {
                  break;
                }
                names.add(item.func_82833_r());
                if (!VillageEconomy.this.itemPrices.containsKey(item.func_82833_r())) {
                  outputStacks.add(item);
                }
              }
              catch (Exception e)
              {
                if (!VillageEconomy.this.itemPrices.containsKey(i.func_77658_a()))
                {
                  item = new ItemStack(i, 1, 0);
                  outputStacks.add(item);
                }
                break;
              }
            }
          }
          else
          {
            ItemStack item = new ItemStack(i, 1, 0);
            if (!VillageEconomy.this.itemPrices.containsKey(item.func_82833_r())) {
              outputStacks.add(item);
            }
          }
        }
        VillageEconomy.this.setupRecipes();
        for (ItemStack item : outputStacks) {
          VillageEconomy.this.calcValueFromRecipe(item, VillageEconomy.this.village);
        }
        ItemStack emerald = new ItemStack(Items.field_151166_bC);
        if (VillageEconomy.this.itemPrices.containsKey(emerald.func_82833_r())) {
          VillageEconomy.this.itemPrices.remove(emerald.func_82833_r());
        }
        if (VillageEconomy.this.itemPrices.containsKey(new ItemStack(Items.field_151116_aA).func_82833_r())) {
          VillageEconomy.this.itemPrices.put(new ItemStack(Items.field_151141_av).func_82833_r(), new ItemPrice(new ItemStack(Items.field_151141_av), ((ItemPrice)VillageEconomy.this.itemPrices.get(new ItemStack(Items.field_151116_aA))).getOriginalPrice() * 5));
        }
        ItemStack wool = new ItemStack(Blocks.field_150325_L);
        ItemStack iron = new ItemStack(Items.field_151042_j);
        ItemStack gold = new ItemStack(Items.field_151043_k);
        ItemStack diamond = new ItemStack(Items.field_151045_i);
        if (VillageEconomy.this.itemPrices.containsKey(iron.func_82833_r()))
        {
          ItemStack ironArmor = new ItemStack(Items.field_151138_bX);
          VillageEconomy.this.itemPrices.put(ironArmor.func_82833_r(), new ItemPrice(ironArmor, ((ItemPrice)VillageEconomy.this.itemPrices.get(iron.func_82833_r())).getOriginalPrice() * 6));
        }
        if (VillageEconomy.this.itemPrices.containsKey(gold.func_82833_r()))
        {
          ItemStack goldArmor = new ItemStack(Items.field_151136_bY);
          VillageEconomy.this.itemPrices.put(goldArmor.func_82833_r(), new ItemPrice(goldArmor, ((ItemPrice)VillageEconomy.this.itemPrices.get(gold.func_82833_r())).getOriginalPrice() * 6));
        }
        if (VillageEconomy.this.itemPrices.containsKey(diamond.func_82833_r()))
        {
          ItemStack diamondArmor = new ItemStack(Items.field_151125_bZ);
          VillageEconomy.this.itemPrices.put(diamondArmor.func_82833_r(), new ItemPrice(diamondArmor, ((ItemPrice)VillageEconomy.this.itemPrices.get(diamond.func_82833_r())).getOriginalPrice() * 6));
        }
        VillageEconomy.this.village.pricesCalculated = true;
      }
    };
    pricesThread.start();
  }
  
  private int calcItemValue(int highestAmount, int lowestAmount, int maxPrice, int minPrice, int amount)
  {
    double x1 = Math.log(lowestAmount);
    double x2 = Math.log(highestAmount);
    double y1 = Math.log(maxPrice);
    double y2 = Math.log(minPrice);
    
    double slopeNum = (x1 - x2) * (y1 - y2);
    double slopeDen = (x1 - x2) * (x1 - x2);
    double slope = slopeNum / slopeDen;
    
    int result = (int)(100.0D * Math.pow(amount, slope));
    
    return Math.max(result, 1);
  }
  
  private void setupRecipes()
  {
    List<IRecipe> recipes = CraftingManager.func_77594_a().func_77592_b();
    VillagerRecipe recipe;
    for (int i = 0; i < recipes.size(); i++)
    {
      ItemStack outputItem = ((IRecipe)recipes.get(i)).func_77571_b();
      if (outputItem != null)
      {
        recipe = new VillagerRecipe((IRecipe)recipes.get(i), false);
        if (recipe.getOutput() != null) {
          if (!this.recipeMap.containsKey(outputItem.func_82833_r()))
          {
            ArrayList<VillagerRecipe> vRecipes = new ArrayList();
            vRecipes.add(recipe);
            this.recipeMap.put(outputItem.func_82833_r(), vRecipes);
          }
          else
          {
            ((ArrayList)this.recipeMap.get(outputItem.func_82833_r())).add(recipe);
          }
        }
      }
    }
    Map map = FurnaceRecipes.func_77602_a().func_77599_b();
    Set<Map.Entry> entrySet = map.entrySet();
    for (Map.Entry entry : entrySet)
    {
      ItemStack outputItem = (ItemStack)entry.getValue();
      
      VillagerRecipe recipe = new VillagerRecipe((ItemStack)entry.getKey(), outputItem, true);
      if (recipe.getOutput() != null) {
        if (!this.recipeMap.containsKey(outputItem.func_82833_r()))
        {
          ArrayList<VillagerRecipe> vRecipes = new ArrayList();
          vRecipes.add(recipe);
          this.recipeMap.put(outputItem.func_82833_r(), vRecipes);
        }
        else
        {
          ((ArrayList)this.recipeMap.get(outputItem.func_82833_r())).add(recipe);
        }
      }
    }
  }
  
  private int calcValueFromRecipe(ItemStack i, HelpfulVillage village)
  {
    if (this.itemPrices.containsKey(i.func_82833_r()))
    {
      Block block = Block.func_149634_a(i.func_77973_b());
      if ((block instanceof BlockLog)) {
        return this.lowestWoodPrice;
      }
      return ((ItemPrice)this.itemPrices.get(i.func_82833_r())).getPrice();
    }
    if (!this.recipeMap.containsKey(i.func_82833_r())) {
      return -1;
    }
    this.searchMap.put(i.func_82833_r(), i);
    
    int price = 0;
    int lowestPrice = Integer.MAX_VALUE;
    ArrayList<VillagerRecipe> recipes = (ArrayList)this.recipeMap.get(i.func_82833_r());
    for (VillagerRecipe recipe : recipes)
    {
      for (ItemStack stack : recipe.getTotalInputs())
      {
        if (this.searchMap.containsKey(stack.func_82833_r()))
        {
          price = 0;
          break;
        }
        int val = calcValueFromRecipe(stack, village) * stack.field_77994_a;
        if (val < 0)
        {
          price = -1;
          break;
        }
        price += val;
      }
      if (price > 0)
      {
        if (recipe.getOutput().field_77994_a > 0) {
          price /= recipe.getOutput().field_77994_a;
        }
        if (price <= 0) {
          price = 1;
        }
        if (price < lowestPrice) {
          lowestPrice = price;
        }
      }
      price = 0;
    }
    this.searchMap.remove(i.func_82833_r());
    if (lowestPrice < Integer.MAX_VALUE)
    {
      this.itemPrices.put(i.func_82833_r(), new ItemPrice(i, lowestPrice));
      return lowestPrice;
    }
    return -1;
  }
  
  public HashMap<String, ItemPrice> getItemPrices()
  {
    return this.itemPrices;
  }
  
  public int getPrice(String name)
  {
    ItemPrice itemPrice = (ItemPrice)this.itemPrices.get(name);
    if (itemPrice != null) {
      return itemPrice.getPrice();
    }
    return -1;
  }
  
  public ItemPrice getItemPrice(String name)
  {
    return (ItemPrice)this.itemPrices.get(name);
  }
  
  public void putItemPrice(ItemPrice itemPrice)
  {
    this.itemPrices.put(itemPrice.getItem().func_82833_r(), itemPrice);
  }
  
  public boolean hasItem(ItemStack item)
  {
    return this.itemPrices.containsKey(item.func_82833_r());
  }
  
  public void accountDeposit(EntityPlayer player, int amount)
  {
    String username = player.func_70005_c_();
    if (this.accountMap.containsKey(username))
    {
      int currAmount = ((Integer)this.accountMap.get(username)).intValue();
      this.accountMap.put(username, Integer.valueOf(currAmount + amount));
    }
    else
    {
      this.accountMap.put(username, Integer.valueOf(amount));
    }
  }
  
  public int accountWithdraw(EntityPlayer player, int amount)
  {
    String username = player.func_70005_c_();
    if (this.accountMap.containsKey(username))
    {
      int currAmount = ((Integer)this.accountMap.get(username)).intValue();
      if (currAmount >= amount)
      {
        this.accountMap.put(username, Integer.valueOf(currAmount - amount));
        return amount;
      }
      return -1;
    }
    return -1;
  }
  
  public int getAccount(EntityPlayer player)
  {
    String username = player.func_70005_c_();
    if (this.accountMap.containsKey(username)) {
      return ((Integer)this.accountMap.get(username)).intValue();
    }
    return -1;
  }
  
  public void setAccount(EntityPlayer player, int amount)
  {
    this.accountMap.put(player.func_70005_c_(), Integer.valueOf(amount));
  }
  
  public void decreaseAllDemand()
  {
    for (Map.Entry<String, ItemPrice> entry : this.itemPrices.entrySet()) {
      ((ItemPrice)entry.getValue()).decreaseDemand(0.005D);
    }
  }
  
  public void increaseItemSupply(AbstractVillager villager, ItemStack item)
  {
    ItemPrice itemPrice = (ItemPrice)this.itemPrices.get(item.func_82833_r());
    if (itemPrice != null)
    {
      itemPrice.increaseSupply(item.field_77994_a);
    }
    else
    {
      generateNewPrice(item);
      itemPrice = (ItemPrice)this.itemPrices.get(item.func_82833_r());
      itemPrice.increaseSupply(item.field_77994_a);
    }
    try
    {
      if (!this.village.world.field_72995_K) {
        itemSyncClient(villager, null, item);
      }
    }
    catch (NullPointerException e) {}
  }
  
  public void decreaseItemSupply(AbstractVillager villager, ItemStack item)
  {
    ItemPrice itemPrice = (ItemPrice)this.itemPrices.get(item.func_82833_r());
    if (itemPrice != null)
    {
      itemPrice.decreaseSupply(item.field_77994_a);
    }
    else
    {
      generateNewPrice(item);
      itemPrice = (ItemPrice)this.itemPrices.get(item.func_82833_r());
      itemPrice.decreaseSupply(item.field_77994_a);
    }
    try
    {
      if (!this.village.world.field_72995_K) {
        itemSyncClient(villager, null, item);
      }
    }
    catch (NullPointerException e) {}
  }
  
  private void generateNewPrice(ItemStack item)
  {
    if (item == null) {
      return;
    }
    Random rand = new Random();
    int sum = 0;
    int highest = Integer.MIN_VALUE;
    for (Map.Entry<String, ItemPrice> entry : this.itemPrices.entrySet()) {
      if ((entry != null) && (entry.getValue() != null))
      {
        int price = ((ItemPrice)entry.getValue()).getPrice();
        sum += price;
        if (price > highest) {
          highest = price;
        }
      }
    }
    int average = sum / this.itemPrices.size();
    int range = (highest - average) / 2;
    int newPrice = rand.nextInt(range) + average;
    
    this.itemPrices.put(item.func_82833_r(), new ItemPrice(item, newPrice));
  }
  
  public void fullSyncClient(AbstractVillager villager, EntityPlayer player)
  {
    if (player == null) {
      for (Map.Entry<String, ItemPrice> entry : this.itemPrices.entrySet()) {
        HelpfulVillagers.network.sendToAll(new ItemPriceClientPacket(villager, (ItemPrice)entry.getValue()));
      }
    } else {
      for (Map.Entry<String, ItemPrice> entry : this.itemPrices.entrySet()) {
        HelpfulVillagers.network.sendTo(new ItemPriceClientPacket(villager, (ItemPrice)entry.getValue()), (EntityPlayerMP)player);
      }
    }
  }
  
  public void fullSyncServer(AbstractVillager villager)
  {
    for (Map.Entry<String, ItemPrice> entry : this.itemPrices.entrySet()) {
      HelpfulVillagers.network.sendToServer(new ItemPriceServerPacket(villager, (ItemPrice)entry.getValue()));
    }
  }
  
  public void itemSyncClient(AbstractVillager villager, EntityPlayer player, ItemStack item)
  {
    if (player == null) {
      HelpfulVillagers.network.sendToAll(new ItemPriceClientPacket(villager, (ItemPrice)this.itemPrices.get(item.func_82833_r())));
    } else {
      HelpfulVillagers.network.sendTo(new ItemPriceClientPacket(villager, (ItemPrice)this.itemPrices.get(item.func_82833_r())), (EntityPlayerMP)player);
    }
  }
  
  public void itemSyncServer(AbstractVillager villager, ItemStack item)
  {
    HelpfulVillagers.network.sendToServer(new ItemPriceServerPacket(villager, (ItemPrice)this.itemPrices.get(item.func_82833_r())));
  }
  
  public NBTBase writeToNBT(NBTTagList nbtTagList)
  {
    for (Map.Entry<String, ItemPrice> entry : this.itemPrices.entrySet())
    {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.func_74778_a("Name", (String)entry.getKey());
      nbttagcompound.func_74782_a("Item", ((ItemPrice)entry.getValue()).writeToNBT(new NBTTagCompound()));
      nbtTagList.func_74742_a(nbttagcompound);
    }
    for (Map.Entry<String, Integer> entry : this.accountMap.entrySet())
    {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.func_74778_a("Player", (String)entry.getKey());
      nbttagcompound.func_74768_a("Amount", ((Integer)entry.getValue()).intValue());
      nbtTagList.func_74742_a(nbttagcompound);
    }
    return nbtTagList;
  }
  
  public void readFromNBT(NBTTagList nbttaglist)
  {
    for (int i = 0; i < nbttaglist.func_74745_c(); i++)
    {
      NBTTagCompound nbttagcompound = nbttaglist.func_150305_b(i);
      if (nbttagcompound.func_74764_b("Name"))
      {
        String name = nbttagcompound.func_74779_i("Name");
        NBTTagCompound priceCompound = nbttagcompound.func_74775_l("Item");
        ItemPrice itemPrice = ItemPrice.loadCraftItemFromNBT(priceCompound);
        this.itemPrices.put(name, itemPrice);
      }
      else
      {
        String player = nbttagcompound.func_74779_i("Player");
        int amount = nbttagcompound.func_74762_e("Amount");
        this.accountMap.put(player, Integer.valueOf(amount));
      }
    }
  }
}

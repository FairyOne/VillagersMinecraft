package mods.helpfulvillagers.crafting;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import mods.helpfulvillagers.entity.AbstractVillager;
import mods.helpfulvillagers.inventory.InventoryVillager;
import mods.helpfulvillagers.util.AIHelper;
import net.minecraft.item.ItemStack;

public class CraftTree
{
  private Node root;
  private AbstractVillager villager;
  
  public CraftTree(ItemStack itemStack, AbstractVillager villager)
  {
    this.root = new Node();
    this.root.itemStack = itemStack;
    this.root.children = new ArrayList();
    this.root.inputs = new ArrayList();
    this.villager = villager;
    populateTree(this.root);
  }
  
  private void populateTree(Node node)
  {
    VillagerRecipe recipe = this.villager.getRecipe(node.itemStack);
    if (node.itemStack.func_82833_r().equals(this.villager.currentCraftItem.getItem().func_82833_r())) {
      this.villager.currentCraftItem.setSensitivity(recipe.getMetadataSensitivity());
    }
    if (recipe != null)
    {
      if (recipe.isSmelted()) {
        AIHelper.mergeItemStackArrays(node.itemStack, this.villager.materialsNeeded);
      }
      int multiplier = (int)Math.ceil(node.itemStack.field_77994_a / recipe.getOutput().field_77994_a);
      int leftover = recipe.getOutput().field_77994_a * multiplier - node.itemStack.field_77994_a;
      node.leftover = leftover;
      this.villager.craftChain.add(0, node);
      
      ArrayList<ItemStack> inputs = new ArrayList();
      for (ItemStack i : recipe.getTotalInputs())
      {
        int stackSize = i.field_77994_a;
        int multipliedSize = stackSize * multiplier;
        int maxSize = i.func_77976_d();
        int numMax = multipliedSize / maxSize;
        for (int j = 0; j < numMax; j++)
        {
          inputs.add(new ItemStack(i.func_77973_b(), maxSize));
          multipliedSize -= maxSize;
        }
        if (multipliedSize > 0) {
          inputs.add(new ItemStack(i.func_77973_b(), multipliedSize));
        }
      }
      node.inputs.addAll(inputs);
      for (ItemStack i : inputs)
      {
        boolean checkChests = true;
        ItemStack currItem = i.func_77946_l();
        this.villager.inventory.storeAsCollected(currItem, recipe.isSmelted());
        if ((currItem != null) && (currItem.field_77994_a > 0))
        {
          this.villager.lookForItem(currItem);
          this.villager.inventory.storeAsCollected(currItem, recipe.isSmelted());
          if ((currItem != null) && (currItem.field_77994_a > 0)) {
            addChild(node, currItem.func_77946_l(), recipe.isSmelted());
          }
        }
      }
      for (Node n : node.children) {
        populateTree(n);
      }
    }
    else if (node.smelt)
    {
      AIHelper.mergeItemStackArrays(node.itemStack, this.villager.smeltablesNeeded);
    }
    else
    {
      AIHelper.mergeItemStackArrays(node.itemStack, this.villager.materialsNeeded);
    }
  }
  
  private void addChild(Node parent, ItemStack itemStack, boolean smelt)
  {
    Node child = new Node();
    child.itemStack = itemStack;
    child.smelt = smelt;
    child.parent = parent;
    child.children = new ArrayList();
    child.inputs = new ArrayList();
    Node.access$500(child).children.add(child);
  }
  
  public void traverseTree()
  {
    this.root.traverseTree();
  }
  
  public static class Node
  {
    private ItemStack itemStack;
    private boolean smelt;
    private int leftover;
    private Node parent;
    private List<Node> children;
    private List<ItemStack> inputs;
    
    private void traverseTree()
    {
      System.out.println(this.itemStack);
      for (Node i : this.children) {
        i.traverseTree();
      }
    }
    
    public ItemStack getItemStack()
    {
      return this.itemStack;
    }
    
    public Node getParent()
    {
      return this.parent;
    }
    
    public ArrayList<ItemStack> getInputs()
    {
      return new ArrayList(this.inputs);
    }
    
    public int getLeftover()
    {
      return this.leftover;
    }
    
    public boolean isSmelted()
    {
      return this.smelt;
    }
    
    public String toString()
    {
      return this.itemStack.toString();
    }
  }
}

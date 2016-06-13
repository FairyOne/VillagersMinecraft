package mods.helpfulvillagers.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.helpfulvillagers.entity.AbstractVillager;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderVillagerCustom
  extends RenderBiped
{
  private static final ResourceLocation villagerTextures = new ResourceLocation("minecraft:textures/entity/villager/villager.png");
  private static final ResourceLocation villagerTexturesCustom = new ResourceLocation("helpfulvillagers", "textures/entity/villager/villager.png");
  private static final ResourceLocation lumberjackTexturesCustom = new ResourceLocation("helpfulvillagers", "textures/entity/villager/lumberjack.png");
  private static final ResourceLocation minerTexturesCustom = new ResourceLocation("helpfulvillagers", "textures/entity/villager/miner.png");
  private static final ResourceLocation farmerTexturesCustom = new ResourceLocation("helpfulvillagers", "textures/entity/villager/farmer.png");
  private static final ResourceLocation soldierTexturesCustom = new ResourceLocation("helpfulvillagers", "textures/entity/villager/soldier.png");
  private static final ResourceLocation archerTexturesCustom = new ResourceLocation("helpfulvillagers", "textures/entity/villager/archer.png");
  private static final ResourceLocation merchantTexturesCustom = new ResourceLocation("helpfulvillagers", "textures/entity/villager/merchant.png");
  
  public RenderVillagerCustom()
  {
    super(new ModelBiped(0.0F), 0.5F);
  }
  
  protected ResourceLocation func_110775_a(Entity entity)
  {
    return textureChanger(((AbstractVillager)entity).func_70946_n());
  }
  
  protected ResourceLocation textureChanger(int profession)
  {
    switch (profession)
    {
    case 0: 
      return villagerTexturesCustom;
    case 1: 
      return lumberjackTexturesCustom;
    case 2: 
      return minerTexturesCustom;
    case 3: 
      return farmerTexturesCustom;
    case 4: 
      return soldierTexturesCustom;
    case 5: 
      return archerTexturesCustom;
    case 6: 
      return merchantTexturesCustom;
    }
    return villagerTexturesCustom;
  }
}

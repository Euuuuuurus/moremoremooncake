package com.moremoremooncake.jei;

import com.moremooncake.mooncake.mooncake.MooncakeState;
import com.moremoremooncake.Moremoremooncake;
import com.moremoremooncake.jei.MoremoremooncakeJeiPlugin.DoubleFillingJeiRecipe;
import com.moremoremooncake.mooncake.DoubleFlavor;
import com.moremoremooncake.registry.Registry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Shows all 66 two-filling combinations at a glance: the representative ingredient of each
 * filling plus the resulting double mooncake slice. The visual language matches the base mod's
 * JEI categories (same slot positions and dimensions).
 */
public class DoubleFillingCategory implements IRecipeCategory<DoubleFillingJeiRecipe> {
    private final IDrawable icon;

    public DoubleFillingCategory(IGuiHelper helper) {
        icon = helper.createDrawableItemStack(
                new ItemStack(Registry.getItem(DoubleFlavor.ALL.get(0), MooncakeState.NORMAL)));
    }

    @Override
    public RecipeType<DoubleFillingJeiRecipe> getRecipeType() {
        return MoremoremooncakeJeiPlugin.DOUBLE_FILLING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei." + Moremoremooncake.MOD_ID + ".double_filling");
    }

    @Override
    public int getWidth() {
        return 116;
    }

    @Override
    public int getHeight() {
        return 54;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DoubleFillingJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19).addItemStack(recipe.first());
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 19).addItemStack(recipe.second());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 19).addItemStack(recipe.output());
    }
}

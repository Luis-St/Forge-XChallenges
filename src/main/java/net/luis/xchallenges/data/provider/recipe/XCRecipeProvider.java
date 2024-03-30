/*
 * XChallenges
 * Copyright (C) 2024 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.xchallenges.data.provider.recipe;

import net.luis.xchallenges.XChallenges;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class XCRecipeProvider extends RecipeProvider {
	
	public XCRecipeProvider(@NotNull DataGenerator generator) {
		super(generator.getPackOutput());
	}
	
	protected static @NotNull String getId(@NotNull ItemLike item) {
		return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item.asItem())).getPath();
	}
	
	protected static @NotNull String getGroup(ItemLike item) {
		String path = getId(item);
		if (!path.contains("_")) {
			return path;
		}
		return path.split("_")[0];
	}
	
	@Override
	protected void buildRecipes(@NotNull RecipeOutput output) {}
	
	protected void furnaceRecipe(RecipeOutput output, Item input, Item result) {
		this.groupAndUnlock(SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, result, 1.0F, 200), getGroup(input), input, result).save(output, new ResourceLocation(XChallenges.MOD_ID, getId(result)));
	}
	
	protected @NotNull RecipeBuilder groupAndUnlock(RecipeBuilder builder, String group, ItemLike @NotNull ... unlockCriterions) {
		for (ItemLike unlockCriterion : unlockCriterions) {
			builder.unlockedBy("has_" + getId(unlockCriterion), has(unlockCriterion));
		}
		return builder.group(group);
	}
	
	protected @NotNull RecipeBuilder groupAndUnlock(RecipeBuilder builder, String group, @NotNull Ingredient ingredientCriterion, Item itemCriterion) {
		for (Ingredient.Value value : ingredientCriterion.values) {
			if (value instanceof Ingredient.ItemValue itemValue) {
				builder.unlockedBy("has_" + getId(itemValue.item().getItem()), has(itemValue.item().getItem()));
			} else if (value instanceof Ingredient.TagValue tagValue) {
				builder.unlockedBy("has_" + tagValue.tag().location().getPath(), has(tagValue.tag()));
			}
		}
		return this.groupAndUnlock(builder, group, itemCriterion);
	}
}
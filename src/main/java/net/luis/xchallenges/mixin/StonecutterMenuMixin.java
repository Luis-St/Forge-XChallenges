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

package net.luis.xchallenges.mixin;

import net.luis.xchallenges.challenges.ChallengesHelper;
import net.luis.xchallenges.challenges.randomizer.RandomizerType;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 *
 * @author Luis-St
 *
 */

@Mixin(StonecutterMenu.class)
public abstract class StonecutterMenuMixin {
	
	@Inject(method = "setupResultSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;set(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	void setupResultSlot(CallbackInfo callback, RecipeHolder<StonecutterRecipe> recipe, ItemStack stack) {
		ChallengesHelper.randomizeCraftingItem(RandomizerType.STONE_CUTTING, stack, null);
	}
}

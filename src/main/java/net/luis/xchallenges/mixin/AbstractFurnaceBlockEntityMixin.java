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
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 *
 * @author Luis-St
 *
 */

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
	
	@Inject(method = "canBurn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
	private void canBurn(RegistryAccess access, RecipeHolder<?> recipe, NonNullList<ItemStack> inventory, int maxStackSize, CallbackInfoReturnable<Boolean> callback, ItemStack stack) {
		ChallengesHelper.randomizeCraftingItem(RandomizerType.SMELTING_AND_COOKING, stack, null);
	}
	
	@Inject(method = "burn", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;get(I)Ljava/lang/Object;", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
	private void burn(RegistryAccess access, RecipeHolder<?> recipe, NonNullList<ItemStack> inventory, int maxStackSize, CallbackInfoReturnable<Boolean> callback, ItemStack ignored, ItemStack stack) {
		ChallengesHelper.randomizeCraftingItem(RandomizerType.SMELTING_AND_COOKING, stack, null);
	}
}

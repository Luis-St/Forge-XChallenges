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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin {
	
	@Inject(method = "dispenseFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;onCraftedBySystem(Lnet/minecraft/world/level/Level;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	protected void dispenseFrom(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo callback, BlockEntity ignored, CrafterBlockEntity blockEntity, Optional<CraftingRecipe> optional, CraftingRecipe recipe, ItemStack stack) {
		ChallengesHelper.randomizeCraftingItem(RandomizerType.CRAFTING, stack, null);
	}
}

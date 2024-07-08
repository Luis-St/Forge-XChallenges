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

package net.luis.xchallenges.challenges;

import net.luis.xchallenges.XChallenges;
import net.luis.xchallenges.challenges.randomizer.RandomizerType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 *
 * @author Luis-St
 *
 */

public class ChallengesHelper {
	
	private static final Field ITEM = ObfuscationReflectionHelper.findField(ItemStack.class, "f_41589_");
	private static final Field DELEGATE = ObfuscationReflectionHelper.findField(ItemStack.class, "delegate");
	
	@SuppressWarnings("UnstableApiUsage")
	public static MinecraftServer getServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}
	
	public static void updateItemOfItemStack(@NotNull ItemStack stack, @NotNull Item item) {
		try {
			ITEM.set(stack, item);
			DELEGATE.set(stack, ForgeRegistries.ITEMS.getDelegateOrThrow(item));
		} catch (Exception e) {
			XChallenges.LOGGER.error("Unable to update item of item stack '{}' to '{}'", stack, item, e);
		}
	}
	
	public static void randomizeCraftingItem(@NotNull RandomizerType<Item> type, @NotNull ItemStack stack, @Nullable ServerPlayer player) {
		Challenges.get().getRandomizer().getIfActive(type).ifPresent(randomizer -> {
			Item item = randomizer.getRandomized(stack.getItem(), player);
			updateItemOfItemStack(stack, item);
		});
	}
	
	public static void sendToAllPlayers(@NotNull String message) {
		getServer().getPlayerList().getPlayers().forEach(player -> player.sendSystemMessage(Component.literal(message)));
	}
	
	public static boolean shouldIgnoreEntity(@NotNull Entity entity) {
		return entity instanceof Player || entity instanceof Projectile || entity instanceof EyeOfEnder || entity instanceof EvokerFangs || entity instanceof FallingBlockEntity ||
			entity instanceof AreaEffectCloud ||entity instanceof ItemEntity || entity instanceof PartEntity<?> || entity instanceof ExperienceOrb || entity instanceof Display || entity instanceof PrimedTnt;
	}
}

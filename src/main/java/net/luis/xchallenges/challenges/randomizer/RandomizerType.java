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

package net.luis.xchallenges.challenges.randomizer;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.luis.xchallenges.XChallenges;
import net.luis.xchallenges.challenges.ChallengesHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public abstract class RandomizerType<T> {
	
	private static final List<RandomizerType<?>> VALUES = Lists.newArrayList();
	
	public static final Codec<RandomizerType<?>> CODEC = ResourceLocation.CODEC.flatXmap(location -> {
		for (RandomizerType<?> type : VALUES) {
			if (type.location.equals(location)) {
				return DataResult.success(type);
			}
		}
		return DataResult.error(() -> "Unknown randomizer type: " + location);
	}, type -> DataResult.success(type.location));
	
	public static final RandomizerType<Item> CRAFTING = create("item/crafting", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> SMELTING_AND_COOKING = create("item/smelting_and_cooking", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> STONE_CUTTING = create("item/stone_cutting", ForgeRegistries.ITEMS);
	public static final RandomizerType<VillagerTrades.ItemListing> TRADES = create("item/trades", () -> {
		List<VillagerTrades.ItemListing> trades = Lists.newArrayList();
		for (VillagerProfession profession : VillagerTrades.TRADES.keySet()) {
			trades.addAll(RandomizerHelper.getTrades(profession));
		}
		trades.addAll(RandomizerHelper.getWanderingTraderTrades());
		return trades;
	});
	public static final RandomizerType<Item> BLOCK_LOOT = create("loot/blocks", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> CHEST_LOOT = create("loot/chests", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> ENTITY_LOOT = create("loot/entities", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> GAMEPLAY_LOOT = create("loot/gameplay", ForgeRegistries.ITEMS);
	public static final RandomizerType<EntityType<?>> ENTITIES = create("world/entities", ForgeRegistries.ENTITY_TYPES);
	
	private final ResourceLocation location;
	
	private RandomizerType(@NotNull ResourceLocation location) {
		this.location = location;
		VALUES.add(this);
	}
	
	public static <T> @NotNull RandomizerType<T> create(@NotNull String name, @NotNull IForgeRegistry<T> registry) {
		return new Registry<>(name, registry);
	}
	
	public static <T> @NotNull RandomizerType<T> create(@NotNull String name, @NotNull Supplier<List<T>> values) {
		return new Constants<>(name, values);
	}
	
	public static @NotNull List<RandomizerType<?>> values() {
		return VALUES;
	}
	
	public static @Nullable RandomizerType<?> byFullName(@NotNull String name) {
		for (RandomizerType<?> type : VALUES) {
			if (type.getFullName().equals(name)) {
				return type;
			}
		}
		return null;
	}
	
	public @NotNull String getType() {
		return this.location.getNamespace();
	}
	
	public @NotNull String getFullName() {
		return this.location.getPath();
	}
	
	public @NotNull String getName() {
		String[] parts = this.location.getPath().split("/");
		return parts[1] + " (" + parts[0] + ")";
	}
	
	protected abstract @NotNull Collection<T> getValues();
	
	public @NotNull String toString() {
		return this.location.toString();
	}
	
	//region Implementations
	private static class Registry<T> extends RandomizerType<T> {
		
		private final IForgeRegistry<T> registry;
		
		private Registry(String name, IForgeRegistry<T> registry) {
			super(new ResourceLocation("registry", name));
			this.registry = registry;
			if (StringUtils.countMatches(name, "/") != 1) {
				throw new IllegalArgumentException("Invalid registry name for randomizer type: " + name);
			}
		}
		
		public @NotNull IForgeRegistry<T> getRegistry() {
			return this.registry;
		}
		
		@Override
		public @NotNull Collection<T> getValues() {
			return this.registry.getValues();
		}
	}
	
	private static class Constants<T> extends RandomizerType<T> {
		
		private final Supplier<List<T>> lazy;
		
		private Constants(String name, Supplier<List<T>> values) {
			super(new ResourceLocation("constants", name));
			this.lazy = values;
			if (StringUtils.countMatches(name, "/") != 1) {
				throw new IllegalArgumentException("Invalid registry name for randomizer type: " + name);
			}
		}
		
		@Override
		public @NotNull Collection<T> getValues() {
			return this.lazy.get();
		}
	}
	//endregion
}

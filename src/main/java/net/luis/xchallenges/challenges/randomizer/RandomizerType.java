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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 * @author Luis-St
 *
 */

public abstract class RandomizerType<T> {
	
	private static final List<RandomizerType<?>> VALUES = Lists.newArrayList();
	
	public static final Codec<RandomizerType<?>> CODEC = Inner.CODEC.flatXmap((inner) -> {
		for (RandomizerType<?> type : VALUES) {
			if (type.getUnique().equals(inner.getUnique())) {
				return DataResult.success(type);
			}
		}
		return DataResult.error(() -> "Unknown randomizer type: " + inner.name());
	}, (type) -> DataResult.success(new Inner(type.getGroup(), type.getName(), type.getType())));
	
	public static final RandomizerType<Item> CRAFTING = create("item", "crafting", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> SMELTING_AND_COOKING = create("item", "smelting_and_cooking", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> BLOCK_LOOT = create("loot", "blocks", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> CHEST_LOOT = create("loot", "chests", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> ENTITY_LOOT = create("loot", "entities", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> GAMEPLAY_LOOT = create("loot", "gameplay", ForgeRegistries.ITEMS);
	public static final RandomizerType<VillagerTrades.ItemListing> TRADES = create("trades", "trades", () -> {
		List<VillagerTrades.ItemListing> trades = Lists.newArrayList();
		for (VillagerProfession profession : VillagerTrades.TRADES.keySet()) {
			trades.addAll(RandomizerHelper.getTrades(profession));
		}
		trades.addAll(RandomizerHelper.getWanderingTraderTrades());
		return trades;
	});
	
	private final String group;
	private final String name;
	private final String type;
	
	private RandomizerType(@NotNull String group, @NotNull String name, @NotNull String type) {
		this.group = group.toLowerCase();
		this.name = name.toLowerCase();
		this.type = type.toLowerCase();
		VALUES.add(this);
	}
	
	public static <T> @NotNull RandomizerType<T> create(@NotNull String group, @NotNull String name, @NotNull IForgeRegistry<T> registry) {
		return new Registry<>(group, name, registry);
	}
	
	public static <T> @NotNull RandomizerType<T> create(@NotNull String group, @NotNull String name, @NotNull Supplier<List<T>> values) {
		return new Constants<>(group, name, values);
	}
	
	public static @NotNull List<RandomizerType<?>> values() {
		return VALUES;
	}
	
	public @NotNull String getGroup() {
		return this.group;
	}
	
	public @NotNull String getName() {
		return this.name;
	}
	
	public @NotNull String getType() {
		return this.type;
	}
	
	public @NotNull String getUnique() {
		return this.type + ":" + this.group + "/" + this.name;
	}
	
	protected abstract @NotNull Collection<T> getValues();
	
	public @NotNull String toString() {
		return this.name;
	}
	
	//region Implementations
	private static class Registry<T> extends RandomizerType<T> {
		
		private final IForgeRegistry<T> registry;
		
		private Registry(String group, String name, IForgeRegistry<T> registry) {
			super(group, name, "registry");
			this.registry = registry;
			VALUES.add(this);
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
		
		private Constants(String group, String name, Supplier<List<T>> values) {
			super(group, name, "constants");
			this.lazy = values;
			VALUES.add(this);
		}
		
		@Override
		public @NotNull Collection<T> getValues() {
			return this.lazy.get();
		}
	}
	//endregion
	
	//region Internal
	private record Inner(String group, String name, String type) {
		
		public static final Codec<Inner> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			Codec.STRING.fieldOf("group").forGetter(Inner::group),
			Codec.STRING.fieldOf("name").forGetter(Inner::name),
			Codec.STRING.fieldOf("type").forGetter(Inner::type)
		).apply(instance, Inner::new));
		
		public @NotNull String getUnique() {
			return this.type + ":" + this.group + "/" + this.name;
		}
	}
	//endregion
}

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
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

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
	}, (type) -> DataResult.success(new Inner(type.getType(), type.getName())));
	
	public static final RandomizerType<Item> CRAFTING = create("crafting", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> BLOCK_LOOT = create("blocks", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> CHEST_LOOT = create("chests", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> ENTITY_LOOT = create("entities", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> GAMEPLAY_LOOT = create("gameplay", ForgeRegistries.ITEMS);
	
	private final String name;
	private final String type;
	
	private RandomizerType(@NotNull String target, @NotNull String type) {
		this.name = target.toLowerCase();
		this.type = type.toLowerCase();
		VALUES.add(this);
	}
	
	public static <T> @NotNull RandomizerType<T> create(String name, IForgeRegistry<T> registry) {
		return new Registry<>(name, registry);
	}
	
	public static <T> @NotNull RandomizerType<T> create(String name, Supplier<List<T>> values) {
		return new Constants<>(name, values);
	}
	
	public static @NotNull List<RandomizerType<?>> values() {
		return VALUES;
	}
	
	public @NotNull String getName() {
		return this.name;
	}
	
	public @NotNull String getType() {
		return this.type;
	}
	
	public @NotNull String getUnique() {
		return this.type + ":" + this.name;
	}
	
	protected abstract @NotNull List<T> getValues();
	
	public @NotNull String toString() {
		return this.name;
	}
	
	//region Implementations
	private static class Registry<T> extends RandomizerType<T> {
		
		private final IForgeRegistry<T> registry;
		private List<T> values;
		
		private Registry(String name, IForgeRegistry<T> registry) {
			super(name, "registry");
			this.registry = registry;
			VALUES.add(this);
		}
		
		public @NotNull IForgeRegistry<T> getRegistry() {
			return this.registry;
		}
		
		@Override
		public @NotNull List<T> getValues() {
			if (this.values == null) {
				this.values = Lists.newArrayList(this.registry);
			}
			return this.values;
		}
	}
	
	private static class Constants<T> extends RandomizerType<T> {
		
		private final Supplier<List<T>> lazy;
		private List<T> values;
		
		private Constants(String name, Supplier<List<T>> values) {
			super(name, "constants");
			this.lazy = values;
			VALUES.add(this);
		}
		
		@Override
		public @NotNull List<T> getValues() {
			if (this.values == null) {
				this.values = this.lazy.get();
			}
			return this.values;
		}
	}
	//endregion
	
	//region Internal
	private record Inner(String type, String name) {
		
		public static final Codec<Inner> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			Codec.STRING.fieldOf("type").forGetter(Inner::type),
			Codec.STRING.fieldOf("target").forGetter(Inner::name)
		).apply(instance, Inner::new));
		
		public @NotNull String getUnique() {
			return this.type + ":" + this.name;
		}
	}
	//endregion
}

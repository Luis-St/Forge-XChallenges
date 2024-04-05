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
import net.luis.xchallenges.server.codec.Codecs;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public class RandomizerType<T> {
	
	private static final List<RandomizerType<?>> VALUES = Lists.newArrayList();
	
	public static final Codec<RandomizerType<?>> CODEC = Codecs.forConstants(RandomizerType::fromString);
	public static final RandomizerType<Item> CRAFTING = new RandomizerType<>("crafting", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> BLOCK_LOOT = new RandomizerType<>("blocks", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> CHEST_LOOT = new RandomizerType<>("chests", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> ENTITY_LOOT = new RandomizerType<>("entities", ForgeRegistries.ITEMS);
	public static final RandomizerType<Item> GAMEPLAY_LOOT = new RandomizerType<>("gameplay", ForgeRegistries.ITEMS);
	
	private final String name;
	private final IForgeRegistry<T> registry;
	private List<T> values;
	
	private RandomizerType(String name, IForgeRegistry<T> registry) {
		this.name = name;
		this.registry = registry;
		VALUES.add(this);
	}
	
	public static @Nullable RandomizerType<?> fromString(@NotNull String string) {
		for (RandomizerType<?> type : VALUES) {
			if (type.toString().equals(string)) {
				return type;
			}
		}
		return null;
	}
	
	public static @NotNull List<RandomizerType<?>> values() {
		return VALUES;
	}
	
	public @NotNull String getName() {
		return this.name;
	}
	
	public @NotNull IForgeRegistry<T> getRegistry() {
		return this.registry;
	}
	
	public @NotNull List<T> getValues() {
		if (this.values == null) {
			this.values = Lists.newArrayList(this.registry);
		}
		return this.values;
	}
	
	public @NotNull String toString() {
		return this.name;
	}
}

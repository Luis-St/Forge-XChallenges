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

package net.luis.xchallenges.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("CodeBlock2Expr")
public class WeightCollection<T> {
	
	private final NavigableMap<Integer, Pair<Integer, T>> map;
	private final Random random;
	private int total;
	
	public WeightCollection() {
		this(new Random());
	}
	
	public WeightCollection(Random random) {
		this.map = Maps.newTreeMap();
		this.random = random;
	}
	
	private WeightCollection(@NotNull List<Pair<Integer, T>> list) {
		this.map = Maps.newTreeMap();
		this.random = new Random();
		list.forEach((pair) -> this.add(pair.getFirst(), pair.getSecond()));
	}
	
	public static <E> @NotNull Codec<WeightCollection<E>> codec(Codec<E> codec) {
		return RecordCodecBuilder.create((instance) -> {
			return instance.group(Codec.pair(Codec.INT.fieldOf("weight").codec(), codec.fieldOf("value").codec()).listOf().fieldOf("weights").forGetter(WeightCollection::asList)).apply(instance, WeightCollection::new);
		});
	}
	
	public void add(int weight, T value) {
		if (0 >= weight) {
			throw new IllegalArgumentException("The weight must be larger that 0 but it is " + weight);
		}
		this.total += weight;
		this.map.put(this.total, Pair.of(weight, value));
	}
	
	public T next() {
		int value = this.random.nextInt(this.total);
		return this.map.higherEntry(value).getValue().getSecond();
	}
	
	public boolean isEmpty() {
		return this.map.isEmpty();
	}
	
	private @NotNull List<Pair<Integer, T>> asList() {
		List<Pair<Integer, T>> list = Lists.newArrayList();
		for (Entry<Integer, Pair<Integer, T>> entry : this.map.entrySet()) {
			list.add(Pair.of(entry.getValue().getFirst(), entry.getValue().getSecond()));
		}
		return list;
	}
}

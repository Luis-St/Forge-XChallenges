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
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.luis.xchallenges.XChallenges;
import net.luis.xchallenges.server.codec.CodecHelper;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("unchecked")
public class RandomizeManager {
	
	private static final Random RNG = new Random();
	public static final Codec<RandomizeManager> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
		Randomizer.CODEC.listOf().fieldOf("randomizers").forGetter(manager -> Lists.newArrayList(manager.randomizers.values()))
	).apply(instance, RandomizeManager::new));
	
	private final Map<RandomizerType<?>, Randomizer<?>> randomizers = Maps.newHashMap();
	
	//region Constructors
	public RandomizeManager() {}
	
	private RandomizeManager(@NotNull List<Randomizer<?>> randomizers) {
		randomizers.forEach(randomizer -> this.randomizers.put(randomizer.getType(), randomizer));
	}
	//endregion
	
	public boolean hasRandomizer(@NotNull RandomizerType<?> type) {
		return this.randomizers.containsKey(type);
	}
	
	public <T> @NotNull Randomizer<T> getRandomizer(@NotNull RandomizerType<T> type) {
		return (Randomizer<T>) this.randomizers.get(type);
	}
	
	public <T> void createRandomizer(@NotNull RandomizerType<T> type, @NotNull RandomizerTarget target) {
		this.randomizers.put(type, Randomizer.create(type, RNG.nextLong(), target));
	}
	
	public boolean remove(@NotNull RandomizerType<?> type) {
		return this.randomizers.remove(type) != null;
	}
	
	//region IO operations
	public void load(@NotNull Path path, @NotNull String file) {
		RandomizeManager loaded = CodecHelper.load(CODEC, path.resolve(file + ".json"));
		if (loaded == null) {
			return;
		}
		XChallenges.LOGGER.info("Loaded {} randomizers from {}", loaded.randomizers.size(), path.resolve(file + ".json"));
		this.randomizers.clear();
		this.randomizers.putAll(loaded.randomizers);

	}
	
	public void save(@NotNull Path path, @NotNull String file) {
		CodecHelper.save(this, CODEC, path.resolve(file + ".json"));
		XChallenges.LOGGER.info("Saved {} randomizers to {}", this.randomizers.size(), path.resolve(file + ".json"));
	}
	//endregion
}

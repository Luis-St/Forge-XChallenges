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

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.luis.xchallenges.XChallenges;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 *
 * @author Luis-St
 *
 */

public class RandomizerInstance<T> {
	
	public static final Codec<RandomizerInstance<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
		RandomizerType.CODEC.fieldOf("factory").forGetter(RandomizerInstance::getType),
		Codec.LONG.fieldOf("seed").forGetter(RandomizerInstance::getSeed),
		RandomizerTarget.CODEC.fieldOf("target").forGetter(RandomizerInstance::getTarget),
		Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("offsets").forGetter(randomizer -> randomizer.offsets)
	).apply(instance, RandomizerInstance::create));
	
	private final RandomizerType<T> type;
	private final long seed;
	private final Map<String, Integer> offsets;
	private RandomizerTarget target;
	
	private RandomizerInstance(@NotNull RandomizerType<T> type, long seed, @NotNull RandomizerTarget target, Map<String, Integer> offsets) {
		this.type = type;
		this.seed = seed;
		this.offsets = offsets;
		this.target = target;
	}
	
	//region Factory methods
	public static <T> @NotNull RandomizerInstance<T> create(@NotNull RandomizerType<T> type, long seed, @NotNull RandomizerTarget target) {
		return new RandomizerInstance<>(type, seed, target, Maps.newHashMap());
	}
	
	@SuppressWarnings("unchecked")
	private static <T> @NotNull RandomizerInstance<T> create(@NotNull RandomizerType<?> type, long seed, @NotNull RandomizerTarget target, Map<String, Integer> offsets) {
		return new RandomizerInstance<>((RandomizerType<T>) type, seed, target, Maps.newHashMap(offsets));
	}
	//endregion
	
	public long getSeed() {
		return this.seed;
	}
	
	public @NotNull RandomizerType<T> getType() {
		return this.type;
	}
	
	public @NotNull RandomizerTarget getTarget() {
		return this.target;
	}
	
	public void setTarget(@Nullable RandomizerTarget target) {
		if (target == null) {
			return;
		}
		this.target = target;
		XChallenges.LOGGER.info("Randomizer target set to: {}", target);
		this.offsets.clear();
		XChallenges.LOGGER.info("Randomizer offsets cleared");
	}
	
	private @NotNull String getKey(@Nullable ServerPlayer player) {
		if (player == null) {
			return "global";
		}
		return switch (this.target) {
			case ALL -> "all";
			case TEAM -> player.getTeam() != null ? player.getTeam().getName() : "none";
			case PLAYER -> player.getUUID().toString();
		};
	}
	
	private int generateOffset() {
		int offset;
		int tries = 0;
		do {
			offset = RandomSource.create(this.seed + this.offsets.size()).nextInt(Integer.MAX_VALUE);
			tries++;
		} while (offset % this.type.getValues().size() == 0 && tries < 100);
		return offset;
	}
	
	public @NotNull T getRandomized(@NotNull T original, @Nullable ServerPlayer player) {
		int index = this.type.getValues().indexOf(original);
		int offset = this.offsets.computeIfAbsent(this.getKey(player), (k) -> this.generateOffset());
		return this.type.getValues().get((index + offset) % this.type.getValues().size());
	}
	
	public @NotNull T getOriginal(@NotNull T randomized, @Nullable ServerPlayer player) {
		int index = this.type.getValues().indexOf(randomized);
		int offset = this.offsets.computeIfAbsent(this.getKey(player), (k) -> {
			int i = this.generateOffset();
			XChallenges.LOGGER.info("Generated offset {} for target {}", i, k);
			return i;
		});
		int i = (index - offset + this.type.getValues().size()) % this.type.getValues().size();
		if (0 > i) {
			i += this.type.getValues().size();
		}
		return this.type.getValues().get(i);
	}
}

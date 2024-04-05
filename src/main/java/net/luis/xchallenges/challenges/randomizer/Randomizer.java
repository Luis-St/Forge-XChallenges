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

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class Randomizer<T> {
	
	public static final Codec<Randomizer<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
		RandomizerType.CODEC.fieldOf("type").forGetter(Randomizer::getType),
		Codec.LONG.fieldOf("seed").forGetter(Randomizer::getSeed),
		RandomizerTarget.CODEC.fieldOf("target").forGetter(Randomizer::getTarget),
		Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("offsets").forGetter(randomizer -> randomizer.offsets)
	).apply(instance, Randomizer::create));
	
	private final RandomizerType<T> type;
	private final long seed;
	private final Map<String, Integer> offsets;
	private RandomizerTarget target;
	
	private Randomizer(@NotNull RandomizerType<T> type, long seed, @NotNull RandomizerTarget target, Map<String, Integer> offsets) {
		this.type = type;
		this.seed = seed;
		this.offsets = offsets;
		this.target = target;
	}
	
	//region Factory methods
	public static <T> @NotNull Randomizer<T> create(@NotNull RandomizerType<T> type, long seed, @NotNull RandomizerTarget target) {
		return new Randomizer<>(type, seed, target, Maps.newHashMap());
	}
	
	@SuppressWarnings("unchecked")
	private static <T> @NotNull Randomizer<T> create(@NotNull RandomizerType<?> type, long seed, @NotNull RandomizerTarget target, Map<String, Integer> offsets) {
		return new Randomizer<>((RandomizerType<T>) type, seed, target, Maps.newHashMap(offsets));
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
	
	private @NotNull String getCacheKey(@Nullable ServerPlayer player) {
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
			offset = RandomSource.create(this.seed).nextInt(Integer.MAX_VALUE);
			tries++;
		} while (offset % this.type.getValues().size() == 0 && tries < 100);
		return offset;
	}
	
	//region Randomized
	private @NotNull T getRandomized(@NotNull T original, @NotNull String key) {
		int index = this.type.getValues().indexOf(original);
		int offset = this.offsets.computeIfAbsent(key, (k) -> this.generateOffset());
		return this.type.getValues().get((index + offset) % this.type.getValues().size());
	}
	
	public @NotNull T getRandomizedNoContext(@NotNull T original) {
		return this.getRandomized(original, "global");
	}
	
	public @NotNull T getRandomizedForPlayer(@NotNull T original, @Nullable ServerPlayer player) {
		return this.getRandomized(original, this.getCacheKey(player));
	}
	//endregion
	
	//region Original
	private @NotNull T getOriginal(@NotNull T randomized, @NotNull String key) {
		int index = this.type.getValues().indexOf(randomized);
		int offset = this.offsets.computeIfAbsent(key, (k) -> this.generateOffset());
		int i = (index - offset + this.type.getValues().size()) % this.type.getValues().size();
		if (0 > i) {
			i += this.type.getValues().size();
		}
		return this.type.getValues().get(i);
	}
	
	public @NotNull T getOriginalNoContext(@NotNull T randomized) {
		return this.getOriginal(randomized, "global");
	}
	
	public @NotNull T getOriginalForPlayer(@NotNull T randomized, @Nullable ServerPlayer player) {
		return this.getOriginal(randomized, this.getCacheKey(player));
	}
	//endregion
}

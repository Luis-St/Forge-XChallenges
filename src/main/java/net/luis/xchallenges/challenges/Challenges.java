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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.luis.xchallenges.XChallenges;
import net.luis.xchallenges.challenges.randomizer.Randomizer;
import net.luis.xchallenges.server.codec.CodecHelper;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public class Challenges {
	
	public static final Codec<Challenges> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
		Codec.BOOL.fieldOf("running").forGetter(manager -> manager.running)
	).apply(instance, Challenges::new));
	private static Challenges instance = null;
	
	private final Timer timer = Timer.create();
	private final Randomizer randomizer = new Randomizer();
	private boolean running;
	
	//region Constructors
	private Challenges() {}
	
	private Challenges(boolean running) {
		this.running = running;
	}
	//endregion
	
	public static @NotNull Challenges create() {
		Challenges manager = new Challenges();
		instance = manager;
		return manager;
	}
	
	public static @NotNull Challenges get() {
		return instance;
	}
	
	//region Actions
	public void start() {
		this.running = true;
	}
	
	public void stop() {
		this.running = false;
	}
	//endregion
	
	public boolean areChallengesActive() {
		return this.running;
	}
	
	public @NotNull Timer getTimer() {
		return this.timer;
	}
	
	public @NotNull Randomizer getRandomizer() {
		return this.randomizer;
	}
	
	public @NotNull Optional<Randomizer> getRandomizerIfActive() {
		if (!this.areChallengesActive() || this.getRandomizer().hasNone()) {
			return Optional.empty();
		}
		return Optional.of(this.getRandomizer());
	}
	
	//region IO operations
	public void load(@NotNull Path path) {
		Challenges manager = CodecHelper.load(CODEC, path.resolve("challenges.json"));
		if (manager != null) {
			XChallenges.LOGGER.info("Loaded challenges from {}", path.resolve("challenges.json"));
			this.running = manager.running;
		}
		this.timer.load(path.resolve("timer.json"));
		this.randomizer.load(path, "randomizer");
	}
	
	public void save(@NotNull Path path) {
		CodecHelper.save(this, CODEC, path.resolve("challenges.json"));
		XChallenges.LOGGER.info("Saved challenges to {}", path.resolve("challenges.json"));
		this.timer.save(path.resolve("timer.json"));
		this.randomizer.save(path, "randomizer");
	}
	//endregion
}

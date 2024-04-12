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
	
	private static Challenges instance = null;
	
	private final Timer timer = Timer.create();
	private final Randomizer randomizer = new Randomizer();
	
	//region Constructor
	private Challenges() {}
	//endregion
	
	public static @NotNull Challenges create() {
		Challenges manager = new Challenges();
		instance = manager;
		return manager;
	}
	
	public static @NotNull Challenges get() {
		return instance;
	}
	
	public @NotNull Timer getTimer() {
		return this.timer;
	}
	
	public @NotNull Randomizer getRandomizer() {
		return this.randomizer;
	}
	
	//region IO operations
	public void load(@NotNull Path path) {
		this.timer.load(path.resolve("timer.json"));
		this.randomizer.load(path, "randomizer");
	}
	
	public void save(@NotNull Path path) {
		this.timer.save(path.resolve("timer.json"));
		this.randomizer.save(path, "randomizer");
	}
	//endregion
}

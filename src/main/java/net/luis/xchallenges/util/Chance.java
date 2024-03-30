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

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 *
 * @author Luis-St
 *
 */

public class Chance {
	
	public static final Codec<Chance> CODEC = Codec.DOUBLE.xmap(Chance::new, (chance) -> chance.chance);
	
	private final Random rng = new Random();
	private final double chance;
	
	private Chance(double chance) {
		this.chance = chance;
	}
	
	public static @NotNull Chance of(double chance) {
		return new Chance(chance);
	}
	
	public void setSeed(long seed) {
		this.rng.setSeed(seed);
	}
	
	public boolean isTrue() {
		return this.chance >= 1.0;
	}
	
	public boolean isFalse() {
		return 0.0 >= this.chance;
	}
	
	public boolean result() {
		if (this.isTrue()) {
			return true;
		} else if (this.isFalse()) {
			return false;
		} else {
			return this.chance > this.rng.nextDouble();
		}
	}
}

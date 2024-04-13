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

import com.mojang.serialization.Codec;
import net.luis.xchallenges.server.codec.Codecs;

/**
 *
 * @author Luis-St
 *
 */

public enum RandomizerTarget {
	
	ALL, TEAM, PLAYER;
	
	public static final Codec<RandomizerTarget> CODEC = Codecs.forEnum(RandomizerTarget::values);
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}

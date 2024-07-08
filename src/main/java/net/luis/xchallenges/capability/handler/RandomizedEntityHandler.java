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

package net.luis.xchallenges.capability.handler;

import net.luis.xchallenges.world.entity.IRandomizedEntity;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public class RandomizedEntityHandler implements IRandomizedEntity {
	
	private boolean randomized;
	
	@Override
	public void setRandomized() {
		this.randomized = true;
	}
	
	@Override
	public boolean isRandomized() {
		return this.randomized;
	}
	
	@Override
	public @NotNull CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean("Randomized", this.randomized);
		return tag;
	}
	
	@Override
	public void deserialize(@NotNull CompoundTag tag) {
		this.randomized = tag.getBoolean("Randomized");
	}
}

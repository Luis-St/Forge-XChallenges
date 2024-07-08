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

package net.luis.xchallenges.world.entity;

import net.luis.xchallenges.capability.XCCapabilities;
import net.luis.xchallenges.capability.handler.RandomizedEntityHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class RandomizedEntityProvider implements ICapabilitySerializable<CompoundTag> {
	
	private final IRandomizedEntity handler = new RandomizedEntityHandler();
	private final LazyOptional<IRandomizedEntity> randomizedEntity = LazyOptional.of(() -> this.handler);
	
	public static @NotNull IRandomizedEntity get(@NotNull Entity entity) {
		return entity.getCapability(XCCapabilities.RANDOMIZED_ENTITY, null).orElseThrow(NullPointerException::new);
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return XCCapabilities.RANDOMIZED_ENTITY.orEmpty(cap, this.randomizedEntity);
	}
	
	@Override
	public CompoundTag serializeNBT() {
		return this.handler.serialize();
	}
	
	@Override
	public void deserializeNBT(CompoundTag tag) {
		this.handler.deserialize(tag);
	}
}

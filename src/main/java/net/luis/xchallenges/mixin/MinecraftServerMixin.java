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

package net.luis.xchallenges.mixin;

import net.luis.xchallenges.challenges.Challenges;
import net.luis.xchallenges.server.CommandBroadcastMode;
import net.luis.xchallenges.server.IMinecraftServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.file.Path;

/**
 *
 * @author Luis-St
 *
 */

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements IMinecraftServer {
	
	//region Mixin
	@Shadow
	protected LevelStorageSource.LevelStorageAccess storageSource;
	//endregion
	
	private final Challenges manager = Challenges.create();
	private CommandBroadcastMode broadcastMode = CommandBroadcastMode.ADMIN;
	
	@Override
	public @NotNull Challenges getChallenges() {
		return this.manager;
	}
	
	@Override
	public @NotNull Path getWorldPath() {
		return this.storageSource.getLevelDirectory().path();
	}
	
	@Override
	public @NotNull CommandBroadcastMode getBroadcastMode() {
		return this.broadcastMode;
	}
	
	@Override
	public void setBroadcastMode(@Nullable CommandBroadcastMode mode) {
		this.broadcastMode = mode == null ? CommandBroadcastMode.ADMIN : mode;
	}
}

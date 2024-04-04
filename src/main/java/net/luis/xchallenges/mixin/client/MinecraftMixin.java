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

package net.luis.xchallenges.mixin.client;

import net.luis.xchallenges.challenges.Timer;
import net.luis.xchallenges.client.IMinecraft;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

/**
 *
 * @author Luis-St
 *
 */

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements IMinecraft {
	
	private final Timer timer = Timer.create();
	
	@Override
	public @NotNull Timer getTimer() {
		return this.timer;
	}
}

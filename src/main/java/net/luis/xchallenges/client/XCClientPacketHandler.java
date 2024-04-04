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

package net.luis.xchallenges.client;

import net.luis.xchallenges.XChallenges;
import net.minecraft.client.Minecraft;

/**
 *
 * @author Luis-St
 *
 */

public class XCClientPacketHandler {
	
	public static void syncTimer(boolean running, long ticks, long currentTicks) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft instanceof IMinecraft mc) {
			mc.getTimer().update(running, ticks, currentTicks);
		} else {
			XChallenges.LOGGER.error("Minecraft is not an instance of IMinecraft");
		}
	}
}

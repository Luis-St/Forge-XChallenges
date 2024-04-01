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
import net.luis.xchallenges.challenges.Timer;

/**
 *
 * @author Luis-St
 *
 */

public class XCClientPacketHandler {
	
	public static void syncTimer(long serverTime, boolean down, boolean up, boolean running, long ticks, long currentTicks) {
		if (down == up) {
			XChallenges.LOGGER.warn("Received invalid timer sync packet: down = {}, up = {}", down, up);
			return;
		}
		Timer timer = Timer.getInstance();
		timer.update(down, up, running, ticks, currentTicks);
		XChallenges.LOGGER.info("Updated timer from server '{}'", timer);
	}
}

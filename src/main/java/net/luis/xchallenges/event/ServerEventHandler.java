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

package net.luis.xchallenges.event;

import net.luis.xchallenges.XChallenges;
import net.luis.xchallenges.challenges.Timer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = XChallenges.MOD_ID)
public class ServerEventHandler {
	
	private static int tickCount = 0;
	
	@SubscribeEvent
	public static void serverTick(TickEvent. @NotNull ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			tickCount++;
			tick(event.getServer());
		}
	}
	
	private static void tick(@NotNull MinecraftServer server) {
		Timer timer = Timer.getInstance();
		timer.tick();
		if (!timer.isPaused() && tickCount % (20 * 60) == 0) {
			XChallenges.LOGGER.info("Syncing timer '{}'", timer);
			timer.sync();
		}
	}
}

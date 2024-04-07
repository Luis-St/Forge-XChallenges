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
import net.luis.xchallenges.server.IMinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 *
 * @author Luis-St
 *
 */

@Mod.EventBusSubscriber(modid = XChallenges.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {
	
	private static final String BASE_PATH = "xchallenges";
	private static long tick;
	
	@SubscribeEvent
	public static void serverStarting(@NotNull ServerStartingEvent event) {
		if (!(event.getServer() instanceof IMinecraftServer server)) {
			XChallenges.LOGGER.error("Server is not an instance of IMinecraftServer");
			return;
		}
		Path path = server.getWorldPath().resolve(BASE_PATH);
		server.getChallenges().load(path);
	}
	
	@SubscribeEvent
	public static void serverTick(TickEvent.@NotNull ServerTickEvent event) {
		if (event.phase != TickEvent.Phase.START) {
			return;
		}
		if (!(event.getServer() instanceof IMinecraftServer server)) {
			XChallenges.LOGGER.error("Server is not an instance of IMinecraftServer");
			return;
		}
		if (server.getTimer().isPaused()) {
			return;
		}
		server.getTimer().tick();
		tick++;
		if (tick % (20 * 60) == 0) {
			server.getTimer().sync();
		}
	}
	
	@SubscribeEvent
	public static void serverStopping(@NotNull ServerStoppingEvent event) {
		if (!(event.getServer() instanceof IMinecraftServer server)) {
			XChallenges.LOGGER.error("Server is not an instance of IMinecraftServer");
			return;
		}
		Path path = server.getWorldPath().resolve(BASE_PATH);
		server.getChallenges().save(path);
	}
}

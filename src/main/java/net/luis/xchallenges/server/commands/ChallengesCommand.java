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

package net.luis.xchallenges.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.luis.xchallenges.XChallenges;
import net.luis.xchallenges.server.IMinecraftServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public class ChallengesCommand {
	
	public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("challenges").requires((stack) -> {
				return stack.hasPermission(2);
			}).then(Commands.literal("start").executes((context) -> {
					return start(context.getSource());
				})
			).then(Commands.literal("stop").executes((context) -> {
					return stop(context.getSource());
				})
			)
		);
	}
	
	private static int start(@NotNull CommandSourceStack source) {
		if (!(source.getServer() instanceof IMinecraftServer mc)) {
			source.sendFailure(Component.translatable("xchallenges.error.critical"));
			XChallenges.LOGGER.error("Minecraft server is not an instance of IMinecraftServer");
			return -1;
		}
		mc.getChallenges().start();
		source.sendSuccess(() -> Component.translatable("command.xchallenges.challenges.start"), false);
		return 0;
	}
	
	private static int stop(@NotNull CommandSourceStack source) {
		if (!(source.getServer() instanceof IMinecraftServer mc)) {
			source.sendFailure(Component.translatable("xchallenges.error.critical"));
			XChallenges.LOGGER.error("Minecraft server is not an instance of IMinecraftServer");
			return -1;
		}
		mc.getChallenges().stop();
		source.sendSuccess(() -> Component.translatable("command.xchallenges.challenges.stop"), false);
		return 0;
	}
}

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
import net.luis.xchallenges.challenges.ChallengesHelper;
import net.luis.xchallenges.server.CommandBroadcastMode;
import net.luis.xchallenges.server.IMinecraftServer;
import net.luis.xchallenges.server.commands.arguments.EnumArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public class BroadcastCommand {
	
	public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("broadcast").executes(context -> {
			return 0;
			}).then(Commands.argument("mode", EnumArgument.argument(CommandBroadcastMode.class)).executes(context -> {
				return setMode(context.getSource(), EnumArgument.get(context, "mode", CommandBroadcastMode.class));
			})
		));
	}
	
	private static int getMode(@NotNull CommandSourceStack source) {
		if (!(source.getServer() instanceof IMinecraftServer mc)) {
			source.sendFailure(Component.translatable("xchallenges.error.critical"));
			XChallenges.LOGGER.error("Minecraft server is not an instance of IMinecraftServer");
			return -1;
		}
		source.sendSuccess(() -> Component.translatable("xchallenges.command.broadcast.get", mc.getBroadcastMode()), false);
		return 0;
	}
	
	private static int setMode(@NotNull CommandSourceStack source, @NotNull CommandBroadcastMode mode) {
		if (!(source.getServer() instanceof IMinecraftServer mc)) {
			source.sendFailure(Component.translatable("xchallenges.error.critical"));
			XChallenges.LOGGER.error("Minecraft server is not an instance of IMinecraftServer");
			return -1;
		}
		mc.setBroadcastMode(mode);
		ChallengesHelper.sendToAllPlayers("Set command broadcast mode to " + mode);
		return 0;
	}
}

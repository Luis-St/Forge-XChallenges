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
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.luis.xchallenges.XChallenges;
import net.luis.xchallenges.challenges.Timer;
import net.luis.xchallenges.server.IMinecraftServer;
import net.luis.xchallenges.server.commands.arguments.RealTimeArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 *
 * @author Luis-St
 *
 */

public class TimerCommand {
	
	public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("timer").requires((stack) -> {
			return stack.hasPermission(2);
		}).then(Commands.literal("start").executes((context) -> {
				return execute(context.getSource(), context.getSource().getServer(), Timer::resume, "start");
			})
		).then(Commands.literal("stop").executes((context) -> {
				return execute(context.getSource(), context.getSource().getServer(), Timer::stop, "stop");
			})
		).then(Commands.literal("pause").executes((context) -> {
				return execute(context.getSource(), context.getSource().getServer(), Timer::pause, "pause");
			})
		).then(Commands.literal("resume").executes((context) -> {
				return execute(context.getSource(), context.getSource().getServer(), Timer::resume, "resume");
			})
		).then(Commands.literal("reset").executes((context) -> {
				return execute(context.getSource(), context.getSource().getServer(), Timer::reset, "reset");
			})
		).then(Commands.literal("set").then(Commands.argument("time", RealTimeArgument.time(1)).executes((context) -> {
					return make(context.getSource(), context.getSource().getServer(), IntegerArgumentType.getInteger(context, "time"));
				})
			)
		));
	}
	
	private static int make(@NotNull CommandSourceStack source, @NotNull MinecraftServer server, int time) {
		if (!(server instanceof IMinecraftServer mc)) {
			source.sendFailure(Component.translatable("xchallenges.error.critical"));
			XChallenges.LOGGER.error("Minecraft server is not an instance of IMinecraftServer");
			return -1;
		}
		Timer timer = mc.getTimer();
		timer.makeDown(time * 20L);
		source.sendSuccess(() -> Component.translatable("commands.xchallenges.timer.set"), false);
		timer.sync();
		return 0;
	}
	
	private static int execute(@NotNull CommandSourceStack source, @NotNull MinecraftServer server, @NotNull Consumer<Timer> action, String message) {
		if (!(server instanceof IMinecraftServer mc)) {
			source.sendFailure(Component.translatable("xchallenges.error.critical"));
			XChallenges.LOGGER.error("Minecraft server is not an instance of IMinecraftServer");
			return -1;
		}
		Timer timer = mc.getTimer();
		action.accept(timer);
		timer.sync();
		source.sendSuccess(() -> Component.translatable("commands.xchallenges.timer." + message), false);
		return 0;
	}
}

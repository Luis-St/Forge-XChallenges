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
				return execute(context.getSource(), Timer::resume, "start");
			})
		).then(Commands.literal("pause").executes((context) -> {
				return execute(context.getSource(), Timer::pause, "pause");
			})
		).then(Commands.literal("resume").executes((context) -> {
				return execute(context.getSource(), Timer::resume, "resume");
			})
		).then(Commands.literal("reset").then(Commands.literal("current").executes((context) -> {
					return execute(context.getSource(), Timer::resetCurrent, "reset_current");
				})
			).then(Commands.literal("default").executes((context) -> {
					return execute(context.getSource(), Timer::resetDefault, "reset_default");
				})
			)
		).then(Commands.literal("set").then(Commands.argument("time", RealTimeArgument.time(1)).executes((context) -> {
					return make(context.getSource(), IntegerArgumentType.getInteger(context, "time"));
				})
			)
		));
	}
	
	private static int make(@NotNull CommandSourceStack source, int time) {
		if (!(source.getServer() instanceof IMinecraftServer mc)) {
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
	
	private static int execute(@NotNull CommandSourceStack source, @NotNull Consumer<Timer> action, String message) {
		if (!(source.getServer() instanceof IMinecraftServer mc)) {
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

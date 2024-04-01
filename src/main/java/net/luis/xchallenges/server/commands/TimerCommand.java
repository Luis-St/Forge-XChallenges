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
import net.luis.xchallenges.challenges.Timer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
				return execute(Timer::resume);
			})
		).then(Commands.literal("stop").executes((context) -> {
				return execute(Timer::stop);
			})
		).then(Commands.literal("pause").executes((context) -> {
				return execute(Timer::pause);
			})
		).then(Commands.literal("resume").executes((context) -> {
				return execute(Timer::resume);
			})
		).then(Commands.literal("reset").executes((context) -> {
				return execute(Timer::reset);
			})
		).then(Commands.literal("set").then(Commands.argument("minutes", IntegerArgumentType.integer()).executes((context) -> {
					return make(IntegerArgumentType.getInteger(context, "minutes"));
				})
			)
		));
	}
	
	private static int make(int minutes) {
		Timer timer = Timer.getInstance();
		if (minutes > 0) {
			timer.makeDown(minutes * 60L * 20L);
		} else {
			timer.makeUp();
		}
		timer.sync();
		return 0;
	}
	
	private static int execute(@NotNull Consumer<Timer> action) {
		Timer timer = Timer.getInstance();
		action.accept(timer);
		timer.sync();
		return 0;
	}
}

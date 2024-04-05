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

package net.luis.xchallenges.server.commands.arguments;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author Luis-St
 *
 */

public class RealTimeArgument implements ArgumentType<Integer> {
	
	private static final DynamicCommandExceptionType ERROR_INVALID_UNIT = new DynamicCommandExceptionType(found -> Component.translatable("arguments.xchallenges.real_time.invalid_unit", found));
	private static final Dynamic3CommandExceptionType ERROR_TIME_TOO_LOW = new Dynamic3CommandExceptionType((found, unit, min) -> Component.translatable("arguments.xchallenges.real_time.time_too_low", found, unit, min));
	private static final Collection<String> EXAMPLES = Arrays.asList("0", "0s", "0m", "0h", "0d");
	private static final Map<String, Integer> UNITS = Util.make(Maps.newHashMap(), map -> {
		map.put("", 60); // Default unit is minutes
		map.put("s", 1);
		map.put("m", 60);
		map.put("h", 60 * 60);
		map.put("d", 60 * 60 * 24);
	});
	private final int min;
	
	private RealTimeArgument(int min) {
		this.min = min;
	}
	
	public static @NotNull RealTimeArgument time() {
		return new RealTimeArgument(0);
	}
	
	public static @NotNull RealTimeArgument time(int min) {
		return new RealTimeArgument(min);
	}
	
	public @NotNull Integer parse(@NotNull StringReader reader) throws CommandSyntaxException {
		int value = reader.readInt();
		String unit = reader.readUnquotedString();
		int unitValue = UNITS.getOrDefault(unit, 0);
		if (unitValue == 0) {
			throw ERROR_INVALID_UNIT.create(unit);
		} else {
			int result = value * unitValue;
			if (result < this.min) {
				throw ERROR_TIME_TOO_LOW.create(result, unit, this.min);
			} else {
				return result;
			}
		}
	}
	
	public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
		StringReader reader = new StringReader(builder.getRemaining());
		try {
			reader.readFloat();
		} catch (CommandSyntaxException exception) {
			return builder.buildFuture();
		}
		return SharedSuggestionProvider.suggest(UNITS.keySet(), builder.createOffset(builder.getStart() + reader.getCursor()));
	}
	
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
	
	//region Argument info
	@SuppressWarnings("ReturnOfInnerClass")
	public static class Info implements ArgumentTypeInfo<RealTimeArgument, RealTimeArgument.Info.Template> {
		
		public void serializeToNetwork(RealTimeArgument.Info.@NotNull Template template, @NotNull FriendlyByteBuf buffer) {
			buffer.writeInt(template.min);
		}
		
		public RealTimeArgument.Info.@NotNull Template deserializeFromNetwork(@NotNull FriendlyByteBuf buffer) {
			int i = buffer.readInt();
			return new RealTimeArgument.Info.Template(i);
		}
		
		public void serializeToJson(RealTimeArgument.Info.@NotNull Template template, @NotNull JsonObject json) {
			json.addProperty("min", template.min);
		}
		
		public RealTimeArgument.Info.@NotNull Template unpack(@NotNull RealTimeArgument argument) {
			return new RealTimeArgument.Info.Template(argument.min);
		}
		
		public final class Template implements ArgumentTypeInfo.Template<RealTimeArgument> {
			
			private final int min;
			
			private Template(int min) {
				this.min = min;
			}
			
			public @NotNull RealTimeArgument instantiate(@NotNull CommandBuildContext context) {
				return RealTimeArgument.time(this.min);
			}
			
			public @NotNull ArgumentTypeInfo<RealTimeArgument, ?> type() {
				return Info.this;
			}
		}
	}
	//endregion
}

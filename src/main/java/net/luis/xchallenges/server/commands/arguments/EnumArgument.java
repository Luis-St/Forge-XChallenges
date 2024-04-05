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

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Luis-St
 *
 */

public class EnumArgument<T extends Enum<T>> implements ArgumentType<T> {
	
	private static final DynamicCommandExceptionType INVALID_ENUM = new DynamicCommandExceptionType(found -> Component.translatable("arguments.xchallenges.enum.invalid", found));
	private final Class<T> clazz;
	
	private EnumArgument(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public static <R extends Enum<R>> @NotNull EnumArgument<R> argument(Class<R> clazz) {
		return new EnumArgument<>(clazz);
	}
	
	public static <R extends Enum<R>> @NotNull R get(@NotNull CommandContext<?> context, @NotNull String name, @NotNull Class<R> clazz) {
		return context.getArgument(name, clazz);
	}
	
	@Override
	public @NotNull T parse(final @NotNull StringReader reader) throws CommandSyntaxException {
		String name = reader.readUnquotedString();
		T[] constants = this.clazz.getEnumConstants();
		if (constants == null || constants.length == 0) {
			throw INVALID_ENUM.create(name);
		}
		for (T constant : constants) {
			if (constant.toString().equals(name)) {
				return constant;
			}
		}
		throw INVALID_ENUM.create(name);
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(Stream.of(this.clazz.getEnumConstants()).map(Enum::toString), builder);
	}
	
	@Override
	public Collection<String> getExamples() {
		return Stream.of(this.clazz.getEnumConstants()).map(Enum::toString).collect(Collectors.toList());
	}
	
	//region Argument info
	@SuppressWarnings("ReturnOfInnerClass")
	public static class Info<T extends Enum<T>> implements ArgumentTypeInfo<EnumArgument<T>, Info<T>.Template> {
		
		@Override
		public void serializeToNetwork(EnumArgument.Info.@NotNull Template template, @NotNull FriendlyByteBuf buffer) {
			buffer.writeUtf(template.clazz.getName());
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public EnumArgument.Info<T>.@NotNull Template deserializeFromNetwork(@NotNull FriendlyByteBuf buffer) {
			try {
				String name = buffer.readUtf();
				return new Template((Class<T>) Class.forName(name));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public void serializeToJson(EnumArgument.Info.@NotNull Template template, @NotNull JsonObject json) {
			json.addProperty("enum", template.clazz.getName());
		}
		
		@Override
		public @NotNull Template unpack(@NotNull EnumArgument<T> argument) {
			return new Template(argument.clazz);
		}
		
		public class Template implements ArgumentTypeInfo.Template<EnumArgument<T>> {
			
			private final Class<T> clazz;
			
			private Template(Class<T> clazz) {
				this.clazz = clazz;
			}
			
			@Override
			public @NotNull EnumArgument<T> instantiate(@NotNull CommandBuildContext context) {
				return new EnumArgument<>(this.clazz);
			}
			
			@Override
			public @NotNull ArgumentTypeInfo<EnumArgument<T>, ?> type() {
				return Info.this;
			}
		}
	}
	//endregion
}

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
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.luis.xchallenges.challenges.randomizer.RandomizerType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Luis-St
 *
 */

public class RandomizerTypeArgument implements ArgumentType<List<RandomizerType<?>>> {
	
	private static final DynamicCommandExceptionType INVALID = new DynamicCommandExceptionType(found -> Component.translatable("arguments.xchallenges.randomizer_type.invalid", found));
	private static final SimpleCommandExceptionType ALL_NOT_ALLOWED = new SimpleCommandExceptionType(Component.translatable("arguments.xchallenges.randomizer_type.all_not_allowed"));
	
	private final boolean allowAll;
	
	public RandomizerTypeArgument(boolean allowAll) {
		this.allowAll = allowAll;
	}
	
	public static @NotNull RandomizerTypeArgument argument(boolean allowAll) {
		return new RandomizerTypeArgument(allowAll);
	}
	
	public static @NotNull RandomizerType<?> getSingle(@NotNull CommandContext<?> context, @NotNull String name) {
		return getMulti(context, name).get(0);
	}
	
	@SuppressWarnings("unchecked")
	public static @NotNull List<RandomizerType<?>> getMulti(@NotNull CommandContext<?> context, @NotNull String name) {
		return context.getArgument(name, List.class);
	}
	
	@Override
	public @NotNull List<RandomizerType<?>> parse(final @NotNull StringReader reader) throws CommandSyntaxException {
		String name = this.readUntilNextWhitespace(reader);
		if ("*".equals(name)) {
			if (!this.allowAll) {
				throw ALL_NOT_ALLOWED.create();
			}
			return RandomizerType.values();
		}
		RandomizerType<?> type = RandomizerType.byFullName(name);
		if (type == null) {
			throw INVALID.create(name);
		}
		return List.of(type);
	}
	
	private @NotNull String readUntilNextWhitespace(@NotNull StringReader reader) {
		if (!reader.canRead()) {
			return "";
		}
		int start = reader.getCursor();
		while (reader.canRead() && !Character.isWhitespace(reader.peek())) {
			reader.skip();
		}
		return reader.getString().substring(start, reader.getCursor());
	}
	
	private @NotNull Stream<String> getNames() {
		List<String> types = RandomizerType.values().stream().map(RandomizerType::getFullName).collect(Collectors.toList());
		if (this.allowAll) {
			types.add(0, "*");
		}
		return types.stream();
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(this.getNames(), builder);
	}
	
	@Override
	public Collection<String> getExamples() {
		return this.getNames().collect(Collectors.toList());
	}
	
	//region Argument info
	@SuppressWarnings("ReturnOfInnerClass")
	public static class Info implements ArgumentTypeInfo<RandomizerTypeArgument, Info.Template> {
		
		@Override
		public void serializeToNetwork(RandomizerTypeArgument.Info.@NotNull Template template, @NotNull FriendlyByteBuf buffer) {
			buffer.writeBoolean(template.allowAll);
		}
		
		@Override
		public RandomizerTypeArgument.Info.@NotNull Template deserializeFromNetwork(@NotNull FriendlyByteBuf buffer) {
			boolean allowAll = buffer.readBoolean();
			return new Template(allowAll);
		}
		
		@Override
		public void serializeToJson(RandomizerTypeArgument.Info.@NotNull Template template, @NotNull JsonObject json) {
			json.addProperty("allow_all", template.allowAll);
		}
		
		@Override
		public @NotNull Template unpack(@NotNull RandomizerTypeArgument argument) {
			return new Template(argument.allowAll);
		}
		
		public class Template implements ArgumentTypeInfo.Template<RandomizerTypeArgument> {
			
			private final boolean allowAll;
			
			public Template(boolean allowAll) {
				this.allowAll = allowAll;
			}
			
			@Override
			public @NotNull RandomizerTypeArgument instantiate(@NotNull CommandBuildContext context) {
				return new RandomizerTypeArgument(this.allowAll);
			}
			
			@Override
			public @NotNull ArgumentTypeInfo<RandomizerTypeArgument, ?> type() {
				return Info.this;
			}
		}
	}
	//endregion
}

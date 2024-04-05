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

package net.luis.xchallenges.server.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.luis.xchallenges.server.commands.arguments.NameableArgument;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public class Codecs {
	
	public static <E extends Enum<E> & NameableArgument> @NotNull Codec<E> forEnum(@NotNull Supplier<E[]> supplier) {
		Map<String, E> map = Arrays.stream(supplier.get()).collect(Collectors.toMap(NameableArgument::getArgumentName, Function.identity()));
		return Codec.STRING.flatXmap(str -> {
			E value = map.get(str.toLowerCase());
			if (value == null) {
				return DataResult.error(() -> "Unknown enum value: " + str.toLowerCase());
			}
			return DataResult.success(value);
		}, value -> DataResult.success(value.name().toLowerCase()));
	}
	
	public static <T> @NotNull Codec<T> forConstants(@NotNull Function<String, T> fromString) {
		return forConstants(fromString, Object::toString);
	}
	
	public static <T> @NotNull Codec<T> forConstants(@NotNull Function<String, T> fromString, @NotNull Function<T, String> toString) {
		return Codec.STRING.flatXmap(str -> {
			T value = fromString.apply(str.toLowerCase());
			if (value == null) {
				return DataResult.error(() -> "Unknown constant value: " + str.toLowerCase());
			}
			return DataResult.success(value);
		}, value -> DataResult.success(toString.apply(value)));
	}
}

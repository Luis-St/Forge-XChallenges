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

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.luis.xchallenges.XChallenges;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Luis-St
 *
 */

public class CodecHelper {
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	public static <T> @Nullable T load(@NotNull Codec<T> codec, @NotNull Path file) {
		try {
			if (Files.notExists(file)) {
				return null;
			}
			BufferedReader reader = new BufferedReader(new FileReader(file.toFile()));
			JsonElement element = GSON.fromJson(reader, JsonElement.class);
			reader.close();
			return codec.parse(JsonOps.INSTANCE, element).getOrThrow(false, s -> {});
		} catch (IOException e) {
			XChallenges.LOGGER.error("Could not load object from file '{}'", file, e);
			return null;
		}
	}
	
	public static <T> void save(@NotNull T object, @NotNull Codec<T> codec, @NotNull Path file) {
		try {
			if (Files.notExists(file)) {
				Files.createDirectories(file.getParent());
				Files.createFile(file);
			}
			JsonElement jsonElement = codec.encodeStart(JsonOps.INSTANCE, object).getOrThrow(false, s -> {});
			String element = GSON.toJson(jsonElement);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.toFile())));
			writer.write(element);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			XChallenges.LOGGER.error("Could not save object '{}' to file '{}'", object, file, e);
		}
	}
}

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
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.luis.xchallenges.XChallenges;
import net.luis.xchallenges.challenges.randomizer.RandomizerTarget;
import net.luis.xchallenges.challenges.randomizer.RandomizerType;
import net.luis.xchallenges.server.IMinecraftServer;
import net.luis.xchallenges.server.commands.arguments.EnumArgument;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class RandomizerCommand {
	
	private static final String BASE_PATH = "xchallenges/user_randomizers";
	
	public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("randomizer").requires((stack) -> {
			return stack.hasPermission(2);
		});
		
		for (RandomizerType<?> type : RandomizerType.values()) {
			builder.then(Commands.literal("get").then(Commands.literal(type.getName()).executes((context) -> {
						return getRandomizerStatus(context.getSource(), type);
					})
				)
			);
			builder.then(Commands.literal("enable").then(Commands.literal(type.getName()).executes((context) -> {
						return enableRandomizer(context.getSource(), type, RandomizerTarget.TEAM);
					}).then(Commands.argument("target", EnumArgument.argument(RandomizerTarget.class)).executes((context) -> {
							return enableRandomizer(context.getSource(), type, context.getArgument("target", RandomizerTarget.class));
						})
					)
				)
			);
			builder.then(Commands.literal("target").then(Commands.literal(type.getName())
					.then(Commands.argument("target", EnumArgument.argument(RandomizerTarget.class)).executes((context) -> {
							return updateRandomizerTarget(context.getSource(), type, context.getArgument("target", RandomizerTarget.class));
						})
					)
				)
			);
			builder.then(Commands.literal("disable").then(Commands.literal(type.getName()).executes((context) -> {
						return disableRandomizer(context.getSource(), type);
					})
				)
			);
		}
		builder.then(Commands.literal("storage").then(Commands.literal("list").executes((context) -> {
					return listRandomizers(context.getSource());
				})
			)
			.then(Commands.literal("load").then(Commands.argument("name", StringArgumentType.word()).executes((context) -> {
						return loadRandomizer(context.getSource(), StringArgumentType.getString(context, "name"));
					})
				)
			).then(Commands.literal("save").then(Commands.argument("name", StringArgumentType.word()).executes((context) -> {
						return saveRandomizer(context.getSource(), StringArgumentType.getString(context, "name"));
					})
				)
			).then(Commands.literal("delete").then(Commands.argument("name", StringArgumentType.word()).executes((context) -> {
						return deleteRandomizer(context.getSource(), StringArgumentType.getString(context, "name"));
					})
				)
			)
		);
		
		dispatcher.register(builder);
	}
	
	//region Helper methods
	private static Optional<IMinecraftServer> getMinecraftServer(@NotNull CommandSourceStack source) {
		if (source.getServer() instanceof IMinecraftServer mc) {
			return Optional.of(mc);
		}
		source.sendFailure(Component.translatable("xchallenges.error.critical"));
		XChallenges.LOGGER.error("Minecraft server is not an instance of IMinecraftServer");
		return Optional.empty();
	}
	
	private static boolean allowModifications(@NotNull CommandSourceStack source, @NotNull IMinecraftServer mc) {
		if (!mc.getChallengesManager().allowModifications()) {
			source.sendFailure(Component.translatable("xchallenges.error.modifications_not_allowed", "the randomizer"));
			XChallenges.LOGGER.error("Modifications to the randomizer are not allowed while challenges are running");
			return false;
		}
		return true;
	}
	
	private static @NotNull Component createClickablePath(@NotNull Path path, @NotNull String name) {
		String fullPath = path.toAbsolutePath().normalize().toString();
		return ComponentUtils.wrapInSquareBrackets(Component.literal(name)).withStyle((style -> {
			return style.withColor(ChatFormatting.GREEN)
				.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, fullPath))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(fullPath)));
		}));
	}
	//endregion
	
	private static int getRandomizerStatus(@NotNull CommandSourceStack source, @NotNull RandomizerType<?> type) {
		return getMinecraftServer(source).map(mc -> {
			String status = mc.getRandomizeManager().hasRandomizer(type) ? "enabled" : "disabled";
			source.sendSuccess(() -> Component.translatable("commands.xchallenges.randomizer.status", StringUtils.capitalize(type.getName()), status), false);
			return 0;
		}).orElse(-1);
	}
	
	private static int enableRandomizer(@NotNull CommandSourceStack source, @NotNull RandomizerType<?> type, @NotNull RandomizerTarget target) {
		return getMinecraftServer(source).filter(mc -> allowModifications(source, mc)).map(mc -> {
			if (mc.getRandomizeManager().hasRandomizer(type)) {
				source.sendFailure(Component.translatable("commands.xchallenges.randomizer.enable.failure", type.getName()));
				XChallenges.LOGGER.error("Randomizer {} is already enabled", type.getName());
				return -1;
			}
			mc.getRandomizeManager().createRandomizer(type, target);
			source.sendSuccess(() -> Component.translatable("commands.xchallenges.randomizer.enable.success", type.getName(), target), false);
			return 0;
		}).orElse(-1);
	}
	
	private static int updateRandomizerTarget(@NotNull CommandSourceStack source, @NotNull RandomizerType<?> type, @NotNull RandomizerTarget target) {
		return getMinecraftServer(source).filter(mc -> allowModifications(source, mc)).map(mc -> {
			if (!mc.getRandomizeManager().hasRandomizer(type)) {
				source.sendFailure(Component.translatable("commands.xchallenges.randomizer.update_target.failure", type.getName()));
				XChallenges.LOGGER.error("Randomizer {} is not enabled", type.getName());
				return -1;
			}
			mc.getRandomizeManager().getRandomizer(type).setTarget(target);
			source.sendSuccess(() -> Component.translatable("commands.xchallenges.randomizer.update_target.success", type.getName(), target), false);
			return 0;
		}).orElse(-1);
	}
	
	private static int disableRandomizer(@NotNull CommandSourceStack source, @NotNull RandomizerType<?> type) {
		return getMinecraftServer(source).filter(mc -> allowModifications(source, mc)).map(mc -> {
			if (mc.getRandomizeManager().remove(type)) {
				source.sendSuccess(() -> Component.translatable("commands.xchallenges.randomizer.disable.success", type.getName()), false);
				return 0;
			}
			source.sendFailure(Component.translatable("commands.xchallenges.randomizer.disable.failure", type.getName()));
			XChallenges.LOGGER.error("Randomizer {} is already disabled", type.getName());
			return -1;
		}).orElse(-1);
	}
	
	private static int listRandomizers(@NotNull CommandSourceStack source) {
		return getMinecraftServer(source).map(mc -> {
			Path path = mc.getWorldPath().resolve(BASE_PATH);
			if (Files.notExists(path)) {
				source.sendSuccess(() -> Component.translatable("commands.xchallenges.randomizer.storage.list.not_found"), false);
				XChallenges.LOGGER.error("Randomizer configuration directory {} does not exist", path);
				return 0;
			}
			Collection<File> files = FileUtils.listFiles(path.toFile(), new String[] { "json" }, false);
			if (files.isEmpty()) {
				source.sendSuccess(() -> Component.translatable("commands.xchallenges.randomizer.storage.list.empty"), false);
				XChallenges.LOGGER.info("No randomizer configurations have been saved");
				return 0;
			}
			XChallenges.LOGGER.info("Found {} randomizer configurations", files.size());
			source.sendSuccess(() -> Component.translatable("commands.xchallenges.randomizer.storage.list.header", files.size()), false);
			files.forEach(file -> {
				String name = file.getName().toLowerCase().replace(".json", "");
				source.sendSuccess(() -> Component.translatable("commands.xchallenges.randomizer.storage.list.entry", name), false);
			});
			return 0;
		}).orElse(-1);
	}
	
	private static int loadRandomizer(@NotNull CommandSourceStack source, @NotNull String name) {
		return getMinecraftServer(source).map(mc -> {
			Path path = mc.getWorldPath().resolve(BASE_PATH).resolve(name + ".json");
			if (Files.notExists(path)) {
				source.sendFailure(Component.translatable("commands.xchallenges.randomizer.storage.load.failure", name, path.getParent()));
				XChallenges.LOGGER.error("Randomizer configuration {} does not exist", path);
				return -1;
			}
			mc.getRandomizeManager().load(mc.getWorldPath().resolve(BASE_PATH), name);
			XChallenges.LOGGER.info("Loaded randomizer configuration from {}", path);
			source.sendSuccess(() -> Component.translatable("commands.xchallenges.randomizer.storage.load.success", name), false);
			return 0;
		}).orElse(-1);
	}
	
	private static int saveRandomizer(@NotNull CommandSourceStack source, @NotNull String name) {
		return getMinecraftServer(source).map(mc -> {
			Path path = mc.getWorldPath().resolve(BASE_PATH).resolve(name + ".json");
			if (Files.exists(path)) {
				source.sendFailure(Component.translatable("commands.xchallenges.randomizer.storage.save.failure", name, path.getParent()));
				XChallenges.LOGGER.error("Randomizer configuration {} already exists", path);
				return -1;
			}
			mc.getRandomizeManager().save(mc.getWorldPath().resolve(BASE_PATH), name);
			XChallenges.LOGGER.info("Saved current randomizer configuration to {}", path);
			source.sendSuccess(() -> Component.translatable("commands.xchallenges.randomizer.storage.save.success").append(" ").append(createClickablePath(path, name)), false);
			return 0;
		}).orElse(-1);
	}
	
	private static int deleteRandomizer(@NotNull CommandSourceStack source, @NotNull String name) {
		return getMinecraftServer(source).map(mc -> {
			Path path = mc.getWorldPath().resolve(BASE_PATH).resolve(name + ".json");
			if (Files.notExists(path)) {
				source.sendFailure(Component.translatable("commands.xchallenges.randomizer.storage.delete.not_found", name, path.getParent()));
				XChallenges.LOGGER.error("Randomizer configuration {} does not exist", path);
				return -1;
			}
			if (!path.toFile().isFile()) {
				source.sendFailure(Component.translatable("commands.xchallenges.randomizer.storage.delete.is_directory", name));
				return -1;
			}
			path.toFile().deleteOnExit();
			XChallenges.LOGGER.info("Marked randomizer configuration {} for deletion", path);
			source.sendSuccess(() -> Component.translatable("commands.xchallenges.randomizer.storage.delete.success", name), false);
			return 0;
		}).orElse(-1);
	}
}

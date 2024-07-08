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

package net.luis.xchallenges.event;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.tree.CommandNode;
import net.luis.xchallenges.XChallenges;
import net.luis.xchallenges.challenges.Challenges;
import net.luis.xchallenges.challenges.ChallengesHelper;
import net.luis.xchallenges.challenges.randomizer.RandomizerInstance;
import net.luis.xchallenges.challenges.randomizer.RandomizerType;
import net.luis.xchallenges.server.CommandBroadcastMode;
import net.luis.xchallenges.server.IMinecraftServer;
import net.luis.xchallenges.world.entity.RandomizedEntityProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ClientCommandSourceStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

@Mod.EventBusSubscriber(modid = XChallenges.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
	
	@SubscribeEvent
	public static void playerLoggedIn(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			if (!(player.getServer() instanceof IMinecraftServer mc)) {
				XChallenges.LOGGER.error("Server is not an instance of IMinecraftServer");
				return;
			}
			mc.getTimer().sync();
			player.sendSystemMessage(Component.literal("Executed commands on this server with permission level " + mc.getBroadcastMode() + " are broadcast to all players"));
		}
	}
	
	@SubscribeEvent
	public static void command(@NotNull CommandEvent event) {
		ParseResults<CommandSourceStack> results = event.getParseResults();
		CommandSourceStack source = results.getContext().getSource();
		String command = results.getReader().getRead();
		if (source instanceof ClientCommandSourceStack) {
			return; // Ignore client commands
		}
		MinecraftServer server = source.getServer();
		if (!(server instanceof IMinecraftServer mc)) {
			XChallenges.LOGGER.error("Server is not an instance of IMinecraftServer");
			event.setCanceled(true); // No commands allowed
			return;
		}
		CommandBroadcastMode mode = mc.getBroadcastMode();
		if (mode == CommandBroadcastMode.ALL) {
			broadcastCommand(command, getExecutor(source));
			return;
		}
		CommandNode<CommandSourceStack> root = results.getContext().getRootNode();
		if (mode == CommandBroadcastMode.ADMIN && isNoneAdminCommand(root, source)) {
			return; // Only admin commands are allowed
		}
		broadcastCommand(command, getExecutor(source));
	}
	
	@SubscribeEvent
	public static void attachEntityCapabilities(@NotNull AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (ChallengesHelper.shouldIgnoreEntity(entity) || !(entity.level() instanceof ServerLevel)) {
			return;
		}
		event.addCapability(new ResourceLocation(XChallenges.MOD_ID, "randomized_entity_capability"), new RandomizedEntityProvider());
	}
	
	@SubscribeEvent
	public static void entityJoinLevel(@NotNull EntityJoinLevelEvent event) {
		if (ChallengesHelper.shouldIgnoreEntity(event.getEntity()) || !(event.getLevel() instanceof ServerLevel level)) {
			return;
		}
		Entity entity = event.getEntity();
		RandomizerInstance<EntityType<?>> instance = Challenges.get().getRandomizer().get(RandomizerType.ENTITIES);
		if (RandomizedEntityProvider.get(entity).isRandomized() || instance == null) {
			return;
		}
		EntityType<?> entityType = instance.getRandomized(entity.getType(), null);
		if (!entityType.isEnabled(level.enabledFeatures())) {
			return;
		}
		Entity randomized = entityType.create(level);
		if (randomized == null) {
			XChallenges.LOGGER.error("Failed to create randomized entity for {}", entityType.getDescriptionId());
			return;
		}
		randomized.copyPosition(entity);
		RandomizedEntityProvider.get(entity).setRandomized();
		event.setCanceled(true);
		level.addFreshEntity(randomized);
	}
	
	//region Helper methods
	private static @NotNull String getExecutor(@NotNull CommandSourceStack source) {
		if (source.getEntity() instanceof ServerPlayer player) {
			return player.getGameProfile().getName();
		}
		if (source.getEntity() instanceof MinecartCommandBlock) {
			return "Command Block Minecart";
		}
		if (source.getPosition() != Vec3.ZERO && source.getRotation() != Vec2.ZERO) {
			return "Command Block";
		}
		return "Server Console";
	}
	
	private static void broadcastCommand(@NotNull String command, @NotNull String executor) {
		ChallengesHelper.sendToAllPlayers(executor + " executed command '/" + command + "'");
	}
	
	private static boolean isNoneAdminCommand(@NotNull CommandNode<CommandSourceStack> root, @NotNull CommandSourceStack source) {
		PlayerList list = source.getServer().getPlayerList();
		return list.getPlayers().stream()
			.filter(player -> !list.isOp(player.getGameProfile())) // Remove ops
			.allMatch(player -> root.canUse(player.createCommandSourceStack())); // Check if all non-ops can use the command
	}
	//endregion
}

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

package net.luis.xchallenges.event.registry;

import net.luis.xchallenges.XChallenges;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 *
 * @author Luis-St
 *
 */

@Mod.EventBusSubscriber(modid = XChallenges.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterModEventHandler {
	
	@SubscribeEvent
	public static void addPackFinders(@NotNull AddPackFindersEvent event) {}
	
	private static void addServerPack(@NotNull AddPackFindersEvent event, @NotNull String packName, @NotNull String displayName, @NotNull PackSource source) {
		Path resourcePath = ModList.get().getModFileById(XChallenges.MOD_ID).getFile().findResource(packName);
		PathPackResources.PathResourcesSupplier resourcesSupplier = new PathPackResources.PathResourcesSupplier(resourcePath, false);
		Pack pack = Pack.readMetaAndCreate("builtin/" + packName, Component.literal(displayName), false, resourcesSupplier, PackType.SERVER_DATA, Pack.Position.TOP, source);
		event.addRepositorySource(packConsumer -> packConsumer.accept(pack));
	}
}

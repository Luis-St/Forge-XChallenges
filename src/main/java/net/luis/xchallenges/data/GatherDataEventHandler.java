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

package net.luis.xchallenges.data;

import net.luis.xchallenges.XChallenges;
import net.luis.xchallenges.data.provider.language.XCLanguageProvider;
import net.luis.xchallenges.data.provider.loot.XCGlobalLootModifierProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 *
 * @author Luis-St
 *
 */

@EventBusSubscriber(modid = XChallenges.MOD_ID, bus = Bus.MOD)
public class GatherDataEventHandler {
	
	@SubscribeEvent
	public static void gatherData(@NotNull GatherDataEvent event) throws IOException {
		DataGenerator generator = event.getGenerator();
		if (event.includeDev()) {
			generator.addProvider(event.includeClient(), new XCLanguageProvider(generator));
			//generator.addProvider(event.includeServer(), new XCGlobalLootModifierProvider(generator));
		}
	}
}

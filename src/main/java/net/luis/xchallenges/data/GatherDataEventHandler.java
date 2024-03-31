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
import net.luis.xchallenges.data.provider.XCBuiltinProvider;
import net.luis.xchallenges.data.provider.block.XCBlockStateProvider;
import net.luis.xchallenges.data.provider.language.XCLanguageProvider;
import net.luis.xchallenges.data.provider.loot.XCGlobalLootModifierProvider;
import net.luis.xchallenges.data.provider.loottable.XCLootTableProvider;
import net.luis.xchallenges.data.provider.recipe.XCRecipeProvider;
import net.luis.xchallenges.data.provider.tag.*;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

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
			generator.addProvider(event.includeServer(), new XCLootTableProvider(generator));
			generator.addProvider(event.includeServer(), new XCRecipeProvider(generator));
			XCBlockTagsProvider blockTagsProvider = new XCBlockTagsProvider(generator, event.getLookupProvider(), event.getExistingFileHelper());
			generator.addProvider(event.includeServer(), blockTagsProvider);
			generator.addProvider(event.includeServer(), new XCItemTagsProvider(generator, event.getLookupProvider(), blockTagsProvider.contentsGetter(), event.getExistingFileHelper()));
			generator.addProvider(event.includeServer(), new XCPoiTypeTagsProvider(generator, event.getLookupProvider(), event.getExistingFileHelper()));
			generator.addProvider(event.includeServer(), new XCBiomeTagsProvider(generator, event.getLookupProvider(), event.getExistingFileHelper()));
			generator.addProvider(event.includeServer(), new XCDamageTypeTagsProvider(generator, event.getLookupProvider(), event.getExistingFileHelper()));
			generator.addProvider(event.includeServer(), new XCGlobalLootModifierProvider(generator));
			generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(generator.getPackOutput(), event.getLookupProvider(), XCBuiltinProvider.createProvider(), Set.of(XChallenges.MOD_ID)));
		}
	}
}

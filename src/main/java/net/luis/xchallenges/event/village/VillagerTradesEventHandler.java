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

package net.luis.xchallenges.event.village;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.luis.xchallenges.XChallenges;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

@EventBusSubscriber(modid = XChallenges.MOD_ID)
public class VillagerTradesEventHandler {
	
	@SubscribeEvent
	public static void villagerTrades(@NotNull VillagerTradesEvent event) {
		Int2ObjectMap<List<ItemListing>> trades = event.getTrades();
		List<ItemListing> trade1 = Lists.newArrayList();
		List<ItemListing> trade2 = Lists.newArrayList();
		List<ItemListing> trade3 = Lists.newArrayList();
		List<ItemListing> trade4 = Lists.newArrayList();
		List<ItemListing> trade5 = Lists.newArrayList();
		if (!trade1.isEmpty()) {
			trades.put(1, trade1);
		}
		if (!trade2.isEmpty()) {
			trades.put(2, trade2);
		}
		if (!trade3.isEmpty()) {
			trades.put(3, trade3);
		}
		if (!trade4.isEmpty()) {
			trades.put(4, trade4);
		}
		if (!trade5.isEmpty()) {
			trades.put(5, trade5);
		}
	}
}

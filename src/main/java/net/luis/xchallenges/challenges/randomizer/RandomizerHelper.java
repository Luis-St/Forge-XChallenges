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

package net.luis.xchallenges.challenges.randomizer;

import net.luis.xchallenges.challenges.ChallengesHelper;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.flag.FeatureFlags;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public class RandomizerHelper {
	
	public static @NotNull @Unmodifiable List<VillagerTrades.ItemListing> getTrades(@NotNull VillagerProfession profession) {
		var trades = VillagerTrades.TRADES.get(profession);
		if (ChallengesHelper.getServer().getWorldData().enabledFeatures().contains(FeatureFlags.TRADE_REBALANCE)) {
			var temp = VillagerTrades.EXPERIMENTAL_TRADES.get(profession);
			if (temp != null) {
				trades = temp;
			}
		}
		return trades.values().stream().flatMap(Arrays::stream).toList();
	}
	
	public static @NotNull @Unmodifiable List<VillagerTrades.ItemListing> getWanderingTraderTrades() {
		if (ChallengesHelper.getServer().getWorldData().enabledFeatures().contains(FeatureFlags.TRADE_REBALANCE)) {
			return VillagerTrades.EXPERIMENTAL_WANDERING_TRADER_TRADES.stream().map(Pair::getKey).flatMap(Arrays::stream).toList();
		}
		return VillagerTrades.WANDERING_TRADER_TRADES.values().stream().flatMap(Arrays::stream).toList();
	}
}

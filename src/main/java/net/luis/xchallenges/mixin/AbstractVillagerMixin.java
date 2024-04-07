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

package net.luis.xchallenges.mixin;

import com.google.common.collect.Lists;
import net.luis.xchallenges.challenges.Challenges;
import net.luis.xchallenges.challenges.randomizer.RandomizerType;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin extends AgeableMob {
	
	//region Mixin
	private AbstractVillagerMixin(EntityType<? extends AgeableMob> entityType, Level level) {
		super(entityType, level);
	}
	//endregion
	
	@Inject(method = "addOffersFromItemListings", at = @At("HEAD"), cancellable = true)
	protected void addOffersFromItemListings(MerchantOffers offers, VillagerTrades.ItemListing[] possibleTrades, int count, CallbackInfo callback) {
		List<VillagerTrades.ItemListing> trades = Lists.newArrayList(possibleTrades);
		while (trades.size() > count) {
			trades.remove(this.random.nextInt(trades.size()));
		}
		Challenges.get().getRandomizerIfActive().filter(randomizer -> randomizer.has(RandomizerType.TRADES)).map(randomizer -> randomizer.get(RandomizerType.TRADES)).ifPresentOrElse(randomizer -> {
			trades.forEach(trade -> {
				MerchantOffer offer = randomizer.getRandomized(trade, null).getOffer(this, this.random);
				if (offer != null) {
					offers.add(offer);
				}
			});
		}, () -> {
			trades.stream().map(trade -> trade.getOffer(this, this.random)).filter(Objects::nonNull).forEach(offers::add);
		});
		callback.cancel();
	}
}

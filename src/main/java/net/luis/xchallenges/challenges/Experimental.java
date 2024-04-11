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

package net.luis.xchallenges.challenges;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.BasicItemListing;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author Luis-St
 *
 */

public class Experimental {
	
	private static final Supplier<Map<Integer, List<ItemLike>>> WOOL = Suppliers.memoize(() -> {
		Map<Integer, List<ItemLike>> wools = Maps.newHashMap();
		wools.put(1, Lists.newArrayList(Blocks.WHITE_WOOL, Blocks.BROWN_WOOL, Blocks.BLACK_WOOL, Blocks.GRAY_WOOL));
		wools.put(2, Lists.newArrayList(Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL, Blocks.MAGENTA_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.YELLOW_WOOL, Blocks.LIME_WOOL, Blocks.PINK_WOOL, Blocks.GRAY_WOOL, Blocks.LIGHT_GRAY_WOOL,
			Blocks.CYAN_WOOL, Blocks.PURPLE_WOOL, Blocks.BLUE_WOOL, Blocks.BROWN_WOOL, Blocks.GREEN_WOOL, Blocks.RED_WOOL, Blocks.BLACK_WOOL));
		return wools;
	});
	private static final Supplier<Map<Integer, List<Item>>> DYE = Suppliers.memoize(() -> {
		Map<Integer, List<Item>> dyes = Maps.newHashMap();
		dyes.put(2, Lists.newArrayList(Items.WHITE_DYE, Items.GRAY_DYE, Items.BLACK_DYE, Items.LIGHT_BLUE_DYE, Items.LIME_DYE));
		dyes.put(3, Lists.newArrayList(Items.YELLOW_DYE, Items.LIGHT_GRAY_DYE, Items.ORANGE_DYE, Items.RED_DYE, Items.PINK_DYE));
		dyes.put(4, Lists.newArrayList(Items.BROWN_DYE, Items.PURPLE_DYE, Items.BLUE_DYE, Items.GREEN_DYE, Items.MAGENTA_DYE, Items.CYAN_DYE));
		return dyes;
	});
	private static final Supplier<List<ItemLike>> CARPET = Suppliers.memoize(() -> {
		List<ItemLike> carpets = Lists.newArrayList();
		carpets.add(Blocks.WHITE_CARPET);
		carpets.add(Blocks.ORANGE_CARPET);
		carpets.add(Blocks.MAGENTA_CARPET);
		carpets.add(Blocks.LIGHT_BLUE_CARPET);
		carpets.add(Blocks.YELLOW_CARPET);
		carpets.add(Blocks.LIME_CARPET);
		carpets.add(Blocks.PINK_CARPET);
		carpets.add(Blocks.GRAY_CARPET);
		carpets.add(Blocks.LIGHT_GRAY_CARPET);
		carpets.add(Blocks.CYAN_CARPET);
		carpets.add(Blocks.PURPLE_CARPET);
		carpets.add(Blocks.BLUE_CARPET);
		carpets.add(Blocks.BROWN_CARPET);
		carpets.add(Blocks.GREEN_CARPET);
		carpets.add(Blocks.RED_CARPET);
		carpets.add(Blocks.BLACK_CARPET);
		return carpets;
	});
	private static final Supplier<List<ItemLike>> BED = Suppliers.memoize(() -> {
		List<ItemLike> beds = Lists.newArrayList();
		beds.add(Blocks.WHITE_BED);
		beds.add(Blocks.YELLOW_BED);
		beds.add(Blocks.RED_BED);
		beds.add(Blocks.BLACK_BED);
		beds.add(Blocks.BLUE_BED);
		beds.add(Blocks.BROWN_BED);
		beds.add(Blocks.CYAN_BED);
		beds.add(Blocks.GRAY_BED);
		beds.add(Blocks.GREEN_BED);
		beds.add(Blocks.LIGHT_BLUE_BED);
		beds.add(Blocks.LIGHT_GRAY_BED);
		beds.add(Blocks.LIME_BED);
		beds.add(Blocks.MAGENTA_BED);
		beds.add(Blocks.ORANGE_BED);
		beds.add(Blocks.PINK_BED);
		beds.add(Blocks.PURPLE_BED);
		return beds;
	});
	private static final Supplier<List<Item>> BANNER = Suppliers.memoize(() -> {
		List<Item> banners = Lists.newArrayList();
		banners.add(Items.WHITE_BANNER);
		banners.add(Items.BLUE_BANNER);
		banners.add(Items.LIGHT_BLUE_BANNER);
		banners.add(Items.RED_BANNER);
		banners.add(Items.PINK_BANNER);
		banners.add(Items.GREEN_BANNER);
		banners.add(Items.LIME_BANNER);
		banners.add(Items.GRAY_BANNER);
		banners.add(Items.BLACK_BANNER);
		banners.add(Items.PURPLE_BANNER);
		banners.add(Items.MAGENTA_BANNER);
		banners.add(Items.CYAN_BANNER);
		banners.add(Items.BROWN_BANNER);
		banners.add(Items.YELLOW_BANNER);
		banners.add(Items.ORANGE_BANNER);
		banners.add(Items.LIGHT_GRAY_BANNER);
		return banners;
	});
	
	//region Shepherd trades
	private static @NotNull Int2ObjectMap<VillagerTrades.ItemListing[]> shepherdTrades() {
		Int2ObjectMap<VillagerTrades.ItemListing[]> trades = new Int2ObjectOpenHashMap<>();
		trades.put(1, new VillagerTrades.ItemListing[] { woolSell(), woolSell(), new BasicItemListing(2, new ItemStack(Items.SHEARS), 12, 1, 0.05F) });
		trades.put(2, new VillagerTrades.ItemListing[] { dye(2), dye(2), woolBuy(), woolBuy(), carpet(), carpet() });
		trades.put(3, new VillagerTrades.ItemListing[] { dye(3), dye(3), bed(), bed() });
		trades.put(4, new VillagerTrades.ItemListing[] { dye(4), dye(4), banner(), banner() });
		return trades;
	}
	
	private static @NotNull VillagerTrades.ItemListing woolSell() {
		return (entity, random) -> {
			List<ItemLike> wools = WOOL.get().get(1);
			ItemStack stack = new ItemStack(wools.get(random.nextInt(wools.size())), 18);
			return new MerchantOffer(stack, new ItemStack(Items.EMERALD), 16, 2, 0.05F);
		};
	}
	
	private static @NotNull VillagerTrades.ItemListing woolBuy() {
		return (entity, random) -> {
			List<ItemLike> wools = WOOL.get().get(2);
			ItemStack stack = new ItemStack(wools.get(random.nextInt(wools.size())));
			return new MerchantOffer(new ItemStack(Items.EMERALD), stack, 16, 5, 0.05F);
		};
	}
	
	private static @NotNull VillagerTrades.ItemListing dye(int level) {
		List<Item> dyes = DYE.get().get(level);
		return (entity, random) -> {
			ItemStack stack = new ItemStack(dyes.get(random.nextInt(dyes.size())));
			return new MerchantOffer(stack, new ItemStack(Items.EMERALD), 16, (level - 1) * 10, 0.05F);
		};
	}
	
	private static @NotNull VillagerTrades.ItemListing carpet() {
		return (entity, random) -> {
			List<ItemLike> carpets = CARPET.get();
			ItemStack stack = new ItemStack(carpets.get(random.nextInt(carpets.size())), 4);
			return new MerchantOffer(new ItemStack(Items.EMERALD), stack, 16, 5, 0.05F);
		};
	}
	
	private static @NotNull VillagerTrades.ItemListing bed() {
		return (entity, random) -> {
			List<ItemLike> beds = BED.get();
			ItemStack stack = new ItemStack(beds.get(random.nextInt(beds.size())));
			return new MerchantOffer(new ItemStack(Items.EMERALD, 3), stack, 12, 10, 0.05F);
		};
	}
	
	private static @NotNull VillagerTrades.ItemListing banner() {
		return (entity, random) -> {
			List<Item> banners = BANNER.get();
			ItemStack stack = new ItemStack(banners.get(random.nextInt(banners.size())));
			return new MerchantOffer(new ItemStack(Items.EMERALD, 3), stack, 12, 15, 0.05F);
		};
	}
	//endregion
}

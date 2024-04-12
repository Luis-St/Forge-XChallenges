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

package net.luis.xchallenges.world.loot;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.luis.xchallenges.challenges.Challenges;
import net.luis.xchallenges.challenges.randomizer.*;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public class RandomizerLootModifier extends LootModifier {
	
	public static final Codec<RandomizerLootModifier> CODEC = RecordCodecBuilder.create((instance) -> {
		return codecStart(instance).apply(instance, RandomizerLootModifier::new);
	});
	private final Map<String, RandomizerType<Item>> handlers = Util.make(Maps.newHashMap(), (map) -> {
		map.put("blocks", RandomizerType.BLOCK_LOOT);
		map.put("entities", RandomizerType.ENTITY_LOOT);
		map.put("chests", RandomizerType.CHEST_LOOT);
		map.put("gameplay", RandomizerType.GAMEPLAY_LOOT);
		map.put("archaeology", RandomizerType.GAMEPLAY_LOOT);
	});
	
	public RandomizerLootModifier(LootItemCondition[] conditions) {
		super(conditions);
	}
	
	@Override
	public @NotNull Codec<? extends IGlobalLootModifier> codec() {
		return XCGlobalLootModifiers.RANDOMIZER_LOOT_MODIFIER.get();
	}
	
	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull LootContext context) {
		return this.getRandomizer(Challenges.get().getRandomizer(), context.getQueriedLootTableId()).map(instance -> {
			return this.doRandomize(generatedLoot, instance, this.getPlayer(context));
		}).orElse(generatedLoot);
	}
	
	private Optional<RandomizerInstance<Item>> getRandomizer(@NotNull Randomizer randomizer, @NotNull ResourceLocation location) {
		for (Map.Entry<String, RandomizerType<Item>> entry : this.handlers.entrySet()) {
			if (location.getPath().contains(entry.getKey())) {
				return randomizer.getIfActive(entry.getValue());
			}
		}
		return randomizer.getIfActive(RandomizerType.GAMEPLAY_LOOT);
	}
	
	private @Nullable ServerPlayer getPlayer(@NotNull LootContext context) {
		Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
		if (entity instanceof ServerPlayer player) {
			return player;
		} else if (entity instanceof PrimedTnt tnt && tnt.getOwner() instanceof ServerPlayer player) {
			return player;
		} else if (entity instanceof Mob mob && mob.getTarget() instanceof ServerPlayer player) {
			return player;
		}
		return null;
	}
	
	private @NotNull ObjectArrayList<ItemStack> doRandomize(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull RandomizerInstance<Item> instance, @Nullable ServerPlayer player) {
		ObjectArrayList<ItemStack> loot = new ObjectArrayList<>();
		for (ItemStack stack : generatedLoot) {
			Item randomized = instance.getRandomized(stack.getItem(), player);
			loot.add(new ItemStack(randomized, stack.getCount()));
		}
		return loot;
	}
}

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

package net.luis.xchallenges.data.provider.loottable;

import com.google.common.collect.Lists;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author Luis-St
 *
 */

public class XCLootTableProvider extends LootTableProvider {
	
	public XCLootTableProvider(@NotNull DataGenerator generator) {
		super(generator.getPackOutput(), Set.of(), Lists.newArrayList(new SubProviderEntry(XCBlockLootSubProvider::new, LootContextParamSets.BLOCK)));
	}
	
	@Override
	protected void validate(@NotNull Map<ResourceLocation, LootTable> lootTables, @NotNull ValidationContext validationContext) {}
}
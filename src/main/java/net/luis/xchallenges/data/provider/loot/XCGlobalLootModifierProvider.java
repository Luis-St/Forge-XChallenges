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

package net.luis.xchallenges.data.provider.loot;

import net.luis.xchallenges.XChallenges;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public class XCGlobalLootModifierProvider extends GlobalLootModifierProvider {
	
	public XCGlobalLootModifierProvider(@NotNull DataGenerator generator) {
		super(generator.getPackOutput(), XChallenges.MOD_ID);
	}
	
	@Override
	protected void start() {}
	
	@Override
	public @NotNull String getName() {
		return "XChallenges Global Loot Modifiers";
	}
}
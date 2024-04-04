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

package net.luis.xchallenges;

import com.mojang.logging.LogUtils;
import net.luis.xchallenges.network.XCNetworkHandler;
import net.luis.xchallenges.server.commands.arguments.XCCommandArgumentTypes;
import net.luis.xchallenges.world.loot.XCGlobalLootModifiers;
import net.luis.xchallenges.world.loot.predicates.XCLootItemConditions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 *
 * @author Luis-St
 *
 */

@Mod(XChallenges.MOD_ID)
public class XChallenges {
	
	public static final String MOD_ID = "xchallenges";
	public static final String MOD_NAME = "XChallenges";
	public static final Logger LOGGER = LogUtils.getLogger();
	
	public XChallenges() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		XCLootItemConditions.LOOT_ITEM_CONDITIONS.register(eventBus);
		XCGlobalLootModifiers.LOOT_MODIFIERS.register(eventBus);
		XCCommandArgumentTypes.COMMAND_ARGUMENT_TYPES.register(eventBus);
		XCNetworkHandler.INSTANCE.initChannel();
		XCNetworkHandler.INSTANCE.registerPackets();
	}
}

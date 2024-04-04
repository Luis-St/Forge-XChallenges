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

package net.luis.xchallenges.server.commands.arguments;

import net.luis.xchallenges.XChallenges;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraftforge.registries.*;

/**
 *
 * @author Luis-St
 *
 */

public class XCCommandArgumentTypes {
	
	public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, XChallenges.MOD_ID);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final RegistryObject<EnumArgument.Info<?>> ENUM = COMMAND_ARGUMENT_TYPES.register("enum", () -> {
		return ArgumentTypeInfos.registerByClass(EnumArgument.class, new EnumArgument.Info());
	});
}

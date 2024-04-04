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

package net.luis.xchallenges.data.provider.language;

import net.luis.xchallenges.XChallenges;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.LanguageProvider;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 *
 * @author Luis-St
 *
 */

public class XCLanguageProvider extends LanguageProvider {
	
	public XCLanguageProvider(@NotNull DataGenerator generator) {
		super(generator.getPackOutput(), XChallenges.MOD_ID, "en_us");
	}
	
	public static @NotNull String getName(ResourceLocation location) {
		StringBuilder name = new StringBuilder();
		if (location != null) {
			Stream.of(location.getPath().split("_")).map(StringUtils::capitalize).forEach(s -> name.append(s).append(" "));
		}
		return name.toString().trim();
	}
	
	public static @NotNull String getLocalizedName(ResourceLocation location) {
		String name = getName(location);
		if (name.contains(" Of ")) {
			return name.replace(" Of ", " of ");
		}
		if (name.contains(" The ")) {
			return name.replace(" The ", " the ");
		}
		return name;
	}
	
	@Override
	protected void addTranslations() {
		String id = XChallenges.MOD_ID;
		this.add(id + ".error.critical", "A critical server error occurred, please check the server log for more information");
		
		this.add("commands." + id + ".timer.start", "Timer started");
		this.add("commands." + id + ".timer.stop", "Timer stopped");
		this.add("commands." + id + ".timer.pause", "Timer paused");
		this.add("commands." + id + ".timer.resume", "Timer resumed");
		this.add("commands." + id + ".timer.reset", "Timer reset");
		this.add("commands." + id + ".timer.set", "Set timer");
		
		this.add("arguments." + id + ".enum.invalid", "Invalid enum {0}");
		this.add("arguments." + id + ".real_time.invalid_unit", "Invalid unit {0}");
		this.add("arguments." + id + ".real_time.time_too_low", "Time {0} scaled with unit {1} is too low, minimum is {2}");
	}
	
	@Override
	public @NotNull String getName() {
		return "XChallenges Languages";
	}
}

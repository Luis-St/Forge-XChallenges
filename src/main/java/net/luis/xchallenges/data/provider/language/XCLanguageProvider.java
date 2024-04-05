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
		// Global errors
		this.add(id + ".error.critical", "A critical server error occurred, please check the server log for more information");
		this.add(id + ".error.modifications_not_allowed", "Modifications to {0} are not allowed while challenges are running");
		// Command arguments
		this.add("arguments." + id + ".enum.invalid", "Invalid enum provided {0}");
		this.add("arguments." + id + ".real_time.invalid_unit", "Invalid unit provided {0}");
		this.add("arguments." + id + ".real_time.time_too_low", "Time {0} scaled with unit {1} is too low, minimum is {2}");
		// Challenges command
		this.add("commands." + id + ".challenges.start", "Started challenges");
		this.add("commands." + id + ".challenges.stop", "Stopped challenges");
		// Timer command
		this.add("commands." + id + ".timer.start", "Timer started");
		this.add("commands." + id + ".timer.stop", "Timer stopped");
		this.add("commands." + id + ".timer.pause", "Timer paused");
		this.add("commands." + id + ".timer.resume", "Timer resumed");
		this.add("commands." + id + ".timer.reset_current", "Reset timer to current configuration");
		this.add("commands." + id + ".timer.reset_default", "Reset timer to default configuration");
		this.add("commands." + id + ".timer.set", "Set timer");
		// Randomizer command
		this.add("commands." + id + ".randomizer.status", "{0} randomizer is {1}");
		this.add("commands." + id + ".randomizer.enable.success", "Enabled randomizer {0} with target {1}");
		this.add("commands." + id + ".randomizer.enable.failure", "Randomizer {0} is already enabled");
		this.add("commands." + id + ".randomizer.update_target.success", "Updated target of randomizer {0} to {1}");
		this.add("commands." + id + ".randomizer.update_target.failure", "Randomizer {0} is not enabled");
		this.add("commands." + id + ".randomizer.disable.success", "Disabled randomizer {0}");
		this.add("commands." + id + ".randomizer.disable.failure", "Randomizer {0} is already disabled");
		this.add("commands." + id + ".randomizer.storage.list.not_found", "No randomizers found");
		this.add("commands." + id + ".randomizer.storage.list.empty", "No randomizer configurations have been saved");
		this.add("commands." + id + ".randomizer.storage.list.header", "Found {0} randomizer configurations:");
		this.add("commands." + id + ".randomizer.storage.list.entry", " - {0}");
		this.add("commands." + id + ".randomizer.storage.load.success", "Loaded randomizer configuration {0}");
		this.add("commands." + id + ".randomizer.storage.load.failure", "Randomizer configuration {0} does not exist in {1}");
		this.add("commands." + id + ".randomizer.storage.save.success", "Saved current randomizer configuration to");
		this.add("commands." + id + ".randomizer.storage.save.failure", "A configuration with name {0} already exists in {1}");
		this.add("commands." + id + ".randomizer.storage.delete.success", "Marked randomizer configuration {0} for deletion, the configuration will be deleted on server restart");
		this.add("commands." + id + ".randomizer.storage.delete.not_found", "Randomizer configuration {0} does not exist in {1}");
		this.add("commands." + id + ".randomizer.storage.delete.is_directory", "Expected a file named {0}, but found a directory");
	}
	
	@Override
	public @NotNull String getName() {
		return "XChallenges Languages";
	}
}

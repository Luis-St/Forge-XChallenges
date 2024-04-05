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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.luis.xchallenges.XChallenges;
import net.luis.xchallenges.network.XCNetworkHandler;
import net.luis.xchallenges.network.packet.SyncTimerPacket;
import net.luis.xchallenges.server.CodecHelper;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.time.Duration;

/**
 *
 * @author Luis-St
 *
 */

public class Timer {
	
	public static final Codec<Timer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.LONG.fieldOf("ticks").forGetter(Timer::getTicks),
		Codec.LONG.fieldOf("current_ticks").forGetter(Timer::getCurrentTicks),
		Codec.BOOL.fieldOf("running").forGetter(Timer::isPaused)
	).apply(instance, Timer::new));
	
	protected long ticks = -1;
	protected long currentTicks;
	protected boolean running;
	
	private Timer() {}
	
	private Timer(long ticks, long currentTicks, boolean running) {
		this.ticks = ticks;
		this.currentTicks = currentTicks;
		this.running = running;
	}
	
	public static @NotNull Timer create() {
		return new Timer();
	}
	
	//region Getters
	public boolean isUp() {
		return this.ticks == -1;
	}
	
	public boolean isDown() {
		return this.ticks > 0;
	}
	
	public long getTicks() {
		return this.ticks;
	}
	
	public long getCurrentTicks() {
		return this.currentTicks;
	}
	
	public boolean isPaused() {
		return !this.running;
	}
	//endregion
	
	public void tick() {
		if (!this.running) {
			return;
		}
		if (this.ticks == -1) {
			this.currentTicks++;
		} else if (this.ticks > 0) {
			if (this.currentTicks > 0) {
				this.currentTicks--;
			} else {
				this.currentTicks = 0;
				this.running = false;
				this.sync();
			}
		}
	}
	
	//region Actions
	public void pause() {
		this.running = false;
	}
	
	public void resume() {
		this.running = true;
	}
	
	public void resetCurrent() {
		if (this.ticks == -1) {
			this.currentTicks = 0;
		} else if (this.ticks > 0) {
			this.currentTicks = this.ticks;
		}
	}
	
	public void resetDefault() {
		this.ticks = -1;
		this.currentTicks = 0;
		this.running = false;
	}
	
	public void makeUp() {
		this.ticks = -1;
		this.currentTicks = 0;
		this.running = false;
	}
	
	public void makeDown(long ticks) {
		if (0 >= ticks) {
			throw new IllegalArgumentException("The ticks must be greater than 0");
		}
		this.ticks = ticks;
		this.currentTicks = ticks;
		this.running = false;
	}
	//endregion
	
	//region IO operations
	public void load(@NotNull Path path) {
		Timer loaded = CodecHelper.load(Timer.CODEC, path.resolve("timer.json"));
		XChallenges.LOGGER.info("Loaded timer '{}' from '{}'", loaded, path.resolve("timer.json"));
		if (loaded != null) {
			this.ticks = loaded.ticks;
			this.currentTicks = loaded.currentTicks;
			this.running = loaded.running;
		}
	}
	
	public void sync() {
		XChallenges.LOGGER.info("Syncing timer '{}' to players", this.toString());
		XCNetworkHandler.INSTANCE.sendToPlayers(new SyncTimerPacket(this.running, this.ticks, this.currentTicks));
	}
	
	public void update(boolean running, long ticks, long currentTicks) {
		String old = this.toString();
		this.running = running;
		this.ticks = ticks;
		this.currentTicks = currentTicks;
		XChallenges.LOGGER.info("Synced timer from '{}' (old, client) to '{}' (new, server)", old, this.toString());
	}
	
	public void save(@NotNull Path path) {
		CodecHelper.save(this, Timer.CODEC, path.resolve("timer.json"));
		XChallenges.LOGGER.info("Saved timer '{}' to '{}'", this.toString(), path.resolve("timer.json"));
	}
	//endregion
	
	//region Object overrides
	@Override
	public String toString() {
		if (!this.running) {
			if (this.ticks == -1) {
				return "Paused";
			} else if (this.ticks > 0) {
				if (this.currentTicks == this.ticks) {
					return this.toString(this.ticks);
				} else if (this.currentTicks == 0) {
					return "Finished";
				}
			}
			return "Paused";
		}
		return this.toString(this.currentTicks);
	}
	
	private @NotNull String toString(long ticks) {
		Duration duration = Duration.ofMillis(ticks * 50);
		StringBuilder sb = new StringBuilder();
		if (duration.toDays() > 0) {
			sb.append(duration.toDays());
			sb.append("d ");
		}
		if (duration.toHours() > 0) {
			sb.append(duration.toHours() % 24);
			sb.append("h ");
		}
		if (duration.toMinutes() > 0) {
			sb.append(duration.toMinutes() % 60);
			sb.append("m ");
		}
		sb.append(duration.toSeconds() % 60).append("s");
		return sb.toString();
	}
	//endregion
}

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

import net.luis.xchallenges.network.XCNetworkHandler;
import net.luis.xchallenges.network.packet.SyncTimerPacket;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 *
 * @author Luis-St
 *
 */

public class Timer {
	
	private static final ThreadLocal<Timer> instance = ThreadLocal.withInitial(Timer::createPaused);
	
	private Mode mode = Mode.UP;
	private long ticks = -1;
	private long currentTicks;
	private boolean running;
	
	public static @NotNull Timer getInstance() {
		return instance.get();
	}
	
	private static @NotNull Timer createPaused() {
		Timer timer = new Timer();
		timer.pause();
		return timer;
	}
	
	public boolean isUp() {
		return this.mode == Mode.UP;
	}
	
	public boolean isDown() {
		return this.mode == Mode.DOWN;
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
	
	public void tick() {
		if (!this.running) {
			return;
		}
		if (this.mode == Mode.UP) {
			this.currentTicks++;
		} else if (this.mode == Mode.DOWN) {
			if (this.currentTicks > 0) {
				this.currentTicks--;
			} else {
				this.currentTicks = 0;
				this.running = false;
			}
		}
	}
	
	public void pause() {
		this.running = false;
	}
	
	public void resume() {
		this.running = true;
	}
	
	public void stop() {
		this.running = false;
		this.reset();
	}
	
	public void reset() {
		if (this.mode == Mode.UP) {
			this.ticks = -1;
			this.currentTicks = 0;
		} else if (this.mode == Mode.DOWN) {
			this.currentTicks = this.ticks;
		}
	}
	
	public void makeUp() {
		this.mode = Mode.UP;
		this.ticks = -1;
		this.currentTicks = 0;
		this.running = false;
	}
	
	public void makeDown(long ticks) {
		if (ticks < 0) {
			throw new IllegalArgumentException("Down timer must have positive ticks");
		}
		this.mode = Mode.DOWN;
		this.ticks = ticks;
		this.currentTicks = ticks;
		this.running = false;
	}
	
	public void sync() {
		XCNetworkHandler.INSTANCE.sendToPlayers(new SyncTimerPacket(System.currentTimeMillis(), this.isDown(), this.isUp(), this.running, this.ticks, this.currentTicks));
	}
	
	public void update(boolean down, boolean up, boolean running, long ticks, long currentTicks) {
		if (down) {
			this.makeDown(ticks);
		} else if (up) {
			this.makeUp();
		}
		this.running = running;
		this.currentTicks = currentTicks;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!this.running) {
			if (this.mode == Mode.UP) {
				sb.append("Paused");
				return sb.toString();
			} else if (this.mode == Mode.DOWN) {
				if (this.currentTicks == this.ticks) {
					sb.append("Stopped");
				} else if (this.currentTicks == 0) {
					sb.append("Finished");
				} else {
					sb.append("Paused");
				}
				return sb.toString();
			}
		}
		Duration duration = Duration.ofMillis(this.currentTicks * 50);
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
	
	private enum Mode {
		
		UP, DOWN;
	}
}

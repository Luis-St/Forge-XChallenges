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

package net.luis.xchallenges.network.packet;

import net.luis.xchallenges.client.XCClientPacketHandler;
import net.luis.xchallenges.network.NetworkPacket;
import net.luis.xchallenges.network.XCNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public class SyncTimerPacket implements NetworkPacket {
	
	private final long serverTime;
	private final boolean down;
	private final boolean up;
	private final boolean running;
	private final long ticks;
	private final long currentTicks;
	
	public SyncTimerPacket(long serverTime, boolean down, boolean up, boolean running, long ticks, long currentTicks) {
		this.serverTime = serverTime;
		this.down = down;
		this.up = up;
		this.running = running;
		this.ticks = ticks;
		this.currentTicks = currentTicks;
	}
	
	public SyncTimerPacket(@NotNull FriendlyByteBuf buffer) {
		this.serverTime = buffer.readLong();
		this.down = buffer.readBoolean();
		this.up = buffer.readBoolean();
		this.running = buffer.readBoolean();
		this.ticks = buffer.readLong();
		this.currentTicks = buffer.readLong();
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuf buffer) {
		buffer.writeLong(this.serverTime);
		buffer.writeBoolean(this.down);
		buffer.writeBoolean(this.up);
		buffer.writeBoolean(this.running);
		buffer.writeLong(this.ticks);
		buffer.writeLong(this.currentTicks);
	}
	
	@Override
	public void handle(CustomPayloadEvent.@NotNull Context context) {
		context.enqueueWork(() -> {
			XCClientPacketHandler.syncTimer(this.serverTime, this.down, this.up, this.running, this.ticks, this.currentTicks);
		});
	}
}

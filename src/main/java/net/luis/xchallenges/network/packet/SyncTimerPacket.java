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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public class SyncTimerPacket implements NetworkPacket {
	
	private final boolean running;
	private final long ticks;
	private final long currentTicks;
	
	public SyncTimerPacket(boolean running, long ticks, long currentTicks) {
		this.running = running;
		this.ticks = ticks;
		this.currentTicks = currentTicks;
	}
	
	public SyncTimerPacket(@NotNull FriendlyByteBuf buffer) {
		this.running = buffer.readBoolean();
		this.ticks = buffer.readLong();
		this.currentTicks = buffer.readLong();
	}
	
	@Override
	public void encode(@NotNull FriendlyByteBuf buffer) {
		buffer.writeBoolean(this.running);
		buffer.writeLong(this.ticks);
		buffer.writeLong(this.currentTicks);
	}
	
	@Override
	public void handle(CustomPayloadEvent.@NotNull Context context) {
		context.enqueueWork(() -> {
			XCClientPacketHandler.syncTimer(this.running, this.ticks, this.currentTicks);
		});
	}
}

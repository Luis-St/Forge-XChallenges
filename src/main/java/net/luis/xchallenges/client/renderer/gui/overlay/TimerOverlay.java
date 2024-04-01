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

package net.luis.xchallenges.client.renderer.gui.overlay;

import net.luis.xchallenges.challenges.Timer;
import net.luis.xchallenges.client.IGui;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public class TimerOverlay implements IGuiOverlay {
	
	@Override
	public void render(@NotNull ForgeGui gui, @NotNull GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
		gui.setupOverlayRenderState(true, false);
		if (gui instanceof IGui iGui && iGui.getOverlayMessage() != null) {
			return;
		}
		String timer = Timer.getInstance().toString();
		int yShift = Math.max(gui.leftHeight, gui.rightHeight) + (68 - 59);
		graphics.pose().pushPose();
		graphics.pose().translate(screenWidth / 2.0, screenHeight - Math.max(yShift, 68), 0.0);
		Font font = gui.getFont();
		int messageWidth = font.width(timer);
		graphics.drawString(font, timer, -messageWidth / 2, -4, 0xFFFFFF);
		graphics.pose().popPose();
	}
}

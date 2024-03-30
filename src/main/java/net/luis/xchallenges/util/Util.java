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

package net.luis.xchallenges.util;

import com.google.common.collect.Lists;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

/**
 *
 * @author Luis-St
 *
 */

public class Util {
	
	public static @NotNull List<Vec3> circlePoints(double radius, @NotNull Vec3 center, int numOfPoints) {
		List<Vec3> points = Lists.newArrayList();
		double slice = 2 * Math.PI / numOfPoints;
		double offset = Math.random() * (2 * Math.PI);
		for (int i = 0; i < numOfPoints; i++) {
			double angle = slice * i + offset;
			double newX = center.x() + radius * Math.cos(angle);
			double newZ = center.z() + radius * Math.sin(angle);
			points.add(new Vec3(newX, 0.00, newZ));
		}
		return points;
	}
	
	public static @NotNull Vec3 offsetPointCircular(double radius, @NotNull Vec3 center) {
		Random random = new Random();
		double angle = Math.toRadians(random.nextFloat() * 360);
		double newX = center.x() + radius * Math.cos(angle);
		double newZ = center.z() + radius * Math.sin(angle);
		return new Vec3(newX, 0.0, newZ);
	}
}

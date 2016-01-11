/*
 * This file is part of NetherDrops.
 *
 * NetherDrops is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NetherDrops is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NetherDrops.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.yahoo.tracebachi.NetherDrops;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * Created by Trace Bachi (tracebachi@yahoo.com, BigBossZee) on 1/2/16.
 */
public class RectangularArea
{
    private Point minPoint;
    private Point maxPoint;

    private class Point
    {
        public final int x;
        public final int y;
        public final int z;

        public Point(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public RectangularArea(ConfigurationSection section)
    {
        List<Integer> firstList = section.getIntegerList("First");
        List<Integer> secondList = section.getIntegerList("Second");

        if(firstList == null || firstList.size() < 3)
        {
            throw new IllegalArgumentException("First point is invalid.");
        }

        if(secondList == null || secondList.size() < 3)
        {
            throw new IllegalArgumentException("Second point is invalid.");
        }

        this.minPoint = new Point(
            Math.min(firstList.get(0), secondList.get(0)),
            Math.min(firstList.get(1), secondList.get(1)),
            Math.min(firstList.get(2), secondList.get(2)));
        this.maxPoint = new Point(
            Math.max(firstList.get(0), secondList.get(0)),
            Math.max(firstList.get(1), secondList.get(1)),
            Math.max(firstList.get(2), secondList.get(2)));
    }

    public boolean isLocationInside(Location location)
    {
        int blockX = location.getBlockX();
        int blockY = location.getBlockY();
        int blockZ = location.getBlockZ();

        return (blockX >= minPoint.x && blockX <= maxPoint.x &&
            blockY >= minPoint.y && blockY <= maxPoint.y &&
            blockZ >= minPoint.z && blockZ <= maxPoint.z);
    }
}

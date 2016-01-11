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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Trace Bachi (tracebachi@yahoo.com, BigBossZee) on 12/21/15.
 */
public class NetherShopItem
{
    private final ItemStack itemStack;
    private final int chanceValue;

    public NetherShopItem(ItemStack itemStack, int chanceValue)
    {
        this.itemStack = itemStack;
        this.chanceValue = chanceValue;
    }

    public NetherShopItem(ConfigurationSection section)
    {
        itemStack = section.getItemStack("item");
        chanceValue = section.getInt("chance-value");
    }

    public ItemStack getItemStack()
    {
        return itemStack;
    }

    public int getChanceValue()
    {
        return chanceValue;
    }
}

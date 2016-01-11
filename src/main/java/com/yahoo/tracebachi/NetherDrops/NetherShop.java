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

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Trace Bachi (tracebachi@yahoo.com, BigBossZee) on 12/21/15.
 */
public class NetherShop
{
    private final ArrayList<NetherShopItem> shopItems = new ArrayList<>();
    private final Random random = new Random();

    private String currencyName;
    private ItemStack currencyItem;
    private int currencyAmount;
    private int totalChanceValue = 0;

    public NetherShop(NetherDrops plugin, ConfigurationSection section)
    {
        DropableItemStack dropableItemStack = plugin.getDropableItemStack(
            section.getString("currency-item-name"));

        if(dropableItemStack == null)
        {
            throw new IllegalArgumentException("There is no currency item by the name: " + currencyItem);
        }

        currencyItem = dropableItemStack.getItemStack();
        currencyName = dropableItemStack.getItemStack().getItemMeta().getDisplayName();
        currencyAmount = Math.max(1, section.getInt("currency-amount", 1));

        ConfigurationSection itemSection = section.getConfigurationSection("possible-items");
        for(String key : itemSection.getKeys(false))
        {
            NetherShopItem item = new NetherShopItem(itemSection.getConfigurationSection(key));
            shopItems.add(item);
            totalChanceValue += item.getChanceValue();
        }
    }

    public String getCurrencyName()
    {
        return currencyName;
    }

    public ItemStack getCurrencyItem()
    {
        return currencyItem;
    }

    public int getCurrencyAmount()
    {
        return currencyAmount;
    }

    public ItemStack getRandomItem()
    {
        int rand = random.nextInt(totalChanceValue);
        int currentValue = 0;

        for(int i = 0; i < shopItems.size() && currentValue < totalChanceValue; ++i)
        {
            NetherShopItem item = shopItems.get(i);
            currentValue += item.getChanceValue();
            if(currentValue > rand)
            {
                return item.getItemStack();
            }
        }
        return null;
    }
}

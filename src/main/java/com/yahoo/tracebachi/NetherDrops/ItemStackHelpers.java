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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Trace Bachi (tracebachi@yahoo.com, BigBossZee) on 12/21/15.
 */
public interface ItemStackHelpers
{
    static boolean removeItemStack(PlayerInventory inventory, ItemStack itemStack, int amount)
    {
        if(amount < 0 || amount > 64)
        {
            throw new IllegalArgumentException("Amount cannot be between 0 and 64.");
        }

        int originalAmount = amount;
        for(int i = 0; i < inventory.getSize(); ++i)
        {
            ItemStack indexItem = inventory.getItem(i);

            if(matchesItemStack(itemStack, indexItem))
            {
                amount -= indexItem.getAmount();
                if(amount < 0)
                {
                    indexItem.setAmount(-amount);
                    inventory.setItem(i, indexItem);
                    return true;
                }
                else
                {
                    inventory.setItem(i, new ItemStack(Material.AIR));
                }
            }
        }

        if(amount > 0 && (originalAmount - amount) != 0)
        {
            ItemStack returned = new ItemStack(itemStack);
            returned.setAmount(originalAmount - amount);
            inventory.addItem(returned);
        }
        return amount <= 0;
    }

    static  boolean matchesItemStack(ItemStack original, ItemStack other)
    {
        if(original == null && other == null)
        {
            return true;
        }
        else if(original == null ^ other == null)
        {
            return false;
        }

        if(original.getType() != other.getType())
        {
            return false;
        }

        if(!original.hasItemMeta() && !other.hasItemMeta())
        {
            return true;
        }

        if(original.hasItemMeta() ^ original.hasItemMeta())
        {
            return false;
        }

        ItemMeta originalMeta = original.getItemMeta();
        ItemMeta otherMeta = other.getItemMeta();

        if(originalMeta.hasDisplayName() ^ otherMeta.hasDisplayName())
        {
            return false;
        }

        if(originalMeta.hasDisplayName() && otherMeta.hasDisplayName())
        {
            if(!originalMeta.getDisplayName().equals(otherMeta.getDisplayName()))
            {
                return false;
            }
        }

        return originalMeta.hasLore() == otherMeta.hasLore();
    }
}

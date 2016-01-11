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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Created by Trace Bachi (tracebachi@yahoo.com, BigBossZee) on 12/21/15.
 */
public class DropGiveCommand implements CommandExecutor
{
    private NetherDrops plugin;

    public DropGiveCommand(NetherDrops plugin)
    {
        this.plugin = plugin;
    }

    public void shutdown()
    {
        plugin = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(Prefixes.FAILURE + "This command can only be used as a player.");
            return true;
        }

        if(sender.hasPermission("NetherDrops.Give"))
        {
            if(args.length < 2)
            {
                sender.sendMessage(Prefixes.INFO + "/dropgive <amount> <item name>");
                return true;
            }

            Integer amount = parseInt(args[0]);
            if(amount == null || amount < 1)
            {
                sender.sendMessage(Prefixes.FAILURE + "Invalid amount.");
                return true;
            }

            String itemName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            DropableItemStack dropableItemStack = plugin.getDropableItemStack(itemName);

            if(dropableItemStack == null)
            {
                sender.sendMessage(Prefixes.FAILURE + "There is no item named: " + Prefixes.input(itemName));
            }
            else
            {
                Player player = (Player) sender;
                ItemStack toGive = new ItemStack(dropableItemStack.getItemStack());

                toGive.setAmount(amount);
                player.getInventory().addItem(toGive);
                sender.sendMessage(Prefixes.SUCCESS + "Given " + Prefixes.input(amount) +
                    " of " + Prefixes.input(itemName));
            }
        }
        return true;
    }

    private Integer parseInt(String src)
    {
        try
        {
            return Integer.parseInt(src);
        }
        catch(NumberFormatException ex)
        {
            return null;
        }
    }
}

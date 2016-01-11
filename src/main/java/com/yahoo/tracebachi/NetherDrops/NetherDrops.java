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
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Trace Bachi (BigBossZee) on 7/6/2015.
 */
public class NetherDrops extends JavaPlugin implements Listener
{
    private HashMap<String, DropableItemStack> dropMap;
    private HashMap<String, RectangularArea> noDropAreaMap;
    private HashMap<String, NetherShop> shopMap;
    private Random random;
    private DropGiveCommand dropGiveCommand;

    @Override
    public void onLoad()
    {
        saveDefaultConfig();
        saveResource("shops.yml", false);
    }

    @Override
    public void onEnable()
    {
        reloadConfig();
        reloadConfigurations();

        getServer().getPluginManager().registerEvents(this, this);

        getCommand("dropsreload").setExecutor(this);
        dropGiveCommand = new DropGiveCommand(this);
        getCommand("dropgive").setExecutor(dropGiveCommand);
    }

    @Override
    public void onDisable()
    {
        if(dropGiveCommand != null)
        {
            getCommand("dropgive").setExecutor(null);
            dropGiveCommand.shutdown();
            dropGiveCommand = null;
        }

        getCommand("dropsreload").setExecutor(null);

        shopMap.clear();
        shopMap = null;
        dropMap.clear();
        dropMap = null;
        random = null;
    }

    public DropableItemStack getDropableItemStack(String name)
    {
        return dropMap.get(name);
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event)
    {
        if(!(event.getEntity() instanceof Player))
        {
            Location location = event.getEntity().getLocation();
            for(RectangularArea area : noDropAreaMap.values())
            {
                if(area.isLocationInside(location))
                {
                    return;
                }
            }

            for(DropableItemStack item : dropMap.values())
            {
                int shiftedProbability = (int) (item.getProbability() * 10000);
                int roll = random.nextInt(10000);

                if(roll < shiftedProbability)
                {
                    Location loc = event.getEntity().getLocation();
                    int amount = random.nextInt(item.getMaxAmount()) + 1;
                    ItemStack toDrop = new ItemStack(item.getItemStack());

                    toDrop.setAmount(amount);
                    loc.getWorld().dropItemNaturally(loc, toDrop);
                }
            }
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
        {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if(block.getState() instanceof Sign)
        {
            Sign sign = (Sign) block.getState();
            if(sign.getLine(0).equals("[NetherShop]"))
            {
                String shopName = sign.getLine(1);
                NetherShop shop = shopMap.get(shopName);

                if(shop != null)
                {
                    boolean hadEnough = ItemStackHelpers.removeItemStack(player.getInventory(),
                        shop.getCurrencyItem(), shop.getCurrencyAmount());

                    if(hadEnough)
                    {
                        ItemStack newItem = shop.getRandomItem();
                        player.getInventory().addItem(newItem);

                        player.sendMessage(Prefixes.SUCCESS + "Traded " +
                            Prefixes.input(shop.getCurrencyAmount() + " " + shop.getCurrencyName()) +
                            " for items at " + Prefixes.input(shopName));
                    }
                    else
                    {
                        player.sendMessage(Prefixes.FAILURE + "You do not have enough of the currency.");
                    }
                }
                else
                {
                    player.sendMessage(Prefixes.FAILURE + "That shop does not exist.");
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(command.getName().equalsIgnoreCase("dropsreload") && sender.isOp())
        {
            dropMap.clear();
            dropMap = null;
            noDropAreaMap.clear();
            noDropAreaMap = null;
            shopMap.clear();
            shopMap = null;
            reloadConfigurations();
        }
        return true;
    }

    private void reloadConfigurations()
    {
        random = new Random();
        dropMap = new HashMap<>();
        noDropAreaMap = new HashMap<>();
        shopMap = new HashMap<>();

        ConfigurationSection itemSection = getConfig().getConfigurationSection("Items");
        ConfigurationSection noDropAreasSection = getConfig().getConfigurationSection("NoDropAreas");

        for(String key : itemSection.getKeys(false))
        {
            double probability = itemSection.getDouble(key + ".probability", 0.5);
            int maxAmount = itemSection.getInt(key + ".max-amount", 1);
            ItemStack itemStack = itemSection.getItemStack(key + ".item-stack");

            dropMap.put(key, new DropableItemStack(itemStack, probability, maxAmount));
            getLogger().info("Loaded drop item: " + key);
        }

        for(String areaName : noDropAreasSection.getKeys(false))
        {
            try
            {
                RectangularArea area = new RectangularArea(
                    noDropAreasSection.getConfigurationSection(areaName));
                noDropAreaMap.put(areaName, area);
            }
            catch(IllegalArgumentException ex)
            {
                getLogger().info("Invalid NoDropArea named " + areaName);
            }
        }

        try
        {
            YamlConfiguration shopConfig = new YamlConfiguration();
            shopConfig.load(new File(getDataFolder(), "shops.yml"));
            for(String shopName : shopConfig.getKeys(false))
            {
                NetherShop shop = new NetherShop(this, shopConfig.getConfigurationSection(shopName));
                shopMap.put(shopName, shop);
                getLogger().info("Loaded shop: " + shopName);
            }
        }
        catch(IOException | InvalidConfigurationException ex)
        {
            ex.printStackTrace();
            getLogger().severe("Failed to load shops.");
        }
    }
}

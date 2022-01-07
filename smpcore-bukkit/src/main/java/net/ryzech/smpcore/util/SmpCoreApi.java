package net.ryzech.smpcore.util;

import net.ryzech.smpcore.SmpCorePlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
import java.util.Objects;

import static org.bukkit.GameMode.CREATIVE;

public class SmpCoreApi {
    private FileUtils fileUtils;
    public static String colorizeText(String string) {
        return string == null ? "" : ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String formatName(Player player) {
        try {
            return colorizeText(SmpCorePlugin.getInstance().chat.getPlayerPrefix(player)) + player.getName();
        } catch (Exception e) {
            SmpCorePlugin.getInstance().getLogger().severe(e.getMessage());
            e.printStackTrace();
        }
        return "" + ChatColor.RESET + player.getName();
    }


    public static boolean isBlockChange(Location from, Location to) {
        return from.getBlockX() != to.getBlockX() ||
                from.getBlockY() != to.getBlockY() ||
                from.getBlockZ() != to.getBlockZ();
    }

    // Removes an item stack of the given description from the players inventory
    public static boolean removeItem(Player player, ItemStack is) {
        return removeItem(player, is.getType(), is.getAmount());
    }

    // Removes a certain number of an item stack of the given description from the players inventory and returns true
    //      if the item stack was direction their inventory
    public static boolean removeItem(Player player, Material mat, int amount) {
        if (player.getGameMode().equals(CREATIVE)) {
            return true;
        }
        Inventory inv = player.getInventory();

        if (!hasItem(player, mat, amount)) {
            return false;
        }

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null && Objects.requireNonNull(inv.getItem(i)).getType() == mat) {
                if (Objects.requireNonNull(inv.getItem(i)).getAmount() > amount) {
                    int res = Objects.requireNonNull(inv.getItem(i)).getAmount() - amount;
                    ItemStack rest = inv.getItem(i);
                    assert rest != null;
                    rest.setAmount(res);
                    inv.setItem(i, rest);
                    return true;
                } else {
                    amount -= Objects.requireNonNull(inv.getItem(i)).getAmount();
                    inv.setItem(i, null);
                }
            }
        }
        return true;
    }

    public static void giveItem(Player player, Collection<ItemStack> drops) {
        for (ItemStack item : drops) {
            giveItem(player, item);
        }
    }

    public static void giveItem(Player player, ItemStack item) {
        player.getInventory().addItem(item);
        player.updateInventory();
        if(player.getInventory().firstEmpty() == -1) {
            player.sendMessage("Item Sent to Vault: " + Objects.requireNonNull(item.getItemMeta()).displayName());
            Objects.requireNonNull(player.getLocation().getWorld()).dropItem(getCenter(player.getLocation()).add(0, 1, 0), item);
        }
    }

    public static void giveItemMaterial(Player player, Material material, Integer amount) {
        player.getInventory().addItem(new ItemStack(material, amount));
        player.updateInventory();
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("Item Sent to Vault: " + Objects.requireNonNull(new ItemStack(material).getItemMeta()).displayName());
            Objects.requireNonNull(player.getLocation().getWorld()).dropItem(getCenter(player.getLocation()).add(0, 1, 0), new ItemStack(material, amount));
        }
    }

    // Removes a certain number of an item stack of the given description from the players inventory and returns true
    //      if the item stack was direction their inventory
    public static boolean hasItem(Player player, Material mat, int amount) {
        if (player.getGameMode().equals(CREATIVE)) {
            return true;
        }
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null && Objects.requireNonNull(inv.getItem(i)).getType() == mat) {
                if (Objects.requireNonNull(inv.getItem(i)).getAmount() >= amount) {
                    amount = 0;
                } else {
                    amount -= Objects.requireNonNull(inv.getItem(i)).getAmount();
                }
            }
        }
        return amount == 0;
    }

    // Returns the exact center of a block of a given location
    public static Location getCenter(Location loc) {
        return getCenter(loc, false);
    }

    // Returns the exact center of a block of a given location
    public static Location getCenter(Location loc, boolean centerVertical) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        if (x >= 0) {
            x += .5;
        } else {
            x += .5;
        }
        if (centerVertical) {
            y = (int) y + .5;
        }
        if (z >= 0) {
            z += .5;
        } else {
            z += .5;
        }
        Location lo = loc.clone();
        lo.setX(x);
        lo.setY(y);
        lo.setZ(z);
        return lo;
    }

    public static int getAmount(Player player, Material mat) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] items = inv.getContents();
        int has = 0;
        for (ItemStack item : items)
        {
            if ((item != null) && (item.getType() == mat) && (item.getAmount() > 0))
            {
                has += item.getAmount();
            }
        }
        return has;
    }
}
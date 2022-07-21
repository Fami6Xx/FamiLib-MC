package me.familib.apis.modules.MenuManager.types;

import me.familib.apis.modules.MenuManager.utils.PlayerMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class Menu implements InventoryHolder {
    protected PlayerMenu playerMenu;
    protected Inventory inventory;
    protected ItemStack FILLER_GLASS = makeFillerGlass();

    public Menu(PlayerMenu menu) {
        this.playerMenu = menu;
    }
    public abstract String getMenuName();
    public abstract int getSlots();
    public abstract void handleMenu(InventoryClickEvent e);
    public abstract void setMenuItems();

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems();

        playerMenu.getPlayer().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setFillerGlass(){
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null){
                inventory.setItem(i, FILLER_GLASS);
            }
        }
    }

    public ItemStack makeFillerGlass(){
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);

        return glass;
    }

    public ItemStack makeItem(Material material, String displayName, String... lore) {
        ItemStack item = new ItemStack(material);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);

        return item;
    }

}

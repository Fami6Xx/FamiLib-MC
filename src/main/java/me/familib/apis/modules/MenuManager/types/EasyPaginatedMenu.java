package me.familib.apis.modules.MenuManager.types;

import me.familib.apis.modules.MenuManager.utils.PlayerMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
public abstract class EasyPaginatedMenu extends PaginatedMenu{
    public EasyPaginatedMenu(PlayerMenu menu){
        super(menu);
    }

    public abstract ItemStack getItemFromIndex(int index);
    public abstract int getSize();
    public abstract String getFirstPageErrorMessage();
    public abstract String getLastPageErrorMessage();

    public abstract void handlePaginatedMenu(InventoryClickEvent e);

    @Override
    public void handleMenu(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
        if(e.getCurrentItem().getType().equals(Material.BARRIER)
                && name.equalsIgnoreCase("Close")){
            p.closeInventory();
        }else if(e.getCurrentItem().getType().equals(Material.STONE_BUTTON)){
            if(name.equalsIgnoreCase("Left")){
                if(page == 0){
                    p.sendMessage(getFirstPageErrorMessage());
                }else{
                    page--;
                    super.open();
                }
            }else if(name.equalsIgnoreCase("Right")){
                if(index + 1 < getSize()){
                    page++;
                    super.open();
                }else{
                    p.sendMessage(getLastPageErrorMessage());
                }
            }
        }

        handlePaginatedMenu(e);
    }

    @Override
    public void setMenuItems(){
        addMenuBorder();

        for(int i = 0; i < getMaxItemsPerPage(); i++){
            index = getMaxItemsPerPage() * page + i;
            if(index >= getSize()) break;

            ItemStack item = getItemFromIndex(index);

            if(item == null) break;

            super.inventory.setItem(index, item);
        }
    }

}

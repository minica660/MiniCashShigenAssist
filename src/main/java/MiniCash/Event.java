package MiniCash;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;

public class Event implements Listener {

    private final MiniCashShigenAssist plugin;

    public Event(MiniCashShigenAssist plugin) {
        this.plugin = plugin;
    }

    private File folder;
    private File playerFile;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        Player player  = event.getPlayer();

        folder = new File(plugin.getDataFolder(), "players");
        if (!folder.exists()){
            folder.mkdirs();
        }

        playerFile = new File(plugin.getDataFolder(), "players/" + player.getUniqueId() + ".yml");

        if (!playerFile.getParentFile().exists()) {
            playerFile.getParentFile().mkdirs();
        }

        if (!playerFile.exists()){
            try {
                playerFile.createNewFile();

                FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

                config.set("name",  player.getName());
                config.set("uuid",  player.getUniqueId().toString());
                config.set("night_vision","false");
                config.set("Durability_value","false");
                config.set("elytra",false);
                config.save(playerFile);
                plugin.getLogger().info(player.getName() + " のデータファイルを作成しました！");

            } catch (IOException e) {
                player.sendMessage(e.getMessage());
                plugin.getLogger().severe(e.getMessage());
            }
        }


    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if ((event.getInventory().getHolder() instanceof GUI)) {

            ItemStack clickedItem = event.getCurrentItem();

            event.setCancelled(true);

            ItemStack nightItem = new ItemStack(Material.EMERALD_BLOCK);
            ItemStack durabilityItem = new ItemStack(Material.EMERALD_BLOCK);
            ItemStack elytraItem = new ItemStack(Material.EMERALD_BLOCK);

            ItemMeta nightItemMeta = nightItem.getItemMeta();
            ItemMeta durabilityItemMeta = durabilityItem.getItemMeta();
            ItemMeta elytraItemMeta = elytraItem.getItemMeta();

            if(clickedItem.getType() == Material.EMERALD_BLOCK && event.getSlot() == 1){

                nightItem = new ItemStack(Material.REDSTONE_BLOCK);
                nightItemMeta.setDisplayName("§4暗視OFF状態");

                event.getInventory().setItem(event.getSlot(),nightItem );

                player.updateInventory();
            }else if(clickedItem.getType() == Material.REDSTONE_BLOCK && event.getSlot() == 1){
                nightItem = new ItemStack(Material.EMERALD_BLOCK);
                nightItemMeta.setDisplayName("§a暗視ON状態");

                event.getInventory().setItem(event.getSlot(),nightItem );

                player.updateInventory();
            }

            if (clickedItem.getType() == Material.EMERALD_BLOCK && event.getSlot() == 3) {
                durabilityItem = new ItemStack(Material.REDSTONE_BLOCK);
                durabilityItemMeta.setDisplayName("§4耐久値警告OFF状態");

                event.getInventory().setItem(event.getSlot(),durabilityItem );

                player.updateInventory();
            }else if(clickedItem.getType() == Material.REDSTONE_BLOCK && event.getSlot() == 3) {
                durabilityItem = new ItemStack(Material.EMERALD_BLOCK);
                durabilityItemMeta.setDisplayName("§a耐久値警告ＯＮ状態");

                event.getInventory().setItem(event.getSlot(),durabilityItem );

                player.updateInventory();
            }

            if (clickedItem.getType() == Material.EMERALD_BLOCK && event.getSlot() == 5) {
                elytraItem = new ItemStack(Material.REDSTONE_BLOCK);
                elytraItemMeta.setDisplayName("§4エリトラ補助ＯＦＦ状態");

                event.getInventory().setItem(event.getSlot(),elytraItem );

                player.updateInventory();
            }else if(clickedItem.getType() == Material.REDSTONE_BLOCK && event.getSlot() == 5) {
                elytraItem = new ItemStack(Material.EMERALD_BLOCK);
                elytraItemMeta.setDisplayName("§aエリトラ補助ＯＮ状態");

                event.getInventory().setItem(event.getSlot(),elytraItem );

                player.updateInventory();
            }


        }
    }

    public File getPlayerFile() {
        return playerFile;
    }

}

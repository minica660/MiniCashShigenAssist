package MiniCash;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Event implements Listener {

    private final MiniCashShigenAssist plugin;
    private final MPublic mPublic;

    public Event(MiniCashShigenAssist plugin,MPublic mPublic) {
        this.plugin = plugin;
        this.mPublic = mPublic;
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
                config.set("night_vision",false);
                config.set("Durability_value",false);
                config.set("elytra",false);
                config.save(playerFile);

                player.sendMessage("[§aMiniCashShigenAssist§r] §6データファイルを作成しました");
                plugin.getLogger().info(player.getName() + " のデータファイルを作成しました！");

            } catch (IOException e) {
                player.sendMessage(e.getMessage());
                plugin.getLogger().severe(e.getMessage());
            }
        }else {
            player.sendMessage("[§aMiniCashShigenAssist§r] /minerassistで補助機能のON,OFFを切り替えられます");

            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

            if(playerConfig.getBoolean("night_vision")){
                mPublic.setCheckNightVision(player.getUniqueId());
            }

            if (playerConfig.getBoolean("Durability_value")) {
                mPublic.setCheckDurability(player.getUniqueId());
            }


        }


    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        // もしプレイヤーが暗視効果を受け取るプレイヤーを入れるListに入っていればListから削除
        UUID id = event.getPlayer().getUniqueId();
        if (mPublic.getCheckNightVision().contains(id)){
            mPublic.removeCheckNightVision(id);
        }

        if (mPublic.getCheckDurability().contains(id)){
            mPublic.removeCheckDurability(id);
        }

        if (mPublic.getCheckNightVision().contains(id)){
            mPublic.removeElytra(id);
        }
    }


    @EventHandler
    public void inventoryClick(InventoryClickEvent event){
        if (event.getCurrentItem() == null){
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if ((event.getInventory().getHolder() instanceof GUI)) {
            File playerFile = new File((plugin.getDataFolder()), "players/" + player.getUniqueId() + ".yml");

            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);


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


                playerConfig.set("night_vision",false);

                try {
                    playerConfig.save(playerFile);
                } catch (IOException e) {
                    player.sendMessage(e.getMessage());
                    plugin.getLogger().severe(e.getMessage());
                }

                if (mPublic.getCheckNightVision().contains(player.getUniqueId())) {
                    mPublic.removeCheckNightVision(player.getUniqueId());
                }

                nightItem.setItemMeta(nightItemMeta);


                event.getInventory().setItem(event.getSlot(),nightItem );

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                player.sendMessage("[§aMiniCashPortalTeleport§r] §6暗視OFF状態に変更しました");
                plugin.getLogger().info(player.getName() + " 暗視OFF状態に変更しました");

                player.updateInventory();
            }else if(clickedItem.getType() == Material.REDSTONE_BLOCK && event.getSlot() == 1){
                nightItem = new ItemStack(Material.EMERALD_BLOCK);
                nightItemMeta.setDisplayName("§a暗視ON状態");

                playerConfig.set("night_vision",true);
                try {
                    playerConfig.save(playerFile);
                } catch (IOException e) {
                    player.sendMessage(e.getMessage());
                    plugin.getLogger().severe(e.getMessage());
                }

                if (!mPublic.getCheckNightVision().contains(player.getUniqueId())) {
                    mPublic.setCheckNightVision(player.getUniqueId());
                }

                nightItem.setItemMeta(nightItemMeta);


                event.getInventory().setItem(event.getSlot(),nightItem );

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                player.sendMessage("[§aMiniCashPortalTeleport§r] §6暗視ON状態に変更しました");
                plugin.getLogger().info(player.getName() + " 暗視ON状態に変更しました");

                player.updateInventory();
            }

            if (clickedItem.getType() == Material.EMERALD_BLOCK && event.getSlot() == 3) {
                durabilityItem = new ItemStack(Material.REDSTONE_BLOCK);
                durabilityItemMeta.setDisplayName("§4耐久値警告OFF状態");

                playerConfig.set("Durability_value",false);
                try {
                    playerConfig.save(playerFile);
                } catch (IOException e) {
                    player.sendMessage(e.getMessage());
                    plugin.getLogger().severe(e.getMessage());
                }


                if (mPublic.getCheckDurability().contains(player.getUniqueId())) {
                    mPublic.removeCheckDurability(player.getUniqueId());
                }

                durabilityItem.setItemMeta(durabilityItemMeta);


                event.getInventory().setItem(event.getSlot(),durabilityItem );

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                player.sendMessage("[§aMiniCashPortalTeleport§r] §6耐久値警告OFF状態に変更しました");
                plugin.getLogger().info(player.getName() + " 耐久値警告OFF状態に変更しました");

                player.updateInventory();
            }else if(clickedItem.getType() == Material.REDSTONE_BLOCK && event.getSlot() == 3) {
                durabilityItem = new ItemStack(Material.EMERALD_BLOCK);
                durabilityItemMeta.setDisplayName("§a耐久値警告ＯＮ状態");

                playerConfig.set("Durability_value",true);
                try {
                    playerConfig.save(playerFile);
                } catch (IOException e) {
                    player.sendMessage(e.getMessage());
                    plugin.getLogger().severe(e.getMessage());
                }

                if (!mPublic.getCheckDurability().contains(player.getUniqueId())) {
                    mPublic.setCheckDurability(player.getUniqueId());
                }

                durabilityItem.setItemMeta(durabilityItemMeta);


                event.getInventory().setItem(event.getSlot(),durabilityItem );

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                player.sendMessage("[§aMiniCashPortalTeleport§r] §6耐久値警告ON状態に変更しました");
                plugin.getLogger().info(player.getName() + " 耐久値警告ON状態に変更しました");


                player.updateInventory();
            }

            if (clickedItem.getType() == Material.EMERALD_BLOCK && event.getSlot() == 5) {
                elytraItem = new ItemStack(Material.REDSTONE_BLOCK);
                elytraItemMeta.setDisplayName("§4エリトラ補助OFF状態");

                playerConfig.set("elytra",false);
                try {
                    playerConfig.save(playerFile);
                } catch (IOException e) {
                    player.sendMessage(e.getMessage());
                    plugin.getLogger().severe(e.getMessage());
                }

                elytraItem.setItemMeta(elytraItemMeta);

                event.getInventory().setItem(event.getSlot(),elytraItem );

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                player.sendMessage("[§aMiniCashPortalTeleport§r] §6エリトラ補助OFF状態に変更しました");
                plugin.getLogger().info(player.getName() + " エリトラ補助OFF状態に変更しました");


                player.updateInventory();
            }else if(clickedItem.getType() == Material.REDSTONE_BLOCK && event.getSlot() == 5) {
                elytraItem = new ItemStack(Material.EMERALD_BLOCK);
                elytraItemMeta.setDisplayName("§aエリトラ補助ＯＮ状態");

                playerConfig.set("elytra",true);
                try {
                    playerConfig.save(playerFile);
                } catch (IOException e) {
                    player.sendMessage(e.getMessage());
                    plugin.getLogger().severe(e.getMessage());
                }

                elytraItem.setItemMeta(elytraItemMeta);

                event.getInventory().setItem(event.getSlot(),elytraItem );

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                player.sendMessage("[§aMiniCashPortalTeleport§r] §6エリトラ補助ON状態に変更しました");
                plugin.getLogger().info(player.getName() + " エリトラ補助ON状態に変更しました");


                player.updateInventory();
            }


        }


    }
    @EventHandler
    public void playerSneak(PlayerToggleSneakEvent event) {

        Player player = event.getPlayer();


        if (!player.getInventory().getChestplate().getType().equals(Material.ELYTRA)) {
            return;
        }

        FileConfiguration config = plugin.getConfig();

        File playerFile = new File((plugin.getDataFolder()), "players/" + player.getUniqueId() + ".yml");

        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

        if( config.getBoolean("elytra") && playerConfig.getBoolean("elytra")){

            if (player.hasMetadata("MiniCash_shigen_assist_cooldownd")){
                Component message = Component.text("クールダウン中です").color(NamedTextColor.RED);
                player.sendActionBar(message);
                return;
            }

            int time = 3;

            mPublic.setElytra(player.getUniqueId(),time);
            plugin.getLogger().info(player.getName() + "のsetElytraメソッドを time" + time + "で呼び出しました");



            new BukkitRunnable() {
            Component message = Component.text("???").color(NamedTextColor.GREEN);
            int charget = time;
                @Override
                public void run() {

                    if (!player.isSneaking()) {

                        mPublic.removeElytra(player.getUniqueId());
                        plugin.getLogger().info(player.getName() + "のremoveElytraメソッドを呼び出しました");

                        message = Component.text("チャージを中断しました").color(NamedTextColor.RED);


                        player.sendActionBar(message);

                        player.setMetadata("MiniCash_shigen_assist_cooldownd",new FixedMetadataValue(plugin, true));

                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            player.removeMetadata("MiniCash_shigen_assist_cooldownd",plugin);
                        },10);

                        this.cancel();
                        return;
                    }

                    if(charget > 0){
                        message = Component.text("チャージ完了まで").color(NamedTextColor.GREEN).append(Component.text(charget+ "...").color(NamedTextColor.YELLOW));

                        player.sendActionBar(message);
                    }else if(charget == 0){

                        cancel();

                        message = Component.text("ブースト！").color(NamedTextColor.BLUE);

                        player.sendActionBar(message);
                        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
                        mPublic.removeElytra(player.getUniqueId());
                        plugin.getLogger().info(player.getName() + "のブーストが完了したため、removeElytraメソッドを呼び出しました");


                        player.setMetadata("MiniCash_shigen_assist_cooldownd",new FixedMetadataValue(plugin, true));

                        if (player.isOnGround()){
                            Vector vector = new Vector(0,2.1,0);

                            player.setVelocity(vector);

                        }else {
                            Vector vector = player.getLocation().getDirection().multiply(1.7);

                            player.setVelocity(vector);
                        }


                        Bukkit.getScheduler().runTaskLater(plugin, () -> {

                            player.removeMetadata("MiniCash_shigen_assist_cooldownd",plugin);
                            message = Component.text("クールダウン終了").color(NamedTextColor.DARK_AQUA);
                            player.sendActionBar(message);
                        },80);

                        return;

                    }

                    charget--;

                }
            }.runTaskTimer(plugin, 0, 20);


        }



    }


    @EventHandler
    public void PlayerItemDamage(PlayerItemDamageEvent event) {

        if (!mPublic.getCheckDurability().contains(event.getPlayer().getUniqueId())) {
            return;
        }

        if (event.getItem().getItemMeta() instanceof Damageable damageable){

            int damage =  damageable.getDamage();
            int max = event.getItem().getType().getMaxDurability();
            if (max <= 1000){
                return;
            }
            int result = max - damage;
            if(result <= 400){

                Component message = Component.text("[警告]").color(NamedTextColor.DARK_RED).append(Component.text("§l残り耐久値： ").append(Component.text(result).color(NamedTextColor.RED).append(Component.text("/").color(NamedTextColor.GRAY).append(Component.text(max).color(NamedTextColor.WHITE)))));
                event.getPlayer().sendActionBar(message);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
            }

        }
    }


    public File getPlayerFile() {
        return playerFile;
    }

}



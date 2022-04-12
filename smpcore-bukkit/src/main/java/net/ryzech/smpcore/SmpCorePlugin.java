package net.ryzech.smpcore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.ryzech.smpcore.commands.admin.toweradmin;
import net.ryzech.smpcore.commands.admin.yell;
import net.ryzech.smpcore.commands.everyone.*;
import net.ryzech.smpcore.compatibilities.CompatibilitiesManager;
import net.ryzech.smpcore.events.DamageEvent;
import net.ryzech.smpcore.managers.HologramManager;
import net.ryzech.smpcore.managers.barManager;
import net.ryzech.smpcore.util.FileUtils;
import net.ryzech.smpcore.util.MySQL;
import net.ryzech.smpcore.util.SmpCoreApi;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SmpCorePlugin extends JavaPlugin implements Listener {

    private static SmpCorePlugin instance;
    public static Logger log;

    public static final String LORE_NO_CRAFT = ChatColor.DARK_RED + "Not Craftable";
    private static SmpCorePlugin smpcore;
    public Chat chat;

    public SmpCorePlugin() throws Exception {
        smpcore = this;
    }

    public static SmpCorePlugin get() {
        return smpcore;
    }
    private ProtocolManager packetManager;
    private HologramManager hologramManager;
    private barManager barManager;
    private FileUtils fileUtils;
    private stuck stuckCommand;
    private Boolean joinmessageenabled;
    private Boolean quitmessageenabled;
    private Boolean DamageIndicatorsEnabled;

    public void onEnable() {
        instance = this;
        PluginDescriptionFile pdf = this.getDescription(); // gets the plugin.yml file
        log = this.getLogger();
        new report(this);
        new MySQL(this);
        try {
            MySQL.connect();
            if(MySQL.update("CREATE TABLE IF NOT EXISTS `smpcore_reports` (  `uuid` CHAR(36) NOT NULL,  `report_id` INT NOT NULL AUTO_INCREMENT primary key, `reporter` TINYTEXT NOT NULL, `reported` TINYTEXT NOT NULL, `report_message` TINYTEXT,  `timestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP );"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.getenv("TOKEN");

        // commandExecutor
        yell yellExecutor = new yell(this);
        stuck stuckExecutor = new stuck(this);
        toweradmin towerExecutor = new toweradmin(this);
        if(getServer().getPluginManager().isPluginEnabled("Essentials")) {
            economy economyExecutor = new economy(this);
        }
        reportinfo reportinfoExecutor = new reportinfo(this);


        // ProtocolLib
        packetManager = ProtocolLibrary.getProtocolManager();

        // Vault registration
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        // Board Management
        hologramManager = new HologramManager(this);
        barManager = new barManager(this);
        fileUtils = new FileUtils(this);
        stuckCommand = new stuck(this);

        // Bukkit registration
        getServer().getPluginManager().registerEvents(this, this);
        CompatibilitiesManager.enableNativeCompatibilities();

        // Try to load config
        fileUtils.loadMain();
        fileUtils.loadLang();

        // Init enable message variables
        String PREFIX = fileUtils.getLang().getString("Lang.Prefix");
        String ENABLETOPBAR = fileUtils.getLang().getString("Lang.EnableTopBar");

        // Init join/quit booleans
        joinmessageenabled = fileUtils.getMain().getBoolean("Join.JoinMessages");
        quitmessageenabled = fileUtils.getMain().getBoolean("Join.QuitMessages");
        DamageIndicatorsEnabled = fileUtils.getMain().getBoolean("DamageIndicators.Enabled");

        // HubManager
        stuckCommand.enable((YamlConfiguration) fileUtils.getMain());

        // Plugin enabled log message
        assert ENABLETOPBAR != null;
        log.info(ChatColor.DARK_AQUA + "Enabling SmpCore...");
        log.info(ChatColor.GREEN + "Enabled SmpCore!");
        log.info(ChatColor.DARK_AQUA + "Plugin Author" + ChatColor.GRAY + " > " + ChatColor.DARK_AQUA + pdf.getAuthors());
    }

    public void onDisable() {
        // WRITE CONFIG
        fileUtils = new FileUtils(this);
        fileUtils.reloadMain();
        saveDefaultConfig();
    }

    // Get plugin instance
    public static SmpCorePlugin getInstance() { return instance; }

    // Get protocollib library
    public ProtocolManager getPacketManager() {
        return packetManager;
    }

    // Get bossbar manager
    public barManager getBarManager() {
        return barManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    // Welcome message!
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();


        if (!player.hasPlayedBefore()) {
            event.setJoinMessage(ChatColor.GOLD + "Welcome "
                    + SmpCoreApi.formatName(player)
                    + ChatColor.GOLD + " to the server!");
            try {
                event.getPlayer().sendMessage(ChatColor.GOLD + "Welcome to the server! Please check out the rules for our community! :D");
            } catch (Exception e){
                log.severe("Exception with new user joining!" + e.getMessage());
                e.printStackTrace();
            }
        }

        barManager.addPlayer(player);
    }

    // Disable crafting for LORE_NO_CRAFT meta
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        for (ItemStack i : event.getInventory().getMatrix()) {
            if (i != null && i.getItemMeta().hasLore() && i.getItemMeta().lore().contains(LORE_NO_CRAFT)) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
            }
        }
    }

    // Disable or Enable join messages
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerConnect(PlayerJoinEvent event) {
        if(!joinmessageenabled) {
            event.setJoinMessage("");
        }
        else {
            event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', "&e" + event.getPlayer().getDisplayName() + " &ejoined the game"));
        }
    }

    // Enable or Disable quit messages
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLeave(PlayerQuitEvent leaveEvent) {
        if(!quitmessageenabled) {
            leaveEvent.setQuitMessage("");
        }
        else {
            leaveEvent.setQuitMessage(ChatColor.translateAlternateColorCodes('&', "&e" + leaveEvent.getPlayer().getDisplayName() + " &eleft the game"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Projectile arrow = (Projectile) event.getDamager();
            Entity target = event.getEntity();

            // Headshot detection logic
            double arrowX = arrow.getLocation().getX();
            double arrowY = arrow.getLocation().getY();
            double damagedX = target.getLocation().getX();
            double damagedY = target.getLocation().getY();
            double boundingX = target.getBoundingBox().getWidthX();
            double boundingY = target.getBoundingBox().getHeight();
            double boundingZ = target.getBoundingBox().getWidthZ();
            double headshotCheckY = (damagedY + boundingY) - arrowY;
            double headshotCheckX = (damagedX + boundingX) - arrowX;
            double headshotCheckZ = (damagedX + boundingZ) - arrowX;
            boolean critical = false;
            if (target instanceof Ravager) {

            } else {
                if (!(target instanceof Player) && headshotCheckY <= 0.46) {
                    critical = true;
                    event.setDamage(event.getDamage());
                }
            }

            if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                Player p = (Player) ((Projectile) event.getDamager()).getShooter();
                DamageEvent damageEvent = new DamageEvent(p, target,
                        ((double) (Math.round((event.getDamage()) * 100) / 100)),
                        arrow.getLocation(), critical);
                Bukkit.getServer().getPluginManager().callEvent(damageEvent);
            }
        } else if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            Entity target = event.getEntity();
            Entity damager = event.getDamager();

            boolean critical = damager.getFallDistance() > 0.0F
                    && !damager.isOnGround()
                    && target instanceof LivingEntity
                    && !((LivingEntity) damager).getActivePotionEffects().contains(PotionEffectType.BLINDNESS)
                    && damager.getVehicle() == null ;

            //TODO: Custom WildCore critical values
            if (damager instanceof Player) {
                Player p = (Player) damager;
                DamageEvent damageEvent = new DamageEvent(p, target,
                        ((double) (Math.round((event.getDamage()) * 100) / 100)),
                        target.getLocation().add(0, target.getHeight() + 1, 0),
                        critical);

                //TODO: Apply perks


                // Show results
                Bukkit.getServer().getPluginManager().callEvent(damageEvent);
            }

        }
    }

    // Displays a hologram if damage is detected
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWildDamageEvent(DamageEvent event) {
        if (event.getDamager() instanceof Player || DamageIndicatorsEnabled) {
            Player p = (Player) event.getDamager();
            getHologramManager().displayFallingHologram(
                    List.of("" + (event.isCritical() ? ChatColor.YELLOW : ChatColor.RED) + (event.getDmg(this) > 0 ? event.getDmg(this) : "IMMUNE")),
                    event.getLocation(),
                    p
            );
        }
    }
}

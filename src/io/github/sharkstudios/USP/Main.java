package io.github.sharkstudios.USP;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;


public class Main extends JavaPlugin {
	
    public static Economy econ = null;
	
	@Override
    public void onEnable() {
        this.saveDefaultConfig();
		this.getConfig().options().copyDefaults(true);
		this.getCommand("sklep").setExecutor(new ShopCmd(this));
		getServer().getPluginManager().registerEvents(new ShopCmd(this), this);
		setupEconomy();
    }
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    @Override
    public void onDisable() {
    }

}

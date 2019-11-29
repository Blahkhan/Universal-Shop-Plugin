package io.github.sharkstudios.USP;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.milkbowl.vault.economy.EconomyResponse;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;

public class ShopCmd implements CommandExecutor, Listener  {

	Main main;
	Inventory shopInventory;
	
	public ShopCmd(Main mi) {
		main = mi;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if(sender instanceof Player) {
			int index = 0;
			Player p = (Player) sender;
			shopInventory = Bukkit.createInventory(null, 9, main.getConfig().getString("shopname"));
			for (String s : main.getConfig().getStringList("category")) {
				MinecraftKey mk = new MinecraftKey(main.getConfig().getString(s+".i1.id"));
				ItemStack it = CraftItemStack.asNewCraftStack(Item.REGISTRY.get(mk));
				ItemMeta itMeta = it.getItemMeta();
				int suff = index + 1;
				itMeta.setDisplayName(main.getConfig().getString(s+".name") + "/" + suff);
				it.setItemMeta(itMeta);
				shopInventory.setItem(index, it);
				index++;
			}
			p.openInventory(shopInventory);
			index = 0;
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		ItemStack clicked = e.getCurrentItem();
		Inventory inventory = e.getInventory();
		if (inventory.getName().equals(main.getConfig().getString("shopname"))) {
			e.setCancelled(true);
			if (!clicked.hasItemMeta()) {
				return;
			}
			String[] s;
			s = clicked.getItemMeta().getDisplayName().split("/");
			String category = "c" + s[1];
			Inventory cat = Bukkit.createInventory(null, 9 * 6, main.getConfig().getString(category + ".name" ));
			ItemStack back = new ItemStack(Material.BARRIER);
			ItemMeta backMeta = back.getItemMeta();
			backMeta.setDisplayName("Powrót");
			back.setItemMeta(backMeta);
			cat.setItem(0, back);
			for(int i = 1; i <= main.getConfig().getInt(category + ".count"); i++){
				MinecraftKey mk = new MinecraftKey(main.getConfig().getString(category+".i" + i + ".id"));
				ItemStack it = CraftItemStack.asNewCraftStack(Item.REGISTRY.get(mk));
				ItemMeta mt = it.getItemMeta();
				mt.setDisplayName(main.getConfig().getString(category+".i" + i + ".name"));
				List<String> lore = new ArrayList<String>();
				lore.add(main.getConfig().getString(category+".i" + i + ".price"));
				List<String> cLore = main.getConfig().getStringList(category+".i" + i + ".lore");
				lore.add(cLore.get(0));
				lore.add(cLore.get(1));
				mt.setLore(lore);
				it.setItemMeta(mt);
				cat.setItem(i, it);
			}
			player.closeInventory();
			player.openInventory(cat);
		}else {
			if (clicked.hasItemMeta() && clicked.getItemMeta().getDisplayName().equals("Powrót")) {
				e.setCancelled(true);
				int index = 0;
				shopInventory = Bukkit.createInventory(null, 9, main.getConfig().getString("shopname"));
				for (String s : main.getConfig().getStringList("category")) {
					MinecraftKey mk = new MinecraftKey(main.getConfig().getString(s+".i1.id"));
					ItemStack it = CraftItemStack.asNewCraftStack(Item.REGISTRY.get(mk));
					ItemMeta itMeta = it.getItemMeta();
					int suff = index + 1;
					itMeta.setDisplayName(main.getConfig().getString(s+".name") + "/" + suff);
					it.setItemMeta(itMeta);
					shopInventory.setItem(index, it);
					index++;
				}
				player.openInventory(shopInventory);
				index = 0;
				return;
			}
			if (clicked.hasItemMeta()) {
				e.setCancelled(true);
				List<String> lore = clicked.getItemMeta().getLore();
				int cena =  Integer.parseInt(lore.get(0));
				EconomyResponse r = Main.econ.depositPlayer(player, cena);
				if(r.transactionSuccess()) {
					ItemStack item = clicked;
					item.setItemMeta(null);
	                player.getInventory().addItem(item);
	                player.sendMessage("Zakup udany");
	            } else {
	            	player.sendMessage("Nie masz tyle pieniêdzy");
	            }
			}
		}
	}

}

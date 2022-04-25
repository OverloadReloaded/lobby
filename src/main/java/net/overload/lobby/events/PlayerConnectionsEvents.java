package net.overload.lobby.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectionsEvents implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		//Profile profile = CommonsPluginBukkit.get().pm.getProfile(p.getUniqueId());
		
		p.teleport(new Location(p.getWorld(), -3.5D, 45D, -2.5D));
	}
}

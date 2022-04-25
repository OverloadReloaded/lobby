package net.overload.lobby;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.GsonBuilder;

import net.overload.commons.CommonsPluginBukkit;
import net.overload.commons.config.OverloadConfiguration;
import net.overload.commons.databases.OverloadRedis;
import net.overload.commons.databases.mongo.OverloadMongoDatabase;
import net.overload.commons.logger.LogLevel;
import net.overload.commons.logger.Logger;
import net.overload.commons.server.MinecraftServerInfo;
import net.overload.commons.server.MinecraftServerState;
import net.overload.commons.server.MinecraftServerType;
import net.overload.lobby.events.PlayerConnectionsEvents;
import redis.clients.jedis.JedisPubSub;

public class LobbyPlugin extends JavaPlugin {

private static LobbyPlugin instance;
	
	public Logger log = new Logger("Lobby");
	public OverloadConfiguration config;
	public OverloadRedis redis;
	public OverloadMongoDatabase database;
	
	public MinecraftServerInfo msi;
	
	/**
	 * Plugin interface
	 */
	
	@Override
	public void onLoad() {		
	}
	
	@Override
	public void onEnable() {
		instance = this;
		log.send(LogLevel.SUCCESS, "Enabled Lobby plugin !");
		
		connectRedis();
		database = CommonsPluginBukkit.get().database;
		config = CommonsPluginBukkit.get().config;
		
		msi = CommonsPluginBukkit.get().msi;

		msi.setType(MinecraftServerType.LOBBY);
		msi.setState(MinecraftServerState.STARTED);
		msi.update(true);
	}

	@Override
	public void onDisable() {
		log.send(LogLevel.INFO, "Disabling Lobby plugin!");
	}
	
	
	/**
	 * Custom functions
	 */
	
	public void connectRedis() {
		redis = CommonsPluginBukkit.get().redis;
		new Thread(new Runnable() {
			@Override
			public void run() {
				JedisPubSub pubsub = new JedisPubSub() {
					@SuppressWarnings({ "unchecked", "unused" })
					@Override
					public void onMessage(String channel, String message) {
						if(channel.equals("toLobbies")) {
							String[] argss = message.split(";");
							if(argss[0].equals("send")) {
								
							}
						}
						
						if(channel.equals("toLobbiesUpdates")) {
							ArrayList<MinecraftServerInfo> lmsi = new GsonBuilder().create().fromJson(message, ArrayList.class);
							logger().send(LogLevel.DEBUG, channel, message);
						}
					}
					
					@Override
					public void onSubscribe(String channel, int subscribedChannels) {
						log.send(LogLevel.INFO, "Redis", String.format("Subscribed to channel : %s.", channel));
					}
				};

				redis.setPubSub(pubsub);
				redis.getJedis().subscribe(pubsub, new String[] { "toLobbies", "toLobbiesUpdates" });
			}
		}).start();
	}
	
	public void registerListeners() {
		PluginManager pluginManager = Bukkit.getPluginManager();
		
		pluginManager.registerEvents(new PlayerConnectionsEvents(), this);
	}

	
	/**
	 * Getters & Setters
	 */
	
	public Logger logger() {
		return log;
	}
	
	public static LobbyPlugin get() {
		return instance;
	}

}

package com.cyntran.generals;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

//import org.json.JSONException;
//import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter.Listener;

public class Client {
	Socket socket;
	onConnect connect;
	gameListener gameConnect;
    JSONObject obj;


	public Client(String userId, String userName) {
		try {
			connect = new onConnect(userId, userName);
			socket = IO.socket("http://botws.generals.io/");
			
			socket.once(Socket.EVENT_CONNECT, connect);
			
			socket.on("chat_message", connect); 

			socket.on(Socket.EVENT_DISCONNECT, connect);
			
			socket.on("game_update", gameConnect);
			
			socket.connect();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}	
	}

	public ArrayList<Integer> patch(ArrayList<Integer> old, ArrayList<Integer> diff) {
		ArrayList<Integer> out = new ArrayList<Integer>();
		int i = 0;
		while(i < diff.size()) {
			if (diff.get(i) != 0) { //number of unchanged items
				out.addAll(old.subList(out.size(), out.size() + diff.get(i)));
			}
			i++;
			if (i < diff.size() && diff.get(i) != 0) { //number of changed items
				out.addAll(diff.subList(i + 1, i + 1 + diff.get(i)));
			}
			i++;
		}
		return out;
	}
	
	public void createPrivate(String gameName) {
		connect.private_game = true;
		connect.gameName = gameName;
	}

	public void createMessage(String message) {
		connect.chat = true;
		connect.chatMessage = message;
	}
	
	public void disconnect() {
		connect.disconnect = true;
	}

	class gameListener implements Listener {
	    int playerIndex;
	    ArrayList<Integer> generals; //an array ordered by playerIndex of generals
	    ArrayList<Integer> cities;
	    ArrayList<Integer> maps;
	    boolean patch = false;

		@Override
		public void call(Object... args) {
			if (patch)
				try {
					cities = patch(cities, (ArrayList<Integer>) obj.get("cities_diff"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			
		}
		
		public void doPatch() {
			patch = true;
		}
		
		
		
	}
	
	class onConnect implements Listener {
		String gameName;
		String chatMessage;
		String userId;
		String userName;
		boolean private_game, chat, init, disconnect = false;

		public onConnect(String userId, String userName) {
			init = true;
			this.userId = userId;
			this.userName = userName;
		}

		@Override
		public void call(Object... args) {
			
			if (init) {
				socket.emit("set_username", userId, userName);
	    	    System.out.println("connected");
	    	    init = false;
			}
			
			if (private_game) {
				socket.emit("join_private", gameName, userId);
				private_game = false;
			}

			if (chat) {
				socket.emit("chat_message", "chat_custom_queue_" + gameName, chatMessage);
				chat = false;
			}
			if (disconnect) {
				System.out.println("disconnected");
			}
		}
	}

}


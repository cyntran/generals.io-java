package com.cyntran.generals;

import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class App {
    public static void main( String[] args ) {
		try {
	    	final Socket socket;
			socket = IO.socket("http://botws.generals.io/");
			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

		    	  @Override
		    	  public void call(Object... args) {
		    	    socket.emit("foo", "hi");
		    	    System.out.println("connected");
		    	    socket.emit("set_username", "kiido789", "Kiifox");
		    	    socket.emit("join_private", "dogs", "kiido789");
		    	    socket.emit("set_force_start", "dogs", true);
		    	    
		    	  }

		    	}).on("chat_message", new Emitter.Listener() {

		    	  @Override
		    	  public void call(Object... args) {
		    		  JSONObject obj = (JSONObject)args[1];
		    		  try {
						if (obj.get("text").equals("you suck")) {
							  socket.emit("chat_message", args[0], "your mum sucks XD");
							  System.out.println("zzzzzz: " + args[0]);
						  }
						  System.out.println(obj.get("text"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	  }

		    	}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

		    	  @Override
		    	  public void call(Object... args) {
			    	 System.out.println("disconnected");
		    	  }

		    	});
		    	socket.connect();
		    	
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}

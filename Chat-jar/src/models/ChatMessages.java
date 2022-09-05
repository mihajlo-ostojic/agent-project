package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatMessages implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Map<String, List<Message>> chats;
	
	public ChatMessages() {
		chats = new HashMap<>();
		chats.put("ALL", new ArrayList<>());
	}

	public void sendMessage(Message message) {
		String reciver = message.getReciver();
		if (reciver.equals("ALL")) {
			chats.get("ALL").add(message);
		} else {
			if (!chats.containsKey(reciver)) {
				chats.put(reciver, new ArrayList<>());
			}
			chats.get(reciver).add(message);	
		}
	}
	
	public void receiveMessage(Message message) {
		String sender = message.getSender();
//		String reciver = message.getReciver();
		String reciverreal = message.getRealReciver();
		if (reciverreal != null) {
			chats.get("ALL").add(message);
		} else {
			if (!chats.containsKey(sender)) {
				chats.put(sender, new ArrayList<>());
			}
			chats.get(sender).add(message);	
		}

	}
	
	public List<Message> getAllChat() {
		return chats.get("ALL");
	}
	
	public List<Message> getChatForUser(String username) {
		return chats.get(username);
	}
}

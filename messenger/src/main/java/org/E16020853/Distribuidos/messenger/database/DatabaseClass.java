package org.E16020853.Distribuidos.messenger.database;

import java.util.HashMap;
import java.util.Map;

import org.E16020853.Distribuidos.messenger.model.Message;
import org.E16020853.Distribuidos.messenger.model.Profile;

public class DatabaseClass {

	private static Map<Long, Message> messages = new HashMap<>();
	//private static Map<Long, Profile> profiles = new HashMap<>();
	private static Map<String, Profile> profiles = new HashMap<>();

	public static Map<Long, Message> getMessages() {
		return messages;
	}
	/*public static Map<Long, Profile> getProfiles() {
		return profiles;
	}*/
	public static Map<String, Profile> getProfiles() {
		return profiles;
	}
	
	
}

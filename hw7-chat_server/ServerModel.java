import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * The {@code ServerModel} is the class responsible for tracking the state of
 * the server, including its current users and the channels they are in. This
 * class is used by subclasses of {@link Command} to handle commands from
 * clients, as well as the {@link ServerBackend} to coordinate client connection
 * and disconnection.
 */
public final class ServerModel implements ServerModelApi {

	private TreeMap<String, Channel> allChannels; 
	private TreeSet<Client> allUsers; 

	/**
	 * Constructs a {@code ServerModel} and initializes any collections
	 * needed for modeling the server state.
	 */
	public ServerModel() {
		allChannels = new TreeMap<String, Channel>(); 
		allUsers = new TreeSet<Client>(); 
	}


	//==========================================================================
	// Client connection handlers
	//==========================================================================

	/**
	 * Informs the model that a client has connected to the server with the
	 * given user ID. The model should update its state so that it can
	 * identify this user during later interactions with the model. Any user
	 * that is registered with the server (without being later deregistered)
	 * should appear in the output of {@link #getRegisteredUsers()}.
	 * @param userId the unique ID created by the backend to represent this user
	 * @return a {@link Broadcast} informing the user of their new nickname
	 */
	public Broadcast registerUser(int userId) {
		String nickname = generateUniqueNickname();
		Client c = new Client(userId, nickname); 
		allUsers.add(c); 
		return Broadcast.connected(nickname); 
	}

	/**
	 * Generates a unique nickname of the form "UserX", where X is the
	 * smallest non-negative integer that yields a unique nickname for a user.
	 * @return the generated nickname
	 */
	private String generateUniqueNickname() {
		int suffix = 0;
		String nickname;
		Collection<String> existingUsers = getRegisteredUsers();
		do {
			nickname = "User" + suffix++;
		} while (existingUsers != null && existingUsers.contains(nickname));
		return nickname;
	}

	/**
	 * Determines if a given nickname is valid or invalid (contains at least
	 * one alphanumeric character, and no non-alphanumeric characters).
	 * @param name The channel or nickname string to validate
	 * @return true if the string is a valid name
	 */
	public static boolean isValidName(String name) {
		if (name == null || name.isEmpty()) {
			return false;
		}
		for (char c : name.toCharArray()) {
			if (!Character.isLetterOrDigit(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Informs the model that the client with the given user ID has
	 * disconnected from the server. After a user ID is deregistered, the
	 * server backend is free to reassign this user ID to an entirely
	 * different client. As such, the model should take care to expunge any
	 * state pertaining to a user who has been deregistered. Any user that is
	 * deregistered (without later being registered) should not appear in the
	 * output of {@link #getRegisteredUsers()}. The behavior of this method if
	 * the given user ID is not registered with the model is undefined.
	 * @param userId the unique ID created by the backend to represent this user
	 * @return A {@link Broadcast} informing other clients in the
	 * disconnected user's channels that they have disconnected
	 */
	public Broadcast deregisterUser(int userId) {
		Client removeMe = null; 
		String removeMeName = null; 
		for (Client c : allUsers) {
			if (c.getUserID() == userId) {
				removeMe = c; 
				removeMeName = removeMe.getNickname(); 
			}
		}
		LinkedList<Channel> channelsIn = removeMe.getChannelsIn(); 
		TreeSet<String> informMe = new TreeSet<String>(); 

		for (Channel c : channelsIn) {
			informMe.addAll(c.getAllUserNames());     		
			c.deleteUser(userId);
		}
		informMe.remove(removeMeName); 

		allUsers.remove(removeMe); 
		return Broadcast.disconnected(removeMeName, informMe); 
	}



	//==========================================================================
	// Model update functions
	//==========================================================================

	public void changeNickname(String newNickname, int userId) { 
		Client user = null; 
		for (Client c : allUsers) {
			if (c.getUserID() == userId) user = c; 
		}
		user.setNickname(newNickname);

		for (Channel ch : user.getChannelsIn()) {
			ch.changeUsername(userId, newNickname);
		}
	}


	/*returns recipients of all channels given client is in*/
	public Set<String> getRecipientsInAllChannels(int userId) {
		Client user = null; 
		for (Client c : allUsers) {
			if (c.getUserID() == userId) {user = c; break;} 
		}
		LinkedList<Channel> channelsIn = user.getChannelsIn(); 
		TreeSet<String> informMe = new TreeSet<String>(); 
		for (Channel ch : channelsIn) {
			informMe.addAll(ch.getAllUserNames()); 
			informMe.add(ch.getOwner());
		}
		return informMe; 
	}

	//return string set of all recipients in given channel
	public Set<String> getRecipientsInOneChannel(String channelName) {
		Channel ch = allChannels.get(channelName);
		Set<String> usernameList = new TreeSet<String>(); 
		Collection<String> nameCollection = ch.getAllUserNames(); 
		usernameList.addAll(nameCollection); 
		return usernameList; 
	}

	/*checks if given nickname is already in allUsers database*/
	public boolean nicknameExist(String newNickname) {
		for (Client c : allUsers) {
			if (newNickname.equals(c.getNickname())) {
				return true; 
			}
		}
		return false; 
	}

	/*creates a new channel, adds owner into it, adds channel
	 * into user's channelsIn field, adds channel to allChannels*/
	public void createChannel(int senderId, String owner, 
			String channelName, boolean isPrivate) {
		Channel c = new Channel(owner, channelName, isPrivate); 
		c.addUser(senderId, owner);
		allChannels.put(channelName, c); 

		Client user = null; 
		for (Client cl : allUsers) {
			if (cl.getNickname().equals(owner)) user = cl; 
		}
		user.addChannelIn(c); 
	}

	public boolean channelExists(String channelName) {
		return allChannels.containsKey(channelName); 
	}

	public void addUserToChannel(String channelName, String userName, int userID) {
		Channel ch = allChannels.get(channelName); 
		ch.addUser(userID, userName);

		Client user = null; 
		for (Client c : allUsers) {
			if (c.getUserID() == userID) user = c; 
		}
		user.addChannelIn(ch); 
	}

	public String getOwnerOfChannel(String channelName) {
		Channel ch = allChannels.get(channelName); 
		return ch.getOwner(); 
	}

	public boolean isUserInChannel(String user, String channelName) {
		Channel ch = allChannels.get(channelName);
		return ch.isUserInChannel(user); 
	}

	//removes given user from given channel
	public void removeUserFromChannel(String user, String channelName) {
		Channel ch = allChannels.get(channelName);
		Set<Integer> allUserIDs = ch.getAllUserIDs(); 

		/*if user that leaves is the owner of the channel
		 * update all users in channel's channelsIn, delete channel 
		 * from allChannels, delete owner from allUsers
		 */
		if (user.equals(ch.getOwner())) { 
			for (int id : allUserIDs) {
				for (Client c : allUsers) {
					if (c.getUserID() == id) {
						c.deleteChannelIn(ch); 
					}
				}
			}
			allChannels.remove(channelName);
		} 
		//if user that leaves is not the owner of the channel
		else {
			Client removeMe = null; 
			for (Client c : allUsers) {
				if (c.getNickname().equals(user)) {
					removeMe = c; 
				}
			}
			ch.deleteUser(removeMe.getUserID());
		}
	}

	public boolean isInviteOnly (String channelName) {
		Channel ch = allChannels.get(channelName);
		return ch.getIsPrivate(); 
	}

	public boolean doesUserExist (String username) {
		for (Client c : allUsers) {
			if (c.getNickname().equals(username)) {
				return true;
			}
		}
		return false; 
	}

	/*returns ID of client with given nickname*/
	public int getIdFromName (String username) {
		for (Client c : allUsers) {
			if (c.getNickname().equals(username)) {
				return c.getUserID(); 
			}
		}
		return -1; 
	}

	//kicks out given user from channel
	public void kickUserFromChannel(String kickMe, String channelName) {
		Channel ch = allChannels.get(channelName);
		Set<Integer> allUserIDs = ch.getAllUserIDs(); 

		/*if user that leaves is the owner of the channel
		 * update all users in channel's channelsIn, delete channel 
		 * from allChannels, delete owner from allUsers
		 */
		if (kickMe.equals(ch.getOwner())) { 
			for (int id : allUserIDs) {
				for (Client c : allUsers) {
					if (c.getUserID() == id) {
						c.deleteChannelIn(ch); 
					}
				}
			}
			allChannels.remove(channelName); 
		} 
		//if user that leaves is not the owner of the channel
		else {
			Client removeMe = null; 
			for (Client c : allUsers) {
				if (c.getNickname().equals(kickMe)) {
					removeMe = c; 
				}
			}
			ch.deleteUser(removeMe.getUserID());
			removeMe.deleteChannelIn(ch); 
		}
	}


	//==========================================================================
	// Server model queries
	// These functions provide helpful ways to test the state of your model.
	// You may also use them in your implementation.
	//==========================================================================

	/**
	 * Returns the user ID currently associated with the given nickname.
	 * The returned ID is -1 if the nickname is not currently in use.
	 * @param nickname The user's nickname
	 * @return the id of said user
	 */
	public int getUserId(String nickname) {

		Iterator<Client> iter = allUsers.iterator(); 
		while (iter.hasNext()) {
			Client c = iter.next(); 
			if (c.getNickname().equals(nickname)) {
				return c.getUserID(); 
			}
		}
		return -1; 

	}

	/**
	 * Returns the nickname currently associated with the given user ID.
	 * The returned string is null if the user ID is not currently in use.
	 * @param userId The ID whose nickname to return.
	 * @return The nickname associated with the current ID.
	 */
	public String getNickname(int userId) {
		Iterator<Client> iter = allUsers.iterator(); 
		while (iter.hasNext()) {
			Client c = iter.next(); 
			if (c.getUserID() == userId) {
				return c.getNickname(); 
			}
		}
		return null; 
	}

	/**
	 * Returns a collection of the nicknames of all users that are registered
	 * with the server. Provided for testing.
	 * @return the collection of registered user nicknames
	 */
	public Collection<String> getRegisteredUsers() {
		TreeSet <String> allNames = new TreeSet<String>(); 
		for (Client c : allUsers) {
			allNames.add(c.getNickname()); 
		}
		return allNames; 
	}

	/**
	 * Returns a collection of the names of all the channels that are present
	 * on the server. The returned collection is empty if no channels exist.
	 * Provided for testing.
	 * @return the collection of channel names
	 */
	public Collection<String> getChannels() {
		return allChannels.keySet(); 
	}

	/**
	 * Returns a collection of the nicknames of all the users in a given
	 * channel. The returned collection is empty if no channel with the given
	 * name exists. Provided for testing.
	 * @param channelName The channel whose member nicknames should be returned
	 * @return the collection of user nicknames in the current channel
	 */
	public Collection<String> getUsers(String channelName) {
		Channel x = allChannels.get(channelName); 
		return x.getAllUserNames(); 
	}

	/**
	 * Returns the nickname of the owner of the current channel. The result is
	 * {@code null} if no channel with the given name exists. Provided for
	 * testing.
	 * @param channelName The channel whose owner nickname should be returned
	 * @return the nickname of the channel owner
	 */
	public String getOwner(String channelName) {
		Channel x = allChannels.get(channelName); 
		return x.getOwner(); 
	}

}

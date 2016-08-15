import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/*channel class contains owner nickname, channel name,
 * treemap of all users currently on channel, and invite 
 * only status
 */

public class Channel {
	private String owner; 
	private String name; 
	private TreeMap<Integer, String> allUsers; 
	private boolean isPrivate; 

	Channel (String owner, String name, boolean isPrivate) {
		this.owner = owner; 
		this.name = name; 
		allUsers = new TreeMap<Integer, String>(); 
		this.isPrivate = isPrivate; 
	}

	public String getOwner() { return owner; }

	public String getChannelName() { return name; }

	public void setChannelName(String newName) { name = newName; }

	public Set<Integer> getAllUserIDs() {
		return allUsers.keySet(); 
	}

	public Collection<String> getAllUserNames() {
		return allUsers.values(); 
	}

	public TreeMap<Integer, String> getAllUsers() {
		return allUsers; 
	}

	public void addUser(int id, String name) {
		allUsers.put(id, name);
	}

	public void deleteUser(int id) {
		allUsers.remove(id); 
	}

	public void changeUsername(int userID, String newName) {
		allUsers.remove(userID); 
		allUsers.put(userID, newName); 
	}

	public boolean getIsPrivate() {
		return isPrivate; 
	}

	public boolean isUserInChannel (String username) {
		return allUsers.containsValue(username); 
	}

}

import java.util.LinkedList;
import java.util.TreeSet;

/*client class contains user id nickname, and linked list of
 * channels client is currently in
 */
public class Client implements Comparable<Client> {

	private int userID; 
	private String nickname; 
	private LinkedList<Channel> channelsIn; 

	Client (int userID, String nickname) {
		this.userID = userID; 
		this.nickname = nickname; 
		this.channelsIn = new LinkedList<Channel>(); 
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getUserID() {
		return userID;
	}

	public LinkedList<Channel> getChannelsIn() {
		return channelsIn; 
	}

	public boolean addChannelIn (Channel c) {
		return channelsIn.add(c); 
	}

	public boolean deleteChannelIn (Channel c) {
		return channelsIn.remove(c); 
	}

	@Override
	public int compareTo(Client o) {
		if (userID < o.getUserID()) { return -1; }
		else if (userID > o.getUserID()) {return 1; }
		else return 0; 
	}

}

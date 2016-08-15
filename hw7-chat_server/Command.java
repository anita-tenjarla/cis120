import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a command string sent from a client to the server, after it has
 * been parsed into a more convenient form. The {@code Command} abstract class
 * has a concrete subclass corresponding to each of the possible commands that
 * can be issued by a client. The protocol specification contains more
 * information about the expected behavior of various commands.
 */
public abstract class Command {

	// The keyword protected is used so only subclasses have access to it
	protected int senderId;
	protected String sender;
	protected Command(int senderId, String sender) {
		this.senderId = senderId;
		this.sender = sender;
	}

	/**
	 * Returns the user ID of the client who issued the {@code Command}.
	 * @return a int which is the current user ID of the client
	 */
	public int getSenderId() {
		return senderId;
	}

	/**
	 * Returns the nickname of the client who issued the {@code Command}.
	 * @return a non-null string which is the current nickname of the client
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * Processes the command and updates the server model accordingly.
	 * @param model An instance of the {@link ServerModelApi} class which
	 *              represents the current state of the server.
	 * @return A {@link Broadcast} object, informing clients about changes
	 * resulting from the command.
	 */
	public abstract Broadcast updateServerModel(ServerModel model);

	/**
	 * Returns {@code true} if two {@code Command}s are equal; that is, they
	 * produce the same string representation.
	 * @param o the object to compare with {@code this} for equality
	 * @return true iff both objects are non-null and equal to each other
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Command)) {
			return false;
		}
		return this.toString().equals(o.toString());
	}
}


//==============================================================================
// Command subclasses
//==============================================================================

/**
 * Represents a {@link Command} issued by a client to change their nickname.
 */
class NicknameCommand extends Command {

	protected int senderId; 
	protected String sender; 
	protected String newNickname; 

	public NicknameCommand(int senderId, String sender, String newNickname) {
		super(senderId, sender);
		this.newNickname = newNickname;
		this.sender = sender; 
		this.newNickname = newNickname; 
	}

	@Override
	public Broadcast updateServerModel(ServerModel model) {
		if (!ServerModel.isValidName(getNewNickname())) {
			return Broadcast.error(this, ServerError.INVALID_NAME); 
		} else if (ServerModel.isValidName(getNewNickname()) && 
				model.nicknameExist(getNewNickname())) {
			return Broadcast.error(this, ServerError.NAME_ALREADY_IN_USE); 
		}
		else {
			model.changeNickname(getNewNickname(), getSenderId()); 
			return Broadcast.okay(this, 
					model.getRecipientsInAllChannels(getSenderId())); 
		}
	}

	public String getNewNickname() {
		return newNickname; 
	}

	@Override
	public String toString() {
		return String.format(":%s NICK %s", getSender(), newNickname);
	}
}


/**
 * Represents a {@link Command} issued by a client to create a new channel.
 */
class CreateCommand extends Command {
	private String channel;
	private boolean inviteOnly;

	public CreateCommand(int senderId, String sender, String channel, boolean inviteOnly) {
		super(senderId, sender);
		this.channel = channel;
		this.inviteOnly = inviteOnly;
	}

	@Override
	public Broadcast updateServerModel(ServerModel model) {
		if (!ServerModel.isValidName(getChannel())) {
			return Broadcast.error(this, ServerError.INVALID_NAME); 
		}
		else if (ServerModel.isValidName(getChannel()) && 
				model.channelExists(getChannel())) {
			return Broadcast.error(this, ServerError.CHANNEL_ALREADY_EXISTS); 
		}
		else {
			model.createChannel(getSenderId(), getSender(), getChannel(), isInviteOnly()); 
			return Broadcast.okay(this, model.getRecipientsInOneChannel(getChannel())); 
		}
	}

	public String getChannel() {
		return channel;
	}

	public boolean isInviteOnly() {
		return inviteOnly;
	}

	@Override
	public String toString() {
		int flag = inviteOnly ? 1 : 0;
		return String.format(":%s CREATE %s %d", getSender(), channel, flag);
	}
}

/**
 * Represents a {@link Command} issued by a client to join an existing channel.
 */
class JoinCommand extends Command {
	private String channel;

	public JoinCommand(int senderId, String sender, String channel) {
		super(senderId, sender);
		this.channel = channel;
	}

	@Override
	public Broadcast updateServerModel(ServerModel model) {
		if (!model.channelExists(getChannel())) {
			return Broadcast.error(this, ServerError.NO_SUCH_CHANNEL); 
		} 
		else if (model.isInviteOnly(getChannel())) {
			return Broadcast.error(this, ServerError.JOIN_PRIVATE_CHANNEL); 
		}
		else {
			model.addUserToChannel(getChannel(), getSender(), getSenderId());
			return Broadcast.names(this, model.getRecipientsInOneChannel(getChannel()), 
					model.getOwnerOfChannel(getChannel())); 

		}
	}

	public String getChannel() {
		return channel;
	}

	@Override
	public String toString() {
		return String.format(":%s JOIN %s", getSender(), channel);
	}
}

/**
 * Represents a {@link Command} issued by a client to send a message to all
 * other clients in the channel.
 */
class MessageCommand extends Command {
	private String channel;
	private String message;

	public MessageCommand(int senderId, String sender, String channel, String message) {
		super(senderId, sender);
		this.channel = channel;
		this.message = message;
	}

	@Override
	public Broadcast updateServerModel(ServerModel model) {
		if (!model.channelExists(channel)) {
			return Broadcast.error(this, ServerError.NO_SUCH_CHANNEL); 
		}
		if (!model.isUserInChannel(sender, channel)) {
			return Broadcast.error(this, ServerError.USER_NOT_IN_CHANNEL); 
		}
		else {
			return Broadcast.okay(this, model.getRecipientsInOneChannel(channel)); 
		}
	}

	@Override
	public String toString() {
		return String.format(":%s MESG %s :%s", getSender(), channel, message);
	}
}

/**
 * Represents a {@link Command} issued by a client to leave a channel.
 */
class LeaveCommand extends Command {
	private String channel;

	public LeaveCommand(int senderId, String sender, String channel) {
		super(senderId, sender);
		this.channel = channel;
	}

	@Override
	public Broadcast updateServerModel(ServerModel model) {
		if (!model.channelExists(channel)) {
			return Broadcast.error(this, ServerError.NO_SUCH_CHANNEL); 
		}
		if (!model.isUserInChannel(sender, channel)) {
			return Broadcast.error(this, ServerError.USER_NOT_IN_CHANNEL); 
		}
		else {
			Set<String> recipients = model.getRecipientsInOneChannel(channel); 
			model.removeUserFromChannel(sender, channel);
			return Broadcast.okay(this, recipients); 
		}
	}

	@Override
	public String toString() {
		return String.format(":%s LEAVE %s", getSender(), channel);
	}
}

/**
 * Represents a {@link Command} issued by a client to add another client to an
 * invite-only channel owned by the sender.
 */
class InviteCommand extends Command {
	private String channel;
	private String userToInvite;

	public InviteCommand(int senderId, String sender, String channel, String userToInvite) {
		super(senderId, sender);
		this.channel = channel;
		this.userToInvite = userToInvite;
	}

	@Override
	public Broadcast updateServerModel(ServerModel model) {
		if (!model.doesUserExist(userToInvite)) {
			return Broadcast.error(this, ServerError.NO_SUCH_USER); 
		} else if (!model.isInviteOnly(channel)) {
			return Broadcast.error(this, ServerError.INVITE_TO_PUBLIC_CHANNEL); 
		} else if (!model.channelExists(channel)) {
			return Broadcast.error(this, ServerError.NO_SUCH_CHANNEL); 
		} else if (!(model.getOwnerOfChannel(getChannel()).equals(sender))) {
			return Broadcast.error(this, ServerError.USER_NOT_OWNER); 
		} else {      	
			int id = model.getIdFromName(getUserToInvite()); 
			model.addUserToChannel(getChannel(), getUserToInvite(), id);
			Set<String> recipients = model.getRecipientsInOneChannel(channel);
			return Broadcast.names(this, recipients, sender); 
		}
	}

	public String getChannel() {
		return channel;
	}

	public String getUserToInvite() {
		return userToInvite;
	}

	@Override
	public String toString() {
		return String.format(":%s INVITE %s %s", getSender(), channel, userToInvite);
	}
}

/**
 * Represents a {@link Command} issued by a client to remove another client
 * from an invite-only channel owned by the sender.
 */
class KickCommand extends Command {
	private String channel;
	private String userToKick;

	public KickCommand(int senderId, String sender, String channel, String userToKick) {
		super(senderId, sender);
		this.channel = channel;
		this.userToKick = userToKick;
	}

	@Override
	public Broadcast updateServerModel(ServerModel model) {
		if (!model.doesUserExist(userToKick)) {
			return Broadcast.error(this, ServerError.NO_SUCH_USER); 
		} else if (!(model.getOwnerOfChannel(channel).equals(sender))) {
			return Broadcast.error(this, ServerError.USER_NOT_OWNER); 
		} else if (!model.channelExists(channel)) {
			return Broadcast.error(this, ServerError.NO_SUCH_CHANNEL); 
		} else if (!model.isUserInChannel(sender, channel)) {
			return Broadcast.error(this, ServerError.USER_NOT_IN_CHANNEL); 
		} else {
			Set<String> recipients = model.getRecipientsInOneChannel(channel); 
			model.kickUserFromChannel(userToKick, channel);
			return Broadcast.okay(this, recipients); 
		}
	}

	@Override
	public String toString() {
		return String.format(":%s KICK %s %s", getSender(), channel, userToKick);
	}
}


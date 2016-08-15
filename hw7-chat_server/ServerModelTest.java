import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class ServerModelTest {
	private ServerModel model;

	@Before
	public void setUp() {
		// We initialize a fresh ServerModel for each test
		model = new ServerModel();
	}

	@Test
	public void testOwnerLeaves() {
		model.registerUser(0); 
		model.registerUser(1);
		Command create = new CreateCommand(0, "User0", "java", false);
		create.updateServerModel(model);
		Command join = new JoinCommand(1, "User1", "java");
		join.updateServerModel(model);


		Command leave = new LeaveCommand(0, "User0", "java");
		Set<String> recipients = new TreeSet<>();
		recipients.add("User1");
		recipients.add("User0"); 
		Broadcast expected = Broadcast.okay(leave, recipients);

		assertEquals("broadcast", expected, leave.updateServerModel(model));
		assertFalse("channel no longer exists",
				model.channelExists("java")); 

	}

	@Test
	public void testChannelAlreadyExists() {
		model.registerUser(0); 
		model.registerUser(1);
		Command create1 = new CreateCommand(0, "User0", "java", false);
		create1.updateServerModel(model);
		Command create2 = new CreateCommand(1, "User1", "java", true); 

		Broadcast expected = Broadcast.error(create2, 
				ServerError.CHANNEL_ALREADY_EXISTS);
		assertEquals("error", expected, create2.updateServerModel(model));
	}

	@Test
	public void testCantChatWithUs() {
		model.registerUser(0); 
		model.registerUser(1);
		Command create = new CreateCommand(0, "User0", "exclusive", true);
		create.updateServerModel(model);
		Command join = new JoinCommand(1, "User1", "exclusive"); 

		Broadcast expected = Broadcast.error(join, 
				ServerError.JOIN_PRIVATE_CHANNEL);
		assertEquals("error", expected, join.updateServerModel(model));
	}

	@Test
	public void testThreeMembersOneChannelRemoveOne() {
		model.registerUser(0); 
		model.registerUser(1);
		model.registerUser(2); 
		Command create = new CreateCommand(0, "User0", "java", false);
		create.updateServerModel(model);

		Command join1 = new JoinCommand(1, "User1", "java");
		join1.updateServerModel(model); 
		Command join2 = new JoinCommand(2, "User2", "java");

		Set<String> recipients = new TreeSet<>();
		recipients.add("User2"); 
		recipients.add("User1");
		recipients.add("User0");
		Broadcast expected = Broadcast.names(join2, recipients, "User0");
		assertEquals("broadcast", expected, join2.updateServerModel(model));

		assertTrue("User0 in channel",
				model.getUsers("java").contains("User0"));
		assertTrue("User2 in channel",
				model.getUsers("java").contains("User2"));
		assertEquals("num. users in channel", 3,
				model.getUsers("java").size());

		Command leave = new LeaveCommand(2, "User2", "java"); 
		Broadcast expected2 = Broadcast.okay(leave, recipients); 
		assertEquals("broadcast", expected2, leave.updateServerModel(model));
		assertFalse("User2 not in channel",
				model.getUsers("java").contains("User2"));
		assertEquals("num. users in channel", 2,
				model.getUsers("java").size());
	}

	@Test
	public void testKickOwner() {
		model.registerUser(0); 
		model.registerUser(1); 
		Command create = new CreateCommand(0, "User0", "stupid", true);
		create.updateServerModel(model); 

		Command invite = new InviteCommand(0, "User0", "stupid", "User1");
		invite.updateServerModel(model);

		Command kick = new KickCommand(0, "User0", "stupid", "User0");
		Set<String> recipients = new TreeSet<>();
		recipients.add("User1");
		recipients.add("User0");

		Broadcast expected = Broadcast.okay(kick, recipients);
		assertEquals(expected, kick.updateServerModel(model));
		assertFalse("channel no longer exists", model.channelExists("stupid")); 
	}

	public void testCantKickDontExist() {
		model.registerUser(0); 
		model.registerUser(1);
		Command create1 = new CreateCommand(0, "User0", "java", false);
		create1.updateServerModel(model);

		Command kick = new KickCommand(0, "User0", "stupid", "User2");

		Broadcast expected = Broadcast.error(kick, 
				ServerError.NO_SUCH_USER);
		assertEquals("error", expected, kick.updateServerModel(model));
	}

	@Test
	public void testAddKickAdd() {
		model.registerUser(0); 
		model.registerUser(1);
		Command create1 = new CreateCommand(0, "User0", "blub", true);
		create1.updateServerModel(model);

		Set<String> recipients = new TreeSet<>();
		recipients.add("User1");
		recipients.add("User0");

		Command invite1 = new InviteCommand(0, "User0", "blub", "User1");
		invite1.updateServerModel(model);

		Broadcast expected = Broadcast.names(invite1, recipients, "User0"); 
		assertEquals(expected, invite1.updateServerModel(model));

		Command kick = new KickCommand(0, "User0", "blub", "User1");
		Broadcast expected2 = Broadcast.okay(kick, recipients);
		assertEquals(expected2, kick.updateServerModel(model));

		Command invite2 = new InviteCommand(0, "User0", "blub", "User1");
		invite2.updateServerModel(model);

		Broadcast expected3 = Broadcast.names(invite2, recipients, "User0"); 
		assertEquals(expected3, invite2.updateServerModel(model));
	}

	@Test
	public void testCantDoThatNotOwner() {
		model.registerUser(0); 
		model.registerUser(1);
		model.registerUser(2); 
		Command create1 = new CreateCommand(0, "User0", "blub", true);
		create1.updateServerModel(model);

		Command invite1 = new InviteCommand(0, "User0", "blub", "User1");
		invite1.updateServerModel(model);

		Command invite2 = new InviteCommand(1, "User1", "blub", "User2");
		Broadcast expected = Broadcast.error(invite2, ServerError.USER_NOT_OWNER); 

		assertEquals("error", expected, invite2.updateServerModel(model));
	}
}

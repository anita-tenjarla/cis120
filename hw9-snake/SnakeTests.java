import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.JLabel;

import org.junit.Before;

public class SnakeTests {
	private GameCourt court; 
	private Creature snake; 
	
	@Before
	public void setUp() {
		JLabel score = new JLabel("Score: "); 
		court = new GameCourt(score);
		court.initState();
		snake = court.getSnake();  
	}
	
	@Test 
	public void snakeBacksIntoSelf() {
		snake.add();
		snake.add();
		for (int i = 0; i < 10; i++) {
			court.tick();
			snake.move();
		}
		for (int i = 0; i < 30; i++) {
		  snake.direction = Direction.LEFT;
		  court.tick();
		  snake.move(); 
		}
		assertFalse("end of game", court.isPlaying()); 
	}
	
	@Test
	public void snakeRunsIntoWall() {
		court.initState();
		for (int i = 0; i < 60; i++) {
			court.tick();
			snake.move();
		}
		assertFalse("end of game", court.isPlaying()); 
		assertTrue("head is past boundary", snake.getHeadX() >= 300 || snake.getHeadY() >= 300); 
	}
	
	@Test
	public void snakeLoopsRunsIntoSelf() {
		court.initState();
		for (int i = 0; i < 10; i++) {
			snake.add();
		}
		snake.direction = Direction.DOWN; 
		for (int i = 0; i < 10; i++) {
			court.tick();
			snake.move();
		}
		snake.direction = Direction.RIGHT; 
		for (int i = 0; i < 10; i++) {
			court.tick();
			snake.move();
		}
		snake.direction = Direction.UP; 
		for (int i = 0; i < 10; i++) {
			court.tick();
			snake.move();
		}
		snake.direction = Direction.LEFT; 
		for (int i = 0; i < 10; i++) {
			court.tick();
			snake.move();
		} 
		assertFalse("end of game", court.isPlaying()); 
	}
	
	@Test
	public void snakeEatsRedEgg() {
		court.addRedEgg(100, 50);
		for (int i = 0; i < 30; i++) {
			court.tick();
			snake.move();
		}
		assertFalse("end of game", court.isPlaying());
	}
	
	@Test
	public void snakeJustMissesRedEggGetsYellowEgg() {
		court.addRedEgg(50, 60);
		court.addYellowEgg(60, 50);
		for (int i = 0; i < 10; i++) {
			court.tick();
			snake.move();
		}
		assertTrue("game still going", court.isPlaying()); 
		assertEquals("snake gets yellow egg", court.getScore(), 10); 
	}
	
	@Test
	public void snakeGetsTwoEggs() {
		court.initState();
		court.addBlueEgg(60, 50);
		for (int i = 0; i < 6; i++) {
			court.tick();
			snake.move();
		}
		court.addBlueEgg(150, 50);
		for (int i = 0; i < 10; i++) {
			court.tick();
			snake.move();
		}
		assertTrue("game still going", court.isPlaying()); 
		assertEquals("snake gets both eggs", court.getScore(), 20); 
	}
	
}
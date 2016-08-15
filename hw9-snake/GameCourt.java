import java.awt.*;


import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import java.util.LinkedList;


@SuppressWarnings("serial")
public class GameCourt extends JPanel {


	private boolean playing = false; 
	private static final int COURT_WIDTH = 300;
	private static final int COURT_HEIGHT = 300;
	private static final int INTERVAL = 35;

	private Creature snake; 
	private final int size = 10; 
	private LinkedList<RedEgg> allRedEggs = new LinkedList<RedEgg>(); 
	private GameObj currentFood; 
	private boolean isYellow; 

	public int score;
	private JLabel scoreLabel; 

	public GameCourt(JLabel scoreLabel) {
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBackground(Color.BLACK); 

		Timer timer = new Timer(INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		timer.start(); 
		setFocusable(true);

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					snake.direction = Direction.LEFT; 
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					snake.direction = Direction.RIGHT; 
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					snake.direction = Direction.DOWN; 
				else if (e.getKeyCode() == KeyEvent.VK_UP)
					snake.direction = Direction.UP; 
			}
		});

		this.scoreLabel = scoreLabel; 
	}

	public void initState() {
		snake = new Creature(); 
		score = 0; 
		newYellowEgg(); 
		allRedEggs.clear();

		playing = true; 
		scoreLabel.setText("Score: " + getScore());

		requestFocusInWindow();
	}

	void tick() {
		if (playing) {

			snake.move();

			// check if food has been eaten
			if (snake.isIntersecting(currentFood)) {
				if (isYellow) {
					score += 10; 
					scoreLabel.setBounds(20, 20, 50, 50);
					scoreLabel.setText("Score: " + score);
				} else {
					score += 20; 
					scoreLabel.setBounds(20, 20, 50, 50);
					scoreLabel.setText("Score: " + score);
				}

				newRedEgg();

				double r = Math.random(); 
				if (r < 0.3) {
					newBlueEgg();
				} else {
					newYellowEgg(); 
				}
				snake.add(); 

		    //check if snake is at wall
			} else if (snake.isAtWall()) {
				playing = false;
				scoreLabel.setText("Score: " + score + "        GAME OVER");
				try {
					compareHighScores();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}  

			//checks if snake collides with self
			} else if (snake.isCollideWithSelf()) {
				playing = false; 
				scoreLabel.setText("Score: " + score + "        GAME OVER");
				try {
					compareHighScores();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
			
			//check if snake eats red egg
			else {
				for (RedEgg i : allRedEggs) {
					if (snake.isIntersecting(i)) {
						playing = false; 
						scoreLabel.setText("Score: " + score + "        GAME OVER");
						try {
							compareHighScores();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} 
					}
				}
			}

			// update the display
			repaint();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		snake.draw(g);
		currentFood.draw(g);
		for (RedEgg i : allRedEggs) {
			i.draw(g);
		}
	}

	public void newBlueEgg() {
		int r = (int) (Math.random() * 29);
		int foodx = ((r * size));

		r = (int) (Math.random() * 29);
		int foody = ((r * size));

		for (GameObj i : allRedEggs) {
			if ((foodx == i.pos_x) && (foody == i.pos_y)) {
				newBlueEgg(); 
			}
		}

		isYellow = false; 
		currentFood = new BlueEgg(foodx, foody, size, 300, 300); 
	}

	public void newYellowEgg() {
		int r = (int) (Math.random() * 29);
		int foodx = ((r * size));

		r = (int) (Math.random() * 29);
		int foody = ((r * size));

		for (GameObj i : allRedEggs) {
			if ((foodx == i.pos_x) && (foody == i.pos_y)) {
				newYellowEgg(); 
			}
		}

		isYellow = true; 
		currentFood = new YellowEgg(foodx, foody, size, 300, 300); 
	}

	public void newRedEgg() {
		int r = (int) (Math.random() * 29);
		int foodx = ((r * size));

		r = (int) (Math.random() * 29);
		int foody = ((r * size));

		if ((foodx == currentFood.pos_x) && (foody == currentFood.pos_y)) {
			newRedEgg(); 
		}

		RedEgg newRedEgg = new RedEgg(foodx, foody, size, 300, 300); 
		allRedEggs.add(newRedEgg); 
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(COURT_WIDTH, COURT_HEIGHT);
	}

	public int getScore() {
		return score; 
	}

	public void compareHighScores() throws FileNotFoundException, IOException {
		FileReader readFile = new FileReader("highscores.txt"); 
		BufferedReader reader = new BufferedReader(readFile);
		try {
			while (reader.ready()) {
				int recordedScore = Integer.parseInt(reader.readLine()); 
				if (getScore() > recordedScore) {
					String topScore = Integer.toString(getScore()); 
					String secondScore = Integer.toString(recordedScore); 
					String thirdScore = reader.readLine(); 
					String [] writeMe = {topScore, secondScore, thirdScore}; 
					writeHighScore(writeMe); 
					scoreLabel.setText("You set the top score of " + getScore() + "!!!");
					break;


				} else if (getScore() <= recordedScore) {
					int score2 = Integer.parseInt(reader.readLine()); 
					int score3 = Integer.parseInt(reader.readLine()); 

					if (recordedScore == getScore() || 
							getScore() == score2 || getScore() == score3) {
						scoreLabel.setText("You tied a high score with a score of " 
							+ getScore() + "!");
						break; 

					} else if (getScore() > score2) {
						String thirdScore = Integer.toString(score2);
						String secondScore = Integer.toString(getScore()); 
						String firstScore = Integer.toString(recordedScore); 
						String [] writeMe = {firstScore, secondScore, thirdScore}; 
						writeHighScore(writeMe);
						scoreLabel.setText("You set the second high score of " 
						+ getScore() + "!!");
						break;
					} else if (getScore() > score3) {
						String firstScore = Integer.toString(recordedScore); 
						String secondScore = Integer.toString(score2);
						String thirdScore = Integer.toString(getScore()); 
						String [] writeMe = {firstScore, secondScore, thirdScore};
						writeHighScore(writeMe);
						scoreLabel.setText("You set the third high score of " 
						+ getScore() + "!");
						break;
					} 
				}
			}

		} catch (IOException e) {
		} finally {
			reader.close();
		}
	}

	//displays top 3 high score values in dialogue box
	public String displayHighScores() throws IOException {
		String returnMe = ""; 
		FileReader read = new FileReader("highscores.txt");
		BufferedReader br = new BufferedReader(read); 
		try {
			for (int i = 0; i < 3; i++) {
				returnMe += br.readLine() + "\n"; 
			}
			return returnMe; 
		} catch (IOException e) {	
		} finally {
			br.close();
			read.close();
		}
		return returnMe; 
	}
	
	//writes out a high score if needed
	public void writeHighScore(String [] writeMe) throws IOException {
		BufferedWriter output = new BufferedWriter(new FileWriter("highscores.txt")); 
		try {
			for (int i = 0; i < writeMe.length; i++) {
				output.write(writeMe[i]);
				output.newLine();
				output.flush();
			}
		} catch (IOException e) {
			System.out.println("IO Exception caught"); 
		} finally {
			output.close();
		}
	}

	
	//methods for testing
	public void addYellowEgg(int posx, int posy) {
		YellowEgg newEgg = new YellowEgg(posx, posy, size, COURT_WIDTH, COURT_HEIGHT); 
		currentFood = newEgg; 
	}

	public void addBlueEgg(int posx, int posy) {
		BlueEgg newEgg = new BlueEgg(posx, posy, size, COURT_WIDTH, COURT_HEIGHT); 
		currentFood = newEgg; 
	}

	public void addRedEgg(int posx, int posy) {
		allRedEggs.add(new RedEgg(posx, posy, size, COURT_WIDTH, COURT_HEIGHT)); 
	}

	public boolean isPlaying() { return playing; }

	public LinkedList<RedEgg> getallRedEggs() { return this.allRedEggs; }
	
	public Creature getSnake() { return this.snake; } 

}

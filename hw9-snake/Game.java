import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;

public class Game implements Runnable {
	public void run() {
		final JFrame frame = new JFrame("Snake");
		frame.setLocation(300, 300);

		//Score panel
		final JPanel scorePanel = new JPanel(); 
		frame.add(scorePanel, BorderLayout.SOUTH); 
		JLabel score = new JLabel("Score: "); 
		scorePanel.add(score);
		score.setBounds(20, 20, 50, 50);

		final JPanel sidePanel = new JPanel(); 
		frame.add(sidePanel, BorderLayout.EAST); 
		sidePanel.setLayout(new GridLayout(2, 1));

		// Main playing area
		final GameCourt court = new GameCourt(score);
		frame.add(court, BorderLayout.CENTER);
		frame.setResizable(false);

		// Reset button
		final JPanel control_panel = new JPanel();
		frame.add(control_panel, BorderLayout.NORTH);

		final JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				court.initState();
			}
		});
		control_panel.add(reset);

		//Add instructions button
		JButton instructions = new JButton("Instructions"); 
		instructions.addActionListener(new ActionListener() {
			String returnMe = "Navigate with up/down/left/right buttons.\n"
					+ "Eat the yellow eggs (10pts) and blue eggs (20pts).\nAvoid "
					+ "the red eggs. \n"
					+ "Don't run into the walls or yourself. \n"
					+ "\nHave fun!!"; 
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(frame, returnMe);
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				}
			}
		}); 
		sidePanel.add(instructions); 

		//Add scores button
		JButton scores = new JButton("Scores"); 
		scores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(frame, court.displayHighScores());
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}); 
		sidePanel.add(scores); 

		// Put the frame on the screen
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// Start game
		court.initState();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}
}

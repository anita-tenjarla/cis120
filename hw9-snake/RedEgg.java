import java.awt.Color;
import java.awt.Graphics;

public class RedEgg extends GameObj {

	public RedEgg(int pos_x, int pos_y, int size, int court_width, int court_height) {
		super(pos_x, pos_y, size, court_width, court_height);
	}
	
	@Override
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(pos_x, pos_y, size, size);
	}

}

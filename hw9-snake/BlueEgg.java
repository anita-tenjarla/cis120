import java.awt.Color;
import java.awt.Graphics;

public class BlueEgg extends GameObj{

	public BlueEgg(int pos_x, int pos_y, int size, int court_width, int court_height) {
		super(pos_x, pos_y, size, court_width, court_height);
	}
	
	@Override
	public void draw(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(pos_x, pos_y, size, size);
	}
}

import java.awt.Graphics;

public class GameObj {
	public int pos_x; 
	public int pos_y;
    public int size; 
	/** Velocity: number of pixels to move every time move() is called */

	/** Upper bounds of the area in which the object can be positioned.  
	 *    Maximum permissible x, y positions for the upper-left 
	 *    hand corner of the object
	 */
	public int max_x;
	public int max_y;
	/**
	 * Constructor
	 */
	public GameObj(int pos_x, int pos_y, 
		int size, int court_width, int court_height){
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.size = size; 
		
		this.max_x = court_width - size;
		this.max_y = court_height - size;

	}
	
	public void clip(){
		if (pos_x < 0) pos_x = 0;
		else if (pos_x > max_x) pos_x = max_x;

		if (pos_y < 0) pos_y = 0;
		else if (pos_y > max_y) pos_y = max_y;
	}
	
	public boolean intersects(GameObj obj){
		return (pos_x == obj.pos_x && pos_y == obj.pos_y); 
	}
	
	
	public int getXPos() {
		return pos_x; 
	}
	
	public int getYPos() {
		return pos_y; 
	}
	
	//will be overrode 
	public void draw(Graphics g) {
	}

}

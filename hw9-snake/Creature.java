import java.awt.Graphics;
import java.util.LinkedList;

public class Creature {
	
	public LinkedList<Body> body = new LinkedList<Body>();  
	public final int size = 10; 
	public final int courtWidth = 300; 
	public final int courtHeight = 300; 
	
	public Direction direction; 
	
	public Creature() {
		for (int i = 0; i < 2; i++) {
			body.add(new Body(50 - (size * i), 50, size, courtWidth, courtHeight));
        }
		direction = Direction.RIGHT; 
	}
	
	public void move() {

        for (int i = body.size() - 1; i > 0; i--) {
        	body.get(i).pos_x = body.get(i-1).pos_x;
        	body.get(i).pos_y = body.get(i-1).pos_y;
        }

        if (direction == Direction.LEFT) {
        	body.get(0).pos_x -= size;
        }

        if (direction == Direction.RIGHT) {
        	body.get(0).pos_x += size;
        }

        if (direction == Direction.UP) {
        	body.get(0).pos_y -= size;
        }

        if (direction == Direction.DOWN) {
        	body.get(0).pos_y += size;;
        }
    }
	
	public void draw(Graphics g) {
		for (int i = 0; i < body.size(); i++) {
			body.get(i).draw(g);
	    }
	}
    
	public boolean isIntersecting(GameObj obj) {
		return body.get(0).intersects(obj); 
	}
	
	public int getSize() {
		return body.size(); 
	}
	
	public void add() {
		body.add(new Body(body.get(body.size()-1).pos_x - size, 
				body.get(body.size()-1).pos_y, size, 300, 300)); 
	}
	
	public boolean isAtWall() {
		return (body.getFirst().pos_x <= 0 || body.getFirst().pos_x == (courtWidth - size)
				|| body.getFirst().pos_y <= 0 || body.getFirst().pos_y == (courtHeight - size)); 
	}
	
	public boolean isCollideWithSelf() {
		for (int i = 2; i < body.size(); i++) {
		   if ((body.getFirst().pos_x == body.get(i).pos_x) && 
				   (body.getFirst().pos_y == body.get(i).pos_y)) { return true; }
	    }
		return false; 
	}
	
	public int getHeadX() { return body.getFirst().pos_x; } 
	public int getHeadY() { return body.getFirst().pos_y; }
	
}

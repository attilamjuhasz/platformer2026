package platformer.code.gamelogic.tiles;

import java.awt.image.BufferedImage;

import platformer.code.gameengine.hitbox.RectHitbox;
import platformer.code.gamelogic.level.Level;

public class Luckyblock extends SolidTile{
	
	public Luckyblock(float x, float y, int size, BufferedImage image, Level level) {
		super(x, y, size, image, level);
		int offset =(int)(level.getLevelData().getTileSize()*0.1); //hitbox is offset by 10% of the tile size
		this.hitbox = new RectHitbox(x *size, y*size, 0, offset, size, size);
	}

	//precondition:
	//postcondition:
	public String toString() {
		return "I'm a luckyblock at "+this.position.x+" "+this.position.y;
	}

	//precondition: Must provide an image
	//postcondition: The image variable will be defined as the provided new image
	public void setImage(BufferedImage image){
		this.image = image;
	}
}

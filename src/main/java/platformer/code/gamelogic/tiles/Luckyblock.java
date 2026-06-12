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
	public String toString() {
		return "I'm a luckyblock at "+this.position.x+" "+this.position.y;
	}
}

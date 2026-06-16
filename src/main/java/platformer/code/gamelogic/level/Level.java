package platformer.code.gamelogic.level;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import platformer.code.gameengine.PhysicsObject;
import platformer.code.gameengine.graphics.Camera;
import platformer.code.gameengine.loaders.Mapdata;
import platformer.code.gameengine.loaders.Tileset;
import platformer.code.gamelogic.GameResources;
import platformer.code.gamelogic.Main;
import platformer.code.gamelogic.enemies.Enemy;
import platformer.code.gamelogic.player.Player;
import platformer.code.gamelogic.tiledMap.Map;
import platformer.code.gamelogic.tiles.Flag;
import platformer.code.gamelogic.tiles.Flower;
import platformer.code.gamelogic.tiles.Gas;
import platformer.code.gamelogic.tiles.Luckyblock;
import platformer.code.gamelogic.tiles.SolidTile;
import platformer.code.gamelogic.tiles.Spikes;
import platformer.code.gamelogic.tiles.Tile;
import platformer.code.gamelogic.tiles.Water;

public class Level {

	private LevelData leveldata;
	private Map map;
	public static Player player;
	private Camera camera;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;

	private ArrayList<Enemy> enemiesList = new ArrayList<>();
	private ArrayList<Flower> flowers = new ArrayList<>();
	private ArrayList<Enemy> waterMonsters = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();
	private List<Water> waters = new ArrayList<>();
	private List<Gas> gas = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;

	//I added for gas
	private long gastimer = 0;
	private long timeAmount = 5;
	
	//I added for water
	private int wMCounter = 0;
	private boolean wasInWater = false;
	

	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();
	}

	public LevelData getLevelData() {
		return leveldata;
	}

	public void restartLevel() {

		enemiesList.clear();

		//I added for water
		waters.clear();

		//I added for gas
		gas.clear();

		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];

		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0)
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this); // Air
				else if (values[x][y] == 1)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				else if (values[x][y] == 2)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				else if (values[x][y] == 3)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				else if (values[x][y] == 4)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				else if (values[x][y] == 5)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				else if (values[x][y] == 6)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this);
				else if (values[x][y] == 7)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				else if (values[x][y] == 8)
					enemiesList.add(new Enemy(xPosition * tileSize, yPosition * tileSize, this)); // TODO: objects vs
																									// tiles
				else if (values[x][y] == 9)
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				else if (values[x][y] == 10) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 11) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower2"), this, 2);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 12)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				else if (values[x][y] == 13)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				else if (values[x][y] == 14)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
				else if (values[x][y] == 15)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasOne"), this, 1);
				else if (values[x][y] == 16)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasTwo"), this, 2);
				else if (values[x][y] == 17)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasThree"), this, 3);
				else if (values[x][y] == 18)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Falling_water"), this, 0);
				else if (values[x][y] == 19)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Full_water"), this, 3);
				else if (values[x][y] == 20)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Half_water"), this, 2);
				else if (values[x][y] == 21)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Quarter_water"), this, 1);
				else if (values[x][y] == 23)
					tiles[x][y] = new Luckyblock(xPosition, yPosition, tileSize, tileset.getImage("Luckyblock"), this);
			}

		}
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
		player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
				this);
		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;
	}

	public void onPlayerDeath() {
		active = false;
		playerDead = true;

		//I added for water
		if (waterMonsters != null && !waterMonsters.isEmpty()) {
			for (int i = 0; i < enemiesList.size(); i++){
				for (int k = 0; k < waterMonsters.size(); k++){
					if (waterMonsters.get(k).equals(enemiesList.get(i))){
						enemiesList.remove(i);
						waterMonsters.remove(k);
						i--;
						break;
					}
				}
			}
		}

		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	//I added for custom
	//precondition: The player must touch a luckyblock on the map
	//postcondition: The player will gain a jump boost and the lucky block will turn into a used icon
	public void luckyBlockTouch(Tile lucky){
		player.jumpPower = 2200;
		tileset = GameResources.tileset;
		lucky.setImage(tileset.getImage("Used_Luckyblock"));
	}

	public void update(float tslf) {
		if (active) {
			// Update the player
			player.update(tslf);

			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();

			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					if (flowers.get(i).getType() == 1)
						water(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 3);
					else
						addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new
					ArrayList<Gas>());
					flowers.remove(i);
					i--;
				}
			}

			// for (int i = 0; i < waters.size(); i++) {
			// 	if (waters.get(i).getHitbox().isIntersecting(player.getHitbox()) && wMCounter < 1) {
			// 		Enemy waterMonster = new Enemy(player.getX(), player.getY()-140, this);
			// 		enemiesList.add(waterMonster);
			// 		wMCounter++;
			// 	}
			// }

			//I added for custom
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Luckyblock){
				luckyBlockTouch(player.getCollisionMatrix()[PhysicsObject.TOP]);
			}

			//I added for gas
			boolean touchingGas = false;
            for (int i = 0; i < gas.size(); i++) {
                if (gas.get(i).getHitbox().isIntersecting(player.getHitbox())) {
                    touchingGas = true;
                    if(gastimer == 0){
                        gastimer = System.currentTimeMillis();
                    }
                    else{
                        if((System.currentTimeMillis() - gastimer) / 1000 >= timeAmount){
                            gastimer = 0;
							if (touchingGas){
								onPlayerDeath();
							}
                        }
                    }
                }
            }

			//I added for water
			boolean currInWater = false;
			for (int i = 0; i < waters.size(); i++){
				if (waters.get(i).getHitbox().isIntersecting(player.getHitbox())){
					currInWater = true;
					break;
				}
			}
			if (currInWater && !wasInWater){
				for (int i = 0; i < waters.size(); i++){
					if (waters.get(i).getHitbox().isIntersecting(player.getHitbox())){
						Enemy waterMonster = new Enemy(player.getX() - 100, player.getY() - 140, this);
						enemiesList.add(waterMonster);
						waterMonsters.add(waterMonster);
						break;
					}
				}
				wasInWater = true;
			}
			else if (!currInWater){
				wasInWater = false;
			}


			// Update the enemies
			for (int i = 0; i < enemiesList.size(); i++) { 
				enemiesList.get(i).update(tslf);
				if (player.getHitbox().isIntersecting(enemiesList.get(i).getHitbox())) {
					onPlayerDeath();
				}
			}

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}
	}

	// #############################################################################################################
	// Your code goes here!
	// Please make sure you read the rubric/directions carefully and implement the
	// solution recursively!

	// precondition: The player or water has to touch the a ordinary flower
	// postcondition: It will create a water block of a specific type, depending on
	// previous water blocks or whether it is falling.
	private void water(int col, int row, Map map, int fullness) {
		int nextFullness = fullness;
		if (fullness > 1) {
			nextFullness = fullness - 1;
		}
		if (row + 1 < map.getTiles()[col].length && map.getTiles()[col][row + 1] != null
				&& !map.getTiles()[col][row + 1].isSolid() && !(map.getTiles()[col][row + 1] instanceof Water)) {
			Water w = new Water(col, row + 1, tileSize, tileset.getImage("Falling_water"), this, 0);
			waters.add(w);
			map.addTile(col, row + 1, w);
			if (!(map.getTiles()[col][row] instanceof Water)) {
				if (fullness == 3) {
					Water w2 = new Water(col, row, tileSize, tileset.getImage("Full_water"), this, 3);
					waters.add(w2);
					map.addTile(col, row, w2);
				} else if (fullness == 2) {
					Water w2 = new Water(col, row, tileSize, tileset.getImage("Half_water"), this, 2);
					waters.add(w2);
					map.addTile(col, row, w2);
				} else if (fullness == 1) {
					Water w2 = new Water(col, row, tileSize, tileset.getImage("Quarter_water"), this, 1);
					waters.add(w2);
					map.addTile(col, row, w2);
				}
			} else {
				Water w2 = new Water(col, row, tileSize, tileset.getImage("Falling_water"), this, 0);
				waters.add(w2);
				map.addTile(col, row, w2);
			}
			water(col, row + 1, map, fullness);
			return;
		}

		if (fullness == 3) {
			Water w = new Water(col, row, tileSize, tileset.getImage("Full_water"), this, 3);
			waters.add(w);
			map.addTile(col, row, w);
		} else if (fullness == 2) {
			Water w = new Water(col, row, tileSize, tileset.getImage("Half_water"), this, 2);
			waters.add(w);
			map.addTile(col, row, w);
		} else if (fullness == 1) {
			Water w = new Water(col, row, tileSize, tileset.getImage("Quarter_water"), this, 1);
			waters.add(w);
			map.addTile(col, row, w);
		} else {
			Water w = new Water(col, row, tileSize, tileset.getImage("Falling_water"), this, 0);
			waters.add(w);
			map.addTile(col, row, w);
		}
		if (col - 1 >= 0 && map.getTiles()[col - 1][row] != null && !map.getTiles()[col - 1][row].isSolid()
				&& !(map.getTiles()[col - 1][row] instanceof Water)) {
			water(col - 1, row, map, nextFullness);
		}

		if (col + 1 < map.getTiles().length && map.getTiles()[col + 1][row] != null
				&& !map.getTiles()[col + 1][row].isSolid() && !(map.getTiles()[col + 1][row] instanceof Water)) {
			water(col + 1, row, map, nextFullness);
		}
		

	}

	public void draw(Graphics g) {
		g.translate((int) -camera.getX(), (int) -camera.getY());
		// Draw the map
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				Tile tile = map.getTiles()[x][y];
				if (tile == null)
					continue;
				if (tile instanceof Gas) {

					int adjacencyCount = 0;
					for (int i = -1; i < 2; i++) {
						for (int j = -1; j < 2; j++) {
							if (j != 0 || i != 0) {
								if ((x + i) >= 0 && (x + i) < map.getTiles().length && (y + j) >= 0
										&& (y + j) < map.getTiles()[x].length) {
									if (map.getTiles()[x + i][y + j] instanceof Gas) {
										adjacencyCount++;
									}
								}
							}
						}
					}
					if (adjacencyCount == 8) {
						((Gas) (tile)).setIntensity(2);
						tile.setImage(tileset.getImage("GasThree"));
					} else if (adjacencyCount > 5) {
						((Gas) (tile)).setIntensity(1);
						tile.setImage(tileset.getImage("GasTwo"));
					} else {
						((Gas) (tile)).setIntensity(0);
						tile.setImage(tileset.getImage("GasOne"));
					}
				}
				if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
					tile.draw(g);
			}
		}

		//I added for gas
		g.setColor(Color.RED);
		g.setFont(new Font("Arial", Font.BOLD, 40));
		g.drawString((System.currentTimeMillis() - gastimer)/1000 + "", (int)(player.getX()), (int)(player.getY()-20));

		// Draw the enemies
		for (int i = 0; i < enemiesList.size(); i++) {
			enemiesList.get(i).draw(g);
		}

		// Draw the player
		player.draw(g);

		// used for debugging
		if (Camera.SHOW_CAMERA)
			camera.draw(g);
		g.translate((int) +camera.getX(), (int) +camera.getY());
	}

	// --------------------------Die-Listener
	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}

	//precondition: The player has just touched a bent flower
	//postcondition: Creates the amount of tiles called to create a gas cloud in the map
	private void addGas(int col, int row, Map map, int numSquaresToFill, ArrayList<Gas> placedThisRound) {
		Gas g = new Gas(col, row, tileSize, tileset.getImage("GasOne"), this, 3);
		map.addTile(col, row, g);
		numSquaresToFill--;
		int[][] leLocation = {
			{-1, 0},
			{-1, 1},
			{-1, -1},
			{0, 1},
			{0, -1},
			{1, 0},
			{1, 1},
			{1, -1}
		};

		List<Integer> orderRow = new ArrayList<>();
		List<Integer> orderCol = new ArrayList<>();

		for (int i = 0; i < leLocation.length; i++){
			if (row + leLocation[i][0] >= 0 && row + leLocation[i][0] < map.getTiles()[0].length && col >= 0 && col < map.getTiles().length && map.getTiles()[col + leLocation[i][1]][row + leLocation[i][0]] != null && !(map.getTiles()[col + leLocation[i][1]][row + leLocation[i][0]] instanceof Gas) && !map.getTiles()[col + leLocation[i][1]][row + leLocation[i][0]].isSolid()){
				Gas leG = new Gas(col + leLocation[i][1], row + leLocation[i][0], tileSize, tileset.getImage("GasOne"), this, 3);
				gas.add(leG);
				map.addTile(col + leLocation[i][1], row + leLocation[i][0], leG);
				orderRow.add(row + leLocation[i][0]);
				orderCol.add(col + leLocation[i][1]);
				numSquaresToFill--;
			}
		}


		while (numSquaresToFill > 0){
			for (int k = 0; k < orderRow.size(); k++){
				for (int i = 0; i < leLocation.length; i++){
					if (orderRow.get(k) + leLocation[i][0] >= 0 && orderRow.get(k) + leLocation[i][0] < map.getTiles()[0].length && orderCol.get(k) >= 0 && orderCol.get(k) < map.getTiles().length && map.getTiles()[orderCol.get(k) + leLocation[i][1]][orderRow.get(k) + leLocation[i][0]] != null && !(map.getTiles()[orderCol.get(k) + leLocation[i][1]][orderRow.get(k) + leLocation[i][0]] instanceof Gas) && !map.getTiles()[orderCol.get(k) + leLocation[i][1]][orderRow.get(k) + leLocation[i][0]].isSolid()){
						System.out.println(numSquaresToFill);
						Gas leG = new Gas(orderCol.get(k) + leLocation[i][1], orderRow.get(k) + leLocation[i][0], tileSize, tileset.getImage("GasOne"), this, 3);
						gas.add(leG);
						map.addTile(orderCol.get(k) + leLocation[i][1], orderRow.get(k) + leLocation[i][0], leG);
						orderRow.add(orderRow.get(k) + leLocation[i][0]);
						orderCol.add(orderCol.get(k) + leLocation[i][1]);
						numSquaresToFill--;
						if (numSquaresToFill <= 0){
							return;
						}
					}
				}
	 		}
		}
	}	
}
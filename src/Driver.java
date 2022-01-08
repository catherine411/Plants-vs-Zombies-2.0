/* Catherine Hsu 
 * ISU - Plants vs Zombies 
 * June 14, 2021
 * Description: Plants vs Zombies is a single-player arcade-type game where the player takes the role of a homeowner in the midst of a zombie apocalypse. The aim of the 
 * player is to protect their home from a horde of zombies as they approach it along several parallel lanes. The player defends the home by putting down plants
 *  (i.e. peashooters), which in turn fire projectiles (in a straight line) at the zombies and after several shots the zombie dies and cannot advance forwards. There 
 *  will be a brief time period before the game for the player to plant the plants before zombies start approaching. The player also has to collect a currency called '
 *  “sun” throughout the game (that falls from the sky or is supplied by sunflowers - a powerup) in order to buy plants and powerups (like wall-nuts, cherry bombs etc.) 
 *  to help them with the game. If zombies make it to the house (opposite end of the screen) on any lane, the game will end and the player will have to restart it.
*/

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

@SuppressWarnings("serial") //funky warning, just suppress it. It's not gonna do anything.
public class Driver extends JPanel implements Runnable, ActionListener, MouseListener{

	static JFrame frame;

	//variables for variables that stay constant
	final int EMPTY = 0; //tracks what is inside each grid 
	final int SHOOTER = 1;
	final int BOMB = 2;
	final int WALL = 3;
	final int SUNFLOWER = 4;
	final int GRID_WIDTH = 80;
	final int GRID_HEIGHT = 90;
	final int SIDE_OFFSET = 290;
	final int BORDER_SIZE = 4;

	int [][] grid;	//grid to track rows and columns on the lawn
	int currentSideY;
	int sideRectHeight;
	int choice;

	int FPS = 60;
	int numFrames;
	Thread thread;
	int screenWidth = 1050;
	int screenHeight = 575;

	boolean gameStart = false; 
	boolean gameOver = false;

	Image bg,start, end, sidebar, shooterImg, sunflowerImg, walnutImg, bombImg, sunImg, zombie1Img, peaImg, choiceImg;
	private final Font COURIER_20 = new Font("Courier", Font.BOLD, 20);
	private final Font COURIER_30 = new Font("Courier", Font.BOLD, 40);
	Clip bgMusic, munch;

	Image offScreenImage;
	Graphics offScreenBuffer;

	Rectangle [] sideRect = new Rectangle [4];

	ArrayList <Peashooter> shooterList = new ArrayList <Peashooter>();	//more random access
	ArrayList <Walnut> walnutList = new ArrayList <Walnut>();	
	ArrayList <Sunflower> sunflowerList = new ArrayList <Sunflower>();
	Map <Integer, Zombie> zombieMap = new TreeMap <Integer, Zombie>(); //might use TreeMap

	Sun skySun = new Sun(false, 0, 0, 0, null);

	//most of these are used for tracking
	boolean sideRectDraw, empty, zombietf, shootertf, skySuntf, sunflowertf, startGame;
	int delayMs, zombieKey, totalSun; 
	int countZombieColumn;	//to track zombie is being animated
	int shooterIndex, walnutIndex;
	int track, trackLife; //to track number of times CollsionIndex is called
	int chosenCost;
	int trackBegin;
	int zombieSpeed, endIndex;
	boolean collidePeaTrack;

	public Driver() {
		
		//audio files (sound effects and background music)
		try {
			AudioInputStream sound = AudioSystem.getAudioInputStream(new File ("Theme.wav"));
			bgMusic= AudioSystem.getClip();
			bgMusic.open(sound);
		}
		catch(Exception e) {}
		
		//sets up JPanel
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setVisible(true);		

		grid = new int [6][11]; //a 6 by 11 grid that keeps track of items in each individual section of the lawn

		JMenuItem aboutOption, rulesOption;
		aboutOption = new JMenuItem ("About");
		rulesOption = new JMenuItem ("Instructions");

		JMenu gameMenu;	
		gameMenu = new JMenu("Game");
		gameMenu.add (rulesOption);
		gameMenu.add (aboutOption);
		
		JMenuBar mainMenu = new JMenuBar ();
		mainMenu.add (gameMenu);
		

		int sideY=0;
		for (int i = 0; i<=3; i++) {
			sideRect[i] = new Rectangle (0, sideY, 115, 115);
			sideY+=115;
		}

		// Use a media tracker to make sure all of the images are
		// loaded before we continue with the program
		MediaTracker tracker = new MediaTracker(this);
		shooterImg = Toolkit.getDefaultToolkit().getImage("Peashooter.png");
		tracker.addImage(shooterImg, 0);
		sunflowerImg = Toolkit.getDefaultToolkit().getImage("Sunflower.png");
		tracker.addImage(sunflowerImg, 1);
		walnutImg = Toolkit.getDefaultToolkit().getImage("Walnut.png");
		tracker.addImage(walnutImg, 2);
		bombImg = Toolkit.getDefaultToolkit().getImage("Bomb.png");
		tracker.addImage(bombImg, 3);
		sunImg = Toolkit.getDefaultToolkit().getImage("Sun.png");
		tracker.addImage(sunImg, 4);
		zombie1Img = Toolkit.getDefaultToolkit().getImage("Zombie1.png");
		tracker.addImage(zombie1Img, 5);
		peaImg = Toolkit.getDefaultToolkit().getImage("Pea.png");
		tracker.addImage(peaImg, 6);

		// Set the menu bar for this frame to mainMenu
		frame.setJMenuBar (mainMenu);

		newGame();
		
		aboutOption.setActionCommand("About");
		aboutOption.addActionListener(this);
		rulesOption.setActionCommand("Rules");
		rulesOption.addActionListener(this);
		
		//starting the thread
		thread = new Thread(this);
		thread.start();
	}

	// 	Description: To handle normal menu items
	//	Parameters: event - the event name that indicates which menu item has been selected
	//	Returns: Void
	public void actionPerformed(ActionEvent event) {
		String eventName = event.getActionCommand();
		if (eventName.equals("About")) {
			JOptionPane.showMessageDialog (frame, (Object) "By: Catherine Hsu \nPlants vs Zombies: Version 1.0", "About", JOptionPane.INFORMATION_MESSAGE);			
		}
		else if(eventName.equals("Rules")) {
			JOptionPane.showMessageDialog (frame, (Object) "Instructions\n1. For the first few seconds of the game, wait for Suns to fall from the sky and click on them to collect\n\n"
					+ "2. Once collected, you can use the suns as a currency to buy powerups (listed on the sidebar)\n\n"
					+ "3. Buy powerups by clicking the item you want on the sidebar, then clicking the grid you want to place it on\n\n"
					+ "4. Peashooters shoot projectiles at zombies and after 6 hits, the zombie would die, Walnut - shields zombies from going further for approx 15 seconds, \nSunflowers generates additional sun, CherryBomb clears all zombies in the row it is placed in\n\n"
					+ "5. Zombies will advance towards the opposite end of the screen, and zombies will be generated at an increasing rate \n\n"
					+ "6. The game ends when a zombie reaches the opposite end it came from", "Rules", JOptionPane.PLAIN_MESSAGE);			
		}
	}

	@Override
	// Description: Prompts the initialize method as well as paint component and update methods every FPS
	// Parameters: None
	// Returns: Void
	public void run() {
		initialize();
		while(true) {
			//main game loop
			update();	//call update method so frame can be repained every second
			this.repaint();

			try {
				Thread.sleep(1000/FPS);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	// Description: Sets up the defaults of the game when the program first runs
	// Parameters: None
	// Returns: Void
	public void initialize() {
		//setups before the game starts running
		sideRectDraw=false;
		try {
			start = ImageIO.read(new File("Start.png"));
			start.getScaledInstance(screenWidth, screenHeight, Image.SCALE_DEFAULT);
			end = ImageIO.read(new File("End.png"));
			end.getScaledInstance(screenWidth, screenHeight, Image.SCALE_DEFAULT);
			bg = ImageIO.read(new File("PZBackground.png"));
			bg.getScaledInstance(screenWidth-50, screenHeight, Image.SCALE_DEFAULT);
			sidebar = ImageIO.read(new File("sidebar.png"));
			sidebar.getScaledInstance(100, screenHeight, Image.SCALE_DEFAULT);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	// Description: Resets the default settings after every new game 
		// Parameters: none
		// Returns: none
	public void newGame() {
		bgMusic.start();
		bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
		clearGrid();
		shooterList = new ArrayList <Peashooter>();	
		walnutList = new ArrayList <Walnut>();	
		sunflowerList = new ArrayList <Sunflower>();
		zombieMap = new TreeMap <Integer, Zombie>();
		gameOver = false;
		sideRectDraw = false;  
		zombietf = false;
		zombieKey = 0;
		delayMs = 0;
		shootertf=false;
		numFrames = 1;
		skySuntf = true;
		shooterIndex = 0;
		trackLife=1;
		track = 0;
		totalSun = 0;
		endIndex = -1;
		countZombieColumn = 0;
		zombieSpeed = 1000;
		trackBegin= 0;
		skySun.setYPos(screenHeight);
		repaint();
	}

	// Description: controls what happens when mouse is clicked 
	// Parameters: x=xPosition where screen is clicked, y=y posiotion where screen is clicked
	// Returns: void
	public void handleAction(int x, int y) {
		if (x>= 0&&x<=screenWidth && endIndex==-1) {	//clicking start screen
			gameStart = true;
		}
		if (endIndex == 0 && x>= 0&&x<=screenWidth ) {	//restarting from end screen
			gameStart = true;
			gameOver = false;
			newGame();
		}
		if (y>=0 && y<=475 && x>=0 && x<=120 && sideRectDraw == false) { //choosing from side bar
			if (y>=0 && y<125 && totalSun >=100) {	//choosing peashooter 
				chosenCost = 100;
				currentSideY=0;
				sideRectHeight=125;
				choice = 1;
				choiceImg = shooterImg;
				sideRectDraw=true;
			}
			else if (y>=125 && y<250 && totalSun >=50) { //choosing sunflower
				chosenCost = 50;
				currentSideY=130;
				sideRectHeight=120;
				choice=4;
				choiceImg = sunflowerImg;
				sideRectDraw=true;
			}
			else if (y>=250 && y<375 && totalSun >=50) { //choosing walnut
				chosenCost = 50;
				currentSideY=255;
				sideRectHeight=125;
				choice=3;
				choiceImg = walnutImg;
				sideRectDraw=true;
			}
			else if (y>=375 && y<=475 && totalSun >=150){ //choosing cherry bomb
				chosenCost = 150;
				currentSideY=385;
				sideRectHeight=105;
				choice=2;
				choiceImg = bombImg;
				sideRectDraw=true;
			}
			repaint();	
		}
		if (sideRectDraw && x > SIDE_OFFSET && x< SIDE_OFFSET+GRID_WIDTH*10 && y>GRID_HEIGHT&& y<GRID_HEIGHT*7) {
			//check if square is empty when clicked on
			int row, column;
			column = (x-SIDE_OFFSET)/GRID_WIDTH+1;
			row = (y-GRID_HEIGHT)/GRID_HEIGHT+1;
			if (row >=0 && row < grid.length && column >= 0 && column < grid[0].length-1)
				empty = isEmpty(row, column);
			if (empty) {
				if (choice == 2) {
					bomb(row);
				}
				repaint();
				zombietf = true; //zombie will start approaching once a powerup has been placed on grid or when approx 5 suns are collected
			}
		}

		if (skySuntf && skySun.getCollected()==false) {	//checks if sun from the sky is collected
			if (x > skySun.getxPos() && x < skySun.getxPos() + 40) {
				totalSun+=25;
				skySun.setCollected(true);
				skySun.setYPos(screenHeight);
			}
		}
		if (sunflowertf) { //checks if sun from each sunflower is collected
			for (int i = 0; i < sunflowerList.size(); i++) {
				if (sunflowerList.get(i).getFlowerSun().getCollected()==false) {
					if (x > sunflowerList.get(i).getFlowerSun().getxPos() && x< sunflowerList.get(i).getFlowerSun().getxPos() + 40 && y>sunflowerList.get(i).getFlowerSun().getyPos() && y<sunflowerList.get(i).getFlowerSun().getyPos()+40) {
						sunflowerList.get(i).getFlowerSun().setCollected(true);
						totalSun+=25;
					}
				}
			}
		}
	}
		
	
	/* Description: Deletes all zombies in the specified row (i.e. the row where the bomb is placed)
	 * Parameters: r = row the bomb is placed on the grid
	 * Returns:	void
	 */
	public void bomb(int r) {
		for (int i = 1; i <= zombieMap.size(); i++) {
			if (zombieMap.get(i).getRemoved() == false) {
				if (zombieMap.get(i).getRow() == r) {
					zombieMap.get(i).setRemoved(true);
				}
			}
		}
	}
	// Description: Updates the position of images, rectangles, background, and updates interactions (ie collisions) every frame
	// Parameters: none
	// Returns: void
	public void update() {
		//update per frame
		if(endIndex == -1) {
			numFrames++;
			if (shootertf) { //updates the positions of the peas of each peashooter
				for (int i = 0; i < shooterList.size(); i++) {
					int currentPeaX = shooterList.get(i).getShooterPea().getxPos();
					currentPeaX+=5;
					shooterList.get(i).getShooterPea().setXPos(currentPeaX);
					shooterList.get(i).getShooterPea().setPeaRect(new Rectangle(currentPeaX, shooterList.get(i).getShooterPea().getyPos(), 20, 20));
					//update pea position
					if ((currentPeaX >1050 || collidePeaTrack) && numFrames%200==0) {
						shooterList.get(i).getShooterPea().setXPos(SIDE_OFFSET + (shooterList.get(i).getColumn() - 1) * GRID_WIDTH + BORDER_SIZE +70);
					}
					peaCollisions(shooterList.get(i).getShooterPea());
				}
			}
			if (zombietf && numFrames%zombieSpeed == 0) {		//controls zombies movement/each zombie object
				randZombies();	//to create a zombie object
			}
			if (zombietf && numFrames%2000 == 0 && zombieSpeed >= 300) {		//rate of generating zombies increases (start with 1000 and decrease until 300)
				zombieSpeed -= 50;
			}
			if (zombietf && numFrames%20 == 0) {		//rends game when zombie hits the end
				for (int i = 1; i <= zombieMap.size(); i++) {
					if (zombieMap.get(i).getRemoved()==false &&zombieMap.get(i).getxPos() <= 250) {
						gameOver = true;
						gameStart = false;
						endIndex = 0;
					}
					if (zombieMap.get(i).getHitCount() == 5) { 	//marks zombie as removed when hit 5 times by the pea
						zombieMap.get(i).setRemoved(true);
						zombieMap.get(i).setWalnutCollide(false);
					}
					if (zombieMap.get(i).getRemoved() == false && zombieMap.get(i).getColliding() == false) {	//tracks adn updates zombies movements
						int shooterCollisionIndex;
						int currentZombieX = zombieMap.get(i).getxPos(); 
						currentZombieX-=8; 	//updates x-position and rectangle of each zombie
						countZombieColumn +=8;
						if (countZombieColumn >=80) {	
							int c = zombieMap.get(i).getColumn()-1;
							zombieMap.get(i).setColumn(c);
							countZombieColumn = 0;
						}
						zombieMap.get(i).setXPos(currentZombieX); //check collisions when looping through each zombie
						//update column
						zombieMap.get(i).setZombieRect(new Rectangle(currentZombieX, zombieMap.get(i).getyPos(), GRID_WIDTH, GRID_HEIGHT));
						int walnutCollisionIndex = walnutCollisions(zombieMap.get(i));
						if (walnutCollisionIndex >=0 ) {
							zombieMap.get(i).setCurrentCollisionIndex(walnutCollisionIndex);
						}
						shooterCollisionIndex = shooterCollisions(zombieMap.get(i), shooterList); //problem with global variable!!, returns index of object in collision with in its list (SAVE TO EACH ZOMBIE OBJECT)
						if (shooterCollisionIndex>=0) {
							zombieMap.get(i).setCurrentCollisionIndex(shooterCollisionIndex);
						}
					}
					if (zombieMap.get(i).getRemoved() == false && zombieMap.get(i).getWalnutCollide() == true) { //when a zombie collides with a walnut
						Collections.sort(walnutList, new SortByWalnutIndex());
						int index = Collections.binarySearch(walnutList, new Walnut(zombieMap.get(i).getCurrentCollisionIndex(), 0, 0, 0, null), new SortByWalnutIndex()); //sort default of walnut by index
						if (index >=0) {	
							int life = walnutList.get(index).getLife();	//not always right...
							life-=trackLife;	//pauses zombie's movements for approx 15 seconds
							walnutList.get(index).setLife(life);
							if (walnutList.get(index).getLife()==0) {
								zombieMap.get(i).setWalnutCollide(false);
								grid[walnutList.get(index).getRow()][walnutList.get(index).getColumn()] = 0;
								walnutList.remove(index);	//walnut is removed after 15 seconds
								zombieMap.get(i).setColliding(false);	
								zombieMap.get(i).setCurrentCollisionIndex(-1);
								if (trackLife < 30)
									trackLife++;
							}
						}
					}
					if (zombieMap.get(i).getRemoved() == false && zombieMap.get(i).getColliding() && zombieMap.get(i).getCurrentCollisionIndex()>=0 && zombieMap.get(i).getWalnutCollide() == false) {
						int count = zombieMap.get(i).getCollideCount();	//when a zombie collides with a peashooter
						count++;
						zombieMap.get(i).setCollideCount(count);
						if (zombieMap.get(i).getCollideCount() >= 10) {
							Collections.sort(shooterList, new SortByShooterIndex());
							//binary searchfr peashooter with that index
							int index = Collections.binarySearch(shooterList, new Peashooter(zombieMap.get(i).getCurrentCollisionIndex(), 0,0,0, null, null), new SortByShooterIndex());
							grid[shooterList.get(index).getRow()][shooterList.get(index).getColumn()] = 0; // keeps zombies from advancing for a few seconds before zombie destroys the peahsooter
							shooterList.remove(index);
							zombieMap.get(i).setCollideCount(0); 
							zombieMap.get(i).setColliding(false);
							zombieMap.get(i).setCurrentCollisionIndex(-1);
						}
					}
				}
			}
			if (sunflowertf) {		//check if sun for sunflower is collected and tracks when the sunflower's sun should be generated
				for (int i = 0; i< sunflowerList.size(); i++) {
					if (sunflowerList.get(i).getFlowerSun().getCollected()) {
						int time = sunflowerList.get(i).getTime();
						time++;
						sunflowerList.get(i).setTime(time); //tracks timing of when sunflower sun should regenerate
						if (sunflowerList.get(i).getTime() == 500) {
							sunflowerList.get(i).getFlowerSun().setCollected(false);
							sunflowerList.get(i).setTime(0);
						}
					}
				}
			}
			if (skySuntf) { //updates positions of sun falling from the sky
				int yPos = skySun.getyPos();
				if (trackBegin == 3) 
					zombietf=true;		
				yPos+=1; //gets faster by 1 
				skySun.setYPos(yPos);
				if (skySun.getyPos() >= screenHeight && numFrames%350 == 0) {
					skySun.setYPos(0);
					skySun.setCollected(false);
					int c = (int)(Math.random()*9)+1;
					skySun.setColumn(c);
					int xPos = SIDE_OFFSET + (c - 1) * GRID_WIDTH + BORDER_SIZE;
					skySun.setXPos(xPos);
					skySun.setSunRect(new Rectangle(xPos, yPos, 40, 40));
					trackBegin++;
				}
			}
			repaint();
		}
	}

	public void clearGrid () {
		for(int row = 0; row<grid.length; row++) 
			for(int column = 0; column < grid[row].length; column++) {
				grid[row][column]=0;
			}
	}

	/* Description: checks if the grid selected to place the powerup in is empty 
	 * Parameters: row = row the powerup would be placed in, column = column the powerup would be placed in
	 * Returns:	true/false - empty or not
	 */	public boolean isEmpty (int row, int column) {
		if (grid[row][column]==0 && choice !=2) {
			grid[row][column]=choice;
			totalSun-=chosenCost; //deletes sun from total from purchasing a power
			add(row, column);	//adds number to rep powerup in the grid
			sideRectDraw = false;
			chosenCost = 0;
			choice = 0;
			return true;
		}
		else if (grid[row][column]==0 && choice ==2){	//if the powerup if a bomb, also checks if the same row has zombies
			int track = 0;
			for (int i = 1; i <= zombieMap.size(); i++) {
				if (zombieMap.get(i).getRemoved() == false) {
					if (zombieMap.get(i).getRow() == row) 
						track++;
				}
			}
			if (track !=0) { 
				sideRectDraw = false;
				chosenCost = 0;
				choice = 0;
				totalSun-=chosenCost;
				return true;
			}
			else {
				return false;
			}
			//loop through list fo zombies to see if there is any in the same row, if yes continue

		}
		else return false;
	}

	/* Description: creates and adds each powerup to their respective list when placed on the grid 
	 * Parameters: row = row the powerup is placed in, column = column the powerup was placed in
	 * Returns:	void
	 */
	public void add (int row, int column) {
		
		if (choice == 1) { //if powerup is a peaShooter, creates a Peashooter object
			Pea shooterPea = new Pea (0,0,0,0, null);
			Peashooter shooter = new Peashooter (0, 0, 0, 0, new Pea(0,0, 0,0, null), new Rectangle());
			shooter.setIndex(shooterIndex);		//creates the Peashooter's Pea object
			int xPos = SIDE_OFFSET + (column - 1) * GRID_WIDTH + BORDER_SIZE +70;
			int yPos = row * GRID_HEIGHT+5;
			shooterPea.setXPos(xPos);
			shooterPea.setYPos(yPos);
			shooterPea.setRow(row);
			shooterPea.setColumn(column);
			shooterPea.setPeaRect(new Rectangle(xPos, yPos, 20, 20));
			shooter.setShooterPea(shooterPea);
			shooter.setLife(shooter.getMaxLife());
			shooter.setColumn(column);
			shooter.setRow(row);
			Rectangle rect = new Rectangle (xPos, yPos, GRID_HEIGHT, GRID_WIDTH);
			shooter.setShooterRect(rect);
			shooterList.add(shooter); //adds peashooter to its list
			sideRectDraw = false;
			empty = false;	//occupies the grid
			shootertf= true;
			shooterIndex ++;
		}
		if (choice == 2) {	//cherryBomb - not an object and does not have a list to be placed in
			for (int i = 1; i <= zombieMap.size(); i++) {
				if (zombieMap.get(i).getRemoved() == false) {
					if (zombieMap.get(i).getRow() == row) {
						zombieMap.get(i).setRemoved(true);
						zombieMap.get(i).setWalnutCollide(false); //removes zombies of the same row as cherry bomb
					}
				}

			}
		}
		else if (choice == 3) { //Creates and adds Walnut object to its list
			Walnut walnut = new Walnut (0,0, 0, 0, null);
			walnut.setLife(walnut.getMaxLife());
			walnut.setIndex(walnutIndex);
			walnut.setColumn(column);
			walnut.setRow(row);
			int xPos = SIDE_OFFSET + (column - 1) * GRID_WIDTH + BORDER_SIZE +70;
			int yPos = row * GRID_HEIGHT+5;
			walnut.setWalnutRect(new Rectangle(xPos, yPos, GRID_WIDTH, GRID_HEIGHT));
			walnutList.add(walnut);
			sideRectDraw = false;
			empty = false;
			walnutIndex++;
		}
		else if (choice == 4) { //Creates sunflower and the sun object associated with each sunflower
			Sun flowerSun = new Sun (false, 0, 0, 0, null);
			Sunflower sunflower = new Sunflower (0, 0, 0, false, null);
			//calculate x and y pos of Sun. and column
			sunflower.setHaveSun(false);
			sunflower.setTime(0);
			flowerSun.setCollected(true);
			sunflower.setColumn(column);
			flowerSun.setColumn(column);
			int xPos = SIDE_OFFSET + (column - 1) * GRID_WIDTH + BORDER_SIZE + 40;
			int yPos = row * GRID_HEIGHT + 40;
			flowerSun.setXPos(xPos);
			flowerSun.setYPos(yPos);//
			flowerSun.setSunRect(new Rectangle (xPos, yPos, 40, 40));
			sunflower.setFlowerSun(flowerSun);
			sunflower.setRow(row);
			sunflowerList.add(sunflower);
			sunflowertf = true;
			sideRectDraw = false;
			empty = false;
		}
	}

	/* Description: Creates Zombie objects in random rows
	 * Parameters: none
	 * Returns:	void
	 */
	public void randZombies() {
		zombieKey++;
		Zombie zombie = new Zombie(false, false, 0, 0, 0, 0, 0, 0, 0, false, new Rectangle());
		zombie.setRemoved(false);
		zombie.setColliding(false);
		zombie.setWalnutCollide(false);
		zombie.setHitCount(0);
		zombie.setColumn(9);
		int row = (int)(Math.random()*5)+1; //generates a random row between 1 and 5
		zombie.setRow(row);
		zombie.setXPos(SIDE_OFFSET + 9 * GRID_WIDTH + BORDER_SIZE);	
		zombie.setYPos(row * GRID_HEIGHT); //y positions stays constant
		zombie.setCurrentCollisionIndex(-1);
		Rectangle rect = new Rectangle (SIDE_OFFSET + 9 * GRID_WIDTH + BORDER_SIZE, row * GRID_HEIGHT, GRID_WIDTH, GRID_HEIGHT);
		zombie.setZombieRect(rect);
		zombieMap.put(zombieKey, zombie);

	}
	
	/* Description: Detects collisions between peaShooter and the each zombie
	 * Parameters: checkZombie = checks is this Zombie object is in contact with a peaShooter, shootList = list of peashooters on the grid
	 * Returns:	i = index of the peashooter in collision with the zombie
	 */
	public int shooterCollisions(Zombie checkZombie, ArrayList <Peashooter> shootList) { //pass in xy of zombie or sun that needs to be checked
		//makes an arraylist of peashooters of the same row as the zombie, sorts this list by column, and checks if peashooters of the same row are in the same column as the zombie as well
		int index = -1;
		ArrayList <Peashooter> shootSameRow = new ArrayList <Peashooter>();	
		Iterator <Peashooter> shootIter = shootList.iterator();
		while (shootIter.hasNext()) {
			Peashooter ps = shootIter.next();
			if (ps.getRow() == (checkZombie.getRow()))
				shootSameRow.add(ps);
		}
		if (shootSameRow.size()>0) {
			Collections.sort(shootSameRow); //sorts by column
			index = Collections.binarySearch(shootSameRow, new Peashooter(0, 0, 0, checkZombie.getColumn(), null, null));
			if (index >= 0) {
				if (checkZombie.getZombieRect().intersects(shootSameRow.get(index).getShooterRect())) {
					checkZombie.setColliding(true); 
					int i = shootSameRow.get(index).getIndex();
					return i;
				}
			}
		}
		return index;
	}

	/* Description: Detects collisions between Walnut and the each zombie
	 * Parameters: checkZombie = checks is this Zombie object is in contact with a Walnut
	 * Returns:	i = the index of the walnut in collision with the zombie
	 */
	public int walnutCollisions(Zombie checkZombie) {
		for (int i = 0; i<walnutList.size();i++) { //if the zombie is colliding with a walnut, the walnut's index is returned
			if (checkZombie.getZombieRect().intersects(walnutList.get(i).getWalnutRect())) {
				checkZombie.setColliding(true);
				checkZombie.setWalnutCollide(true);
				return i;
			}
		}
		return -1;
	}

	/* Description: Detects collisions between peas and the each zombie
	 * Parameters: shooterPea = checks is this Pea object is in contact with a zombie
	 * Returns:	void
	 */
	public void peaCollisions (Pea shooterPea) {
		for (int i = 1; i <= zombieMap.size(); i++) {
			if (zombieMap.get(i).getRemoved() == false) {
				if (shooterPea.getPeaRect().intersects(zombieMap.get(i).getZombieRect())) {	//detects collision with rectangles surrounding each object
					int hit = zombieMap.get(i).getHitCount();
					hit++;
					zombieMap.get(i).setHitCount(hit);
					shooterPea.setXPos(SIDE_OFFSET + (shooterPea.getColumn()-1) * GRID_WIDTH + BORDER_SIZE +70);
				}
			}
		}
	}

	// Description: draws all graphics, including invisible rectangles to keep track of positions/collisions
	// Parameters: g - graphics parameter required for paintComponent method
	// Returns: void
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Set up the offscreen buffer the first time paint() is called
		if (offScreenBuffer == null) {
			offScreenImage = createImage (this.getWidth (), this.getHeight ());
			offScreenBuffer = offScreenImage.getGraphics ();
		}

		// All of the drawing is done to an off screen buffer which is
		// then copied to the screen.  This will prevent flickering
		// Clear the offScreenBuffer first
		offScreenBuffer.clearRect (0, 0, this.getWidth (), this.getHeight ());

		//start screen
		if (gameStart ==false && gameOver == false) {
			offScreenBuffer.setFont(COURIER_30); 
			offScreenBuffer.setColor(Color.WHITE);
			offScreenBuffer.drawImage(start, 0, 0, this);
			String startStr = "Click anywhere to Start";
			offScreenBuffer.drawString(startStr, 450, 500);

		}
		
		//end screen
		if (endIndex ==0) {
			offScreenBuffer.setFont(COURIER_30); 
			offScreenBuffer.setColor(Color.WHITE);
			offScreenBuffer.drawImage(end, 0, 0, this);
			String startStr = "Click anywhere to Restart";
			offScreenBuffer.drawString(startStr, 250, 520);
		}
		
		//during the game
		if(gameStart && gameOver == false) {

			offScreenBuffer.drawImage(bg, 70, 0, this);	//draws background
			offScreenBuffer.drawImage(sidebar, 0, 0, this);

			if (sideRectDraw) {
				offScreenBuffer.setColor(Color.YELLOW);
				offScreenBuffer.drawRect(0, currentSideY, 120, sideRectHeight);
			}

			// Redraw the grid with current pieces
			int w = GRID_WIDTH-5;
			int h = GRID_HEIGHT-10;
			for(int row = 0; row<grid.length; row++) {
				int yPos = row * GRID_HEIGHT;
				for(int column = 0; column < grid[row].length - 1; column++) {
					int xPos = SIDE_OFFSET + (column - 1) * GRID_WIDTH + BORDER_SIZE;
					if (grid [row] [column] == SHOOTER)
						offScreenBuffer.drawImage (shooterImg, xPos, yPos, w, h, this);
					else if (grid [row] [column] == SUNFLOWER)
						offScreenBuffer.drawImage (sunflowerImg, xPos, yPos, w, h, this);
					else if (grid [row][column] == WALL)
						offScreenBuffer.drawImage(walnutImg, xPos, yPos, w, h, this); 
					else if (grid[row][column]==BOMB) {
						offScreenBuffer.drawImage(bombImg, xPos, yPos, w, h, this);
					}
				}
				//redraws all zombies on the grid
				if (zombietf) {
					for (int i = 1; i <= zombieMap.size(); i++) {
						if (zombieMap.get(i).getRemoved() == false)
							offScreenBuffer.drawImage(zombie1Img, zombieMap.get(i).getxPos(), zombieMap.get(i).getyPos(), GRID_WIDTH, GRID_HEIGHT, this);
					}
				}
				//redraws all peas
				if(shootertf) {
					for (int i = 0; i < shooterList.size(); i++) {
						offScreenBuffer.drawImage(peaImg, shooterList.get(i).getShooterPea().getxPos(), shooterList.get(i).getShooterPea().getyPos(), 20, 20, this);
					}
				}//redraws sun falling from the sky
				if(skySuntf) {
					offScreenBuffer.drawImage(sunImg, skySun.getxPos(), skySun.getyPos(), 40, 40, this);
				}
				//draw sun from each sunflower
				if (sunflowertf) {
					for (int i = 0; i < sunflowerList.size(); i++) {
						if (sunflowerList.get(i).getFlowerSun().getCollected()==false)
							offScreenBuffer.drawImage(sunImg, sunflowerList.get(i).getFlowerSun().getxPos(), sunflowerList.get(i).getFlowerSun().getyPos(), 40, 40, this);
					}
				}
				//sun currency label
				String total = totalSun + "";
				offScreenBuffer.setFont(COURIER_20); 
				offScreenBuffer.setColor(Color.BLACK);
				if (totalSun < 100)
					offScreenBuffer.drawString(total, 55, screenHeight-10);
				else
					offScreenBuffer.drawString(total, 45, screenHeight-10);
			}
		}

		// Transfer the offScreenBuffer to the screen
		g.drawImage (offScreenImage, 0, 0, this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		int x, y;
		x=e.getX();
		y=e.getY()-50;	//taking into account frame and menubar
		
		handleAction(x,y);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	
	// Description: main method to create the window
	// Returns: void
	public static void main(String[] args) {

		//makes a brand new JFrame
		frame = new JFrame ("Plants vs Zombies");
		//makes a new copy of your "game" that is also a JPanel
		Driver myPanel = new Driver ();
		//so your JPanel to the frame so you can actually see it
		frame.add(myPanel);
		//so you can actually get mouse input
		frame.addMouseListener(myPanel);
		//self explanatory. You want to see your frame
		frame.setVisible(true);
		//some weird method that you must run
		frame.pack();
		//place your frame in the middle of the screen
		frame.setLocationRelativeTo(null);
		//without this, your thread will keep running even when you windows is closed!
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//self explanatory. You don't want to resize your window because
		//it might mess up your graphics and collisions
		frame.setResizable(false);

	}

}
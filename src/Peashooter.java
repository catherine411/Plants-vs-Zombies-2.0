import java.awt.Rectangle;

//Description: This Class creates Peashooter objects and its instance variables 


public class Peashooter implements Comparable <Peashooter> {
	
	private int index;
	private int life;	//tracks how long peashooter has been in contact with a zombie
	private int row;
	private int column;
	private Rectangle shooterRect;	//used for detecting collision
	private Pea shooterPea;	//one pea is associated with each peashooter
	static private int maxLife = 5000; 
	
	public Peashooter(int index, int life, int row, int column, Pea shooterPea, Rectangle shooterRect) {
		this.index = index;
		this.life = life;
		this.row = row;
		this.column = column;
		this.shooterPea = shooterPea;
		this.shooterRect = shooterRect;
	}

	
	public int getIndex() {
		return index;
	}
	public int getLife() {
		return life;
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return column;
	}
	public int getMaxLife () {
		return maxLife;
	}
	public Pea getShooterPea() {
		return shooterPea;
	}
	public Rectangle getShooterRect () {
		return shooterRect;
	}

	
	public void setIndex(int i) {
		index = i;
	}
	public void setLife(int lives) {
		life = lives;
	}
	public void setRow(int r) {
		row = r;
	}
	public void setColumn(int c) {
		column = c;
	}
	public void setShooterPea(Pea pea) {
		shooterPea = pea;
	}
	public void setShooterRect (Rectangle rect) {
		shooterRect = rect;
	}

	public int compareTo (Peashooter shooter) {
		return this.column - shooter.column;
	}


}

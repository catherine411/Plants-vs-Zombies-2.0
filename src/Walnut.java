//Description: This Class creates Walnut objects and its instance variables 

import java.awt.Rectangle;

public class Walnut {
	private int index;
	private int life;
	private int row;
	private int column;
	Rectangle walnutRect;
	static private int maxLife = 40;
	
	public Walnut(int index, int life, int row, int column, Rectangle walnutRect) {
		this.index = index;
		this.life = life;
		this.row = row;
		this.column = column;
		this.walnutRect = walnutRect;
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
	public Rectangle getWalnutRect() {
		return walnutRect;
	}
	
	public int getMaxLife () {
		return maxLife;
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
	public void setWalnutRect(Rectangle rect) {
		walnutRect = rect;
	}
}

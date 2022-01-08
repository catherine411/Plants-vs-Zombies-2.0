//Description: This Class creates Walnut objects and its instance variables 

import java.awt.Rectangle;

public class Zombie {

	private boolean colliding;
	private boolean walnutCollide;
	private int collideCount;
	private int hitCount;
	private int row;
	private int column;
	private int xPos;
	private int yPos;
	private int currentCollisionIndex;
	private Rectangle zombieRect;
	private boolean removed; //might include
	
	public Zombie(boolean colliding, boolean walnutCollide, int collideCount, int hitCount, int row, int column, int xPos, int yPos, int currentCollisionIndex, boolean removed, Rectangle zombieRect) {
		this.colliding = colliding;		//tracking variables to see whether it is colliding with powerups of the same row
		this.walnutCollide=walnutCollide;
		this.collideCount = collideCount;
		this.hitCount = hitCount;
		this.row = row;
		this.column = column;
		this.xPos = xPos;
		this.yPos = yPos;
		this.currentCollisionIndex = currentCollisionIndex;
		this.removed = removed;
		this.zombieRect = zombieRect;	//for detecting collision
	}
	
	public boolean getColliding() {
		return colliding;
	}
	public boolean getWalnutCollide() {
		return walnutCollide;
	}
	public int getCollideCount() {
		return collideCount;
	}
	public int getHitCount() {
		return hitCount;
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return column;
	}
	public int getxPos() {
		return xPos;
	}
	public int getyPos() {
		return yPos;
	}
	public int getCurrentCollisionIndex() {
		return currentCollisionIndex;
	}
	public boolean getRemoved() {
		return removed;
	}
	public Rectangle getZombieRect() {
		return zombieRect;
	}
	
	public void setColliding(boolean collide) {
		colliding = collide;
	}
	public void setWalnutCollide(boolean wallCollide) {
		walnutCollide = wallCollide;
	}
	public void setCollideCount (int collideC) {
		collideCount = collideC;
	}
	public void setHitCount(int count) {
		hitCount = count;
	}
	public void setRow(int r) {
		row = r;
	}
	public void setColumn(int c) {
		column = c;
	}
	public void setXPos(int x) {
		xPos = x;
	}
	public void setYPos (int y) {
		yPos = y;
	}
	public void setCurrentCollisionIndex(int cci) {
		currentCollisionIndex = cci;
	}
	public void setRemoved(boolean remove) {
		removed = remove;
	}
	public void setZombieRect(Rectangle rect) {
		zombieRect = rect;
	}
	
}

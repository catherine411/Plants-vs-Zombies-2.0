import java.awt.Rectangle;

//Description: This Class creates Sun objects and its instance variables 


public class Sun {
	private boolean collected; //whether the sun is collected or not
	private int column; 
	private int yPos;
	private int xPos;
	private Rectangle sunRect;
	
	public Sun (boolean collected, int column, int xPos, int yPos, Rectangle sunRect) {
		this.collected = collected; 
		this.column = column; 
		this.xPos = xPos;
		this.yPos = yPos;
		this.sunRect = sunRect;
	}
	
	public boolean getCollected() {
		return collected;
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
	public Rectangle getSunRect() {
		return sunRect;
	}

	public void setCollected (boolean collect) {
		collected = collect;
	}
	public void setXPos(int x) {
		xPos = x;
	}
	public void setYPos (int y) {
		yPos = y;
	}
	public void setColumn(int c) {
		column = c;
	}
	public void setSunRect(Rectangle rect) {
		sunRect = rect;
	}
}

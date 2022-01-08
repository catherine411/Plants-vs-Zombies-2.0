import java.awt.Rectangle;
//Description: This Class creates Pea objects and its instance variables 


public class Pea {
	private int xPos; 
	private int yPos;
	private int row;
	private int column;
	private Rectangle peaRect;
	
	public Pea (int xPos, int yPos, int row, int column, Rectangle peaRect){
		this.xPos = xPos;
		this.yPos = yPos;
		this.row = row;
		this.column = column;
		this.peaRect = peaRect; 
	}
	
	public int getxPos() {
		return xPos;
	}
	public int getyPos() {
		return yPos;
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return column;
	}
	public Rectangle getPeaRect() {
		return peaRect;
	}

	public void setXPos(int x) {
		xPos = x;
	}
	public void setYPos (int y) {
		yPos = y;
	}
	public void setRow (int r) {
		row = r;
	}
	public void setColumn (int c) {
		column = c;
	}
	public void setPeaRect(Rectangle rect) {
		peaRect = rect;
	}
}

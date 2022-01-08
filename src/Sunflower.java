//Description: This Class creates Sunflower objects and its instance variables 

public class Sunflower {

	private int time;	//tracks time when last sun was released
	private int row;
	private int column;
	private boolean haveSun;
	private Sun flowerSun;	//one Sun object with each sunflower
	static private int maxLife = 15000;

	public Sunflower(int time, int row, int column, boolean haveSun, Sun flowerSun) {
		this.time = time;
		this.row = row;
		this.column = column;
		this.haveSun = haveSun;
		this.flowerSun = flowerSun;
	}


	public int getTime() {
		return time;
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return column;
	}
	public boolean getHaveSun() {
		return haveSun;
	}
	public Sun getFlowerSun() {
		return flowerSun;
	}
	public int getMaxLife () {
		return maxLife;
	}

	public void setTime(int times) {
		time = times;
	}
	public void setRow(int r) {
		row = r;
	}
	public void setColumn(int c) {
		column = c;
	}
	public void setHaveSun(boolean have) {
		haveSun = have;
	}
	public void setFlowerSun(Sun sun) {
		flowerSun = sun;
	}

}

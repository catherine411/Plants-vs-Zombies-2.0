
import java.util.*;
//sorts peashooter objects by its index in ascending order (the index assigned when they are created/as part of their instance variables)

public class SortByShooterIndex implements Comparator<Peashooter> {
	public int compare(Peashooter shooter1, Peashooter shooter2) {
		return shooter1.getIndex() - shooter2.getIndex();
	}
}


import java.util.*;

//sorts walnut objects by its index in ascending order (the index assigned when they are created/as part of their instance variables)

public class SortByWalnutIndex implements Comparator<Walnut>  {
	public int compare(Walnut wall1, Walnut wall2) {
		return wall1.getIndex() - wall2.getIndex();
	}
}

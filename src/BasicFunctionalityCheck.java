import java.util.ArrayList;
import java.util.List;

public class BasicFunctionalityCheck {
	public static void main(String[] args) {
		//List [] lists = {new ArrayList<Number>(), new SkipList<Number>()};
		List [] lists = {new SkipList<Number>()};
		for (List <Number> currentList : lists){
			System.out.println(currentList.getClass().getSimpleName());
			System.out.println(currentList);
			currentList.add(currentList.size(), 1);
			System.out.println(currentList);
			currentList.add(currentList.size(), 2);
			System.out.println(currentList);
			currentList.add(currentList.size(), 3);
			System.out.println(currentList);
			currentList.add(currentList.size(), 4);
			System.out.println(currentList);


			System.out.println("toString()");
			System.out.println(currentList);
			System.out.println("Expected:");
			System.out.println("[1, 2, 3, 4]");
			System.out.println();

			System.out.println("size(): " + currentList.size());
			System.out.println("Expected:");
			System.out.println("size(): 4");
			System.out.println();

			System.out.println("Adding two more items");
			currentList.add(1, 5);
			System.out.println(currentList);
			currentList.add(0, 6);
			System.out.println(currentList);
			System.out.println("size(): " + currentList.size());
			System.out.println("Expected:");
			System.out.println("[6, 1, 5, 2, 3, 4]");
			System.out.println("size(): 6");
			System.out.println();

			System.out.println("Testing get");
			for (int i = 0; i < currentList.size(); i++){
				System.out.print(currentList.get(i)+" ");
			}
			System.out.println("\n");
			
			System.out.println("Testing remove");
			System.out.println(currentList.remove(4));
			System.out.println(currentList);
			System.out.println("");
			
			System.out.println("List clear");
			currentList.clear();
			System.out.println(currentList.toString());
			System.out.println("size(): " + currentList.size());
			System.out.println("Expected:");
			System.out.println("[]");
			System.out.println("size(): 0");
			System.out.println();
		}
	}
}

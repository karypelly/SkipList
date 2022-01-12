import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.Test;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SkipListUnitTester {

	@Test
	public void constructorInterfaceTester_10_pts() {
		Field[] fields = SkipList.class.getDeclaredFields();
		for (Field f: fields){
			assertTrue("SkipList contains a public field", 
					!Modifier.isPublic(f.getModifiers()));
		}

		assertTrue ("Number of constructors != 1", 
				SkipList.class.getDeclaredConstructors().length == 1);

		assertTrue ("List interface not implemented or other interfaces are", 
				SkipList.class.getInterfaces().length == 1 
				&& SkipList.class.getInterfaces()[0].getName().equals("java.util.List"));
	}

	@Test
	public void addTester1_5_pts() {
		SkipList<String> list = new SkipList<>();
		//check for exceptions
		try{
			list.add(1, "0");
			fail("Exception was to be thrown");
		}catch (IndexOutOfBoundsException e){
			//OK
		}catch (Exception ex) {
			fail("Wrong type of exception");
		}

		list.add("0");
		assertTrue(list.get(0).equals("0"));
	}

	@Test
	public void addTester2_10_pts() {
		SkipList<String> list = new SkipList<>();
		list.add(0, "0");
		list.add(0, "1");
		list.add(1, "2");
		assertTrue(list.toString().equals("[1, 2, 0]") 
				|| list.get(0).equals("1") && list.get(1).equals("2") && list.get(2).equals("0"));
	}

	@Test
	public void removeTester1_5_pts() {
		SkipList<String> list = new SkipList<>();
		//check for exception
		try{
			list.remove(0);
			fail("Exception was to be thrown");
		}catch (IndexOutOfBoundsException e){
			//OK
		}catch (Exception ex) {
			fail("Wrong type of exception");
		}
	}

	@Test
	public void removeTester2_10_pts() {
		SkipList<String> list = new SkipList<>();
		list.add("1"); list.add("2"); list.add("3");
		assertTrue(list.size() == 3);
		list.remove(1);
		assertTrue(list.size() == 2);
		assertTrue(list.toString().equals("[1, 3]") 
				|| list.get(0).equals("1") && list.get(1).equals("3"));
	}

	@Test
	public void randomAddRemove_25_pts(){
		Random rnd = new Random();
		List <Integer> list1 = new ArrayList<>();
		List <Integer> list2 = new SkipList<>();
		for (int i = 0; i < 10_000; i++){
			if (rnd.nextDouble() < 0.5){
				int position = rnd.nextInt(list1.size()+1);
				int value = rnd.nextInt(1000);
				list1.add(position, value);
				list2.add(position, value);
			}
			else{
				if (list1.size() > 0){
					int position = rnd.nextInt(list1.size());
					list1.remove(position);
					list2.remove(position);
				}
			}
		}

		assertTrue("randomAddRemove_20_pts: toString" , list1.toString().equals(list2.toString()));
		for (int i = 0; i < list1.size(); i++){
			assertTrue("randomAddRemove_20_pts: " + list1 + "\n" + list2, list1.get(i).equals(list2.get(i)));
		}
	}

	@Test
	public void getTester1_5_pts() {
		SkipList<String> list = new SkipList<>();
		//check for exceptions
		try{
			list.get(0);
			fail("Exception was to be thrown");
		}catch (IndexOutOfBoundsException e){
			//OK
		}catch (Exception ex) {
			fail("Wrong type of exception");
		}
	}

	@Test
	public void getTester2_5_pts() {
		SkipList<String> list = new SkipList<>();
		list.add("0");list.add("1");
		assertTrue(list.get(0).equals("0"));
		assertTrue(list.get(1).equals("1"));
	}

	@Test
	public void clearTester_5_pts() {
		SkipList<String> list = new SkipList<>();
		assertTrue(list.size() == 0);

		list.add("1"); list.add("2"); list.add("3");
		assertTrue(!list.isEmpty());

		list.clear();
		assertTrue(list.size() == 0);

		try{
			list.get(0);
			fail("Exception was to be thrown");
		}catch (IndexOutOfBoundsException e){
			//OK
		}catch (Exception ex) {
			fail("Wrong type of exception");
		}
	}

	@Test
	public void sizeTester_5_pts() {
		SkipList<String> list = new SkipList<>();
		assertTrue(list.size() == 0);
		list.add("1");
		assertTrue(list.size() == 1);
		list.add("9");
		assertTrue(list.size() == 2);
	}

	@Test
	public void toStringTester1_5_pts() {
		SkipList<String> list = new SkipList<>();
		assertTrue(list.toString().equals("[]"));
	}

	@Test
	public void toStringTester2_10_pts() {
		SkipList<String> list = new SkipList<>();
		list.add("1"); list.add("2"); list.add("3");
		assertTrue(list.toString().equals("[1, 2, 3]"));
	}
	
	@Test (timeout = 2000)
	public void addLogPerformance_30_pts(){
		final int SIZE = 65_536;
		final int LOG2_SIZE = 16;
		final int CONST_FACTOR = 5;
		List <Integer> list1 = new ArrayList<>();
		List <Integer> list2 = new SkipList<>();

		//array list add at the end (benchmark)
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < SIZE; i++){
				list1.add(i);
		}
		long endTime = System.currentTimeMillis();
		long elapsedAL = endTime - startTime;

		//skip list add at the end
		startTime = System.currentTimeMillis();
		for (int i = 0; i < SIZE; i++){
			list2.add(i);
		}
		endTime = System.currentTimeMillis();
		long elapsedSL = endTime - startTime;
		
		list2.clear();
		//skip list add at the beginning
		startTime = System.currentTimeMillis();
		for (int i = 0; i < SIZE; i++){
			list2.add(0, i);
		}
		endTime = System.currentTimeMillis();
		long elapsedSL0 = endTime - startTime;
		
		System.out.println(elapsedAL + "\n" + elapsedSL + "\n" + elapsedSL0);
		
		assertTrue("Non-logarithmic add() performance" , 
				elapsedSL < elapsedAL * LOG2_SIZE * CONST_FACTOR 
				&& elapsedSL0 < elapsedAL * LOG2_SIZE * CONST_FACTOR);
	}

}
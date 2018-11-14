
import rxtxrobot.*;
import java.io.Console;
import java.util.Scanner;
//import EXERobot.java;

public class RobotDriver {
	private RXTXRobot robot;
	private Console terminal;

	public static void main(String[] args) {


		EXERobot robot = new EXERobot();
		//reading input from user for program to run
		Console terminal = System.console();

		System.out.println("Hello");
		Scanner input = new Scanner(System.in);

		printMenu();
		char in = input.nextLine().charAt(0);

		while (in != 'q') {
			switch (in) {
				case 'a':
					//				LEFT		RIGHT
					robot.quadrantOne();
					break;
				case 'b':
					robot.quadrantTwo();
					break;
				case 'c':
					robot.quadrantThree();
					break;
				case 'd':
					robot.quadrantFour();
					break;
				case 'e':
					robot.test();
					break;


			}
			in = input.nextLine().charAt(0);
		printMenu();
		}
	robot.close();
	}

	static public void printMenu()
	{
		String menu = "Options For Robot: \n" +
				"a: Quadrant One \n" +
				"b: Quadrant Two\n" +
				"c: Quadrant Three \n" +
				"d: Quadrant Four\n" +
				"e: Whatever is in test function";
		System.out.print(menu);

	}
}
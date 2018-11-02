/*
import java.io.Console;
import EXERobot.*;
import java.util.Scanner;

public class Driver {
	//private RXTXRobot robot;
	private Console terminal;
	public static void main(String[] args)
	{

		//setPort(); //need to input the terminal
		//connect(); //attempts to connect to arduino
		//RXTXRobot r = new ArduinoNano();

		EXERobot robot = new EXERobot();
		//reading input from user for program to run
		Console terminal = System.console();
		
		terminal.printf("HELLO");
		char in = terminal.readLine("Enter Program Number").charAt(0);


		while(in != 'q'){
			switch(in)
			{
				case 'a':
					robot.moveMotor(0, 0, 0, 0, 0);//enter int channel1, int speed1, int channel2, int speed2, int time
					break;
				case 'b':
					robot.moveServoMotor(0);  //enter angle
					break;
				case 'c':
					terminal.printf("" + robot.readIRChar());
					break;
				case 'd':
					robot.motorRunIndefinitely();
					break;
				case 'e':
					terminal.printf("" + robot.getTemperature());
					break;
			}
			in = terminal.readLine("Enter Program Number").charAt(0);

		}while(in != 'q');
		
	}

	/*private void moveMotor(int channel1, int speed1, int channel2, int speed2, int time)
	{
		//TODO
		runTwoPCAMotor(channel1, speed1, channel2, speed2, time)

	}*/
	
	/*private void moveServoMotor(double angle)
	{
		runPCAServo(int channel, angle); // need to know the channel
	}
	
	private char readIRChar()
	{
		refreshDigitalPins();
		char output = '0'; //connected to D4;
		output = getIRChar();
		return output;
	}
	
	private void motorRunIndefinitely()
	{
		runPCAMotor(4, 50, 0); //need to know channel, speed, and time 0 = infinite DOUBLE CHECK CHANNEL
	}
	
	private double getTemperature()
	{
		refreshDigitalPins()
		double output = -1;
		output = getAnalogPin(0);
		return output;
	}

	//make one for bump or ping


	//close();
	
}

*/


import rxtxrobot.*;
import java.io.Console;
import java.util.Scanner;
//import EXERobot.java;

public class RobotDriver {
	private RXTXRobot robot;
	private Console terminal;

	public static void main(String[] args) {


		EXERobot robot = new EXERobot("COM3");
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
					robot.moveMotor(5000);//enter int channel1, int speed1, int channel2, int speed2, int time
					break;
				case 'b':
					System.out.println("Enter Angle: ");
					int angle = Integer.parseInt(input.nextLine());
					robot.setMeasurementServo(angle);  //20 degrees is neutral
					break;
				case 'c':
					terminal.printf("" + robot.readIRChar());
					break;
				case 'd':
					robot.motorRunIndefinitely();
					break;
				case 'e':
					System.out.println("" + robot.getTemperature());
					break;
				case 'f':
						robot.checkBump();
						break;
				case 'g':
					for(int i = 0; i < 9; i++)
						robot.turnTen();
					break;
				case 'h':
					for(int i = 0; i < 9; i++)
						robot.turnTen();
					robot.stop();
					for(int i = 0; i < 9; i++)
						robot.turnTen();
					break;
				case 'i':
					robot.angleRecieved();
					break;
				case 'j':
					System.out.println("Enter an angle: ");
					angle = Integer.parseInt(input.nextLine());
					robot.moveIrSensorServo(angle);  //20 degrees is neutral
					break;
				case 'k':
					System.out.println("YEETING THE BRIDGE: ");
					robot.yeetTheBridge();
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
				"a: move three meters \n" +
				"b: move servo motor \n" +
				"c: read IR char \n" +
				"d: run motor until bump sensor \n" +
				"e: get temperature \n";
		System.out.print(menu);

	}
}
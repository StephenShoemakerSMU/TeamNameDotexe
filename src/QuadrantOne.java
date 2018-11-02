import rxtxrobot.*;
import java.io.Console;
import java.util.Scanner;

public class QuadrantOne{
public static void main(String[]args){
        EXERobot robot = new EXERobot("COM3");
        robot.quadrantOne();
        robot.close();
        }
}
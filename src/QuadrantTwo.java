import rxtxrobot.*;
import java.io.Console;
import java.util.Scanner;

public class QuadrantTwo{
    public static void main(String[]args){
        EXERobot robot = new EXERobot("COM3");
        robot.quadrantTwo();
        robot.close();
    }
}
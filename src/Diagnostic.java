import java.util.Scanner;

public class Diagnostic {
    public static void main(String[] args) {

       menu();

        Scanner input = new Scanner(System.in);
        char in = input.nextLine().charAt(0);
        EXERobot robot = new EXERobot();



        while (in != 'q') {
            switch (in) {
                //test Temperature
                case 'a':
                    robot.testTemperature();
                    break;
                //test Conductivity
                case 'b':
                    robot.testConductivity();
                    break;
                //test Inclinometer
                case 'c':
                    robot.testInclinometer();
                    break;
                //test IR
                case 'd':
                    robot.testIR();
                    break;

            }
            in = input.nextLine().charAt(0);
            menu();
        }
        robot.close();
    }


        private static void menu () {
            String output = "a. Test Temperature \n" +
                    "b. Test Conductivity \n" +
                    "c. Test Inclinometer \n" +
                    "d. Test IR \n" +
                    "e. Test Ping \n";
            System.out.println(output);
        }
    }

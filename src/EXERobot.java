import rxtxrobot.*;

public class EXERobot {
    //USB PORT
    final private static String USB_PORT = "COM3";
    final private static int INCLINOMETER_SENSOR_PORT = 1;

    //Sensor Ports
    final private static int TEMP_SENSOR_PORT = 0;
    final private static int IR_SENSOR_PORT = 4;
    final private static int BUMP_SENSOR = 5;
    final private static int PING_SENSOR = -1; //TODO

    //Movement Motor Ports
    final private static int MOTOR_LEFT = 11;
    final private static int MOTOR_RIGHT = 15;

    //Servo Measurement Ports
    final private static int SERVO_MEASUREMENTS_1 = 0;
    final private static int SERVO_MEASUREMENTS_2 = 4;
    final private static int PING_PONG_SERVO = 3;

    //Bump_Sensor Values
    final private static int BUMP_SENSOR_TRIGGERED = 0;
    final private static int BUMP_SENSOR_RELAXED = 1;


    //Pylons
    final private static char PYLON_NORTHEAST = 'K';
    final private static char PYLON_NORTH = 'G';
    //final private static char PYLON_NORTHWEST; //TODO
    final private static char PYLON_SOUTHEAST = 'V';
    final private static char PYLON_SOUTH = 'N';
    //final private static char PYLON_SOUTHWEST; //TODO

    //Movement Constants
    final private static int MOTORLEFTCONSTANT = 450;
    final private static int MOTORRIGHTCONSTANT = -210;


    private RXTXRobot robot;

    //Zero arg constructor
    public EXERobot() {
        RXTXRobot robot = new ArduinoUno();
        robot.setPort(USB_PORT);
        robot.connect();
    }

    //One Arg Constructor
    //Don't know why this is called
    public EXERobot(String port) {
        robot = new ArduinoUno();
        robot.setPort(USB_PORT);
        robot.connect();
    }

    //Turns of robot, call at end of run
    public void close() {
        robot.close();
    }

    //Moves the motor for a certain amount of time
    public void moveMotor(int time) {
        robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, MOTORRIGHTCONSTANT, time);

    }

    //Sets the measurement servo to a certain value
    public void setMeasurementServo(int angle) {
        robot.runPCAServo(SERVO_MEASUREMENTS_1, (int) ((7.2 / 9.0) * angle));
    }

    //Reads and gives an average value for ten readings from the ir beacons
    public char readIRChar() {

        //The amount of reading counts
        int readingCount = 10;
        char chars[] = new char[readingCount];
        robot.refreshDigitalPins();

        //pulling the IRChar multiple times to account for dead times
        for (int i = 0; i < readingCount; i++) {
            char currentReceived = robot.getIRChar();
            robot.refreshDigitalPins();
            chars[i] = currentReceived;
            robot.sleep(10);
        }

        // calculating the highest average value
        int charOc = 0;
        int zOccur = 0;
        char found = '0';
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '0')
                zOccur++;
            if (chars[i] != '0') {
                charOc++;
                found = chars[i];
            }

        }

        //returning the value
        if (charOc > zOccur)
            return found;
        return '0';


    }


    //This function runs into it hits a bump
    public void motorRunIndefinitely() {
        boolean stopCondition = false; //create a function to check bump sensor
        //running until it hits a bump

        while (!checkBump()) {
            robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, MOTORRIGHTCONSTANT, 0);

        }
        robot.runTwoPCAMotor(MOTOR_LEFT, 0, MOTOR_RIGHT, 0, 0);
    }

    //This function checks if the bump sensor is triggerered
    public boolean checkBump() {
        robot.refreshDigitalPins();

        //getting bump value
        int bumpValue = robot.getDigitalPin(BUMP_SENSOR).getValue();

        //checking bump value
        return (bumpValue == BUMP_SENSOR_TRIGGERED);
    }

    //Returns temperature
    public double getTemperature() {
        int sum = 0;

        //CALIBRATE THESE VALUES
        //double slope =  -1.071428571;
        //double yInter = 847.7452381;


        double averageReading = 0;
        int readingCount = 10;
        for (int i = 0; i < readingCount; i++) {
            robot.refreshAnalogPins();
            int reading = robot.getAnalogPin(TEMP_SENSOR_PORT).getValue();
            sum += reading;
        }
        averageReading = sum /  (double) readingCount;
        //double temp = (averageReading - yInter) / slope;
        return averageReading;
    }

    //Returns the current slope the robot is on
    public int getSlope() {
        robot.refreshAnalogPins();


        int adc = robot.getAnalogPin(INCLINOMETER_SENSOR_PORT).getValue(); //Todo needs to be calibrated

        //CALIBRATE THESE
        double slope = -0.380952381;
        double yInter = 118.2380952;

        return (int) ((slope * adc) + yInter);
        //return adc;
    }

    //Return conductivity
    public double getConductivity() {
        int ADC = robot.getConductivity();
        //double slope =-.363636;
        //double yInter = ;
        //return yInter + (slope * ADC);

        return ADC;
    }

    // TODO
    // calculates the angle between two servos
    public void angleRecieved() {
        int[] rtn = {-1, -1, -1}; // need to return the chars that go with that
        int count = 0;
        char c;
        int angle = 0;
        boolean beaconOneFound = false;
        char beaconOne = '0';
        int angleOne = -1;
        for (int i = 0; i < 36; i++) {
            robot.runPCAServo(SERVO_MEASUREMENTS_2, (int) ((7.2 / 9.0) * angle));
            robot.sleep(100);

            c = readIRChar();
            if (c != 0 && beaconOneFound) {
                System.out.println("Angle between beacon " + beaconOne + " and " + c + "is " + (angleOne - angle));
            }

            System.out.println("Character: " + c + " Angle: " + angle);
            beaconOne = c;
            angleOne = angle;
            //rtn[count] = angle;
            //count++;

            angle += 5;
        }

    }

    void moveIrSensorServo(int angle) {
        robot.runPCATimedServo(SERVO_MEASUREMENTS_2, (int) ((16.0 / 21.0) * angle) , 1000);
    }
//    public double pylonAngle() //todo how would we know which one is the second one
//    {
//        return 180 - angleRecieved() - angleRecieved();
//    }

    //Turns the robot ten degrees clockwise
    public void turnTen() {
        int time = 80; //todo calibrate this so it does a 10 degree turn
        robot.runTwoPCAMotor(MOTOR_LEFT, 200, MOTOR_RIGHT, 350, time);
        robot.sleep(time + 100);
    }

    //Turns the robot ninety degrees clockwise
    public void turnNinety() {
        int time = 1060;
        robot.runTwoPCAMotor(MOTOR_LEFT, 200, MOTOR_RIGHT, 300, time);
        robot.sleep(time + 1000);
        System.out.println("Turning ninety");
    }

    //turns the robot ninety degrees counter clockwise
    public void turnNinetyOpp() {
        int time = 600;
        robot.runTwoPCAMotor(MOTOR_LEFT, -200, MOTOR_RIGHT, -300, time);
        robot.sleep(time + 1000);
        System.out.println("Turning ninety");
    }

    // turns the robot ten degrees x amount of itmes
    public void turnTenMulti(int x) {
        for (int i = 0; i < x; i++) {
            turnTen();
        }
    }

    // turns the robot Fourty Five Degrees clockwise??
    //TODO CHECK THE DIRECTION
    public void turnFourtyFive() {
        int time = 530;
        robot.runTwoPCAMotor(MOTOR_LEFT, 200, MOTOR_RIGHT, 300, time);
    }


    //Stops the robot
    public void stop() {

        robot.runTwoPCAMotor(MOTOR_LEFT, 0, MOTOR_RIGHT, 0, 1000);
    }

    //returns conductivity
    public double conduction() {

        //Calibrate these values
        double slope = -7.811519566;
        double Yinter = 1023.646847;

        int adc = robot.getConductivity();

        return adc;
        //return (adc* slope)) + Yinter;
    }

//    public location takeTwoAngles(){
//
//    }

    //This function yeets the bridge
    public void yeetTheBridge() {
        robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, MOTORRIGHTCONSTANT + 15, 3700);
        robot.sleep(3800);
        System.out.println("Yeeting the bridge");
    }

    //This function drops the ball
    public void ballDrop() {
        robot.runPCAServo(PING_PONG_SERVO, (int) ((7.2 / 9.0) * 160));
    }

    //This is the function for temperature quadrant
    public void quadrantOne() {
        //setMeasurementServo(80);
        //moveIrSensorServo(0);
        //yeetTheBridge();
        //turnNinety();

        //robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, MOTORRIGHTCONSTANT, 3000);
        //robot.sleep(3000);
        //turnNinetyOpp();

        //going towards temperature bin

        //robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT-20, MOTOR_RIGHT, MOTORRIGHTCONSTANT + 50, 4800);
        //robot.sleep(6200);
        //setMeasurementServo(20);
        //robot.sleep(1000);
        /*robot.sleep(15000);
        System.out.println("Temperature: " +  getTemperature());
        setMeasurementServo(80);

        robot.runTwoPCAMotor(MOTOR_LEFT, -1*MOTORLEFTCONSTANT, MOTOR_RIGHT, (-1*MOTORRIGHTCONSTANT)+170, 2700);
        robot.sleep(2800);
        turnTen();
        robot.sleep(100);
        turnTen();
        robot.sleep(100);
        */
        //robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, (  MOTORRIGHTCONSTANT - 100), 3000);
        System.out.println("Angle: " + getSlope());
        //angleRecieved();

        //robot.sleep(1000);
        //turnFourtyFive();
        //robot.sleep(600);
        //robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, MOTORRIGHTCONSTANT, 2500);
        //robot.sleep(2600);
        //ballDrop();

        //angleRecieved();
        //robot.runPCAServo(SERVO_MEASUREMENTS_2, (int)((7.2/9.0)* 180));

//        moveTilPerpindicularTowardsBin();
        //    getAngleBetweenTwoPylons();
        //    turnTowardsBin()
        //    moveTowardsBin()
        //    dropConductivityProbe()
        //    getMeasurement();
        //    liftConductvityProbe();
        //    turn90TowardsMountain()
        //    turnAtMountain();
        //    yeetHalfwayAtMountain();
        //    getInclinometerMeasurement();
        //    yeetSecondHalfOfVolcano();
        //    yeetTheGasSensor();
        //    yeetBackDownTheMountain();
        //
//        moveMotor(-200, -250, 7890);
//        getTemperature();
    }

    //Run for quadrant two
    public void quadrantTwo() {
        robot.runPCAServo(PING_PONG_SERVO, 180);
        //robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, MOTORRIGHTCONSTANT, 5000);
        //robot.sleep(50000);
        robot.runPCAServo(PING_PONG_SERVO, 20);
        robot.sleep(3000);
        robot.runPCAServo(PING_PONG_SERVO, 180);

    }
}

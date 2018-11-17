import rxtxrobot.*;
import java.util.*;

public class EXERobot {
    //USB PORT
    final private static String USB_PORT = "COM3";
    final private static int INCLINOMETER_SENSOR_PORT = 1;

    //Sensor Ports
    final private static int TEMP_SENSOR_PORT = 0;
    final private static int IR_SENSOR_PORT = 4;
    final private static int BUMP_SENSOR = 5;
    final private static int PING_SENSOR = 7; //TODO

    //Movement Motor Ports
    final private static int MOTOR_LEFT = 11;
    final private static int MOTOR_RIGHT = 15;

    //Servo Measurement Ports
    final private static int SERVO_MEASUREMENTS_1 = 1;
    final private static int SERVO_MEASUREMENTS_2 = 4;
    final private static int PING_PONG_SERVO = 3;

    //Bump_Sensor Values
    final private static int BUMP_SENSOR_TRIGGERED = 0;
    final private static int BUMP_SENSOR_RELAXED = 1;


    //Pylons
    final private static char PYLON_NORTHEAST = 'K';
    final private static char PYLON_NORTH = 'G';
    final private static char PYLON_NORTHWEST = 'Z'; //TODO
    final private static char PYLON_SOUTHEAST = 'V';
    final private static char PYLON_SOUTH = 'N';
    final private static char PYLON_SOUTHWEST = 'Z'; //TODO

    //Movement Constants
    final private static int MOTORLEFTCONSTANT = 450;
    final private static int MOTORRIGHTCONSTANT = -210;


    private RXTXRobot robot;

    //Zero arg constructor
    public EXERobot() {
        robot = new ArduinoUno();
        robot.setPort(USB_PORT);
        robot.connect();
        zeroServos();
    }

    //One Arg Constructor
    //Don't know why this is called
    public EXERobot(String port) {
        robot = new ArduinoUno();
        robot.setPort(USB_PORT);
        robot.connect();
        zeroServos();
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
        int adjustedAngle = (int) ((7.2 / 9.0) * angle);
        robot.runPCAServo(SERVO_MEASUREMENTS_1, adjustedAngle);
    }

    //Reads and gives an average value for ten readings from the ir beacons
    public char readIRChar() {

        //The amount of reading counts
        int readingCount = 4;
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
        double slope =  -0.534779;
        double yInter = 447.017;


        double averageReading = 0;
        int readingCount = 10;
        for (int i = 0; i < readingCount; i++) {
            robot.sleep(200);
            robot.refreshAnalogPins();
            int reading = robot.getAnalogPin(TEMP_SENSOR_PORT).getValue();
            sum += reading;
        }
        averageReading = sum / (double) readingCount;
        double temp = (averageReading * slope) + yInter;
        return temp;
    }

    //use this function to getPing sensor value
    public double getPing() {
        int sum = 0;

        //CALIBRATE THESE VALUES
        double slope = 0.374359205;
        double yInter = 0.295889918;


        double averageReading = 0;
        int readingCount = 10;
        for (int i = 0; i < readingCount; i++) {
            robot.refreshDigitalPins();
            int reading = robot.getPing(PING_SENSOR);
            sum += reading;
        }
        averageReading = sum / (double) readingCount;
        double pingValue = (averageReading - yInter) / slope;
        return pingValue;
        //return averageReading;
    }

    //Returns the current slope the robot is on
    public int getSlope() {

        robot.sleep(1000);
        robot.refreshAnalogPins();

        int adc = robot.getAnalogPin(INCLINOMETER_SENSOR_PORT).getValue();
        robot.sleep(1000);
        adc = robot.getAnalogPin(INCLINOMETER_SENSOR_PORT).getValue();
        //Todo needs to be calibrated

        //CALIBRATE THESE
        //double slope = 1;
        //double yInter = 0;
        double slope = -0.169839;
        double yInter = 80.516;



        return (int) ((slope * adc) + yInter);
        //return adc;
    }

    //Return conductivity
    public double getConductivity() {
        robot.sleep(1000);
        robot.refreshDigitalPins();
        int ADC = robot.getConductivity();
        double slope = -.568211;
        double yInter = 578.889;
        return yInter + (slope * ADC);


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
        for (int i = 0; i < 36; i++)
        {
            robot.runPCAServo(SERVO_MEASUREMENTS_2, (int) ((7.2 / 9.0) * angle));
            robot.sleep(100);

            c = readIRChar();
            System.out.println("Character: " + c + " Angle: " + angle);
            //beaconOne = c;
            //angleOne = angle;
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
        int time = 50; //todo calibrate this so it does a 10 degree turn
        robot.runTwoPCAMotor(MOTOR_LEFT, 200, MOTOR_RIGHT, 350, time);
        robot.sleep(time + 1000);
    }

    public void turnTenOpp() {
        int time = 50; //todo calibrate this so it does a 10 degree turn
        robot.runTwoPCAMotor(MOTOR_LEFT, -200, MOTOR_RIGHT, -240, time);
        robot.sleep(time + 1000);
    }

    //Turns the robot ninety degrees clockwise
    public void turnNinety() {
        int time = 1060;
        robot.runTwoPCAMotor(MOTOR_LEFT, 255, MOTOR_RIGHT, 300, time);
        robot.sleep(time + 1000);
        System.out.println("Turning ninety");
    }

    //turns the robot ninety degrees counter clockwise
    public void turnNinetyOpp() {
        int time = 1060;
        robot.runTwoPCAMotor(MOTOR_LEFT, -250, MOTOR_RIGHT, -200, time);
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
        robot.runTwoPCAMotor(MOTOR_LEFT, 200, MOTOR_RIGHT, 340, time);
        robot.sleep(1000);
    }

    public void turnFourtyFiveOpp() {
        int time = 530;
        robot.runTwoPCAMotor(MOTOR_LEFT, -180, MOTOR_RIGHT, -230,time);
        robot.sleep(1000);
    }


    //Stops the robot
    public void stop() {

        robot.runTwoPCAMotor(MOTOR_LEFT, 0, MOTOR_RIGHT, 0, 1000);
    }

    public void defaultPing()
    {
        robot.runPCAServo(PING_PONG_SERVO, 180);
    }
    public void releaseBall()
    {
        robot.runPCAServo(PING_PONG_SERVO, 40);
        robot.sleep(100);
        robot.runPCAServo(PING_PONG_SERVO, 180);
        // robot.runPCAServo(PING_PONG_SERVO, 20);
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
        int QUADRANT  = 1;
        yeetTheBridge();

        turnFourtyFive();
        yeetTheBridge();
        turnNinety();
        robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, MOTORRIGHTCONSTANT, 2000);
        robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, MOTORRIGHTCONSTANT, 1000);
        robot.sleep(1500);
        turnNinetyOpp();
        turnTenOpp();
        turnTenOpp();
        turnTenOpp();
        yeetTheBridge();

        robot.sleep(1000);


        testTemperature();



        testInclinometer();
    }

    //Run for quadrant two
    public void quadrantTwo() {
        int QUADRANT =2;

        yeetTheBridge();

        turnNinetyOpp();
        turnTen();

        yeetTheBridge();

        testConductivity();



        testInclinometer();
    }

    //Run for Quadrant Three
    public void quadrantThree(){
        int QUADRANT = 3;

        //goToOrigin();
        yeetTheBridge();
        turnNinety();
        robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, MOTORRIGHTCONSTANT, 3000);
        robot.sleep(3500);
        turnNinetyOpp();
        yeetTheBridge();
        yeetTheBridge();
        robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, MOTORRIGHTCONSTANT, 1000);




        testInclinometer();


        robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, MOTORRIGHTCONSTANT, 1000);



        //fin
    }

    //Run for Quadrant Four
    public void quadrantFour(){
        yeetTheBridge();
        //angleRecieved();

        turnNinetyOpp();
        turnTen();

        yeetTheBridge();
        yeetTheBridge();
        yeetTheBridge();

        testInclinometer();
    }


    public void ReleaseBall()
    {
        robot.runPCAServo(PING_PONG_SERVO, 180);
        robot.sleep(900);
        robot.runPCAServo(PING_PONG_SERVO, 60);
    }
    //Put whatever needs to be tested into here
    public void test(){
        moveIrSensorServo(235);
    }

    //This function figures out where the robot is and goes to a set point based on where it is after leaving the bridge
    public void goToOrigin(){

    }
    //sets the servos to 0 origin
    public void zeroServos(){
        setMeasurementServo(80);
        moveIrSensorServo(0);
    }

    //This function will be used to pass the movable barriers
    //Treat it like a game of super easy frogger
    public void frogger(){

    }



    //DIAGNOSTIC FUNCTIONS
    public void testTemperature(){
        zeroServos();
        System.out.println("TESTING TEMPERATURE");
        setMeasurementServo(20);
        robot.sleep(1000);
        robot.sleep(9000);
        System.out.println("Temperature: " +  getTemperature());
        robot.sleep(1000);
        zeroServos();
    }

    public void testConductivity(){
        zeroServos();
        setMeasurementServo(45);
        System.out.println("Getting Conductivity");
        robot.sleep(1000);
        robot.sleep(5000);
        System.out.println("Conductivity: " +  getConductivity());
        robot.sleep(1000);
        setMeasurementServo(80);
    }

    public void testInclinometer(){
        System.out.println("Inclinometer: " + getSlope());
    }

    public void testIR(){
        char IRMAtrix[][] = generateIRArrayMatrix();
        IRArrayDensity beaconDensity = new IRArrayDensity(IRMAtrix);
        System.out.println("Angle between K and M: " + beaconDensity.indexBetween('A', 'M')*5);
    }

    public void testPing(){
        System.out.println("Ping: " + getPing());
    }

    public char[] getIRArray(){
        final int READING_COUNT = 3;
        char readings[] = new char[READING_COUNT];
        robot.sleep(1000);
        robot.refreshDigitalPins();

        //populating readings with readings
        for (int index = 0; index < READING_COUNT; index++) {
            robot.refreshDigitalPins();
            char currentReceived = robot.getIRChar();

            readings[index] = currentReceived;
            robot.sleep(100);
        }

        return readings;
    }


    public char[][] generateIRArrayMatrix(){
        //this is the amount of readings that can be taken on a 5degree interval in the range of the IR Servo
        int ANGLE_COUNT = 47;
        char arrayMatrix[][] = new char[ANGLE_COUNT][];

        zeroServos();

        for(int index = 0; index < ANGLE_COUNT; index++){
            robot.runPCAServo(4, (int) ((7.2 / 9.0) * (index*5)));
            robot.sleep(10);

            robot.refreshDigitalPins();

            arrayMatrix[index] = getIRArray();

        }

        return arrayMatrix;

    }

    public double[] getLocation(int quadrant){

        int retval;
        double[] soln;

        IRArrayDensity beaconData = new IRArrayDensity(generateIRArrayMatrix());

        char beaconOne;
        char beaconTwo;
        char beaconThree;

        switch(quadrant){
            case 1:
                beaconOne = PYLON_SOUTHEAST;
                beaconTwo = PYLON_NORTHEAST;
                beaconThree = PYLON_NORTH;
                break;
            case 2:
                beaconOne = PYLON_NORTH;
                beaconTwo = PYLON_NORTHWEST;
                beaconThree = PYLON_SOUTHWEST;
                break;
            case 3:
                beaconOne = PYLON_NORTHWEST;
                beaconTwo = PYLON_SOUTHWEST;
                beaconThree = PYLON_SOUTH;
                break;
            case 4:
                beaconOne = PYLON_SOUTH;
                beaconTwo = PYLON_SOUTHEAST;
                beaconThree= PYLON_NORTHEAST;
                break;
            default:
                beaconOne = PYLON_SOUTH;
                beaconTwo = PYLON_SOUTHEAST;
                beaconThree= PYLON_NORTHEAST;
                break;
        }


// Create an instance of the Navigation object class
        Navigation nav = new Navigation();
// The two beacon angle differences can be set and the solver run any number of times
        nav.setAngles(beaconData.indexBetween(beaconOne, beaconTwo)*5, beaconData.indexBetween(beaconTwo, beaconThree)*5);
// Run solver to find unknown robot coordinates
// RETURN_RANGE, RETURN_SUCCESS, RETURN_SINGULAR, and RETURN_DIVERGENCE are error codes from our code, don't worry about them
        retval = nav.newton_raphson();
        if (retval == Navigation.RETURN_SUCCESS) {
// Retrieve solution of coordinates
            soln = nav.getSolution();
            System.out.println("(x,y) coordinates of robot = (" +
                    soln[0] + "," + soln[1] + ")");
            return soln;
        }
        else if (retval == Navigation.RETURN_RANGE) {
            System.err.println("Angle out of range");

        }
        else if (retval == Navigation.RETURN_SINGULAR) {
            System.err.println("Singular Jacobian matrix");
        }
        else if (retval == Navigation.RETURN_DIVERGENCE) {
            System.err.println("Convergence failure in 100 iterations");
        }
        double output[] = {-1.0,-1.0};
        return output;
    }


}
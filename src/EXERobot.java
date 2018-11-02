import rxtxrobot.*;

public class EXERobot {

    //all the port constants should go here
    final private static int TEMP_SENSOR_PORT = 0;
    final private static int IR_SENSOR_PORT = 4;
    final private static int MOTOR_LEFT = 11; //TODO
    final private static int MOTOR_RIGHT = 15; //TODO
    final private static int SERVO_MEASUREMENTS_1 = 0;//TODO
    final private static int SERVO_MEASUREMENTS_2 = 4;
    final private static int PING_PONG_SERVO = 3;
    final private static int SERVO_DROP_BALL = -1;//TODO
    final private static int BUMP_SENSOR = 5;//TOD0
    final private static String USB_PORT = "COM3";
    final private static int BUMP_SENSOR_TRIGGERED = 0;
    final private static int BUMP_SENSOR_RELAXED = 1;

    final private static int INCLINOMETER_SENSOR_PORT = 1;

    final private static char PYLON_NORTHEAST = 'K';
    final private static char PYLON_NORTH = 'G';
    //final private static char PYLON_NORTHWEST; //TODO
    final private static char PYLON_SOUTHEAST = 'V';
    final private static char PYLON_SOUTH = 'N';
    //final private static char PYLON_SOUTHWEST; //TODO

    final private static int MOTORLEFTCONSTANT = 450;
    final private static int MOTORRIGHTCONSTANT = -210;
    private RXTXRobot robot;


    public EXERobot() {
        RXTXRobot robot = new ArduinoUno();
    }

    public EXERobot(String port) {
        robot = new ArduinoUno();
        robot.setPort(USB_PORT);
        robot.connect();
    }

    public void close()
    {
        robot.close();
    }
    public void moveMotor(int time) {
        //TODO
        robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, MOTORRIGHTCONSTANT, time);

    }

    public void setMeasurementServo(int angle) {
        robot.runPCAServo(SERVO_MEASUREMENTS_1, (int)((7.2/9.0)*angle)); // need to know the channel //TODO
    }

    public char readIRChar() {
        //TODO
        char chars[] = new char[2];
        robot.refreshDigitalPins();
        //pulling the IRChar multiple times to account for dead times
        char lastReceived = '0';
        char currentReceived;
        for (int i = 0; i <2 ; i++) {

            currentReceived = robot.getIRChar();
            chars[i] = currentReceived;
            robot.sleep(10);
        }



        int charOc = 0;
        int zOccur = 0;
        char found = '0';
        for(int i = 0; i < chars.length; i++)
        {
            if(chars[i] == '0')
                zOccur ++;
            if(chars[i] != '0')
            {charOc++;
            found = chars[i];
            }

        }
        if(charOc > zOccur)
            return found;
        return '0';




    }

    public void motorRunIndefinitely() {
        boolean stopCondition = false; //create a function to check bump sensor
        int speed1 = 300;
        int speed2 = 300;
        while (!checkBump()) {
            robot.runTwoPCAMotor(MOTOR_LEFT, speed1, MOTOR_RIGHT, speed2, 0);

        }
        robot.runTwoPCAMotor(MOTOR_LEFT, 0, MOTOR_RIGHT, 0,0 );
    }

    public boolean checkBump()
    {
        robot.refreshDigitalPins();

        int bumpValue =robot.getDigitalPin(BUMP_SENSOR).getValue();

        if(bumpValue == BUMP_SENSOR_TRIGGERED)

        {
            return true;
        }
        return false;

    }

    //

    public double getTemperature() {
        int sum = 0;
//        double slope =  -1.071428571;
//        double yInter = 847.7452381;
       double averageReading = 0;
           double readingCount = 10.0;
        for (int i = 0; i < readingCount; i++)
        {
            robot.refreshAnalogPins();
            int reading = robot.getAnalogPin(TEMP_SENSOR_PORT).getValue();
            sum += reading;
        }
        averageReading = sum / readingCount;
//        double temp = (averageReading - yInter) / slope;
        return averageReading/9.0;
        //return averageReading;
    }

    public int getSlope()
    {
        robot.refreshAnalogPins();
        int adc =  robot.getAnalogPin(INCLINOMETER_SENSOR_PORT).getValue(); //Todo needs to be calibrated
        double slope = -0.380952381;
        double yInter = 118.2380952;
        return (int) ((slope * adc) + yInter);
        //return adc;
    }

    public double getConductivity()
    {
        int ADC = robot.getConductivity();
        double slope =-.363636 ;
//        double yInter = ;
//        return yInter + slope * ADC;
        return ADC;
    }


    public void angleRecieved()
    {
        int[] rtn = {-1, -1, -1}; // need to return the chars that go with that
        int count = 0;
        char c;
        int angle = 0;
        boolean beaconOneFound = false;
        char beaconOne = '0';
        int angleOne = -1;
        for(int i = 0; i < 36; i ++) {
            robot.runPCAServo(SERVO_MEASUREMENTS_2, (int)((7.2/9.0)*angle));
            robot.sleep(100);

            c = readIRChar();
            if(c!=0 && beaconOneFound)
            {
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

    void moveIrSensorServo(int angle)
    {
        robot.runPCATimedServo(SERVO_MEASUREMENTS_2, (int)((16.0/21.0) * angle), 1000);
    }
//    public double pylonAngle() //todo how would we know which one is the second one
//    {
//        return 180 - angleRecieved() - angleRecieved();
//    }

    public void turnTen()
    {
        int time  = 80; //todo calibrate this so it does a 10 degree turn
        robot.runTwoPCAMotor(MOTOR_LEFT, 200, MOTOR_RIGHT, 350, time);
    }

    public void turnNinety()
    {
        int time = 1060;
        robot.runTwoPCAMotor(MOTOR_LEFT, 200, MOTOR_RIGHT, 300, time);
        robot.sleep(time+1000);
        System.out.println("Turning ninety");
    }
    public void turnNinetyOpp()
    {
        int time = 600;
        robot.runTwoPCAMotor(MOTOR_LEFT, -200, MOTOR_RIGHT, -300, time);
        robot.sleep(time+1000);
        System.out.println("Turning ninety");
    }


    public void turnTenMulti(int x)
    {
        for(int i = 0; i < x; i++)
        {
            turnTen();
        }
    }

    public void turnFourtyFive()
    {
        int time = 530;
        robot.runTwoPCAMotor(MOTOR_LEFT, 200, MOTOR_RIGHT, 300, time);
    }



    public void stop()
    {

        robot.runTwoPCAMotor(MOTOR_LEFT, 0, MOTOR_RIGHT, 0, 1000);
    }

    public double conduction()
    {
        return (robot.getConductivity() * (-7.811519566)) + 1023.646847;
    }

//    public location takeTwoAngles()
//    {
//
//    }

    public void yeetTheBridge() {
        robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT,MOTOR_RIGHT,MOTORRIGHTCONSTANT + 15,3700);
        robot.sleep(3800);
        System.out.println("Yeeting the bridge");
    }
    public void ballDrop()
    {
        robot.runPCAServo(PING_PONG_SERVO, (int)((7.2/9.0)* 160));
    }

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

     public void quadrantTwo(){
        robot.runPCAServo(PING_PONG_SERVO, 180);
        //robot.runTwoPCAMotor(MOTOR_LEFT, MOTORLEFTCONSTANT, MOTOR_RIGHT, MOTORRIGHTCONSTANT, 5000);
        //robot.sleep(50000);
        robot.runPCAServo(PING_PONG_SERVO, 20);
        robot.sleep(3000);
        robot.runPCAServo(PING_PONG_SERVO,180);

     }

     public void testSoil(){
        //robot.runPCAServo(IR_SENSOR_PORT, 0);
        robot.runPCAServo(SERVO_MEASUREMENTS_1,80);
        robot.sleep(1000);
        robot.runPCAServo(SERVO_MEASUREMENTS_1, 0);
        robot.sleep(1000);
        System.out.println("WATER PERCENT: " + conduction());
        robot.runPCAServo(SERVO_MEASUREMENTS_1,80);
        robot.close();
     }
//
//    public void quadrantTwo()
//    {
//        conduction();
//    }
//
//    public void quadrantThree()
//    {
//        //move
//        //get inclinometer
//    }
//
//    public void quadrantFour()
//    {
//        //avoid barriers
//        //get inclinometer
//        //move
//    }


}

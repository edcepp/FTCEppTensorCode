// Copyright (c) 2020 Edward C. Epp. All rights reserved.
//
// This opCode adds an action to detecting a gold mineral. The 
// robot attempts to orient itself facing a gold game piece.
//
// Enhacements to the ObjectDetection example include:
// * Include motor handling methods.
// * Turn toward the gold mineral.
//
// The goal is to permit redistribution in source and binary forms. 
// Enhancements, corrections and other modification will be encouraged. 
// Authorship must be acknowledged  and the 
// code may not be used for commercial purposes. Details, language 
// and license will be worked out.
//
// DISCLAIMER:
// The coding and teaching practices that appear represent the authors
// 20 years of teaching and 15 years in industry. They do not represent
// universal agreement about what is best.
//
// ASSUMED KNOWLEDGE: These are the highlights - not a complete list
//   Basic autonomous skills
//     * Manage robot controller and driver station
//     * Load programs into robot 
//     * Manage hardware configurations
//   Basic Java skills
//     * Selection: if and switch
//     * Interation: while and for 
//     * Using objects and classes
//     * Create methods
//
// **************************************************************


package org.firstinspires.ftc.teamcode;

import java.util.List;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.robot.RobotState;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

@Autonomous(name = "Orient toward gold mineral", group = "Concept")
//@Disabled

public class OrientToGoldEpp extends LinearOpMode
{
    /****************************** constants **************************/
    /****************************** constants **************************/

    private static final String TFOD_MODEL_ASSET     = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL   = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private static final int SCREEN_WIDTH   = 1280;
    private static final String VUFORIA_KEY = "AY2Daiz/////Enter your key";

    // Velocity in counts per second
    private static final int MAX_VELOCITY = 600;
    private static final int MID_VELOCITY = MAX_VELOCITY *  2 / 3;
    private static final int TURN_DIVIDE  = 13;
    
    /****************************** member variables *******************/
    /****************************** member variables *******************/

    // The Vuforia localization engine.
    private VuforiaLocalizer myVuforia = null;

    // The Tensor Flow Object Detection engine.
    private TFObjectDetector myTfod = null;

    // links to the physical robot driver motors
    DcMotorEx myLeftMotor  = null;
    DcMotorEx myRightMotor = null;

  // ********** RunOpMode
  // Our execution entry point 
  
  public void runOpMode () 
  {
    // intialize object detector and motors
    initVuforia();
    initTfod();
    initMotors();

    // Wait for start 
    telemetry.addData(">", "Press play: 20200825a");
    telemetry.update();
    waitForStart();

    // Keep on looking until time limit reached
    while (opModeIsActive()) 
    {
        // Ask the Tensorflow Object Detector for a list of objects recognized
        // in the current video image.
        List<Recognition> updatedRecognitions = 
                            myTfod.getUpdatedRecognitions();

        // Make sure we have a list of recognized objects
        if (updatedRecognitions != null)
        {
            // Go through the list of recognized objects one-by-one and tell us 
            // the distance of the gold minerals from center
            for (Recognition recognition : updatedRecognitions) 
            {
                // Respond to gold mineral find
                if (recognition.getLabel().equals("Gold Mineral")) 
                {
                    int goldMineralLeftX = (int) recognition.getLeft();
                    int goldMineralRightX = (int) recognition.getRight();
                    int goldMineralCenterX = (goldMineralLeftX + goldMineralRightX) / 2;
                    int offset = goldMineralCenterX - SCREEN_WIDTH / 2;
                    telemetry.addData ("Status: ", "Gold location offset " + offset);
                
                    int turn_clicks = offset / TURN_DIVIDE;
                    moveFor(turn_clicks, -turn_clicks);
                    
                    // Skip the rest of the list because we fouund
                    // the gold mineral
                    break;  
                }
            
                // Respond to silver mineral find
                else
                {
                    telemetry.addData ("Status: ", "Ignore the silver minerals");
                } 
            }
        }
        else
        {
            telemetry.addData ("Status: ", "No recognition changes");
        }
        telemetry.update();
        //sleep (2000);  // Pause 2 seconds so we can read the status
      }
    }
  
    // ********** initVuforia helper
    // Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
    // Configure the phone to use the rear camera.

    private void initVuforia() 
    {
        // Specify the Vuforia license key that gives us
        // permission to use their software. 
        // Indicate which camera we intend to use. 
        // Bundle this information into a list of parameters.

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        // Create the Vuforia viewing engine specified by our parameters
        myVuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Exit the program if we couldn't create the engine
        if (myVuforia == null)
        {
            telemetry.addData("ERROR", "the Vuforia engine did not initialize");
            sleep(2000);
            System.exit(1);
        }
    }

    // ********** initTfod helper
    // Initialize the Tensor Flow Object Detection engine.

    private void initTfod() 
    {
        // Determine if the Robot Controller is capable of supporting
        // Tensorflow
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) 
        {
            // Get the id for the monitor that will be used to view
            // the camera's image so we can see it.
            int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                    "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());

            // Create the TensorFlow Object Detector
            TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
            myTfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, myVuforia);

            // Load the model trained for the Rover Ruckus challenge.
            // It recognizes two objects. Give them meaningful names
            myTfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);

            if (myTfod == null) 
            {
                telemetry.addData("ERROR: ", "TensorFlow lite did not activate");
                sleep(2000);
                System.exit(1);
            }

            // Start detection - Camera stream should be on the monitor 
            else
            {
                myTfod.activate();
            } 
        }
 
        // The Robot Control is not capable so exit
        else {
            telemetry.addData("ERROR: ", "This device is not compatible with TFOD");
            sleep(2000);
            System.exit(1);
        }
    }

    // ********** initMotors helper
    //Initialize the drive motors.

    private void initMotors () 
    {
        // Set up drive motors
        myLeftMotor  = hardwareMap.get(DcMotorEx.class, "myLeftMotor");
        myRightMotor = hardwareMap.get(DcMotorEx.class, "myRightMotor");
        myLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        // reset encoder count
        myLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        myRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    // ********** moveFor helper
    // Turn each motor for a given number of counts
    //    leftCount:  the number of counts and direction to turn the left wheel
    //    rightCount:                                                right wheel
    // if the leftCount is less than the right count the robot will turn left
    // if the rightCount is less than the left count the robot will turn right

    private void moveFor (int leftCount, int rightCount)
    {
        myLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        myRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        myLeftMotor.setTargetPosition(leftCount);
        myRightMotor.setTargetPosition(rightCount);
        myLeftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        myRightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        myLeftMotor.setVelocity(MID_VELOCITY);
        myRightMotor.setVelocity(MID_VELOCITY);
        while (opModeIsActive() && myRightMotor.isBusy()) 
        {
            idle();
        }
    }
}

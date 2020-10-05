// Copyright (c) 2020 Edward C. Epp. All rights reserved.
//
// This opCode adds an action to detecting a gold mineral. It 
// reports the position of the gold mineral in relation
// to its horizontal position on the display.
//
// Enhacements to the ObjectDetection example include:
// * Include a while opModeIsActive loop to enable continuous monitoring
// * Report postion of gold mineral.
// * Name constant values to aid understanding and modification.
// * Implement an initialization method to manage complexity 
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

@Autonomous(name = "Compute gold mineral location", group = "Concept")
//@Disabled

public class ComputeTurnEpp extends LinearOpMode
{
    /****************************** constants **************************/
    /****************************** constants **************************/

    private static final String TFOD_MODEL_ASSET     = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL   = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private static final int SCREEN_WIDTH   = 1280;
    private static final String VUFORIA_KEY = "AY2Daiz/////Enter you key";

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

    // Wait for start 
    telemetry.addData(">", "Press play: 20200918b");
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
                    int error = goldMineralCenterX - SCREEN_WIDTH / 2;
                    telemetry.addData ("Status ", "Gold location error " + error);
                }
            
                // Respond to silver mineral find
                else
                {
                    telemetry.addData ("Status ", "Ignore the silver minerals");
                } 
            }
            telemetry.addData("Status", " End list");
        }
        else
        {
            telemetry.addData ("Status", " Nothing recognized");
        }
        telemetry.update();
        sleep (2000);  // Pause 2 seconds so we can read the status
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

}

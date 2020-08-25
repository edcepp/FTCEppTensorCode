// Copyright (c) 2020 Edward C. Epp. All rights reserved.
//
// This opCode recognizes the objects it detects. If it is 
// a gold minieral it will compute how far it is from the  
// horizonal center of the screen.
//
// It focuses on a simple implementation at the expense of 
// established BKMs (Best Know Methods).
//
// The goal is to permit redistribution in source and binary forms. 
// Enhancements, corrections and other modification will be encouraged. 
// Authorship must be acknowledged  and the 
// code may not be used for commercial purposes. Details, language 
// and license will be worked out.
//
// DISCLAIMER: This code does not follow best practices for error
// handling, concept abstraction or general coding. It is presented
// in this simplified linear form to avoid cognitive  overload. 
// These defects will be addressed in future versions. The author 
// suggests using best practices and outline in future versions 
// to avoid unfavorable judge evaluations.
//
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

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

@Autonomous(name = "Recognize a Rover Ruckus Object", group = "Concept")

public class ObjectRecognitionEpp extends LinearOpMode
{
  private static final int SCREEN_WIDTH   = 1280;
  private static final String VUFORIA_KEY = "AY2Daiz/////AAABmYb00Vop7EAWqs/eRSieR19M5zxWECKfF05bE/xrCZXcvuMIT5zW88kcMPbUb2Bh/yA1O30q1tiOUQBj1TAXbCj4eRSLWWaYrxAYm+0Y1093z7T4uMbD0S+R/JXJAg/Siy8ALkMXiJWDA16H7GmOz1xqSb8v7R77hxcFP82xpmMk3kp4145aqeSzRI2UhyETgYqwAyQB8rtgbfRa0w+iG+A8F47Lwroq2g4PHgVZ5qHv6YDpz2Krw8StYEDoF1PtANTyNPWpGs9aABZakCBlXoZlzixwCoqZHpmS3RrkMyGRER+74aIDk2u+RJOf6DDDa5SHKdpCr24QVrV2W0AwoP6Fvpdm9rfTZ7nYYs7lk7ol";

  // ********** RunOpMode
  // Our execution entry point 
  
  public void runOpMode () 
  {

    // Specify the Vuforia license key that gives us
    // permission to use their software. 
    // Indicate which camera we intend to use. 
    // Bundle this information into a list of parameters.

    VuforiaLocalizer.Parameters parameters = new
                  VuforiaLocalizer.Parameters();

    parameters.vuforiaLicenseKey = VUFORIA_KEY;
    parameters.cameraDirection   = CameraDirection.BACK;

    // Create the Vuforia viewing engine specified by our parameters

    VuforiaLocalizer  myVuforia =     
        ClassFactory.getInstance().createVuforia(parameters);

    // Get the id for the monitor that will be used to view
    // the camera's image so we can see it.
    int tfodMonitorViewId =
          hardwareMap.appContext.getResources().getIdentifier(
          "tfodMonitorViewId", "id", 
          hardwareMap.appContext.getPackageName());
  
    // Create a Rover Ruckus object detector
    TFObjectDetector.Parameters tfodParameters = new 
        TFObjectDetector.Parameters(tfodMonitorViewId);

    // Create the TensorFlow Object Detector
    TFObjectDetector myTfod =  
        ClassFactory.getInstance().createTFObjectDetector(
            tfodParameters, myVuforia);


    // Load the model trained for the Rover Ruckus challenge.
    // It recognizes two objects. Give them meaningful names
    myTfod.loadModelFromAsset("RoverRuckus.tflite", 
          "Gold Mineral", "Silver Mineral");

    // Start up camera and Tensorflow Ojbect Detector. You should
    // be able to see the recognized objects in the monitor
    myTfod.activate();

    // Wait for start 
    telemetry.addData(">", "Press play: 20200824a");
    telemetry.update();
    waitForStart();

    // Keep on looking until time limit reached
    while (opModeIsActive()) 
    {
        // Ask the Tensorflow Object Detector for a list of objects recognized
        // in the current video image.
        List<Recognition> updatedRecognitions = 
                            myTfod.getUpdatedRecognitions();

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
                telemetry.addData ("Status: ", "Gold location error " + error);
            }
            
            // Respond to silever mineral find
            else
            {
                telemetry.addData ("Status: ", "Ignore the silver minerals");
            }            
        }
        telemetry.update();
        sleep (2000);  // Pause 2 seconds so we can read the status
    }
    telemetry.update();
  }
}

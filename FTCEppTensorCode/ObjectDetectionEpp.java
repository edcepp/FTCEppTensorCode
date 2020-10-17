// Copyright (c) 2020 Edward C. Epp. All rights reserved.
//
// This opCode recognizes and displays the names of objects used in 
// FIRST Tech Challenges (FTC) Rover Ruckus. It focuses on a
// simple implementation at the expense of established BKMs (Best Know 
// Methods).
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
//     * Iteration: while and for 
//     * Using objects and classes
//     * Create methods
//
// **************************************************************


package org.firstinspires.ftc.teamcode;

import java.util.List;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

@Autonomous(name = "Detect Rover Ruckus Object", group = "Concept")
//@Disabled

public class ObjectDetectionEpp extends LinearOpMode
{
  private static final String VUFORIA_KEY = "AY2Daiz/////Enter your key";

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
    telemetry.addData(">", "Press play: 20200819");
    telemetry.update();
    waitForStart();

    // Ask the Tensorflow Object Detector for a list of objects recognized
    // in the current video image.
    List<Recognition> updatedRecognitions = 
                            myTfod.getUpdatedRecognitions();

    // Go through the list of recognized objects one-by-one and tell us 
    // what kind of object each is
    for (Recognition recognition : updatedRecognitions) 
    {
        telemetry.addData(">", recognition.getLabel());
    } 
    telemetry.update();
    
    // Pause for 10 seconds so we can read results
    sleep(10000);
  }
}

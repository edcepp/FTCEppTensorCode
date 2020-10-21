/* ***************************************************************************************
 * Copyright (c) 2019 Edward C. Epp. All rights reserved.
 *
 * Ed C. Epp 10-2020
 * Locate Gold and Push
 *
 * Locate the gold mineral using Tensorflow and push it. This is a linear version.
 * Motion characteristics are based on the REV MiniBot Hardware Kit REV-45-1171.
 * Tuning is required to make the current robot perform. The bot does not go 
 * straight because the motors behave significanly differently.
 *
 ******************************************************************************************/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.State;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;
import java.lang.Math.*;

// I don't understand why this is necessary
import static org.firstinspires.ftc.teamcode.LocateGoldAndPushLinearMinniEpp.RoverState.DONE;
import static org.firstinspires.ftc.teamcode.LocateGoldAndPushLinearMinniEpp.RoverState.MOVE_GOLD;
import static org.firstinspires.ftc.teamcode.LocateGoldAndPushLinearMinniEpp.RoverState.TARGET_GOLD;
import static org.firstinspires.ftc.teamcode.LocateGoldAndPushLinearMinniEpp.RoverState.TEST;
import static org.firstinspires.ftc.teamcode.LocateGoldAndPushLinearMinniEpp.RoverState.ERROR;

@Autonomous(name = "Locate Gold And Push Minni", group = "Concept")
//@Disabled
public class LocateGoldAndPushLinearMinniEpp extends LinearOpMode {

    /****************************** constants **************************/
    /****************************** constants **************************/

    private static final String TFOD_MODEL_ASSET     = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL   = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private static final int SCREEN_WIDTH        = 1280;
    private static final int SCREEN_HEIGHT       =  720;
    private static final int POINTING_TOLERANCE  =   50;

//  For REV Minibot Hardware kit REV-45-1171 with
//  REV Core Hex Motor REV-41-1354
//  REV 90 mm Traction Wheel REV-41-1354
//
//  Wheel
    private static final double WHEEL_DIAMETER_MM = 90.0;
    private static final double DISTANCE_PER_ROTATION_MM =
                      WHEEL_DIAMETER_MM * Math.PI;
    private static final double DISTANCE_PER_ROTATION_INCH =
                      DISTANCE_PER_ROTATION_MM / 25.4;

//  Motor
    private static final double MAX_RPM             = 125.0; // no load
    private static final double MAX_RPS             = MAX_RPM / 60.0;
    private static final double COUNTS_PER_ROTATION = 288.0;
    private static final double MAX_COUNTS_PER_SEC  =
                      MAX_RPS * COUNTS_PER_ROTATION;  // velocity no load 
    private static final double TARGET_COUNTS_PER_SEC = 
                      MAX_COUNTS_PER_SEC * 2.0 / 3.0;

    // Compute turn and distances
    private static final int TURN_DIVIDE         =   13; // by Trial and error
    private static final double COUNTS_PER_INCH   =
           COUNTS_PER_ROTATION / DISTANCE_PER_ROTATION_INCH;
    private static final int COUNTS_TO_TARGET    = (int)(COUNTS_PER_INCH * 12.0);


     enum RoverState
     {
        TARGET_GOLD,
        MOVE_GOLD,
        DONE,
        TEST,
        ERROR,
    }

    // This Vuforia key is for exclusive use by Ed C. Epp
    private static final String VUFORIA_KEY = "AY2Daiz/////AAABmYb00Vop7EAWqs/eRSieR19M5zxWECKfF05bE/xrCZXcvuMIT5zW88kcMPbUb2Bh/yA1O30q1tiOUQBj1TAXbCj4eRSLWWaYrxAYm+0Y1093z7T4uMbD0S+R/JXJAg/Siy8ALkMXiJWDA16H7GmOz1xqSb8v7R77hxcFP82xpmMk3kp4145aqeSzRI2UhyETgYqwAyQB8rtgbfRa0w+iG+A8F47Lwroq2g4PHgVZ5qHv6YDpz2Krw8StYEDoF1PtANTyNPWpGs9aABZakCBlXoZlzixwCoqZHpmS3RrkMyGRER+74aIDk2u+RJOf6DDDa5SHKdpCr24QVrV2W0AwoP6Fvpdm9rfTZ7nYYs7lk7ol";

    /****************************** member variables *******************/
    /****************************** member variables *******************/

    // Stores the apps execution state
    // private RobotState myRobotState = TEST;
    private RoverState myRobotState = TARGET_GOLD;

    // The Vuforia localization engine.
    private VuforiaLocalizer myVuforia = null;

    // The Tensor Flow Object Detection engine.
    private TFObjectDetector myTfod = null;

    // links to the physical robot driver motors
    DcMotorEx myLeftMotor  = null;
    DcMotorEx myRightMotor = null;

    /****************************** runOpMode **************************/
    /****************************** runOpMode **************************/
    /****************************** runOpMode **************************/
    // The robot execution loop and state machine
    @Override
    public void runOpMode() 
    {
        initRobot();

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking: 20201021b");
        telemetry.update();
        waitForStart();

        // Main Linear OpMod
        while (opModeIsActive()) 
        {
            switch (myRobotState) 
            {
                case TARGET_GOLD:
                    targetGold();
                    break;
                case MOVE_GOLD:
                    moveToGold();
                    break;
                case DONE:
                    shutdown();
                    break;
                case TEST:
                    moveFor(200,200);
                    moveFor(-200, 200);
                    myRobotState = DONE;
                    break;
                case ERROR:
                    myRobotState = DONE;
                    break;
                default: 
                {
                    telemetry.addData("Error", "This program should never be here");
                    myRobotState = ERROR;
                }
            }
        }
    }

    //****************************** initRobot **************************
    //****************************** initRobot **************************
    // Initialize the Vuforia Localization Engine, TensorFlow Object Detection, and motors.
    // Vuforia is required for the cameras

    private void initRobot() 
    {
        initVuforia();

        if (myRobotState != ERROR) 
        {
            initTfod();
        }

        if (myRobotState != ERROR) 
        {
            initMotors();
        }
        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    // ********** initVuforia helper
    // Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
    // Configure the phone to use the rear camera.

    private void initVuforia() 
    {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        myVuforia = ClassFactory.getInstance().createVuforia(parameters);

        if (myVuforia == null)
        {
            myRobotState = ERROR;
            telemetry.addData("ERROR", "the Vuforia engine did not initialize");
        }
    }

    // ********** initTfod helper
    // Initialize the Tensor Flow Object Detection engine.

    private void initTfod() 
    {
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) 
        {
            int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                    "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
            TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
            myTfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, myVuforia);
            myTfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);

            if (myTfod != null) 
            {
                myTfod.activate();
            } 
            else 
            {
                telemetry.addData("ERROR", "TensorFlow lite did not activate");
                myRobotState = ERROR;
            }
        }

        else 
        {
            telemetry.addData("ERROR", "This device is not compatible with TFOD");
            myRobotState = ERROR;
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

    //****************************** targetGold *************************
    //****************************** targetGold  ************************
    // Turn the robot to face the gold mineral
    private void targetGold () 
    {
        Recognition goldPiece = null;

        // Return without changing state if there is no new information.
        List<Recognition> updatedRecognitions = myTfod.getUpdatedRecognitions();
        if (updatedRecognitions != null) 
        {
            // Find the first gold piece if there is one
            telemetry.addData("State: ", "Target Gold");
            for (Recognition recognition : updatedRecognitions) 
            {
                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) 
                {
                    goldPiece = recognition;
                    break;
                }
            }

            // we found one
            if (goldPiece != null) 
            {
                int goldMineralLeftX = (int) goldPiece.getLeft();
                int goldMineralRightX = (int) goldPiece.getRight();
                int goldMineralCenterX = (goldMineralLeftX + goldMineralRightX) / 2;
                int offset = goldMineralCenterX - SCREEN_WIDTH / 2;

                if (Math.abs(offset) < POINTING_TOLERANCE) 
                {
                    myRobotState = MOVE_GOLD;
                } 
                else 
                {
                    telemetry.addData("Action: ", "Turn " + offset);
                    telemetry.update();
                    int turn_clicks = offset / TURN_DIVIDE;
                    moveFor(turn_clicks, -turn_clicks);
                }
            } 
            else 
            {
                telemetry.addData("Status: ", "No gold found");
                telemetry.update();
            }
        } 
        else 
        {
            idle();
        }
    }

    /****************************** moveToGold *************************/
    /****************************** moveToGold *************************/
    // Move forward CLICKS_TO_TARGET
    private void moveToGold()
    {
        telemetry.addData("State: ", "Moving to Gold");
        telemetry.update();

        moveFor(COUNTS_TO_TARGET, COUNTS_TO_TARGET);

        myRobotState = DONE;
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
        myLeftMotor.setVelocity(TARGET_COUNTS_PER_SEC);
        myRightMotor.setVelocity(TARGET_COUNTS_PER_SEC);
        while (opModeIsActive() && myRightMotor.isBusy()) 
        {
            idle();
        }
    }

    /****************************** shutdown ***************************/
    /****************************** shutdown ***************************/
    // Turn the motor power off and shutdown the TensorFlow Object Detection Engine
    public void shutdown()
    {
        telemetry.addData("Status: ", "Shutdown");

        myLeftMotor.setPower(0.0);
        myRightMotor.setPower(0.0);

        if (myTfod != null)
        {
            myTfod.shutdown();
        }

        telemetry.update();
    }
}

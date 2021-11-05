/* ***************************************************************************************
 * Copyright (c) 2021 Edward C. Epp. All rights reserved.
 *
 * Ed C. Epp 9-2021
 * Test Motors
 *
 * This program has two goals: 
 *   Test to determine whether the robot setup and programming support motors
 *   Illustrate how to write a complete robot program
 *
 * The following actions are demostrated
 *   Display the program in the driver station's available autonomous program's list
 *       Group the program with other "Concept" programs
 *   Start the program when INIT is pressed
 *   Tell the user it is ready and wait for the user to press start (right arrow)
 *   Configure the two motors when start is pressed
 *   Run the motors at 50% power until 
 *       30 seconds have passed or 
 *       the user presses stop  
 *   Unpower the motors
 *   Shutdown the program
 *
 * The goal of this program is to get you started. It does not attempt to explain ever
 * word and semicolon. You can learn those details along with how all the mechanics
 * of getting the programming loaded into the robot in other tutorials. This program can
 * be used as a starting point and modified to do more interesting actions. 
 *   
 ******************************************************************************************/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Autonomous (name = "Epp motor test", group= "Concept")
// @Disabled

public class TestMotorEpp extends LinearOpMode 
{
    private DcMotor motor;
    
    /***************************** runOpMode ****************************
     * Execution begins here
     * Initialize the TensorFlow Object Detection engine.
    ********************************************************************/
    @Override 
    public void runOpMode () 
    {
        // initialize the robot and wait for the user to press start
        telemetry.addData(">", " To start press play");
        telemetry.update(); 
        waitForStart();

        // fire up the motors to turn forward at 50%
        DcMotor myLeftMotor  = hardwareMap.get(DcMotorEx.class, "myLeftMotor");
        DcMotor myRightMotor = hardwareMap.get(DcMotorEx.class, "myRightMotor");
        myLeftMotor.setPower(-0.5);
        myRightMotor.setPower(0.5);
        
        // Let the motors run for 30 seconds or until stop pressed
        while (opModeIsActive())
        {
        }
        
        // Shut everything down
        myLeftMotor.setPower(0);
        myRightMotor.setPower(0);
    }
}

# TensorCode for FTC Competitions

LinkedIn has identified the Machine Learning Engineer as one of the five top emerging jobs in 2018. The top skills include Deep Learning, Machine Learning, and TensorFlow. QualComm has integrated it into the FTC STK giving our teams an opportunity to learn and leverage this emerging technology.  This tutorial shows you how to write the java code required to locate objects using TensorFlow. Once an object of interest is located the program computes the horizontal offset from the center of the screen. Future tutorials will use this offset to move the robot toward the targeted game piece.

Example code illustrates using TensorFlow for the Rover Ruckus challenge. It is easily modified to recognize objects for any current challenge. My goal is to show you how Tensorflow can be applied to FTC. It is your job to do those modification. 

![Skystone Recognition](SkystoneRec.png)
![Move Gold Mineral Steps](Steps.png)

<img src="SkystoneRec.png"/>

## Object Detection with TensorFlow 

### Overview

The material in this section supports 
- Recognizing the object of interest: Step 1
- Computing the offset of the object from the horizontal center of the display screen to support turning the robot toward the object: Step 2
- Understanding recognition confidence
- Exploring issues that may impact confidence
- Managing error recovery
- Making the code easier to manage by using methods

### Tutorial Video

The YouTube tutorial video can be found at:

- [Tutorial Video](https://www.youtube.com/watch?v=Cd2PYhapyvw)

A YouTube demo of the TensorFlow code in action can be found at:

- [Locate Gold Mineral And Push Demo Video](https://youtu.be/GBGK6WiEGt4)

### Presentation Slides

The PowerPoint slides used in the tutorial to explaining object detection is found in this GitHub directory in the PDF file FTCOjectDetectionWOffset30.pdf.

- [Tutorial Slides](https://drive.google.com/file/d/17m7KTk_3dXme-aBUYMyznHNLZDZ3_Imc/view?usp=sharing)

### Code

The TensorFlow object detection and turn offset computation Java code is found in this GitHub directory in the file ObjectDetetionEpp.java

  - [ObjectDetectionEpp.java](https://github.com/edcepp/FTCEppTensorCode/blob/master/FTCEppTensorCode/ObjectDetectionEpp.java)

  - [ComputeTurnEpp.java](https://github.com/edcepp/FTCEppTensorCode/blob/master/FTCEppTensorCode/ComputeTurnEpp.java)


## Orient Robot Toward Gold Mineral

### Overview

The code in this section turns the robot towards the gold mineral. This represents Steps 1) and 2) in the diagram above. The tutorial videos and slides have not been constructed.

### Code

  - [OrientToGoldEpp.java](https://github.com/edcepp/FTCEppTensorCode/blob/master/FTCEppTensorCode/OrientToGoldEpp.java)

## Move the Robot to the Gold Mineral using a State Machine 

### Overview

The code in this section completes the goals to point Rover Ruckus challenge to move the gold mineral off of its location. In completes steps 1), 2) and 3) in the diagram above.

### Code
 
  - [LocateGoldAndPushLinearMinninEpp.java](https://github.com/edcepp/FTCEppTensorCode/blob/master/FTCEppTensorCode/LocateGoldAndPushLinearMinniEpp.java)


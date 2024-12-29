package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Testing OpMode", group="Linear OpMode")
public class Experiment extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightFront = null;
    private DcMotor rightBack = null;
    private DcMotor rightElevator = null;
    private DcMotor leftElevator = null;
    private Servo rightElevatorServo = null;
    private Servo leftElevatorServo = null;
    private Servo masterClaw = null;
    private Servo clawRotation = null; // New servo variable
    private double speedMultiplier = 0.3; // Speed multiplier with default value

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Sigma skibidi");
        telemetry.update();

        // Initialize hardware variables
        leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightElevator = hardwareMap.get(DcMotor.class, "rightElevator");
        leftElevator = hardwareMap.get(DcMotor.class, "leftElevator");
        rightElevatorServo = hardwareMap.get(Servo.class, "rightElevatorServo");
        leftElevatorServo = hardwareMap.get(Servo.class, "leftElevatorServo");
        masterClaw = hardwareMap.get(Servo.class, "masterClaw");
        clawRotation = hardwareMap.get(Servo.class, "clawRotation"); // this init the new servo

        rightElevator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftElevator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightElevator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftElevator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rightElevator.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftElevator.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        rightElevator.setDirection(DcMotor.Direction.FORWARD);
        leftElevator.setDirection(DcMotor.Direction.REVERSE);

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // Set initial servo positions
        rightElevatorServo.setPosition(0.75); // Initial position for right elevator servo
        leftElevatorServo.setPosition(0.25);  // Initial position for left elevator servo
        masterClaw.setPosition(0);      // Initial position for master claw
        clawRotation.setPosition(0.47);       // Initial position for claw rotation

        telemetry.addData("Status", "Sigmalicious Skibidi Ready for Launch");
        telemetry.speak("Sigmalicious Skibidi Ready for Launch");

        telemetry.update();

        waitForStart();
        runtime.reset();

        // Set servo positions after game starts
        rightElevatorServo.setPosition(0.5);
        leftElevatorServo.setPosition(0.5);
        clawRotation.setPosition(0.47);//for vertical samples and rest state

        while (opModeIsActive()) {
            // Change speed multiplier based on right trigger
            speedMultiplier = (gamepad1.left_trigger > 0.1 ? 1.0 : 0.3);

            // Mecanum wheel drive calculations
            double drive = -gamepad1.left_stick_y; // Forward/Backward
            double strafe = gamepad1.left_stick_x; // Left/Right
            double turn = gamepad1.right_stick_x; // Turning

            if (gamepad2.left_stick_y != 0 || gamepad2.left_stick_x != 0) {
                drive = -gamepad2.left_stick_y * 0.4; // 0.1 power for all drive motors
                strafe = gamepad2.left_stick_x * 0.4;
                turn = 0; // No turning action for gamepad 2
                //this is so that there can be more fine action from player 2 than player 1 controlling the entire thing
            }

            // Calculate power for each wheel
            double leftFrontPower = Range.clip((drive + strafe + turn) * speedMultiplier, -1.0, 1.0);
            double rightFrontPower = Range.clip((drive - strafe - turn) * speedMultiplier, -1.0, 1.0);
            double leftBackPower = Range.clip((drive - strafe + turn) * speedMultiplier, -1.0, 1.0);
            double rightBackPower = Range.clip((drive + strafe - turn) * speedMultiplier, -1.0, 1.0);

            // Send calculated power to wheels
            leftFront.setPower(leftFrontPower);
            rightFront.setPower(rightFrontPower);
            leftBack.setPower(leftBackPower);
            rightBack.setPower(rightBackPower);

            // Elevator control code
            int rightElevatorPosition = rightElevator.getCurrentPosition();
            int leftElevatorPosition = leftElevator.getCurrentPosition();

            if (gamepad1.right_bumper && rightElevatorPosition < 2350 && leftElevatorPosition < 2350) {
                // Raise elevator and also tune for new Misumi and new ultra planetary gears.
                rightElevator.setPower(1.0);
                leftElevator.setPower(1.0);
            } else if (gamepad1.left_bumper && rightElevatorPosition > 60 && leftElevatorPosition > 60) {
                // Lower elevator
                rightElevator.setPower(-0.9);
                leftElevator.setPower(-0.9);
            } else {
                rightElevator.setPower(0.0);
                leftElevator.setPower(0.0);
            }

            // Claw rotation control with gamepad2 right joystick
            double clawIncrement = 0.01; // this how much the increment increases by.
            double rightStickX = gamepad2.right_stick_x;

            if (rightStickX > 0.1) {
                // rotate right
                clawRotation.setPosition(Range.clip(clawRotation.getPosition() - clawIncrement, 0.0, 1.0));
            } else if (rightStickX < -0.1) {
                // rotates left
                clawRotation.setPosition(Range.clip(clawRotation.getPosition() + clawIncrement, 0.0, 1.0));
            }

            if (gamepad1.dpad_left || gamepad2.dpad_left) {
                clawRotation.setPosition(0.64);//diagonal left
            } else if (gamepad1.dpad_right || gamepad2.dpad_right) {
                clawRotation.setPosition(0.27); // diagonal right
            } else if ((gamepad1.dpad_down || gamepad2.dpad_down)) {
                clawRotation.setPosition(0.47); //reset to legal point
            } else if ((gamepad1.dpad_up || gamepad2.dpad_up)) {
                clawRotation.setPosition(0.8); //Horizontal pickup
            }

            if (gamepad1.a || gamepad2.a) {
                masterClaw.setPosition(0.5);
                // Rumble both gamepads
                gamepad1.rumble(0.5, 0.5, 100); // Left and right rumbling thing n the controller at full strength for 1 sec cuz why not
                gamepad2.rumble(0.5, 0.5, 100);
            } else {
                masterClaw.setPosition(0.0);//grip of the claw
            }

            if (gamepad1.b || gamepad2.b) {
                // reset everything and go to default position
                rightElevatorServo.setPosition(0.52);
                leftElevatorServo.setPosition(0.48);
                clawRotation.setPosition(0.47);
            }

            if (gamepad1.y || gamepad2.y) {
                // Move servos to specific positions. This is the hover point
                rightElevatorServo.setPosition(0.27);//real low to hover. Make higher to hover higher and make lower to hover lower
                leftElevatorServo.setPosition(0.73);//these two numbers should always add up to hundred. otherwise u are breaking the servos
            }

            if (gamepad1.x || gamepad2.x) {
                // Check if servos are in the correct positions for to perform a grab
                //so if y is pressed and then x is pressed it performs a grab.
                if (rightElevatorServo.getPosition() == 0.27 && leftElevatorServo.getPosition() == 0.73) {
                    performGrab();
                }
            }

            // Telemetry data
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "leftFront (%.2f), rightFront (%.2f)", leftFrontPower, rightFrontPower);
            telemetry.addData("Motors", "leftBack (%.2f), rightBack (%.2f)", leftBackPower, rightBackPower);
            telemetry.addData("/nAccuracy Mode Speed", speedMultiplier);
            telemetry.addData("/nElevator Position", "Right: %d, Left: %d", rightElevatorPosition, leftElevatorPosition);
            telemetry.addData("/nClaw Position", clawRotation.getPosition());
            telemetry.update();
        }
    }
    private void performGrab() {
        ElapsedTime timer = new ElapsedTime();

        // Open masterClaw to position 0.4
        masterClaw.setPosition(0.44);
        timer.reset();
        while (timer.seconds() < 0.05 && opModeIsActive()) {
            // Wait for 0.1 seconds
            telemetry.addData("Grab Step", "Opening Claw: %.2f", timer.seconds());
            telemetry.update();
        }

        // Move servos to new positions
        rightElevatorServo.setPosition(0.23);
        leftElevatorServo.setPosition(0.77);
        timer.reset();
        while (timer.seconds() < 0.1 && opModeIsActive()) {
            // Wait for 0.5 second
            telemetry.addData("Grab Step", "Moving Servos: %.2f", timer.seconds());
            telemetry.update();
        }

        // Close masterClaw to position 0
        masterClaw.setPosition(0);

        // Wait until the claw is closed
        while (masterClaw.getPosition() != 0 && opModeIsActive()) {
            telemetry.addData("Grab Step", "Closing Claw");
            telemetry.update();
        }

        // Wait for 0.3 seconds before setting servos
        timer.reset();
        while (timer.seconds() < 0.3 && opModeIsActive()) {
            telemetry.addData("Grab Step", "Waiting before setting servos: %.2f", timer.seconds());
            telemetry.update();
        }

        // Set right and left servo positions to 1 and 0 respectively
        rightElevatorServo.setPosition(0.3);
        leftElevatorServo.setPosition(0.7);
//        clawRotation.setPosition(0.47);
        masterClaw.setPosition(0);
    }
}
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="BadWolf Teleop", group="Linear OpMode")
public class BadWolfTeleOp extends LinearOpMode {
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
    private boolean masterClawPosition = false;
    private double speedMultiplier = 1.0; // Speed multiplier


    @Override
    public void runOpMode() {
        telemetry.addData("Status", "No status for you");
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
        clawRotation = hardwareMap.get(Servo.class, "clawRotation"); //

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
        rightElevatorServo.setPosition(1); // Initial position for right elevator servo
        leftElevatorServo.setPosition(0);  // Initial position for left elevator servo
        masterClaw.setPosition(1);      // Initial position for master claw. Open cuz we hit 18in if closed.
        clawRotation.setPosition(0);       // Initial position for claw rotation

        telemetry.addData("Status", "Ready To Start");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // Change speed multiplier based on right trigger
            speedMultiplier = gamepad1.right_trigger > 0.1 ? 0.3 : 1.0;

            // Mecanum wheel drive calculations
            double drive = -gamepad1.left_stick_y; // Forward/Backward
            double strafe = gamepad1.left_stick_x; // Left/Right
            double turn = gamepad1.right_stick_x; // Turning

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

            if (gamepad1.right_bumper && rightElevatorPosition < 2000 && leftElevatorPosition < 2000) {
                // Raise elevator and also tune for new Misumi and new ultra planetary gears.
                rightElevator.setPower(0.7);//elevator power when going up.
                leftElevator.setPower(0.7); //elevator power when going down.
            } else if (gamepad1.left_bumper && rightElevatorPosition > 0 && leftElevatorPosition > 0) {
                // Lower elevator
                rightElevator.setPower(-0.7);
                leftElevator.setPower(-0.7);
            } else {
                // Stall and hold position, still have to complete
                rightElevator.setPower(0.00000005);
                leftElevator.setPower(0.000000005);
            }

            // Claw rotation control
            if (gamepad1.dpad_left || gamepad2.dpad_left) {
                clawRotation.setPosition(0.35);
            } else if (gamepad1.dpad_right || gamepad2.dpad_right) {
                clawRotation.setPosition(0);
            }

            // Existing code for servos and claw control
            if (gamepad1.left_trigger > 0.1 || gamepad2.left_trigger > 0.1) { // gamepad 1 master control arm
                rightElevatorServo.setPosition(0.4); // Lower the right elevator servo
                leftElevatorServo.setPosition(0.6);  // Lower the left elevator servo
            } else {
                rightElevatorServo.setPosition(0.6); // Reset right elevator servo
                leftElevatorServo.setPosition(0.4);  // Reset left elevator servo
            }

            if (gamepad1.a || gamepad2.a) {
                masterClaw.setPosition(0.4);
            } else {
                masterClaw.setPosition(0);
            }

            // Telemetry data
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "leftFront (%.2f), rightFront (%.2f)", leftFrontPower, rightFrontPower);
            telemetry.addData("Motors", "leftBack (%.2f), rightBack (%.2f)", leftBackPower, rightBackPower);
            telemetry.addData("Accuracy Mode Speed", speedMultiplier);
            telemetry.addData("Elevator Position", "Right: %d, Left: %d", rightElevatorPosition, leftElevatorPosition);
            telemetry.update();
        }
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Specimen Technique TeleOp", group="Linear OpMode")
public class SpecimenTrial extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFront = null;
    private boolean grip = true;
    private DcMotor leftBack = null;
    private DcMotor rightFront = null;
    private DcMotor rightBack = null;
    private DcMotor rightElevator = null;
    private DcMotor leftElevator = null;
    private Servo rightElevatorServo = null;
    private Servo leftElevatorServo = null;
    private Servo masterClaw = null;
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

        rightElevator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftElevator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightElevator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftElevator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rightElevator.setDirection(DcMotor.Direction.REVERSE);
        leftElevator.setDirection(DcMotor.Direction.FORWARD);

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // Set initial servo positions
        rightElevatorServo.setPosition(1); // Initial position for right elevator servo
        leftElevatorServo.setPosition(0);  // Initial position for left elevator servo
        masterClaw.setPosition(0.35);         // Initial position for master claw

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

            if (gamepad1.right_bumper && rightElevatorPosition < 4800 && leftElevatorPosition < 4800) {
                // Raise elevator
                rightElevator.setPower(1.0);
                leftElevator.setPower(1.0);
            } else if (gamepad1.left_bumper && rightElevatorPosition > 20 && leftElevatorPosition > 20) {
                // Lower elevator
                rightElevator.setPower(-0.7);
                leftElevator.setPower(-0.7);
            } else {
                rightElevator.setPower(0);
                leftElevator.setPower(0);
            }

            // Existing code for servos and claw control
            if (gamepad1.left_trigger > 0.1 || gamepad2.left_trigger > 0.1) { // gamepad 1 master control arm
                rightElevatorServo.setPosition(0.3); // Lower the right elevator servo
                leftElevatorServo.setPosition(0.7);  // Lower the left elevator servo
            } else {
                rightElevatorServo.setPosition(0.7); // Reset right elevator servo
                leftElevatorServo.setPosition(0.3);  // Reset left elevator servo
            }


            if (gamepad1.a || gamepad2.a) {
                masterClaw.setPosition(0.8);
            } else {
                masterClaw.setPosition(0.3);
            }

            // Telemetry data
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "leftFront (%.2f), rightFront (%.2f)", leftFrontPower, rightFrontPower);
            telemetry.addData("Motors", "leftBack (%.2f), rightBack (%.2f)", leftBackPower, rightBackPower);
            telemetry.addData("Accuracy Mode Speed", speedMultiplier);
            telemetry.addData("Elevator Position", "Right: %d, Left: %d",
                    rightElevatorPosition, leftElevatorPosition);
            telemetry.update();
        }
    }
}
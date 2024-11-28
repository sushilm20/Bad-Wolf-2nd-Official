package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Robot: Auto Drive By Time", group="Robot")
public class Auto extends LinearOpMode {

    /* Declare OpMode members. */
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
    private boolean masterClawPosition = false;

    static final double FORWARD_SPEED = 0.6;
    static final double TURN_SPEED    = 0.5;

    @Override
    public void runOpMode() {

        // Initialize the drive system variables.
        leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        rightElevator = hardwareMap.get(DcMotor.class, "rightElevator");
        leftElevator = hardwareMap.get(DcMotor.class, "leftElevator");
        rightElevatorServo = hardwareMap.get(Servo.class, "rightElevatorServo");
        leftElevatorServo = hardwareMap.get(Servo.class, "leftElevatorServo");
        masterClaw = hardwareMap.get(Servo.class, "masterClaw");

        // Set motor directions
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // Set initial servo positions
        rightElevatorServo.setPosition(1); // Initial position for right elevator servo
        leftElevatorServo.setPosition(0);  // Initial position for left elevator servo
        masterClaw.setPosition(0.35);         // Initial position for master claw

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // Step 1: Spin right for 0.8 seconds
        leftFront.setPower(-TURN_SPEED);
        leftBack.setPower(-TURN_SPEED);
        rightFront.setPower(TURN_SPEED);
        rightBack.setPower(TURN_SPEED);
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 1)) {
            telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds());
            telemetry.update();
        }

        // Step 2: Drive forward for 1 second
        leftFront.setPower(FORWARD_SPEED);
        leftBack.setPower(FORWARD_SPEED);
        rightFront.setPower(FORWARD_SPEED);
        rightBack.setPower(FORWARD_SPEED);
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 0.8)) {
            telemetry.addData("Path", "Leg 2: %4.1f S Elapsed", runtime.seconds());
            telemetry.update();
        }

        // Step 3: Stop and adjust master claw position
        leftFront.setPower(0);
        leftBack.setPower(0);
        rightFront.setPower(0);
        rightBack.setPower(0);
        masterClaw.setPosition(0.8); // Set master claw to 0.8

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);
    }
}
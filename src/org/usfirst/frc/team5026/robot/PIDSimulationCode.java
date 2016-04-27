package org.usfirst.frc.team5026.robot;

import java.util.Scanner;

import javax.management.timer.Timer;




/*
 * FAKE ENCODER (2)
 * FAKE MOTOR (6)
 * 
 * FAKE ENCODER PORTS
 * FAKE MOTOR PORTS
 * 
 * NEED TO CREATE DIFFERENT CLASSES FOR ENCODERS
 */
public class PIDSimulationCode {
	static PIDWithMotors motors;
	static double time = 0001;
	
	public PIDSimulationCode () {
	}
	public static void main (String[] args) throws InterruptedException {
		motors = new PIDWithMotors(0.01,1,0,0,0);
		String sr = "";
		long startTime = System.nanoTime();
		while (true) {
			motors.setSpeed(1000);
			System.out.println("START!");
			Scanner userInput = new Scanner(System.in);
			System.out.print("Target: ");
			int target = userInput.nextInt();
			motors.setTarget(target);
			startTime = System.nanoTime();
			
			while (motors.getError() != 0) {
				sr = "";
				sr += "Error: " + motors.getError();
				sr += "\tTarget: " + target;
				sr += "\tCurrent Speed: " + motors.getVoltage() * motors.maxRPM;
				sr += "\tWanted Speed: " + motors.getNewSpeed();
				sr += "\tCurrent Voltage: " + motors.getVoltage();
				System.out.println(sr);
				time = (System.nanoTime() - startTime) * 1e-9;
				motors.changeMotorSpeed(motors.getNewSpeed(), time / 10);
				if (motors.smoothing()) {
					System.out.println("END");
					break;
				}
				Thread.sleep(500);
				
			}
		}
	}
	
}
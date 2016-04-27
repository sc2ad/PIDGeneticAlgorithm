package org.usfirst.frc.team5026.robot;

import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PIDWithMotors {
	
	double p;
	double i;
	double d;
	double f;
	int target = 1000;
	int motor; //RN this is just the current speed, will add an object later
	double error = 14000;
	double totalError = 0;
	double prevError = 14000;
	double maxRPM = 10000.0;
	double voltage = 0.0;
	double wantedVoltage = 0.0;
	
	double alpha = 0.5;
	int currentExponentialError = 45000;
	int lastExponentialError = 45000;
	double range = 200;
	
	public PIDWithMotors(double p, double i, double d, double f, int speedOfMotorTest) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		this.motor = speedOfMotorTest;
		this.voltage = speedOfMotorTest/maxRPM;
		this.wantedVoltage = voltage;
	}
	
	
	public void setTarget(int trg) {
		target = trg;
	}
	public double getError() {
		error = target - motor;
		totalError += error;
		return error;
	}
	public double getNewSpeed() {
		
		if (p != 0 || i != 0 || d != 0 || f != 0) {
			return p * getError() + i * totalError + d * (error - prevError) + f * target;
		}
		return target;
	}
	public double getVoltage() {
		voltage = motor / maxRPM; //current speed
		return voltage;
	}
	public double getWantedVoltage() {
		wantedVoltage = getNewSpeed() / maxRPM; //wanted speed
		return wantedVoltage;
	}
	public void setSpeed(double speed) {
		motor = (int) (speed);
	}
	public void changeMotorSpeed(double speed, double time) {
		// Time should be 1.0 when motor reaches 0.5 voltage (5000 RPM)
		motor = (int) (speed * time);
		if (speed * time >= maxRPM) motor = (int) maxRPM;
		if (speed * time <= -maxRPM) motor = (int) -maxRPM;
	}
	public boolean smoothing() {
		currentExponentialError = (int) (alpha * Math.abs(getError()) + (1 - alpha) * lastExponentialError);
    	
    	if(Math.abs(currentExponentialError) < range) {
    		System.out.println("Stable!: " + currentExponentialError + " < " + range);
    		return true;
    	}
    	
    	lastExponentialError = currentExponentialError;
    	return false;
	}
}

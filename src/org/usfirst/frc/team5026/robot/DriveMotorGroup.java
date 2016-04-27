package org.usfirst.frc.team5026.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.PIDSubsystem;

/**
 * For use with drive base motors since one motor is flipped.
 */

public class DriveMotorGroup extends PIDSubsystem implements SpeedController, PIDSource {
	
    double out = 0.0;
    
    private PIDSourceType pidsource = PIDSourceType.kRate;
    
    private Timer timer;
    
    boolean isInverted;
    
    public DriveMotorGroup(double p, double i, double d, double f, boolean isInverted) {
    	super(p, i, d, f);
    	this.isInverted = isInverted;
    	timer.reset();
    }
    
    public double get() {
    	return out;
    }
    
    public void set(double speed, byte syncGroup) {
        set(isInverted ? -speed : speed);
    }
    
    public void set(double speed) {
    	/*
    	motor1.set(-speed); // Top motor in gearbox
    	motor2.set(speed);
    	motor3.set(speed);
    	*/
    	this.enable();
    	this.setInputRange(-1, 1);
    	this.setSetpoint(speed);
    	this.setOutputRange(-1, 1);
    	this.setPercentTolerance(0.05);
    }
    
    public void setInverted(boolean isRightSide) {
    	isInverted = isRightSide;
    }

    public boolean getInverted() {
    	return isInverted;
    }
    
    public void disable() {
    	System.out.println("DISABLED!");
    	this.getPIDController().disable();
    	timer.stop();
    	timer.reset();
    }
    
    public void pidWrite(double output) {
    	System.out.println(out);
    }

    public void initDefaultCommand() {}

	public void stopMotor() {}

	@Override
	protected double returnPIDInput() {
		// TODO Auto-generated method stub
		return this.pidGet();
	}

	@Override
	protected void usePIDOutput(double output) {
		// TODO Auto-generated method stub
		System.out.println("Motor Out: "+output);
	}

	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		// TODO Auto-generated method stub
		pidsource = pidSource;
	}

	@Override
	public PIDSourceType getPIDSourceType() {
		// TODO Auto-generated method stub
		return pidsource;
	}

	public void startPid() {
		timer.reset();
		timer.start();
	}
	@Override
	public double pidGet() {
		// TODO Auto-generated method stub
		// TIME FOR REGRESSIONS
		
		
		double timeScale = 0.5/out;
		double output = 0;
		output = timer.get() * timeScale * 100;
		return output;
	}
}


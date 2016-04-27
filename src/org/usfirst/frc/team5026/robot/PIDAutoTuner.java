package org.usfirst.frc.team5026.robot;

import org.jgap.*;
import org.jgap.impl.*;

public class PIDAutoTuner {
	
	static PIDWithMotors motors;
	static double time = 1;
	
	static int maxCounts = 1000;
	static int counts = 0;
	
	static double p = 0;
	static double i = 0;
	static double d = 0;
	static double f = 0;
	
	static double fitness = 0;
	
	public static void main(String args[]) throws InterruptedException {
		int numEvolutions = 500;
	    Configuration gaConf = new DefaultConfiguration();
	    gaConf.setPreservFittestIndividual(true);
	    gaConf.setKeepPopulationSizeConstant(false);
	    Genotype genotype = null;
	    int chromeSize = 16;
	    double maxFitness = Math.pow(2.0, (double) chromeSize) - 1;
	    if (chromeSize > 32) {
	    	System.err.println("This example does not handle Chromosomes greater than 32 bits in length.");
		    System.exit( -1);
	    }
	    try {
	        IChromosome sampleChromosome = new Chromosome(gaConf, new BooleanGene(gaConf), chromeSize); //NEED TO FIX 2nd PARAMETER
	        gaConf.setSampleChromosome(sampleChromosome);
	        gaConf.setPopulationSize(20);
	        gaConf.setFitnessFunction(new ()); //WILL BE MY COOL FUNCTION HERE
	        genotype = Genotype.randomInitialGenotype(gaConf);
	    }
	    catch (InvalidConfigurationException e) {
	        e.printStackTrace();
	        System.exit( -2);
	    }
	    int progress = 0;
	    int percentEvolution = numEvolutions / 100;
	    
	    
		// LOOP FOR EACH GENOTYPE
	    
	    for (i = 0; i < numEvolutions; i ++) {
	    	genotype.evolve();
	    	if (percentEvolution > 0 && i % percentEvolution == 0) {
	            progress++;
	            IChromosome fittest = genotype.getFittestChromosome();
	            double fitness = fittest.getFitnessValue();
	            System.out.println("Currently fittest Chromosome has fitness " + fitness);
	            if (fitness >= maxFitness) {
	            	break;
	            }
	    	}
	    	
	    	
	    	// ALL THIS GOES IN THE GENOTYPE CLASS
			motors = new PIDWithMotors(p,i,d,f,0);
			String sr = "";
			long startTime = System.nanoTime();
			motors.setSpeed(0);
			System.out.println("START!");
			int target = 8850;
			motors.setTarget(target);
			startTime = System.nanoTime();
			
			fitness = 0;
			
			while (counts <= maxCounts) {
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
				counts++;
			}
			fitness = 1 / motors.totalError * 1000; // 1000 is amazing: tiny is bad!
			System.out.println("HAS COMPLETED!");
	    }
	    IChromosome fittest = genotype.getFittestChromosome();
	    System.out.println("Fittest Chromosome has fitness " + fittest.getFitnessValue());
	}

}

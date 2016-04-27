package org.usfirst.frc.team5026.robot;

import org.jgap.*;
import org.jgap.impl.*;

public class PIDAutoTuner {
	
	static PIDWithMotors motors;
	static double time = 1;
	
	static int maxCounts = 100;
	static int counts = 0;
	
	static double p = 0;
	static double i = 0;
	static double d = 0;
	static double f = 0;
	
	static int target = 8850;
	
	static double fitness = 0;
	static double maxFitness = 0.25;
	static int numEvolutions = 500;
	static int population = 100;
	
	
	public static void main(String args[]) throws InterruptedException, InvalidConfigurationException {
		
	    Configuration gaConf = new DefaultConfiguration();
	    gaConf.setPreservFittestIndividual(true);
	    gaConf.setKeepPopulationSizeConstant(false);
	    Genotype genotype = null;
	    Gene[] sampleGenes = new Gene[ 4 ];

	    sampleGenes[0] = new DoubleGene(gaConf, 0, 2.0);  // P
	    sampleGenes[1] = new DoubleGene(gaConf, 0, 2.0);  // I
	    sampleGenes[2] = new DoubleGene(gaConf, 0, 2.0);  // D
	    sampleGenes[3] = new DoubleGene(gaConf, 0, 4.0);  // F

	    Chromosome sampleChromosome = new Chromosome(gaConf, sampleGenes );

	    gaConf.setSampleChromosome( sampleChromosome);
	    Double pInt = (Double) sampleChromosome.getGene(0).getAllele();
	    Double iInt = (Double) sampleChromosome.getGene(1).getAllele();
	    Double dInt = (Double) sampleChromosome.getGene(2).getAllele();
	    Double fInt = (Double) sampleChromosome.getGene(3).getAllele();
	    
	    p = 0; // need to move in loop
	    i = 0;
	    d = 0;
	    f = 0;
	    
	    motors = new PIDWithMotors(p,i,d,f,0);
	    
	    try {
	        gaConf.setSampleChromosome(sampleChromosome);
	        gaConf.setPopulationSize(population);
	        gaConf.setFitnessFunction(new FitFunc(motors)); //WILL BE MY COOL FUNCTION HERE
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
	    	for (int index = 0; index < genotype.getPopulation().getChromosomes().size(); index++) {
	    		motors.setPID(p, i, d, f);
				motors.reset();
				String sr = "";
				long startTime = System.nanoTime();
				System.out.println("START!");
				motors.setTarget(target);
				startTime = System.nanoTime();
				counts = 0;
				
				while (counts <= maxCounts) {
					sr = "";
					sr += "Error: " + motors.getError();
					sr += "\tTarget: " + target;
					sr += "\tCurrent Speed: " + motors.getVoltage() * motors.maxRPM;
					sr += "\tWanted Speed: " + motors.getNewSpeed();
					sr += "\tCurrent Voltage: " + motors.getVoltage();
					sr += "\tP: " + p;
					sr += "\tI: " + i;
					sr += "\tD: " + d;
					sr += "\tF: " + f;
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
				System.out.println("PID CHANGE!");
				pInt = (Double) genotype.getPopulation().getChromosomes().get(index).getGene(0).getAllele();
			    iInt = (Double) genotype.getPopulation().getChromosomes().get(index).getGene(1).getAllele();
			    dInt = (Double) genotype.getPopulation().getChromosomes().get(index).getGene(2).getAllele();
			    fInt = (Double) genotype.getPopulation().getChromosomes().get(index).getGene(3).getAllele();
			    
			    p = pInt.doubleValue(); // need to move in loop
			    i = iInt.doubleValue();
			    d = dInt.doubleValue();
			    f = fInt.doubleValue();
	    	}
	    	
			
	    	genotype.evolve(); // what does this even do?
	    	
	    	
	    	if (percentEvolution > 0 && i % percentEvolution == 0) {
	            progress++;
	            IChromosome fittest = genotype.getFittestChromosome();
	            double fitness = fittest.getFitnessValue();
	            System.out.println("Currently fittest Chromosome has fitness " + fitness);
	            if (fitness >= maxFitness) {
	            	break;
	            }
	    	}
	    	
	    	
	    	pInt = (Double) genotype.getFittestChromosome().getGene(0).getAllele();
		    iInt = (Double) genotype.getFittestChromosome().getGene(1).getAllele();
		    dInt = (Double) genotype.getFittestChromosome().getGene(2).getAllele();
		    fInt = (Double) genotype.getFittestChromosome().getGene(3).getAllele();
		    
		    p = pInt.intValue(); // need to move in loop
		    i = iInt.intValue();
		    d = dInt.intValue();
		    f = fInt.intValue();
	    	
	    	// ALL THIS GOES IN THE GENOTYPE CLASS
			System.out.println("HAS COMPLETED!");
	    }
	    IChromosome fittest = genotype.getFittestChromosome();
	    System.out.println("Fittest Chromosome has fitness " + fittest.getFitnessValue());
	}

}
class FitFunc extends FitnessFunction {

	PIDWithMotors pidthing;
	
	public FitFunc (PIDWithMotors pid) {
		pidthing = pid;
	}
	
	@Override
	protected double evaluate(IChromosome arg0) {
		return 1 / pidthing.totalPositiveError * 1000;
	}
	
}

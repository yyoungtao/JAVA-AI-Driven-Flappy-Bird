package AIbird;

import java.util.Random;

public class ConnectionGene {

	NodeGene fromNode;
	NodeGene toNode;
	double weight;
	boolean enabled;
	int innovationNo; // each connection is given a innovation number to compare genomes

	public ConnectionGene(NodeGene from, NodeGene to, double weight, int innovate) {
		this.fromNode = from;
		this.toNode = to;
		this.weight = weight;
		this.enabled = true;
		this.innovationNo = innovate;
	}

	// changes the this.weight
	public void mutateWeight() {
		Random r = getRandom();
	    double rand2 = r.nextFloat();
	    if (rand2 < 0.1) { //10% of the time completely change the this.weight
	      weight = initWeight();
	    } else { //otherwise slightly change it
	      weight += ( r.nextGaussian()/ 50.0);
	      //keep this.weight between bounds
	      if (weight > 1) {
	        weight = 1;
	      }
	      if (weight < -1) {
	        weight = -1;

	      }
	    }
	  }

	// returns a copy of this connectionGene
	public ConnectionGene clone(NodeGene from, NodeGene to) {
		ConnectionGene clone = new ConnectionGene(from, to, weight, innovationNo);
	    clone.enabled = enabled;
	    return clone;
	  }
	
	public static double initWeight(){
		return getRandom().nextFloat()*(getRandom().nextBoolean() ? -1 : 1);
	}
	
	public static Random getRandom(){
		return new Random();
	}
}

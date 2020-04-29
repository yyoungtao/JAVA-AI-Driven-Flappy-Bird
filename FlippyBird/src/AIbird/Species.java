package AIbird;

import java.util.ArrayList;
import java.util.List;

public class Species {

    List<AIBird> players;
    double bestFitness = 0;
    AIBird champ;
    double averageFitness = 0;
    double staleness = 0; //how many generations the species has gone without an improvement
    Genome rep;
    //coefficients for testing compatibility
    double excessCoeff = 1;
    double weightDiffCoeff = 0.5;
    double compatibilityThreshold = 3;
    
	public Species(AIBird p) {
	    players = new ArrayList<AIBird>();


	    if (p != null) {
	      players.add(p);
	      //since it is the only one in the species it is by default the best
	      bestFitness = p.fitness;
	      //rep = p.brain.clone();
	      //champ = p.cloneForReplay();
	      rep = p.brain.clone();
	      champ = p;
	    }
	  }
//
//	  //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	  //returns whether the parameter genome is in this species
	  public boolean sameSpecies(Genome g) {
	    double compatibility;
	    int excessAndDisjoint = getExcessDisjoint(g, rep); //get the number of excess and disjoint genes between this player and the current species this.rep
	   double averageWeightDiff = averageWeightDiff(g, rep); //get the average weight difference between matching genes


	    int largeGenomeNormaliser = g.connections.size()- 20;
	    if (largeGenomeNormaliser < 1) {
	      largeGenomeNormaliser = 1;
	    }

	    compatibility = (excessCoeff * excessAndDisjoint / largeGenomeNormaliser) + (weightDiffCoeff * averageWeightDiff); //compatibility formula
	    return (compatibilityThreshold > compatibility);
	  }
//
//	  //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	  //add a player to the species
	  public void addToSpecies(AIBird p) {
	    players.add(p);
	  }
//
//	  //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	  //returns the number of excess and disjoint genes between the 2 input genomes
	  //i.e. returns the number of genes which dont match
	  public int getExcessDisjoint(Genome brain1, Genome brain2) {
	      int matching = 0;
	      for (int i = 0; i < brain1.connections.size(); i++) {
	        for (int j = 0; j < brain2.connections.size(); j++) {
	          if (brain1.connections.get(i).innovationNo == brain2.connections.get(j).innovationNo) {
	            matching++;
	            break;
	          }
	        }
	      }
	      return (brain1.connections.size() + brain2.connections.size() - 2 * (matching)); //return no of excess and disjoint genes
	    }
//	    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	    //returns the avereage weight difference between matching genes in the input genomes
	  public double averageWeightDiff(Genome brain1, Genome brain2) {
	      if (brain1.connections.size() == 0 || brain2.connections.size() == 0) {
	        return 0;
	      }

	      int matching = 0;
	      int totalDiff = 0;
	      for (int i = 0; i < brain1.connections.size(); i++) {
	        for (int j = 0; j < brain2.connections.size(); j++) {
	          if (brain1.connections.get(i).innovationNo == brain2.connections.get(j).innovationNo) {
	            matching++;
	            totalDiff += Math.abs(brain1.connections.get(i).weight - brain2.connections.get(j).weight);
	            break;
	          }
	        }
	      }
	      if (matching == 0) { //divide by 0 error
	        return 100;
	      }
	      return totalDiff / matching;
	    }
//	    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	    //sorts the species by fitness
	  public void sortSpecies() {

		  List<AIBird> temp = new ArrayList<AIBird> ();

	    //selection short
	    for (int i = 0; i < players.size(); i++) {
	      double max = 0;
	      int maxIndex = 0;
	      for (int j = 0; j < players.size(); j++) {
	        if (players.get(j).fitness > max) {
	          max = players.get(j).fitness;
	          maxIndex = j;
	        }
	      }
	      temp.add(players.get(maxIndex));
	      players.remove(maxIndex);
	      i--;
	    }
	    //players = new ArrayList<AIBird>();
	    players = temp;
	    //System.arraycopy(temp, 0, players, 0, temp.size());
	    
	    if (players.size() == 0) {
	      staleness = 200;
	      return;
	    }
	    //if new best player
	    if (players.get(0).fitness > bestFitness) {
	      this.staleness = 0;
	      this.bestFitness = players.get(0).fitness;
	      this.rep = players.get(0).brain.clone();
	      this.champ = players.get(0).clone();
	    } else { //if no new best player
	      this.staleness++;
	    }
	  }
//
//	  //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	  //simple stuff
	  public void setAverage() {
	      double sum = 0.0;
	      for (int i = 0; i < players.size(); i++) {
	        sum += players.get(i).fitness;
	      }
	      averageFitness = sum / players.size();
	    }
//	    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//
	  //gets baby from the this.players in this species
	  public AIBird generateOffSpring(List<HistoricalMarking> innovationHistory) {
		  AIBird baby;
	    if (ConnectionGene.getRandom().nextFloat() < 0.25) { //25% of the time there is no crossover and the child is simply a clone of a random(ish) player
	      baby = selectPlayer();
	    } else { //75% of the time do crossover
	      //get 2 random(ish) parents
	      AIBird parent1 = selectPlayer();
	      AIBird parent2 = selectPlayer();

	      //the crossover function expects the highest fitness parent to be the object and the lowest as the argument
	      if (parent1.fitness < parent2.fitness) {
	        baby = parent2.crossover(parent1);
	      } else {
	        baby = parent1.crossover(parent2);
	      }
	    }
	    baby.brain.mutate(innovationHistory); //mutate that baby brain

	    return baby;
	  }
//
//	  //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	  //selects a player based on it fitness
	  public AIBird selectPlayer() {
	      double fitnessSum = 0;
	      for (int i = 0; i < this.players.size(); i++) {
	        fitnessSum += this.players.get(i).fitness;
	      }
	      double rand = ConnectionGene.getRandom().nextFloat()*fitnessSum;
	      double runningSum = 0.0;

	      for (int i = 0; i < this.players.size(); i++) {
	        runningSum += this.players.get(i).fitness;
	        if (runningSum > rand) {
	          return this.players.get(i);
	        }
	      }
	      //unreachable code to make the parser happy
	      return this.players.get(0);
	    }
//	    //------------------------------------------------------------------------------------------------------------------------------------------
	    //kills off bottom half of the species
	  public void cull() {
	      if (players.size() > 2) {
	        for (int i = players.size() / 2; i < players.size(); i++) {
	          this.players.remove(i);
	          i--;
	        }
	      }
	    }
//	    //------------------------------------------------------------------------------------------------------------------------------------------
	    //in order to protect unique this.players, the fitnesses of each player is divided by the number of this.players in the species that that player belongs to
	  public void fitnessSharing() {
	    for (int i = 0; i < players.size(); i++) {
	      players.get(i).fitness /= players.size();
	    }
	  }
}

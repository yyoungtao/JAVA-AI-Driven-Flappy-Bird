package AIbird;

import java.util.ArrayList;
import java.util.List;

import main.FlippyBirdPanel;

public class Population {

	public List<AIBird> players; // new ArrayList<Player>();
	public AIBird bestPlayer; // the best ever player
	int bestScore = 0; // the score of the best ever player
	int globalBestScore = 0;
	int gen = 1;
	public List<HistoricalMarking> innovationHistory;
	public List<AIBird> genPlayers; // new ArrayList<Player>();
	public List<Species> species; // new ArrayList<Species>();

	boolean massExtinctionEvent = false;
	boolean newStage = false;

	int gensSinceNewWorld = 0;

	public Population(int size) {
		players = new ArrayList<AIBird>();
		innovationHistory = new ArrayList<HistoricalMarking>();
		genPlayers = new ArrayList<AIBird>();
		species = new ArrayList<Species>();

		for (int i = 0; i < size; i++) {
			players.add(new AIBird());
			// this.players[this.players.length -
			// 1].brain.fullyConnect(this.innovationHistory);
			players.get(players.size() - 1).setLayoutX(FlippyBirdPanel.screen_width/2);//set the initial location of bird
			players.get(players.size() - 1).brain.mutate(this.innovationHistory); // fullyConnect(this.innovationHistory);
			players.get(players.size() - 1).brain.generateNetwork();
		}
	}
//
//	public AIBird getCurrentBest() {
//		for (int i = 0; i < players.size(); i++) {
//			if (!players.get(i).dead) {
//				return players.get(i);
//			}
//		}
//		return players.get(0);
//	}
//
//	public void updateAlive() {
//		boolean firstShown = false;
//		for (int i = 0; i < players.size(); i++) {
//			if (!players.get(i).dead) {
//				for (int j = 0; j < superSpeed; j++) {
//					players.get(i).look(); // get inputs for brain
//					players.get(i).think(); // use outputs from neural network
//					players.get(i).update(); // move the player according to the
//												// outputs from the neural
//												// network
//				}
//				if (!showNothing && (!showBest || !firstShown)) {
//					players.get(i).show();
//					firstShown = true;
//				}
//				if (players.get(i).score > globalBestScore) {
//					this.globalBestScore = players.get(i).score;
//				}
//
//			}
//		}
//	}
//
	// returns true if all the players are dead sad
	public boolean done() {
		for (int i = 0; i < players.size(); i++) {
			if (!players.get(i).dead) {
				return false;
			}
		}
		return true;
	}
//
	// sets the best player globally and for thisthis.gen
	public void setBestPlayer() {
		AIBird tempBest = species.get(0).players.get(0);//end up to delete all birds
		tempBest.gen = gen;
		// if best this.gen is better than the global best score then set the
		// global best as the best thisthis.gen
		if (tempBest.score >= bestScore) {
			genPlayers.add(tempBest);
			bestScore = tempBest.score;
			bestPlayer = tempBest;
		}

	}
//	
	  //this function is called when all the players in the this.players are dead and a newthis.generation needs to be made
	  public void naturalSelection() {

	    // this.batchNo = 0;
	    AIBird previousBest = players.get(0);
	    speciate(); //seperate the this.players varo this.species
	    calculateFitness(); //calculate the fitness of each player
	    sortSpecies(); //sort the this.species to be ranked in fitness order, best first

//	    if (this.massExtinctionEvent) {
//	      this.massExtinction();
//	      this.massExtinctionEvent = false;
//	    }
	    cullSpecies(); //kill off the bottom half of each this.species
	    setBestPlayer(); //save the best player of thisthis.gen
	    killStaleSpecies(); //remove this.species which haven't improved in the last 15(ish)this.generations
	    killBadSpecies(); //kill this.species which are so bad that they cant reproduce
        //print the brain best node
//	    System.out.println("inputs:"+this.bestPlayer.brain.inputs);
//	    System.out.println("outputs:"+this.bestPlayer.brain.outputs);
//	    System.out.println("layers:"+this.bestPlayer.brain.layers);
//	    System.out.println("nextnode:"+this.bestPlayer.brain.nextNode);
//	    System.out.println("biasNode:"+this.bestPlayer.brain.biasNode);
//	    System.out.println("nextConnectionNo:"+FlippyBirdPanel.nextConnectionNo);
//	    System.out.println("connections:"+this.bestPlayer.brain.connections.size());
//	    System.out.println("weights");
//	    for(int k=0 ; k<this.bestPlayer.brain.connections.size();k++){
//	    	System.out.println("from node:"+this.bestPlayer.brain.connections.get(k).fromNode.inputSum);
//	    	System.out.println("to node:"+this.bestPlayer.brain.connections.get(k).toNode.inputSum);
//	    	System.out.println(this.bestPlayer.brain.connections.get(k).innovationNo+" "+this.bestPlayer.brain.connections.get(k).weight);
//	    }
//	    System.out.println("weights ends");
	    
	    
	    //console.log("generation  " + this.gen + "  Number of mutations  " + this.innovationHistory.length + "  species:   " + this.species.length + "  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        //System.out.println(species.size());
	    double averageSum = this.getAvgFitnessSum();
	    List<AIBird> children = new ArrayList<AIBird>();
	    for (int j = 0; j < species.size(); j++) { //for each this.species

	      children.add(species.get(j).champ.clone()); //add champion without any mutation//some problems
	      double NoOfChildren = Math.floor(species.get(j).averageFitness / averageSum * players.size()) - 1; //the number of children this this.species is allowed, note -1 is because the champ is already added
          
          //System.out.println(NoOfChildren);
	      for (int i = 0; i < NoOfChildren; i++) { //get the calculated amount of children from this this.species
	        children.add(species.get(j).generateOffSpring(this.innovationHistory));
	      }
	    }
	    // setup();
	    // return;
	    if (children.size() < this.players.size()) {
	      children.add(previousBest.clone());
	    }
	    while (children.size() < this.players.size()) { //if not enough babies (due to flooring the number of children to get a whole var)
	      children.add(this.species.get(0).generateOffSpring(this.innovationHistory)); //get babies from the best this.species
	    }

	    this.players = new ArrayList<AIBird>();
	    players = children;
	    //System.arraycopy(children, 0, this.players, 0, children.size()); //set the children as the current this.playersulation
	    this.gen += 1;
	    for (int i = 0; i < this.players.size(); i++) { //generate networks for each of the children
	        players.get(i).brain.generateNetwork();
	    }
	  }
//	  
//	//seperate this.players into this.species based on how similar they are to the leaders of each this.species in the previousthis.gen
	  public void speciate() {
	      for (Species s : species) { //empty this.species
	        s.players = new ArrayList<AIBird>();
	      }
	      for (int i = 0; i < players.size(); i++) { //for each player
	        boolean speciesFound = false;
	        for (Species s : species) { //for each this.species
	          if (s.sameSpecies(players.get(i).brain)) { //if the player is similar enough to be considered in the same this.species
	            s.addToSpecies(players.get(i)); //add it to the this.species
	            speciesFound = true;
	            break;
	          }
	        }
	        if (!speciesFound) { //if no this.species was similar enough then add a new this.species with this as its champion
	          species.add(new Species(players.get(i)));
	        }
	      }
	    }
//	  
//	    //calculates the fitness of all of the players
	  public void calculateFitness() {
	      for (int i = 0; i < players.size(); i++) {
	        players.get(i).calculateFitness();
	      }
	    }
//	  
	    //sorts the players within a this.species and the this.species by their fitnesses
	  public void sortSpecies() {
	      //sort the players within a this.species
	      for (Species s : species) {
	        s.sortSpecies();
	      }

	      //sort the this.species by the fitness of its best player
	      //using selection sort like a loser
	      List<Species> temp = new ArrayList<Species>();
	      for (int i = 0; i < species.size(); i++) {
	        double max = 0;
	        int maxIndex = 0;
	        for (int j = 0; j < species.size(); j++) {
	          if (species.get(j).bestFitness > max) {
	            max = species.get(j).bestFitness;
	            maxIndex = j;
	          }
	        }
	        temp.add(species.get(maxIndex));
	        species.remove(maxIndex);
	        i--;
	      }
	      //species = new ArrayList<Species>();
	      this.species = temp;
	      //System.arraycopy(temp, 0, this.species, 0, temp.size());
	    }
//	  
	//kills all this.species which haven't improved in 15this.generations
	  public void killStaleSpecies() {
	      for (int i = 2; i < species.size(); i++) {
	        if (species.get(i).staleness >= 15) {
	          species.remove(i);
	          i--;
	        }
	      }
	    }
//	  
	//if a this.species sucks so much that it wont even be allocated 1 child for the nextthis.generation then kill it now
	  public void killBadSpecies() {
	      double averageSum = getAvgFitnessSum();

	      for (int i = 1; i < this.species.size(); i++) {
	        if (this.species.get(i).averageFitness / averageSum * this.players.size() < 1) { //if wont be given a single child
	          this.species.remove(i); //sad
	          i--;
	        }
	      }
	    }
//	  
	//returns the sum of each this.species average fitness
	  public double getAvgFitnessSum() {
	    double averageSum = 0.0;
	    for (Species s : species) {
	      averageSum += s.averageFitness;
	    }
	    return averageSum;
	  }
//	  
	  //kill the bottom half of each this.species
	  public void cullSpecies() {
	    for (Species s : species) {
	      s.cull(); //kill bottom half
	      s.fitnessSharing(); //also while we're at it lets do fitness sharing, no need to do this
	      s.setAverage(); //reset averages because they will have changed
	    }
	  }
//
//
//	  massExtinction() {
//	      for (var i = 5; i < this.species.length; i++) {
//	        // this.species.remove(i); //sad
//	        this.species.splice(i, 1);
//
//	        i--;
//	      }
//	    }
//	  
//	//------------------------------------------------------------------------------------------------------------------------------------------
//	    //              BATCH LEARNING
//	    //------------------------------------------------------------------------------------------------------------------------------------------
//	    //update all the players which are alive
//	  updateAliveInBatches() {
//	    let aliveCount = 0;
//	    for (var i = 0; i < this.players.length; i++) {
//	      if (this.playerInBatch(this.players[i])) {
//
//	        if (!this.players[i].dead) {
//	          aliveCount++;
//	          this.players[i].look(); //get inputs for brain
//	          this.players[i].think(); //use outputs from neural network
//	          this.players[i].update(); //move the player according to the outputs from the neural network
//	          if (!showNothing && (!showBest || i == 0)) {
//	            this.players[i].show();
//	          }
//	          if (this.players[i].score > this.globalBestScore) {
//	            this.globalBestScore = this.players[i].score;
//	          }
//	        }
//	      }
//	    }
//
//
//	    if (aliveCount == 0) {
//	      this.batchNo++;
//	    }
//	  }
//
//
//	  playerInBatch(player) {
//	    for (var i = this.batchNo * this.worldsPerBatch; i < min((this.batchNo + 1) * this.worldsPerBatch, worlds.length); i++) {
//	      if (player.world == worlds[i]) {
//	        return true;
//	      }
//	    }
//
//	    return false;
//
//
//	  }
//
//	  stepWorldsInBatch() {
//	      for (var i = this.batchNo * this.worldsPerBatch; i < min((this.batchNo + 1) * this.worldsPerBatch, worlds.length); i++) {
//	        worlds[i].Step(1 / 30, 10, 10);
//	      }
//	    }
//	    //returns true if all the players in a batch are dead      sad
//	  batchDead() {
//	    for (var i = this.batchNo * this.playersPerBatch; i < min((this.batchNo + 1) * this.playersPerBatch, this.players.length); i++) {
//	      if (!this.players[i].dead) {
//	        return false;
//	      }
//	    }
//	    return true;
//	  }
//	  
//	  
//	  
}

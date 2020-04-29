package AIbird;

import java.util.ArrayList;
import java.util.List;

import main.FlippyBirdPanel;

public class Genome {

	public List<ConnectionGene> connections; // a list of connections between
										// this.nodes which represent the NN
	public List<NodeGene> nodes; // node of neural network
	public int inputs; // input layers
	public int outputs;// output layers
	public int layers = 2;// layer numbers
	public int nextNode = 0;
	// this.network = []; //a list of the this.nodes in the order that they need
	// to be considered in the NN
	public int biasNode = 0;
	List<NodeGene> network;

	public Genome(int inputs, int outputs, boolean crossover) {
		// initial variables
		connections = new ArrayList<ConnectionGene>();
		nodes = new ArrayList<NodeGene>();
		this.inputs = inputs;
		this.outputs = outputs;
		this.layers = 2;
		this.nextNode = 0;
		network = new ArrayList<NodeGene>();

		if (crossover) {
			return;
		}
		// init the input layer
		for (int i = 0; i < this.inputs; i++) {
			nodes.add(new NodeGene(i));
			nextNode++;
			nodes.get(i).layer = 0;
		}

		// create output layer
		for (int i = 0; i < this.outputs; i++) {
			nodes.add(new NodeGene(i + this.inputs));
			nodes.get(i + this.inputs).layer = 1;
			nextNode++;
		}

		nodes.add(new NodeGene(this.nextNode)); // bias node
		biasNode = nextNode;
		nextNode++;
		nodes.get(biasNode).layer = 0;
	}

	/*
	 * initialize the neural network
	 * create neural network connect the inputs with the outputs a simple two
	 * layer neural network
	 */
	public void fullyConnect(List<HistoricalMarking> innovationHistory) {
		for (int i = 0; i < inputs; i++) {
			for (int j = 0; j < outputs; j++) {
				int connectionInnovationNumber = getInnovationNumber(innovationHistory, nodes.get(i),
						nodes.get(nodes.size() - j - 2));
				connections.add(new ConnectionGene(nodes.get(i), nodes.get(nodes.size() - j - 2),
						ConnectionGene.initWeight(), connectionInnovationNumber));
			}
		}

		int connectionInnovationNumber = getInnovationNumber(innovationHistory, nodes.get(this.biasNode),
				nodes.get(nodes.size() - 2));
		connections.add(new ConnectionGene(nodes.get(biasNode), nodes.get(nodes.size() - 2), ConnectionGene.initWeight(),
				connectionInnovationNumber));

		// changed this so if error here
		this.connectNodes();
	}
	
	  public int getInnovationNumber(List<HistoricalMarking> innovationHistory, NodeGene from, NodeGene to) {
	      boolean isNew = true;
	      int connectionInnovationNumber = FlippyBirdPanel.nextConnectionNo;
	      for (int i = 0; i < innovationHistory.size(); i++) { //for each previous mutation
	        if (innovationHistory.get(i).matches(this, from, to)) { //if match found
	          isNew = false; //its not a new mutation
	          connectionInnovationNumber = innovationHistory.get(i).innovationNumber; //set the innovation number as the innovation number of the match
	          break;
	        }
	      }
	      if (isNew) { //if the mutation is new then create an arrayList of varegers representing the current state of the genome
	        List<Integer> innoNumbers = new ArrayList<Integer>();
	        for (int i = 0; i < connections.size(); i++) { //set the innovation numbers
	          innoNumbers.add(connections.get(i).innovationNo);
	        }
	        //then add this mutation to the innovationHistory
	        innovationHistory.add(new HistoricalMarking(from, to, connectionInnovationNumber, innoNumbers));
	        FlippyBirdPanel.nextConnectionNo++;
	      }
	      return connectionInnovationNumber;
	    }

	public void connectNodes() {
		for (int i = 0; i < nodes.size(); i++) { // clear the connections
			nodes.get(i).outputConnections = new ArrayList<ConnectionGene>();
		}
		for (int i = 0; i < connections.size(); i++) { // for each connectionGene
			connections.get(i).fromNode.outputConnections.add(connections.get(i)); // add it to node
		}
	}

	public NodeGene getNode(int nodeNumber) {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).number == nodeNumber) {
				return nodes.get(i);
			}
		}
		return null;
	}

	// feeding in input values varo the NN and returning output array
	public double[] feedForward(double[] inputValues) {
	    //set the outputs of the input this.nodes
	    for (int i = 0; i < inputs; i++) {
	        nodes.get(i).outputValue = inputValues[i];
	    }
	    
	    nodes.get(biasNode).outputValue = 1; //output of bias is 1

	    for (int i = 0; i < network.size(); i++) { //for each node in the network engage it(see node class for what this does)
	    	network.get(i).engage();
	    }
	    
	    //the outputs are this.nodes[inputs] to this.nodes [inputs+outputs-1]
	    double outs [] = new double[outputs];
	    for (int i = 0; i < outputs; i++) {
	      outs[i] = nodes.get(this.inputs + i).outputValue;
	    }

	    for (int i = 0; i < nodes.size(); i++) { //reset all the this.nodes for the next feed forward
	        nodes.get(i).inputSum = 0;
	    }
	    return outs;
	  }
	
	 //sets up the NN as a list of this.nodes in order to be engaged
//	  generateNetwork() {
//	      this.connectNodes();
//	      this.network = [];
//	      //for each layer add the node in that layer, since layers cannot connect to themselves there is no need to order the this.nodes within a layer
//
//	      for (var l = 0; l < this.layers; l++) { //for each layer
//	        for (var i = 0; i < this.nodes.length; i++) { //for each node
//	          if (this.nodes[i].layer == l) { //if that node is in that layer
//	            this.network.push(this.nodes[i]);
//	          }
//	        }
//	      }
//	    }
	public void generateNetwork(){
		connectNodes();
		network = new ArrayList<NodeGene>();
		
	    for (int l = 0; l < this.layers; l++) { //for each layer
	        for (int i = 0; i < this.nodes.size(); i++) { //for each node
	          if (this.nodes.get(i).layer == l) { //if that node is in that layer
	            this.network.add(this.nodes.get(i));
	          }
	        }
      }
		
	}
	  //mutate
	  //mutates the genome
	  public void mutate(List<HistoricalMarking> innovationHistory) {
	    if (connections.size() == 0) {
	    	//add new connection
	        addConnection(innovationHistory);
	    }
	    double rand1 = ConnectionGene.getRandom().nextFloat();
	    if (rand1 < 0.8) { // 80% of the time mutate weights
	      for (int i = 0; i < connections.size(); i++) {
	        connections.get(i).mutateWeight();
	      }
	    }
	    //5% of the time add a new connection
	    double rand2 = ConnectionGene.getRandom().nextFloat();
	    if (rand2 < 0.05) {
          //add new conneciton
	      addConnection(innovationHistory);
	    }
	    //1% of the time add a node
	    double rand3 = ConnectionGene.getRandom().nextFloat();
	    if (rand3 < 0.01) {
          //add new node
	      addNode(innovationHistory);
	    }
	  }
	 
	 //addNode
	 public void addNode(List<HistoricalMarking> innovationHistory){
	    //pick a random connection to create a node between
	    if (this.connections.size() == 0) {
	      this.addConnection(innovationHistory);
	      return;
	    }
	    int randomConnection = (int)Math.floor(ConnectionGene.getRandom().nextFloat()*connections.size());

	    while (this.connections.get(randomConnection).fromNode.number == this.nodes.get(this.biasNode).number && this.connections.size() != 1) { //dont disconnect bias
	      randomConnection = (int)Math.floor(ConnectionGene.getRandom().nextFloat()*connections.size());
	    }

	    this.connections.get(randomConnection).enabled = false; //disable it

	    int newNodeNo = this.nextNode;
	    this.nodes.add(new NodeGene(newNodeNo));
	    this.nextNode++;
	    //add a new connection to the new node with a weight of 1
	    int connectionInnovationNumber = this.getInnovationNumber(innovationHistory, this.connections.get(randomConnection).fromNode, this.getNode(newNodeNo));
	    this.connections.add(new ConnectionGene(this.connections.get(randomConnection).fromNode, this.getNode(newNodeNo), 1, connectionInnovationNumber));


	    connectionInnovationNumber = this.getInnovationNumber(innovationHistory, this.getNode(newNodeNo), this.connections.get(randomConnection).toNode);
	    //add a new connection from the new node with a weight the same as the disabled connection
	    this.connections.add(new ConnectionGene(this.getNode(newNodeNo), this.connections.get(randomConnection).toNode, this.connections.get(randomConnection).weight, connectionInnovationNumber));
	    this.getNode(newNodeNo).layer = this.connections.get(randomConnection).fromNode.layer + 1;


	    connectionInnovationNumber = this.getInnovationNumber(innovationHistory, this.nodes.get(this.biasNode), this.getNode(newNodeNo));
	    //connect the bias to the new node with a weight of 0
	    this.connections.add(new ConnectionGene(this.nodes.get(this.biasNode), this.getNode(newNodeNo), 0, connectionInnovationNumber));

	    //if the layer of the new node is equal to the layer of the output node of the old connection then a new layer needs to be created
	    //more accurately the layer numbers of all layers equal to or greater than this new node need to be incrimented
	    if (this.getNode(newNodeNo).layer == this.connections.get(randomConnection).toNode.layer) {
	      for (int i = 0; i < this.nodes.size() - 1; i++) { //dont include this newest node
	        if (this.nodes.get(i).layer >= this.getNode(newNodeNo).layer) {
	          this.nodes.get(i).layer++;
	        }
	      }
	      this.layers++;
	    }
	    this.connectNodes();
	 }
	 //addConnection
	 public void addConnection(List<HistoricalMarking> innovationHistory){
	      //cannot add a connection to a fully connected network
	      if (this.fullyConnected()) {
	        //console.log("connection failed");
	        return;
	      }
	      //get random this.nodes
	      int randomNode1 = (int)Math.floor(ConnectionGene.getRandom().nextFloat()*nodes.size());
	      int randomNode2 = (int)Math.floor(ConnectionGene.getRandom().nextFloat()*nodes.size());
	      while (this.randomConnectionNodesAreShit(randomNode1, randomNode2)) { //while the random this.nodes are no good
	        //get new ones
	        randomNode1 = (int)Math.floor(ConnectionGene.getRandom().nextFloat()*nodes.size());
	        randomNode2 = (int)Math.floor(ConnectionGene.getRandom().nextFloat()*nodes.size());
	      }
	      int temp;
	      if (this.nodes.get(randomNode1).layer > this.nodes.get(randomNode2).layer) { //if the first random node is after the second then switch
	        temp = randomNode2;
	        randomNode2 = randomNode1;
	        randomNode1 = temp;
	      }

	      //get the innovation number of the connection
	      //this will be a new number if no identical genome has mutated in the same way
	      int connectionInnovationNumber = this.getInnovationNumber(innovationHistory, nodes.get(randomNode1), this.nodes.get(randomNode2));
	      //add the connection with a random array

	      this.connections.add(new ConnectionGene(this.nodes.get(randomNode1), this.nodes.get(randomNode2), ConnectionGene.initWeight(), connectionInnovationNumber)); //changed this so if error here
	      this.connectNodes();
	 }
	 
	  //returns whether the network is fully connected or not
	  public boolean fullyConnected() {

	    int maxConnections = 0;
	    int[] nodesInLayers = new int[layers]; //array which stored the amount of this.nodes in each layer
	    
	    for (int i = 0; i < this.layers; i++) {
	      nodesInLayers[i] = 0;
	    }
	    //populate array
	    for (int i = 0; i < this.nodes.size(); i++) {
	      nodesInLayers[this.nodes.get(i).layer] += 1;
	    }
	    //for each layer the maximum amount of connections is the number of nodes in this layer * the number of nodes infront of it
	    //so lets add the max for each layer together and then we will get the maximum amount of connections in the network
	    for (int i = 0; i < this.layers - 1; i++) {
	      int nodesInFront = 0;
	      for (int j = i + 1; j < this.layers; j++) { //for each layer infront of this layer
	        nodesInFront += nodesInLayers[j]; //add up this.nodes
	      }

	      maxConnections += nodesInLayers[i] * nodesInFront;
	    }
	    if (maxConnections <= this.connections.size()) { //if the number of connections is equal to the max number of connections possible then it is full
	      return true;
	    }

	    return false;
	  }
	 
	  public boolean randomConnectionNodesAreShit(int r1, int r2) {
		    if (this.nodes.get(r1).layer == this.nodes.get(r2).layer) return true; // if the this.nodes are in the same layer
		    if (this.nodes.get(r1).isConnectedTo(this.nodes.get(r2))) return true; //if the this.nodes are already connected
		    return false;
		  }
	 
	 
	 //crossover
	 public Genome crossover(Genome parent2){
		 Genome child = new Genome(this.inputs, this.outputs, true);
		    //child.genes = [];
		    //child.nodes = [];
		    child.layers = this.layers;
		    child.nextNode = this.nextNode;
		    child.biasNode = this.biasNode;
		    List<ConnectionGene> childGenes = new ArrayList<ConnectionGene>();//list of genes to be inherrited form the parents
		    List<Boolean> isEnabled = new ArrayList<Boolean>();
		    //all inherited genes
		    for (int i = 0; i < this.connections.size(); i++) {
		      boolean setEnabled = true; //is this node in the chlid going to be enabled

		      int parent2gene = this.matchingGene(parent2, this.connections.get(i).innovationNo);
		      if (parent2gene != -1) { //if the genes match
		        if (!this.connections.get(i).enabled || !parent2.connections.get(parent2gene).enabled) { //if either of the matching genes are disabled

		          if (ConnectionGene.getRandom().nextFloat() < 0.75) { //75% of the time disable the childs gene
		            setEnabled = false;
		          }
		        }
		        double rand = ConnectionGene.getRandom().nextFloat();
		        if (rand < 0.5) {
		          childGenes.add(this.connections.get(i));
		          //get gene from this parent1
		        } else {
		          //get gene from parent2
		          childGenes.add(parent2.connections.get(parent2gene));
		        }
		      } else { //disjoint or excess gene
		        childGenes.add(this.connections.get(i));
		        setEnabled = this.connections.get(i).enabled;
		      }
		      isEnabled.add(setEnabled);
		    }
		    //since all excess and disjovar genes are inherrited from the more fit parent (this Genome) the childs structure is no different from this parent | with exception of dormant connections being enabled but this wont effect this.nodes
		    //so all the this.nodes can be inherrited from this parent
		    for (int i = 0; i < this.nodes.size(); i++) {
		      child.nodes.add(this.nodes.get(i).clone());
		    }
		    //inherent from both parents for nodes and connections that is not match
		    

		    //clone all the connections so that they connect the childs new this.nodes
		    for (int i = 0; i < childGenes.size(); i++) {
		      //child.connections.add(childGenes.get(i).clone(child.getNode(childGenes[i].fromNode.number), child.getNode(childGenes[i].toNode.number)));
		      child.connections.add(childGenes.get(i));
		      child.connections.get(i).enabled = isEnabled.get(i);
		    }

		    //child.connectNodes();
		    return child;
	 }
	 
	 //crossover
	 public Genome crossover_improve(Genome parent2){
		 Genome child = new Genome(this.inputs, this.outputs, true);
		    child.layers = this.layers;
		    child.nextNode = this.nextNode;
		    child.biasNode = this.biasNode;
		    List<ConnectionGene> childGenes = new ArrayList<ConnectionGene>();//list of genes to be inherrited form the parents
		    List<Boolean> isEnabled = new ArrayList<Boolean>();
		    List<Integer> matchNo = new ArrayList<Integer>();
		    //all inherited genes
		    for (int i = 0; i < this.connections.size(); i++) {
		      boolean setEnabled = true; //is this node in the chlid going to be enabled

		      int parent2gene = this.matchingGene(parent2, this.connections.get(i).innovationNo);
		      if (parent2gene != -1) { //if the genes match
		    	  matchNo.add(this.connections.get(i).innovationNo);
		        if (!this.connections.get(i).enabled || !parent2.connections.get(parent2gene).enabled) { //if either of the matching genes are disabled

		          if (ConnectionGene.getRandom().nextFloat() < 0.75) { //75% of the time disable the childs gene
		            setEnabled = false;
		          }
		        }
		        double rand = ConnectionGene.getRandom().nextFloat();
		        if (rand < 0.5) {
		          childGenes.add(this.connections.get(i));
		          //get gene from this parent1
		        } else {
		          //get gene from parent2
		          childGenes.add(parent2.connections.get(parent2gene));
		        }
		      } else { 
		        childGenes.add(this.connections.get(i));
		        setEnabled = this.connections.get(i).enabled;
		      }
		      isEnabled.add(setEnabled);
		    }
		    
		    //inherent from parent2 for connections that is not match
		    //disjoint or excess gene
		    for (int i = 0; i < parent2.connections.size(); i++) {
		        if (! matchNo.contains(parent2.connections.get(i).innovationNo)) {
		        	childGenes.add(parent2.connections.get(i));
		        	isEnabled.add(parent2.connections.get(i).enabled);
		        }
		    }
		    
		    //since all excess and disjovar genes are inherrited from the more fit parent (this Genome) the childs structure is no different from this parent | with exception of dormant connections being enabled but this wont effect this.nodes
		    //so all the this.nodes can be inherrited from this parent
		    List<Integer> matchNode = new ArrayList<Integer>();
		    for (int i = 0; i < this.nodes.size(); i++) {
		      child.nodes.add(this.nodes.get(i).clone());
		      for (int j = 0; j < parent2.nodes.size(); j++) {
		    	  if(this.nodes.get(i).number == parent2.nodes.get(j).number){
		    		  matchNode.add(this.nodes.get(i).number);
		    	  }
		      }
		    }
		    
		    for (int i = 0; i < parent2.nodes.size(); i++) {
		    	if(!matchNode.contains(parent2.nodes.get(i).number)){
		    		child.nodes.add(parent2.nodes.get(i).clone());
		    	}
			}
		    

		    //clone all the connections so that they connect the childs new this.nodes
		    for (int i = 0; i < childGenes.size(); i++) {
		      //child.connections.add(childGenes.get(i).clone(child.getNode(childGenes[i].fromNode.number), child.getNode(childGenes[i].toNode.number)));
		      child.connections.add(childGenes.get(i));
		      child.connections.get(i).enabled = isEnabled.get(i);
		    }

		    child.connectNodes();
		    return child;
	 }
	 
	  //returns whether or not there is a gene matching the input innovation number  in the input genome
	 public int matchingGene(Genome parent2, int innovationNumber) {
	      for (int i = 0; i < parent2.connections.size(); i++) {
	        if (parent2.connections.get(i).innovationNo == innovationNumber) {
	          return i;
	        }
	      }
	      return -1; //no matching gene found
	    }
	 
	 //clone
	  //returns a copy of this genome
	  public Genome clone() {

	      Genome clone = new Genome(this.inputs, this.outputs, true);

	      for (int i = 0; i < this.nodes.size(); i++) { //copy this.nodes
	        clone.nodes.add(this.nodes.get(i).clone());
	      }

	      //copy all the connections so that they connect the clone new this.nodes

	      for (int i = 0; i < this.connections.size(); i++) { //copy genes
	        clone.connections.add(this.connections.get(i).clone(clone.getNode(this.connections.get(i).fromNode.number), clone.getNode(this.connections.get(i).toNode.number)));
	      }

	      clone.layers = this.layers;
	      clone.nextNode = this.nextNode;
	      clone.biasNode = this.biasNode;
	      clone.connectNodes();

	      return clone;
	    }
}

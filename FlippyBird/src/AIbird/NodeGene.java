package AIbird;

import java.util.ArrayList;
import java.util.List;

public class NodeGene {

	int number = 0;
	double inputSum = 0; // current sum i.e. before activation
	double outputValue = 0; // after activation function is applied
	List<ConnectionGene> outputConnections; // new ArrayList<connectionGene>();
	int layer = 0;
	// int drawPos = createVector();

	public NodeGene(int no) {
		number = no;
		outputConnections = new ArrayList<ConnectionGene>();
	}

	// the node sends its output to the inputs of the nodes its connected to
	public void engage() {
		if (layer != 0) { // no sigmoid for the inputs and bias
			outputValue = sigmoid(inputSum);
		}

		for (int i = 0; i < outputConnections.size(); i++) { // for each
			if (outputConnections.get(i).enabled) { // dont do shit if not enabled
				// add the weighted output to the sum of the inputs of whatever node this node is connected to
				outputConnections.get(i).toNode.inputSum += outputConnections.get(i).weight * outputValue;
			}
		}
	}

	// sigmoid activation function
	public double sigmoid(double x) {
		return 1.0 / (1.0 + Math.pow(Math.E, -4.9 * x));
		//return 1.0 / (1.0 + Math.pow(Math.E, -x));
	}

	// returns whether this node connected to the parameter node
	// used when adding a new connection
	public boolean isConnectedTo(NodeGene node) {
	      if(node.layer == layer) { //nodes in the same this.layer cannot be connected
	        return false;
	      }

	      if(node.layer < layer) {
	        for(int i = 0; i < node.outputConnections.size(); i++) {
	          if(node.outputConnections.get(i).toNode.number == this.number) {
	            return true;
	          }
	        }
	      } else {
	        for(int i = 0; i < outputConnections.size(); i++) {
	          if(outputConnections.get(i).toNode.number == node.number) {
	            return true;
	          }
	        }
	      }
	      return false;
	    }

	// returns a copy of this node
	public NodeGene clone() {
		NodeGene clone = new NodeGene(number);
	    clone.layer = layer;
	    return clone;
	  }
}

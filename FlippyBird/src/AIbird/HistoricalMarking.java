package AIbird;

import java.util.List;

public class HistoricalMarking {
	
    NodeGene fromNode;
    NodeGene toNode;
    int innovationNumber;
    List<Integer> innovationNumbers; 
	
	public HistoricalMarking(NodeGene fromNode, NodeGene toNode, int inno, List<Integer> innovationNos){
	    this.fromNode = fromNode;
	    this.toNode = toNode;
	    this.innovationNumber = inno;
	    this.innovationNumbers = innovationNos; 
	}
	  //returns whether the genome matches the original genome and the connection is between the same nodes
	  public boolean matches(Genome genome, NodeGene from, NodeGene to) {
	    if (genome.connections.size() != innovationNumbers.size()) { //if the number of connections are different then the genoemes aren't the same
	      if (from.number == fromNode.number && to.number == toNode.number) {
	        //next check if all the innovation numbers match from the genome
	        for (int i = 0; i < genome.connections.size(); i++) {
	        	for(int j=0; j<innovationNumbers.size();j++){
	  	          if (innovationNumbers.get(j).intValue() != genome.connections.get(i).innovationNo) {
	  	            return false;
	  	          }
	        	}
	        }
	        //if reached this far then the innovationNumbers match the genes innovation numbers and the connection is between the same nodes
	        //so it does match
	        return true;
	      }
	    }
	    return false;
	  }
}

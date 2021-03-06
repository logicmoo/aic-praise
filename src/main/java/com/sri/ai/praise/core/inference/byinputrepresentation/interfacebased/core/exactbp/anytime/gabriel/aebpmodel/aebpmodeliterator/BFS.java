package com.sri.ai.praise.core.inference.byinputrepresentation.interfacebased.core.exactbp.anytime.gabriel.aebpmodel.aebpmodeliterator;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

import com.sri.ai.praise.core.inference.byinputrepresentation.interfacebased.core.exactbp.anytime.gabriel.aebpmodel.AEBPModel;
import com.sri.ai.praise.core.inference.byinputrepresentation.interfacebased.core.exactbp.anytime.gabriel.aebptree.AEBPFactorTreeNode;
import com.sri.ai.praise.core.inference.byinputrepresentation.interfacebased.core.exactbp.anytime.gabriel.aebptree.AEBPVariableTreeNode;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.Factor;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.Variable;

public class BFS extends AbstractAEBPTreeIterator{
	private Set<Factor> visited = new LinkedHashSet<>();
	private Queue<AEBPFactorTreeNode> queue = new LinkedList<>();
	
	public BFS(AEBPModel model) {
		super(model);
		
		this.fromNodeToPartition.put(model.getQuery(),getRootOfTree());
		 
		//for each query neighbor
		for(Factor factor : model.getEntireGraph().getNeighbors(getRootOfTree().getRoot())) {
			AEBPFactorTreeNode factorNode = new AEBPFactorTreeNode(factor, getRootOfTree(), isExhausted);
			//getRootOfTree().addChild(factorNode);
			visited.add(factor);
			queue.add(factorNode);
			fromNodeToPartition.put(factor, factorNode);
		} 
	 }
	
	@Override
	public boolean hasNext() {
		return !this.queue.isEmpty();
	}

	@Override
	public AEBPFactorTreeNode next() {
		if (!hasNext()) {
            throw new NoSuchElementException();
        }
		
		AEBPFactorTreeNode next = queue.remove();
		
		for(Variable variableNeighbour : model.getEntireGraph().getNeighbors(next.getRoot())) {
			AEBPVariableTreeNode variableTreeNode;
			if(fromNodeToPartition.get(variableNeighbour) == null) {
				variableTreeNode = new AEBPVariableTreeNode(variableNeighbour, next, isExhausted);
				next.addChild(variableTreeNode);
				fromNodeToPartition.put(variableNeighbour, variableTreeNode);
			}
			else {
				variableTreeNode = (AEBPVariableTreeNode) fromNodeToPartition.get(variableNeighbour);
			}
			for(Factor factorNeighbour : model.getEntireGraph().getNeighbors(variableNeighbour)){
				if(!visited.contains(factorNeighbour)) {
					AEBPFactorTreeNode factorTreeNode =
							new AEBPFactorTreeNode(factorNeighbour, variableTreeNode, isExhausted);
					//variableTreeNode.addChild(factorTreeNode);
					
					visited.add(factorNeighbour);
					queue.add(factorTreeNode);
				}
			}
		}
		
		return next;
	}


}

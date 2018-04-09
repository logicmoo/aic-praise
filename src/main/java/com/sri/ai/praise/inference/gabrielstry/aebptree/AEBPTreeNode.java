package com.sri.ai.praise.inference.gabrielstry.aebptree;

import java.util.List;

import com.sri.ai.praise.inference.gabrielstry.Approximations.api.Approximation;
import com.sri.ai.praise.inference.representation.api.Factor;
import com.sri.ai.praise.inference.representation.api.Variable;

public interface AEBPTreeNode<RootNode,ParentNode> {
	public Factor messageSent();
	
	RootNode getRoot();
	AEBPTreeNode<ParentNode,RootNode> getParent();
	List<AEBPTreeNode<ParentNode,RootNode>> getChildren();
	
	void addChild(AEBPTreeNode<ParentNode, RootNode> node);

	default boolean isRootAFactor() {
		return getRoot() instanceof Factor;
	}
	default boolean isRootAVariable() {
		return getRoot() instanceof Variable;
	}
}

package com.sri.ai.praise.learning.parameterlearning;

import java.util.List;

import com.sri.ai.praise.core.representation.interfacebased.factor.api.Variable;

public interface Datapoint {
	
	public Object getValueOfVariable(Variable variable);
	
	public List<? extends Object> getValuesOfVariables(List<? extends Variable> variables);
}

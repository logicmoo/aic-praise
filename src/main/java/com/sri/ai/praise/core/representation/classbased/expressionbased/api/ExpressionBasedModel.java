package com.sri.ai.praise.core.representation.classbased.expressionbased.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sri.ai.expresso.api.Expression;
import com.sri.ai.expresso.api.Type;
import com.sri.ai.grinder.api.Context;
import com.sri.ai.grinder.api.Theory;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.Model;
import com.sri.ai.praise.other.integration.proceduralattachment.api.ProceduralAttachments;

public interface ExpressionBasedModel extends Cloneable, Model {

	Context getContext();

	List<Expression> getFactors();

	List<Expression> getRandomVariables();

	ExpressionBasedModel getConditionedModel(Expression evidence);

	Map<String, String> getMapFromRandomVariableNameToTypeName();

	Map<String, String> getMapFromNonUniquelyNamedConstantNameToTypeName();

	Map<String, String> getMapFromUniquelyNamedConstantNameToTypeName();

	Map<String, String> getMapFromCategoricalTypeNameToSizeString();

	Collection<Type> getAdditionalTypes();

	boolean isKnownToBeBayesianNetwork();

	Theory getTheory();

	void setTheory(Theory newTheory);
	
	ProceduralAttachments getProceduralAttachments();
	
	void setProceduralAttachments(ProceduralAttachments proceduralAttachments);

	String toString();

	ExpressionBasedModel clone();

}
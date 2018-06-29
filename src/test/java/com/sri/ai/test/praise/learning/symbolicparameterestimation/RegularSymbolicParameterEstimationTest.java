package com.sri.ai.test.praise.learning.symbolicparameterestimation;

import static com.sri.ai.expresso.helper.Expressions.parse;
import static com.sri.ai.praise.learning.symbolicparameterestimation.util.ExpressionBasedModelExamples.buildModel1;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sri.ai.expresso.api.Expression;
import com.sri.ai.praise.core.representation.classbased.expressionbased.api.ExpressionBasedModel;
import com.sri.ai.praise.learning.symbolicparameterestimation.RegularSymbolicParameterEstimation;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for Regular Symbolic  Parameter Estimation.
 * 
 * @author Sarah Perrin
 *
 */

public class RegularSymbolicParameterEstimationTest {
	
	@Test
	public void testCountOccurencesGivenParameter() {

		List<Expression> queryExpressionList = new LinkedList<Expression>();
		queryExpressionList.add(parse("earthquake and burglary"));
		queryExpressionList.add(parse("earthquake"));
		queryExpressionList.add(parse("earthquake"));
		queryExpressionList.add(parse("burglary"));

		ExpressionBasedModel expressionBasedModel = buildModel1();
		RegularSymbolicParameterEstimation regularParameterEstimation = new RegularSymbolicParameterEstimation(expressionBasedModel, queryExpressionList);
		int nbAlpha = regularParameterEstimation.countOccurencesGivenParameter(parse("Alpha"));
		int nbBeta = regularParameterEstimation.countOccurencesGivenParameter(parse("Beta"));
		
		Assert.assertEquals(3, nbAlpha);
		Assert.assertEquals(2, nbBeta);
	}
	
	@Test
	public void testOptimization() {
		
		ExpressionBasedModel expressionBasedModel = buildModel1();
		
		List<Expression> queryExpressionList = new LinkedList<Expression>();
		queryExpressionList.add(parse("not earthquake and not burglary"));
		queryExpressionList.add(parse("not earthquake and not burglary"));
		queryExpressionList.add(parse("earthquake and burglary"));
		queryExpressionList.add(parse("not earthquake and burglary"));
		queryExpressionList.add(parse("earthquake and not burglary"));
		queryExpressionList.add(parse("earthquake and burglary"));
		queryExpressionList.add(parse("earthquake and burglary"));
		queryExpressionList.add(parse("earthquake and burglary"));
		queryExpressionList.add(parse("earthquake and burglary"));
		queryExpressionList.add(parse("not earthquake and burglary"));

		RegularSymbolicParameterEstimation regularParameterEstimation = new RegularSymbolicParameterEstimation(expressionBasedModel, queryExpressionList);
		
		Map<Expression, Double> result = regularParameterEstimation.optimize();
		
		System.out.println(result);
		
	}

}

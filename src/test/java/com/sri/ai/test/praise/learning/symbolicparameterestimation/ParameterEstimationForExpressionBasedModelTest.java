package com.sri.ai.test.praise.learning.symbolicparameterestimation;

import static com.sri.ai.expresso.helper.Expressions.parse;
import static com.sri.ai.grinder.library.number.Times.getMultiplicands;
import static com.sri.ai.praise.learning.symbolicparameterestimation.util.UsefulOperationsParameterEstimation.buildOptimizedExpressionBasedModel;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.map;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.junit.Test;

import com.sri.ai.expresso.api.Expression;
import com.sri.ai.praise.core.representation.classbased.expressionbased.api.ExpressionBasedModel;
import com.sri.ai.praise.core.representation.classbased.expressionbased.core.DefaultExpressionBasedModel;
import com.sri.ai.praise.learning.symbolicparameterestimation.ParameterEstimationForExpressionBasedModel;
import com.sri.ai.praise.learning.symbolicparameterestimation.util.ExpressionBasedModelExamples;

public class ParameterEstimationForExpressionBasedModelTest {

	@Test
	public void testExpressionBased() {
		
		ExpressionBasedModel expressionBasedModel = ExpressionBasedModelExamples.buildModel1();

		List<Expression> queryExpressionList = new LinkedList<Expression>();
		queryExpressionList.add(parse("earthquake"));
		queryExpressionList.add(parse("not earthquake"));

		HashMap<Expression,Double> expected = new HashMap<Expression,Double>();
		expected.put(parse("Alpha"), 0.5);
		
		HashMap<Expression,Double> mapResult = runTestExpressionBased(queryExpressionList,
				expressionBasedModel, new double[] {0});

		System.out.println("expected : " + expected);
		System.out.println("result : " + mapResult);
		assertEquals(expected, mapResult);

		queryExpressionList.clear();
		queryExpressionList.add(parse("earthquake"));
		queryExpressionList.add(parse("earthquake"));

		expected.put(parse("Alpha"), 1.0);

		mapResult = runTestExpressionBased(queryExpressionList,
				expressionBasedModel, new double[] {0});

		System.out.println("expected : " + expected);
		System.out.println("result : " + mapResult);
		assertEquals(expected, mapResult);

		queryExpressionList.clear();
		queryExpressionList.add(parse("not earthquake"));
		queryExpressionList.add(parse("not earthquake"));

		expected.put(parse("Alpha"), 1.4905019930082135E-22);

		mapResult = runTestExpressionBased(queryExpressionList,
				expressionBasedModel, new double[] {0});

		System.out.println("expected : " + expected);
		System.out.println("result : " + mapResult);
		assertEquals(expected, mapResult);




		queryExpressionList.clear();
		queryExpressionList.add(parse("not earthquake"));
		queryExpressionList.add(parse("earthquake"));
		queryExpressionList.add(parse("earthquake"));
		queryExpressionList.add(parse("earthquake"));
		queryExpressionList.add(parse("earthquake"));
		queryExpressionList.add(parse("earthquake"));
		queryExpressionList.add(parse("earthquake"));
		queryExpressionList.add(parse("earthquake"));
		queryExpressionList.add(parse("earthquake"));
		queryExpressionList.add(parse("earthquake"));

		expected.put(parse("Alpha"), 0.9000011823080596);

		mapResult = runTestExpressionBased(queryExpressionList,
				 expressionBasedModel, new double[] {0});

		System.out.println("expected : " + expected);
		System.out.println("result : " + mapResult);
		assertEquals(expected, mapResult);

		queryExpressionList.clear();
		queryExpressionList.add(parse("not earthquake"));
		queryExpressionList.add(parse("burglary"));

		expected.put(parse("Alpha"), 6.289892249011522E-23);
		expected.put(parse("Beta"), 1.0);

		mapResult = runTestExpressionBased(queryExpressionList, 
				expressionBasedModel, new double[] {0,0});

		System.out.println("expected : " + expected);
		System.out.println("result : " + mapResult);
		assertEquals(expected, mapResult);
		
		//////////
		
		queryExpressionList.clear();
		queryExpressionList.add(parse("earthquake and not burglary"));

		expected.put(parse("Alpha"), 1.0);
		expected.put(parse("Beta"), 6.289892248981177E-23);

		mapResult = runTestExpressionBased(queryExpressionList,
				 expressionBasedModel, new double[] {0,0});

		System.out.println("expected : " + expected);
		System.out.println("result : " + mapResult);
		assertEquals(expected, mapResult);
		
		//////////
		

		// Test with another model

		queryExpressionList.clear();
		queryExpressionList.add(parse("earthquake"));
		expected.remove(parse("Beta"));
		expected.put(parse("Alpha"), 1.0); 
		
		ExpressionBasedModel expressionBasedModel2 = ExpressionBasedModelExamples.buildModel4();
		
		System.out.println(expressionBasedModel2);
		
		mapResult = runTestExpressionBased(queryExpressionList, 
				expressionBasedModel2, new double[] {0});

		System.out.println("expected : " + expected);
		System.out.println("result : " + mapResult);
		assertEquals(expected, mapResult);
		
		// Test with other parameters to learn

		queryExpressionList.clear();
		queryExpressionList.add(parse("earthquake and alarm"));
		queryExpressionList.add(parse("earthquake and not alarm"));
		
		expected.put(parse("Alpha"), 0.5); 
		expected.put(parse("Beta"), 0.5); 
		
		ExpressionBasedModel expressionBasedModel3 = ExpressionBasedModelExamples.buildModel5();

		System.out.println(expressionBasedModel3);

		mapResult = runTestExpressionBased(queryExpressionList, 
				expressionBasedModel3, new double[] {0,0});

		System.out.println("expected : " + expected);
		System.out.println("result : " + mapResult);
		assertEquals(expected, mapResult);

	}
	
	@Test
	
	public void testBuildOptimizedExpressionBasedModel() {
		
		// The definitions of types
				Map<String, String> mapFromCategoricalTypeNameToSizeString = map();

				// The definitions of variables
				Map<String, String> mapFromRandomVariableNameToTypeName = map(
						"earthquake", "Boolean",
						"burglary",    "Boolean", 
						"alarm",      "Boolean"
						);

				// The definitions of non-uniquely named constants
				Map<String, String> mapFromNonUniquelyNamedConstantNameToTypeName = map(
						"Alpha", "Real",
						"Beta", "Real"
						);

				// The definitions of non-uniquely named constants
				Map<String, String> mapFromUniquelyNamedConstantNameToTypeName = map();

				// a variant of the earthquake/burglary model in which some burglars are more active than others.
				boolean isBayesianNetwork = true;
				List<Expression> factors = getMultiplicands(parse("" + 
						"(if earthquake then Alpha else 1-Alpha) * " +
						"(if burglary then Beta else 1-Beta) * " +
						// note the division above of the potential by number of remaining values, as the probabilities must sum up to 1
						"(if burglary or earthquake "
						+    "then if alarm then 0.9 else 0.1 "
						+    "else if alarm then 0.05 else 0.95) " +
						""));
				
				ExpressionBasedModel expressionBasedModel = new DefaultExpressionBasedModel(
						factors,
						mapFromRandomVariableNameToTypeName,
						mapFromNonUniquelyNamedConstantNameToTypeName,
						mapFromUniquelyNamedConstantNameToTypeName,
						mapFromCategoricalTypeNameToSizeString,
						list(),
						isBayesianNetwork);
				
				System.out.println(expressionBasedModel);

				List<Expression> queryExpressions = new LinkedList<Expression>();
				queryExpressions.add(parse("earthquake"));
				
				ParameterEstimationForExpressionBasedModel parameterEstimationForExpressionBasedModel = new ParameterEstimationForExpressionBasedModel(expressionBasedModel, queryExpressions);
				HashMap<Expression,Double> result = parameterEstimationForExpressionBasedModel.optimize(
						expressionBasedModel,
						queryExpressions,
						GoalType.MAXIMIZE,
						new double[] {0});
				ExpressionBasedModel newModel = buildOptimizedExpressionBasedModel(result, expressionBasedModel);
				
				System.out.println(newModel);
				
				List<Expression> queryExpressions2 = new LinkedList<Expression>();
				queryExpressions2.add(parse("burglary"));
				ParameterEstimationForExpressionBasedModel parameterEstimationForExpressionBasedModel2 = new ParameterEstimationForExpressionBasedModel(newModel, queryExpressions2);
				HashMap<Expression,Double> result2 = parameterEstimationForExpressionBasedModel2.optimize(
						newModel,
						queryExpressions2,
						GoalType.MAXIMIZE,
						new double[] {0});
				ExpressionBasedModel newModel2 = buildOptimizedExpressionBasedModel(result2, newModel);
				System.out.println(newModel2);
		
	}

	private HashMap<Expression,Double> runTestExpressionBased(List<Expression> queryExpressions, ExpressionBasedModel expressionBasedModel, double[] startPoint) {

		ParameterEstimationForExpressionBasedModel parameterEstimationForExpressionBasedModel = new ParameterEstimationForExpressionBasedModel(expressionBasedModel, queryExpressions);
		HashMap<Expression,Double> result = parameterEstimationForExpressionBasedModel.optimize(
				expressionBasedModel,
				queryExpressions,
				GoalType.MAXIMIZE,
				startPoint);
		ExpressionBasedModel newModel = buildOptimizedExpressionBasedModel(result, expressionBasedModel);
		System.out.println(" New Model : " + newModel);
		
		return result;

	}
	
}
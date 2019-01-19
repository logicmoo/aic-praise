package com.sri.ai.praise.core.representation.translation.rodrigoframework.fromexpressionstosamplingfactors;

import static com.sri.ai.expresso.helper.Expressions.ONE;
import static com.sri.ai.expresso.helper.Expressions.ZERO;
import static com.sri.ai.expresso.helper.Expressions.getConstantDoubleValueOrThrowErrorWithMessage;
import static com.sri.ai.grinder.library.FunctorConstants.DIVISION;
import static com.sri.ai.grinder.library.FunctorConstants.EQUALITY;
import static com.sri.ai.grinder.library.FunctorConstants.EXPONENTIATION;
import static com.sri.ai.grinder.library.FunctorConstants.MINUS;
import static com.sri.ai.grinder.library.FunctorConstants.PLUS;
import static com.sri.ai.grinder.library.FunctorConstants.TIMES;
import static com.sri.ai.grinder.library.controlflow.IfThenElse.condition;
import static com.sri.ai.grinder.library.controlflow.IfThenElse.elseBranch;
import static com.sri.ai.grinder.library.controlflow.IfThenElse.isIfThenElse;
import static com.sri.ai.grinder.library.controlflow.IfThenElse.thenBranch;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.mapIntoArrayList;
import static com.sri.ai.util.Util.myAssert;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import com.sri.ai.expresso.api.Expression;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.Factor;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.Variable;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.expression.core.DefaultExpressionVariable;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.sampling.core.distribution.EqualitySamplingFactor;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.sampling.core.distribution.NormalWithFixedStandardDeviation;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.sampling.core.factor.ConstantSamplingFactor;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.sampling.core.factor.math.DivisionSamplingFactor;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.sampling.core.factor.math.ExponentiationSamplingFactor;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.sampling.core.factor.math.MultiplicationSamplingFactor;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.sampling.core.factor.math.SubtractionSamplingFactor;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.sampling.core.factor.math.SumSamplingFactor;

public class FromExpressionToSamplingFactors {
	
	private Predicate<Expression> isVariable;

	private Random random;
	
	public FromExpressionToSamplingFactors(Predicate<Expression> isVariable, Random random) {
		this.isVariable = isVariable;
		this.random = random;
	}

	public List<? extends Factor> factorCompilation(Expression expression) {
		List<Factor> factors = list();
		factorCompilation(expression, factors);
		return factors;
	}

	private void factorCompilation(Expression expression, List<Factor> factors) {
		if (isFormulaAssertedToBeTrueWithProbabilityOne(expression)) {
			factorCompilation(condition(expression), factors);
		}
		else if (expression.hasFunctor(EQUALITY)) {
			equalityCompilation(expression, factors);
		}
	}

	private void equalityCompilation(Expression expression, List<Factor> factors) {
		Variable leftHandSideVariable = expressionCompilation(expression.get(0), factors);
		Variable rightHandSideVariable = expressionCompilationWithCompoundExpressionVariableAlreadyAvailable(expression.get(1), leftHandSideVariable, factors);
		boolean leftHandSideVariableWasUsedAsRightHandSideVariable = rightHandSideVariable == leftHandSideVariable;
		if (leftHandSideVariableWasUsedAsRightHandSideVariable) {
			// no need to enforce equality
		}
		else {
			Factor equalityFactor = new EqualitySamplingFactor(leftHandSideVariable, rightHandSideVariable, new Random());
			factors.add(equalityFactor);
		}
	}

	private Variable expressionCompilation(Expression expression, List<Factor> factors) {
		return expressionCompilationWithCompoundExpressionVariableAlreadyAvailable(expression, null, factors);
	}

	private Variable expressionCompilationWithCompoundExpressionVariableAlreadyAvailable(Expression expression, Variable compoundExpressionVariableIfAvailable, List<Factor> factors) {
		Variable variable;
		if (isVariable(expression)) {
			variable = new DefaultExpressionVariable(expression);
		}
		else {
			variable = compoundExpressionCompilation(expression, compoundExpressionVariableIfAvailable, factors);
		}
		return variable;
	}

	private Variable compoundExpressionCompilation(Expression expression, Variable compoundExpressionVariableIfAvailable, List<Factor> factors) throws Error {
		Variable compoundExpressionVariable = compoundExpressionVariableIfAvailable != null? compoundExpressionVariableIfAvailable : new DefaultExpressionVariable(expression);
		if (isConstant(expression)) {
			constantCompilation(compoundExpressionVariable, expression, factors);
		}
		else if (expression.hasFunctor(PLUS)) {
			sumCompilation(compoundExpressionVariable, expression, factors);
		}
		else if (expression.hasFunctor(MINUS) && expression.numberOfArguments() == 2) {
			subtractionCompilation(compoundExpressionVariable, expression, factors);
		}
		else if (expression.hasFunctor(TIMES)) {
			multiplicationCompilation(compoundExpressionVariable, expression, factors);
		}
		else if (expression.hasFunctor(DIVISION)) {
			divisionCompilation(compoundExpressionVariable, expression, factors);
		}
		else if (expression.hasFunctor(EXPONENTIATION)) {
			exponentiationCompilation(compoundExpressionVariable, expression, factors);
		}
		else if (expression.hasFunctor("Normal")) {
			normalCompilation(compoundExpressionVariable, expression, factors);
		}
		return compoundExpressionVariable;
	}

	private void constantCompilation(Variable compoundExpressionVariable, Expression expression, List<Factor> factors) {
		Factor constantFactor = new ConstantSamplingFactor(compoundExpressionVariable, value(expression), new Random());
		factors.add(constantFactor);
	}

	private Object value(Expression expression) {
		Object expressionValue = expression.getValue();
		if (expressionValue instanceof Number) {
			return ((Number) expressionValue).doubleValue();
		}
		else {
			return expressionValue;
		}
	}

	private void sumCompilation(Variable compoundExpressionVariable, Expression expression, List<Factor> factors) {
		ArrayList<Variable> argumentVariables = mapIntoArrayList(expression.getArguments(), a -> expressionCompilation(a, factors));
		Factor sumFactor = new SumSamplingFactor(compoundExpressionVariable, argumentVariables, getRandom());
		factors.add(sumFactor);
	}

	private void subtractionCompilation(Variable compoundExpressionVariable, Expression expression, List<Factor> factors) {
		ArrayList<Variable> argumentVariables = mapIntoArrayList(expression.getArguments(), a -> expressionCompilation(a, factors));
		Factor subtractionFactor = new SubtractionSamplingFactor(compoundExpressionVariable, argumentVariables, getRandom());
		factors.add(subtractionFactor);
	}

	private void multiplicationCompilation(Variable compoundExpressionVariable, Expression expression, List<Factor> factors) {
		ArrayList<Variable> argumentVariables = mapIntoArrayList(expression.getArguments(), a -> expressionCompilation(a, factors));
		Factor sumFactor = new MultiplicationSamplingFactor(compoundExpressionVariable, argumentVariables, getRandom());
		factors.add(sumFactor);
	}

	private void divisionCompilation(Variable compoundExpressionVariable, Expression expression, List<Factor> factors) {
		ArrayList<Variable> argumentVariables = mapIntoArrayList(expression.getArguments(), a -> expressionCompilation(a, factors));
		Factor divisionFactor = new DivisionSamplingFactor(compoundExpressionVariable, argumentVariables, getRandom());
		factors.add(divisionFactor);
	}

	private void exponentiationCompilation(Variable compoundExpressionVariable, Expression expression, List<Factor> factors) {
		ArrayList<Variable> argumentVariables = mapIntoArrayList(expression.getArguments(), a -> expressionCompilation(a, factors));
		Factor exponentiationFactor = new ExponentiationSamplingFactor(compoundExpressionVariable, argumentVariables, getRandom());
		factors.add(exponentiationFactor);
	}

	private void normalCompilation(Variable compoundExpressionVariable, Expression expression, List<Factor> factors) throws Error {
		int numberOfArguments = expression.numberOfArguments();
		myAssert(numberOfArguments == 2, () -> "the Normal distribution must have two arguments, but this one has " + expression.getArguments());
		Variable meanVariable = expressionCompilation(expression.get(0), factors);
		double standardDeviation = getStandardDeviation(expression);
		Factor normalFactor = new NormalWithFixedStandardDeviation(compoundExpressionVariable, meanVariable, standardDeviation, getRandom());
		factors.add(normalFactor);
	}

	private double getStandardDeviation(Expression expression) throws Error {
		double standardDeviation = 
				getConstantDoubleValueOrThrowErrorWithMessage(
						expression.get(1), 
						"Normal must take a constant standard deviation, but got " + expression.get(1) + " instead");
		return standardDeviation;
	}

	private boolean isVariable(Expression expression) {
		boolean result = isVariable.test(expression);
		return result;
	}

	private boolean isConstant(Expression expression) {
		boolean result = 
				expression.getSyntacticFormType().equals("Symbol") 
				&& 
				! isVariable(expression);
		return result;
	}

	public Random getRandom() {
		return random;
	}

	private boolean isFormulaAssertedToBeTrueWithProbabilityOne(Expression expression) {
		return isIfThenElse(expression) && thenBranch(expression).equals(ONE) && elseBranch(expression).equals(ZERO);
	}
	
	

}

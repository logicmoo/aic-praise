package com.sri.ai.praise.core.inference.byinputrepresentation.classbased.expressionbased.core;

import com.sri.ai.expresso.api.Expression;
import com.sri.ai.praise.core.inference.byinputrepresentation.classbased.expressionbased.api.ExpressionBasedSolver;
import com.sri.ai.praise.core.representation.classbased.expressionbased.api.ExpressionBasedProblem;

public abstract class AbstractExpressionBasedSolver implements ExpressionBasedSolver {

	protected abstract Expression solveForQuerySymbolDefinedByExpressionBasedProblem(ExpressionBasedProblem problem);

	@Override
	public Expression solve(ExpressionBasedProblem problem) {
		Expression normalizedMarginal = solveForQuerySymbolDefinedByExpressionBasedProblem(problem);
		Expression result = replaceQuerySymbolByQueryExpressionIfNeeded(problem, normalizedMarginal);
		return result;
	}

	protected Expression replaceQuerySymbolByQueryExpressionIfNeeded(ExpressionBasedProblem problem, Expression normalizedMarginal) {
		Expression result = problem.replaceQuerySymbolByQueryExpressionIfNeeded(normalizedMarginal);
		return result;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
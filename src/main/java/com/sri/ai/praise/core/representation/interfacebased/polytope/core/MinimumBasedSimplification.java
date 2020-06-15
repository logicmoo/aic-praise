/*
 * Copyright (c) 2015, SRI International
 * All rights reserved.
 * Licensed under the The BSD 3-Clause License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * 
 * http://opensource.org/licenses/BSD-3-Clause
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the aic-praise nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.sri.ai.praise.core.representation.interfacebased.polytope.core;

import static com.sri.ai.praise.core.representation.interfacebased.polytope.core.IdentityPolytope.IDENTITY_POLYTOPE;
import static com.sri.ai.praise.core.representation.interfacebased.polytope.core.ProductPolytope.makeEquivalentToProductOf;
import static com.sri.ai.praise.core.representation.interfacebased.polytope.core.Simplex.productOfSimplices;
import static com.sri.ai.util.Util.intersect;
import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.listFrom;
import static com.sri.ai.util.Util.mapIntoList;
import static com.sri.ai.util.Util.pair;
import static com.sri.ai.util.Util.println;
import static com.sri.ai.util.Util.product;
import static com.sri.ai.util.Util.thereExists;
import static com.sri.ai.util.Util.unionOfResults;
import static com.sri.ai.util.base.IsInstanceOf.isInstanceOf;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;
import static com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger.explain;
import static com.sri.ai.util.graph.JGraphTUtil.getConnectedSets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.sri.ai.praise.core.representation.interfacebased.factor.api.Factor;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.Variable;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.table.api.TableFactor;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.table.core.base.TableVariable;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.table.core.bydatastructure.arraylist.ArrayTableFactor;
import com.sri.ai.praise.core.representation.interfacebased.polytope.api.AtomicPolytope;
import com.sri.ai.praise.core.representation.interfacebased.polytope.api.FunctionConvexHull;
import com.sri.ai.praise.core.representation.interfacebased.polytope.api.Polytope;
import com.sri.ai.util.Timer;
import com.sri.ai.util.Util;
import com.sri.ai.util.base.Pair;
import com.sri.ai.util.base.Wrapper;

/**
 * A simplification of polytope bounds that decreases their space complexity at the cost of tightness.
 * 
 * It is based on the simplification of a {@link FunctionConvexHull} based on function f(I, FreeVariables) to the product polytope
 * <code>prod_{F in FreeVariables} Margin_Simplex( NormalizedProjection(F) )</code> 
 * where
 * <code>NormalizedProjection_F = normalize_F sum_{FreeVariables \ {F}} ConvexHull ( { f(Indices, FreeVariables) }_Indices ) </code>
 * and
 * <code>Margin_Simplex(ConvexHull ( { f(I, V) }_I ) ) = { Vertex_v : v in Values(V) }</code>
 * and
 * <code>Vertex_v = ( min_I f(I,v) if u = v, max_I f(I,v) otherwise }_{u in Values(V)}</code>
 * <p>
 * Intuitively, we replace the convex hull by a product of the "margin simplexes" on each free variable.
 * A "margin simplex" is a convex hull of the same shape as the variable's polytope
 * (therefore with the same number of vertices, which is to say the variable's number of values),
 * but reduced in size.
 * It therefore has one vertex per value.
 * The vertex for a certain value is a distribution in which all other values have the minimum probability
 * the exhibit in the original convex hull,
 * while the special value has the maximum probability
 * (computed as 1 - the sum of the minimal probabilities of the remaining values).
 * 
 * For identity polytopes, the simplification simply returns the same.
 * 
 * For product polytopes, the simplification separates sets of atomic polytopes that share variables
 * and simplifies each of these subsets.
 * For subsets containing function convex hulls only, we compute an equivalent function convex hull and simplify it.
 * For subsets containing simplices, the result is the product of simplices of the free variables, based on:
 * S_x * { phi(x,y,i) }_i
 * =
 * { (x=x') }_x' * { phi(x,y,i) }_i
 * =
 * { (x=x') * phi(x,y,i) }_i,_x'
 * =
 * { if x=x' then phi(x,y,i) else 0 }_i,_x'
 * 
 * This is a set of distributions { P_i,x'(x, y) }_i,x'.
 * 
 * We want M(x,y) = min_i,x' P_i,x'(x,y)
 * 
 * min_i,x' P_i,x'(x,y)
 * =
 * min( min_i P_i,x(x,y), min_i,x':x'!=x P_i,x'(x,y) )
 * =
 * min( min_i P_i,x(x,y), 0 )
 * =
 * 0
 * If each assignment to free variables has a minimum of 0, then there will be a vertex in which its probability is 1,
 * thus forming a simplex of free variables, which is equivalent to the product of simplices of individual free
 * variables.
 * 
 * @author braz
 *
 */
final public class MinimumBasedSimplification {
	
	private static final boolean debug = false;
	
	private static final boolean USE_SIMPLICES_OPTIMIZATION = true;

	public static Polytope simplify(Polytope polytope, boolean forced) {
		if (polytope.isIdentity()) {
			return IDENTITY_POLYTOPE;
		}
		else if (polytope instanceof FunctionConvexHull) {
			return simplify((FunctionConvexHull) polytope, forced);
		}
		else if (polytope instanceof ProductPolytope){
			return simplifyProductPolytope(polytope, forced);
		}
		else { // no simplification available
			return polytope;
		}
	}

	private static Polytope simplifyProductPolytope(Polytope nonIdentityPolytope, boolean forced) {
		var connectedSets = 
				getConnectedSets(
						nonIdentityPolytope.getAtomicPolytopes(), 
						(a1, a2) -> intersect(a1.getFreeVariables(), a2.getFreeVariables()));
		return simplifyConnectedSets(connectedSets, forced);
	}

	private static
	Polytope
	simplifyConnectedSets(List<? extends Set<? extends AtomicPolytope>> connectedSets, boolean forced) {
		var simplifiedConnectedSets = mapIntoList(connectedSets, cs -> simplifyConnectedSet(cs, forced));
		return Polytope.multiply(simplifiedConnectedSets);
	}

	private static Polytope simplifyConnectedSet(Set<? extends AtomicPolytope> connectedSet, boolean forced) {
		if (USE_SIMPLICES_OPTIMIZATION && thereExists(connectedSet, isInstanceOf(Simplex.class))) {
			if (forced) {
				return simplifyConnectedSetWithSimplicesOptimization(connectedSet);
			}
			else {
				// No point simplifying to a trivial bound!
				return ProductPolytope.makeEquivalentToProductOf(connectedSet);
			}
		}
		else {
			return simplifyConnectedSetWithoutSimplicesOptimization(connectedSet, forced);
		}
	}

	private static Polytope simplifyConnectedSetWithSimplicesOptimization(Set<? extends AtomicPolytope> connectedSet) {
		var connectedSetFreeVariables = unionOfResults(connectedSet, Polytope::getFreeVariables);
		return productOfSimplices(connectedSetFreeVariables);
	}

	private static Polytope simplifyConnectedSetWithoutSimplicesOptimization(
			Set<? extends AtomicPolytope> connectedSet,
			boolean forced) {
		
		var setProduct = makeEquivalentToProductOf(connectedSet);
		var atomicPolytope = setProduct.getEquivalentAtomicPolytope();
		var result = simplify(atomicPolytope, forced);
		return result;
	}
	
	/**
	 * Computes simplification.
	 * @param convexHull function convex hull to be simplified
	 * @return simplified polytope
	 */
	public static FunctionConvexHull simplify(FunctionConvexHull convexHull) {
		return simplify(convexHull, false /* forced is false by default */);
	}
	
	/**
	 * Computes simplification.
	 * @param convexHull function convex hull to be simplified
	 * @param forced whether to force the simplification even if summation cost (representation size) is not decreased by simplification.
	 * @return simplified polytope
	 */
	public static FunctionConvexHull simplify(FunctionConvexHull convexHull, boolean forced) {
		var summationCostOfIndices = summationCost(convexHull.getIndices());
		var summationCostOfFreeVariables = summationCost(convexHull.getFreeVariables());
		if ( !forced && summationCostOfFreeVariables >= summationCostOfIndices) {
			return convexHull;
		}
		
		if (debug) {
			println();
			println("Before simplification: " + convexHull.getFactor().summationCost());
			println("              indices: " + convexHull.getIndices() + " with cardinalities " + getIndicesCardinalities(convexHull));
			println("       free variables: " + convexHull.getFreeVariables());
			println("          convex hull: " + convexHull);
		}

		var normalized = convexHull.normalize(convexHull.getFreeVariables());
		var result = Timer.getResultAndTime(() -> makeMarginSimplex(normalized));

		if (debug) {
			println("After  simplification: " + result.first.getFactor().summationCost());
			println("              indices: " + result.first.getIndices() + " with cardinalities " + getIndicesCardinalities(result.first));
			println("       free variables: " + result.first.getFreeVariables());
		}
		
		return result.first;
	}

	@SuppressWarnings("unchecked")
	private static String getIndicesCardinalities(FunctionConvexHull functionConvexHull) {
		return join(mapIntoList((Collection<? extends TableVariable>) functionConvexHull.getIndices(), TableVariable::getCardinality));
	}

	public static int summationCost(Collection<? extends Variable> variables) {
		return product(functionIterator(variables, v -> ((TableVariable)v).getCardinality())).intValue();
	}

	public static FunctionConvexHull makeMarginSimplex(FunctionConvexHull normalizedConvexHull) {
		var indicesAndFactor = makeMarginSimplexIndicesAndFactor(normalizedConvexHull);
		return normalizedConvexHull.newInstance(indicesAndFactor.first, indicesAndFactor.second);
	}

	/**
	 * Given a normalized function convex hull, determines the minimum probability
	 * for each assignment to the free variables
	 * when ranging over all index assignments,
	 * and then builds the indices and factor for a new normalized convex hull with one vertex per assignment A to free variables,
	 * equal to the distribution in which A has its maximum probability and all remaining assignments have their minimum probability.
	 * The maximum probability of A is determined by <code>1 - sum_{A' != A} min P(A')</code>.
	 * Because the elements of this new convex hull correspond to assignments to free variables,
	 * its indices are copies of these free variables.
	 * These copies are present also in the factor (since it must depend on the indices),
	 * and are placed in the first half of the list of factor variables.
	 */
	public static 
	Pair<? extends List<? extends Variable>, Factor> 
	makeMarginSimplexIndicesAndFactor(FunctionConvexHull normalizedConvexHull) {
		explain("Normalized: ", normalizedConvexHull);
		var minima = normalizedConvexHull.getFactor().min(normalizedConvexHull.getIndices());
		explain("minima: ", minima);
		var marginSimplexFactor = marginSimplexFactor(minima);
		var numberOfMarginSimplexIndices = marginSimplexFactor.getVariables().size()/2;
		var marginSimplexIndices = marginSimplexFactor.getVariables().subList(0, numberOfMarginSimplexIndices);
		return pair(marginSimplexIndices, marginSimplexFactor);
	}

	/**
	 * Takes a factor containing the minimum probabilities for the values of a variable,
	 * and returns a new factor with twice the dimensions as the original one,
	 * with the first half of the dimensions being new index variables (I0, I1, ...)
	 * equal to the original variables in cardinality.
	 * Each assignment A to the tuple (I0, I1, ...) maps to a probability distribution
	 * on the values of the original variables with probabilities equal to the given minima,
	 * with the exception of the position corresponding to the same assignment A but to the original variables
	 * (so it's the diagonal if we consider (I0, I1, ...) assignments as the row and original variable assignments as the column).
	 * The probability at this position is instead (1 - sum of minima in row), that is, the maximum possible probability for that assignment
	 * given all the minima.
	 * @param minima
	 * @return
	 */
	private static Factor marginSimplexFactor(Factor minima) {
		TableFactor tableFactor = (TableFactor) minima;
		int numberOfAssignments = tableFactor.numberOfEntries();
		ArrayList<Double> array = tableFactor.getEntries();
		double sumOfMinima = 0;
		for (int col = 0; col != numberOfAssignments; col++) {
			sumOfMinima += array.get(col);
		}
		double newArray[] = new double[numberOfAssignments * numberOfAssignments];
		for (int row = 0; row != numberOfAssignments; row++) {
			for (int col = 0; col != numberOfAssignments; col++) {
				newArray[row*numberOfAssignments + col] = array.get(col); 
			}
			int offsetForDiagonalColumnInThisRow = row*numberOfAssignments + row;
			newArray[offsetForDiagonalColumnInThisRow] = 1.0 - (sumOfMinima - array.get(row)); 
		}
		var indexIndex = new Wrapper<Integer>(0);
		var indexVariables = mapIntoList(tableFactor.getVariables(), v -> new TableVariable("I" + indexIndex.value++, v.getCardinality()));
		var newVariables = Util.makeListWithElementsOfTwoCollections(indexVariables, tableFactor.getVariables());
		return new ArrayTableFactor(newVariables, newArray);
	}

	/**
	 * A even more aggressive relaxation that decouples variables; to try later.
	 * @param normalizedConvexHull
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Polytope getDecoupledMarginSimplexProduct(FunctionConvexHull normalizedConvexHull) {
		Collection<? extends Variable> freeVariables = normalizedConvexHull.getFreeVariables();
		List<Variable> freeVariablesBuffer = listFrom(freeVariables);
		var marginSimplices = mapIntoList(freeVariables, v -> getMarginSimplexOnSingleVariable(normalizedConvexHull, v, freeVariablesBuffer));
		return ProductPolytope.makeEquivalentToProductOf(marginSimplices);
	}

	private static FunctionConvexHull getMarginSimplexOnSingleVariable(
			FunctionConvexHull normalizedConvexHull, 
			Variable v, 
			List<Variable> freeVariablesBuffer) {
	
		freeVariablesBuffer.remove(v);
	
		var otherFreeVariables = freeVariablesBuffer;
		var projection = normalizedConvexHull.sumOut(otherFreeVariables); 
		var normalizedProjection = projection.normalize(list(v));
		var result = makeMarginSimplex(normalizedProjection);
	
		freeVariablesBuffer.add(v);
	
		return result;
	}
}
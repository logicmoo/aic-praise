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
package com.sri.ai.praise.core.inference.byinputrepresentation.interfacebased.exactbp.anytime.rodrigo;

import static com.sri.ai.praise.core.representation.interfacebased.polytope.core.AbstractFunctionConvexHull.multiplyIntoSingleFunctionConvexHull;
import static com.sri.ai.praise.core.representation.interfacebased.polytope.core.IdentityPolytope.identityPolytope;
import static com.sri.ai.util.Util.accumulate;
import static com.sri.ai.util.Util.collectToList;
import static com.sri.ai.util.Util.getFirst;
import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.listFrom;
import static com.sri.ai.util.Util.mapIntoList;
import static com.sri.ai.util.Util.println;
import static com.sri.ai.util.Util.setFrom;
import static com.sri.ai.util.Util.sortByString;
import static com.sri.ai.util.Util.subtract;
import static com.sri.ai.util.Util.union;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sri.ai.praise.core.inference.byinputrepresentation.interfacebased.exactbp.fulltime.api.ExactBPNode;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.Factor;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.Variable;
import com.sri.ai.praise.core.representation.interfacebased.polytope.api.AtomicPolytope;
import com.sri.ai.praise.core.representation.interfacebased.polytope.api.FunctionConvexHull;
import com.sri.ai.praise.core.representation.interfacebased.polytope.api.Polytope;
import com.sri.ai.praise.core.representation.interfacebased.polytope.core.DefaultFunctionConvexHull;
import com.sri.ai.praise.core.representation.interfacebased.polytope.core.ProductPolytope;
import com.sri.ai.praise.core.representation.interfacebased.polytope.core.Simplex;
import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.collect.NestedIterator;
import com.sri.ai.util.computation.anytime.api.Anytime;
import com.sri.ai.util.computation.anytime.api.Approximation;
import com.sri.ai.util.computation.treecomputation.anytime.core.AbstractAnytimeTreeComputationBasedOnTreeComputation;

/**
 * An anytime version of {@link ExactBPNode} algorithms.
 * This is implemented as a {@link AbstractAnytimeTreeComputationBasedOnTreeComputation}
 * based on an {@link ExactBPNode}, which is gradually expanded.
 * <p>
 * It uses {@link Simplex} as an initial approximation,
 * and computes an approximation to the base's answer by not summing out the indices whose sub-messages
 * are simplexes (justification of this is to be found in the related publications).
 * 
 * @author braz
 *
 */
public class OldAnytimeExactBP<RootType,SubRootType> extends AbstractAnytimeTreeComputationBasedOnTreeComputation<Factor> {
	
	public static final boolean debug = true;

	///////////////// DATA MEMBERS
	
	private Iterator<? extends OldAnytimeExactBP<SubRootType,RootType>> subRoundRobinIterator;

	///////////////// CONSTRUCTOR
	
	public OldAnytimeExactBP(ExactBPNode<RootType,SubRootType> base) {
		super(base, new Simplex(base.getMessageVariable()));
	}

	///////////////// IMPLEMENTATIONS
	
	@Override
	public ArrayList<? extends OldAnytimeExactBP<SubRootType,RootType>> makeSubs() {
		@SuppressWarnings("unchecked")
		var newlyMadeSubs = (ArrayList<? extends OldAnytimeExactBP<SubRootType, RootType>>) super.makeSubs();
		subRoundRobinIterator = newlyMadeSubs.iterator();
		return newlyMadeSubs;
	}

	@Override
	public boolean evenOneSubWithTotalIgnoranceRendersApproximationEqualToTotalIgnorance() {
		boolean result = getBase().getRoot() instanceof Variable;
		return result;
	}

	@Override
	protected void makeSubsAndIterateThemToTheirFirstApproximation() {
		super.makeSubsAndIterateThemToTheirFirstApproximation();
	}

	@Override
	public OldAnytimeExactBP<SubRootType, RootType> pickNextSubToIterate() {
		
		if (getSubs().isEmpty()) {
			return null;
		}
		
		OldAnytimeExactBP<SubRootType, RootType> subWeStartedWith = getNextInSubRoundRobin();
		
		OldAnytimeExactBP<SubRootType, RootType> nextSubThatCanBeRefined = null; 
		OldAnytimeExactBP<SubRootType, RootType> currentSub = subWeStartedWith; 
		boolean cameBackToTheOneWeStartedWith = false;
		do {
			if (currentSub.hasNext()) {
				nextSubThatCanBeRefined = currentSub;
			}
			else {
				currentSub = getNextInSubRoundRobin();
				if (currentSub == subWeStartedWith) {
					cameBackToTheOneWeStartedWith = true;
				}
			}
		} while (nextSubThatCanBeRefined == null && !cameBackToTheOneWeStartedWith);
		
		return nextSubThatCanBeRefined;
	}

	private OldAnytimeExactBP<SubRootType, RootType> getNextInSubRoundRobin() {
		if (!subRoundRobinIterator.hasNext()) {
			subRoundRobinIterator = getSubs().iterator();
		}
		return subRoundRobinIterator.next();
	}

	@Override
	protected OldAnytimeExactBP<SubRootType,RootType> makeAnytimeVersion(NullaryFunction<Factor> baseSub) {
		@SuppressWarnings("unchecked")
		var baseExactBP = (ExactBPNode<SubRootType, RootType>) baseSub;
		return new OldAnytimeExactBP<SubRootType,RootType>(baseExactBP);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ExactBPNode<RootType,SubRootType> getBase() {
		return (ExactBPNode<RootType,SubRootType>) super.getBase();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<? extends OldAnytimeExactBP<SubRootType,RootType>> getSubs() {
		return (ArrayList<? extends OldAnytimeExactBP<SubRootType,RootType>>) super.getSubs();
	}
	
	@Override
	public Approximation<Factor> function(List<Approximation<Factor>> subsApproximations) {
		
		var productPolytope = getProductOfAllIncomingPolytopesAndFactorAtRoot(subsApproximations);
		var summandVariables = productPolytope.getFreeVariables();
		var variablesToBeSummedOut = getBase().variablesToBeSummedOutAmong(summandVariables);
		
		Approximation<Factor> result = productPolytope.sumOut(variablesToBeSummedOut);
		
		if (debug) {
			println("\nAnytimeExactBP:");
			println("Root: " + getBase().getRoot());
			println("Subs roots:\n" + join("\n", mapIntoList(getSubs(), sub -> sub.getBase().getRoot())));
			println("\nSubs approximations:\n" + join("\n", mapIntoList(getSubs(), sub -> sub.getCurrentApproximation())));
			println("Summand (product of all factors):", productPolytope);
			println("Summand variables              :", join(summandVariables));
			println("Free variables in summand      :", join(collectToList(summandVariables, getBase()::isFreeVariable)));
			println("Summed out variables in summand:", join(variablesToBeSummedOut));
			println("Result of summing out : " + result);

			Variable indexThatShouldBeFreeInPolytope = violatingIndex_DEBUG(result);

			if (indexThatShouldBeFreeInPolytope != null) {
				println("AnytimeExactBP: found assignment that is free variable but is in polytope's assignment: " + indexThatShouldBeFreeInPolytope);
				println("Polytope: "  + result);
				System.exit(-1);
			}
		}

		return result;
	}

	@Override
	protected Approximation<Factor> getCurrentApproximationForSub(Anytime<Factor> sub) {
		
		Approximation<Factor> subCurrentApproximation = super.getCurrentApproximationForSub(sub);
		
		if (debug) {
			var indexThatShouldBeFree = violatingIndex_DEBUG(subCurrentApproximation);
			if (indexThatShouldBeFree != null) {
				println("AnytimeExactBP.getCurrentApproximationForSub: found assignment that is free variable but is in sub's result's assignment: " + indexThatShouldBeFree);
				println("Sub: " + sub);
				println("Sub's result: "  + subCurrentApproximation);
				System.exit(-1);
			}
		}
		
		return subCurrentApproximation;
	}

	private Polytope getProductOfAllIncomingPolytopesAndFactorAtRoot(List<Approximation<Factor>> subsApproximations) {
		List<Polytope> polytopesToMultiply = getAllPolytopes(subsApproximations);
		Polytope result = accumulate(polytopesToMultiply, Polytope::multiply, identityPolytope());
		return result;
	}

	private List<Polytope> getAllPolytopes(List<Approximation<Factor>> subsApproximations) {
		List<Polytope> polytopesToMultiply = mapIntoList(subsApproximations, a -> (Polytope) a);
		addFactorAtRootPolytope(polytopesToMultiply);
		return polytopesToMultiply;
	}

	private void addFactorAtRootPolytope(List<Polytope> polytopesToMultiply) {
		var singletonDefaultFunctionConvexHullAtRoot = getFactorAtRootPolytope();
		polytopesToMultiply.add(singletonDefaultFunctionConvexHullAtRoot);
	}

	private FunctionConvexHull getFactorAtRootPolytope() {
		var factorAtRoot = Factor.multiply(getBase().getFactorsAtRoot());
		return new DefaultFunctionConvexHull(list(), factorAtRoot);
	}

	@Override
	public void refreshFromWithout() {
		Polytope polytope = (Polytope) getCurrentApproximation();
		
		// We "unsum" the newly free variables by removing them from indices and re-creating their simplices
		// This is based on the fact that indices always result from summing out a simplex variable
		// Also note that summed-out non-simplex variables never become free because they never become externally free.
		// To see this, consider that at the time of their summing out, they were not simplex variables (or they would have become indices).
		// They were also not the root variable, or they would be free and therefore not summed out.
		// If they were neither simplex variables not the root variable, all their factor neighbors were already included in this branch.
		// Therefore their factor neighbors were not in any other branch.
		// Since only their neighbor factors have any information about them and were not in external branches, the variable itself
		// might not appear in an external branch, so it is never made free by expanding them.

		Polytope polytopeAfterReversingSummingOutOfNewlyFreeVariables = polytope.unSumOutSimplexVariables(getBase()::isFreeVariable);

		setCurrentApproximation(polytopeAfterReversingSummingOutOfNewlyFreeVariables);
	}

	@Override
	public void setCurrentApproximation(Approximation<Factor> newCurrentApproximation) {
		
		if (debug) {
			check_DEBUG(newCurrentApproximation);
		}
		
		super.setCurrentApproximation(newCurrentApproximation);
	}

	
	@Override
	public String toString() {
		return "Anytime " + getBase();
	}
	
	//////////////////////////////// DEBUGGING METHODS

	private Variable violatingIndex_DEBUG(Approximation<Factor> approximation) {
		return getFirst(getIndices_DEBUG((Polytope)approximation), getBase()::isFreeVariable);
	}
	
	private Collection<? extends Variable> getIndices_DEBUG(Polytope polytope) {
		return union(mapIntoList(polytope.getAtomicPolytopes(), a -> a instanceof Simplex? list() : ((FunctionConvexHull)a).getIndices()));
	}

	private void check_DEBUG(Approximation<Factor> newCurrentApproximation) {
		
		Polytope actual = (Polytope) newCurrentApproximation;
		
		if (getBase().hasMadeSubsYet()) {
			checkBranchWithMadeSubs_DEBUG(actual);
		}
		else {
			checkCaseWithoutMadeSubs_DEBUG(actual);
		}
	}

	private void checkCaseWithoutMadeSubs_DEBUG(Polytope actual) {
		var rootIsVariable = getBase().getRoot() instanceof Variable;
		var simplexVariable = (Variable) (rootIsVariable? getBase().getRoot() : getBase().getParent());
		Simplex expected = new Simplex(simplexVariable);

		var equalityCheck = expected.checkEquality(actual);
		
		if (! equalityCheck.areEqual()) {
			println();
			println("Discrepancy in expected simplex and actual computation");
			println("Expected: " + expected);
			println("Actual  : " + actual);
			println("Equality check: " + equalityCheck);
			System.exit(-1);
		}
	}

	private void checkBranchWithMadeSubs_DEBUG(Polytope actual) {
		
		Set<Factor> factors = setFrom(getBase().getIncludedFactors());

		var includedVariables = setFrom(getBase().getIncludedVariables());
		var includedFreeVariables = collectToList(includedVariables, getBase()::isFreeVariable);
		var summedOutVariables = subtract(includedVariables, includedFreeVariables);
		var simplexVariables = getSimplexVariables_DEBUG();
		var summedOutSimplexVariables = subtract(simplexVariables, includedFreeVariables);
		var nonSummedOutSimplexVariables = subtract(simplexVariables, summedOutSimplexVariables);
		var summedOutNonSimplexVariables = subtract(summedOutVariables, simplexVariables);

		var allIncludedFactorsProduct = Factor.multiply(factors);
		var summedOutFactor = allIncludedFactorsProduct.sumOut(summedOutNonSimplexVariables);
		
		var simplices = mapIntoList(nonSummedOutSimplexVariables, v -> new Simplex(v));
		var convexHull = new DefaultFunctionConvexHull(summedOutSimplexVariables, summedOutFactor);
		@SuppressWarnings("unchecked")
		List<AtomicPolytope> multiplicands = listFrom(new NestedIterator(simplices, convexHull));
		var expected = ProductPolytope.makePolytopeEquivalentToProductOfAtomicPolytopes(multiplicands);

		@SuppressWarnings("unchecked")
		var actualConvexHulls = (Collection<? extends FunctionConvexHull>) collectToList(actual.getAtomicPolytopes(), a -> a instanceof FunctionConvexHull);
		var actualSimplices = collectToList(actual.getAtomicPolytopes(), a -> a instanceof Simplex);
		var actualSingleConvexHull = multiplyIntoSingleFunctionConvexHull(actualConvexHulls);
		@SuppressWarnings("unchecked")
		List<AtomicPolytope> actualMultiplicands = listFrom(new NestedIterator(actualSimplices, actualSingleConvexHull));
		var atomicActual = ProductPolytope.makePolytopeEquivalentToProductOfAtomicPolytopes(actualMultiplicands);
		
		var equalityCheck = expected.checkEquality(atomicActual);
		
		if (! equalityCheck.areEqual()) {
			println();
			println("Discrepancy in expected polytope and actual computation");
			println("root: " + getBase().getRoot());
			println();
			println("Included factors:\n" + join("\n", factors));
			println();
			println("All variables                                           : " + join(", ", sortByString(includedVariables)));
			println("Included free variables                                 : " + join(", ", sortByString(includedFreeVariables)));
			println("Summed out variables                                    : " + join(", ", sortByString(summedOutVariables)));
			println("Simplex variables                                       : " + join(", ", sortByString(simplexVariables)));
			println("Non-summed out simplex variables (to make simplices)    : " + join(", ", sortByString(nonSummedOutSimplexVariables)));
			println("Summed out simplex variables (to make into indices)     : " + join(", ", sortByString(summedOutSimplexVariables)));
			println("Summed out non-simplex variables (to remove from factor): " + join(", ", sortByString(summedOutNonSimplexVariables)));
			println();
			println("Expected       : " + expected);
			println();
			println("Atomic actual  : " + atomicActual);
			println();
			println("Equality check: " + equalityCheck);
			System.exit(-1);
		}
	}

	private Collection<? extends Variable> getSimplexVariables_DEBUG() {
		List<Variable> simplexVariables = list();
		if (getBase().hasMadeSubsYet()) {
			getSubs().stream().forEach(s -> simplexVariables.addAll(((OldAnytimeExactBP<SubRootType, RootType>) s).getSimplexVariables_DEBUG()));
		}
		else if (getBase().getRoot() instanceof Variable) {
			simplexVariables.add((Variable) getBase().getRoot());
		}
		else {
			simplexVariables.add((Variable) getBase().getParentNode().getRoot());
		}
		return simplexVariables;
	}

}
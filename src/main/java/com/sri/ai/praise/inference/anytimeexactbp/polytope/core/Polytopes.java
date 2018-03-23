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
package com.sri.ai.praise.inference.anytimeexactbp.polytope.core;

import static com.sri.ai.praise.inference.representation.core.IdentityFactor.IDENTITY_FACTOR;
import static com.sri.ai.util.Util.collect;
import static com.sri.ai.util.Util.getFirst;
import static com.sri.ai.util.Util.intersect;
import static com.sri.ai.util.Util.list;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.sri.ai.praise.inference.anytimeexactbp.polytope.api.AtomicPolytope;
import com.sri.ai.praise.inference.anytimeexactbp.polytope.api.Polytope;
import com.sri.ai.praise.inference.representation.api.Factor;
import com.sri.ai.praise.inference.representation.api.Variable;

/**
 * <pre>
 * This class offers methods to solve the problem of summing out a set of variables from a polytope:
 * 
 * sum_V polytope
 * 
 * A polytope may be of three types: atomic polytopes simplex and intensional convex hull, and products of polytopes.
 * 
 * We break product of polytopes into their atomic component and obtain the form:
 * 
 * sum_V CH_1...CH_m S_1 ... S_n
 * 
 * where
 * CH_i = {(on U_i) phi_i } is an intensional convex hull, and
 * S_j is a simplex on variable W_j.
 * 
 * We can easily factor out polytopes whose free variables do not intersect with V,
 * and assume the form above in which free variables always intersect with V.
 * Then we have:
 * 
 * sum_V { phi_1 }_U_1...{ phi_m }_U_m   S_1 ... S_n
 * 
 * \propto (this is equality if V = {W_1,...,W_n})
 * 
 * Union_{W_1,...,W_n} sum_{V \ {W_1,...,W_n}} {(on U_1) phi_1 }...{(on U_m) phi_m } 
 * 
 * \propto (this is equality if V = Union_i U_i)
 * 
 * Union_{W_1,...,W_n, U_1...U_m} { sum_{V \ {W_1,...,W_n} \ Union_I U_i} phi_1...phi_m }
 *  
 * =
 * 
 * {(on W_1,...,W_n, U_1...U_m) sum_{V \ {W_1,...,W_n} \ Union_I U_i} phi_1...phi_m } 
 *  
 * =
 * 
 * {(on W_1,...,W_n, U_1...U_m) phi' } 
 * 
 * which is an intensional convex hull representing the result.
 * 
 * The result can be further simplified by eliminating indices that do no appear in phi',
 * and by considering {(on ) 1} a multiplication identity polytope that can be eliminated from products.
 * 
 * Examples:
 *
 * Example 1:
 * 
 * sum_{I,J,K} {(on J) if I = K and I = M then 1 else 0} S_K S_M
 * \propto
 * S_M * sum_{I,J,K} {(on J) if I = K and I = M then 1 else 0} S_K
 * \propto
 * S_M * Union_K sum_{I,J} {(on J) if I = K and I = M then 1 else 0}
 * \propto
 * S_M * Union_{J,K} { sum_I if I = K and I = M then 1 else 0 }
 * \propto
 * S_M * Union_{J,K} { phi(K,M) }  for some phi
 * =
 * S_M * {(on J,K) phi(K,M) }
 * \propto
 * S_M * {(on K) phi(K,M) }
 * 
 * 
 * Example 2: m = 0.
 * 
 * sum_{I,J,K} S_K S_M
 * \propto
 * S_M * sum_{I,J,K} S_K
 * \propto
 * S_M * Union_K { sum_{I,J} 1 }
 * \propto
 * S_M * Union_K { 1 }
 * \propto
 * S_M * {(on K) 1 }
 * \propto
 * S_M
 * </pre>
 *
 * 
 * Example 3:
 * 
 * sum_{I,J,K} phi(I,K) S_K S_M
 * \propto
 * sum_{I,J,K} {(on ) phi(I,K)} S_K S_M
 * \propto
 * S_M * sum_{I,J,K} {(on ) phi(I,K)} S_K
 * \propto
 * S_M * Union_K { sum_{I,J} phi(I,K) }
 * \propto
 * S_M * Union_K { phi'(K) }
 * \propto
 * S_M * {(on K) phi'(K) }
 * </pre>

 * @author braz
 *
 */
public class Polytopes {
	
	public static Polytope sumOut(List<? extends Variable> variablesSummedOut, Polytope polytope) {
		return sumOut(variablesSummedOut, list(polytope));
	}

	public static Polytope sumOut(
			List<? extends Variable> variablesToBeSummedOut, 
			Collection<? extends Polytope> polytopes) {

		List<Polytope> independentOfVariablesToBeSummedOut = list();
		List<Polytope> dependentOfVariablesToBeSummedOut = list();

		collect(
				getNonIdentityAtomicPolytopes(polytopes), 
				independentOfVariablesToBeSummedOut, 
				isIndependentOf(variablesToBeSummedOut), 
				dependentOfVariablesToBeSummedOut);

		Polytope projectedPolytope = makeProjectedPolytope(variablesToBeSummedOut, dependentOfVariablesToBeSummedOut);

		Polytope result = makeProductOfPolytopes(independentOfVariablesToBeSummedOut, projectedPolytope);

		return result;
	}
	
	private static Polytope makeProjectedPolytope(List<? extends Variable> variablesToBeSummedOut, List<Polytope> dependentOfVariablesToBeSummedOut) {

		List<Variable> indices = collectIndicesFromSimplexAndIntensionalPolytopes(dependentOfVariablesToBeSummedOut);
		
		List<Factor> factors = collectFactorsFromIntensionalPolytopes(dependentOfVariablesToBeSummedOut);
		
		Factor productOfFactors = Factor.multiply(factors);
		
		Factor projectedFactor = productOfFactors.sumOut(variablesToBeSummedOut);
		
		Polytope projectedPolytope = new IntensionalConvexHullOfFactors(indices, projectedFactor);
		
		return projectedPolytope;
	}

	private static List<Variable> collectIndicesFromSimplexAndIntensionalPolytopes(List<Polytope> dependentOfVariablesToBeSummedOut) {
		List<Variable> indices = list();
		for (Polytope polytope : dependentOfVariablesToBeSummedOut) {
			if (polytope instanceof Simplex) {
				collectIndicesFromSimplex(indices, polytope);
			}
			else if (polytope instanceof AbstractAtomicPolytope) {
				collectIndicesFromIntensionalPolytope(indices, polytope);
			}
		}
		return indices;
	}

	private static void collectIndicesFromSimplex(List<Variable> indices, Polytope polytope) {
		indices.addAll(polytope.getFreeVariables());
		// note that the free variable of a simplex becomes an index of the projected polytope!
	}

	private static void collectIndicesFromIntensionalPolytope(List<Variable> indices, Polytope polytope) {
		IntensionalConvexHullOfFactors intensionalPolytope = (IntensionalConvexHullOfFactors) polytope;
		indices.addAll(intensionalPolytope.getIndices());
	}

	private static List<Factor> collectFactorsFromIntensionalPolytopes(List<Polytope> dependentOfVariablesToBeSummedOut) {
		List<Factor> factors = list();
		for (Polytope polytope : dependentOfVariablesToBeSummedOut) {
			collectFactorIfIntensionalConvexHull(polytope, factors);
		}
		return factors;
	}

	private static void collectFactorIfIntensionalConvexHull(Polytope polytope, List<Factor> factors) {
		if (polytope instanceof IntensionalConvexHullOfFactors) {
			IntensionalConvexHullOfFactors intensionalConvexHull = (IntensionalConvexHullOfFactors) polytope;
			factors.add(intensionalConvexHull.getFactor());
		}
	}

	private static Polytope makeProductOfPolytopes(List<Polytope> independentOfVariablesToBeSummedOut, Polytope projectedPolytope) {
		List<Polytope> resultingPolytopes = independentOfVariablesToBeSummedOut;
		resultingPolytopes.add(projectedPolytope);
		Polytope result = Polytope.multiply(resultingPolytopes);
		return result;
	}

	private static Predicate<Polytope> isIndependentOf(List<? extends Variable> variables) {
		return p -> ! intersect(p.getFreeVariables(), variables);
	}

	private static List<? extends AtomicPolytope> getNonIdentityAtomicPolytopes(Collection<? extends Polytope> polytopes) {
		List<AtomicPolytope> result = list();
		collectNonIdentityAtomicPolytopes(polytopes, result);
		return result;
	}

	private static void collectNonIdentityAtomicPolytopes(Collection<? extends Polytope> polytopes, List<AtomicPolytope> result) {
		for (Polytope polytope : polytopes) {
			if ( ! polytope.isIdentity()) {
				if (polytope instanceof ProductPolytope) {
					collectNonIdentityAtomicPolytopesInProduct(polytope, result);
				}
				else {
					result.add((AtomicPolytope) polytope);
				}
			}
		}
	}

	private static void collectNonIdentityAtomicPolytopesInProduct(Polytope polytope, List<AtomicPolytope> result) {
		Collection<? extends Polytope> immediateSubPolytopes = ((ProductPolytope)polytope).getPolytopes();
		collectNonIdentityAtomicPolytopes(immediateSubPolytopes, result);
	}

	public static IntensionalConvexHullOfFactors identityPolytope() {
		return new IntensionalConvexHullOfFactors(list(), IDENTITY_FACTOR);
	}

	public static Polytope multiplyListOfAlreadyMultipledNonIdentityAtomicPolytopesWithANewOne(
			Collection<? extends AtomicPolytope> nonIdentityAtomicPolytopes, 
			AtomicPolytope nonIdentityAtomicAnother) {
		
		List<AtomicPolytope> resultNonIdentityAtomicPolytopes = list();
		boolean anotherAlreadyIncorporated = false;
		for (AtomicPolytope nonIdentityAtomicPolytope : nonIdentityAtomicPolytopes) {
			anotherAlreadyIncorporated = takeNextPolytopeInList(nonIdentityAtomicPolytope, nonIdentityAtomicAnother, resultNonIdentityAtomicPolytopes, anotherAlreadyIncorporated);
		}
		
		includeAnotherByItselfIfMultiplicationsFailed(nonIdentityAtomicAnother, anotherAlreadyIncorporated, resultNonIdentityAtomicPolytopes);
		
		Polytope result = makePolytopeFromListOfNonIdentityAtomicPolytopes(resultNonIdentityAtomicPolytopes);
		
		return result;
	}

	private static void includeAnotherByItselfIfMultiplicationsFailed(AtomicPolytope nonIdentityAtomicAnother, boolean anotherAlreadyIncorporated, List<AtomicPolytope> resultNonIdentityAtomicPolytopes) {
		if (! anotherAlreadyIncorporated) {
			resultNonIdentityAtomicPolytopes.add(nonIdentityAtomicAnother);
		}
	}

	private static boolean takeNextPolytopeInList(AtomicPolytope nonIdentityAtomicPolytope, AtomicPolytope nonIdentityAtomicAnother, List<AtomicPolytope> resultNonIdentityAtomicPolytopes, boolean anotherAlreadyIncorporated) {
		if (anotherAlreadyIncorporated) {
			resultNonIdentityAtomicPolytopes.add(nonIdentityAtomicPolytope);
		}
		else {
			anotherAlreadyIncorporated = 
					tryNextPolytopeInList(
							nonIdentityAtomicPolytope, 
							nonIdentityAtomicAnother, 
							resultNonIdentityAtomicPolytopes, 
							anotherAlreadyIncorporated);
		}
		return anotherAlreadyIncorporated;
	}

	private static boolean tryNextPolytopeInList(AtomicPolytope nonIdentityAtomicPolytope, AtomicPolytope nonIdentityAtomicAnother, List<AtomicPolytope> resultNonIdentityAtomicPolytopes, boolean anotherAlreadyIncorporated) {
		AtomicPolytope nonIdentityAtomicProductWithAnother = 
				nonIdentityAtomicPolytope.nonIdentityAtomicProductOrNull(nonIdentityAtomicAnother);
		if (nonIdentityAtomicProductWithAnother == null) {
			resultNonIdentityAtomicPolytopes.add(nonIdentityAtomicPolytope);
		}
		else {
			anotherAlreadyIncorporated = addSuccessfulProductAndIndicateItsBeenIncorporated(resultNonIdentityAtomicPolytopes, nonIdentityAtomicProductWithAnother);
		}
		return anotherAlreadyIncorporated;
	}

	private static boolean addSuccessfulProductAndIndicateItsBeenIncorporated(List<AtomicPolytope> resultNonIdentityAtomicPolytopes, AtomicPolytope nonIdentityAtomicProductWithAnother) {
		boolean anotherAlreadyIncorporated;
		resultNonIdentityAtomicPolytopes.add(nonIdentityAtomicProductWithAnother);
		anotherAlreadyIncorporated = true;
		return anotherAlreadyIncorporated;
	}

	public static Polytope makePolytopeFromListOfNonIdentityAtomicPolytopes(List<AtomicPolytope> resultNonIdentityAtomicPolytopes) {
		Polytope result;
		if (resultNonIdentityAtomicPolytopes.isEmpty()) {
			result = identityPolytope();
		}
		else if (resultNonIdentityAtomicPolytopes.size() == 1) {
			result = getFirst(resultNonIdentityAtomicPolytopes);
		}
		else {
			result = new ProductPolytope(resultNonIdentityAtomicPolytopes);
		}
		return result;
	}
	
}
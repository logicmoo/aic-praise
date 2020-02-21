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

import static com.sri.ai.util.Util.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.sri.ai.praise.core.representation.interfacebased.factor.api.Factor;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.Variable;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.table.api.TableFactor;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.table.core.base.TableVariable;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.table.core.bydatastructure.arraylist.ArrayTableFactor;

/**
 * An implementation of {@link AbstractFunctionConvexHull} that 
 * simplifies the convex hull by keeping only the vertices
 * { v_i : v_i,j
 * 
 * @author braz
 *
 */
final public class MinBasedFunctionConvexHull extends AbstractFunctionConvexHull {
	
	public MinBasedFunctionConvexHull(Collection<? extends Variable> indices, Factor factor) {
		super(indices, factor);
	}
	
	@Override
	public MinBasedFunctionConvexHull newInstance(Collection<? extends Variable> indices, Factor factor) {
		return new MinBasedFunctionConvexHull(indices, factor);
	}
	
	@Override
	public FunctionConvexHull simplify() {
		Factor normalized = getFactor().normalize(getFreeVariables());
		Factor minimaOfFreeVariables = normalized.min(getIndices());
		List<Variable> others = new LinkedList<>(getFreeVariables());
		var marginalSimplices = list();
		for (Variable freeVariable: getFreeVariables()) {
			TableVariable tableFreeVariable = (TableVariable) freeVariable;
			others.remove(freeVariable);
			Factor minimaOfFreeVariable = minimaOfFreeVariables.min(others);
			double sum = ((TableFactor) minimaOfFreeVariable.sumOut(list(freeVariable))).getEntries().get(0);
			ArrayList<Double> minimaOfFreeVariableList = ((TableFactor) minimaOfFreeVariable).getEntries();
			double[] newVerticesForFreeVariable = new double[tableFreeVariable.getCardinality()^2];
			int i = 0;
			for (int valueIndex = 0; valueIndex != minimaOfFreeVariableList.size(); valueIndex++) {
				for (int valueIndex2 = 0; valueIndex2 != minimaOfFreeVariableList.size(); valueIndex2++) {
					if (valueIndex == valueIndex2) {
						double sumOfMinimumOfOtherValues = sum - minimaOfFreeVariableList.get(valueIndex);
						double maximumGivenMinimaOfOtherValues = 1.0 - sumOfMinimumOfOtherValues;
						newVerticesForFreeVariable[i++] = maximumGivenMinimaOfOtherValues;
					}
					else {
						newVerticesForFreeVariable[i++] = minimaOfFreeVariableList.get(valueIndex);
					}
				}
				Factor factorForMarginalSimplex = new ArrayTableFactor(list(tableFreeVariable, tableFreeVariable), newVerticesForFreeVariable);
				FunctionConvexHull marginalSimplex = new DefaultFunctionConvexHull(list(tableFreeVariable), factorForMarginalSimplex);
				marginalSimplices.add(marginalSimplex);
			}
		}
		return this;
	}

}
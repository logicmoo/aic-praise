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
package com.sri.ai.praise.core.representation.interfacebased.factor.core;

import static com.sri.ai.util.Util.mapIntoList;
import static com.sri.ai.util.base.IdentityWrapper.identityWrapper;

import java.util.List;

import com.sri.ai.praise.core.representation.interfacebased.factor.api.Factor;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.FactorNetwork;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.Variable;
import com.sri.ai.util.base.IdentityWrapper;
import com.sri.ai.util.collect.DefaultManyToManyRelation;

/**
 * An abstract {@link FactorNetwork} indexing factors and indices with a {@link DefaultManyToManyRelation}.
 * @author braz
 *
 */
public abstract class AbstractFactorNetwork 
extends DefaultManyToManyRelation<IdentityWrapper<Factor>, Variable> implements FactorNetwork {

	public AbstractFactorNetwork(List<? extends Factor> factors) {
		super();
		indexFactorsAndVariables(factors);
	}

	private void indexFactorsAndVariables(List<? extends Factor> factors) {
		for (Factor factor : factors) {
			indexFactorAndItsVariables(factor);
		}
	}

	private void indexFactorAndItsVariables(Factor factor) {
		for (Variable variable : factor.getVariables()) {
			indexFactorAndVariable(factor, variable);
		}
	}

	private void indexFactorAndVariable(Factor factor, Variable variable) {
		this.add(identityWrapper(factor), variable);
	}

	@Override
	public List<? extends Variable> getNeighbors(Factor factor) {
		return mapIntoList(getBsOfA(identityWrapper(factor)), v -> v);
	}

	@Override
	public List<? extends Factor> getNeighbors(Variable variable) {
		List<Factor> result = mapIntoList(getAsOfB(variable), IdentityWrapper::getObject);
		return result;
	}

}
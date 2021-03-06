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
package com.sri.ai.praise.core.representation.translation.ciaranframework.core.uai;

import java.io.PrintWriter;
import java.util.List;

import com.google.common.annotations.Beta;
import com.sri.ai.expresso.api.Expression;
import com.sri.ai.praise.core.representation.classbased.expressionbased.api.ExpressionBasedModel;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.ModelLanguage;
import com.sri.ai.praise.core.representation.translation.ciaranframework.core.common.AbstractHOGMv1_to_Target_Translator;

/**
 * Translator: HOGMv1->UAI
 * 
 * @author oreilly
 *
 */
@Beta
public class HOGMv1_to_UAI_Translator extends AbstractHOGMv1_to_Target_Translator {
	private static final String[] _outputFileExtensions = AbstractUAI_to_Target_Translator.INPUT_FILE_EXTENSIONS;
	//
	// START-Translator	
	@Override 
	public ModelLanguage getTarget() {
		return ModelLanguage.UAI;
	}
	
	@Override
	public int getNumberOfOutputs() {
		return _outputFileExtensions.length;
	}
	
	@Override
	public String[] getOutputFileExtensions() {
		return _outputFileExtensions;
	}
	// END-Translator
	//
	
	@Override
	protected void translate(String identifier, ExpressionBasedModel hogmv1FactorsAndTypes, List<Expression> evidence, PrintWriter[] translatedOutputs) throws Exception {			
		//
		// Ground out the HOGM FactorNetwork and translate it to the UAI model format
		HOGModelGrounding.ground(hogmv1FactorsAndTypes, evidence, new UAIHOGModelGroundingListener(translatedOutputs[0], translatedOutputs[1]));
	}
}

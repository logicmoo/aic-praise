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
package com.sri.ai.praise.core.representation.translation.ciaranframework.api;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;

import com.google.common.annotations.Beta;
import com.google.common.base.Charsets;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.ModelLanguage;

/**
 * Interface to be implemented by all Language Translators.
 * 
 * @author oreilly
 *
 */
@Beta
public interface Translator {
	
	/**
	 * 
	 * @return the source model language for the translator.
	 */
	ModelLanguage getSource();
	
	/**
	 * 
	 * @return the source model's charset.
	 */
	default Charset getSourceCharset() {
		return Charsets.UTF_8;
	}
	
	/**
	 * 
	 * @return the target model language for the translator.
	 */
	ModelLanguage getTarget();
	
	/**
	 * 
	 * @return the target model's charset.
	 */
	default Charset getTargetCharset() {
		return Charsets.UTF_8;
	}
	
	/**
	 * Get the number of inputs required by the translator (usually just 1).
	 * 
	 * @return the number of inputs required to perform the translation
	 *         (e.g. a UAI input to target translation may comprise of translating the model file '.uai' and its evidence file '.uai.evid').
	 */
	default int getNumberOfInputs() {
		return 1;
	}
	
	/**
	 * Used if the translator's inputs are to be read from a file.
	 * 
	 * NOTE: the first input file extension is to always be the main model file, others are considered supporting.
	 * 
	 * @return the unique file extensions to use for each input (# file extensions must = number of inputs).
	 */
	default String[] getInputFileExtensions() {
		return new String[] { getSource().getDefaultFileExtension() };
	}
	
	/**
	 * Get the number of outputs generated by the translator.
	 * 
	 * @return the number of outputs generated by the translation 
	 *         (e.g. UAI generates two outputs, one for the model and the other for the default evidence).
	 */
	default int getNumberOfOutputs() {
		return 1;
	}
	
	/**
	 * Used if the translator's outputs are to be written to a file. 
	 * 
	 * NOTE: the first output file extension is to always be the main model file, others are considered supporting.
	 * 
	 * @return the unique file extensions to use for each output (# file extensions must = number of outputs).
	 */
	default String[] getOutputFileExtensions() {
		return new String[] { getTarget().getDefaultFileExtension() };
	}
	
	/**
	 * Perform the translation from an input model to an output model.
	 * 
	 * @param inputIdentifier
	 *        an identifying name for the input.
	 * @param inputModelReaders
	 *        the readers for the input model definitions.
	 * @param translatedOutputs
	 *        the outputs to be used for the translated model.
	 * @param options
	 *        options to be passed to the translator.       
	 * @throws Exception
	 *        exceptions handling to be performed outside of the translator.
	 */
	void translate(String inputIdentifier, Reader[] inputModelReaders, PrintWriter[] translatedOutputs, TranslatorOptions options) throws Exception;
	
	/**
	 * Utility routine that based on the file name convention for models will return an extension neutral model name
	 * for a source model file.
	 * 
	 * @param sourceModelFile
	 *        The input model file whose extension neutral name is to be returned.
	 * @return the extension neutral name of the input model file.
	 */
	default String getInputModelFileNameWithNoExtension(File sourceModelFile) {
		String extension = getInputFileExtensions()[0];
		String result    = sourceModelFile.getName();
		
		if (!result.endsWith(extension)) {
			throw new IllegalArgumentException("Source FactorNetwork File: "+sourceModelFile.getName()+" does not have the expected extension: "+extension);
		}
		
		result = result.substring(0, result.length()-extension.length());
		
		return result;		
	}
}

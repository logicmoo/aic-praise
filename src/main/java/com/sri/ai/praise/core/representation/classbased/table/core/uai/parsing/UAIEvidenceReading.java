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
package com.sri.ai.praise.core.representation.classbased.table.core.uai.parsing;

import static com.sri.ai.praise.core.representation.classbased.table.core.uai.UAIUtil.readLine;
import static com.sri.ai.praise.core.representation.classbased.table.core.uai.UAIUtil.split;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.google.common.annotations.Beta;
import com.sri.ai.praise.core.representation.classbased.table.core.uai.UAIModel;

/**
 * Utility file for reading in Uncertainty in Artificial Intelligence (UAI) 
 * <a href="http://www.hlt.utdallas.edu/~vgogate/uai14-competition/evidformat.html">Inference Competition Evidence Format files</a>.
 * 
 * @author oreilly
 */
@Beta
public class UAIEvidenceReading {

	public static void read(File modelFile, UAIModel modelFromFile) throws IOException {
		// Evidence is specified in a separate file. This file has the same name as the original network 
		// file but with an added .evid suffix. For instance, problem.uai will have evidence in problem.uai.evid. 
		File evidenceFile = new File(modelFile.getAbsolutePath()+".evid");
		if (!evidenceFile.exists()) {
			System.err.println("FactorNetwork "+modelFile.getName()+" does not have a default evidence file associated with it.");
		}
		else {
			read(new FileReader(evidenceFile), modelFromFile);
		}
	}
	
	public static void read(Reader evidenceReader, UAIModel uaiModel) throws IOException {
		// The evidence file consists of a single line. The line will begin with the number of observed variables in the sample, 
		// followed by pairs of variable and its observed value. The indexes correspond to the ones implied by the original problem file. 
		try (BufferedReader br = new BufferedReader(evidenceReader)) {
			String evidenceInfo = readLine(br);
			String[] evidence = split(evidenceInfo);
			if (Integer.parseInt(evidence[0]) != ((evidence.length)/2)) {
				System.err.println("Evidence specifies incorrect number of var/value pairs: "+evidenceInfo);
			}
			else {
				for (int i = 1; i < evidence.length; i += 2) {
					uaiModel.addEvidence(Integer.parseInt(evidence[i]), Integer.parseInt(evidence[i+1]));
				}
			}
		}
	}
}
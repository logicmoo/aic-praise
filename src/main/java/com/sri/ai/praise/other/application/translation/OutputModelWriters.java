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
package com.sri.ai.praise.other.application.translation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import com.sri.ai.praise.core.representation.translation.ciaranframework.api.Translator;

public class OutputModelWriters implements AutoCloseable {
	
	public PrintWriter[] writers;
	
	public OutputModelWriters(Translator translator, File sourceModelFile, String sourceModelFileExtension) throws Exception {
		writers = new PrintWriter[translator.getNumberOfOutputs()];

		String modelName = translator.getInputModelFileNameWithNoExtension(sourceModelFile);
		File outputDir = new File(sourceModelFile.getParentFile().getParent(), translator.getTarget().getCode());
		for (int i = 0; i < writers.length; i++) {
			Path file = new File(outputDir, modelName + translator.getOutputFileExtensions()[i]).toPath();
			Charset targetCharset = translator.getTargetCharset();
			BufferedWriter bufferedWriter = Files.newBufferedWriter(file, targetCharset);
			writers[i] = new PrintWriter(bufferedWriter);
		}
	}
	
	@Override
	public void close() throws IOException {
		for (PrintWriter writer : writers) {
			writer.flush();
			writer.close();
		}
	}
}
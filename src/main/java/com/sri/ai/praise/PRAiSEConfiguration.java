/*
 * Copyright (c) 2013, SRI International
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
 * Neither the name of the aic-expresso nor the names of its
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
package com.sri.ai.praise;

import com.google.common.annotations.Beta;
import com.sri.ai.util.Configuration;

/**
 * Configuration information for the classes in the PRAiSE package.
 * 
 * @author braz
 *
 */
@Beta
public class PRAiSEConfiguration extends Configuration {
	public static final String  KEY_USE_UNIFORM_SAMPLING_BACKUP = "praise.use.uniform.sampling.backup";
	public static final Boolean DEFAULT_VALUE_USE_UNIFORM_SAMPLING_BACKUP = false;
			
	/**
	 * Indicates whether sampling should use uniform sampling for a variable
	 * when the model does not specify a more specific way of sampling values for it;
	 * if false, an error will be thrown if a value is needed for such a variable.
	 */
	public static boolean useUniformSamplingBackup() {
		boolean result = getBoolean(KEY_USE_UNIFORM_SAMPLING_BACKUP, DEFAULT_VALUE_USE_UNIFORM_SAMPLING_BACKUP);
		return result;
	}

	/**
	 * Sets the value to be returned by {@link #useUniformSamplingBackup()}
	 * and returns the old value.
	 */
	public static boolean setUseUniformSamplingBackup(boolean newValue) {
		boolean result = useUniformSamplingBackup();
		Configuration.setProperty(KEY_USE_UNIFORM_SAMPLING_BACKUP, Boolean.toString(newValue));
		return result;
	}
}

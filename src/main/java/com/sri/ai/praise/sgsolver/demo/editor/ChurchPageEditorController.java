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
package com.sri.ai.praise.sgsolver.demo.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fxmisc.undo.UndoManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import com.google.common.annotations.Beta;
import com.sri.ai.expresso.api.Expression;
import com.sri.ai.praise.model.Model;
import com.sri.ai.praise.model.imports.church.TranslateChurchToModel;
import com.sri.ai.praise.sgsolver.demo.FXUtil;
import com.sri.ai.praise.sgsolver.demo.query.QueryController;
import com.sri.ai.praise.sgsolver.demo.service.QueryError;
import com.sri.ai.util.base.Pair;
import com.sri.ai.util.base.Triple;

@Beta
public class ChurchPageEditorController implements ModelPageEditor {
	@FXML private Pane       rootPane;
	@FXML private AnchorPane churchEditorPane;
	@FXML private AnchorPane hogmEditorPane;
	@FXML private AnchorPane queryOutputPane;
	//
	private ChurchCodeArea  churchCodeArea = new ChurchCodeArea();
	private HOGMCodeArea    hogmCodeArea   = new HOGMCodeArea();
	private QueryController queryController;
	// 
	private TranslateChurchToModel translator = new TranslateChurchToModel();
	
	public static FXMLLoader newLoader( ) {
		FXMLLoader result = new FXMLLoader(ChurchPageEditorController.class.getResource("churchpageeditor.fxml"));
		return result;
	}
	
	//
	// START-ModelPageEditor
	@Override
	public Pane getRootPane() {
		return rootPane;
	}
	
	@Override
	public void setPage(String modelPage, List<String> defaultQueries) {
		churchCodeArea.setText(modelPage);
		queryController.addDefaultQueries(defaultQueries);	
		generateModel();
	}
	
	@Override
	public String getCurrentPageContents() {		
		return churchCodeArea.getText();
	}
	
	@Override
	public List<String> getCurrentQueries() {
		return queryController.getCurrentQueries();
	}
	
	@Override
	public Pair<List<QueryError>, String> validateAndGetModel() {
		List<QueryError> errors = generateModel();
		Pair<List<QueryError>, String> result = new Pair<>(errors, hogmCodeArea.getText());
		return result;
	}
	
	@Override
	public UndoManager getUndoManager() {
		return churchCodeArea.getUndoManager();
	}
	
	@Override
	public void undo() {
		churchCodeArea.undo();
	}
	
	@Override
	public void redo() {
		churchCodeArea.redo();
	}
	
	@Override
	public void highlight(int startIdx, int endIdx) {
		hogmCodeArea.highlight(startIdx, endIdx);
	}
	
	@Override
	public void gotoModelEditor() {
		churchCodeArea.setFocus();
	}
	
	@Override
	public void gotoQueryEditor() {
		queryController.gotoQueryEditor();
	}
	
	@Override 
	public void executeQuery() {
		queryController.executeQuery();
	}
	// END-ModelPageEditor
	//
	
	@FXML
	private void initialize() throws IOException {
		FXUtil.anchor(churchCodeArea);
		churchEditorPane.getChildren().add(churchCodeArea);
		
		hogmCodeArea.setEditable(false);
		FXUtil.anchor(hogmCodeArea);
		hogmEditorPane.getChildren().add(hogmCodeArea);
		
		FXMLLoader queryLoader = QueryController.newLoader();
		Pane queryPane  = queryLoader.load();
		queryController = queryLoader.getController();
		queryController.setModelPageEditor(this);
		FXUtil.anchor(queryPane);
		queryOutputPane.getChildren().add(queryPane);
	}
	
	private List<QueryError> generateModel() {
		List<QueryError> problems = new ArrayList<>();
		try {
			String churchProgram = churchCodeArea.getText();
			Triple<String, Model, List<Expression>> translation = translator.translate("Church Program", ""
				+ churchProgram
				);
			String hogmModel = translation.first;
			hogmCodeArea.setText(hogmModel);
		} catch (Throwable t) {
			String problem = "/* ERROR in Translation:\n"+ExceptionUtils.getStackTrace(t)+"\n*/";
			problems.add(new QueryError(t));
			hogmCodeArea.setText(problem);
		}
		return problems;
	}
}
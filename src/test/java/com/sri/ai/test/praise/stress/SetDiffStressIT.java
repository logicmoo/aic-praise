package com.sri.ai.test.praise.stress;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.sri.ai.expresso.api.Expression;
import com.sri.ai.expresso.helper.Expressions;
import com.sri.ai.grinder.api.RewritingProcess;
import com.sri.ai.praise.LPIUtil;
import com.sri.ai.praise.lbp.LBPRewriter;
import com.sri.ai.praise.model.Model;
import com.sri.ai.test.praise.AbstractLPITest;

/**
 * Lifted Belief Propagation Stress Tests.
 * 
 * @author oreilly
 *
 */
public class SetDiffStressIT extends AbstractLPITest {
	
	@Before
	public void ignoreTest() {
		Assume.assumeFalse("Stress Tests Ignored.", Boolean.getBoolean("ignore.stress.tests"));
	}

	@Test
	public void testStressTest1() {
		perform(new TestData[] {
			new SetDifferenceTestData(Expressions.TRUE.toString(), 
				new Model(Model.getModelDeclarationFromResource("Example4.model")),
				"{ ( on AnotherWord, X0, AnotherWord', Word, X0', AnotherWord'', Word' ) ( ([ canHave(X0', AnotherWord'') ]), ([ if possessive(Word') and dependency(AnotherWord'', Word') then if referenceOf(Word', X0') and canHave(X0', AnotherWord'') then 1 else 0 else 0.5 ]) ) | X != w7 and X != AnotherWord and AnotherWord != w7 and X0 != w7 and X0 != AnotherWord' and AnotherWord' != w7 and (AnotherWord' != AnotherWord or X0 != X) and (X0 = X and X = AnotherWord' or X0 != X) and AnotherWord' != constituency and X0 != Word and Word != AnotherWord' and Word != w7 and (X0 = X and (X = Word or X = AnotherWord') or X0 != X) and (X = Word or X0 != X or X != Word and X0 = X and X = AnotherWord') and X0' != Word and X0' != AnotherWord'' and Word != AnotherWord'' and (AnotherWord'' != AnotherWord' or X0' != X0) and (X = constituency or X = Word or Word = constituency or AnotherWord'' = constituency and X0' = X) and (X = Word or X != constituency and Word != constituency and (AnotherWord'' != constituency or X0' != X)) and (Word = constituency or X != constituency and X != Word and (AnotherWord'' != constituency or X0' != X) or X != Word and (X = constituency or AnotherWord'' = constituency and X0' = X)) and (X != constituency and (X0' != X and (X = Word or Word = constituency) or X0' = X and (AnotherWord'' != constituency and (X = Word or Word = constituency) or AnotherWord'' = constituency)) or X = constituency) and (X = Word or X != constituency and Word != constituency and (AnotherWord'' != constituency or X0' != X) or X != constituency and (AnotherWord'' != constituency or X0' != X) and (AnotherWord'' = constituency and X0' = X or Word != constituency)) and (X0' != X and (X = Word or X != constituency and Word != constituency) or X0' = X) and (X = constituency and X0' != constituency or (X != constituency or X0' = constituency) and (AnotherWord'' != constituency and (Word = constituency or X != Word or X = constituency) or AnotherWord'' = constituency and (Word = constituency or X != constituency and X != Word and X0' != X or X != Word and (X = constituency or X0' = X) or X != constituency and X0' != X and X != Word))) and X0' != Word' and Word' != AnotherWord'' and Word' != Word }",
				// - 
				"{ ( on Word ) ( ([ canHave(X, constituency) ]), ([ if possessive(Word) and dependency(constituency, Word) then if referenceOf(Word, X) and canHave(X, constituency) then 1 else 0 else 0.5 ]) ) | X != w7 and X != constituency and X != Word and Word != constituency and Word != w7 } "
				+ " union " +
				"{ ( on AnotherWord, Word ) ( ([ canHave(X, AnotherWord) ]), ([ if possessive(Word) and dependency(AnotherWord, Word) then if referenceOf(Word, X) and canHave(X, AnotherWord) then 1 else 0 else 0.5 ]) ) | X != w7 and X != AnotherWord and AnotherWord != w7 and AnotherWord != constituency and X != Word and Word != AnotherWord and Word != w7 }"
				+ " union " + 
				"{ ( on AnotherWord, X0, Word ) ( ([ canHave(X0, constituency) ]), ([ if possessive(Word) and dependency(constituency, Word) then if referenceOf(Word, X0) and canHave(X0, constituency) then 1 else 0 else 0.5 ]) ) | X != w7 and X != AnotherWord and AnotherWord != w7 and X0 != w7 and X0 != constituency and X0 != X and X0 != Word and Word != constituency and Word != w7 }"
				+ " union " +
				"{ ( on AnotherWord, X0, AnotherWord', Word ) ( ([ canHave(X0, AnotherWord') ]), ([ if possessive(Word) and dependency(AnotherWord', Word) then if referenceOf(Word, X0) and canHave(X0, AnotherWord') then 1 else 0 else 0.5 ]) ) | X != w7 and X != AnotherWord and AnotherWord != w7 and X0 != w7 and X0 != AnotherWord' and AnotherWord' != w7 and (AnotherWord' != AnotherWord or X0 != X) and (X0 = X and X = AnotherWord' or X0 != X) and AnotherWord' != constituency and X0 != Word and Word != AnotherWord' and Word != w7 and (X0 = X and (X = Word or X = AnotherWord') or X0 != X) }"
				+ " union " +
				"{ ( on Word, X0, Word' ) ( ([ canHave(X0, constituency) ]), ([ if possessive(Word') and dependency(constituency, Word') then if referenceOf(Word', X0) and canHave(X0, constituency) then 1 else 0 else 0.5 ]) ) | X != w7 and X != constituency and X != Word and Word != constituency and Word != w7 and X0 != Word and X0 != constituency and X0 != X and X0 != Word' and Word' != constituency and Word' != Word and (X0 = w7 or Word' = w7) }"
				+ " union " + 
				"{ ( on Word, X0, AnotherWord, Word' ) ( ([ canHave(X0, AnotherWord) ]), ([ if possessive(Word') and dependency(AnotherWord, Word') then if referenceOf(Word', X0) and canHave(X0, AnotherWord) then 1 else 0 else 0.5 ]) ) | X != w7 and X != constituency and X != Word and Word != constituency and Word != w7 and X0 != Word and X0 != AnotherWord and Word != AnotherWord and AnotherWord != constituency and X0 != Word' and Word' != AnotherWord and Word' != Word and (X0 = X and (X = Word' or Word' = w7 or X = AnotherWord or AnotherWord = w7) or X0 != X) and (X0 = w7 or AnotherWord = w7 or X != AnotherWord and X0 = X or Word' = w7 or X != Word' and X != AnotherWord and X0 = X) }"
				+ " union " + 
				"{ ( on AnotherWord, Word, X0, Word' ) ( ([ canHave(X0, constituency) ]), ([ if possessive(Word') and dependency(constituency, Word') then if referenceOf(Word', X0) and canHave(X0, constituency) then 1 else 0 else 0.5 ]) ) | X != w7 and X != AnotherWord and AnotherWord != w7 and AnotherWord != constituency and X != Word and Word != AnotherWord and Word != w7 and X0 != Word and X0 != constituency and Word != constituency and (X = constituency or X0 = X) and X0 != Word' and Word' != constituency and Word' != Word and (X != constituency and (X0 = X and (X = Word' or Word' = w7) or X0 != X) or X = constituency) and (Word' = w7 or Word' != w7 and (X0 = w7 or X0 = X)) and (X = constituency or X0 = X or X0 != w7 and Word' != w7) }" 
				+ " union " +
				"{ ( on AnotherWord, Word, X0, AnotherWord', Word' ) ( ([ canHave(X0, AnotherWord') ]), ([ if possessive(Word') and dependency(AnotherWord', Word') then if referenceOf(Word', X0) and canHave(X0, AnotherWord') then 1 else 0 else 0.5 ]) ) | X != w7 and X != AnotherWord and AnotherWord != w7 and AnotherWord != constituency and X != Word and Word != AnotherWord and Word != w7 and X0 != Word and X0 != AnotherWord' and Word != AnotherWord' and (AnotherWord' != AnotherWord or X0 != X) and (X = constituency or Word = constituency) and (X = constituency or X != constituency and Word = constituency) and AnotherWord' != constituency and X0 != Word' and Word' != AnotherWord' and Word' != Word and (X0 = X and (X = Word' or Word' = w7 or X = AnotherWord' or AnotherWord' = w7) or X0 != X) and (X0 = w7 or AnotherWord' = w7 or X != AnotherWord' and X0 = X or Word' = w7 or X != Word' and X != AnotherWord' and X0 = X) and (X = constituency or X != Word' and Word' != w7 and X != AnotherWord' and AnotherWord' != w7 and X0 = X) }", 
				false,
				"{ }")
		});
	}
	
	@Test 
	public void testStressTest2() {
		perform(new TestData[] {
			new SetDifferenceTestData(Expressions.TRUE.toString(), 
				new Model(Model.getModelDeclarationFromResource("Example4.model")),
				"{ ( on AnotherWord, X0, AnotherWord', Word, Z, AnotherWord'' ) ( ([ referenceOf(Word, Z) ]), ([ if possessive(Word) and dependency(AnotherWord'', Word) then if referenceOf(Word, Z) and canHave(Z, AnotherWord'') then 1 else 0 else 0.5 ]) ) | X != w7 and X != AnotherWord and AnotherWord != w7 and X0 != w7 and X0 != AnotherWord' and AnotherWord' != w7 and (AnotherWord' != AnotherWord or X0 != X) and (X0 = X and X = AnotherWord' or X0 != X) and AnotherWord' != constituency and Word != AnotherWord' and Word != w7 and (X0 = X and (X = Word or X = AnotherWord') or X0 != X) and (X0 = X and (X != Word and X = AnotherWord' or X = Word) or X0 != X) and Word != X0 and Word != Z and Z != X0 and (X0 != X or X0 = X and (X = constituency or X = Word or Word = constituency)) and (X0 != X or X0 = X and (X = Word or X != constituency and Word != constituency)) and (Word = constituency or Word != constituency and (X0 = constituency or X0 = X)) and (X != constituency and (Z = X or X0 != X or Word = constituency or Word = X) or X = constituency) and (Word = X or Z = X or X != constituency and Word != constituency or X0 != X) and Z != AnotherWord'' and Word != AnotherWord'' }",
				// - 
				"{ ( on Z, AnotherWord ) ( ([ referenceOf(w7, Z) ]), ([ if possessive(w7) and dependency(AnotherWord, w7) then if referenceOf(w7, Z) and canHave(Z, AnotherWord) then 1 else 0 else 0.5 ]) ) | X != w7 and Z != w7 and Z != X and Z != AnotherWord and AnotherWord != w7 }"
				+ " union " +
				"{ ( on AnotherWord, AnotherWord', AnotherWord'' ) ( ([ referenceOf(w7, X) ]), ([ if possessive(w7) and dependency(AnotherWord'', w7) then if referenceOf(w7, X) and canHave(X, AnotherWord'') then 1 else 0 else 0.5 ]) ) | X != w7 and X != AnotherWord and AnotherWord != w7 and X != AnotherWord' and AnotherWord' != w7 and AnotherWord' != AnotherWord and X != AnotherWord'' and AnotherWord'' != w7 and AnotherWord'' != AnotherWord' }"
				+ " union " +
				"{ ( on Word, AnotherWord ) ( ([ referenceOf(Word, X) ]), ([ if possessive(Word) and dependency(AnotherWord, Word) then if referenceOf(Word, X) and canHave(X, AnotherWord) then 1 else 0 else 0.5 ]) ) | X != w7 and X != constituency and X != Word and Word != constituency and Word != w7 and X != AnotherWord and Word != AnotherWord and AnotherWord != constituency }"
				+ " union " +
				"{ ( on AnotherWord, Word, AnotherWord' ) ( ([ referenceOf(Word, X) ]), ([ if possessive(Word) and dependency(AnotherWord', Word) then if referenceOf(Word, X) and canHave(X, AnotherWord') then 1 else 0 else 0.5 ]) ) | X != w7 and X != AnotherWord and AnotherWord != w7 and AnotherWord != constituency and X != Word and Word != AnotherWord and Word != w7 and X != AnotherWord' and Word != AnotherWord' and AnotherWord' != AnotherWord and (X = constituency or Word = constituency or AnotherWord' = constituency) }"
				+ " union " +
				"{ ( on AnotherWord, X0, Word, AnotherWord' ) ( ([ referenceOf(Word, X0) ]), ([ if possessive(Word) and dependency(AnotherWord', Word) then if referenceOf(Word, X0) and canHave(X0, AnotherWord') then 1 else 0 else 0.5 ]) ) | X != w7 and X != AnotherWord and AnotherWord != w7 and X0 != w7 and X0 != constituency and X0 != X and X0 != Word and Word != constituency and Word != w7 and X0 != AnotherWord' and Word != AnotherWord' and AnotherWord' != constituency }"
				+ " union " +
				"{ ( on AnotherWord, X0, AnotherWord', Word, AnotherWord'' ) ( ([ referenceOf(Word, X0) ]), ([ if possessive(Word) and dependency(AnotherWord'', Word) then if referenceOf(Word, X0) and canHave(X0, AnotherWord'') then 1 else 0 else 0.5 ]) ) | X != w7 and X != AnotherWord and AnotherWord != w7 and X0 != w7 and X0 != AnotherWord' and AnotherWord' != w7 and (AnotherWord' != AnotherWord or X0 != X) and (X0 = X and X = AnotherWord' or X0 != X) and AnotherWord' != constituency and X0 != Word and Word != AnotherWord' and Word != w7 and (X0 = X and (X = Word or X = AnotherWord') or X0 != X) and (X0 = X and (X != Word and X = AnotherWord' or X = Word) or X0 != X) and X0 != AnotherWord'' and Word != AnotherWord'' and AnotherWord'' != AnotherWord' and (X != constituency and (X0 = X and (X = AnotherWord'' or AnotherWord'' = constituency or X = Word or Word = constituency) or X0 != X) or X = constituency) and (X0 != X or X0 = X and (X = Word or X = AnotherWord'' or X != constituency and Word != constituency and AnotherWord'' != constituency)) and (Word = constituency or AnotherWord'' = constituency or Word != constituency and AnotherWord'' != constituency and (X0 = constituency or X0 = X)) }"
				+ " union " +
				"{ ( on Word, X0, AnotherWord, AnotherWord' ) ( ([ referenceOf(Word, X0) ]), ([ if possessive(Word) and dependency(AnotherWord', Word) then if referenceOf(Word, X0) and canHave(X0, AnotherWord') then 1 else 0 else 0.5 ]) ) | X != w7 and X != constituency and X != Word and Word != constituency and Word != w7 and X0 != Word and X0 != AnotherWord and Word != AnotherWord and (AnotherWord != constituency or X0 != X) and (X0 = X and AnotherWord != constituency or X0 != X) and (X0 = X and (X = AnotherWord or AnotherWord = w7 or AnotherWord = constituency) or X0 != X) and (AnotherWord != constituency or X0 = w7 or X0 = constituency or X0 = X) and (X0 = w7 or AnotherWord = w7 or X != AnotherWord and X0 = X or AnotherWord = constituency) and X0 != AnotherWord' and Word != AnotherWord' and AnotherWord' != AnotherWord and (X0 = X and (X = AnotherWord' or AnotherWord' = constituency) or X0 != X) and (X0 != X or X0 = X and AnotherWord' != constituency) and (AnotherWord' = constituency or AnotherWord' != constituency and (X0 = w7 or X0 = constituency or X0 = X)) and (X0 = w7 or X != AnotherWord' and AnotherWord' != constituency and X0 = X or X0 = X and AnotherWord' = constituency or AnotherWord' != constituency and X0 != constituency and X0 != X or X = AnotherWord' and AnotherWord' != constituency and X0 = X) }"
				+ " union " +
				"{ ( on AnotherWord, Word, X0, AnotherWord', AnotherWord'' ) ( ([ referenceOf(Word, X0) ]), ([ if possessive(Word) and dependency(AnotherWord'', Word) then if referenceOf(Word, X0) and canHave(X0, AnotherWord'') then 1 else 0 else 0.5 ]) ) | X != w7 and X != AnotherWord and AnotherWord != w7 and AnotherWord != constituency and X != Word and Word != AnotherWord and Word != w7 and X0 != Word and X0 != AnotherWord' and Word != AnotherWord' and (AnotherWord' != AnotherWord or X0 != X) and (X = constituency or Word = constituency or AnotherWord' = constituency and X0 = X) and (X != constituency and (X0 = X and (Word = constituency or AnotherWord' != constituency) or X0 != X) or X = constituency) and (X0 = X and (X = AnotherWord' or AnotherWord' = w7 or AnotherWord' = constituency) or X0 != X) and (Word = constituency or AnotherWord' != constituency or Word != constituency and AnotherWord' = constituency and (X0 = w7 or X0 = constituency or X0 = X)) and (X0 = w7 or AnotherWord' = w7 or X != AnotherWord' and X0 = X or AnotherWord' = constituency) and (X = constituency or Word = constituency or AnotherWord' = constituency and X0 = X or X != AnotherWord' and AnotherWord' != w7 and AnotherWord' != constituency and X0 = X or AnotherWord' = constituency and X0 != w7 and X0 != constituency and X0 != X) and X0 != AnotherWord'' and Word != AnotherWord'' and AnotherWord'' != AnotherWord' and (X != constituency and (X0 = X and (X = AnotherWord'' or AnotherWord'' = constituency or Word = constituency) or X0 != X) or X = constituency) and (X0 != X or X0 = X and (X = AnotherWord'' or X != constituency and Word != constituency and AnotherWord'' != constituency)) and (Word = constituency or AnotherWord'' = constituency or Word != constituency and AnotherWord'' != constituency and (X0 = w7 or X0 = constituency or X0 = X)) and (X0 = w7 or X != AnotherWord'' and AnotherWord'' != constituency and Word != constituency and X0 = X and X != constituency or X0 = X and X != AnotherWord'' and (X = constituency or Word = constituency or AnotherWord'' = constituency) or Word != constituency and AnotherWord'' != constituency and X0 != constituency and X0 != X or (X = AnotherWord'' or AnotherWord'' = constituency or Word = constituency or X = constituency) and (X = AnotherWord'' or X != constituency and Word != constituency and AnotherWord'' != constituency) and X0 = X) and (X = constituency or Word = constituency or X != AnotherWord'' and AnotherWord'' != constituency and X0 = X or X0 = X and AnotherWord'' = constituency or AnotherWord'' != constituency and X0 != w7 and X0 != constituency and X0 != X or X0 != w7 and (X = AnotherWord'' or AnotherWord'' = constituency or X0 != X) and (X0 != X or AnotherWord'' != constituency) and (AnotherWord'' = constituency or X0 = constituency or X0 = X) and (X != AnotherWord'' or AnotherWord'' = constituency or X0 != X) or (X = AnotherWord'' or AnotherWord'' = constituency or X0 != X) and (X0 != X or AnotherWord'' != constituency) and (AnotherWord'' = constituency or X0 = w7 or X0 = constituency or X0 = X) and (X0 = w7 or AnotherWord'' != constituency and X0 != constituency and X0 != X or X = AnotherWord'' and AnotherWord'' != constituency and X0 = X) and (X0 != w7 or AnotherWord'' = X) and (X0 != X and AnotherWord'' = w7 and X0 != w7 or X0 = X and (X0 = w7 or AnotherWord'' = w7))) }"
				,
				false,
				"{ ( on Word, Z, AnotherWord'' ) ( ([ referenceOf(Word, Z) ]), ([ if possessive(Word) and dependency(AnotherWord'', Word) then if referenceOf(Word, Z) and canHave(Z, AnotherWord'') then 1 else 0 else 0.5 ]) ) | X != w7 and Word != w7 and Word != Z and Z != AnotherWord'' and Word != AnotherWord'' and (Word = constituency or Z != constituency and X != constituency) and (X = constituency or (Z = X and (X = Word or Word = constituency or X = AnotherWord'' or AnotherWord'' = constituency) or Z != X)) and (Z != X or (X = Word or X = AnotherWord'' or X != constituency and Word != constituency and AnotherWord'' != constituency)) and (Word = constituency or AnotherWord'' = constituency or (Z = w7 or Z = constituency or Z = X)) and (Z = w7 or X != AnotherWord'' and AnotherWord'' != constituency and X != Word and Word != constituency and Z = X and X != constituency or Z = X and X != Word and X != AnotherWord'' and (X = constituency or Word = constituency or AnotherWord'' = constituency) or Word != constituency and AnotherWord'' != constituency and Z != constituency and Z != X or (X = AnotherWord'' or AnotherWord'' = constituency or X = Word or Word = constituency or X = constituency) and (X = Word or X = AnotherWord'' or X != constituency and Word != constituency and AnotherWord'' != constituency) and Z = X) and (X = constituency or X = Word or Word = constituency or X != AnotherWord'' and AnotherWord'' != constituency and Z = X or Z = X and AnotherWord'' = constituency or AnotherWord'' != constituency and Z != w7 and Z != constituency and Z != X or Z != w7 and (X = AnotherWord'' or AnotherWord'' = constituency or Z != X) and (Z != X or AnotherWord'' != constituency) and (AnotherWord'' = constituency or Z = constituency or Z = X) and (X != AnotherWord'' or AnotherWord'' = constituency or Z != X) or (X = AnotherWord'' or AnotherWord'' = constituency or Z != X) and (Z != X or AnotherWord'' != constituency) and (AnotherWord'' = constituency or Z = w7 or Z = constituency or Z = X) and (Z = w7 or AnotherWord'' != constituency and Z != constituency and Z != X or X = AnotherWord'' and AnotherWord'' != constituency and Z = X) and (Z != w7 or AnotherWord'' = X) and (Z != X and (Z = constituency and AnotherWord'' = w7 or Z != w7 and Z != constituency and AnotherWord'' = w7) or Z = X and (Z = w7 or AnotherWord'' = w7))) and (X = Word or X != AnotherWord'' and AnotherWord'' != constituency and Word != constituency and Z = X and X != constituency or Z = X and X != AnotherWord'' and (X = constituency or Word = constituency or AnotherWord'' = constituency) or Word != constituency and AnotherWord'' != constituency and Z != w7 and Z != constituency and Z != X or Z != w7 and (X = AnotherWord'' or AnotherWord'' = constituency or Word = constituency or Z != X or X = constituency) and (Z != X or X = AnotherWord'' or X != constituency and Word != constituency and AnotherWord'' != constituency) and (Word = constituency or AnotherWord'' = constituency or Z = constituency or Z = X) and (X != AnotherWord'' and AnotherWord'' != constituency and Word != constituency and X != constituency or X != AnotherWord'' and (X = constituency or Word = constituency or AnotherWord'' = constituency) or Z != X) or X != constituency and Word != constituency and (X = AnotherWord'' or AnotherWord'' = constituency or Z != X) and (Z != X or AnotherWord'' != constituency) and (AnotherWord'' = constituency or Z = w7 or Z = constituency or Z = X) and (Z = w7 or X != AnotherWord'' and AnotherWord'' != constituency and Z = X or Z = X and AnotherWord'' = constituency or AnotherWord'' != constituency and Z != constituency and Z != X or X = AnotherWord'' and AnotherWord'' != constituency and Z = X) and (X != AnotherWord'' and AnotherWord'' != constituency and Z = X or Z = X and AnotherWord'' = constituency or AnotherWord'' != constituency and Z != w7 and Z != constituency and Z != X or Z != w7 and (AnotherWord'' = constituency or Z = constituency or Z = X) and (X != AnotherWord'' or AnotherWord'' = constituency or Z != X) or Z = w7 and AnotherWord'' != X or (Z = X or AnotherWord'' != w7 or Z = w7) and (Z != X or Z != w7 and AnotherWord'' != w7)) or (X = AnotherWord'' or AnotherWord'' = constituency or Word = constituency or Z != X or X = constituency) and (Z != X or X = AnotherWord'' or X != constituency and Word != constituency and AnotherWord'' != constituency) and (Word = constituency or AnotherWord'' = constituency or Z = w7 or Z = constituency or Z = X) and (Z = w7 or Word != constituency and AnotherWord'' != constituency and Z != constituency and Z != X or (X = AnotherWord'' or AnotherWord'' = constituency or Word = constituency or X = constituency) and (X = AnotherWord'' or X != constituency and Word != constituency and AnotherWord'' != constituency) and Z = X) and (X = constituency or Word = constituency or X != AnotherWord'' and AnotherWord'' != constituency and Z = X or Z = X and AnotherWord'' = constituency or AnotherWord'' != constituency and Z != w7 and Z != constituency and Z != X or Z != w7 and (X = AnotherWord'' or AnotherWord'' = constituency or Z != X) and (Z != X or AnotherWord'' != constituency) and (AnotherWord'' = constituency or Z = constituency or Z = X) and (X != AnotherWord'' or AnotherWord'' = constituency or Z != X) or (X = AnotherWord'' or AnotherWord'' = constituency or Z != X) and (Z != X or AnotherWord'' != constituency) and (AnotherWord'' = constituency or Z = w7 or Z = constituency or Z = X) and (Z = w7 or AnotherWord'' != constituency and Z != constituency and Z != X or X = AnotherWord'' and AnotherWord'' != constituency and Z = X) and (Z != w7 or AnotherWord'' = X) and (Z != X and AnotherWord'' = w7 and Z != w7 or Z = X and (Z = w7 or AnotherWord'' = w7))) and (Z = X or X != constituency and Word != constituency or Word != constituency and X = constituency and Z != w7 and Z != constituency or Z != w7 and X != constituency or AnotherWord'' = X) and (Z != X and (Z = w7 or X != constituency and Word != constituency or AnotherWord'' = w7) and (X != constituency and Word != constituency or Word != constituency and X = constituency and Z != w7 and Z != constituency or Z != w7 and X != constituency or AnotherWord'' = X) and (Word = constituency or X != constituency or Z != w7 or AnotherWord'' = constituency) and (Word = constituency and (X = constituency and Z != constituency and Z != w7 and AnotherWord'' = w7 or X != constituency and Z != w7 and AnotherWord'' = w7) or Word != constituency and (Z != w7 and Z != constituency and AnotherWord'' = w7 or X != constituency)) or Z = X and (Z = w7 or X != constituency and Word != constituency or AnotherWord'' = w7) and (Z = constituency or Word = constituency or X != constituency or AnotherWord'' = constituency))) }"
				)
		});
	}
	
	class SetDifferenceTestData extends TestData {
		private String S1, S2; 
		private Expression exprS1, exprS2;
		
		public SetDifferenceTestData(String contextualConstraint, Model model, String S1, String S2, boolean illegalArgumentTest,  String expected) {
			super(contextualConstraint, model, illegalArgumentTest, expected);
			this.S1 = S1;
			this.S2 = S2;
		};
		
		@Override
		public Expression getTopExpression() {
			this.exprS1 = parse(S1);
			this.exprS2 = parse(S2);
			
			return Expressions.apply("-", exprS1, exprS2);
		}
		
		@Override
		public Expression callRewrite(RewritingProcess process) {
			return process.rewrite(LBPRewriter.R_set_diff,
							LPIUtil.argForSetDifferenceRewriteCall(exprS1, exprS2));
		}
	};
}

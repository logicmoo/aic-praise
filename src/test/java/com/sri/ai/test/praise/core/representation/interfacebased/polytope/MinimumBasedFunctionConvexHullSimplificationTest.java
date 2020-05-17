package com.sri.ai.test.praise.core.representation.interfacebased.polytope;

import static com.sri.ai.praise.core.representation.interfacebased.factor.core.table.core.bydatastructure.arraylist.ArrayTableFactor.arrayTableFactor;
import static com.sri.ai.praise.core.representation.interfacebased.factor.core.table.core.bydatastructure.arraylist.ArrayTableFactor.decimalPlaces;
import static com.sri.ai.praise.core.representation.interfacebased.factor.core.table.core.bydatastructure.arraylist.ArrayTableFactor.maximumNumberOfEntriesToShow;
import static com.sri.ai.praise.core.representation.interfacebased.polytope.core.MinimumBasedFunctionConvexHullSimplification.simplify;
import static com.sri.ai.util.Util.arrayList;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.println;
import static com.sri.ai.util.Util.repeat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sri.ai.praise.core.representation.interfacebased.factor.api.Factor;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.table.core.base.TableVariable;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.table.core.bydatastructure.arraylist.ArrayTableFactor;
import com.sri.ai.praise.core.representation.interfacebased.polytope.core.DefaultFunctionConvexHull;
import com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger;

class MinimumBasedFunctionConvexHullSimplificationTest {

	@BeforeEach
	void before() {
		maximumNumberOfEntriesToShow = -1;
		decimalPlaces = 5;
		ThreadExplanationLogger.setIsActive(true);
	}
	
	@AfterEach
	void after() {
		ThreadExplanationLogger.setIsActive(false);
	}
	
	@Test
	void tests() {
		
		int iCardinality = 5;
		int jCardinality = 5;
		int aCardinality = 2;
		int bCardinality = 2;
		
		TableVariable i = new TableVariable("i", iCardinality);
		TableVariable j = new TableVariable("j", jCardinality);
		TableVariable a = new TableVariable("a", aCardinality);
		TableVariable b = new TableVariable("b", bCardinality);
		TableVariable i0 = new TableVariable("I0", aCardinality);
		TableVariable i1 = new TableVariable("I1", bCardinality);
		
		LinkedList<TableVariable> indices;
		Factor factor;
		LinkedList<TableVariable> expectedIndices;
		Factor expectedFactor;
		
		indices = list(i);
		factor = arrayTableFactor(
				list(i, a), 
				(vi, va) -> 
					vi == 0? 
						va == 0 ? 2 : 8 : 
						va == 0 ? 9 : 1);
		expectedIndices = list(i0);
		expectedFactor = new ArrayTableFactor(
				list(i0, a),
				new double[] {0.9, 0.1, 0.2, 0.8});
		runTest(indices, factor, expectedIndices, expectedFactor);

		
		
		indices = list(i, j);
		factor = arrayTableFactor(
				list(i, j, a), 
				(vi, vj, va) -> 
					vi == 0? 
						vj == 0?
								va == 0 ? 2 : 8 : 
								va == 0 ? 9 : 1 :
						vj == 0?
								va == 0 ? 3 : 7 : 
								va == 0 ? 0.5 : 0.5
						);
		expectedIndices = list(i0);
		expectedFactor = new ArrayTableFactor(
				list(i0, a),
				new double[] {0.9, 0.1, 0.2, 0.8});
		runTest(indices, factor, expectedIndices, expectedFactor);

		
		
		indices = list(i);
		factor = arrayTableFactor(
				list(i, a, b), 
				(vi, va, vb) -> 
					vi == 0? 
						va == 0?
								vb == 0 ? 0.2 : 0.3 : 
								vb == 0 ? 0.4 : 0.1 :
						va == 0?
								vb == 0 ? 0.05 : 0.45 : 
								vb == 0 ? 0.25 : 0.25
						);
		expectedIndices = list(i0, i1);
		expectedFactor = new ArrayTableFactor(
				list(i0, i1, a, b),
				new double[] {0.35, 0.3, 0.25, 0.1, 0.05, 0.6, 0.25, 0.1, 0.05, 0.3, 0.55, 0.1, 0.05, 0.3, 0.25, 0.4});
		runTest(indices, factor, expectedIndices, expectedFactor);

		
		
		indices = list(i, j);
		factor = arrayTableFactor(
				list(i, j, a, b), 
				(vi, vj, va, vb) -> 
					vi == 0? 
						va == 0?
								vb == 0 ? 0.2 : 0.3 : 
								vb == 0 ? 0.4 : 0.1 :
						va == 0?
								vb == 0 ? 0.05 : 0.45 : 
								vb == 0 ? 0.25 : 0.25
						);
		expectedIndices = list(i0, i1);
		expectedFactor = new ArrayTableFactor(
				list(i0, i1, a, b),
				new double[] {0.35, 0.3, 0.25, 0.1, 0.05, 0.6, 0.25, 0.1, 0.05, 0.3, 0.55, 0.1, 0.05, 0.3, 0.25, 0.4});
		runTest(indices, factor, expectedIndices, expectedFactor);

		
		
		indices = list();
		factor = arrayTableFactor(
				list(a, b), 
				(va, vb) -> 
						va == 0?
								vb == 0 ? 2 : 3 : 
								vb == 0 ? 4 : 1
						);
		expectedIndices = list(i0, i1);
		expectedFactor = new ArrayTableFactor(
				list(i0, i1, a, b),
				repeat(aCardinality * bCardinality,  new double[] {0.2, 0.3, 0.4, 0.1}));
		runTest(indices, factor, expectedIndices, expectedFactor);

		
		
		indices = list();
		factor = new ArrayTableFactor(list(), arrayList(1.0));
		expectedIndices = list();
		expectedFactor = new ArrayTableFactor(list(), new double[] {1.0});
		runTest(indices, factor, expectedIndices, expectedFactor);
	}

	private void runTest(
			LinkedList<TableVariable> indices,
			Factor factor,
			LinkedList<TableVariable> expectedIndices,
			Factor expectedFactor) {
		
		var functionConvexHull = new DefaultFunctionConvexHull(indices, factor);
		var forced = true;
		var actual = simplify(functionConvexHull, forced);
		
		var expected = new DefaultFunctionConvexHull(expectedIndices, expectedFactor);
		
		println("MinimumBasedFunctionConvexHullSimplificationTest");
		println("Convex hull: " + functionConvexHull + ", length: " + functionConvexHull.length());
		println(" Simplified: " + actual + ", length: " + actual.length());
		println("   Expected: " + expected);
		
		var checkEquality = expected.checkEquality(actual);
		println("Equality: " + checkEquality);
		assertTrue(checkEquality.areEqual());
		assertEquals(expected, actual);
	}
}
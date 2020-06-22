package com.sri.ai.test.praise.core.representation.table;

import static com.sri.ai.praise.core.representation.interfacebased.factor.api.equality.FactorsEqualityCheck.factorsAreEqual;
import static com.sri.ai.praise.core.representation.interfacebased.factor.api.equality.FactorsEqualityCheck.factorsAreOfIncomparableClasses;
import static com.sri.ai.praise.core.representation.interfacebased.factor.api.equality.FactorsEqualityCheck.factorsHaveDifferentValues;
import static com.sri.ai.praise.core.representation.interfacebased.factor.api.equality.FactorsEqualityCheck.factorsHaveDifferentVariables;
import static com.sri.ai.praise.core.representation.interfacebased.factor.core.table.core.bydatastructure.arraylist.ArrayTableFactor.arrayTableFactor;
import static com.sri.ai.util.Util.arrayList;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.print;
import static com.sri.ai.util.Util.println;
import static com.sri.ai.util.Util.set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sri.ai.praise.core.representation.interfacebased.factor.api.Factor;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.base.KroneckerDeltaFactor;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.table.api.TableFactor;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.table.core.base.TableVariable;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.table.core.bydatastructure.arraylist.ArrayTableFactor;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.table.core.bydatastructure.ndarray.NDArrayTableFactor;
import com.sri.ai.util.Util;

/**
 * Class to test the TableFactor data type
 * 
 * @author Bobak
 *
 */
public class TableFactorTest {

	@BeforeEach
	public void setUp() {
		ArrayTableFactor.maximumNumberOfEntriesToShow = 100;
		ArrayTableFactor.decimalPlaces = -1;
		println(ArrayTableFactor.class.getSimpleName() + " in " + TableFactorTest.class.getSimpleName() + " will only show tables with up to " + ArrayTableFactor.maximumNumberOfEntriesToShow + " elements.");
	}
	
	// CREATE TABLES TO TEST //////////////////////////////////////////////////////////////////////////////////////////
	
	TableVariable V1 = new TableVariable("V1", 2);
	TableVariable V2 = new TableVariable("V2", 3);
	TableVariable V3 = new TableVariable("V3", 4);
	TableVariable V4 = new TableVariable("V4", 2);

	ArrayTableFactor f0 = new ArrayTableFactor("f0",arrayList(),
			arrayList(1.));
	ArrayTableFactor f1 = new ArrayTableFactor("f1",arrayList(V1,V2,V3),
			arrayList(1., 1., 1., 1., 1., 1., 1., 1., 1., 1., 1., 1.,1., 1., 1., 1., 1., 1., 1., 1., 1., 1., 1., 1.));
	ArrayTableFactor f2 = new ArrayTableFactor("f2",arrayList(V2,V4), 
			arrayList(11., 12., 21., 22., 31., 32.));
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	
	// TEST CASES //////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testSummationCost() {
		assertEquals(1,  f0.summationCost());
		assertEquals(24, f1.summationCost());
		assertEquals(6,  f2.summationCost());
	}
	
	@Test
	public void checkInitialFactors() {
		
		println();
		println("PRINTING INITIAL FACTORS");
		println("------------------------");
		
		println(f0);
		assertEquals("f0[]: [1.0]", f0.toString());
		
		println(f1);
		assertEquals("f1[V1, V2, V3]: [1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, "
															   + "1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0]", f1.toString());
		println(f2);
		assertEquals("f2[V2, V4]: [11.0, 12.0, 21.0, 22.0, 31.0, 32.0]", f2.toString());

		println();
	}
	
	@Test
	public void testMultiplicationf0f1() {
		
		println();
		println("MULTIPLYING f0 * f1");
		println("-------------------");
		
		ArrayTableFactor f0f1 = f0.multiply(f1);
		f0f1.setName("f1f2");
		
		println(f0f1);
		assertEquals("f1f2[V1, V2, V3]: [1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0]",
				   f0f1.toString());
		println();
	}
	
	@Test
	public void testMultiplicationf1f2() {
		
		println();
		println("MULTIPLYING f1 * f2");
		println("-------------------");
		
		ArrayTableFactor f1f2 = f1.multiply(f2);
		f1f2.setName("f1f2");
		
		println(f1f2);
		assertEquals(
				"f1f2[V1, V3, V4, V2]: [11.0, 21.0, 31.0, 12.0, 22.0, 32.0, 11.0, 21.0, 31.0, 12.0, 22.0, 32.0, 11.0, 21.0, 31.0, 12.0, 22.0, 32.0, 11.0, 21.0, 31.0, 12.0, 22.0, 32.0, 11.0, 21.0, 31.0, 12.0, 22.0, 32.0, 11.0, 21.0, 31.0, 12.0, 22.0, 32.0, 11.0, 21.0, 31.0, 12.0, 22.0, 32.0, 11.0, 21.0, 31.0, 12.0, 22.0, 32.0]",
				f1f2.toString());
		println();
	}
	
	@Test
	public void testMultiplicationf2f1() {
		
		println();
		println("MULTIPLYING f2 * f1");
		println("-------------------");
		
		ArrayTableFactor f2f1 = f2.multiply(f1);
		f2f1.setName("f2f1");

		println(f2f1);
		assertEquals(
				"f2f1[V4, V1, V3, V2]: [11.0, 21.0, 31.0, 11.0, 21.0, 31.0, 11.0, 21.0, 31.0, 11.0, 21.0, 31.0, 11.0, 21.0, 31.0, 11.0, 21.0, 31.0, 11.0, 21.0, 31.0, 11.0, 21.0, 31.0, 12.0, 22.0, 32.0, 12.0, 22.0, 32.0, 12.0, 22.0, 32.0, 12.0, 22.0, 32.0, 12.0, 22.0, 32.0, 12.0, 22.0, 32.0, 12.0, 22.0, 32.0, 12.0, 22.0, 32.0]", 
				f2f1.toString());
		println();
	}
	
	@Test
	public void testf0SumOutNothing() {
		
		println();
		println("SUM OUT nothing from f0");
		println("------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList();
		ArrayTableFactor f0SumOutNothing = f0.sumOut(variablesToSumOut);
		f0SumOutNothing.setName("f0SumOutNothing");
		
		println(f0SumOutNothing);
		assertEquals("f0SumOutNothing[]: [1.0]", f0SumOutNothing.toString());
		
		println();
	}
	
	@Test
	public void testf1SumOutV1() {
		
		println();
		println("SUM OUT V1 from F1");
		println("------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList(V1);
		ArrayTableFactor f1SumOutV1 = f1.sumOut(variablesToSumOut);
		f1SumOutV1.setName("f1SumOutV1");
		
		println(f1SumOutV1);
		assertEquals("f1SumOutV1[V2, V3]: [2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0]", f1SumOutV1.toString());
		
		println();
	}
	
	@Test
	public void testf1SumOutV2() {
		
		println();
		println("SUM OUT V2 from F1");
		println("------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList(V2);
		ArrayTableFactor f1SumOutV2 = f1.sumOut(variablesToSumOut);
		f1SumOutV2.setName("f1SumOutV2");
		
		println(f1SumOutV2);
		assertEquals("f1SumOutV2[V1, V3]: [3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0]", f1SumOutV2.toString());
		
		println();
	}
	
	@Test
	public void testf1SumOutV3() {
		
		println();
		println("SUM OUT V3 from F1");
		println("------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList(V3);
		ArrayTableFactor f1SumOutV3 = f1.sumOut(variablesToSumOut);
		f1SumOutV3.setName("f1SumOutV3");
		
		println(f1SumOutV3);
		assertEquals("f1SumOutV3[V1, V2]: [4.0, 4.0, 4.0, 4.0, 4.0, 4.0]", f1SumOutV3.toString());
		
		println();
	}
	
	public void testf2SumOutV2() {
		
		println();
		println("SUM OUT V2 from F2");
		println("------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList(V2);
		ArrayTableFactor f2SumOutV2 = f2.sumOut(variablesToSumOut);
		f2SumOutV2.setName("f2SumOutV2");
		
		println(f2SumOutV2);
		assertEquals("f2SumOutV2[V4]: [63.0, 66.0]", f2SumOutV2.toString());
		
		println();
	}
	
	public void testf2SumOutV4() {
		
		println();
		println("SUM OUT V4 from F2");
		println("------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList(V4);
		ArrayTableFactor f2SumOutV4 = f2.sumOut(variablesToSumOut);
		f2SumOutV4.setName("f2SumOutV4");
		
		println(f2SumOutV4);
		assertEquals("f2SumOutV4[V4]: [13.0, 23.0, 33.0]", f2SumOutV4.toString());
		
		println();
	}
	
	
	
	@Test
	public void testf1SumOutV1V2() {
		
		println();
		println("SUM OUT V1 and V2 frpm F1");
		println("-------------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList(V1,V2);
		ArrayTableFactor f1SumOutV1V2 = f1.sumOut(variablesToSumOut);
		f1SumOutV1V2.setName("f1SumOutV1V2");
		
		println(f1SumOutV1V2);
		assertEquals("f1SumOutV1V2[V3]: [6.0, 6.0, 6.0, 6.0]", f1SumOutV1V2.toString());
		
		println();
	}
	
	@Test
	public void testf1SumOutV1V3() {
		
		println();
		println("SUM OUT V1 and V3 from F1");
		println("-------------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList(V1,V3);
		ArrayTableFactor f1SumOutV1V3 = f1.sumOut(variablesToSumOut);
		f1SumOutV1V3.setName("f1SumOutV1V3");
		
		println(f1SumOutV1V3);
		assertEquals("f1SumOutV1V3[V2]: [8.0, 8.0, 8.0]", f1SumOutV1V3.toString());
		
		println();
	}
	
	@Test
	public void testf1SumOutV2V3() {
		
		println();
		println("SUM OUT V2 and V3 from F1");
		println("-------------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList(V2,V3);
		ArrayTableFactor f1SumOutV2V3 = f1.sumOut(variablesToSumOut);
		f1SumOutV2V3.setName("f1SumOutV2V3");
		
		println(f1SumOutV2V3);
		assertEquals("f1SumOutV2V3[V1]: [12.0, 12.0]", f1SumOutV2V3.toString());
		
		println();
	}
	
	
	
	@Test
	public void testf1SumOutV1V2V3() {
		
		println();
		println("SUM OUT V1, V2, and V3 from F1");
		println("-------------------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList(V1,V2,V3);
		TableFactor f1SumOutV1V2V3 = f1.sumOut(variablesToSumOut);
		f1SumOutV1V2V3.setName("f1SumOutV1V2V3");
		
		print("f1SumOutV1V2V3: "); println(f1SumOutV1V2V3);
		assertEquals("f1SumOutV1V2V3[]: [24.0]", f1SumOutV1V2V3.toString());
		
		println();
	}
	
	@Test
	public void testf2SumOutV2V4() {
		
		println();
		println("SUM OUT V2 and V4 from F2");
		println("-------------------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList(V2,V4);
		TableFactor f2SumOutV2V4 = f2.sumOut(variablesToSumOut);
		f2SumOutV2V4.setName("f2SumOutV2V4");
		
		print("f2SumOutV2V4: "); println(f2SumOutV2V4);
		assertEquals("f2SumOutV2V4[]: [129.0]", f2SumOutV2V4.toString());
		
		println();
	}
	
	
	
	@Test
	public void testf2f1SumOutV1() {
		
		println();
		println("SUM OUT V1 from F2*F1");
		println("------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList(V1);
		ArrayTableFactor f2f1SumOutV1 = (f2.multiply(f1)).sumOut(variablesToSumOut);
		f2f1SumOutV1.setName("f2f1SumOutV1");
		
		println(f2f1SumOutV1);
		assertEquals(
				"f2f1SumOutV1[V4, V3, V2]: [22.0, 42.0, 62.0, 22.0, 42.0, 62.0, 22.0, 42.0, 62.0, 22.0, 42.0, 62.0, 24.0, 44.0, 64.0, 24.0, 44.0, 64.0, 24.0, 44.0, 64.0, 24.0, 44.0, 64.0]", 
				f2f1SumOutV1.toString());
		
		println();
	}
	
	@Test
	public void testf2f1SumOutV1V2() {
		
		println();
		println("SUM OUT V1 and V2 from F2*F1");
		println("------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList(V1,V2);
		ArrayTableFactor f2f1SumOutV1V2 = (f2.multiply(f1)).sumOut(variablesToSumOut);
		f2f1SumOutV1V2.setName("f2f1SumOutV1V2");
		
		println(f2f1SumOutV1V2);
		assertEquals("f2f1SumOutV1V2[V4, V3]: [126.0, 126.0, 126.0, 126.0, 132.0, 132.0, 132.0, 132.0]", f2f1SumOutV1V2.toString());
		
		println();
	}
	
	@Test
	public void testf2f1SumOutV1V2V3() {
		
		println();
		println("SUM OUT V1, V2, and V3 from F2*F1");
		println("------------------");

		ArrayList<TableVariable> variablesToSumOut = Util.arrayList(V1,V2,V3);
		ArrayTableFactor f2f1SumOutV1V2V3 = (f2.multiply(f1)).sumOut(variablesToSumOut);
		f2f1SumOutV1V2V3.setName("f2f1SumOutV1V2");
		
		println(f2f1SumOutV1V2V3);
		assertEquals("f2f1SumOutV1V2[V4]: [504.0, 528.0]", f2f1SumOutV1V2V3.toString());
		
		println();
	}
	
	
	@Test
	public void testSumOutAllinf1() {
		
		println();
		println("SUM OUT ALL from F1");
		println("------------------");

		ArrayList<? extends TableVariable> variablesToSumOut = f1.getVariables();
		ArrayTableFactor f1SumOutAll = f1.sumOut(variablesToSumOut);
		f1.setName("f1SumOutAll");
		
		println(f1SumOutAll);
		assertEquals("phi[]: [24.0]", f1SumOutAll.toString());
		
		println();
	}
	
	
	@Test
	public void testF2NormalizationOnAllVariables() {
		
		println();
		println("Normalize F2");
		println("------------------");

		ArrayList<? extends TableVariable> variablesToNormalize = f2.getVariables();
		ArrayTableFactor normalizedF2 = f2.normalizeBySummingOverThese(variablesToNormalize);
		normalizedF2.setName("normalizedF2");
		
		println(normalizedF2);
		assertEquals(
				"normalizedF2[V2, V4]: [0.08527131782945736, 0.09302325581395349, 0.16279069767441862, 0.17054263565891473, 0.24031007751937986, 0.24806201550387597]", 
				normalizedF2.toString());
		
		assertEquals(1.0, normalizedF2.sumOut(normalizedF2.getVariables()).getEntries().get(0), 0.0001);
		
		println();
	}
	
	
	@Test
	public void testF2NormalizationOnV2() {
		
		println();
		println("Normalize F2 on V2");
		println("------------------");

		ArrayList<? extends TableVariable> variablesToNormalize = arrayList(V2);
		ArrayTableFactor normalizedF2OnV2 = f2.normalizeBySummingOverThese(variablesToNormalize);
		normalizedF2OnV2.setName("normalizedF2OnV2");
		
		println(normalizedF2OnV2);
		assertEquals(
				"normalizedF2OnV2[V2, V4]: [0.1746031746031746, 0.18181818181818182, 0.3333333333333333, 0.3333333333333333, 0.49206349206349204, 0.48484848484848486]", 
				normalizedF2OnV2.toString());
		
		assertEquals(arrayList(1.0, 1.0), normalizedF2OnV2.sumOut(list(V2)).getEntries());
		
		println();
	}
	
	
	@Test
	public void testF2NormalizationOnNoVariables() {
		
		println();
		println("Normalize F2 on no variables");
		println("------------------");

		ArrayList<? extends TableVariable> variablesToNormalize = arrayList();
		ArrayTableFactor normalizedF2OnNoVariables = f2.normalizeBySummingOverThese(variablesToNormalize);
		normalizedF2OnNoVariables.setName("normalizedF2OnNoVariables");
		
		println(normalizedF2OnNoVariables);
		assertEquals(
				"normalizedF2OnNoVariables[V2, V4]: [1.0, 1.0, 1.0, 1.0, 1.0, 1.0]", 
				normalizedF2OnNoVariables.toString());
		
		assertEquals(arrayList(1.0, 1.0, 1.0, 1.0,1.0, 1.0), normalizedF2OnNoVariables.getEntries());
		
		println();
	}
	
	@Test
	public void testMathematicallyEquivalent() {
		
		TableVariable x = new TableVariable("x", 2);
		TableVariable y = new TableVariable("y", 2);
		TableVariable z = new TableVariable("z", 2);
		
		TableVariable u = new TableVariable("u", 1);
		TableVariable v = new TableVariable("v", 2);
		TableVariable w = new TableVariable("w", 3);
		
		ArrayTableFactor f1;
		ArrayTableFactor f2;
		
		f1 = new ArrayTableFactor(list(), new double[] {1});
		f2 = new ArrayTableFactor(list(), new double[] {1});
		assertTrue(f1.mathematicallyEquals(f2));
		
		f1 = new ArrayTableFactor(list(), new double[] {1});
		f2 = new ArrayTableFactor(list(x), new double[] {1, 2});
		assertFalse(f1.mathematicallyEquals(f2));
		
		f1 = new ArrayTableFactor(list(x), new double[] {1, 2});
		f2 = new ArrayTableFactor(list(), new double[] {1});
		assertFalse(f1.mathematicallyEquals(f2));
		
		f1 = new ArrayTableFactor(list(x,y), new double[] {1, 2, 3, 4});
		f2 = new ArrayTableFactor(list(x,y), new double[] {1, 2, 3, 4});
		assertTrue(f1.mathematicallyEquals(f2));
		
		f1 = new ArrayTableFactor(list(x,y), new double[] {1, 2, 3, 4});
		f2 = new ArrayTableFactor(list(y,x), new double[] {1, 3, 2, 4});
		assertTrue(f1.mathematicallyEquals(f2));
		
		f1 = new ArrayTableFactor(list(x,y,z), new double[] {1, 2, 3, 4, 5, 6, 7, 8});
		f2 = new ArrayTableFactor(list(z,y,x), new double[] {1, 5, 3, 7, 2, 6, 4, 8});
		assertTrue(f1.mathematicallyEquals(f2));
		
		f1 = new ArrayTableFactor(list(x,y,z), new double[] {1, 2, 3, 4, 5, 6, 7, 8});
		f2 = new ArrayTableFactor(list(z,x,y), new double[] {1, 3, 5, 7, 2, 4, 6, 8});
		assertTrue(f1.mathematicallyEquals(f2));
		
		f1 = new ArrayTableFactor(list(u,v,w), new double[] {1, 2, 3, 4, 5, 6});
		f2 = new ArrayTableFactor(list(w,u,v), new double[] {1, 4, 2, 5, 3, 6});
		assertTrue(f1.mathematicallyEquals(f2));
		
		f1 = new ArrayTableFactor(list(u,v,w), new double[] {1, 2, 3, 4, 5, 6});
		f2 = new ArrayTableFactor(list(w,u,v), new double[] {1, 4, 2000, 5, 3, 6});
		assertFalse(f1.mathematicallyEquals(f2));

		f1 = new ArrayTableFactor(list(x,y), new double[] {1, 2, 3, 4});
		f2 = new ArrayTableFactor(list(y,x), new double[] {1000, 3, 2, 4});
		assertFalse(f1.mathematicallyEquals(f2));
		
		f1 = new ArrayTableFactor(list(x,y), new double[] {1, 2, 3, 4});
		f2 = new ArrayTableFactor(list(y,x,z), new double[] {1, 3, 2, 4, 1, 3, 2, 4});
		assertFalse(f1.mathematicallyEquals(f2));
	}

	
	@Test
	public void testCheckEquality() {
		
		TableVariable x = new TableVariable("x", 2);
		TableVariable y = new TableVariable("y", 2);
		TableVariable z = new TableVariable("z", 2);
		
		TableVariable u = new TableVariable("u", 1);
		TableVariable v = new TableVariable("v", 2);
		TableVariable w = new TableVariable("w", 3);
		
		Factor f1;
		Factor f2;
		
		f1 = new ArrayTableFactor(list(), new double[] {1});
		f2 = new ArrayTableFactor(list(), new double[] {1});
		assertEquals(factorsAreEqual(f1, f2), f1.checkEquality(f2));
		
		f1 = new ArrayTableFactor(list(), new double[] {1});
		f2 = new NDArrayTableFactor(list(), new double[] {1});
		assertEquals(factorsAreOfIncomparableClasses(f1, f2), f1.checkEquality(f2));
		
		f1 = new ArrayTableFactor(list(), new double[] {1});
		f2 = new ArrayTableFactor(list(x), new double[] {1, 2});
		assertEquals(factorsHaveDifferentVariables(f1, f2, set(), set(x)), f1.checkEquality(f2));
		
		f1 = new ArrayTableFactor(list(x,z), new double[] {1, 2, 3, 4});
		f2 = new ArrayTableFactor(list(y,z), new double[] {1, 2, 3, 4});
		assertEquals(factorsHaveDifferentVariables(f1, f2, set(x), set(y)), f1.checkEquality(f2));
		
		f1 = new ArrayTableFactor(list(x), new double[] {1, 2});
		f2 = new ArrayTableFactor(list(), new double[] {1});
		assertEquals(factorsHaveDifferentVariables(f1, f2, set(x), set()), f1.checkEquality(f2));
		
		f1 = new ArrayTableFactor(list(x,y), new double[] {1, 2, 3, 4});
		f2 = new ArrayTableFactor(list(x,y), new double[] {1, 2, 3, 4});
		assertEquals(factorsAreEqual(f1, f2), f1.checkEquality(f2));
		
		f1 = new ArrayTableFactor(list(x,y), new double[] {1, 2, 3, 4});
		f2 = new ArrayTableFactor(list(y,x), new double[] {1, 3, 2, 4});
		assertEquals(factorsAreEqual(f1, f2), f1.checkEquality(f2));
		
		f1 = new ArrayTableFactor(list(x,y), new double[] {1, 2, 3, 4});
		f2 = new ArrayTableFactor(list(y,x), new double[] {1000, 3, 2, 4});
		assertEquals(factorsHaveDifferentValues(f1, f2, list(0,0), 1, 1000), f1.checkEquality(f2));
		
		f1 = new ArrayTableFactor(list(x,y), new double[] {1, 2, 3000, 4});
		f2 = new ArrayTableFactor(list(y,x), new double[] {1, 3, 2, 4});
		assertEquals(factorsHaveDifferentValues(f1, f2, list(1,0), 3000, 3), f1.checkEquality(f2));
		
		f1 = new ArrayTableFactor(list(x,y,z), new double[] {1, 2, 3, 4, 5, 6, 7, 8});
		f2 = new ArrayTableFactor(list(z,y,x), new double[] {1, 5, 3, 7000, 2, 6, 4, 8});
		assertEquals(factorsHaveDifferentValues(f1, f2, list(1,1,0), 7, 7000), f1.checkEquality(f2));
		
		f1 = new ArrayTableFactor(list(x,y,z), new double[] {1, 2, 3, 4, 5, 6, 7, 8});
		f2 = new ArrayTableFactor(list(z,x,y), new double[] {1, 3, 5000, 7, 2, 4, 6, 8});
		assertEquals(factorsHaveDifferentValues(f1, f2, list(1,0,0), 5, 5000), f1.checkEquality(f2));
		
		f1 = new ArrayTableFactor(list(u,v,w), new double[] {1, 2, 3, 4, 5, 6});
		f2 = new ArrayTableFactor(list(w,u,v), new double[] {1, 4, 2, 5, 3, 6});
		assertEquals(factorsAreEqual(f1, f2), f1.checkEquality(f2));
		
		f1 = new ArrayTableFactor(list(u,v,w), new double[] {1, 2, 3, 4, 5, 6});
		f2 = new ArrayTableFactor(list(w,u,v), new double[] {1, 4, 2, 5000, 3, 6});
		assertEquals(factorsHaveDifferentValues(f1, f2, list(0,1,1), 5, 5000), f1.checkEquality(f2));
		
		f1 = new ArrayTableFactor(list(x,y), new double[] {1, 2, 3, 4});
		f2 = new ArrayTableFactor(list(y,x,z), new double[] {1, 3, 2, 4, 1, 3, 2, 4});
		assertEquals(factorsHaveDifferentVariables(f1, f2, set(), set(z)), f1.checkEquality(f2));

		f1 = new ArrayTableFactor(list(x), new double[] {1, 2});
		f2 = new ArrayTableFactor(list(), new double[] {1});
		assertEquals(factorsHaveDifferentVariables(f1, f2, set(x), set()), f1.checkEquality(f2));
		
	}
	
	@Test
	public void testMultiplyByKroneckerDeltaFactor() {
		TableVariable a = new TableVariable("a", 3);
		TableVariable b = new TableVariable("b", 3);
		TableVariable c = new TableVariable("c", 2);
		TableVariable d = new TableVariable("d", 2);
		
		ArrayTableFactor f;
		KroneckerDeltaFactor k;
		Factor expected;
		
		f = arrayTableFactor(list(a, b), 
				(va, vb) -> va == 0? vb == 0? 0.8 : 0.1 : vb == 0? 0.4 : 0.3);
		k = new KroneckerDeltaFactor(a, b);
		expected = new ArrayTableFactor(list(a,b), new double[] {0.8, 0.0, 0.0, 0.0, 0.3, 0.0, 0.0, 0.0, 0.3});
		runMultiplyByKroneckerTest(f, k, expected);
		
		f = arrayTableFactor(list(a, b), 
				(va, vb) -> va == 0? vb == 0? 0.8 : 0.1 : vb == 0? 0.4 : 0.3);
		k = new KroneckerDeltaFactor(c, d);
		expected = arrayTableFactor(list(a,b,c,d), 
				(va, vb, vc, vd) -> vc != vd? 0.0 : va == 0? vb == 0? 0.8 : 0.1 : vb == 0? 0.4 : 0.3);
		runMultiplyByKroneckerTest(f, k, expected);
		
		f = arrayTableFactor(list(a, c), 
				(va, vc) -> va == 0? vc == 0? 0.8 : 0.2 : vc == 0? 0.7 : 0.3);
		k = new KroneckerDeltaFactor(c, d);
		expected = arrayTableFactor(list(a,d,c), 
				(va, vd, vc) -> vc != vd? 0.0 : va == 0? vc == 0? 0.8 : 0.2 : vc == 0? 0.7 : 0.3);
		runMultiplyByKroneckerTest(f, k, expected);
	}

	private void runMultiplyByKroneckerTest(ArrayTableFactor f, KroneckerDeltaFactor k, Factor expected) {
		Factor product;
		product = f.multiply(k);
		println(f);
		println("*");
		println(k);
		println("=");
		println(product);
		println();
		assertEquals(expected, product);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

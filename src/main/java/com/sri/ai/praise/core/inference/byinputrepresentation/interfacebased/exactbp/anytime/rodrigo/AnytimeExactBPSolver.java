package com.sri.ai.praise.core.inference.byinputrepresentation.interfacebased.exactbp.anytime.rodrigo;

import java.util.Iterator;

import com.sri.ai.praise.core.inference.byinputrepresentation.interfacebased.exactbp.fulltime.core.ExactBP;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.Factor;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.FactorNetwork;
import com.sri.ai.praise.core.representation.interfacebased.factor.api.Variable;
import com.sri.ai.util.base.BinaryFunction;
import com.sri.ai.util.computation.anytime.api.Approximation;

/**
 * An adapter from {@link AnytimeExactBP} to {@link BinaryFunction}.
 * 
 * @author braz
 *
 */
public class AnytimeExactBPSolver implements BinaryFunction<Variable, FactorNetwork, Iterator<Approximation<Factor>>> {

	@Override
	public Iterator<Approximation<Factor>> apply(Variable query, FactorNetwork factorNetwork) {
		return makeAnytimeExactBP(new ExactBP(query, factorNetwork));
	}

	/**
	 * Makes the {@link AnytimeExactBP} from an {@link ExactBP} object.
	 * Override to use extensions of {@link AnytimeExactBP}.
	 */
	protected AnytimeExactBP<Variable, Factor> makeAnytimeExactBP(ExactBP exactBP) {
		return new AnytimeExactBP<>(exactBP);
	}

}
package com.sri.ai.praise.core.representation.interfacebased.factor.core.sampling.api.schedule;

import static com.sri.ai.util.Util.assertType;
import static com.sri.ai.util.Util.pair;

import com.sri.ai.praise.core.representation.interfacebased.factor.core.sampling.api.sample.Sample;
import com.sri.ai.praise.core.representation.interfacebased.factor.core.sampling.core.schedule.SamplingState;
import com.sri.ai.util.base.Pair;
import com.sri.ai.util.planning.api.ContingentGoal;
import com.sri.ai.util.planning.api.State;

/**
 * A {@link SamplingGoal} that is also a {@link ContingentGoal},
 * defining a default method for {@link #isSatisfied(State)}
 * that reduces to {@link SamplingGoal#isSatisfiedBySampleWithoutModifyingIt(Sample)}.
 *  
 * @author braz
 *
 */
public interface ContingentSamplingGoal extends ContingentGoal, SamplingGoal {

	/**
	 * Implements {@link #isSatisfied(State)} by assuming the state is a {@link SampleState}
	 * and invoking {@link SamplingGoal#isSatisfiedBySampleWithoutModifyingIt(Sample)} on it.
	 */
	@Override
	default Pair<Boolean, State> isSatisfied(State state) {
		SamplingState sampleState = assertType(state, SamplingState.class, getClass());
		boolean result = isSatisfiedBySampleWithoutModifyingIt(sampleState.getUnmodifiableSample());
		return pair(result, state); // note that we are relying on the sample not having been modified
	}
	
}
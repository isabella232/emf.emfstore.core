package org.eclipse.emf.emfstore.modelmutator.test;

import org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationTargetSelectorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AddObjectMutationTest.class, AttributeChangeMutationTest.class,
		DeleteObjectMutationTest.class, FeatureMapKeyMutationTest.class,
		MoveObjectMutationTest.class, MutationTargetSelectorTest.class,
		ReferenceChangeMutationTest.class })
public class AllMutationTests {

}

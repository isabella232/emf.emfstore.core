/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Langer - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.modelmutator.mutation;

import static com.google.common.base.Predicates.or;
import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.any;
import static org.eclipse.emf.emfstore.internal.modelmutator.api.ModelMutatorUtil.getAllObjectsCount;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.change.FeatureMapEntry;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;

import com.google.common.base.Predicate;

/**
 * A set of predicates used by mutations for selecting, filtering, checking objects and features.
 *
 * @author Philip Langer
 *
 */
public final class MutationPredicates {

	private MutationPredicates() {
		// hides constructor
	}

	/** Extended meta data source of EAnnotations. */
	public static final String EXTENDED_META_DATA = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData"; //$NON-NLS-1$
	/** Key of a details entry in EAnnotations to denote FeatureMap groups. */
	public static final String KIND = "kind"; //$NON-NLS-1$
	/** Value of a details aentry in EAnnotations to denote Feature Map groups. */
	public static final String GROUP = "group"; //$NON-NLS-1$

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} is a {@link EReference}.
	 */
	public static final Predicate<? super EStructuralFeature> isReference =
		new Predicate<EStructuralFeature>() {
		public boolean apply(EStructuralFeature input) {
			return input != null && input instanceof EReference;
		}
	};

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} is a containment {@link EReference}.
	 */
	public static final Predicate<? super EStructuralFeature> isContainmentReference =
		new Predicate<EStructuralFeature>() {
		public boolean apply(EStructuralFeature input) {
			return isReference.apply(input)
				&& ((EReference) input).isContainment();
		}
	};

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} is a containment {@link EReference} or the opposite of
	 * a containment {@link EReference}.
	 */
	public static final Predicate<? super EStructuralFeature> isContainmentOrOppositeOfContainmentReference =
		new Predicate<EStructuralFeature>() {
		public boolean apply(EStructuralFeature input) {
			return isContainmentReference.apply(input) || isOppositeOfContainmentReference.apply(input);
		}
	};

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} is the opposite of a containment {@link EReference}.
	 */
	public static final Predicate<? super EStructuralFeature> isOppositeOfContainmentReference =
		new Predicate<EStructuralFeature>() {
		public boolean apply(EStructuralFeature input) {
			return input != null
				&& input instanceof EReference
				&& ((EReference) input).getEOpposite() != null
				&& ((EReference) input).getEOpposite().isContainment();
		}
	};

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} is a mutable containment {@link EReference}.
	 */
	public static final Predicate<? super EStructuralFeature> isMutatableContainmentReference =
		new Predicate<EStructuralFeature>() {
		public boolean apply(EStructuralFeature input) {
			return isMutatable.apply(input)
				&& isContainmentReference.apply(input);
		}
	};

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} is mutable.
	 */
	public static final Predicate<? super EStructuralFeature> isMutatable =
		new Predicate<EStructuralFeature>() {
		public boolean apply(EStructuralFeature input) {
			return input != null && input.isChangeable();
		}
	};

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} is multi-valued.
	 */
	public static final Predicate<? super EStructuralFeature> isMultiValued =
		new Predicate<EStructuralFeature>() {
		public boolean apply(EStructuralFeature input) {
			return input != null && input.isMany();
		}
	};

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} is a mutable {@link EAttribute}.
	 */
	public static final Predicate<? super EStructuralFeature> isMutatableAttribute =
		new Predicate<EStructuralFeature>() {
		public boolean apply(EStructuralFeature input) {
			return isMutatable.apply(input) && input instanceof EAttribute;
		}
	};

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} is a mutable {@link EReference}.
	 */
	public static final Predicate<? super EStructuralFeature> isMutatableReference =
		new Predicate<EStructuralFeature>() {
		public boolean apply(EStructuralFeature input) {
			return isMutatable.apply(input) && input instanceof EReference;
		}
	};

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} is of type {@link FeatureMapEntry}.
	 */
	public static final Predicate<? super EStructuralFeature> hasFeatureMapEntryType =
		new Predicate<EStructuralFeature>() {
		public boolean apply(EStructuralFeature input) {
			return input != null
				&& EcorePackage.eINSTANCE.getEFeatureMapEntry().equals(input.getEType());
		}
	};

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} is the feature map attribute of a feature map group.
	 */
	public static Predicate<? super EStructuralFeature> hasGroupFeatureMapEntryType =
		new Predicate<EStructuralFeature>() {
		public boolean apply(EStructuralFeature input) {
			return hasFeatureMapEntryType.apply(input)
				&& input.getEAnnotation(EXTENDED_META_DATA) != null
				&& input.getEAnnotation(EXTENDED_META_DATA).getDetails().get(KIND) != null
				&& input.getEAnnotation(EXTENDED_META_DATA).getDetails().get(KIND).equals(GROUP);
		}
	};

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} may take the given {@code eObject} as value.
	 *
	 * @param eObject The {@link EObject} to check.
	 * @return <code>true</code> if it may take {@code eObject} as value, <code>false</code> otherwise.
	 */
	public static Predicate<? super EStructuralFeature> mayTakeEObjectAsValue(final EObject eObject) {
		return new Predicate<EStructuralFeature>() {
			public boolean apply(EStructuralFeature input) {
				if (input != null && input instanceof EReference) {
					final EReference eReference = (EReference) input;
					return eReference.getEType().isInstance(eObject);
				}
				return false;
			}
		};
	}

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} has a compatible type to the given {@code feature}.
	 *
	 * @param feature The {@link EStructuralFeature} to check.
	 * @return <code>true</code> if its type is compatible to the one of {@code feature}, <code>false</code> otherwise.
	 */
	public static Predicate<? super EStructuralFeature> hasCompatibleType(final EStructuralFeature feature) {
		return new Predicate<EStructuralFeature>() {
			public boolean apply(EStructuralFeature input) {
				return input != null &&
					(input.getEType() == feature.getEType()
					|| isSubTypeOf(input.getEType(), feature.getEType()));
			}
		};
	}

	private static boolean isSubTypeOf(EClassifier eType, EClassifier eType2) {
		if (eType instanceof EClass && eType2 instanceof EClass) {
			final EClass eClass1 = (EClass) eType;
			final EClass eClass2 = (EClass) eType2;
			return eClass2.isSuperTypeOf(eClass1);
		}
		return false;
	}

	/**
	 * Predicate specifying whether an {@link EStructuralFeature} has a compatible type to any of the features' types of
	 * the given {@code eClass}.
	 *
	 * @param eClass The {@link EClass} to check.
	 * @return <code>true</code> if there is at least one feature in {@code eClass} having a compatible type,
	 *         <code>false</code> otherwise.
	 */
	public static Predicate<? super EStructuralFeature> isCompatibleWithAnyFeatureOfEClass(final EClass eClass) {
		return new Predicate<EStructuralFeature>() {
			public boolean apply(EStructuralFeature input) {
				return input != null &&
					any(eClass.getEStructuralFeatures(), hasCompatibleType(input));
			}
		};
	}

	/**
	 * Predicate specifying whether an {@link EObject} may be contained by the given {@code feature}.
	 *
	 * @param feature The {@link EStructuralFeature} to check.
	 * @return <code>true</code> if it may be contained by {@code feature}, <code>false</code> otherwise.
	 */
	public static Predicate<? super EObject> mayBeContainedByFeature(final EStructuralFeature feature) {
		return new Predicate<EObject>() {
			public boolean apply(EObject input) {
				return mayTakeEObjectAsValue(input).apply(feature);
			}
		};
	}

	/**
	 * Predicate specifying whether an {@link EObject} may be contained by any of the given {@code references}.
	 *
	 * @param references The references to check.
	 * @return <code>true</code> if there is at least one reference in {@code references}, which may contain it,
	 *         <code>false</code> otherwise.
	 */
	public static Predicate<? super EObject> mayBeContainedByAnyOfTheseReferences(
		final Iterable<EReference> references) {
		return new Predicate<EObject>() {
			public boolean apply(EObject input) {
				return input != null && any(references, mayTakeEObjectAsValue(input));
			}
		};
	}

	/**
	 * Predicate specifying whether an {@link EObject} is not the same as the given {@code eObject}.
	 *
	 * @param eObject The {@link EObject} to compare it to.
	 * @return <code>true</code> if it is not the same, <code>false</code> otherwise.
	 */
	public static Predicate<? super EObject> isNotTheSame(final EObject eObject) {
		return new Predicate<EObject>() {
			public boolean apply(EObject input) {
				return input != null && input != eObject;
			}
		};
	}

	/**
	 * Predicate specifying whether an {@link EObject} is the child of the given {@code eObject}.
	 *
	 * @param eObject The {@link EObject} to check.
	 * @return <code>true</code> if it is a child of {@code eObject}, <code>false</code> otherwise.
	 */
	public static Predicate<? super EObject> isChild(final EObject eObject) {
		return new Predicate<EObject>() {
			public boolean apply(EObject input) {
				return input != null && EcoreUtil.isAncestor(eObject, input);
			}
		};
	}

	/**
	 * Predicate specifying whether an {@link EObject} is the ancestor of a given {@code eObject}.
	 *
	 * @param eObject The {@link EObject} to check.
	 * @return <code>true</code> if it is the ancestor of {@code eObject}, <code>false</code> otherwise.
	 */
	public static Predicate<? super EObject> isAncestor(final EObject eObject) {
		return new Predicate<EObject>() {
			public boolean apply(EObject input) {
				return input != null && EcoreUtil.isAncestor(input, eObject);
			}
		};
	}

	/**
	 * Predicate specifying whether an {@link EObject} is contained by the given {@code feature}.
	 *
	 * @param feature The {@link EStructuralFeature} to check.
	 * @return <code>true</code> if it is contained by {@code feature}, <code>false</code> otherwise.
	 */
	public static Predicate<? super EObject> isContainedByFeature(final EStructuralFeature feature) {
		return new Predicate<EObject>() {
			public boolean apply(EObject input) {
				return input != null && input.eContainingFeature() == feature;
			}
		};
	}

	/**
	 * Predicate specifying whether an {@link EObject} is contained by the given {@code eContainer}.
	 *
	 * @param eContainer The {@link EObject} to check.
	 * @return <code>true</code> if it is contained by {@code eContainer}, <code>false</code> otherwise.
	 */
	public static Predicate<? super EObject> isContainedByEObject(final EObject eContainer) {
		return new Predicate<EObject>() {
			public boolean apply(EObject input) {
				return input != null && input.eContainer() == eContainer;
			}
		};
	}

	/**
	 * Predicate specifying whether an {@link Object} is not <code>null</code> or an empty list.
	 */
	public static final Predicate<? super Object> isNonEmptyValueOrList =
		new Predicate<Object>() {
		public boolean apply(Object input) {
			return !(input == null || isEmptyList(input));
		}
	};

	private static boolean isEmptyList(Object input) {
		if (input instanceof List<?>) {
			final List<?> list = (List<?>) input;
			return list.isEmpty();
		}
		return false;
	}

	/**
	 * Predicate specifying whether an {@link Object} is a feature map containing at least one value.
	 */
	public static final Predicate<? super Object> isNonEmptyFeatureMap =
		new Predicate<Object>() {
		public boolean apply(Object input) {
			return input != null && input instanceof FeatureMap && ((FeatureMap) input).size() > 0;
		}
	};

	/**
	 * Predicate specifying whether an {@link Object} is an {@link EObject} or a list of {@link EObject EObjects}
	 * containing at least one EObject.
	 */
	public static final Predicate<? super Object> isNonEmptyEObjectValueOrList =
		new Predicate<Object>() {
		public boolean apply(Object input) {
			return isNonNullEObject.apply(input) || isNonEmptyEObjectList.apply(input);
		}
	};

	/**
	 * Predicate specifying whether an {@link Object} is not null and an {@link EObject}.
	 */
	public static final Predicate<? super Object> isNonNullEObject =
		new Predicate<Object>() {
		public boolean apply(Object input) {
			return input != null && input instanceof EObject;
		}
	};

	/**
	 * Predicate specifying whether an {@link Object} is a list of {@link EObject EObjects} containing at least one
	 * EObject.
	 */
	public static final Predicate<? super Object> isNonEmptyEObjectList =
		new Predicate<Object>() {
		public boolean apply(Object input) {
			return input instanceof List<?> && isNonEmptyEObjectList((List<?>) input);
		}
	};

	private static boolean isNonEmptyEObjectList(List<?> input) {
		return !input.isEmpty() && all(input, isNonNullEObject);
	}

	/**
	 * Predicate specifying whether an {@link Object} is <code>null</code> or a list.
	 */
	public static final Predicate<? super Object> isNullValueOrList =
		new Predicate<Object>() {
		public boolean apply(Object input) {
			return input == null || isList.apply(input);
		}
	};

	/**
	 * Predicate specifying whether an {@link Object} is a list.
	 */
	public static final Predicate<? super Object> isList =
		new Predicate<Object>() {
		public boolean apply(Object input) {
			return input instanceof List<?>;
		}
	};

	/**
	 * Predicate specifying whether an {@link Object} is the container of an {@link EObject} that itself has at most the
	 * given number of {@code maxNumberOfContainments}.
	 *
	 * @param maxNumberOfContainments The maximum number of containments to check.
	 * @return <code>true</code> if it contains an {@link EObject} that itself has a less or equal number of
	 *         containments than specified in {@code maxNumberOfContainments}, <code>false</code> otherwise.
	 */
	public static Predicate<? super Object> containsEObjectWithMaxNumberOfContainments(final int maxNumberOfContainments) {
		return new Predicate<Object>() {
			public boolean apply(Object input) {
				return input != null
					&& or(isListContainingEObjectWithMaxNumberOfContainments(maxNumberOfContainments),
						isEObjectWithMaxNumberOfContainments(maxNumberOfContainments)).apply(input);
			}
		};
	}

	/**
	 * Predicate specifying whether an {@link Object} is a list of {@link EObject EObjects} containing one EObject
	 * that itself has at most the given number of {@code maxNumberOfContainments}.
	 *
	 * @param maxNumberOfContainments The maximum number of containments to check.
	 * @return <code>true</code> if it is a list containing an {@link EObject} that itself has a less or equal number of
	 *         containments than specified in {@code maxNumberOfContainments}, <code>false</code> otherwise.
	 */
	public static Predicate<? super Object> isListContainingEObjectWithMaxNumberOfContainments(
		final int maxNumberOfContainments) {
		return new Predicate<Object>() {
			@SuppressWarnings("unchecked")
			public boolean apply(Object input) {
				return input instanceof List<?>
				&& any((List<EObject>) input, hasMaxNumberOfContainments(maxNumberOfContainments));
			}
		};
	}

	/**
	 * Predicate specifying whether an {@link Object} is an {@link EObject} containing at most the given number of
	 * {@code maxNumberOfContainments}.
	 *
	 * @param maxNumberOfContainments The maximum number of containments to check.
	 * @return <code>true</code> if it is is an {@link EObject} containing a less or equal number of
	 *         containments than specified in {@code maxNumberOfContainments}, <code>false</code> otherwise.
	 */
	public static Predicate<? super Object> isEObjectWithMaxNumberOfContainments(final int maxNumberOfContainments) {
		return new Predicate<Object>() {
			public boolean apply(Object input) {
				return input instanceof EObject
					&& hasMaxNumberOfContainments(maxNumberOfContainments).apply((EObject) input);
			}
		};
	}

	/**
	 * Predicate specifying whether an {@link EObject} contains at most the given number of
	 * {@code maxNumberOfContainments}.
	 *
	 * @param maxNumberOfContainments The maximum number of containments to check.
	 * @return <code>true</code> if it contains a less or equal number of containments than specified in
	 *         {@code maxNumberOfContainments}, <code>false</code> otherwise.
	 */
	public static Predicate<? super EObject> hasMaxNumberOfContainments(final int maxNumberOfContainments) {
		return new Predicate<EObject>() {
			public boolean apply(EObject input) {
				return getAllObjectsCount(input) <= maxNumberOfContainments;
			}
		};
	}

	/**
	 * Predicate specifying whether an {@link Object} is a list with the given {@code size}.
	 *
	 * @param size The size to check.
	 * @return <code>true</code> if it is a list of the given {@code size}, <code>false</code> otherwise.
	 */
	public static Predicate<? super Object> isListWithSpecifiedSize(final int size) {
		return new Predicate<Object>() {
			public boolean apply(Object originalValue) {
				if (originalValue instanceof List<?>) {
					final List<?> originalValueList = (List<?>) originalValue;
					return originalValueList.size() == size;
				}
				return false;
			}
		};
	}

}

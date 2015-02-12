/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.modelmutator.mutation;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Utility class for detecting any cycles within an {@link EObject}.
 *
 * @author emueller
 *
 */
public final class EMFCycleDetector {

	private EMFCycleDetector() {
		// private ctor
	}

	/**
	 * Checks whether the given {@link EObject} would contain a
	 * cycle if a edge, that is, a reference (containment or non-containment),
	 * from <code>source</code> to <code>target</code> is inserted.
	 *
	 * @param root
	 *            the root {@link EObject} forming the graph
	 * @param source
	 *            the source of the edge
	 * @param target
	 *            the target of the edge
	 * @return {@code true} if {@code root} would contain a cycle, {@code false} otherwise
	 */
	public static boolean wouldBeCycle(EObject root, EObject source, EObject target) {
		final DirectedGraph<EObject, DefaultEdge> graph = makeGraph(root);
		assertVertexIsKnown(graph, source);
		assertVertexIsKnown(graph, target);
		graph.addEdge(source, target);
		return detectCycle(graph);
	}

	/**
	 * Detects cycles within the {@link EObject} graph of the given object.
	 *
	 * @param eObject
	 *            the {@link EObject} which should be checked for cycles
	 * @return {@code true} if the given object contains a cycle, {@code false} otherwise
	 */
	public static boolean detect(EObject eObject) {
		final DirectedGraph<EObject, DefaultEdge> graph = makeGraph(eObject);
		return detectCycle(graph);
	}

	private static boolean detectCycle(final DirectedGraph<EObject, DefaultEdge> graph) {
		return new CycleDetector<EObject, DefaultEdge>(graph).detectCycles();
	}

	private static DirectedGraph<EObject, DefaultEdge> makeGraph(EObject eObject) {
		final DirectedGraph<EObject, DefaultEdge> graph =
			new DefaultDirectedGraph<EObject, DefaultEdge>(DefaultEdge.class);

		final TreeIterator<EObject> eAllContents = eObject.eAllContents();
		while (eAllContents.hasNext()) {
			final EObject vertex = eAllContents.next();
			graph.addVertex(vertex);
			final EClass eClass = vertex.eClass();
			final EList<EReference> eAllReferences = eClass.getEAllReferences();
			for (final EReference eReference : eAllReferences) {
				final Object target = vertex.eGet(eReference);

				if (List.class.isInstance(target)) {
					final List<?> targetObjects = List.class.cast(target);
					for (final Object o : targetObjects) {
						assertVertexIsKnown(graph, o);
						if (EObject.class.isInstance(o)) {
							graph.addEdge(vertex, (EObject) o);
						}
					}
				} else {
					if (EObject.class.isInstance(target)) {
						assertVertexIsKnown(graph, target);
						graph.addEdge(vertex, (EObject) target);
					}
				}
			}
		}
		return graph;
	}

	private static void assertVertexIsKnown(final DirectedGraph<EObject, DefaultEdge> graph, final Object target) {
		if (!graph.vertexSet().contains(target)) {
			graph.addVertex((EObject) target);
		}
	}

}

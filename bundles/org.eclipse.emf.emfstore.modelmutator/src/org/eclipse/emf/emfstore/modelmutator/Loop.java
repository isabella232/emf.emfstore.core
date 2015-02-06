package org.eclipse.emf.emfstore.modelmutator;

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

public class Loop {

	public static boolean wouldBeCycle(EObject root, EObject source, EObject target) {
		final DirectedGraph<EObject, DefaultEdge> graph = makeGraph(root);
		assertVertexIsKnown(graph, source);
		assertVertexIsKnown(graph, target);
		graph.addEdge(source, target);
		return detectCycle(graph);
	}

	public static boolean detect(EObject eObject) {
		final DirectedGraph<EObject, DefaultEdge> graph = makeGraph(eObject);
		return detectCycle(graph);
	}

	/**
	 * @param graph
	 * @return
	 */
	private static boolean detectCycle(final DirectedGraph<EObject, DefaultEdge> graph) {
		return new CycleDetector<EObject, DefaultEdge>(graph).detectCycles();
	}

	/**
	 * @param eObject
	 * @return
	 */
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

	/**
	 * @param graph
	 * @param target
	 */
	private static void assertVertexIsKnown(final DirectedGraph<EObject, DefaultEdge> graph, final Object target) {
		if (!graph.vertexSet().contains(target)) {
			graph.addVertex((EObject) target);
		}
	}

}

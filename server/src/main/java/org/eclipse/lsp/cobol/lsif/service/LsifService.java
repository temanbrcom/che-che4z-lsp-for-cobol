/*
 * Copyright (c) 2021 Broadcom.
 * The term "Broadcom" refers to Broadcom Inc. and/or its subsidiaries.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Broadcom, Inc. - initial API and implementation
 *
 */

package org.eclipse.lsp.cobol.lsif.service;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.eclipse.lsp.cobol.core.model.variables.Variable;
import org.eclipse.lsp.cobol.lsif.model.Node;
import org.eclipse.lsp.cobol.lsif.model.edges.*;
import org.eclipse.lsp.cobol.lsif.model.vertices.*;
import org.eclipse.lsp.cobol.service.CobolDocumentModel;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolKind;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** asdfsaf */
public class LsifService {

  /**
   * asdfsfd
   *
   * @param uri
   * @param model
   */
  public void dumpGraph(String uri, CobolDocumentModel model) {
    String dump =
        createGraph(uri, model).stream().map(this::dumpNode).collect(Collectors.joining("\n"));
    System.out.println(dump);
    try {
      Files.write(Paths.get(URI.create(uri + ".lsif")), dump.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private List<Node> createGraph(String uri, CobolDocumentModel model) {
    List<Node> graph = new ArrayList<>(createStaticNodes(uri));
    Project project = new Project("cobolProject", null);
    Document document = new Document(uri, "COBOL", model.getText());
    graph.add(project);
    graph.add(project.beginEvent());
    graph.add(document);
    graph.add(document.beginEvent());
    graph.add(new Contains(ImmutableList.of(document.getId()), project.getId()));
    graph.addAll(createVariableGraphs(document, model));
    graph.add(document.endEvent());
    graph.add(project.endEvent());
    return graph;
  }

  private List<Node> createVariableGraphs(Node document, CobolDocumentModel model) {
    return model.getAnalysisResult().getVariables().stream()
        .map(it -> createVariableGraph(document, it))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private List<Node> createVariableGraph(Node document, Variable variable) {
    List<Node> graph = new ArrayList<>();
    Node vertexRange = variableDefinitionToRange(variable);
    graph.add(vertexRange);
    graph.add(new Contains(ImmutableList.of(document.getId()), vertexRange.getId()));
    Node resultSet = new Result(Result.Type.RESULT_SET);
    graph.add(resultSet);
    graph.add(new Next(resultSet.getId(), vertexRange.getId()));
    Node definitionResult = new Result(Result.Type.DEFINITION);
    graph.add(definitionResult);
    graph.add(
        new Request(Request.Type.DEFINITION, definitionResult.getId(), resultSet.getId(), null));
    graph.add(
        new Item(
            null,
            definitionResult.getId(),
            ImmutableList.of(vertexRange.getId()),
            document.getId(),
            null));

    Node hoverResult =
        new HoverResult(
            ImmutableList.of(new HoverResult.Content(variable.getFormattedDisplayLine())));
    graph.add(hoverResult);
    graph.add(new Request(Request.Type.HOVER, hoverResult.getId(), resultSet.getId(), null));
    Node moniker = new Moniker(variable.getName());
    graph.add(moniker);
    graph.add(new MonikerEdge(moniker.getId(), resultSet.getId()));
    return graph;
  }

  private VertexRange variableDefinitionToRange(Variable variable) {
    Range range = variable.getDefinition().getRange();
    return new VertexRange(
        range.getStart(),
        range.getEnd(),
        new VertexRange.Tag(
            VertexRange.Type.DEFINITION, variable.getName(), SymbolKind.Variable, range));
  }

  private List<Node> createStaticNodes(String uri) {
    List<Node> graph = new ArrayList<>();
    graph.add(new MetaData());
    graph.add(new Source(getRootURI(uri)));
    graph.add(new Capabilities());
    return graph;
  }

  private static String getRootURI(String uri) {
    int index = uri.lastIndexOf('/');
    return uri.substring(0, index);
  }

  private String dumpNode(Node node) {
    return new Gson().toJson(node);
  }
}

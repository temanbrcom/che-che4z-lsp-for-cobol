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
import org.eclipse.lsp.cobol.core.model.Locality;
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
    Node definitionRange = variableDefinitionToRange(variable);
    graph.add(definitionRange);
    graph.add(new Contains(ImmutableList.of(document.getId()), definitionRange.getId()));
    List<Node> referenceRanges = variableUsagesToRanges(variable);
    graph.addAll(referenceRanges);
    referenceRanges.stream()
        .map(it -> new Contains(ImmutableList.of(it.getId()), document.getId()))
        .forEach(graph::add);
    Node resultSet = new Result(Result.Type.RESULT_SET);
    graph.add(resultSet);
    graph.add(new Next(resultSet.getId(), definitionRange.getId()));
    referenceRanges.stream().map(it -> new Next(resultSet.getId(), it.getId())).forEach(graph::add);
    Node definitionResult = new Result(Result.Type.DEFINITION);
    graph.add(definitionResult);
    graph.add(
        new Request(Request.Type.DEFINITION, definitionResult.getId(), resultSet.getId(), null));
    graph.add(
        new Item(
            null,
            definitionResult.getId(),
            ImmutableList.of(definitionRange.getId()),
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
    Node referencesResult = new Result(Result.Type.REFERENCES);
    graph.add(referencesResult);
    graph.add(
        new Request(Request.Type.REFERENCES, referencesResult.getId(), resultSet.getId(), null));
    graph.add(
        new Item(
            null,
            referencesResult.getId(),
            ImmutableList.of(definitionRange.getId()),
            document.getId(),
            "definitions"));
    referenceRanges.stream()
        .map(
            it ->
                new Item(
                    null,
                    referencesResult.getId(),
                    ImmutableList.of(it.getId()),
                    document.getId(),
                    "references"))
        .forEach(graph::add);
    return graph;
  }

  private List<Node> variableUsagesToRanges(Variable variable) {
    return variable.getUsages().stream()
        .map(Locality::getRange)
        .map(
            it ->
                new VertexRange(
                    it.getStart(),
                    it.getEnd(),
                    new VertexRange.Tag(
                        VertexRange.Type.REFERENCE, variable.getName(), null, null)))
        .collect(Collectors.toList());
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

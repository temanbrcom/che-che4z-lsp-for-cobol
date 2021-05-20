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
import com.google.inject.Inject;
import org.eclipse.lsp.cobol.core.model.CopybookModel;
import org.eclipse.lsp.cobol.core.model.Locality;
import org.eclipse.lsp.cobol.lsif.model.Node;
import org.eclipse.lsp.cobol.lsif.model.edges.*;
import org.eclipse.lsp.cobol.lsif.model.vertices.*;
import org.eclipse.lsp.cobol.service.CobolDocumentModel;
import org.eclipse.lsp.cobol.service.CopybookProcessingMode;
import org.eclipse.lsp.cobol.service.CopybookService;
import org.eclipse.lsp.cobol.service.delegates.validations.AnalysisResult;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolKind;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;

/** asdfsaf */
public class FileWritingLsifService implements LsifService {
  private final CopybookService service;

  @Inject
  public FileWritingLsifService(CopybookService service) {
    this.service = service;
  }

  @Override
  public void dumpGraph(String uri, CobolDocumentModel model) {
    String dump = createGraph(uri, model).stream().map(this::dumpNode).collect(joining("\n"));
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
    graph.addAll(createGraphsForSemantics(uri, model, document, project));
    graph.add(document.endEvent());
    graph.add(project.endEvent());
    return graph;
  }

  private List<Node> createGraphsForSemantics(
      String uri, CobolDocumentModel model, Node document, Project project) {
    AnalysisResult analysisResult = model.getAnalysisResult();
    List<CopybookModel> copybooks =
        retrieveCopybooks(analysisResult.getCopybookUsages().keySet(), uri);
    Map<String, Node> copybookNodes = convertCopybooksToNodes(copybooks);
    Map<String, Node> documents = new HashMap<>(copybookNodes);
    documents.put(uri, document);
    List<Node> graph = new ArrayList<>(copybookNodes.values());
    graph.addAll(createDocumentConnections(copybookNodes.values(), project));
    List<Document> subroutineDocuments = createSubroutineDocuments(analysisResult);
    graph.addAll(subroutineDocuments);
    subroutineDocuments.forEach(it -> documents.put(it.getUri(), it));
    graph.addAll(createDocumentConnections(subroutineDocuments, project));
    graph.addAll(createSubroutineGraph(documents, analysisResult));
    graph.addAll(createCopybookGraph(documents, analysisResult));
    graph.addAll(createVariableGraphs(documents, analysisResult));
    graph.addAll(createParagraphGraphs(documents, analysisResult));
    graph.addAll(createSectionGraphs(documents, analysisResult));
    return graph;
  }

  private List<Document> createSubroutineDocuments(AnalysisResult analysisResult) {
    return analysisResult.getSubroutineDefinitions().values().stream()
        .map(it -> it.get(0))
        .map(locations -> new Document(locations.getUri(), "COBOL", ""))
        .collect(toList());
  }

  private Map<String, Node> convertCopybooksToNodes(List<CopybookModel> copybooks) {
    return copybooks.stream()
        .map(it -> new Document(it.getUri(), "COBOL Copybook", it.getContent()))
        .collect(toMap(Document::getUri, Function.identity()));
  }

  private List<Node> createDocumentConnections(
      Collection<? extends Node> documents, Project project) {
    return documents.stream()
        .map(it -> new Contains(ImmutableList.of(it.getId()), project.getId()))
        .collect(toList());
  }

  private List<CopybookModel> retrieveCopybooks(Set<String> copybooks, String uri) {
    return copybooks.stream()
        .map(it -> service.resolve(it, uri, CopybookProcessingMode.ENABLED))
        .collect(toList());
  }

  private List<Node> createVariableGraphs(Map<String, Node> documents, AnalysisResult result) {
    return result.getVariables().stream()
        .map(
            it ->
                createSubGraph(
                    documents,
                    it.getName(),
                    SymbolKind.Variable,
                    it.getFormattedDisplayLine(),
                    it.getDefinition().toLocation(),
                    it.getUsages().stream().map(Locality::toLocation).collect(toList())))
        .flatMap(Collection::stream)
        .collect(toList());
  }

  private List<Node> createParagraphGraphs(Map<String, Node> documents, AnalysisResult result) {
    return result.getParagraphDefinitions().entrySet().stream()
        .map(
            it ->
                createSubGraph(
                    documents,
                    it.getKey(),
                    SymbolKind.Method,
                    it.getKey(),
                    it.getValue().get(0),
                    ofNullable(result.getParagraphUsages())
                        .map(usages -> usages.get(it.getKey()))
                        .orElse(ImmutableList.of())))
        .flatMap(Collection::stream)
        .collect(toList());
  }

  private List<Node> createSectionGraphs(Map<String, Node> documents, AnalysisResult result) {
    return result.getSectionDefinitions().entrySet().stream()
        .map(
            it ->
                createSubGraph(
                    documents,
                    it.getKey(),
                    SymbolKind.Function,
                    it.getKey(),
                    it.getValue().get(0),
                    ofNullable(result.getSectionUsages())
                        .map(usages -> usages.get(it.getKey()))
                        .orElse(ImmutableList.of())))
        .flatMap(Collection::stream)
        .collect(toList());
  }

  private List<Node> createCopybookGraph(Map<String, Node> documents, AnalysisResult result) {
    return result.getCopybookDefinitions().entrySet().stream()
        .map(
            it ->
                createSubGraph(
                    documents,
                    it.getKey(),
                    SymbolKind.Class,
                    it.getKey(),
                    it.getValue().get(0),
                    ofNullable(result.getCopybookUsages())
                        .map(usages -> usages.get(it.getKey()))
                        .orElse(ImmutableList.of())))
        .flatMap(Collection::stream)
        .collect(toList());
  }

  private List<Node> createSubroutineGraph(Map<String, Node> documents, AnalysisResult result) {
    return result.getSubroutineDefinitions().entrySet().stream()
        .map(
            it ->
                createSubGraph(
                    documents,
                    it.getKey(),
                    SymbolKind.File,
                    it.getKey(),
                    it.getValue().get(0),
                    ofNullable(result.getSubroutineUsages())
                        .map(usages -> usages.get(it.getKey()))
                        .orElse(ImmutableList.of())))
        .flatMap(Collection::stream)
        .collect(toList());
  }

  private List<Node> createSubGraph(
      Map<String, Node> documents,
      String name,
      SymbolKind kind,
      String hover,
      Location definition,
      List<Location> usages) {
    List<Node> graph = new ArrayList<>();
    Node definitionRange = convertDefinitionToRange(name, definition, kind);
    int definitionDocId = documents.get(definition.getUri()).getId();
    graph.add(definitionRange);
    graph.add(new Contains(ImmutableList.of(definitionRange.getId()), definitionDocId));
    Map<Node, String> referenceRanges = convertUsagesToRanges(name, usages, kind);
    graph.addAll(referenceRanges.keySet());
    graph.addAll(createContainsEdges(documents, referenceRanges));
    Node resultSet = new Result(Result.Type.RESULT_SET);
    graph.add(resultSet);
    graph.add(new Next(resultSet.getId(), definitionRange.getId()));
    graph.addAll(createNextEdges(referenceRanges, resultSet));
    graph.addAll(createDefinitionRequest(definitionRange, definitionDocId, resultSet));
    graph.addAll(
        createReferenceResult(
            documents, definitionRange, definitionDocId, referenceRanges, resultSet));
    graph.addAll(createHover(hover, resultSet));
    graph.addAll(createMoniker(name, resultSet));
    return graph;
  }

  private List<Node> createDefinitionRequest(
      Node definitionRange, int definitionDocId, Node resultSet) {
    List<Node> graph = new ArrayList<>();
    Node definitionResult = new Result(Result.Type.DEFINITION);
    graph.add(definitionResult);
    graph.add(
        new Request(Request.Type.DEFINITION, definitionResult.getId(), resultSet.getId(), null));
    graph.add(createItemNode(definitionRange, definitionDocId, definitionResult, null));
    return graph;
  }

  private List<Node> createReferenceResult(
      Map<String, Node> documents,
      Node definitionRange,
      int definitionDocId,
      Map<Node, String> referenceRanges,
      Node resultSet) {
    List<Node> graph = new ArrayList<>();
    Node referencesResult = new Result(Result.Type.REFERENCES);
    graph.add(referencesResult);
    graph.add(
        new Request(Request.Type.REFERENCES, referencesResult.getId(), resultSet.getId(), null));
    graph.add(createItemNode(definitionRange, definitionDocId, referencesResult, "definition"));
    graph.addAll(createReferenceItemEdges(documents, referenceRanges, referencesResult));
    return graph;
  }

  private List<Node> createHover(String hover, Node resultSet) {
    List<Node> graph = new ArrayList<>();
    Node hoverResult = new HoverResult(ImmutableList.of(new HoverResult.Content(hover)));
    graph.add(hoverResult);
    graph.add(new Request(Request.Type.HOVER, hoverResult.getId(), resultSet.getId(), null));
    return graph;
  }

  private List<Node> createMoniker(String name, Node resultSet) {
    List<Node> graph = new ArrayList<>();
    Node moniker = new Moniker(name);
    graph.add(moniker);
    graph.add(new MonikerEdge(moniker.getId(), resultSet.getId()));
    return graph;
  }

  private List<Next> createNextEdges(Map<Node, String> referenceRanges, Node resultSet) {
    return referenceRanges.keySet().stream()
        .map(it -> new Next(resultSet.getId(), it.getId()))
        .collect(toList());
  }

  private List<Contains> createContainsEdges(
      Map<String, Node> documents, Map<Node, String> ranges) {
    return ranges.entrySet().stream()
        .map(
            it ->
                new Contains(
                    ImmutableList.of(it.getKey().getId()), documents.get(it.getValue()).getId()))
        .collect(toList());
  }

  private Item createItemNode(
      Node definitionRange, int definitionDocId, Node referencesResult, String property) {
    return new Item(
        null,
        referencesResult.getId(),
        ImmutableList.of(definitionRange.getId()),
        definitionDocId,
        property);
  }

  private List<Node> createReferenceItemEdges(
      Map<String, Node> documents, Map<Node, String> referenceRanges, Node referencesResult) {
    return referenceRanges.entrySet().stream()
        .map(
            it ->
                createItemNode(
                    it.getKey(),
                    documents.get(it.getValue()).getId(),
                    referencesResult,
                    "references"))
        .collect(toList());
  }

  private VertexRange convertDefinitionToRange(String name, Location location, SymbolKind kind) {
    Range range = location.getRange();
    return new VertexRange(
        range.getStart(),
        range.getEnd(),
        new VertexRange.Tag(VertexRange.Type.DEFINITION, name, kind, range));
  }

  private Map<Node, String> convertUsagesToRanges(
      String name, List<Location> usages, SymbolKind kind) {
    return usages.stream().collect(toMap(locationToRange(name, kind), Location::getUri));
  }

  private Function<Location, Node> locationToRange(String name, SymbolKind kind) {
    return it -> {
      Range range = it.getRange();
      return new VertexRange(
          range.getStart(),
          range.getEnd(),
          new VertexRange.Tag(VertexRange.Type.REFERENCE, name, kind, range));
    };
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

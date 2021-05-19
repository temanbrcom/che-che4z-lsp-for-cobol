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
import org.eclipse.lsp.cobol.lsif.model.*;
import org.eclipse.lsp.cobol.service.CobolDocumentModel;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    try {
      Files.write(Paths.get(URI.create(uri + ".lsif")), dump.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private List<Node> createGraph(String uri, CobolDocumentModel model) {
    List<Node> graph = new ArrayList<>();
    graph.add(new MetaData());
    graph.add(new Source(getRootURI(uri)));
    graph.add(new Capabilities());
    Project project = new Project("cobolProject", null);
    Document document = new Document(uri, "COBOL", model.getText());
    graph.add(project);
    graph.add(project.beginEvent());
    graph.add(document);
    graph.add(document.beginEvent());
    graph.add(new Contains(ImmutableList.of(document.getId()), project.getId()));
    graph.add(document.endEvent());
    graph.add(project.endEvent());
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

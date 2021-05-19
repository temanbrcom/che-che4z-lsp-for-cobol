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

package org.eclipse.lsp.cobol.lsif.model.edges;

import org.eclipse.lsp.cobol.lsif.model.vertices.Vertex;

import java.util.List;

/** asdf */
public class HoverResult extends Vertex {
  final Result result;

  public HoverResult(List<Content> contents) {
    super("hoverResult");
    this.result = new Result(contents);
  }

  public static class Result {
    final List<Content> contents;

    public Result(List<Content> contents) {
      this.contents = contents;
    }
  }

  public static class Content {
    final String language = "COBOL";
    final String value;

    public Content(String value) {
      this.value = value;
    }
  }
}

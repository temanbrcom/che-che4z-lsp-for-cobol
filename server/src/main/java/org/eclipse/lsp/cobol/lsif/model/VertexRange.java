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

package org.eclipse.lsp.cobol.lsif.model;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/** asdfasr */
public class VertexRange extends Vertex {
  final Position start;
  final Position end;
  final Tag tag;

  public VertexRange(Position start, Position end, Tag tag) {
    super("range");
    this.start = start;
    this.end = end;
    this.tag = tag;
  }

  /** asdfasr */
  static class Tag {
    final String type;
    final String text;
    final Integer kind;
    final Range fullRange;

    Tag(String type, String text, Integer kind, Range fullRange) {
      this.type = type;
      this.text = text;
      this.kind = kind;
      this.fullRange = fullRange;
    }
  }
}

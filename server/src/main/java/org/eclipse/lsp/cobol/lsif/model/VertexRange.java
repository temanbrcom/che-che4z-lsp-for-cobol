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
import org.eclipse.lsp4j.SymbolKind;

import java.util.Optional;

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
  public static class Tag {
    final String type;
    final String text;
    final Integer kind;
    final Range fullRange;

    public Tag(Type type, String text, SymbolKind kind, Range fullRange) {
      this.type = type.desc;
      this.text = text;
      this.kind = Optional.ofNullable(kind).map(SymbolKind::getValue).orElse(null);
      this.fullRange = fullRange;
    }
  }

  /** asdfasr */
  public enum Type {
    REFERENCE("reference"),
    DEFINITION("definition");
    final String desc;

    Type(String desc) {
      this.desc = desc;
    }
  }
}

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

package org.eclipse.lsp.cobol.lsif.model.vertices;

import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
/** asdfsaf */
public class Document extends Vertex {
  public static final String LABEL = "document";
  @Getter final String uri;
  final String languageId;
  final String contents;

  public Document(String uri, String languageId, String contents) {
    super(LABEL);
    this.contents = Base64.getEncoder().encodeToString(contents.getBytes(StandardCharsets.UTF_8));
    this.languageId = languageId;
    this.uri = uri;
  }

  public Event beginEvent() {
    return Event.begin(LABEL, id);
  }

  public Event endEvent() {
    return Event.end(LABEL, id);
  }
}

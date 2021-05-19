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

// {"id":7,"type":"vertex","label":"project","kind":"typescript","name":"testProject",
// "resource":"file:///home/anton/projects/lsif/testProject/tsconfig.json"}

/** asdfsaf */
public class Project extends Vertex {
  final String kind = "COBOL";
  final String name;
  final String resource;

  public Project(String name, String resource) {
    super("project");
    this.name = name;
    this.resource = resource;
  }

  public Event beginEvent() {
    return Event.begin("project", id);
  }

  public Event endEvent() {
    return Event.end("project", id);
  }
}

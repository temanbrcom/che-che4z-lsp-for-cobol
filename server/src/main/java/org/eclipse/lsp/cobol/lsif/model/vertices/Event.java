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
/** awer */
public class Event extends Vertex {
  String scope;
  String kind;
  int data;

  public Event(String scope, String kind, int data) {
    super("$event");
    this.scope = scope;
    this.kind = kind;
    this.data = data;
  }

  public static Event begin(String scope, int data) {
    return new Event(scope, "begin", data);
  }

  public static Event end(String scope, int data) {
    return new Event(scope, "end", data);
  }
}

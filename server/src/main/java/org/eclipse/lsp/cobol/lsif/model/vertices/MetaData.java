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

// {"id":1,"type":"vertex","label":"metaData","version":"0.6.0-next.2","positionEncoding":"utf-16"}

/** asdfsaf */
public class MetaData extends Vertex {
  final String version = "0.6.0-next.2";
  final String positionEncoding = "utf-16";

  public MetaData() {
    super("metaData");
  }
}

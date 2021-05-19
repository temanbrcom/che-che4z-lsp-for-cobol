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

import java.util.List;
/** asdfsaf */
public abstract class Edge extends Node {
  Integer inV;
  int outV;
  List<Integer> inVs;

  protected Edge(String label, Integer inV, int outV, List<Integer> inVs) {
    super("edge", label);
    this.inV = inV;
    this.outV = outV;
    this.inVs = inVs;
  }
}

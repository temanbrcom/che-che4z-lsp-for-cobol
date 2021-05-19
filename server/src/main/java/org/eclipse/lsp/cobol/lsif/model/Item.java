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

/** asdasd */
public class Item extends Edge {
  final String shard;
  final String property;

  public Item(Integer inV, int outV, List<Integer> inVs, String shard, String property) {
    super("item", inV, outV, inVs);
    this.shard = shard;
    this.property = property;
  }
}

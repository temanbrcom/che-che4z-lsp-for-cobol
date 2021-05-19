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

// {"id":46,"type":"edge","label":"contains","outV":7,"inVs":[9]}
/** asdfsaf */
public class Contains extends Edge {
  public Contains(List<Integer> inVs, int outV) {
    super("contains", null, outV, inVs);
  }

}

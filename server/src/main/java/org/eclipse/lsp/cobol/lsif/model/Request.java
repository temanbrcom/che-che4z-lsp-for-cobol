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
/** asdfasr */
public class Request extends Edge {
  public Request(Type type, Integer inV, int outV, List<Integer> inVs) {
    super(type.desc, inV, outV, inVs);
  }

  /** asdfasr */
  public enum Type {
    DEFINITION("textDocument/definition"),
    REFERENCES("textDocument/references");
    final String desc;

    Type(String desc) {
      this.desc = desc;
    }
  }
}

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

// {"id":3,"type":"vertex","label":"capabilities","hoverProvider":true,
// "declarationProvider":false,"definitionProvider":true,"typeDefinitionProvider":true,
// "referencesProvider":true,"documentSymbolProvider":true,"foldingRangeProvider":true,
// "diagnosticProvider":true}

/** asdfs */
public class Capabilities extends Vertex {

  final boolean hoverProvider = true;
  final boolean declarationProvider = false;
  final boolean definitionProvider = true;
  final boolean typeDefinitionProvider = false;
  final boolean referencesProvider = true;
  final boolean documentSymbolProvider = true;
  final boolean foldingRangeProvider = false;
  final boolean diagnosticProvider = true;

  public Capabilities() {
    super("capabilities");
  }
}

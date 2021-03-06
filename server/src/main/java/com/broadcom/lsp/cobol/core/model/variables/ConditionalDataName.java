/*
 * Copyright (c) 2020 Broadcom.
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

package com.broadcom.lsp.cobol.core.model.variables;

import com.broadcom.lsp.cobol.core.model.Locality;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * This value class represents a conditional data name entry, that has a level number 88. It cannot
 * be a top element in the structure. It always contains a variable name and a value, but not PIC
 * clause.
 */
@Value
@AllArgsConstructor
public class ConditionalDataName implements Variable {
  private String name;
  private String value;
  private Locality definition;
}

/*
 *
 *  Copyright (c) 2019 Broadcom.
 *  The term "Broadcom" refers to Broadcom Inc. and/or its subsidiaries.
 *
 *  This program and the accompanying materials are made
 *  available under the terms of the Eclipse Public License 2.0
 *  which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Broadcom, Inc. - initial API and implementation
 *
 */
package com.broadcom.lsp.domain.cobol.model;

import lombok.Value;

import java.io.Serializable;

@Value
public final class Position implements Serializable {
  private final int tokenIndex;
  private final int startPosition;
  private final int stopPosition;
  private final int line;
  private final int charPositionInLine;

  public Position(
      int tokenIndex, int startPosition, int stopPosition, int line, int charPositionInLine) {
    this.tokenIndex = tokenIndex;
    this.startPosition = startPosition;
    this.stopPosition = stopPosition;
    this.line = line;
    this.charPositionInLine = charPositionInLine;
  }
}
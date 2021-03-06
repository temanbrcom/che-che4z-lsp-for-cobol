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

package com.broadcom.lsp.cobol.usecases;

import com.broadcom.lsp.cobol.positive.CobolText;
import com.broadcom.lsp.cobol.service.delegates.validations.SourceInfoLevels;
import com.broadcom.lsp.cobol.usecases.engine.UseCaseEngine;
import org.eclipse.lsp4j.Diagnostic;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.eclipse.lsp4j.DiagnosticSeverity.Information;

/**
 * This test verifies that the replacing statement changes the variable names following one by one,
 * and definition errors for them found correctly. Here, :TAG: should be replaced with CSTOUT, so
 * the copybook content processed as CSTOUT-KEY and CSTOUT-ID. ABC-CHILD not defined, so there
 * should be the semantic error.
 */
class TestReplacingForSeveralTokensInOneLine {

  private static final String DOCUMENT =
      "       IDENTIFICATION DIVISION.\r\n"
          + "       PROGRAM-ID. TEST1.\r\n"
          + "       DATA DIVISION.\r\n"
          + "       WORKING-STORAGE SECTION.\r\n"
          + "       01 {$*ABCDE-PARENT}.\r\n"
          + "       PROCEDURE DIVISION.\r\n"
          + "       {#*MAIN-LINE}.\r\n"
          + "       COPY {~REPL} REPLACING ==:TAG:== BY ==ABCDE==.\r\n"
          + "           GOBACK.";

  private static final String REPL =
      "              MOVE 10 TO {_{$:TAG:-CHILD^ABCDE-CHILD|invalid} OF {$:TAG:-PARENT^ABCDE-PARENT}|struct_}";

  private static final String REPL_NAME = "REPL";
  private static final String MESSAGE = "Invalid definition for: ABCDE-CHILD";
  private static final String MESSAGE_STRUCTURE = "Invalid definition for: ABCDE-CHILD OF ABCDE-PARENT";

  @Test
  void test() {
    UseCaseEngine.runTest(
        DOCUMENT,
        List.of(new CobolText(REPL_NAME, REPL)),
        Map.of(
            "invalid", new Diagnostic(null, MESSAGE, Information, SourceInfoLevels.INFO.getText()),
            "struct", new Diagnostic(null, MESSAGE_STRUCTURE, Information, SourceInfoLevels.INFO.getText())));
  }
}

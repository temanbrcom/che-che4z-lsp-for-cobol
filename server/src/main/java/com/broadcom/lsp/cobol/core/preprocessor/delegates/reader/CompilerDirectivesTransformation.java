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

package com.broadcom.lsp.cobol.core.preprocessor.delegates.reader;

import com.broadcom.lsp.cobol.core.preprocessor.ProcessingConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This delegate moves the compiler directives to the content area not to let the writer cut the
 * compiler directives, that are allowed in the comment area.
 */
public class CompilerDirectivesTransformation implements CobolLineReaderDelegate {
  private static final Pattern COMPILER_DIRECTIVE_LINE =
      Pattern.compile("(?i)(.{0,6} +|\\s*)(CBL|PROCESS) .+");

  @Override
  public String apply(String line) {
    final Matcher compilerConstantMatcher = COMPILER_DIRECTIVE_LINE.matcher(line);
    if (!compilerConstantMatcher.matches()) {
      return line;
    }
    line = cutTooLongString(line);
    return moveContentFromCommentArea(line);
  }

  private String cutTooLongString(String line) {
    if (line.length() == 80) {
      line = line.substring(0, 72).strip();
    }
    return line;
  }

  private String moveContentFromCommentArea(String line) {
    int index = getLineContentStart(line);
    return ProcessingConstants.BLANK_SEQUENCE_AREA + ProcessingConstants.WS + line.substring(index);
  }

  private int getLineContentStart(String line) {
    int cbl = line.toUpperCase().indexOf("CBL");
    int process = line.toUpperCase().indexOf("PROCESS");
    int max = Math.max(cbl, process);
    return max == -1 ? 0 : max;
  }
}

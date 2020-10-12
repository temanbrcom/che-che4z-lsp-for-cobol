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
package com.broadcom.lsp.cobol.core.preprocessor.delegates.rewriter.impl;

import com.broadcom.lsp.cobol.core.model.CobolLine;
import com.broadcom.lsp.cobol.core.preprocessor.ProcessingConstants;
import com.broadcom.lsp.cobol.core.preprocessor.delegates.rewriter.CobolLineIndicatorProcessorImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.broadcom.lsp.cobol.core.model.CobolLineTypeEnum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Testing that the cobol lines formatted correctly before being used for token analysis */
class CobolLineIndicatorProcessorImplTest {

  private static final String EMPTY_STRING = "";

  /** Testing Debug lines preformatting for Token analysis */
  @Test
  void debugLineTest() {
    CobolLine debugLine = new CobolLine();
    debugLine.setType(DEBUG);
    debugLine.setIndicatorArea(ProcessingConstants.WS);
    debugLine.setContentAreaA("    ");
    debugLine.setContentAreaB("     DEBUG LINE HERE      ");

    CobolLineIndicatorProcessorImpl processor = new CobolLineIndicatorProcessorImpl();
    List<CobolLine> outcome = processor.processLines(List.of(debugLine));
    CobolLine actual = outcome.get(0);

    assertEquals(
        ProcessingConstants.WS + "         DEBUG LINE HERE",
        actual.getIndicatorArea() + actual.getContentArea());
  }

  /** Testing normal lines pre-formatting for Token analysis */
  @Test
  void normalLineTest() {
    CobolLine normalLine = new CobolLine();
    normalLine.setType(NORMAL);
    normalLine.setIndicatorArea(ProcessingConstants.WS);
    normalLine.setContentAreaA("    ");
    normalLine.setContentAreaB("         RANDOM TEXT ,  ");

    CobolLineIndicatorProcessorImpl processor = new CobolLineIndicatorProcessorImpl();

    List<CobolLine> outcome = processor.processLines(List.of(normalLine));
    CobolLine actual = outcome.get(0);

    assertEquals(
        ProcessingConstants.WS + "             RANDOM TEXT , ",
        actual.getIndicatorArea() + actual.getContentArea());
  }

  /** Testing Compiler Directive lines pre-formatting for Token analysis */
  @Test
  void compilerDirectiveTest() {
    CobolLine compilerDirectiveLine = new CobolLine();
    compilerDirectiveLine.setType(COMPILER_DIRECTIVE);
    compilerDirectiveLine.setIndicatorArea(ProcessingConstants.WS);
    compilerDirectiveLine.setContentAreaA("    ");
    compilerDirectiveLine.setContentAreaB("DEFINE");

    CobolLineIndicatorProcessorImpl processor = new CobolLineIndicatorProcessorImpl();
    List<CobolLine> outcome = processor.processLines(List.of(compilerDirectiveLine));
    CobolLine actual = outcome.get(0);

    assertEquals(
        ProcessingConstants.WS + EMPTY_STRING, actual.getIndicatorArea() + actual.getContentArea());
  }

  /** Testing comment lines pre-formatting for Token analysis */
  @Test
  void commentLineTest() {
    CobolLine commentLine = new CobolLine();
    commentLine.setType(COMMENT);
    commentLine.setIndicatorArea("*");
    commentLine.setContentAreaA("    ");
    commentLine.setContentAreaB("THIS IS A COMMENT        ");

    CobolLineIndicatorProcessorImpl processor = new CobolLineIndicatorProcessorImpl();

    List<CobolLine> outcome = processor.processLines(List.of(commentLine));
    CobolLine actual = outcome.get(0);

    assertEquals(
        ProcessingConstants.COMMENT_TAG + ProcessingConstants.WS + "    THIS IS A COMMENT",
        actual.getIndicatorArea() + actual.getContentArea());
  }

  /** Testing normal continuation line pre-formatting for Token analysis */
  @Test
  void continuationLineTest() {
    CobolLine startContinuationLine = new CobolLine();
    startContinuationLine.setType(NORMAL);
    startContinuationLine.setIndicatorArea(ProcessingConstants.WS);
    startContinuationLine.setContentAreaA("    ");
    startContinuationLine.setContentAreaB("       \"RANDOM TEXT   ");

    CobolLine middleContinuationLine = new CobolLine();
    middleContinuationLine.setType(CONTINUATION);
    middleContinuationLine.setIndicatorArea("-");
    middleContinuationLine.setContentAreaA("    ");
    middleContinuationLine.setContentAreaB("        \"RANDOM TEXT   ");

    CobolLine lastContinuationLine = new CobolLine();
    lastContinuationLine.setType(CONTINUATION);
    lastContinuationLine.setIndicatorArea("-");
    lastContinuationLine.setContentAreaA("    ");
    lastContinuationLine.setContentAreaB("        \"CONTINUED LINE ENDS HERE\"     ");

    startContinuationLine.setSuccessor(middleContinuationLine);
    middleContinuationLine.setSuccessor(lastContinuationLine);

    List<CobolLine> listOfLines =
        List.of(startContinuationLine, middleContinuationLine, lastContinuationLine);
    CobolLineIndicatorProcessorImpl processor = new CobolLineIndicatorProcessorImpl();
    List<CobolLine> outcomeList = processor.processLines(listOfLines);

    assertEquals(
        ProcessingConstants.WS + "           \"RANDOM TEXT   ",
        outcomeList.get(0).getIndicatorArea() + outcomeList.get(0).getContentArea());
    assertEquals(
        ProcessingConstants.WS + "RANDOM TEXT   ",
        outcomeList.get(1).getIndicatorArea() + outcomeList.get(1).getContentArea());
    assertEquals(
        ProcessingConstants.WS + "\"CONTINUED LINE ENDS HERE\"",
        outcomeList.get(2).getIndicatorArea() + outcomeList.get(2).getContentArea());
  }

  /** Testing empty continuation line pre-formatting for Token analysis */
  @Test
  void emptyContinuationLine() {
    CobolLine continuationLine = new CobolLine();
    continuationLine.setType(NORMAL);
    continuationLine.setIndicatorArea(ProcessingConstants.WS);
    continuationLine.setContentAreaA("    ");
    continuationLine.setContentAreaB("       \"RANDOM TEXT   ");

    CobolLine emptyContinuationLine = new CobolLine();
    emptyContinuationLine.setType(CONTINUATION);
    emptyContinuationLine.setIndicatorArea("-");
    emptyContinuationLine.setContentAreaA("    ");
    emptyContinuationLine.setContentAreaB("           ");

    continuationLine.setSuccessor(emptyContinuationLine);

    final List<CobolLine> listOfLines = List.of(continuationLine, emptyContinuationLine);
    CobolLineIndicatorProcessorImpl processor = new CobolLineIndicatorProcessorImpl();
    List<CobolLine> outcomeList = processor.processLines(listOfLines);

    assertEquals(
        ProcessingConstants.WS + "           \"RANDOM TEXT   ",
        outcomeList.get(0).getIndicatorArea() + outcomeList.get(0).getContentArea());
    assertEquals(
        ProcessingConstants.WS + EMPTY_STRING,
        outcomeList.get(1).getIndicatorArea() + outcomeList.get(1).getContentArea());
  }

  /** Testing continuation lines with trailing comma pre-formatting for Token analysis */
  @Test
  void trailingCommaContinuationLineTest() {
    CobolLine startContinuationLine = new CobolLine();
    startContinuationLine.setType(NORMAL);
    startContinuationLine.setIndicatorArea(ProcessingConstants.WS);
    startContinuationLine.setContentAreaA("    ");
    startContinuationLine.setContentAreaB("       \"RANDOM TEXT   ");

    CobolLine trailingCommaContinuationLine = new CobolLine();
    trailingCommaContinuationLine.setType(CONTINUATION);
    trailingCommaContinuationLine.setIndicatorArea("-");
    trailingCommaContinuationLine.setContentAreaA("    ");
    trailingCommaContinuationLine.setContentAreaB("         ,");

    startContinuationLine.setSuccessor(trailingCommaContinuationLine);
    trailingCommaContinuationLine.setPredecessor(startContinuationLine);

    List<CobolLine> listOfLines = List.of(startContinuationLine, trailingCommaContinuationLine);
    CobolLineIndicatorProcessorImpl processor = new CobolLineIndicatorProcessorImpl();
    List<CobolLine> outcomeList = processor.processLines(listOfLines);

    assertEquals(
        ProcessingConstants.WS + "           \"RANDOM TEXT   ",
        outcomeList.get(0).getIndicatorArea() + outcomeList.get(0).getContentArea());
    assertEquals(
        ProcessingConstants.WS + "             ," + ProcessingConstants.WS,
        outcomeList.get(1).getIndicatorArea() + outcomeList.get(1).getContentArea());
  }

  /**
   * Testing a continuation line with no quotes at the start of the line pre-formatting for Token
   * analysis
   */
  @Test
  void continuationLineWithoutBeginningQuotes() {
    CobolLine startContinuationLine = new CobolLine();
    startContinuationLine.setType(NORMAL);
    startContinuationLine.setIndicatorArea(ProcessingConstants.WS);
    startContinuationLine.setContentAreaB("       \"RANDOM TEXT   ");

    CobolLine quoteContinuationLine = new CobolLine();
    quoteContinuationLine.setType(CONTINUATION);
    quoteContinuationLine.setIndicatorArea("-");
    quoteContinuationLine.setContentAreaA("    ");
    quoteContinuationLine.setContentAreaB("          \"RANDOM TEXT SINGLE CONTINUATION LINE\"");

    CobolLine lastContinuationLine = new CobolLine();
    lastContinuationLine.setType(CONTINUATION);
    lastContinuationLine.setIndicatorArea("-");
    lastContinuationLine.setContentAreaA("    ");
    lastContinuationLine.setContentAreaB("          RANDOM TEXT SINGLE CONTINUATION LINE\"");

    startContinuationLine.setSuccessor(quoteContinuationLine);
    quoteContinuationLine.setSuccessor(lastContinuationLine);

    CobolLineIndicatorProcessorImpl processor = new CobolLineIndicatorProcessorImpl();

    List<CobolLine> outcome = processor.processLines(List.of(lastContinuationLine));
    CobolLine actual = outcome.get(0);

    assertEquals(
        ProcessingConstants.WS + "RANDOM TEXT SINGLE CONTINUATION LINE\"",
        actual.getIndicatorArea() + actual.getContentArea());
  }

  /**
   * Testing a continuation line with quotes at the start and end of the line pre-formatting for
   * Token analysis
   */
  @Test
  void continuationLineWithOuterQuotes() {
    CobolLine startContinuationLine = new CobolLine();
    startContinuationLine.setType(NORMAL);
    startContinuationLine.setIndicatorArea(ProcessingConstants.WS);
    startContinuationLine.setContentAreaB("       \"RANDOM TEXT   ");

    CobolLine quoteContinuationLine = new CobolLine();
    quoteContinuationLine.setType(CONTINUATION);
    quoteContinuationLine.setIndicatorArea("-");
    quoteContinuationLine.setContentAreaA("    ");
    quoteContinuationLine.setContentAreaB("          \"RANDOM TEXT SINGLE CONTINUATION LINE\"");

    CobolLine lastContinuationLine = new CobolLine();
    lastContinuationLine.setType(CONTINUATION);
    lastContinuationLine.setIndicatorArea("-");
    lastContinuationLine.setContentAreaA("    ");
    lastContinuationLine.setContentAreaB("          \"RANDOM TEXT SINGLE CONTINUATION LINE\"");

    startContinuationLine.setSuccessor(quoteContinuationLine);
    quoteContinuationLine.setSuccessor(lastContinuationLine);

    CobolLineIndicatorProcessorImpl processor = new CobolLineIndicatorProcessorImpl();

    List<CobolLine> outcome = processor.processLines(List.of(lastContinuationLine));
    CobolLine actual = outcome.get(0);

    assertEquals(
        ProcessingConstants.WS + "RANDOM TEXT SINGLE CONTINUATION LINE\"",
        actual.getIndicatorArea() + actual.getContentArea());
  }
}

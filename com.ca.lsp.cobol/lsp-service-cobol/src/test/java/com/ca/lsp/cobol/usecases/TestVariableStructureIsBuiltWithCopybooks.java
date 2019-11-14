package com.ca.lsp.cobol.usecases;

import com.broadcom.lsp.cdi.LangServerCtx;
import com.ca.lsp.cobol.positive.CobolText;
import com.ca.lsp.cobol.service.mocks.MockWorkspaceService;
import org.junit.Test;

import java.util.List;

/**
 * This test case checks that there is no semantic error when a variable structure is defined using
 * a copybook. Here COPYBOOK-CONTENT represents a copybook that has a variable definition with a
 * level 02. By idea this variable will be recognized as a child of PARENT variable. if not, there
 * will be an error thrown at CHILD OF PARENT statement.
 */
public class TestVariableStructureIsBuiltWithCopybooks extends PositiveUseCase {
  private static final String TEXT =
      "       IDENTIFICATION DIVISION.\n"
          + "       PROGRAM-ID. TEST1.\n"
          + "       ENVIRONMENT DIVISION.\n"
          + "       DATA DIVISION.\n"
          + "       WORKING-STORAGE SECTION.\n"
          + "       01  PARENT1.  COPY COPYBOOK-CONTENT.\n"
          + "       01  PARENT2.  COPY COPYBOOK-CONTENT.\n"
          + "       PROCEDURE DIVISION.\n"
          + "           MAINLINE.\n"
          + "           MOVE 00 TO CHILD OF PARENT1.\n"
          + "           MOVE 00 TO CHILD OF PARENT2.\n"
          + "           GOBACK.";

  private static final String COPYBOOK_CONTENT = "       02 CHILD PIC X.";

  public TestVariableStructureIsBuiltWithCopybooks() {
    super(TEXT);
    MockWorkspaceService workspaceService =
        LangServerCtx.getInjector().getInstance(MockWorkspaceService.class);
    workspaceService.setCopybooks(
        () -> List.of(new CobolText("COPYBOOK-CONTENT", COPYBOOK_CONTENT)));
  }

  @Test
  public void test() {
    super.test();
  }
}
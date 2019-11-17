package com.ca.lsp.cobol.usecases;

import com.broadcom.lsp.cdi.LangServerCtx;
import com.ca.lsp.cobol.service.mocks.MockWorkspaceService;
import org.eclipse.lsp4j.Range;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

/** This test checks that if a copybook is not found than error is shown. */
public class TestMissingCopybooksReturnsError extends NegativeUseCase {

  private static final String TEXT =
      "        IDENTIFICATION DIVISION. \r\n"
          + "        PROGRAM-ID. test1.\r\n"
          + "        DATA DIVISION.\r\n"
          + "        WORKING-STORAGE SECTION.\r\n"
          + "        COPY MISSING-COPYBOOK.\n\n"
          + "        PROCEDURE DIVISION.\n\n";

  public TestMissingCopybooksReturnsError() {
    super(TEXT);

    MockWorkspaceService workspaceService =
        LangServerCtx.getInjector().getInstance(MockWorkspaceService.class);
    workspaceService.setCopybooks(Collections::emptyList);
  }

  @Test
  public void test() {
    super.test();
  }

  @Override
  protected void assertRange(Range range) {
    assertEquals(4, range.getStart().getLine());
    assertEquals(13, range.getStart().getCharacter());
    assertEquals(4, range.getEnd().getLine());
    assertEquals(30, range.getEnd().getCharacter());
  }
}

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

package com.broadcom.lsp.cobol.domain.databus.impl;

import com.broadcom.lsp.cobol.domain.event.impl.RequiredCopybookEventSubscriber;
import com.broadcom.lsp.cobol.domain.event.model.DataEvent;
import com.broadcom.lsp.cobol.domain.event.model.DataEventType;
import com.broadcom.lsp.cobol.domain.event.model.RequiredCopybookEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.fail;

/** This test verifies that the observer is triggered by the event it is subscribed to. */
@Slf4j
class DataBusSubscribeHappyTest extends DatabusConfigProvider {

  private DefaultDataBusBroker<RequiredCopybookEvent, RequiredCopybookEventSubscriber> databus;

  @BeforeEach
  void setUp() {
    databus = new DefaultDataBusBroker<>(3, new CopybookRepositoryLRU(3, 3, "HOURS"));
  }

  @Override
  public void observerCallback(DataEvent adaptedDataEvent) {
    waiter.assertTrue(DataEventType.REQUIRED_COPYBOOK_EVENT == adaptedDataEvent.getEventType());
    LOG.debug(String.format("Received : %s", adaptedDataEvent.getEventType().getId()));
    LOG.debug(String.format("Expected : %s", DataEventType.REQUIRED_COPYBOOK_EVENT.getId()));
    waiter.resume();
  }

  @Test
  void subscribe() {
    databus.subscribe(DataEventType.REQUIRED_COPYBOOK_EVENT, this);
    databus.postData(RequiredCopybookEvent.builder().name("CPYBUILD_SUBSCRIPTION TEST").build());
    try {
      waiter.await(5000);
    } catch (TimeoutException | InterruptedException e) {
      fail("No events were received");
    }
  }
}

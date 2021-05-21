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

package org.eclipse.lsp.cobol.lsif.model.vertices;

/** asdfsaf */
public class MetaData extends Vertex {
  final String version;
  final String positionEncoding = "utf-16";
  final String projectRoot;
  final ToolInfo toolInfo;

  private MetaData(String version, String projectRoot, ToolInfo toolInfo) {
    super("metaData");
    this.version = version;
    this.projectRoot = projectRoot;
    this.toolInfo = toolInfo;
  }

  public static MetaData version4(String projectRoot){
    return new MetaData("0.4.3", projectRoot, new ToolInfo());
  }

  public static MetaData version6(){
    return new MetaData("0.6.0-next.2", null, null);
  }

  private static class ToolInfo {
    final String name = "lsif-cobol";
    final String[] args = new String[] {};
    final String version = "0.0.1";
  }
}

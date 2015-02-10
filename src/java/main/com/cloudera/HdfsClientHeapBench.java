/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudera;

import java.io.IOException;
import java.lang.Thread;
import java.lang.System;
import java.net.URI;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;

/**
 * This tests symlink behaviors in Hadoop.
 */
public class HdfsClientHeapBench {
  private static void createFile(FileSystem dfs, Path path, int len)
        throws Exception {
    FSDataOutputStream out = dfs.create(path);
    byte buf[] = new byte[len];
    try {
      out.write(buf);
    } finally {
      out.close();
    }
  }

  public static void main(String[] args) throws Exception {
    System.out.println("running HdfsClientHeapBench: benchmarks " +
        "input stream size in Hadoop...\n");
    final int NUM_OPENS = 50000;
    if (args.length < 1) {
      System.err.println("You must specify a single argument: the URI " +
          "of a directory to test.\n" +
          "Examples: file:///tmp, hdfs:///\n");
      System.exit(1);
    }
    final String uri = args[0];
    Configuration conf = new Configuration();
    conf.setBoolean("dfs.client.read.shortcircuit", false);
    FSDataInputStream[] streams = new FSDataInputStream[NUM_OPENS];
    try {
      FileSystem dfs = FileSystem.get(new URI(uri), conf);
      final Path TEST_PATH = new Path("/testFile");
      createFile(dfs, TEST_PATH, 131072);
      for (int i = 0; i < NUM_OPENS; i++) {
        streams[i] = dfs.open(TEST_PATH);
        System.out.println("opening file " + i + "...");
        if (0 != streams[i].read()) {
          throw new IOException("failed to read a byte from stream " + i +
              ": unexpected EOF.");
        }
        streams[i].unbuffer();
      }
      // Sleep for a long time so we can run jmat to get a heap dump
      Thread.sleep(9000000L);
    } finally {
      for (FSDataInputStream stream : streams) {
        try {
          if (stream != null) {
            stream.close();
          }
        } catch (IOException e) {
          System.out.println("error closing stream: " + e.getMessage());
        }
      }
    }
  }
}

HdfsClientHeapBench
======================
This benchmarks the memory consumed by open files in HDFS.

Building
-------------------------------------------------------------
In order to build and run this test, you must put the HDFS and Hadoop-common
jar files into your classpath.  In the Hadoop install, these are found under
share/hadoop/common/ share/hadoop/hdfs/

TODO: use Ivy.

Running
-------------------------------------------------------------
Here is an example of how to run the test:

    java com.cloudera.HdfsClientHeapBench hdfs://localhost:6000/

Contact information
-------------------------------------------------------------
Colin Patrick McCabe <cmccabe@apache.org>

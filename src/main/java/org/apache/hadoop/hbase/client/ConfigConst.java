/**
 * Licensed to Cloudera, Inc. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  Cloudera, Inc. licenses this file
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

package org.apache.hadoop.hbase.client;

public class ConfigConst {
  public static final String HBASE_FAILOVER_CLUSTERS_CONFIG = "hbase.failover.clusters";
  public static final String HBASE_FAILOVER_CLUSTER_CONFIG = "hbase.failover.cluster";
  public static final String HBASE_FAILOVER_MODE_CONFIG = "hbase.failover.mode";
  public static final String HBASE_WAIT_TIME_BEFORE_ACCEPTING_FAILOVER_RESULT_CONFIG = "hbase.wait.time.before.accepting.failover.result";
  public static final String HBASE_WAIT_TIME_BEFORE_REQUEST_FAILOVER_CONFIG = "hbase.wait.time.before.request.failover";
  public static final String HBASE_WAIT_TIME_BEFORE_MUTATING_FAILOVER_CONFIG = "hbase.wait.time.before.mutating.failover";
  public static final String HBASE_WAIT_TIME_BEFORE_MUTATING_FAILOVER_WITH_PRIMARY_EXCEPTION_CONFIG = "hbase.wait.time.before.mutating.failover.with.primary.exception";
  public static final String HBASE_WAIT_TIME_BEFORE_ACCEPTING_FAILOVER_BATCH_RESULT_CONFIG = "hbase.wait.time.before.accepting.batch.failover.result";
  public static final String HBASE_WAIT_TIME_BEFORE_REQUEST_BATCH_FAILOVER_CONFIG = "hbase.wait.time.before.request.batch.failover";
  public static final String HBASE_WAIT_TIME_BEFORE_MUTATING_BATCH_FAILOVER_CONFIG = "hbase.wait.time.before.mutating.batch.failover";
  public static final String HBASE_MULTI_CLUSTER_CONNECTION_POOL_SIZE = "hbase.multi.cluster.connection.pool.size";
  public static final String HBASE_WAIT_TIME_BEFORE_TRYING_PRIMARY_AFTER_FAILURE = "hbase.wait.time.before.trying.primary.after.failure";

  
}

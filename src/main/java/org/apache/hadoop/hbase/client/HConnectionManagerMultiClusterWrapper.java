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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

public class HConnectionManagerMultiClusterWrapper {

  public static HConnection createConnection(Configuration conf)
      throws IOException {

    Logger LOG = Logger.getLogger(HConnectionManagerMultiClusterWrapper.class);

    Collection < String > failoverClusters = conf
            .getStringCollection(ConfigConst.HBASE_FAILOVER_CLUSTERS_CONFIG);

    if (failoverClusters.size() == 0) {
      LOG.info(" -- Getting a signle cluster connection !!");
      return HConnectionManager.createConnection(conf);
    } else {

      Map<String, Configuration> configMap = HBaseMultiClusterConfigUtil
          .splitMultiConfigFile(conf);

      LOG.info(" -- Getting primary Connction");
      HConnection primaryConnection = HConnectionManager
          .createConnection(configMap
              .get(HBaseMultiClusterConfigUtil.PRIMARY_NAME));
      LOG.info(" --- Got primary Connction");

      ArrayList<HConnection> failoverConnections = new ArrayList<HConnection>();

      for (Entry<String, Configuration> entry : configMap.entrySet()) {
        if (!entry.getKey().equals(HBaseMultiClusterConfigUtil.PRIMARY_NAME)) {
          LOG.info(" -- Getting failure Connction");
          failoverConnections.add(HConnectionManager.createConnection(entry
              .getValue()));
          LOG.info(" --- Got failover Connction");
        }
      }
      
      return new HConnectionMultiCluster(conf, primaryConnection,
          failoverConnections.toArray(new HConnection[0]));
    }
  }
}

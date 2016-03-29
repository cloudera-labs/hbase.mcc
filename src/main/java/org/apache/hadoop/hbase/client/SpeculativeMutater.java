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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SpeculativeMutater {
  static final Log LOG = LogFactory.getLog(SpeculativeMutater.class);

  static ExecutorService exe = Executors.newFixedThreadPool(200);
  
  public static Boolean mutate(final long waitToSendFailover, 
      final long waitToSendFailoverWithException, 
      final HBaseTableFunction<Void> function, 
      final HTableInterface primaryTable,
      final Collection<HTableInterface> failoverTables,
      final AtomicLong lastPrimaryFail,
      final int waitTimeFromLastPrimaryFail) {
    ExecutorCompletionService<Boolean> exeS = new ExecutorCompletionService<Boolean>(exe);
    
    ArrayList<Callable<Boolean>> callables = new ArrayList<Callable<Boolean>>();
    
    final AtomicBoolean isPrimarySuccess = new AtomicBoolean(false);
    final long startTime = System.currentTimeMillis();
    final long lastPrimaryFinalFail = lastPrimaryFail.get();
    
    if (System.currentTimeMillis() - lastPrimaryFinalFail > 5000) {
      callables.add(new Callable<Boolean>() {
        public Boolean call() throws Exception {
          try {
            LOG.info(" --- CallingPrimary.1:" + isPrimarySuccess.get() + ", " + (System.currentTimeMillis() - startTime));
            function.call(primaryTable);
            LOG.info(" --- CallingPrimary.2:" + isPrimarySuccess.get() + ", " + (System.currentTimeMillis() - startTime));
            isPrimarySuccess.set(true);
            return true; 
          } catch (java.io.InterruptedIOException e) {
            Thread.currentThread().interrupt();
          } catch (Exception e) {
            lastPrimaryFail.set(System.currentTimeMillis());
            Thread.currentThread().interrupt();
          }
          return null;
        }
      });
    }
    

    for (final HTableInterface failoverTable : failoverTables) {
      callables.add(new Callable<Boolean>() {

        public Boolean call() throws Exception {
          long waitToRequest = (System.currentTimeMillis() - lastPrimaryFinalFail > 5000)?
              waitToSendFailover - (System.currentTimeMillis() - startTime): waitToSendFailoverWithException - (System.currentTimeMillis() - startTime);

          LOG.info(" --- waitToRequest:" + waitToRequest + "," + (System.currentTimeMillis() - lastPrimaryFinalFail) +
                  "," + (waitToSendFailover - (System.currentTimeMillis() - startTime)) +
                  "," + (waitToSendFailoverWithException - (System.currentTimeMillis() - startTime)));
              
          if (waitToRequest > 0) {
            Thread.sleep(waitToRequest);
          }
          LOG.info(" --- isPrimarySuccess.get():" + isPrimarySuccess.get());
          if (isPrimarySuccess.get() == false) {
            LOG.info(" --- CallingFailOver.1:" + isPrimarySuccess.get() + ", " + (System.currentTimeMillis() - startTime));
            function.call(failoverTable);
            LOG.info(" --- CallingFailOver.2:" + isPrimarySuccess.get() + ", " + (System.currentTimeMillis() - startTime));
          }

          return false;
        }
      });
    }
    try {

      for (Callable<Boolean> call: callables) {
        exeS.submit(call);
      }
      Boolean result = exeS.take().get();

      return result;
    } catch (InterruptedException e) {
      e.printStackTrace();
      LOG.error(e);
    } catch (ExecutionException e) {
      e.printStackTrace();
      LOG.error(e);
    }    
    return null;
  }
}

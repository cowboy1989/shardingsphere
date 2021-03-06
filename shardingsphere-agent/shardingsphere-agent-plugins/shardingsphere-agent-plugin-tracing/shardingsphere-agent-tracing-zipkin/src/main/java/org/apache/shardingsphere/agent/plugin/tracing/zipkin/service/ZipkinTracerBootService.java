/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.agent.plugin.tracing.zipkin.service;

import brave.Tracing;
import org.apache.shardingsphere.agent.core.config.AgentConfiguration;
import org.apache.shardingsphere.agent.core.plugin.service.BootService;
import org.apache.shardingsphere.agent.core.cache.AgentObjectPool;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * Zipkin tracer boot service.
 */
public final class ZipkinTracerBootService implements BootService {
    
    private AsyncZipkinSpanHandler zipkinSpanHandler;
    
    private OkHttpSender sender;
    
    private Tracing tracing;

    @Override
    public void setup() {
        AgentConfiguration configuration = AgentObjectPool.INSTANCE.get(AgentConfiguration.class);
        AgentConfiguration.TracingConfiguration tracingConfiguration = configuration.getTracing();
        sender = OkHttpSender.create(buildHttpPath(tracingConfiguration));
        zipkinSpanHandler = AsyncZipkinSpanHandler.create(sender);
    }

    @Override
    public void start() {
        tracing = Tracing.newBuilder().localServiceName("shardingsphere-agent").addSpanHandler(zipkinSpanHandler).build();
    }

    @Override
    public void cleanup() {
        tracing.close();
        zipkinSpanHandler.close();
        sender.close();
    }
    
    private String buildHttpPath(final AgentConfiguration.TracingConfiguration tracingConfiguration) {
        return "http://" + tracingConfiguration.getAgentHost() + ":" + tracingConfiguration.getAgentPort();
    }
}

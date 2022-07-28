/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.resteasy.rxjava3;

import java.util.concurrent.ExecutorService;

import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.client.SyncInvoker;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;

import dev.resteasy.rxjava3.i18n.Messages;

public class SingleRxInvokerProvider implements RxInvokerProvider<SingleRxInvoker> {
    @Override
    public boolean isProviderFor(Class<?> clazz) {
        return SingleRxInvoker.class.equals(clazz);
    }

    @Override
    public SingleRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService) {
        if (syncInvoker instanceof ClientInvocationBuilder) {
            return new SingleRxInvokerImpl((ClientInvocationBuilder) syncInvoker);
        }
        throw Messages.MESSAGES.expectedClientInvocationBuilder(syncInvoker.getClass().getName());
    }
}

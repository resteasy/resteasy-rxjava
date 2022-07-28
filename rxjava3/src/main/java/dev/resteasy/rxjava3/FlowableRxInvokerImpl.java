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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.SyncInvoker;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.InboundSseEvent;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.plugins.providers.sse.InboundSseEventImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl.SourceBuilder;

import dev.resteasy.rxjava3.i18n.Messages;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;

public class FlowableRxInvokerImpl implements FlowableRxInvoker {
    private static final Object monitor = new Object();
    private final ClientInvocationBuilder syncInvoker;
    private final ScheduledExecutorService executorService;
    private BackpressureStrategy backpressureStrategy = BackpressureStrategy.BUFFER;

    public FlowableRxInvokerImpl(final SyncInvoker syncInvoker, final ExecutorService executorService) {
        if (!(syncInvoker instanceof ClientInvocationBuilder)) {
            throw Messages.MESSAGES.expectedClientInvocationBuilder(syncInvoker.getClass().getName());
        }
        this.syncInvoker = (ClientInvocationBuilder) syncInvoker;
        if (executorService instanceof ScheduledExecutorService) {
            this.executorService = (ScheduledExecutorService) executorService;
        } else {
            this.executorService = null;
        }
    }

    @Override
    public Flowable<?> get() {
        return eventSourceToObservable(getEventSource(), String.class, "GET", null, getAccept());
    }

    @Override
    public <R> Flowable<?> get(Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "GET", null, getAccept());
    }

    @Override
    public <R> Flowable<?> get(GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "GET", null, getAccept());
    }

    @Override
    public Flowable<?> put(Entity<?> entity) {
        return eventSourceToObservable(getEventSource(), String.class, "PUT", entity, getAccept());
    }

    @Override
    public <R> Flowable<?> put(Entity<?> entity, Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "PUT", entity, getAccept());
    }

    @Override
    public <R> Flowable<?> put(Entity<?> entity, GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "PUT", entity, getAccept());
    }

    @Override
    public Flowable<?> post(Entity<?> entity) {
        return eventSourceToObservable(getEventSource(), String.class, "POST", entity, getAccept());
    }

    @Override
    public <R> Flowable<?> post(Entity<?> entity, Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "POST", entity, getAccept());
    }

    @Override
    public <R> Flowable<?> post(Entity<?> entity, GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "POST", entity, getAccept());
    }

    @Override
    public Flowable<?> delete() {
        return eventSourceToObservable(getEventSource(), String.class, "DELETE", null, getAccept());
    }

    @Override
    public <R> Flowable<?> delete(Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "DELETE", null, getAccept());
    }

    @Override
    public <R> Flowable<?> delete(GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "DELETE", null, getAccept());
    }

    @Override
    public Flowable<?> head() {
        return eventSourceToObservable(getEventSource(), String.class, "HEAD", null, getAccept());
    }

    @Override
    public Flowable<?> options() {
        return eventSourceToObservable(getEventSource(), String.class, "OPTIONS", null, getAccept());
    }

    @Override
    public <R> Flowable<?> options(Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "OPTIONS", null, getAccept());
    }

    @Override
    public <R> Flowable<?> options(GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "OPTIONS", null, getAccept());
    }

    @Override
    public Flowable<?> trace() {
        return eventSourceToObservable(getEventSource(), String.class, "TRACE", null, getAccept());
    }

    @Override
    public <R> Flowable<?> trace(Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "TRACE", null, getAccept());
    }

    @Override
    public <R> Flowable<?> trace(GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "TRACE", null, getAccept());
    }

    @Override
    public Flowable<?> method(String name) {
        return eventSourceToObservable(getEventSource(), String.class, name, null, getAccept());
    }

    @Override
    public <R> Flowable<?> method(String name, Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, name, null, getAccept());
    }

    @Override
    public <R> Flowable<?> method(String name, GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, name, null, getAccept());
    }

    @Override
    public Flowable<?> method(String name, Entity<?> entity) {
        return eventSourceToObservable(getEventSource(), String.class, name, entity, getAccept());
    }

    @Override
    public <R> Flowable<?> method(String name, Entity<?> entity, Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, name, entity, getAccept());
    }

    @Override
    public <R> Flowable<?> method(String name, Entity<?> entity, GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, name, entity, getAccept());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public BackpressureStrategy getBackpressureStrategy() {
        return backpressureStrategy;
    }

    @Override
    public void setBackpressureStrategy(BackpressureStrategy backpressureStrategy) {
        this.backpressureStrategy = backpressureStrategy;
    }

    private <T> Flowable<T> eventSourceToObservable(SseEventSourceImpl sseEventSource, Class<T> clazz, String verb,
            Entity<?> entity, MediaType[] mediaTypes) {
        return Flowable.create(
                emitter -> {
                    sseEventSource.register(
                            (InboundSseEvent e) -> {
                                T t = e.readData(clazz, ((InboundSseEventImpl) e).getMediaType());
                                emitter.onNext(t);
                            },
                            emitter::onError,
                            emitter::onComplete);
                    synchronized (monitor) {
                        if (!sseEventSource.isOpen()) {
                            sseEventSource.open(null, verb, entity, mediaTypes);
                        }
                    }
                },
                backpressureStrategy);
    }

    private <T> Flowable<T> eventSourceToObservable(SseEventSourceImpl sseEventSource, GenericType<T> type, String verb,
            Entity<?> entity, MediaType[] mediaTypes) {
        return Flowable.create(
                emitter -> {
                    sseEventSource.register(
                            (InboundSseEvent e) -> {
                                T t = e.readData(type, ((InboundSseEventImpl) e).getMediaType());
                                emitter.onNext(t);
                            },
                            emitter::onError,
                            emitter::onComplete);
                    synchronized (monitor) {
                        if (!sseEventSource.isOpen()) {
                            sseEventSource.open(null, verb, entity, mediaTypes);
                        }
                    }
                },
                backpressureStrategy);
    }

    private SseEventSourceImpl getEventSource() {
        SourceBuilder builder = (SourceBuilder) SseEventSource.target(syncInvoker.getTarget());
        if (executorService != null) {
            builder.executor(executorService);
        }
        return (SseEventSourceImpl) builder.alwaysReconnect(false).build();
    }

    private MediaType[] getAccept() {
        if (syncInvoker != null) {
            List<MediaType> accept = syncInvoker.getHeaders().getAcceptableMediaTypes();
            return accept.toArray(new MediaType[0]);
        } else {
            return null;
        }
    }
}

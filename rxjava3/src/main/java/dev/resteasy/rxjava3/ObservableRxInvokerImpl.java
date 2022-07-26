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

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.plugins.providers.sse.InboundSseEventImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl.SourceBuilder;

import dev.resteasy.rxjava3.i18n.Messages;
import io.reactivex.rxjava3.core.Observable;

public class ObservableRxInvokerImpl implements ObservableRxInvoker {
    private static final Object monitor = new Object();
    private final ClientInvocationBuilder syncInvoker;
    private final ScheduledExecutorService executorService;

    public ObservableRxInvokerImpl(final SyncInvoker syncInvoker, final ExecutorService executorService) {
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
    public Observable<?> get() {
        return eventSourceToObservable(getEventSource(), String.class, "GET", null, getAccept());
    }

    @Override
    public <R> Observable<?> get(Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "GET", null, getAccept());
    }

    @Override
    public <R> Observable<?> get(GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "GET", null, getAccept());
    }

    @Override
    public Observable<?> put(Entity<?> entity) {
        return eventSourceToObservable(getEventSource(), String.class, "PUT", entity, getAccept());
    }

    @Override
    public <R> Observable<?> put(Entity<?> entity, Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "PUT", entity, getAccept());
    }

    @Override
    public <R> Observable<?> put(Entity<?> entity, GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "PUT", entity, getAccept());
    }

    @Override
    public Observable<?> post(Entity<?> entity) {
        return eventSourceToObservable(getEventSource(), String.class, "POST", entity, getAccept());
    }

    @Override
    public <R> Observable<?> post(Entity<?> entity, Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "POST", entity, getAccept());
    }

    @Override
    public <R> Observable<?> post(Entity<?> entity, GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "POST", entity, getAccept());
    }

    @Override
    public Observable<?> delete() {
        return eventSourceToObservable(getEventSource(), String.class, "DELETE", null, getAccept());
    }

    @Override
    public <R> Observable<?> delete(Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "DELETE", null, getAccept());
    }

    @Override
    public <R> Observable<?> delete(GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "DELETE", null, getAccept());
    }

    @Override
    public Observable<?> head() {
        return eventSourceToObservable(getEventSource(), String.class, "HEAD", null, getAccept());
    }

    @Override
    public Observable<?> options() {
        return eventSourceToObservable(getEventSource(), String.class, "OPTIONS", null, getAccept());
    }

    @Override
    public <R> Observable<?> options(Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "OPTIONS", null, getAccept());
    }

    @Override
    public <R> Observable<?> options(GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "OPTIONS", null, getAccept());
    }

    @Override
    public Observable<?> trace() {
        return eventSourceToObservable(getEventSource(), String.class, "TRACE", null, getAccept());
    }

    @Override
    public <R> Observable<?> trace(Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "TRACE", null, getAccept());
    }

    @Override
    public <R> Observable<?> trace(GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, "TRACE", null, getAccept());
    }

    @Override
    public Observable<?> method(String name) {
        return eventSourceToObservable(getEventSource(), String.class, name, null, getAccept());
    }

    @Override
    public <R> Observable<?> method(String name, Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, name, null, getAccept());
    }

    @Override
    public <R> Observable<?> method(String name, GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, name, null, getAccept());
    }

    @Override
    public Observable<?> method(String name, Entity<?> entity) {
        return eventSourceToObservable(getEventSource(), String.class, name, entity, getAccept());
    }

    @Override
    public <R> Observable<?> method(String name, Entity<?> entity, Class<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, name, entity, getAccept());
    }

    @Override
    public <R> Observable<?> method(String name, Entity<?> entity, GenericType<R> responseType) {
        return eventSourceToObservable(getEventSource(), responseType, name, entity, getAccept());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private <T> Observable<T> eventSourceToObservable(SseEventSourceImpl sseEventSource, Class<T> clazz, String verb,
            Entity<?> entity, MediaType[] mediaTypes) {
        return Observable.create(
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
                });
    }

    private <T> Observable<T> eventSourceToObservable(SseEventSourceImpl sseEventSource, GenericType<T> type, String verb,
            Entity<?> entity, MediaType[] mediaTypes) {
        return Observable.create(
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
                });
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

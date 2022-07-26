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

import java.util.Objects;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.PublisherRxInvokerImpl;
import org.reactivestreams.Publisher;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class SingleRxInvokerImpl implements SingleRxInvoker {

    private final SinglePublisherInvoker publisherInvoker;

    public SingleRxInvokerImpl(final ClientInvocationBuilder builder) {
        publisherInvoker = new SinglePublisherInvoker(Objects.requireNonNull(builder));
    }

    static class SinglePublisherInvoker extends PublisherRxInvokerImpl {
        SinglePublisherInvoker(final ClientInvocationBuilder builder) {
            super(builder);
        }

        @Override
        protected <T> Publisher<T> toPublisher(CompletionStage<T> completable) {
            return Flowable.fromFuture(completable.toCompletableFuture());
        }
    }

    @Override
    public Single<Response> get() {
        return Single.fromPublisher(publisherInvoker.get());
    }

    @Override
    public <T> Single<T> get(Class<T> responseType) {
        return Single.fromPublisher(publisherInvoker.get(responseType));
    }

    @Override
    public <T> Single<T> get(GenericType<T> responseType) {
        return Single.fromPublisher(publisherInvoker.get(responseType));
    }

    @Override
    public Single<Response> put(Entity<?> entity) {
        return Single.fromPublisher(publisherInvoker.put(entity));
    }

    @Override
    public <T> Single<T> put(Entity<?> entity, Class<T> clazz) {
        return Single.fromPublisher(publisherInvoker.put(entity, clazz));
    }

    @Override
    public <T> Single<T> put(Entity<?> entity, GenericType<T> type) {
        return Single.fromPublisher(publisherInvoker.put(entity, type));
    }

    @Override
    public Single<Response> post(Entity<?> entity) {
        return Single.fromPublisher(publisherInvoker.post(entity));
    }

    @Override
    public <T> Single<T> post(Entity<?> entity, Class<T> clazz) {
        return Single.fromPublisher(publisherInvoker.post(entity, clazz));
    }

    @Override
    public <T> Single<T> post(Entity<?> entity, GenericType<T> type) {
        return Single.fromPublisher(publisherInvoker.post(entity, type));
    }

    @Override
    public Single<Response> delete() {
        return Single.fromPublisher(publisherInvoker.delete());
    }

    @Override
    public <T> Single<T> delete(Class<T> responseType) {
        return Single.fromPublisher(publisherInvoker.delete(responseType));
    }

    @Override
    public <T> Single<T> delete(GenericType<T> responseType) {
        return Single.fromPublisher(publisherInvoker.delete(responseType));
    }

    @Override
    public Single<Response> head() {
        return Single.fromPublisher(publisherInvoker.head());
    }

    @Override
    public Single<Response> options() {
        return Single.fromPublisher(publisherInvoker.options());
    }

    @Override
    public <T> Single<T> options(Class<T> responseType) {
        return Single.fromPublisher(publisherInvoker.options(responseType));
    }

    @Override
    public <T> Single<T> options(GenericType<T> responseType) {
        return Single.fromPublisher(publisherInvoker.options(responseType));
    }

    @Override
    public Single<Response> trace() {
        return Single.fromPublisher(publisherInvoker.trace());
    }

    @Override
    public <T> Single<T> trace(Class<T> responseType) {
        return Single.fromPublisher(publisherInvoker.trace(responseType));
    }

    @Override
    public <T> Single<T> trace(GenericType<T> responseType) {
        return Single.fromPublisher(publisherInvoker.trace(responseType));
    }

    @Override
    public Single<Response> method(String name) {
        return Single.fromPublisher(publisherInvoker.method(name));
    }

    @Override
    public <T> Single<T> method(String name, Class<T> responseType) {
        return Single.fromPublisher(publisherInvoker.method(name, responseType));
    }

    @Override
    public <T> Single<T> method(String name, GenericType<T> responseType) {
        return Single.fromPublisher(publisherInvoker.method(name, responseType));
    }

    @Override
    public Single<Response> method(String name, Entity<?> entity) {
        return Single.fromPublisher(publisherInvoker.method(name, entity));
    }

    @Override
    public <T> Single<T> method(String name, Entity<?> entity, Class<T> responseType) {
        return Single.fromPublisher(publisherInvoker.method(name, entity, responseType));
    }

    @Override
    public <T> Single<T> method(String name, Entity<?> entity, GenericType<T> responseType) {
        return Single.fromPublisher(publisherInvoker.method(name, entity, responseType));
    }
}

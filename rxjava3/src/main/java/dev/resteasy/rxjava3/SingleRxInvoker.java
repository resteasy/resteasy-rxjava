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

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.RxInvoker;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import io.reactivex.rxjava3.core.Single;

public interface SingleRxInvoker extends RxInvoker<Single<?>> {
    @Override
    Single<Response> get();

    @Override
    <T> Single<T> get(Class<T> responseType);

    @Override
    <T> Single<T> get(GenericType<T> responseType);

    @Override
    Single<Response> put(Entity<?> entity);

    @Override
    <T> Single<T> put(Entity<?> entity, Class<T> clazz);

    @Override
    <T> Single<T> put(Entity<?> entity, GenericType<T> type);

    @Override
    Single<Response> post(Entity<?> entity);

    @Override
    <T> Single<T> post(Entity<?> entity, Class<T> clazz);

    @Override
    <T> Single<T> post(Entity<?> entity, GenericType<T> type);

    @Override
    Single<Response> delete();

    @Override
    <T> Single<T> delete(Class<T> responseType);

    @Override
    <T> Single<T> delete(GenericType<T> responseType);

    @Override
    Single<Response> head();

    @Override
    Single<Response> options();

    @Override
    <T> Single<T> options(Class<T> responseType);

    @Override
    <T> Single<T> options(GenericType<T> responseType);

    @Override
    Single<Response> trace();

    @Override
    <T> Single<T> trace(Class<T> responseType);

    @Override
    <T> Single<T> trace(GenericType<T> responseType);

    @Override
    Single<Response> method(String name);

    @Override
    <T> Single<T> method(String name, Class<T> responseType);

    @Override
    <T> Single<T> method(String name, GenericType<T> responseType);

    @Override
    Single<Response> method(String name, Entity<?> entity);

    @Override
    <T> Single<T> method(String name, Entity<?> entity, Class<T> responseType);

    @Override
    <T> Single<T> method(String name, Entity<?> entity, GenericType<T> responseType);

}

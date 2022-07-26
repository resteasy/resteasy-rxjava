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

package dev.resteasy.rxjava3.propagation;

import java.util.concurrent.Executor;

import org.jboss.resteasy.concurrent.ContextualExecutors;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.functions.Function;

@SuppressWarnings("rawtypes")
class ContextPropagatorOnSingleAssemblyAction implements Function<Single, Single> {

    ContextPropagatorOnSingleAssemblyAction() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Single apply(final Single t) throws Exception {
        return new ContextPropagatorSingle(t, ContextualExecutors.executor());
    }

    private static class ContextPropagatorSingle<T> extends Single<T> {

        private final Single<T> source;
        private final Executor contextExecutor;

        private ContextPropagatorSingle(final Single<T> t, final Executor contextExecutor) {
            this.source = t;
            this.contextExecutor = contextExecutor;
        }

        @Override
        protected void subscribeActual(final SingleObserver<? super T> observer) {
            contextExecutor.execute(() -> source.subscribe(observer));
        }

    }

}

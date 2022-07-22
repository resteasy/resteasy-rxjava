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

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;

@SuppressWarnings("rawtypes")
class ContextPropagatorOnMaybeCreateAction implements BiFunction<Maybe, MaybeObserver, MaybeObserver> {

    ContextPropagatorOnMaybeCreateAction() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public MaybeObserver apply(final Maybe maybe, final MaybeObserver observer) throws Exception {
        return new ContextCapturerMaybe<>(maybe, observer, ContextualExecutors.executor());
    }

    private static class ContextCapturerMaybe<T> implements MaybeObserver<T> {

        private final MaybeObserver<T> source;
        private final Executor contextExecutor;

        private ContextCapturerMaybe(final Maybe<T> observable, final MaybeObserver<T> observer,
                final Executor contextExecutor) {
            this.source = observer;
            this.contextExecutor = contextExecutor;
        }

        @Override
        public void onComplete() {
            contextExecutor.execute(source::onComplete);
        }

        @Override
        public void onError(final Throwable t) {
            contextExecutor.execute(() -> source.onError(t));
        }

        @Override
        public void onSubscribe(final Disposable d) {
            contextExecutor.execute(() -> source.onSubscribe(d));
        }

        @Override
        public void onSuccess(T v) {
            contextExecutor.execute(() -> source.onSuccess(v));
        }
    }

}

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

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;

class ContextPropagatorOnCompletableCreateAction
        implements BiFunction<Completable, CompletableObserver, CompletableObserver> {

    ContextPropagatorOnCompletableCreateAction() {
    }

    @Override
    public CompletableObserver apply(final Completable completable, final CompletableObserver observer)
            throws Exception {
        return new ContextCapturerCompletable(completable, observer, ContextualExecutors.executor());
    }

    private static class ContextCapturerCompletable implements CompletableObserver {

        private final CompletableObserver source;
        private final Executor contextExecutor;

        private ContextCapturerCompletable(final Completable s, final CompletableObserver o,
                final Executor contextExecutor) {
            this.source = o;
            this.contextExecutor = contextExecutor;
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
        public void onComplete() {
            contextExecutor.execute(source::onComplete);
        }
    }

}

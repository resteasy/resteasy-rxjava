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
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.BiFunction;

@SuppressWarnings("rawtypes")
class ContextPropagatorOnFlowableCreateAction implements BiFunction<Flowable, Subscriber, Subscriber> {

    ContextPropagatorOnFlowableCreateAction() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Subscriber apply(final Flowable flowable, final Subscriber observer) throws Exception {
        return new ContextCapturerFlowable<>(flowable, observer, ContextualExecutors.executor());
    }

    @SuppressWarnings("ReactiveStreamsSubscriberImplementation")
    private static class ContextCapturerFlowable<T> implements Subscriber<T> {

        private final Subscriber<T> source;
        private final Executor contextExecutor;

        private ContextCapturerFlowable(final Flowable<T> observable, final Subscriber<T> observer,
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
        public void onNext(final T v) {
            contextExecutor.execute(() -> source.onNext(v));
        }

        @Override
        public void onSubscribe(final Subscription s) {
            contextExecutor.execute(() -> source.onSubscribe(s));
        }
    }

}

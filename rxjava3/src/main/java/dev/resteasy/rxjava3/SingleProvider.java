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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncClientResponseProvider;
import org.jboss.resteasy.spi.AsyncResponseProvider;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;

@Provider
public class SingleProvider implements AsyncResponseProvider<Single<?>>, AsyncClientResponseProvider<Single<?>> {
    private static class SingleAdaptor<T> extends CompletableFuture<T> {
        private final Disposable subscription;

        SingleAdaptor(final Single<T> single) {
            this.subscription = single.subscribe(this::complete, this::completeExceptionally);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            subscription.dispose();
            return super.cancel(mayInterruptIfRunning);
        }
    }

    @Override
    public CompletionStage<?> toCompletionStage(Single<?> asyncResponse) {
        return new SingleAdaptor<>(asyncResponse);
    }

    @Override
    public Single<?> fromCompletionStage(CompletionStage<?> completionStage) {
        return Single.fromFuture(completionStage.toCompletableFuture());
    }

    @Override
    public Single<?> fromCompletionStage(final Supplier<CompletionStage<?>> completionStageSupplier) {
        return Single.defer(() -> fromCompletionStage(completionStageSupplier.get()));
    }
}

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

import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

@Provider
public class ObservableProvider implements AsyncStreamProvider<Observable<?>> {
    @Override
    public Publisher<?> toAsyncStream(Observable<?> asyncResponse) {
        return asyncResponse.toFlowable(BackpressureStrategy.BUFFER);
    }

}

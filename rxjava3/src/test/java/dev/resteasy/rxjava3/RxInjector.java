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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ContextInjector;

import io.reactivex.rxjava3.core.Single;

@Provider
public class RxInjector implements ContextInjector<Single<Integer>, Integer> {

    @Override
    public Single<Integer> resolve(Class<? extends Single<Integer>> rawType, Type genericType,
            Annotation[] annotations) {
        boolean async = false;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Async.class)
                async = true;
        }
        if (!async)
            return Single.just(42);
        return Single.create(emitter -> {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    emitter.onError(e);
                    return;
                }
                emitter.onSuccess(42);
            }).start();
        });
    }

}

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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.Stream;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Path("/")
public class RxResource {
    @Path("single")
    @GET
    public Single<String> single() {
        return Single.just("got it");
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("observable")
    @GET
    @Stream
    public Observable<String> observable() {
        return Observable.fromArray("one", "two");
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("flowable")
    @GET
    @Stream
    public Flowable<String> flowable() {
        return Flowable.fromArray("one", "two");
    }

    @Path("context/single")
    @GET
    public Single<String> contextSingle(@Context UriInfo uriInfo) {
        return Single.<String> create(foo -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new Runnable() {
                public void run() {
                    foo.onSuccess("got it");
                }
            });
        }).map(str -> {
            uriInfo.getAbsolutePath();
            return str;
        });
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("context/observable")
    @GET
    @Stream
    public Observable<String> contextObservable(@Context UriInfo uriInfo) {
        return Observable.<String> create(foo -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new Runnable() {
                public void run() {
                    foo.onNext("one");
                    foo.onNext("two");
                    foo.onComplete();
                }
            });
        }).map(str -> {
            uriInfo.getAbsolutePath();
            return str;
        });
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("context/flowable")
    @GET
    @Stream
    public Flowable<String> contextFlowable(@Context UriInfo uriInfo) {
        return Flowable.<String> create(foo -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new Runnable() {
                public void run() {
                    foo.onNext("one");
                    foo.onNext("two");
                    foo.onComplete();
                }
            });
        }, BackpressureStrategy.BUFFER).map(str -> {
            uriInfo.getAbsolutePath();
            return str;
        });
    }

    @Path("injection")
    @GET
    public Single<Integer> injection(@Context Integer value) {
        return Single.just(value);
    }

    @Path("injection-async")
    @GET
    public Single<Integer> injectionAsync(@Async @Context Integer value) {
        return Single.just(value);
    }
}

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

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;

public class RxTest {
    private static UndertowJaxrsServer server;

    private static CountDownLatch latch;
    private static final AtomicReference<Object> value = new AtomicReference<>();
    private static final Logger LOG = Logger.getLogger(RxTest.class);

    @BeforeAll
    public static void beforeClass() {
        server = new UndertowJaxrsServer();
        server.getDeployment().getActualResourceClasses().add(RxResource.class);
        server.getDeployment().getActualProviderClasses().add(RxInjector.class);
        server.getDeployment().start();
        server.getDeployment().registration();
        // Required to explicitly deploy
        server.deploy();
        server.start();
    }

    @AfterAll
    public static void afterClass() {
        server.stop();
        server = null;
    }

    private Client client;
    private Disposable disposable;

    @BeforeEach
    public void before() {
        client = ClientBuilder.newBuilder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
        value.set(null);
        latch = new CountDownLatch(1);
    }

    @AfterEach
    public void after() {
        client.close();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Test
    public void testSingle() throws Exception {
        Single<Response> single = client.target(generateURL("/single")).request().rx(SingleRxInvoker.class).get();
        disposable = single.subscribe((Response r) -> {
            value.set(r.readEntity(String.class));
            latch.countDown();
        });
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS), "Did not complete request within 5 seconds");
        Assertions.assertEquals("got it", value.get());
    }

    @Test
    public void testSingleContext() throws Exception {
        Single<Response> single = client.target(generateURL("/context/single")).request().rx(SingleRxInvoker.class).get();
        disposable = single.subscribe((Response r) -> {
            value.set(r.readEntity(String.class));
            latch.countDown();
        });
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS), "Did not complete request within 5 seconds");
        Assertions.assertEquals("got it", value.get());
    }

    @Test
    public void testObservable() throws Exception {
        ObservableRxInvoker invoker = client.target(generateURL("/observable")).request().rx(ObservableRxInvoker.class);
        @SuppressWarnings("unchecked")
        Observable<String> observable = (Observable<String>) invoker.get();
        Set<String> data = new TreeSet<>(); //FIXME [RESTEASY-2778] Intermittent flow / flux test failure
        disposable = observable.subscribe(
                data::add,
                (Throwable t) -> LOG.error(t.getMessage(), t),
                () -> latch.countDown());
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS), "Did not complete request within 5 seconds");
        Assertions.assertArrayEquals(new String[] { "one", "two" }, data.toArray());
    }

    @Test
    public void testObservableContext() throws Exception {
        ObservableRxInvoker invoker = ClientBuilder.newClient().target(generateURL("/context/observable")).request()
                .rx(ObservableRxInvoker.class);
        @SuppressWarnings("unchecked")
        Observable<String> observable = (Observable<String>) invoker.get();
        Set<String> data = new TreeSet<>(); //FIXME [RESTEASY-2778] Intermittent flow / flux test failure
        disposable = observable.subscribe(
                data::add,
                (Throwable t) -> LOG.error(t.getMessage(), t),
                () -> latch.countDown());
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS), "Did not complete request within 5 seconds");
        Assertions.assertArrayEquals(new String[] { "one", "two" }, data.toArray());
    }

    @Test
    public void testFlowable() throws Exception {
        FlowableRxInvoker invoker = client.target(generateURL("/flowable")).request().rx(FlowableRxInvoker.class);
        @SuppressWarnings("unchecked")
        Flowable<String> flowable = (Flowable<String>) invoker.get();
        Set<String> data = new TreeSet<>(); //FIXME [RESTEASY-2778] Intermittent flow / flux test failure
        disposable = flowable.subscribe(
                data::add,
                (Throwable t) -> LOG.error(t.getMessage(), t),
                () -> latch.countDown());
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS), "Did not complete request within 5 seconds");
        Assertions.assertArrayEquals(new String[] { "one", "two" }, data.toArray());
    }

    @Test
    public void testFlowablecontext() throws Exception {
        FlowableRxInvoker invoker = client.target(generateURL("/context/flowable")).request().rx(FlowableRxInvoker.class);
        @SuppressWarnings("unchecked")
        Flowable<String> flowable = (Flowable<String>) invoker.get();
        Set<String> data = new TreeSet<>(); //FIXME [RESTEASY-2778] Intermittent flow / flux test failure
        disposable = flowable.subscribe(
                data::add,
                (Throwable t) -> LOG.error(t.getMessage(), t),
                () -> {
                    latch.countDown();
                    LOG.info("onComplete()");
                });
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS), "Did not complete request within 5 seconds");
        Assertions.assertArrayEquals(new String[] { "one", "two" }, data.toArray());
    }

    // @Test
    public void testChunked() throws Exception {
        Invocation.Builder request = client.target(generateURL("/chunked")).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("onetwo", entity);
    }

    @Test
    public void testInjection() {
        Integer data = client.target(generateURL("/injection")).request().get(Integer.class);
        Assertions.assertEquals((Integer) 42, data);

        data = client.target(generateURL("/injection-async")).request().get(Integer.class);
        Assertions.assertEquals((Integer) 42, data);
    }
}

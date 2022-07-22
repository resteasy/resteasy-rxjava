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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.reactivex.rxjava3.core.Single;

public class SingleProviderTest {
    private final SingleProvider provider = new SingleProvider();

    @Test
    public void testFromCompletionStage() {
        final CompletableFuture<Integer> cs = new CompletableFuture<>();
        cs.complete(1);
        final Single<?> single = provider.fromCompletionStage(cs);
        Assertions.assertEquals(1, single.blockingGet());
    }

    @Test
    public void testFromCompletionStageNotDeferred() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Single<?> single = provider.fromCompletionStage(someAsyncMethod(latch));

        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals("Hello!", single.blockingGet());
        Assertions.assertEquals(0, latch.getCount());
    }

    @Test
    public void testFromCompletionStageDeferred() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Single<?> single = provider.fromCompletionStage(() -> someAsyncMethod(latch));

        Assertions.assertFalse(latch.await(1, TimeUnit.SECONDS));
        Assertions.assertEquals("Hello!", single.blockingGet());
        Assertions.assertEquals(0, latch.getCount());
    }

    private CompletableFuture<String> someAsyncMethod(final CountDownLatch latch) {
        latch.countDown();
        return CompletableFuture.completedFuture("Hello!");
    }

    @Test
    public void testToCompletionStageCase() throws Exception {
        final Object actual = provider.toCompletionStage(Single.just(1)).toCompletableFuture().get();
        Assertions.assertEquals(1, actual);
    }
}

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

import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.Dispatcher;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

/**
 * Reactive Context propagator for RxJava 2. Supports propagating context to all
 * {@link Single}, {@link Observable}, {@link Completable}, {@link Flowable} and
 * {@link Maybe} types.
 *
 * @author Stéphane Épardaud
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Provider
public class RxJava3ContextPropagator implements Feature {

    @Override
    public boolean configure(final FeatureContext context) {
        // this is tied to the deployment, which is what we want for the reactive context
        if (context.getConfiguration().getRuntimeType() == RuntimeType.CLIENT)
            return false;
        if (ResteasyContext.getContextData(Dispatcher.class) == null) {
            // this can happen, but it means we're not able to find a deployment
            return false;
        }
        configure();
        return true;
    }

    private synchronized void configure() {
        RxJavaPlugins.setOnSingleSubscribe(new ContextPropagatorOnSingleCreateAction());
        RxJavaPlugins.setOnCompletableSubscribe(new ContextPropagatorOnCompletableCreateAction());
        RxJavaPlugins.setOnFlowableSubscribe(new ContextPropagatorOnFlowableCreateAction());
        RxJavaPlugins.setOnMaybeSubscribe(new ContextPropagatorOnMaybeCreateAction());
        RxJavaPlugins.setOnObservableSubscribe(new ContextPropagatorOnObservableCreateAction());

        RxJavaPlugins.setOnSingleAssembly(new ContextPropagatorOnSingleAssemblyAction());
        RxJavaPlugins.setOnCompletableAssembly(new ContextPropagatorOnCompletableAssemblyAction());
        RxJavaPlugins.setOnFlowableAssembly(new ContextPropagatorOnFlowableAssemblyAction());
        RxJavaPlugins.setOnMaybeAssembly(new ContextPropagatorOnMaybeAssemblyAction());
        RxJavaPlugins.setOnObservableAssembly(new ContextPropagatorOnObservableAssemblyAction());
    }
}

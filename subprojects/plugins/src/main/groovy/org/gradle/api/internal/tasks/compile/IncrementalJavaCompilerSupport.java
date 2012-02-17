/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.tasks.compile;

import org.gradle.api.tasks.WorkResult;

/**
 * A dumb incremental compiler. Deletes stale classes before invoking the actual compiler
 */
public abstract class IncrementalJavaCompilerSupport<T extends JavaCompiler> extends JavaCompilerSupport {
    private final T compiler;

    public IncrementalJavaCompilerSupport(T compiler) {
        this.compiler = compiler;
    }

    public T getCompiler() {
        return compiler;
    }

    public WorkResult execute() {
        StaleClassCleaner cleaner = createCleaner();
        cleaner.setDestinationDir(spec.getDestinationDir());
        cleaner.setSource(spec.getSource());
        cleaner.setCompileOptions(compiler.getCompileOptions());
        cleaner.execute();

        configure(compiler);
        return compiler.execute();
    }

    protected abstract StaleClassCleaner createCleaner();
}

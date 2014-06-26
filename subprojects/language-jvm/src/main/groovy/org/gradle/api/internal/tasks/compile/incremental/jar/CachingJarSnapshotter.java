/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.tasks.compile.incremental.jar;

import org.gradle.api.internal.hash.Hasher;
import org.gradle.api.internal.tasks.compile.incremental.analyzer.ClassDependenciesAnalyzer;
import org.gradle.api.internal.tasks.compile.incremental.cache.JarSnapshotCache;
import org.gradle.internal.Factory;

import java.util.Map;

public class CachingJarSnapshotter implements JarSnapshotter {

    private final DefaultJarSnapshotter snapshotter;
    private final Hasher hasher;
    private final JarSnapshotCache cache;
    private final Map<String, byte[]> inputFilesSnapshot;

    public CachingJarSnapshotter(Hasher hasher, ClassDependenciesAnalyzer analyzer, JarSnapshotCache cache, Map<String, byte[]> inputFilesSnapshot) {
        this.inputFilesSnapshot = inputFilesSnapshot;
        this.snapshotter = new DefaultJarSnapshotter(hasher, analyzer);
        this.hasher = hasher;
        this.cache = cache;
    }

    public JarSnapshot createSnapshot(final JarArchive jarArchive) {
        final byte[] hash = getHash(jarArchive);
        return cache.get(hash, new Factory<JarSnapshot>() {
            public JarSnapshot create() {
                return snapshotter.createSnapshot(hash, jarArchive);
            }
        });
    }

    private byte[] getHash(JarArchive jarArchive) {
        byte[] hash = inputFilesSnapshot.get(jarArchive.file.getPath());
        if (hash != null) {
            return hash;
        }
        return hasher.hash(jarArchive.file);
    }
}
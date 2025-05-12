package com.eriwen.gradle.js.source.internal

import com.eriwen.gradle.js.source.JavaScriptProcessingChain
import com.eriwen.gradle.js.source.JavaScriptSourceSet
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.model.ObjectFactory
import org.gradle.internal.reflect.Instantiator

class DefaultJavaScriptSourceSet implements JavaScriptSourceSet {

    private final String name
    private final String displayName
    private final SourceDirectorySet js
    private final JavaScriptProcessingChain processing
    private final FileCollection processed

    DefaultJavaScriptSourceSet(String name, Project project, Instantiator instantiator, FileResolver fileResolver) {
        this.name = name
        this.displayName = name
        ObjectFactory objectFactory = project.getObjects();
        js = objectFactory.sourceDirectorySet(name, String.format("%s JavaScript source", displayName));
        this.processing = instantiator.newInstance(DefaultJavaScriptProcessingChain, project, this, instantiator)
        this.processed = project.files({ processing.empty ? js : processing.last().outputs.files })
    }

    String getName() {
        name
    }
    
    SourceDirectorySet getJs() {
        js
    }

    SourceDirectorySet js(Action<SourceDirectorySet> action) {
        action.execute(js)
        js
    }

    JavaScriptSourceSet configure(Closure closure) {
        closure.setDelegate(this)
        closure.setResolveStrategy(Closure.DELEGATE_FIRST)
        closure.call()
        return this
    }

    JavaScriptProcessingChain getProcessing() {
        processing
    }

    JavaScriptProcessingChain processing(Action<JavaScriptProcessingChain> action) {
        action.execute(processing)
        processing
    }

    FileCollection getProcessed() {
        processed
    }
}

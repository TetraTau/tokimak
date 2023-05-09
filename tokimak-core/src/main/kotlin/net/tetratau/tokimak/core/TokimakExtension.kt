package net.tetratau.tokimak.core


import org.gradle.api.Action
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import org.gradle.kotlin.dsl.register

open class TokimakExtension(project: Project, private val objects: ObjectFactory, tasks: TaskContainer) {

}
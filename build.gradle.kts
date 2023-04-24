
import net.minecrell.gitpatcher.task.FindGitTask
import net.minecrell.gitpatcher.task.UpdateSubmodulesTask
import net.minecrell.gitpatcher.task.patch.ApplyPatchesTask
import net.minecrell.gitpatcher.task.patch.MakePatchesTask
import net.minecrell.gitpatcher.task.patch.PatchTask

plugins {
    id("net.minecraftforge.gitpatcher") version "0.10.+"
}

group = "net.tetratau.tokimak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

patches {
    submodule = "paperweight"
    target = file("paperweight-plugin")
    patches = file("patches/paperweight")
}

createPatchTasks(rootProject, "fabric-mixin-compile-extensions", file("compile-extensions"), file("patches/compile-extensions"))

fun createPatchTasks(project: Project, submodule: String, target: File, patches: File) {
    project.withGroovyBuilder {
        val root = projectDir

        val findGit = tasks.register<FindGitTask>("find${submodule}Git")
        val updateSubmodules = tasks.register<UpdateSubmodulesTask>("update${submodule}Submodules") { dependsOn(findGit) }
        val applyPatches = tasks.register<ApplyPatchesTask>("apply${submodule}Patches"/*, dependsOn: 'updateSubmodules' We don't want to update the submodule if we're targeting a specific commit */)
        val makePatches = tasks.register<MakePatchesTask>("make${submodule}Patches") { dependsOn(findGit) }

        tasks.withType<PatchTask>().configureEach {
            addAsSafeDirectory.set(project.providers.environmentVariable("GITPATCHER_ADD_GIT_SAFEDIR")
                .map { it.equals("true") }
                .orElse(false))
            committerName.set("GitPatcher")
            committerEmail.set("gitpatcher@noreply")
        }

        afterEvaluate {
            findGit.configure {
                this.submodule = submodule
            }

            configure(listOf(applyPatches.get(), makePatches.get())) {
                this.repo = target
                this.root = root
                this.submodule = submodule
                this.patchDir = patches
            }

            applyPatches.get().updateTask = updateSubmodules.get()

            updateSubmodules.configure {
                this.repo = root
                this.submodule = submodule
            }
        }
    }
}

package nebula.test

import nebula.test.functional.ExecutionResult
import nebula.test.functional.internal.launcherapi.LauncherExecutionResult
import org.gradle.api.logging.LogLevel
import spock.lang.Unroll

class ConcreteIntegrationSpec extends IntegrationSpec {
    def 'runs build'() {
        when:
        ExecutionResult buildResult = runTasks('dependencies')

        then:
        useToolingApi
        buildResult.failure == null
    }

    def 'runs successful build with Launcher'() {
        when:
        useToolingApi = false
        logLevel = LogLevel.DEBUG
        ExecutionResult buildResult = runTasks('dependencies')

        then:
        buildResult.success
        buildResult.failure == null
        buildResult instanceof LauncherExecutionResult
        ((LauncherExecutionResult) buildResult).gradle != null

        cleanup:
        useToolingApi = true
    }

    def 'runs failed build with Launcher'() {
        when:
        useToolingApi = false
        logLevel = LogLevel.DEBUG
        ExecutionResult buildResult = runTasks('unknown')

        then:
        !buildResult.success
        buildResult.failure
        buildResult instanceof LauncherExecutionResult
        ((LauncherExecutionResult) buildResult).gradle != null

        cleanup:
        useToolingApi = true
    }

    def 'setup and run build'() {
        buildFile << '''
            apply plugin: 'java'
        '''.stripIndent()

        when:
        writeHelloWorld('nebula.test.hello')

        then:
        fileExists('src/main/java/nebula/test/hello/HelloWorld.java')

        when:
        def result = runTasksSuccessfully('build', '--info')

        then:
        fileExists('build/classes/main/nebula/test/hello/HelloWorld.class')
        result.wasExecuted(':compileTestJava')
        result.getStandardOutput().contains('Skipping task \':compileTestJava\' as it has no source files.')
    }


    @Unroll
    def 'can import from classpath using #desc #testTooling'(String desc, boolean testTooling) {
        useToolingApi = testTooling

        buildFile << '''
            import nebula.test.FakePlugin
            apply plugin: FakePlugin
        '''.stripIndent()

        when:
        runTasksSuccessfully('tasks')

        then:
        noExceptionThrown()

        where:
        desc       | testTooling
        "Tooling"  | true
        "Launcher" | false
    }
}

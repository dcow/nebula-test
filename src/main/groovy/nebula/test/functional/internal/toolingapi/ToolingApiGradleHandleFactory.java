package nebula.test.functional.internal.toolingapi;

import nebula.test.functional.internal.GradleHandle;
import nebula.test.functional.internal.GradleHandleFactory;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;
import java.util.List;

public class ToolingApiGradleHandleFactory implements GradleHandleFactory {

    public GradleHandle start(File directory, List<String> arguments) {
        GradleConnector connector = GradleConnector.newConnector();
        connector.forProjectDirectory(directory);
        ProjectConnection connection = connector.connect();
        BuildLauncher launcher = connection.newBuild();
        // TODO Deal with connection.close()
        String[] argumentArray = new String[arguments.size()];
        arguments.toArray(argumentArray);
        launcher.withArguments(argumentArray);
        return new BuildLauncherBackedGradleHandle(launcher);
    }
}
package net.tetratau.tokimak.core;

import net.tetratau.toki.installer.Installer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class TokimakCorePlugin implements Plugin<Project> {
    private static final String API_LINK = "https://api.papermc.io/v2/projects/paper/versions/%s/builds/%s/downloads/paper-%s-%s.jar";

    @Override
    public void apply(Project target) {
        target.getConfigurations().create("paperclip");

        final Provider<Directory> cache = target.getLayout().dir(target.provider(() -> new File(".gradle/tokimak-caches")));
        final Provider<Directory> buildOutput = target.getLayout().dir(target.provider(() -> new File("build/libs")));
        try {
            Files.createDirectories(cache.get().getAsFile().toPath());
            Files.createDirectories(buildOutput.get().getAsFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        target.getTasks().register("createPaperclipJar", task -> {
            Project project = task.getProject();
            String paperBuildNumber = (String) project.getProperties().get("tokimak.build");
            String paperBuildVersion = (String) project.getProperties().get("tokimak.gameVersion");
            if (paperBuildNumber == null)
                throw new IllegalStateException("No tokimak.build property defined");
            if (paperBuildVersion == null)
                throw new IllegalStateException("No tokimak.gameVersion property defined");

            final File rawTokiPaperclipJar = project.getConfigurations().named("paperclip").get().getSingleFile();
            final File tokiPaperclip = cache.get().file("toki-server-" + paperBuildVersion + "-" + paperBuildNumber + ".jar").getAsFile();
            try {
                Files.copy(rawTokiPaperclipJar.toPath(), tokiPaperclip.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Couldn't copy raw paperclip jar to caches to build the toki paperclip.", e);
            }
            final File paperPaperclip = cache.get().file("paperPaperclip.jar").getAsFile();
            try {
                TokimakCorePlugin.downloadFile(new URL(String.format(API_LINK, paperBuildVersion, paperBuildNumber, paperBuildVersion, paperBuildNumber)), paperPaperclip.toPath());
            } catch (IOException e) {
                throw new RuntimeException("Couldn't download paperclip jar from the Paper API: ", e);
            }
            Installer.transferPatchInfo(paperPaperclip.toPath(), tokiPaperclip.toPath(), paperBuildVersion);
            try {
                Files.copy(tokiPaperclip.toPath(), buildOutput.get().file(tokiPaperclip.getName()).getAsFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Couldn't copy toki jar to the build/libs folder", e);
            }
        });
    }

    public static void downloadFile(URL url, Path path) throws IOException {
        try (InputStream in = openUrl(url)) {
            Files.createDirectories(path.getParent());
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Throwable t) {
            try {
                Files.deleteIfExists(path);
            } catch (Throwable t2) {
                t.addSuppressed(t2);
            }

            throw t;
        }
    }

    private static final int HTTP_TIMEOUT_MS = 8000;

    private static InputStream openUrl(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(HTTP_TIMEOUT_MS);
        conn.setReadTimeout(HTTP_TIMEOUT_MS);
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) throw new IOException("HTTP request to "+url+" failed: "+responseCode);

        return conn.getInputStream();
    }
}

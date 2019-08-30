package top.wboost.common.boost.handler.compiler;

import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.jar.Handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * @Auther: jwsun
 * @Date: 2019/6/21 16:49
 */
public class SpringBootResolver {

    Archive archive;
    List<Archive> archives;
    JarLauncherResolver jarLauncher;

    public SpringBootResolver() throws Exception {
        this.archive = createArchive();
        this.jarLauncher = new JarLauncherResolver();
        this.archives = new ArrayList<>(this.archive.getNestedArchives(jarLauncher::isNestedArchive));
    }

    public static Archive createArchive() throws Exception {
        return new JarFileArchive(new File(SpringBootResolver.class.getClassLoader().getResource("").toString().replace("jar:file:","").replaceAll("!.*","")));
    }

    public InputStream getInputStream(String className) throws IOException {
        return getClassURL(className).openConnection().getInputStream();
    }

    public URL getClassURL(String className) {
        URL urlFind = null;
        try {

            int lastDot = className.lastIndexOf('.');
            if (lastDot > 0) {
                String packageName = className.substring(0, lastDot);
                urlFind = AccessController.doPrivileged((PrivilegedExceptionAction<URL>) () -> {
                    String packageEntryName = packageName.replace('.', '/') + "/";
                    String classEntryName = className.replace('.', '/') + ".class";
                    for (Archive archive : archives) {
                        try {
                            URLConnection connection = archive.getUrl().openConnection();
                            if (connection instanceof JarURLConnection) {
                                JarFile jarFile = ((JarURLConnection) connection)
                                        .getJarFile();
                                if (jarFile.getEntry(classEntryName) != null
                                        && jarFile.getEntry(packageEntryName) != null
                                        && jarFile.getManifest() != null) {
                                    return new URL("jar", "", -1, jarFile.toString() + "!/" + classEntryName, new Handler(new Handler().getRootJarFileFromUrl(archive.getUrl())));
                                }
                            }
                        } catch (IOException ex) {
                            // Ignore
                        }
                    }
                    return null;
                },AccessController.getContext());
            }

        } catch (Exception e) {
            // ignore
        }
        return urlFind;
}

    public String getClassURLPath(String className) {
        return getClassURL(className).getPath();
    }

    class JarLauncherResolver {
        static final String BOOT_INF_CLASSES = "BOOT-INF/classes/";

        static final String BOOT_INF_LIB = "BOOT-INF/lib/";
        protected boolean isNestedArchive(Archive.Entry entry) {
            if (entry.isDirectory()) {
                return entry.getName().equals(BOOT_INF_CLASSES);
            }
            return entry.getName().startsWith(BOOT_INF_LIB);
        }
    }

}

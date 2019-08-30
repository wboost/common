package top.wboost.common.boost.handler.compiler;

import javassist.ClassPath;
import javassist.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public final class SpringBootClassPath implements ClassPath {
    SpringBootResolver springBootResolver;

    public SpringBootClassPath() throws Exception {
        this.springBootResolver = new SpringBootResolver();
    }

    public InputStream openClassfile(String classname)
            throws NotFoundException {
        try {
            return this.springBootResolver.getInputStream(classname);
        } catch (IOException e) {
            e.printStackTrace();
            throw new NotFoundException("broken jar file?: "
                    + this.springBootResolver);
        }
    }

    public URL find(String classname) {
        return this.springBootResolver.getClassURL(classname);
    }

    public SpringBootResolver getSpringBootResolver() {
        return springBootResolver;
    }

    @Override
    public void close() {

    }

}
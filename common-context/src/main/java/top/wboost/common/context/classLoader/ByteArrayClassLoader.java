package top.wboost.common.context.classLoader;

public class ByteArrayClassLoader extends ClassLoader {

    public ByteArrayClassLoader() {
        super();
    }

    public Class<?> defineClass(String name, byte[] data) {
        return defineClass(name, data, 0, data.length);
    }

}
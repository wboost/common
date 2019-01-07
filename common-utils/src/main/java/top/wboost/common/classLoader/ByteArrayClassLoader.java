package top.wboost.common.classLoader;

public class ByteArrayClassLoader extends ClassLoader {

    public ByteArrayClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }

    public Class<?> defineClass(String name, byte[] data) {
        return defineClass(name, data, 0, data.length);
    }

}
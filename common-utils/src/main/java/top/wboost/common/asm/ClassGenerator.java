package top.wboost.common.asm;

import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import top.wboost.common.classLoader.ByteArrayClassLoader;

import java.io.IOException;

import static org.springframework.asm.Opcodes.*;

/**
 * ASM创建类
 * <pre>
 *     ClassGenerator classGenerator = new ClassGenerator();
 *     classGenerator.setName("ModelTest");
 *     classGenerator.addFieldGenerator(new ClassGenerator.FieldGenerator("data", ModelTest.class));
 *     classGenerator.addFieldGenerator(new ClassGenerator.FieldGenerator("info", Map.class));
 *     classGenerator.addFieldGenerator(new ClassGenerator.FieldGenerator("status", Integer.class));
 *     classGenerator.addFieldGenerator(new ClassGenerator.FieldGenerator("validate", Boolean.class));
 *     Class<?> aClass = generatorClass(classGenerator);
 * </pre>
 * @author jwSun
 * @date 2019/1/2
 */
public class ClassGenerator {

    private static final String JAVA_LANG_OBJECT = "java/lang/Object";
    private static final String INIT = "<init>";
    private static ByteArrayClassLoader classLoader = new ByteArrayClassLoader();

    /**
     * 创建一个类
     * @param classGenerator
     * @return
     */
    public static Class<?> generatorClass(ClassGeneratorEntity classGenerator) throws IOException {
        ClassWriter cw;
        if (classGenerator.getVisitClass() != null) {
            throw new RuntimeException("method not implement");
            /*try {
                Class.forName(classGenerator.getVisitClass().getName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            ClassReader classReader = new ClassReader(classGenerator.getVisitClass().getName());
            cw = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);*/
        } else {
            cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            visitDefaultConstructor(cw);
        }
        cw.visit(Opcodes.V1_6, ACC_PUBLIC + ACC_SUPER, classGenerator.getGeneratorName(), null, JAVA_LANG_OBJECT,
                null);
        visitAddFiled(cw,classGenerator);
        visitGetMethod(cw,classGenerator);
        visitSetMethod(cw, classGenerator);
        cw.visitEnd();
        return classLoader.defineClass(classGenerator.getName(), cw.toByteArray());
    }

    /**
     * 创建构造器
     * @param cw
     */
    public static void visitDefaultConstructor(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, INIT, "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, JAVA_LANG_OBJECT, INIT, "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    /**
     * 创建属性
     * @param cw
     * @param classGenerator
     */
    public static void visitAddFiled(ClassWriter cw,ClassGeneratorEntity classGenerator) {
        classGenerator.getFieldGenerators().forEach(
                fieldGenerator -> cw.visitField(
                        ACC_PRIVATE,
                        fieldGenerator.getName(),
                        Type.getType(fieldGenerator.getClazz()).getDescriptor(),
                        null,
                        null).visitEnd());
    }

    /**
     * 创建get方法
     * @param cw
     * @param classGenerator
     */
    public static void visitGetMethod(ClassWriter cw,ClassGeneratorEntity classGenerator) {
        classGenerator.getFieldGenerators().forEach(fieldGenerator -> {
            String methodName = "get" + String.valueOf(Character.toUpperCase(fieldGenerator.getName().charAt(0))) + fieldGenerator.getName().substring(1);
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, "()" + fieldGenerator.getDescriptor(), null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, classGenerator.getGeneratorName(), fieldGenerator.getName(), fieldGenerator.getDescriptor());
            mv.visitInsn(ARETURN);
            mv.visitMaxs(0, 1);
            mv.visitEnd();
        });
    }

    /**
     * 创建set方法
     * @param cw
     * @param classGenerator
     */
    public static void visitSetMethod(ClassWriter cw,ClassGeneratorEntity classGenerator) {
        classGenerator.getFieldGenerators().forEach(fieldGenerator -> {
            String methodName = "set" + String.valueOf(Character.toUpperCase(fieldGenerator.getName().charAt(0))) + fieldGenerator.getName().substring(1);
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, "(" + fieldGenerator.getDescriptor() + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            // set 入参
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, classGenerator.getGeneratorName(), fieldGenerator.getName(), fieldGenerator.getDescriptor());
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 2);
            mv.visitEnd();
        });
    }


}

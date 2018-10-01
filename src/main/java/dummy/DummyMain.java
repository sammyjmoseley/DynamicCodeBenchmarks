package dummy;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import utils.DynamicClassLoader;

import java.lang.reflect.InvocationTargetException;

import static org.objectweb.asm.Opcodes.*;

public class DummyMain {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        ClassWriter classVisitor = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
        };

        classVisitor.visit(V1_8, ACC_PUBLIC, "Hello", null, "java/lang/Object",
                new String[]{"dummy/DummyInterface"});

        MethodVisitor constructor = classVisitor.visitMethod(ACC_PUBLIC, "<init>",
                        "()V", null, null);

        constructor.visitCode();
        //super()
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL,
                "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);

        constructor.visitMaxs(0, 0);
        constructor.visitEnd();

        MethodVisitor getValueMethod = classVisitor.visitMethod(ACC_PUBLIC,
                "getValue", "()I", null, null);
        getValueMethod.visitCode();
        getValueMethod.visitIntInsn(SIPUSH, 2);
        getValueMethod.visitInsn(IRETURN);
        getValueMethod.visitMaxs(1, 2);
        getValueMethod.visitEnd();

        DynamicClassLoader classLoader = new DynamicClassLoader();
        Class helloClass = classLoader.defineClass("Hello", classVisitor.toByteArray());

        DummyInterface obj = (DummyInterface) helloClass.getConstructor().newInstance();
        System.out.println(obj.getValue());

    }
}

package operator;

import utils.ASMMethodSignatureBuilder;
import utils.ASMUtils;
import utils.DynamicClassLoader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import utils.MaxStackAcum;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.IRETURN;

public class OperatorMain {
    private static Random random = new Random();
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        AdditionOperator oldOp = new AdditionOperator(
                new AdditionOperator(
                        new GetValueOperator(0),
                        new GetValueOperator(0)),
                new AdditionOperator(
                        new GetValueOperator(1),
                        new GetValueOperator(1)));
        Class<Operator> clazz = convertOperator(oldOp);
        Operator newOp = clazz.getConstructor().newInstance();

        int[][] values = new int[100000000][2];
        int[][] results = new int[3][values.length];
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[0].length; j++) {
                values[i][j] = random.nextInt(10000);
            }
        }

        computeWithOp(oldOp, values, results[0]);
        computeWithOp(newOp, values, results[1]);
        computeStatically(values, results[2]);
        System.out.println("Correct results: " + checkCorrectResult(results));

        long time = System.currentTimeMillis();
        computeWithOp(oldOp, values, results[0]);
        System.out.println("Old-Op: " + (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();
        computeWithOp(newOp, values, results[1]);
        System.out.println("New-Op: " + (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();
        computeStatically(values, results[2]);
        System.out.println("Cpt-static: " + (System.currentTimeMillis() - time));

        System.out.println("Correct results: " + checkCorrectResult(results));
    }

    private static void computeWithOp(Operator op, int[][] values, int[] result) {
        for (int i = 0; i < values.length; i++) {
            result[i] = op.compute(values[i]);
        }
    }

    private static void computeStatically(int[][] values, int[] result) {
        for (int i = 0; i < values.length; i++) {
            result[i] = values[i][0] + values[i][0] + values[i][1] + values[i][1];
        }
    }

    private static boolean checkCorrectResult(int[][] results) {
        for (int i = 0; i < results.length - 1; i++) {
            for (int j = 0; j < results[0].length; j++) {
                if (results[i][j] != results[i + 1][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    private static Class<Operator> convertOperator(Operator op) {
        ClassWriter classVisitor = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
        };

        classVisitor.visit(V1_8, ACC_PUBLIC, "Hello", null, "java/lang/Object",
                new String[]{"operator/Operator"});

        MethodVisitor constructor = classVisitor.visitMethod(ACC_PUBLIC, "<init>",
                "()V", null, null);

        constructor.visitCode();
        //super()
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL,
                ASMUtils.getFullyQualifiedName(Object.class), "<init>", "()V", false);
        constructor.visitInsn(RETURN);

        constructor.visitMaxs(0, 0);
        constructor.visitEnd();

        ASMMethodSignatureBuilder asmMethodSignatureBuilder = new ASMMethodSignatureBuilder();
        asmMethodSignatureBuilder.addParam(int[].class)
                .addRet(int.class);
        MethodVisitor computeMethod = classVisitor.visitMethod(ACC_PUBLIC,
                "compute", asmMethodSignatureBuilder.toString(), null, null);
        MaxStackAcum maxStackAcum = new MaxStackAcum();
        computeMethod.visitCode();
        op.convertCode(computeMethod, maxStackAcum);
        computeMethod.visitInsn(IRETURN);
        maxStackAcum.stackInc(-1);
        computeMethod.visitMaxs(1, maxStackAcum.getMaxStack());
        computeMethod.visitEnd();

        DynamicClassLoader classLoader = new DynamicClassLoader();
        Class<Operator> helloClass = classLoader.defineClass("Hello", classVisitor.toByteArray());
        return helloClass;

    }
}

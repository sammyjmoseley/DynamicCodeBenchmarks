package operator;

import org.objectweb.asm.MethodVisitor;
import utils.ASMMethodSignatureBuilder;
import utils.ASMUtils;
import utils.MaxStackAcum;

import static org.objectweb.asm.Opcodes.*;

public class AdditionOperator implements Operator {
    private Operator l, r;
    public AdditionOperator(Operator l, Operator r) {
        this.l = l;
        this.r = r;
    }

    public int compute(int[] row) {
        return l.compute(row) + r.compute(row);
    }

    public void convertCode(MethodVisitor mv, MaxStackAcum stackAcum) {
//        mv.visitVarInsn(ALOAD, 0);
//        mv.visitFieldInsn(GETFIELD, ASMUtils.getFullyQualifiedName(this.getClass()), "l",
//                ASMUtils.getTypeName(this.l.getClass()));
//        mv.visitVarInsn(ALOAD, 1);
//        stackAcum.stackInc(2);
//        mv.visitMethodInsn(INVOKEDYNAMIC, ASMUtils.getFullyQualifiedName(this.getClass()), "compute",
//                new ASMMethodSignatureBuilder().addParam(int[].class).addRet(int.class).toString(), false);
//        stackAcum.stackInc(-1);
        l.convertCode(mv, stackAcum);
        r.convertCode(mv, stackAcum);
        mv.visitInsn(IADD);
        stackAcum.stackInc(-1);
    }
}

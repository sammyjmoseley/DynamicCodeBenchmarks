package operator;

import org.objectweb.asm.MethodVisitor;
import utils.MaxStackAcum;

import static org.objectweb.asm.Opcodes.*;

public class GetValueOperator implements Operator {
    private int index;

    public GetValueOperator(int index) {
        this.index = index;
    }

    public int compute(int[] row) {
        return row[index];
    }

    public void convertCode(MethodVisitor mv, MaxStackAcum stackAcum) {
        mv.visitVarInsn(ALOAD, 1);
        mv.visitIntInsn(SIPUSH, index);
        stackAcum.stackInc(2);
        mv.visitInsn(IALOAD);
        stackAcum.stackInc(-1);
    }
}

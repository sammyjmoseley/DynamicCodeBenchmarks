package operator;

import org.objectweb.asm.MethodVisitor;
import utils.MaxStackAcum;

public interface Operator {
    int compute(int[] row);
    void convertCode(MethodVisitor mv, MaxStackAcum stackAcum);
}

package utils;

import java.util.ArrayList;
import java.util.List;

public class ASMMethodSignatureBuilder {
    List<Class<?>> params;
    List<Class<?>> rets;

    public ASMMethodSignatureBuilder() {
        this.params = new ArrayList<>();
        this.rets = new ArrayList<>();
    }

    public ASMMethodSignatureBuilder addParam(Class<?> clazz) {
        this.params.add(clazz);
        return this;
    }

    public ASMMethodSignatureBuilder addRet(Class<?> clazz) {
        this.rets.add(clazz);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");

        for (Class clazz : params) {
            sb.append(ASMUtils.getTypeName(clazz));
        }

        sb.append(")");

        for (Class clazz : rets) {
            sb.append(ASMUtils.getTypeName(clazz));
        }

        return sb.toString();
    }
}

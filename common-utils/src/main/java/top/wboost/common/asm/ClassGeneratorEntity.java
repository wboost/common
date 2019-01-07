package top.wboost.common.asm;

import lombok.Data;
import org.springframework.asm.Type;

import java.util.ArrayList;
import java.util.List;

@Data
public  class ClassGeneratorEntity {
    /**top.asm.Test**/
    private String name;
    private Class<?> visitClass;
    private List<FieldGenerator> fieldGenerators = new ArrayList<>();
    private List<MethodGenerator> methodGenerators = new ArrayList<>();

    public String getGeneratorName() {
        return this.name.replaceAll("\\.", "/");
    }

    public ClassGeneratorEntity addFieldGenerator(FieldGenerator fieldGenerator) {
        this.fieldGenerators.add(fieldGenerator);
        return this;
    }

    @Data
    public static class FieldGenerator {
        private String name;
        private Class<?> clazz;

        public FieldGenerator(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public FieldGenerator() {
        }

        public String getDescriptor() {
            return Type.getType(this.getClazz()).getDescriptor();
        }

    }

    @Data
    public static class MethodGenerator {
        private String name;
        private Class<?> clazz;
    }
}
package br.ufma.ecp;

public class VMWriter {
    private StringBuilder vmOutput = new StringBuilder();

    // seguimentos de memoria
    enum Segment {
        CONST("constant"),
        ARG("argument"),
        LOCAL("local"),
        STATIC("static"),
        THIS("this"),
        THAT("that"),
        POINTER("pointer"),
        TEMP("temp");

        private Segment(String value) {
            this.value = value;
        }

        public String value;
    };

    // comandos que a vm interpreta
    enum Command {
        ADD,
        SUB,
        NEG,
        EQ,
        GT,
        LT,
        AND,
        OR,
        NOT
    };

    // mostrando a string construída
    public String vmOutput() {
        return vmOutput.toString();
    }

    // empilha
    void writePush(Segment segment, int index) {
        vmOutput.append(String.format("push %s %d\n", segment.value, index));
    }

    // desempilha
    void writePop(Segment segment, int index) {
        vmOutput.append(String.format("pop %s %d\n", segment.value, index));
    }

    // comandos aritméticos
    void writeArithmetic(Command command) {
        vmOutput.append(String.format("%s\n", command.name().toLowerCase()));
    }

    // definindo o label, pra saber pra onde ir após um go-to
    void writeLabel(String label) {
        vmOutput.append(String.format("label %s\n", label));
    }

    // go-to
    void writeGoto(String label) {
        vmOutput.append(String.format("goto %s\n", label));
    }

    // if
    void writeIf(String label) {
        vmOutput.append(String.format("if-goto %s\n", label));
    }

    // chamada de função
    void writeCall(String name, int nArgs) {
        vmOutput.append(String.format("call %s %d\n", name, nArgs));
    }

    // definindo uma função, exemplo: function Main.main 0
    void writeFunction(String name, int nLocals) {
        vmOutput.append(String.format("function %s %d\n", name, nLocals));
    }

    // return
    void writeReturn() {
        vmOutput.append(String.format("return\n"));
    }

}

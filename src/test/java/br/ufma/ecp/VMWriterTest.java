package br.ufma.ecp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.ufma.ecp.VMWriter.Command;
import br.ufma.ecp.VMWriter.Segment;

public class VMWriterTest {
    @Test
    public void writePushTest() {
        VMWriter vm = new VMWriter();
        vm.writePush(Segment.LOCAL, 2);
        String actual = vm.vmOutput();
        String expected = """
                push local 2
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void writePopTest() {
        VMWriter vm = new VMWriter();
        vm.writePop(Segment.CONST, 2);
        String actual = vm.vmOutput();
        String expected = """
                pop constant 2
                """;
        assertEquals(expected, actual);
    }

    @Test
    public void writeArithmeticTest() {
        VMWriter vm = new VMWriter();
        vm.writeArithmetic(Command.NEG);
        String actual = vm.vmOutput();
        String expected = """
                neg
                """;
        assertEquals(expected, actual);
    }

}

package br.ufma.ecp;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class GeneratorCodeTest {

    @Test
    public void arrayTest() {
        var input = """
                class Main {
                    function void main () {
                        var Array v;
                        let v = Array.new (10);
                        let v[2] = v[3] + 42;
                        return;
                    }
                }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();
        String actual = parser.VMOutput();
        String expected = """
                function Main.main 1
                push constant 10
                call Array.new 1
                pop local 0
                push constant 2
                push local 0
                add
                push constant 3
                push local 0
                add
                pop pointer 1
                push that 0
                push constant 42
                add
                pop temp 0
                pop pointer 1
                push temp 0
                pop that 0
                push constant 0
                return
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void ifTest() {
        var input = """
                class Main {
                    function void main () {
                        var int sum, i;
                        let i = 0;
                        if (i < 10) {
                            let sum = 42;
                        }
                        return;
                    }
                }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();
        String actual = parser.VMOutput();
        String expected = """
                function Main.main 2
                push constant 0
                pop local 1
                push local 1
                push constant 10
                lt
                if-goto IF_TRUE0
                goto IF_FALSE0
                label IF_TRUE0
                push constant 42
                pop local 0
                label IF_FALSE0
                push constant 0
                return
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void ifElseTest() {
        var input = """
                class Main {
                    function void main () {
                        var int sum, i;
                        let i = 0;
                        if (i < 10) {
                            let sum = 42;
                        } else {
                            let sum = 35;
                        }
                        return;
                    }
                }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();
        String actual = parser.VMOutput();
        String expected = """
                function Main.main 2
                push constant 0
                pop local 1
                push local 1
                push constant 10
                lt
                if-goto IF_TRUE0
                goto IF_FALSE0
                label IF_TRUE0
                push constant 42
                pop local 0
                goto IF_END0
                label IF_FALSE0
                push constant 35
                pop local 0
                label IF_END0
                push constant 0
                return
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void whileTest() {
        var input = """
                class Main {
                    function void main () {
                        var int sum, i;
                        let i = 0;
                        let sum = 0;
                        while (i < 10) {
                            let sum = sum + i;
                        }
                        return;
                    }
                }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();
        String actual = parser.VMOutput();
        String expected = """
                function Main.main 2
                push constant 0
                pop local 1
                push constant 0
                pop local 0
                label WHILE_EXP0
                push local 1
                push constant 10
                lt
                not
                if-goto WHILE_END0
                push local 0
                push local 1
                add
                pop local 0
                goto WHILE_EXP0
                label WHILE_END0
                push constant 0
                return
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void operatorTest() {
        var input = """
                class Main {
                    function void main () {
                        do Output.printInt (10+20-60*4/2);
                        return;
                    }
                }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();
        String actual = parser.VMOutput();
        String expected = """
                function Main.main 0
                push constant 10
                push constant 20
                add
                push constant 60
                sub
                push constant 4
                call Math.multiply 2
                push constant 2
                call Math.divide 2
                call Output.printInt 1
                pop temp 0
                push constant 0
                return
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void helloTest() {
        var input = """
                class Main {
                    function void main () {
                        do Output.printString ("Ola!");
                        return;
                    }
                }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();
        String actual = parser.VMOutput();
        String expected = """
                function Main.main 0
                push constant 4
                call String.new 1
                push constant 79
                call String.appendChar 2
                push constant 108
                call String.appendChar 2
                push constant 97
                call String.appendChar 2
                push constant 33
                call String.appendChar 2
                call Output.printString 1
                pop temp 0
                push constant 0
                return
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void methodsConstructorTest() {
        var input = """
                class Point {
                    field int x, y;

                    method int getX () {
                        return x;
                    }

                    method int getY () {
                        return y;
                    }

                    method void print () {
                        do Output.printInt(getX());
                        do Output.printInt(getY());
                        return;
                    }

                    constructor Point new(int Ax, int Ay) {
                      var int w;
                      let x = Ax;
                      let y = Ay;
                      let w = 42;
                      let x = w;
                      return this;
                   }
                  }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();
        String actual = parser.VMOutput();
        String expected = """
                function Point.getX 0
                push argument 0
                pop pointer 0
                push this 0
                return
                function Point.getY 0
                push argument 0
                pop pointer 0
                push this 1
                return
                function Point.print 0
                push argument 0
                pop pointer 0
                push pointer 0
                call Point.getX 1
                call Output.printInt 1
                pop temp 0
                push pointer 0
                call Point.getY 1
                call Output.printInt 1
                pop temp 0
                push constant 0
                return
                function Point.new 1
                push constant 2
                call Memory.alloc 1
                pop pointer 0
                push argument 0
                pop this 0
                push argument 1
                pop this 1
                push constant 42
                pop local 0
                push local 0
                pop this 0
                push pointer 0
                return
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void methodTest() {
        var input = """
                class Main {
                    function void main () {
                        var Point p;
                        var int x;
                        let p = Point.new (10, 20);
                        let x = p.getX();
                        return;
                    }
                }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();
        String actual = parser.VMOutput();
        String expected = """
                function Main.main 2
                push constant 10
                push constant 20
                call Point.new 2
                pop local 0
                push local 0
                call Point.getX 1
                pop local 1
                push constant 0
                return
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void doStatement() {
        var input = """
                class Main {
                    function void main () {
                        var int x;
                        let x = 10;
                        do Output.printInt(x);
                        return;
                    }
                }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();
        String actual = parser.VMOutput();
        String expected = """
                function Main.main 1
                push constant 10
                pop local 0
                push local 0
                call Output.printInt 1
                pop temp 0
                push constant 0
                return
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void termExpressionLiteralKeyword() {
        var input = """
                class Main {
                    function void main () {
                        var bool x;
                        let x = true;
                        let x = false;
                        let x = null;
                        return;
                    }
                }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();
        String actual = parser.VMOutput();
        String expected = """
                function Main.main 1
                push constant 0
                not
                pop local 0
                push constant 0
                pop local 0
                push constant 0
                pop local 0
                push constant 0
                return
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void termExpressionVar() {
        var input = """
                class Main {

                  function void main () {
                      var int x, y;
                      let x = 42;
                      let y = x;
                  }
                }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();
        String actual = parser.VMOutput();
        String expected = """
                function Main.main 2
                push constant 42
                pop local 0
                push local 0
                pop local 1
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void termExpressionLiteral() {
        var input = """
                class Main {

                  function void main () {
                      var int x;
                      let x = 42;
                      return;
                  }
                }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();
        String actual = parser.VMOutput();
        String expected = """
                function Main.main 1
                push constant 42
                pop local 0
                push constant 0
                return
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void termExpression() {
        var input = """
                class Point {
                  field int x, y;
                  constructor Point new(int Ax, int Ay) {
                    var int w;
                    let x = Ax;
                    let y = Ay;
                    let w = 42;
                    let x = w;
                    return this;
                 }
                }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();
        String actual = parser.VMOutput();
        String expected = """
                function Point.new 1
                push constant 2
                call Memory.alloc 1
                pop pointer 0
                push argument 0
                pop this 0
                push argument 1
                pop this 1
                push constant 42
                pop local 0
                push local 0
                pop this 0
                push pointer 0
                return
                    """;
        assertEquals(expected, actual);
    }

    @Test
    public void writeFunctionTest() {

        var input = """
                class Main {
                    function int soma (int x, int y) {
                           return  x + y;
                    }

                    function void main () {
                           var int d;
                           let d = Main.soma(4,5);
                           return;
                     }

                   }
                """;
        ;
        var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
        parser.parser();

        String actual = parser.VMOutput();
        String expected = """
                function Main.soma 0
                push argument 0
                push argument 1
                add
                return
                function Main.main 1
                push constant 4
                push constant 5
                call Main.soma 2
                pop local 0
                push constant 0
                return
                    """;
        assertEquals(expected, actual);

    }

}

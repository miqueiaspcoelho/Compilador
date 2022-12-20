# Compilador Jack (jack compiler)

Este projeto foi desenvolvido durante o semestre 2022.2 da disciplina
de Compiladores pela Universidade Federal do Maranhão(UFMA).

This project was developed during the 2022.2 semester of the discipline
of Compilers by the Federal University of Maranhão (UFMA).

# Objetivo (objective)
O objetivo do projeto é construir um compilador funcional para
a linguagem Jack, desenvolvida por Shimon Schocken e Noam Nisan.

The goal of the project is to build a working compiler for
the Jack language, developed by Shimon Schocken and Noam Nisan.

# Etapas (steps)
O projeto aplicado na disciplina de compiladores UFMA, teve como foco
entender a parte do software, abstraindo a construção do computador em si.

The project applied in the UFMA compiler discipline focused on
understand the software part, abstracting the construction of the computer itself.

## I-Scanner 
Aqui foi feita a construção do analisador léxico da linguagem Jack, ou seja,
temos o responsável por indicar quais os termos permitidos na linguagem,
bem como classificá-los de acordo com as seguintes categorias:

Here, the construction of the lexical analyzer of the Jack language was carried out, that is,
we are responsible for indicating which terms are allowed in the language,
as well as classify them according to the following categories:

- Inteiros (Integers);
- Strings;
- Palavras-chave (Keywords);
- Identificadores (Identifiers);
- Símbolos (Symbols).

O funcionamento do Scanner é similar a um autômato deterministico, onde
certos caracteres vão determinando estados predefinidos da máquina.

The operation of the Scanner is similar to a deterministic automaton, where
certain characters determine predefined states of the machine.

## II-Parser
Nesta etapa temos o analisador sintático da linguagem. Determina como e o que
pode ser escrito, quais construções de "frases" fazem sentido para a linguagem Jack. A [gramática](https://www.coursera.org/lecture/nand2tetris2/unit-4-6-the-jack-grammar-NhXZS) 
de fato.

In this step we have the parser of the language. 
It determines how and what can be written, which "sentence" 
constructions make sense for the Jack language. 
The actual [grammar](https://www.coursera.org/lecture/nand2tetris2/unit-4-6-the-jack-grammar-NhXZS).

## III-Geração de código intermediário (intermediate code generation)
É gerado um código em linguagem intermediária, parecido com bytecode.

A code is generated in an intermediate language, similar to bytecode.

## IV-Geração do HACK assembly (HACK assembly generation)
O HACK assembly é uma assembly mais simples, está parte está em um [repositório](https://github.com/miqueiaspcoelho/compilador-vmtranslator) extra.

The HACK assembly is a simpler assembly, this part is in an [extra repository](https://github.com/miqueiaspcoelho/compilador-vmtranslator).

## V-Geração de código binário (Binary code generation)
Esta etapa não foi implementada devido a brevidade do semestre.

This step was not implemented due to the brevity of the semester.

# Testes (tests)
Foram feitos testes de unidade, assim  como testes utilizando as próprias
tools do projeto [nand2tetris2](https://www.nand2tetris.org/):

Unit tests were done, as well as tests using the [nand2tetris2 project's](https://www.nand2tetris.org/) own tools:
- junit;
- vm emulator;
- cpu emulator.
## Stack utilizada (stack used)

**Back-end:** Java.


## Melhorias (improvements)

Construção do código da parte final, ou seja, tradução do HACK assembly
para o executável (código de máquina).

Construction of the final part code, i.e. translation of the HACK assembly
to the executable (machine code).


## Agradecimentos (thanks)
Professor Sergio Costa - Universidade Federal do Maranhão (UFMA), 
que gentilmente disponibilizou os códigos fonte para que 
nós, enquanto alunos, pudessemos estudar e também ir fazendo as modificações
ou acréscimos que fossem necessários.

Professor Sergio Costa - Universidade Federal do Maranhão (UFMA),
 who kindly made the source codes available so that we, as students, 
 could study and also make the modifications or additions 
 that were necessary.
package br.ufma.ecp;

import static br.ufma.ecp.token.TokenType.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
public class App 
{

    private static String fromFile() {
        File file = new File("Main.jack");

        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
            String textoDoArquivo = new String(bytes, "UTF-8");
            return textoDoArquivo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    } 

    public static void main( String[] args )
    {

        
        String input = """
                class Main {
                   field int x,y;
                   static boolean b; 
                }
                """;
        Parser p = new Parser(input.getBytes());
        p.parser();
        System.out.println(p.XMLOutput());

        
        //String input = "45 preco2 + 96";


        /*
        Scanner scan = new Scanner(fromFile().getBytes());
        System.out.println("<tokens>");        
        for (Token tk = scan.nextToken(); tk.type != EOF; tk = scan.nextToken()) {
            System.out.println(tk);
        }
        System.out.println("</tokens>");        
        */
    }
}

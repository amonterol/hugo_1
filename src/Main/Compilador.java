/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import AnalizadorLexico.AnalizadorLexico;
import AnalizadorLexico.CodigoFuente;
import AnalizadorLexico.LineaContenido;
import AnalizadorLexico.MiError;
import AnalizadorLexico.Token;
import AnalizadorSintactico.AnalizadorSintactico;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author PC
 */
public class Compilador {

    private static AnalizadorLexico lexico;
    private static AnalizadorSintactico sintactico;
    private static List<LineaContenido> contenidoFinal;
    private static List<LineaContenido> contenido;

    private static String fileName;
    private static String archivoFuente = "";

    public static void main(String[] args) throws IOException {
        /*
         public static void main(final String[] args) {
            final String nombre = "malo.HUGO";
            final String filename = nombre;
           
            final FindFile ff = new FindFile(filename, baseDir, 6);
            
            
        }
         */

        if( args.length == 0){
           JOptionPane.showMessageDialog(null, "Debe suministrar un nombre de archivo con el formato: nombreArchivo.HUGO", "Falta archivo", JOptionPane.WARNING_MESSAGE);
            System.exit(0); 
        } else if (args.length > 1) {
            //Muestra un joptionpane dialog using showMessageDialog
            JOptionPane.showMessageDialog(null, "Solo se permite un programa fuente con formato  nombreArchivo.HUGO", "Formato incorrecto", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        } else if (args.length == 1) {
            fileName = args[0].toUpperCase();
        }
         final File baseDir = new File("C:/");
        FindFile ff = new FindFile(fileName, baseDir, 6);
        final long ini = System.currentTimeMillis();
            final File f = ff.find();
            final long end = System.currentTimeMillis();
            System.out.println(f.getParent() + " " + (end - ini) + " ms");
            System.out.println(f.getName() + " " + (end - ini) + " ms");
        
        if(f.getParent() != null){
            archivoFuente = f.getParent() +"\\"+ fileName;
        } else {
             JOptionPane.showMessageDialog(null, "El program fuente no existe en esta PC", "Falta programa", JOptionPane.WARNING_MESSAGE);
            System.exit(0); 
        }
        
        
   /*    
        //El archivoFuente contiene la localizacion del programa escrito en .HUGO
        //String archivoFuente = "C:\\Program Files (x86)\\MSWLogo\\" + fileName;
        if (fileName.endsWith(".HUGO")) {
            archivoFuente = "C:\\Program Files (x86)\\MSWLogo\\" + fileName;
        } else {
            JOptionPane.showMessageDialog(null, "La extension del programa debe ser .HUGO o .hugo", "Extension incorrecta", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
*/
        if (!archivoFuente.isEmpty()) {
            contenidoFinal = new ArrayList<>();
            contenidoFinal = compilarArchivoFuente(archivoFuente, fileName);
        } else {
            System.out.println("El archivo fuente no contiene informacion");
        }

    }

    /*
    /Esta funcion es la que se encarga de llamar a las diferentes partes del compilador
    Primero, crea un objeto archivo que es el encargado de abrir el archivo desde la localizacion
    recibida desde el main.
    Luego, creamos un objeto "lexico" con el cual realizamos linea por linea> este objeto, se encarga de 
    la eliminacion de la los espacios y la creacion de la lista de tokens o tabla de simbolos, es decir, la 
    separacion en tokens segun sus tipos.
    Seguidamente, creamos un objeto "sintactico" de la clase AnalisisSintactico el cual recibe
    el objeto lexico anterior. El sintactico se encarga de realizar en analisis sintactico y semantico
    del compilador.
    El producto del analisis sintactico depende de si se encontraron o no errores. En el primer caso,
    produce un archivo denominado "nombreArchivoOriginal-Hugo-Errores.txt". En el segundo caso, es decir, 
    no encuentra errores se produce el nombreArchivo.
     */
    static List<LineaContenido> compilarArchivoFuente(String archivoFuente, String fileName) throws IOException {
        //System.out.println("crearArchivoSinErrores-EL NOMBRE DEL ARCHIVO ORIGINAL SIN LA EXTENSION ES-> " + fileName);
        //Creamos un objeto "archivo de la clase CodigoFuente pasandole como paramentro el archivo original a compilar
        CodigoFuente archivo = new CodigoFuente(archivoFuente);

        //El objeto archivo usa el metodo abrirArchivo para convertir las lineas del archivo en una lista de strings
        //la cual va quedar almacenada en el atributo "contenido" del objeto archivo
        archivo.abrirArchivo();

        // Instanciamos un objeto "lexico" de la clase AnalizadorLexico con la lista de strings 
        //almacenada en el atributo "contenido" del objeto archivo, a traves del metodo "getContenidoArchivo"
        //de la clase CodigoFuente
        lexico = new AnalizadorLexico(archivo.getContenidoArchivo());

        //Mediante el objeto "lexico" accedemos al metodo analisisLexico de la clase AnalizadorLexico
        //para realizar el analisis lexico (creacion de tokens) el cual produce una tabla de simbolos de nombre
        //"listaTokens"
        contenido = lexico.analisisLexico();

        //Creamos un objeto de nombre "sintactico" de la clase AnalizadorSintactico, para la cual le pasamos 
        //el objeto "lexico" creado anteriormeente
        sintactico = new AnalizadorSintactico(lexico, fileName);

        //El objeto "sintactico" llama al metodo "sintactico()" de la clase AnalizadorSintactico para realizar el
        //analisis sintanctico y semantico de la tabla de simbolos, en este caso la "listaTokens"
        contenidoFinal = sintactico.sintactico();
        return contenidoFinal;
    }

}

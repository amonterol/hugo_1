/*
Los “parsers” toman cada token, encuentran información sintáctica, 
y construyen un objeto llamado “Árbol de Sintaxis Abstracta”. Imagina que un ASA
es como un mapa para nuestro código 
— una forma de entender cómo es la estructura de cada pedazo de código.
 */
package AnalizadorSintactico;

import AnalizadorLexico.*;
import AnalizadorLexico.Token.Tipos;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static java.util.Objects.isNull;

/**
 *
 * @author PC
 */
public class AnalizadorSintactico {

    //Contiene la lista de tokens producida por el analizador lexico equivale a la tabla de simbolos
    private List<Token> listaTokens;
    private List<MiError> listaErrores;
    //Contiene las lineas de instruccion  y sus errores, se utiliza para confeccional el archivo nombre-HUGO-errores.txt
    private List<LineaContenido> listaContenidoFinal;
    //Contiene las lineas de instruccion sin errores, se utiliza para confeccional el archivo .lgo final
    private List<LineaContenido> listaContenidoFinalSinErrores;
    //Ambos sirve para controlar la existencia y la cantidad de errores sintacticos y semanticos en las instrucciones
    boolean existenErroresEnArchivoOriginal = false;
    int numeroErroresEnArchivoOriginal = 0;
    //Controla si estamos dentro del comando REPITE, el cual contiene su propia lista de instrucciones
    //la cual debemos aislar para su analisis y no afectar la recurrencia del  while de analisis
    boolean estamosEnRepite = false;
    //Contiene el nombre del archivo fuente
    String nombreArchivoOriginal;
    //Controla las variables declaradas por medio del comando HAZ
    ArrayList<String> variablesDeclaradas = new ArrayList<>();

    public AnalizadorSintactico(AnalizadorLexico lexico, String nombreArchivoOriginal) {
        this.listaTokens = lexico.getAuxTokens();
        this.listaErrores = lexico.getListaErrores();
        this.listaContenidoFinal = lexico.getListaContenidoFinal();
        this.listaContenidoFinalSinErrores = new ArrayList<>();
        this.nombreArchivoOriginal = nombreArchivoOriginal;
    }

    public List<LineaContenido> sintactico() throws IOException {

        //Copiamos la listaTokens en una nueva lista,porque el analisis sintactico
        //va removiendo en token en la cabeza de la lista.
        List<Token> nuevaListaTokens = new ArrayList<>();
        listaTokens.forEach((item) -> {
            nuevaListaTokens.add(item);
        });

        int m = 0;
        int lineaTknRepite = -1;

        boolean existeFin = false;
        boolean posicionFin = true;
        //Controla la existencia del comando PARA y FIN pues solo puede existir una instruccion con estos comandos
        //boolean existeParaEnPosicionCorrecta = false;
        boolean existeParaEnPosicionCorrecta = existeParaComoPrimeraInstruccion();
        boolean existeFinEnPosicionCorrecta = existeFinComoUltimaInstruccion();
        int cantidadFin = cantidadComandosFin();
        int cantidadPara = cantidadComandosPara();
        int posicionPara = 0;

        boolean existeVariableDeclarada = false;
        boolean existeCorIzqEnRepite = false;
        boolean existeCorDerEnRepite = false;
        boolean existeListaComandosEnRepite = false;
        boolean existePonColorRelleno = false;
        //Revisa  que existen tokens que analizar
        if (!listaTokens.isEmpty()) {

            //Verifica que el tamano del archivo nosupera al maximo permitido
            if (listaTokens.size() > 999) {
                System.out.println(" ERROR 101: el numero de líneas del programa excede la cantidad máxima permitida");
            }

            //Lista que contienen los errores presentes en cada linea del programa
            //utilizamos una exclusiva para el comando REPITE por cuanto este comando
            //contiene una lista de comandos HUGO, por lo cual merece un tratamiento diferente
            List<MiError> erroresEncontrados = new ArrayList<>();
            List<MiError> erroresEncontradosEnRepite = new ArrayList<>();

            //Obejto que contiene el error encontrado en la linea del programa
            //Este objeto posee un atributo con una lista de errores.
            MiError e;

            //Contiene el numero de la linea de programa que se esta procesando
            int linea = 0;

            //Objeto con el cual se accede a la linea de contenido que se esta analizando 
            LineaContenido nuevoContenido = null;

            while (!nuevaListaTokens.isEmpty()) {

                //Removemos el primer token de la lista para aplicar tecnica FIFO -> ¿sera mejor usar una cola?
                Token tknActual = nuevaListaTokens.remove(0);

                //Observa el token siguiente al actual, en este caso esta en la posicion nuevaListaTokens(0) pues vamos removiendo cada token para el analisis
                Token tknSigte = new Token();

                //Controla si los tokens analizados son parte de la lista de instrucciones del comando REPITE
                //de esta forma no creamos una nueva lista de errores encontrados, pues no es una linea nueva, sino la misma linea de REPITE
                if (tknActual.getNombre().equals("REPITE")) {
                    lineaTknRepite = tknActual.getLinea();
                }
                estamosEnRepite = tknActual.getLinea() == lineaTknRepite;
                if (estamosEnRepite) {
                    erroresEncontrados = erroresEncontradosEnRepite;

                } else {
                    erroresEncontrados = new ArrayList<>();

                }

                //DEBO VERIFICAR LA EXISTENCIA DE AMBAS PALABRAS Y EN SUS POSICIONES CORRECTAS
                existenErroresEnArchivoOriginal = false;

                //El switch toma el tokenActual y verifica el tipo, porque el analisis sintactico y semantico es llevado acabo de acuerdo al tipo de token
                switch (tknActual.getTipo().toString().trim()) {

                    case "COMANDOHUGO":

                        OUTER:
                        switch (tknActual.getNombre()) {
                            case "PARA":

                                //El primer comando debe ser PARA 
                                linea = tknActual.getLinea();

                                //Buscamos la instruccion correspondiente al token actual en el programa
                                nuevoContenido = buscarInstruccion(tknActual);

                                //Verificamos que la posicion del comando PARA sea el inicio del procedimiento, sino => nuevo error
                                if (existeParaEnPosicionCorrecta) {
                                    //Verificamos si existe mas de un comando PARA en el programa observando si la linea coincide con la ultima 
                                    if (!lineaDelComandoPara(tknActual)) {
                                        if (cantidadPara > 1) {
                                            //Existe mas de un comando PARA=> nuevo error
                                            e = new MiError(linea, " ERROR 171: solo puede existir una instruccion que comience con el comando PARA");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;
                                            --cantidadPara;

                                        } else {
                                            //Es el primer PARA
                                            //Verificamos el argumento del comando que tiene que ser un nombre de procedimiento valiso y que coincida con el nombre del programa
                                            if (!nuevaListaTokens.isEmpty()) {
                                                tknSigte = nuevaListaTokens.get(0);
                                                if (tknSigte.getLinea() == linea) {
                                                    //Como esta en la misma linea de instruccion los removemos para analizarlo
                                                    tknActual = nuevaListaTokens.remove(0);
                                                    if (!tknActual.getTipo().equals(Tipos.NOMBREPROCEDIMIENTO)) {
                                                        //El argumento de PARA es una nombre de procedimiento  NO es correcto, entonces verificamos que
                                                        e = new MiError(linea, " ERROR 141: el nombre del programa debe ser un identificador valido");
                                                        erroresEncontrados.add(e);
                                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                        existenErroresEnArchivoOriginal = true;
                                                        ++numeroErroresEnArchivoOriginal;

                                                    }
                                                    //Verificamos coincida con el nombre del archivo fuente con el nombre del procedimiento, sino => nuevo error
                                                    //Primero extraemos el nombre del archivo fuente quitandole la extension
                                                    int index = nombreArchivoOriginal.indexOf(".");
                                                    String nombreSinExtension = nombreArchivoOriginal.substring(0, index);
                                                    if (!tknActual.getNombre().equalsIgnoreCase(nombreSinExtension)) {
                                                        e = new MiError(linea, " ERROR 169: el nombre del procedimiento no coincide con el nombre del archivo fuente");
                                                        erroresEncontrados.add(e);
                                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                        existenErroresEnArchivoOriginal = true;
                                                        ++numeroErroresEnArchivoOriginal;

                                                    }
                                                }
                                            }
                                        }

                                    } else {
                                        //Es el primer PARA
                                        //Verificamos el argumento del comando que tiene que ser un nombre de procedimiento valiso y que coincida con el nombre del programa
                                        if (!nuevaListaTokens.isEmpty()) {
                                            tknSigte = nuevaListaTokens.get(0);
                                            if (tknSigte.getLinea() == linea) {
                                                //Como esta en la misma linea de instruccion los removemos para analizarlo
                                                tknActual = nuevaListaTokens.remove(0);
                                                if (!tknActual.getTipo().equals(Tipos.NOMBREPROCEDIMIENTO)) {
                                                    //El argumento de PARA es una nombre de procedimiento  NO es correcto, entonces verificamos que
                                                    e = new MiError(linea, " ERROR 141: el nombre del programa debe ser un identificador valido");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                }
                                                //Verificamos coincida con el nombre del archivo fuente con el nombre del procedimiento, sino => nuevo error
                                                //Primero extraemos el nombre del archivo fuente quitandole la extension
                                                int index = nombreArchivoOriginal.indexOf(".");
                                                String nombreSinExtension = nombreArchivoOriginal.substring(0, index);
                                                if (!tknActual.getNombre().equalsIgnoreCase(nombreSinExtension)) {
                                                    e = new MiError(linea, " ERROR 169: el nombre del procedimiento no coincide con el nombre del archivo fuente");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                }
                                            }
                                        }
                                    }

                                    /*
                                  
                                     */
                                } else {
                                    if (posicionPara == 0) {
                                        //Es el primer comando PARA y no esta en la primer linea => ERROR
                                        e = new MiError(linea, " ERROR 167: la estructura del programa requiere que el comando PARA sea el comando de inicio");
                                        erroresEncontrados.add(e);
                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        existenErroresEnArchivoOriginal = true;
                                        ++numeroErroresEnArchivoOriginal;

                                        --cantidadPara;
                                        //Es el primer PARA
                                        //Verificamos el argumento del comando que tiene que ser un nombre de procedimiento valiso y que coincida con el nombre del programa
                                        if (!nuevaListaTokens.isEmpty()) {
                                            tknSigte = nuevaListaTokens.get(0);
                                            if (tknSigte.getLinea() == linea) {
                                                //Como esta en la misma linea de instruccion los removemos para analizarlo
                                                tknActual = nuevaListaTokens.remove(0);
                                                if (!tknActual.getTipo().equals(Tipos.NOMBREPROCEDIMIENTO)) {
                                                    //El argumento de PARA es una nombre de procedimiento  NO es correcto, entonces verificamos que
                                                    e = new MiError(linea, " ERROR 141: el nombre del programa debe ser un identificador valido");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                }
                                                //Verificamos coincida con el nombre del archivo fuente con el nombre del procedimiento, sino => nuevo error
                                                //Primero extraemos el nombre del archivo fuente quitandole la extension
                                                int index = nombreArchivoOriginal.indexOf(".");
                                                String nombreSinExtension = nombreArchivoOriginal.substring(0, index);
                                                if (!tknActual.getNombre().equalsIgnoreCase(nombreSinExtension)) {
                                                    e = new MiError(linea, " ERROR 169: el nombre del procedimiento no coincide con el nombre del archivo fuente");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                }

                                            }
                                        }
                                        posicionPara = 1;
                                    } else {
                                        //Verificamos si existe mas de un comando PARA en el programa 
                                        if (!lineaDelComandoPara(tknActual)) {
                                            if (cantidadPara >= 1) {
                                                //Existe mas de un comando PARA=> nuevo error
                                                e = new MiError(linea, " ERROR 170: solo puede existir una instruccion que comience con el comando PARA");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                existenErroresEnArchivoOriginal = true;
                                                ++numeroErroresEnArchivoOriginal;
                                                --cantidadPara;

                                            }
                                        }
                                    }
                                }

                                //Verificamos que la linea de instruccion no tenga errores y procedemos a incluir en una nueva lista
                                //para la confeccion del archivo .lgo final
                                if (!existenErroresEnArchivoOriginal) {
                                    listaContenidoFinalSinErrores.add(nuevoContenido);
                                }
                                break;

                            case "FIN":

                                //El ultimo comando debe ser FIN
                                linea = tknActual.getLinea();

                                //Buscamos la instruccion correspondiente al token actual en el programa
                                nuevoContenido = buscarInstruccion(tknActual);

                                //existeFinEnPosicionCorrecta = posicionComandoFin();
                                //Verificamos si existe un comando FIN en la ultima instruccion
                                if (existeFinEnPosicionCorrecta) {
                                    //Verificamos si existe mas de un comando FIN en el programa observando si la linea coincide con la ultima 
                                    if (!lineaDelComandoFin(tknActual)) {
                                        if (cantidadFin > 1) {
                                            //Existe mas de un comando FIN=> nuevo error
                                            e = new MiError(linea, " ERROR 170: solo puede existir una instruccion que comience con el comando FIN");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;
                                            --cantidadFin;

                                            //A continuacion verificamos sin el comando FIN encontrado tiene algun argumento => error 
                                            if (!nuevaListaTokens.isEmpty()) {
                                                tknSigte = nuevaListaTokens.get(0);

                                                //Revisamos si hay mas tokens en la misma linea => error
                                                if (tknSigte.getLinea() == linea) {
                                                    while (nuevaListaTokens.size() > 0 && tknSigte.getLinea() == linea) {
                                                        tknActual = nuevaListaTokens.remove(0);
                                                        if (nuevaListaTokens.size() > 0) {
                                                            tknSigte = nuevaListaTokens.get(0);
                                                        }
                                                    }
                                                    e = new MiError(linea, " Error 111: este comando no admite argumentos");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    this.existenErroresEnArchivoOriginal = true;
                                                    ++this.numeroErroresEnArchivoOriginal;

                                                }
                                            }
                                        } else {
                                            //Es el ultimo FIN
                                        }

                                    } else {
                                        //Encontramos el comando FIN de la ultima linea, entonces revisamos si tiene algun argumento => error
                                        if (!nuevaListaTokens.isEmpty()) {
                                            tknSigte = nuevaListaTokens.get(0);

                                            //Revisamos si hay mas tokens en la misma linea => error
                                            if (tknSigte.getLinea() == linea) {
                                                while (nuevaListaTokens.size() > 0 && tknSigte.getLinea() == linea) {
                                                    tknActual = nuevaListaTokens.remove(0);
                                                    if (nuevaListaTokens.size() > 0) {
                                                        tknSigte = nuevaListaTokens.get(0);
                                                    }
                                                }
                                                e = new MiError(linea, " Error 111: este comando no admite argumentos");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                this.existenErroresEnArchivoOriginal = true;
                                                ++this.numeroErroresEnArchivoOriginal;

                                            }
                                        }
                                    }

                                } else {
                                    //No  hay un comando FIN en la ultima instruccion, pero podri haber mas de una instruccion con el comando
                                    //Asi que verificamos la linea de cada uno
                                    if (!lineaDelComandoFin(tknActual)) {
                                        if (cantidadFin > 1) {
                                            //Existe mas de un comando PARA => nuevo error
                                            e = new MiError(linea, " ERROR 170: solo puede existir una instruccion que comience con el comando PARA PARA");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;
                                            --cantidadFin;

                                            //A continuacion verificamos sin el comando FIN encontrado tiene algun argumento => error 
                                            if (!nuevaListaTokens.isEmpty()) {
                                                tknSigte = nuevaListaTokens.get(0);

                                                //Revisamos si hay mas tokens en la misma linea => error
                                                if (tknSigte.getLinea() == linea) {
                                                    while (nuevaListaTokens.size() > 0 && tknSigte.getLinea() == linea) {
                                                        tknActual = nuevaListaTokens.remove(0);
                                                        if (nuevaListaTokens.size() > 0) {
                                                            tknSigte = nuevaListaTokens.get(0);
                                                        }
                                                    }
                                                    e = new MiError(linea, " Error 111: este comando no admite argumentos");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    this.existenErroresEnArchivoOriginal = true;
                                                    ++this.numeroErroresEnArchivoOriginal;

                                                }
                                            }

                                        } else {
                                            //Es el ultimo comando FIN y no esta en la ultima linea => ERROR
                                            e = new MiError(linea, " ERROR 166: la estructura requiere que este comando sea la ultima instruccion del programa");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;

                                            if (!nuevaListaTokens.isEmpty()) {
                                                tknSigte = nuevaListaTokens.get(0);

                                                //Revisamos si hay mas tokens en la misma linea => error
                                                if (tknSigte.getLinea() == linea) {
                                                    while (nuevaListaTokens.size() > 0 && tknSigte.getLinea() == linea) {
                                                        tknActual = nuevaListaTokens.remove(0);
                                                        if (nuevaListaTokens.size() > 0) {
                                                            tknSigte = nuevaListaTokens.get(0);
                                                        }
                                                    }
                                                    e = new MiError(linea, " Error 111: este comando no admite argumentos");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    this.existenErroresEnArchivoOriginal = true;
                                                    ++this.numeroErroresEnArchivoOriginal;

                                                }
                                            }
                                        }

                                    }
                                }

                                if (!existenErroresEnArchivoOriginal) {
                                    listaContenidoFinalSinErrores.add(nuevoContenido);
                                }
                                break;
                            case "BORRAPANTALLA":
                            case "BP":
                            case "SUBELAPIZ":
                            case "SL":
                            case "BAJALAPIZ":
                            case "BL":
                            case "GOMA":
                            case "CENTRO":
                            case "OCULTATORTUGA":
                            case "OT":
                            case "MUESTRATORTUGA":
                            case "MT":
                            case "PONLAPIZ":
                            case "LAPIZNORMAL":

                                nuevoContenido = casoComandoSinArgumento(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin);
                                break;

                            case "AVANZA":
                            case "AV":
                            case "GIRADERECHA":
                            case "GD":
                            case "GIRAIZQUIERDA":
                            case "GI":
                            case "RETROCEDE":
                            case "RE":

                                nuevoContenido = casoComandoConArgumentoEntero(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin, variablesDeclaradas);

                                break;
                            case "PONCOLORLAPIZ":
                            case "PONCL":
                                nuevoContenido = casoPonColorLapiz(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin);
                                break;
                            case "PONCOLORRELLENO":
                                existePonColorRelleno = true;
                                nuevoContenido = casoPonColorRelleno(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin);
                                break;
                            case "RELLENA":
                                nuevoContenido = casoComandoRellena(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin, existePonColorRelleno);
                                break;
                            case "HAZ":
                                //HAZ se utiliza para la declaración de variables, su sintaxis es-> HAZ[espacio]"NOMBREDELAVARIABLE[espacio]VALORDELAVARIABLEe 

                                // Argumento #2 Token esperado debe ser tipo OPERADOR DE DECLARACION DE VARIABLE (")
                                //Recuperamos la linea donde se encuentran el comando HAZ para analizar si contiene o no argumentos
                                linea = tknActual.getLinea();
                                //Recuperamos el contenido de archivo del contenido final correspondiente a esta linea de instruccion
                                //para poder incluirle los errores si aparecen
                                nuevoContenido = buscarInstruccion(tknActual);

                                //Almacena el nombre de la variable que se declara para agregarla a la lista de variablesDeclaradas
                                String nuevaVariable = "";

                                //Verificamos si el comando pertenece o no a una lista de comandos del comando REPITE
                                if (estamosEnRepite) {
                                    e = new MiError(linea, " ERROR 150: la lista de comandos de REPITE no debe contener el comando HAZ");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;

                                }

                                if (!nuevaListaTokens.isEmpty()) {
                                    //Como estamos dentro del comando HAZ esperamos que el siguiente token sea de tipo OPERADOR DECLARACION (")
                                    tknSigte = nuevaListaTokens.get(0);
                                    //Revisamos que sigamos en la misma linea de HAZ
                                    if (tknSigte.getLinea() == linea) {
                                        // Como sigue siendo un argumento de HAZ lo removemos de la lista de tokens para analizarlo
                                        tknActual = nuevaListaTokens.remove(0);

                                        if (tknActual.getTipo().equals(Tipos.DECLARACION)) {
                                            //El tokenActual es tipo esperado, por lo tanto solo lo aceptamos y seguimos adelante
                                        } else {
                                            //Como el token no coincide con el esperado entonces existe un error
                                            e = new MiError(linea, " ERROR 119: falta el operador de declaracion de variables ( \" )");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;

                                        }
                                    } else {
                                        //Como el token siguiente no esta en la misma linea => que el comando HAZ no tiene argumentos => ERROR
                                        e = new MiError(linea, " ERROR 153: la lista de argumentos esta incompleta, se require HAZ \"Nombre de la variable :Valor de la variable");
                                        erroresEncontrados.add(e);
                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        existenErroresEnArchivoOriginal = true;
                                        ++numeroErroresEnArchivoOriginal;

                                        break;

                                    }

                                    //Argumento #2 Token esperado debe ser tipo IDENTIFICADOR o nombre de la variable  
                                    tknSigte = nuevaListaTokens.get(0);

                                    //Revisamos que sigamos en la misma linea
                                    if (tknSigte.getLinea() == linea) {
                                        // Como sigue siendo un argumento de HAZ lo removemos de la lista de tokens para analizarlo
                                        tknActual = nuevaListaTokens.remove(0);

                                        if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                            //Encontramos el token esperado, al ser IDENTIFICADOR, debemos verificar  no haya sido declarado antes
                                            existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                            if (existeVariableDeclarada) {
                                                //El identificador existe por lo tanto no puede ser usado nuevante, lanzamos un error
                                                e = new MiError(linea, " ERROR 122: la variable fue definida previamente");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                existenErroresEnArchivoOriginal = true;
                                                ++numeroErroresEnArchivoOriginal;

                                            } else {
                                                //El identificador no existe en las variablesDeclaradas => es nuevaVariable
                                                nuevaVariable = tknActual.getNombre();
                                                //Sin embargo, no podemos meterlo en las variables declaradas hasta ver si le asignaron un valor entero  u otro identificador

                                                //variablesDeclaradas.add(nuevaVariable);
                                            }
                                            //Como el token no es el esperado tratamos de idenficar su tipo para dar mas detalle al mensaje de error    
                                        } else if (tknActual.getTipo().equals(Tipos.COLOR)) {
                                            e = new MiError(linea, " ERROR 158: un color valido no puede ser utilizado como nombre de variable a declarar");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;

                                        } else if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                                            e = new MiError(linea, " ERROR 159: un comando de hugo no puede ser como nombre de variable");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;

                                        } else if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                                            e = new MiError(linea, " ERROR 161: un comando de logo no puede ser usado como nombre de variable");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;

                                        } else {
                                            e = new MiError(linea, " ERROR 136: el nombre de variable no es valido");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;

                                        }
                                    } else {
                                        //Como el token siguiente no esta en la misma linea del comando HAZ => no se incluyeron argumentos
                                        e = new MiError(linea, " ERROR 153: la lista de argumentos esta incompleta, se require HAZ \"Nombre de la variable :Valor de la variable");
                                        erroresEncontrados.add(e);
                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        existenErroresEnArchivoOriginal = true;
                                        ++numeroErroresEnArchivoOriginal;

                                        break;

                                    }

                                    //Argumento #3.-Token esperado debe ser  ENTERO o un OPERADOR DE ASIGNACION
                                    tknSigte = nuevaListaTokens.get(0);

                                    //Verificamos si el tokenSiguiente esta en la misma linea del comando
                                    if (tknSigte.getLinea() == linea) {
                                        //El token esta en la misma linea, por lo tanto, es un argumento del comando => removerlo para analisis
                                        tknActual = nuevaListaTokens.remove(0);

                                        if (tknActual.getTipo().equals(Tipos.ENTERO)) {
                                            //Como el token encontrado es tipo ENTERO solo lo aceptamos y declaramos la variable incluyendo en la 
                                            //lista de variablesDeclaradas
                                            variablesDeclaradas.add(nuevaVariable);

                                        } else if (tknActual.getTipo().equals(Tipos.REAL)) {
                                            e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;

                                        } else if (tknActual.getTipo().equals(Tipos.ASIGNACION)) {
                                            //Encontramos el token de asignacion => el argumento del comando es una variable declarada
                                            //lo aceptamos y seguimos a revisar el siguiente token
                                            //Token esperado debe ser NOMBRE DE VARIABLE ya declarada
                                            tknSigte = nuevaListaTokens.get(0);
                                            //Verificamos si el tokenSiguiente esta en la misma linea del comando
                                            if (tknSigte.getLinea() == linea) {
                                                //El token esta en la misma linea, por lo tanto, es un argumento del comando => removerlo para analisis
                                                tknActual = nuevaListaTokens.remove(0);
                                                //Verificamos si el tokenActual es del tipo IDENTIFICADOR o sea un nombre de variable
                                                if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                                    //Como es un nombre de variable debemos verificar que haya sido declarada con anteriormente
                                                    //para ello consultamos el ArrayList de nombre "variablesDeclaradas"
                                                    existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                                    if (!existeVariableDeclarada) {
                                                        //La variable no ha sido declarada con anterioridad => no puede usarse => ERROR
                                                        e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                                                        erroresEncontrados.add(e);
                                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                        existenErroresEnArchivoOriginal = true;
                                                        ++numeroErroresEnArchivoOriginal;

                                                    } else {
                                                        //Como la variable de asignacion utilizada ya habia sido declrada antes puede utilizarse 
                                                        //en la declaracion de la nuevaVariable, por lo tanto la agregamos a las variablesDeclaradas
                                                        variablesDeclaradas.add(nuevaVariable);
                                                    }
                                                } else if (tknActual.getTipo().equals(Tipos.COLOR)) {
                                                    e = new MiError(linea, " ERROR 160: un color valido no puede ser utilizado como valor de la variable");
                                                    erroresEncontrados.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                } else if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                                                    e = new MiError(linea, " ERROR 162: un comando de hugo no puede ser usado como valor");
                                                    erroresEncontrados.add(e);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);

                                                } else if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                                                    e = new MiError(linea, " ERROR 163: un comando de logo no puede ser usado como valor");
                                                    erroresEncontrados.add(e);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);

                                                } else if (tknActual.getTipo().equals(Tipos.DESCONOCIDO)) {
                                                    e = new MiError(linea, " ERROR 110: se require una variable o identificador valido");
                                                    erroresEncontrados.add(e);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);

                                                } else {
                                                    //El token encontrado no es del tipo IDENTIFICADOR => no es un nombre de variable valido

                                                    e = new MiError(linea, " ERROR 110: se require una variable o identificador valido");
                                                    erroresEncontrados.add(e);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);

                                                }
                                            } else {
                                                //
                                                e = new MiError(linea, " Error 112: se require un argumento entero o una variable declarada previamente para este comando");
                                                erroresEncontrados.add(e);
                                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                                existenErroresEnArchivoOriginal = true;
                                                ++numeroErroresEnArchivoOriginal;

                                            }

                                        } else {
                                            //Como el token no era entero, se espera el uso de una variable declarada, por lo tanto debe estar el operador de asignacion (:)
                                            e = new MiError(linea, " ERROR 110: se require una variable o identificador valido");
                                            erroresEncontrados.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;

                                            if (!nuevaListaTokens.isEmpty()) {
                                                //Token siguiente esperado -> NINGUNO 
                                                tknSigte = nuevaListaTokens.get(0);

                                                //Revisamos si sigamos en la misma linea
                                                if (tknSigte.getLinea() == linea) {
                                                    while (tknSigte.getLinea() == linea) {
                                                        tknActual = nuevaListaTokens.remove(0);
                                                        if (nuevaListaTokens.size() > 0) {
                                                            tknSigte = nuevaListaTokens.get(0);
                                                        }
                                                    }

                                                }

                                            }

                                        }

                                    } else {
                                        //El siguiente token esta en otra linea y todavia faltan argumentos
                                        e = new MiError(linea, " Error 112: se require un argumento entero o una variable declarada previamente para este comando");
                                        erroresEncontrados.add(e);
                                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                        existenErroresEnArchivoOriginal = true;
                                        ++numeroErroresEnArchivoOriginal;
                                    }
                                }

                                if (!existenErroresEnArchivoOriginal) {
                                    listaContenidoFinalSinErrores.add(nuevoContenido);

                                }
                                break;

                            case "REPITE":
                                existeCorDerEnRepite = false;
                                linea = tknActual.getLinea();

                                nuevoContenido = buscarInstruccion(tknActual);
                                erroresEncontradosEnRepite = new ArrayList<MiError>();

                                estamosEnRepite = true;
                                if (!posicionFin) {
                                    e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                                    erroresEncontradosEnRepite.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;

                                    break;

                                } else {
                                    if (!nuevaListaTokens.isEmpty()) {
                                        // Argumento #1- Comprobamos  si el token siguiente es un  ENTERO o una VARIABLE YA DECLARADA
                                        tknSigte = nuevaListaTokens.get(0);

                                        // Verificamos que el token analizado este en la misma linea del comando REPITE
                                        if (tknSigte.getLinea() == linea) {
                                            //Es un argumento de la funcion REPITE asi que lo removemos para analizarlo observando su tipo
                                            tknActual = nuevaListaTokens.remove(0);
                                            switch (tknActual.getTipo()) {

                                                case ASIGNACION:
                                                    tknSigte = nuevaListaTokens.get(0);
                                                    if (tknSigte.getLinea() == linea) {
                                                        //Es un argumento de la funcion REPITE asi que lo removemos para analizarlo observando su tipo
                                                        //Token esperado => tipo IDENTIFICADOR
                                                        tknActual = nuevaListaTokens.remove(0);
                                                        if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                                            //Al ser el token esperado lo aceptamos pero com es un identificador  revisamos si la variable fue declarada con anterioridad
                                                            existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                                            if (!existeVariableDeclarada) {
                                                                e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                                                                erroresEncontradosEnRepite.add(e);
                                                                nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                                existenErroresEnArchivoOriginal = true;
                                                                ++numeroErroresEnArchivoOriginal;

                                                            }

                                                        } else if (tknActual.getTipo().equals(Tipos.COLOR)) {
                                                            e = new MiError(linea, " ERROR 160: un color valido no puede ser utilizado como valor de la variable");
                                                            erroresEncontradosEnRepite.add(e);
                                                            nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                            existenErroresEnArchivoOriginal = true;
                                                            ++numeroErroresEnArchivoOriginal;

                                                        } else if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                                                            e = new MiError(linea, " ERROR 162: un comando de hugo no puede ser usado como valor");
                                                            erroresEncontradosEnRepite.add(e);
                                                            existenErroresEnArchivoOriginal = true;
                                                            ++numeroErroresEnArchivoOriginal;
                                                            nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);

                                                        } else if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                                                            e = new MiError(linea, " ERROR 163: un comando de logo no puede ser usado como valor");
                                                            erroresEncontradosEnRepite.add(e);
                                                            existenErroresEnArchivoOriginal = true;
                                                            ++numeroErroresEnArchivoOriginal;
                                                            nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);

                                                        } else if (tknActual.getTipo().equals(Tipos.DESCONOCIDO)) {
                                                            e = new MiError(linea, " ERROR 110: se require una variable o identificador valido");
                                                            erroresEncontradosEnRepite.add(e);
                                                            existenErroresEnArchivoOriginal = true;
                                                            ++numeroErroresEnArchivoOriginal;
                                                            nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);

                                                        } else {
                                                            e = new MiError(linea, " ERROR 110: se require una variable o identificador valido");
                                                            erroresEncontradosEnRepite.add(e);
                                                            nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                            existenErroresEnArchivoOriginal = true;
                                                            ++numeroErroresEnArchivoOriginal;
                                                        }
                                                    }
                                                    break;
                                                case IDENTIFICADOR:
                                                    e = new MiError(linea, " ERROR 134: falta el operador de (:)de poder utilizar una variable");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    /*    
                                                    //Al ser el token esperado lo aceptamos pero com es un identificador  revisamos si la variable fue declarada con anterioridad
                                                    existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                                    if (!existeVariableDeclarada) {
                                                        e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                                                        erroresEncontradosEnRepite.add(e);
                                                        nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                        existenErroresEnArchivoOriginal = true;
                                                        ++numeroErroresEnArchivoOriginal;

                                                    }
                                                     */
                                                    break;
                                                case ENTERO:
                                                    //lo aceptamos y vemos el siguiente argumento
                                                    break;
                                                case REAL:
                                                    e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                    break;
                                                case COMANDOHUGO:
                                                case COMANDOLOGO:
                                                    e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    e = new MiError(linea, " ERROR 145: los comandos solo estan permitidos dentro de los corchetes");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                    break;
                                                case CORDER:
                                                    existeCorDerEnRepite = true;
                                                    e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    e = new MiError(linea, " ERROR 102: falta corchete izquierdo");
                                                    erroresEncontradosEnRepite.add(e);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    e = new MiError(linea, " ERROR 146: se requiere una lista de comandos validos");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                    break;
                                                case DESCONOCIDO:
                                                    e = new MiError(linea, " ERROR 132: se require un argumento entero");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                    break;
                                                default:
                                                    e = new MiError(linea, " ERROR 144: falta el entero que indica el numero de repiticiones del comando");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                    break;
                                            }

                                            // Argumento #2 - Comprobamos si el token siguiente es el que esperamos, en este caso, un CORIZQ
                                            tknSigte = nuevaListaTokens.get(0);
                                            //Comprobamos que sea un argumento de REPITE esperamos un CORIZQ, es decir, en la misma linea

                                            if (tknSigte.getLinea() == linea) {

                                                if (tknSigte.getTipo().equals(Tipos.CORIZQ)) {
                                                    //es el token esperado,  un corchete izquierdo por lo tanto, lo aceptamos y establecemos su existencia 
                                                    existeCorIzqEnRepite = true;

                                                    //Vemos si el token siguiente esperamos un COMANDOHUGO
                                                } else {
                                                    //No existe el corchete izquiedo 
                                                    existeCorIzqEnRepite = false;
                                                    e = new MiError(linea, " ERROR 102: falta corchete izquierdo");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                    //Tratamos de ver que tipo de token es para mejorar la explicacion del error
                                                    if (tknSigte.getTipo().equals(Tipos.COMANDOHUGO)) {
                                                        //es un CORIZQ -> lo aceptamos ->  ¿ tknSigte = nuevaListaTokens.get(0); ?
                                                        //Comprobamos que sea un argumento de REPITE esperamos un CORIZQ

                                                        existeListaComandosEnRepite = true;

                                                    } else if (tknSigte.getTipo().equals(Tipos.CORDER)) {

                                                        existeCorIzqEnRepite = false;
                                                        e = new MiError(linea, " ERROR 146: se requiere una lista de comandos validos");
                                                        erroresEncontradosEnRepite.add(e);
                                                        nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                        existenErroresEnArchivoOriginal = true;
                                                        ++numeroErroresEnArchivoOriginal;

                                                    } else if (!tknSigte.getTipo().equals(Tipos.COMANDOHUGO)) {

                                                        existeCorIzqEnRepite = false;
                                                        e = new MiError(linea, " ERROR 131: la lista de comandos a repetir debe comenzar con un comando valido");
                                                        erroresEncontradosEnRepite.add(e);
                                                        nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                        existenErroresEnArchivoOriginal = true;
                                                        ++numeroErroresEnArchivoOriginal;

                                                    }
                                                }

                                            } else {
                                                if (!existeCorDerEnRepite) {
                                                    //El token analizado no es un CORCHETE IZQUIERDO,ademas, esta en otra linea no es un argumento de REPITE
                                                    e = new MiError(linea, " ERROR 102: falta corchete izquierdo");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                    e = new MiError(linea, " ERROR 146: se requiere una lista de comandos validos");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                    e = new MiError(linea, " ERROR 103: falta corchete derecho");
                                                    erroresEncontradosEnRepite.add(e);
                                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                                    existenErroresEnArchivoOriginal = true;
                                                    ++numeroErroresEnArchivoOriginal;

                                                }
                                            }

                                        } else {
                                            //El token analizado no es un identificador ni un entero ademas, esta en otra linea no es un argumento de REPITE
                                            e = new MiError(linea, " ERROR 149: la lista de argumentos esta incompleta");
                                            erroresEncontradosEnRepite.add(e);
                                            nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                            existenErroresEnArchivoOriginal = true;
                                            ++numeroErroresEnArchivoOriginal;

                                        }

                                    } //fin de if de si la lista no esta a

                                    if (!existenErroresEnArchivoOriginal) {
                                        listaContenidoFinalSinErrores.add(nuevoContenido);

                                    }

                                }//fin else posicion fin
                        } //fin del switch dentro del case COMANDOHUGO
                        break;
                    // fin case  COMANDOHUGO

                    case "COMANDOLOGO":
                        switch (tknActual.getNombre()) {
                            case "ABIERTOS":
                            case "ABRE":
                            case "ABREACTUALIZAR":
                            case "ABREDIALOGO":
                            case "ABREMIDI":
                            case "ABREPUERTO":
                            case "AC":
                            case "ACTIVA":
                            case "ACTIVAVENTANA":
                            case "ACTUALIZABOTON":
                            case "ACTUALIZAESTATICO":
                            case "AJUSTA":
                            case "ALTO":
                            case "ANALIZA":
                            case "ANTERIOR":
                            case "ANTES":
                            case "APLICA":
                            case "ARCCOS":
                            case "ARCODEELIPSE":
                            case "ARCSEN":
                            case "ARCTAN":
                            case "AREAACTIVA":
                            case "ARREGLO":
                            case "ASCII":
                            case "ATRAPA":
                            case "ATRAS":
                            case "AYUDA":
                            case "AYUDADEWINDOWS":
                            case "AZAR":
                            case "AÑADECADENALISTBOX":
                            case "AÑADELINEACOMBOBOX":
                            case "BA":
                            case "BAJAN":
                            case "BAJANARIZ":
                            case "BAL":
                            case "BALANCEA":
                            case "BALANCEAIZQUIERDA":
                            case "BALANCEO":
                            case "BARRERA":
                            case "BITINVERSO":
                            case "BITO":
                            case "BITXOR":
                            case "BITY":
                            case "BO":
                            case "BOARCHIVO":
                            case "BORRA ":
                            case "BORRABARRADESPLAZAMIENTO":
                            case "BORRABOTON":
                            case "BORRABOTONRADIO":
                            case "BORRACADENALISTBOX":
                            case "BORRACHECKBOX":
                            case "BORRACOMBOBOX":
                            case "BORRADIALOGO":
                            case "BORRADIR":
                            case "BORRAESTATICO":
                            case "BORRAGROUPBOX":
                            case "BORRALINEACOMBOBOX":
                            case "BORRALISTBOX":
                            case "BORRAPALETA":
                            case "BORRAPANTALLA":
                            case "BORRAR":
                            case "BORRARARCHIVO":
                            case "BORRATEXTO":
                            case "BORRAVENTANA":
                            case "BOTON":
                            case "BT":
                            case "CABECEA":
                            case "CABECEO":
                            case "CAI":
                            case "CAMBIADIRECTORIO":
                            case "CAMBIASIGNO":
                            case "CAR":
                            case "CARACTER":
                            case "CARGA":
                            case "CARGADIB":
                            case "CARGADIBTAMAÑO":
                            case "CARGADLL":
                            case "CARGAGIF":
                            case "CD":
                            case "CERCA":
                            case "CIERRA":
                            case "CIERRAMIDI":
                            case "CIERRAPUERTO":
                            case "CL":
                            case "CO":
                            case "COGE":
                            case "COLORLAPIZ":
                            case "COLORPAPEL":
                            case "COLORRELLENO":
                            case "COMODEVUELVE":
                            case "CONTADORACERO":
                            case "CONTENIDO":
                            case "CONTINUA":
                            case "COPIAAREA":
                            case "COPIADEF":
                            case "CORTAAREA":
                            case "CREABARRADESPLAZAMIENTO":
                            case "CREABOTON":
                            case "CREABOTONRADIO":
                            case "CREACHECKBOX":
                            case "CREACOMBOBOX":
                            case "CREADIALOGO":
                            case "CREADIR":
                            case "CREADIRECTORIO":
                            case "CREAESTATICO":
                            case "CREAGROUPBOX":
                            case "CREALISTBOX":
                            case "CREAVENTANA":
                            case "CS":
                            case "CUENTA":
                            case "CUENTAREPITE":
                            case "CURSOR":
                            case "DEFINE":
                            case "DEFINEMACRO":
                            case "DEFINIDO":
                            case "DEFINIDOP":
                            case "DESPLAZA":
                            case "DESPLAZAIZQUIERDA":
                            case "DESPLAZAX":
                            case "DESPLAZAY":
                            case "DESTAPA":
                            case "DEV":
                            case "DEVUELVE":
                            case "DIFERENCIA":
                            case "DIRECTORIO":
                            case "DIRECTORIOPADRE":
                            case "DIRECTORIOS":
                            case "DIVISION":
                            case "ED":
                            case "EDITA":
                            case "EDITAFICHERO":
                            case "EJECUTA":
                            case "EJECUTAANALIZA":
                            case "ELEMENTO":
                            case "EMPIEZAPOLIGONO":
                            case "ENCADENA":
                            case "ENTERO":
                            case "ENVIA":
                            case "ENVIAVALORRED":
                            case "ENVOLVER":
                            case "ERROR":
                            case "ESCRIBE":
                            case "ESCRIBEBOTONRADIO":
                            case "ESCRIBECADENAPUERTO":
                            case "ESCRIBECARACTERPUERTO":
                            case "ESCRIBEPUERTO":
                            case "ESCRIBEPUERTO2":
                            case "ESCRIBERED":
                            case "ESCRIBIRARCHIVO":
                            case "ESCRITURA":
                            case "ESPERA":
                            case "ESTADO":
                            case "ESTADOCHECKBOX":
                            case "EXCLUSIVO":
                            case "EXP":
                            case "FINLEC":
                            case "FINRED":
                            case "FORMATONUMERO":
                            case "FR":
                            case "FRASE":
                            case "GOTEAR":
                            case "GROSOR":
                            case "GUARDA":
                            case "GUARDADIALOGO":
                            case "GUARDADIB":
                            case "GUARDAGIF":
                            case "HABILITABOTON":
                            case "HABILITACHECKBOX":
                            case "HABILITACOMBOBOX":
                            case "HACIA":
                            case "HACIAXYZ":
                            case "HORA":
                            case "HORAMILI":
                            case "IG":
                            case "IGUAL":
                            case "IGUALES":
                            case "ILA":
                            case "IM":
                            case "IMPROP":
                            case "IMTS":
                            case "IMTSP":
                            case "INDICEIMAGEN":
                            case "INICIARED":
                            case "INVERSOLAPIZ":
                            case "IZ":
                            case "IZQUIERDA":
                            case "LAPIZ":
                            case "LC":
                            case "LCS":
                            case "LECTURA":
                            case "LEEBARRADESPLAZAMIENTO":
                            case "LEEBOTONRADIO":
                            case "LEECADENAPUERTO":
                            case "LEECAR":
                            case "LEECARACTERPUERTO":
                            case "LEECARC":
                            case "LEECARCS":
                            case "LEEFOCO":
                            case "LEELISTA":
                            case "LEEPALABRA":
                            case "LEEPUERTO":
                            case "LEEPUERTO2":
                            case "LEEPUERTOJUEGOS":
                            case "LEERED":
                            case "LEESELECCIONLISTBOX":
                            case "LEETECLA":
                            case "LEETEXTOCOMBOBOX":
                            case "LEEVALORRED":
                            case "LIMPIA":
                            case "LIMPIAPUERTO":
                            case "LISTA":
                            case "LISTAARCH":
                            case "LL":
                            case "LLAMADLL":
                            case "LN":
                            case "LOCAL":
                            case "LOG":
                            case "LPROP":
                            case "LR":
                            case "LUZ":
                            case "LVARS":
                            case "MACRO":
                            case "MATRIZ":
                            case "MAYOR":
                            case "MAYORQUE":
                            case "MAYUSCULAS":
                            case "MCI":
                            case "MENOR":
                            case "MENORQUE":
                            case "MENOS":
                            case "MENOSPRIMERO":
                            case "MENOSPRIMEROS":
                            case "MENSAJE":
                            case "MENSAJEMIDI":
                            case "MIEMBRO":
                            case "MINUSCULAS":
                            case "MODOBITMAP":
                            case "MODOPUERTO":
                            case "MODOTORTUGA":
                            case "MODOVENTANA":
                            case "MODULO":
                            case "MP":
                            case "MPR":
                            case "MPS":
                            case "MU":
                            case "MUESTRA":
                            case "MUESTRAPOLIGONO":
                            case "MUESTRAT":
                            case "MUESTRATORTUGA":
                            case "NO":
                            case "NODOS":
                            case "NOESTADO":
                            case "NOEXCLUSIVO":
                            case "NOGOTEAR":
                            case "NOMBRE":
                            case "NOMBRES":
                            case "NOPAS":
                            case "NORED":
                            case "NOTRAZA":
                            case "NUMERO":
                            case "O":
                            case "PALABRA":
                            case "PARADA":
                            case "PASO":
                            case "PATRONLAPIZ":
                            case "PAUSA":
                            case "PEGA":
                            case "PEGAENINDICE":
                            case "PERSPECTIVA":
                            case "PFT":
                            case "PINTACOLOR":
                            case "PIXEL":
                            case "PLA":
                            case "POCCR":
                            case "PONAREAACTIVA":
                            case "PONBALANCEO":
                            case "PONBARRADESPLAZAMIENTO":
                            case "PONCABECEO":
                            case "PONCHECKBOX":
                            case "PONCLIP":
                            case "PONCOLORPAPEL":
                            case "PONCONTADOR":
                            case "PONCP":
                            case "PONCURSORESPERA":
                            case "PONCURSORNOESPERA":
                            case "PONELEMENTO":
                            case "PONESCRITURA":
                            case "PONF":
                            case "PONFOCO":
                            case "PONFONDO":
                            case "PONFORMATORTUGA":
                            case "PONG":
                            case "PONGROSOR":
                            case "PONINDICEBIT":
                            case "PONLECTURA":
                            case "PONLUPA":
                            case "PONLUZ":
                            case "PONMARGENES":
                            case "PONMODOBIT":
                            case "PONMODOTORTUGA":
                            case "PONMP":
                            case "PONPATRONLAPIZ":
                            case "PONPIXEL":
                            case "PONPOS":
                            case "PONPOSESCRITURA":
                            case "PONPOSLECTURA":
                            case "PONPRIMERO":
                            case "PONPROP":
                            case "PONR":
                            case "PONRATON":
                            case "PONRED":
                            case "PONRONZAL":
                            case "PONRUMBO":
                            case "PONTAMAÑOTIPO":
                            case "PONTECLADO":
                            case "PONTEXTOCOMBOBOX":
                            case "PONULTIMO":
                            case "PONX":
                            case "PONXY":
                            case "PONXYZ":
                            case "PONY":
                            case "PONZ":
                            case "POS":
                            case "POS3D":
                            case "POSICIONATE":
                            case "POSLECTURA":
                            case "POSRATON":
                            case "POTENCIA":
                            case "PP":
                            case "PPR":
                            case "PREGUNTABOX":
                            case "PRI":
                            case "PRIMERO":
                            case "PRIMEROS":
                            case "PRIMITIVA":
                            case "PRODUCTO":
                            case "PROP":
                            case "PROPIEDAD":
                            case "PRUEBA":
                            case "PTT":
                            case "PUL":
                            case "QUITADIBUJOTORTUGA":
                            case "QUITADLL":
                            case "QUITAESTADO":
                            case "QUITARED":
                            case "QUITARRATON":
                            case "QUITATECLADO":
                            case "RADARCCOS":
                            case "RADARCSEN":
                            case "RADARCTAN":
                            case "RADCOS":
                            case "RADSEN":
                            case "RADTAN":
                            case "RAIZCUADRADA":
                            case "RC":
                            case "REAZAR":
                            case "RECTANGULORRELLENO":
                            case "REDONDEA":
                            case "RESTO":
                            case "RESULTADOEJECUTA":
                            case "RO":
                            case "RONZAL":
                            case "ROTULA":
                            case "RUMBO":
                            case "SELECCIONBOX":
                            case "SEN":
                            case "SHELL":
                            case "SI":
                            case "SIC":
                            case "SICIERTO":
                            case "SIEMPRE":
                            case "SIEVENTO":
                            case "SIF":
                            case "SIFALSO":
                            case "SINOBOX":
                            case "SIRED":
                            case "SISINO":
                            case "SISTEMA":
                            case "SIVERDADERO":
                            case "STANDOUT":
                            case "SUENAWAVE":
                            case "SUMA":
                            case "TAMAÑODECORADO":
                            case "TAMAÑODIBUJO":
                            case "TAMAÑOGIF":
                            case "TAMAÑOTIPO":
                            case "TAN":
                            case "TAPA":
                            case "TAPADO":
                            case "TAPANOMBRE":
                            case "TECLA":
                            case "TERMINAPOLIGONO":
                            case "TEXTO":
                            case "TIENEBARRA":
                            case "TIPO":
                            case "TONO":
                            case "TORTUGA":
                            case "TORTUGAS":
                            case "TRAZA":
                            case "UL":
                            case "ULTIMO":
                            case "UNSTE":
                            case "VACIA":
                            case "VACIO":
                            case "VALOR":
                            case "VAR":
                            case "VENTANADEPURADOR":
                            case "VIRA":
                            case "VISIBLE":
                            case "Y":

                                nuevoContenido = casoInstruccionSoloValidaEnLogo(tknActual, erroresEncontrados, nuevaListaTokens, posicionFin, existenErroresEnArchivoOriginal);

                                break;
                        } //SWITCH COMANDOLOGO
                        break;
                    case "ENTERO":

                        //SE QUIRE MANEJAR LOS CASOS EN QUE APARECE NUMEROS ENTEROS COMO INICIO DE UNA INSTRUCCION => error
                        linea = tknActual.getLinea();
                        nuevoContenido = buscarInstruccion(tknActual);

                        if (!nuevaListaTokens.isEmpty()) {
                            if (estamosEnRepite) {
                                //Solo lo aceptamos 
                                e = new MiError(linea, " ERROR 135: la instruccion debe comenzar con un comando valido");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;

                            } else if (tknActual.getPosicion() == 0) {
                                //Token es un entero en el comienzo de una nueva linea 
                                e = new MiError(linea, " ERROR 135: la instruccion debe comenzar con un comando valido");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;

                                //Vemos si el token siguiente NO esperamos ningun otro token
                                //Comprobamos que este en la misma linea que entero encontrado al inicio de una instruccion
                                //A continuacion verificamos sin entero  encontrado tiene algun argumento => error 
                                if (!nuevaListaTokens.isEmpty()) {
                                    tknSigte = nuevaListaTokens.get(0);

                                    //Revisamos si hay mas tokens en la misma linea => error
                                    if (tknSigte.getLinea() == linea) {
                                        while (nuevaListaTokens.size() > 0 && tknSigte.getLinea() == linea) {
                                            tknActual = nuevaListaTokens.remove(0);
                                            if (nuevaListaTokens.size() > 0) {
                                                tknSigte = nuevaListaTokens.get(0);
                                            }
                                        }
                                    }
                                }

                            }
                        }

                        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {

                            listaContenidoFinalSinErrores.add(nuevoContenido);

                        }
                        break;
                    case "REAL":
                        //SE QUIRE MANEJAR LOS CASOS EN QUE APARECE NUMEROS REALES COMO INICIO DE UNA INSTRUCCION => error
                        linea = tknActual.getLinea();
                        nuevoContenido = buscarInstruccion(tknActual);

                        if (!nuevaListaTokens.isEmpty()) {
                            if (estamosEnRepite) {
                                //Solo lo aceptamos 
                                e = new MiError(linea, " ERROR 135: la instruccion debe comenzar con un comando valido");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;

                            } else if (tknActual.getPosicion() == 0) {
                                //Token es un real en el comienzo de una nueva linea 
                                e = new MiError(linea, " ERROR 135: la instruccion debe comenzar con un comando valido");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;

                                //Vemos si el token siguiente NO esperamos ningun otro token
                                //Comprobamos que este en la misma linea que entero encontrado al inicio de una instruccion
                                //A continuacion verificamos sin entero  encontrado tiene algun argumento => error 
                                if (!nuevaListaTokens.isEmpty()) {
                                    tknSigte = nuevaListaTokens.get(0);

                                    //Revisamos si hay mas tokens en la misma linea => error
                                    if (tknSigte.getLinea() == linea) {
                                        while (nuevaListaTokens.size() > 0 && tknSigte.getLinea() == linea) {
                                            tknActual = nuevaListaTokens.remove(0);
                                            if (nuevaListaTokens.size() > 0) {
                                                tknSigte = nuevaListaTokens.get(0);
                                            }
                                        }
                                    }
                                }

                            }
                        }

                        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {
                            listaContenidoFinalSinErrores.add(nuevoContenido);
                        }
                        break;
                    case "DESCONOCIDO":
                        //SE QUIRE MANEJAR LOS CASOS EN QUE APARECE NOMBRES DE VARIABLES QUE NO CUMPLEN LAS CONDICIONES DE IDENTFICADOR VALIDO
                        linea = tknActual.getLinea();

                        nuevoContenido = buscarInstruccion(tknActual);

                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;

                        } else {
                            if (!nuevaListaTokens.isEmpty()) {
                                if (estamosEnRepite) {
                                    //Solo lo aceptamos 
                                    e = new MiError(linea, " ERROR 135: la instruccion debe comenzar con un comando valido");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;

                                } else if (tknActual.getPosicion() == 0) {
                                    //Token es un identificador en el comienzo de una nueva linea 
                                    e = new MiError(linea, " ERROR 135: la instruccion debe comenzar con un comando valido");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;

                                    //Vemos si el token siguiente NO esperamos ningun otro token
                                    //Comprobamos que este en la misma linea que entero encontrado al inicio de una instruccion
                                    //A continuacion verificamos sin entero  encontrado tiene algun argumento => error 
                                    if (!nuevaListaTokens.isEmpty()) {
                                        tknSigte = nuevaListaTokens.get(0);

                                        //Revisamos si hay mas tokens en la misma linea => error
                                        if (tknSigte.getLinea() == linea) {
                                            while (nuevaListaTokens.size() > 0 && tknSigte.getLinea() == linea) {
                                                tknActual = nuevaListaTokens.remove(0);
                                                if (nuevaListaTokens.size() > 0) {
                                                    tknSigte = nuevaListaTokens.get(0);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {

                            listaContenidoFinalSinErrores.add(nuevoContenido);

                        }

                        break;
                    case "COLOR":
                        //MANEJA LOS CASOS EN QUE APAREZCAN COLORES EN OTRAS POSICIONES QUE NO SEAN ARGUMENTOS
                        //DE FUNCIONES QUE REQUIEREN COMO PARAMETRO UN COLOR VALIDO
                        //EL CASO DEL BUEN USO DEL COLOR SE MANEJA EN funciones poncolorrelleno y poncolorlapiz

                        linea = tknActual.getLinea();

                        nuevoContenido = buscarInstruccion(tknActual);

                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;

                        } else {
                            if (!nuevaListaTokens.isEmpty()) {
                                if (estamosEnRepite) {
                                    //Solo lo aceptamos 
                                    e = new MiError(linea, " ERROR 156: un color valido solo pueden utilizarse como argumento de PONCOLORELLENO o PONCOLORLAPIZ");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;

                                } else if (tknActual.getPosicion() == 0) {
                                    //Token es un identificador en el comienzo de una nueva linea 
                                    e = new MiError(linea, " ERROR 135: la instruccion debe comenzar con un comando valido");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;

                                    //Vemos si el token siguiente NO esperamos ningun otro token
                                    //Comprobamos que este en la misma linea que entero encontrado al inicio de una instruccion
                                    //A continuacion verificamos sin entero  encontrado tiene algun argumento => error 
                                    if (!nuevaListaTokens.isEmpty()) {
                                        tknSigte = nuevaListaTokens.get(0);

                                        //Revisamos si hay mas tokens en la misma linea => error
                                        if (tknSigte.getLinea() == linea) {
                                            while (nuevaListaTokens.size() > 0 && tknSigte.getLinea() == linea) {
                                                tknActual = nuevaListaTokens.remove(0);
                                                if (nuevaListaTokens.size() > 0) {
                                                    tknSigte = nuevaListaTokens.get(0);
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {

                            listaContenidoFinalSinErrores.add(nuevoContenido);

                        }

                        break;
                    case "IDENTIFICADOR":
                        //SE QUIERE MANEJAR LOS CASOS EN QUE APARECE UN IDENTIFICADOR VALIDO AL INICIO DE UNA INSTRUCCION 
                        //O UN IDENTIFICADOR SIN ESTAR ASOCIADO A UN COMANDO EN REPITE

                        linea = tknActual.getLinea();

                        nuevoContenido = buscarInstruccion(tknActual);

                        if (!posicionFin) {
                            e = new MiError(linea, " ERROR 143: no se permiten mas comandos luego del comando FIN");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;

                        } else {
                            if (!nuevaListaTokens.isEmpty()) {
                                if (estamosEnRepite) {
                                    //Solo lo aceptamos 
                                    e = new MiError(linea, " ERROR 151: toda identificador o variable debe ser el argumento de un comando valido");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;

                                } else if (tknActual.getPosicion() == 0) {
                                    //Token es un identificador en el comienzo de una nueva linea 
                                    e = new MiError(linea, " ERROR 135: la instruccion debe comenzar con un comando valido");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;

                                    //Vemos si el token siguiente NO esperamos ningun otro token
                                    //Comprobamos que este en la misma linea que entero encontrado al inicio de una instruccion
                                    //A continuacion verificamos sin entero  encontrado tiene algun argumento => error 
                                    if (!nuevaListaTokens.isEmpty()) {
                                        tknSigte = nuevaListaTokens.get(0);

                                        //Revisamos si hay mas tokens en la misma linea => error
                                        if (tknSigte.getLinea() == linea) {
                                            while (nuevaListaTokens.size() > 0 && tknSigte.getLinea() == linea) {
                                                tknActual = nuevaListaTokens.remove(0);
                                                if (nuevaListaTokens.size() > 0) {
                                                    tknSigte = nuevaListaTokens.get(0);
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {

                            listaContenidoFinalSinErrores.add(nuevoContenido);

                        }

                        break;

                    case "CORIZQ":

                        linea = tknActual.getLinea();

                        nuevoContenido = buscarInstruccion(tknActual);

                        if (!nuevaListaTokens.isEmpty()) {
                            //Primero verificamos estar dentro del comando REPITE
                            if (estamosEnRepite) {
                                // 1 - Comprobamos si el token siguiente es el que esperamos, en este caso, un   COMANDO HUGO
                                tknSigte = nuevaListaTokens.get(0);
                                //Comprobamos que sea un argumento de REPITE esperamos un COMANDOHUGO, es decir, en la misma linea

                                if (tknSigte.getLinea() == linea) {

                                    if (tknSigte.getTipo().equals(Tipos.COMANDOHUGO)) {
                                        //Es el token esperaco COMANDOHUGO, lo aceptamos 

                                    } else if (tknSigte.getTipo().equals(Tipos.CORDER)) {

                                        existeCorIzqEnRepite = false;
                                        e = new MiError(linea, " ERROR 146: se requiere una lista de comandos validos");
                                        erroresEncontradosEnRepite.add(e);
                                        nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                        existenErroresEnArchivoOriginal = true;
                                        ++numeroErroresEnArchivoOriginal;

                                    } else {

                                        existeCorIzqEnRepite = false;
                                        e = new MiError(linea, " ERROR 131: la lista de comandos a repetir debe comenzar con un comando valido");
                                        erroresEncontradosEnRepite.add(e);
                                        nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                        existenErroresEnArchivoOriginal = true;
                                        ++numeroErroresEnArchivoOriginal;

                                    }
                                } else {
                                    e = new MiError(linea, " ERROR 146: se requiere una lista de comandos validos");
                                    erroresEncontradosEnRepite.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;

                                }

                                // 2 - Buscamos la existencia del CORDER en la misma linea del CORIZQ
                                Token tok = new Token();
                                for (int i = 0; i < nuevaListaTokens.size(); ++i) {
                                    tok = nuevaListaTokens.get(i);
                                    if (tok.getLinea() == linea) {
                                        if (tok.getTipo().equals(Tipos.CORDER)) {
                                            existeCorDerEnRepite = true;
                                            break;
                                        } else {
                                            existeCorDerEnRepite = false;
                                        }
                                    } else {
                                        //El CORDER no estaba en la misma linea del CORIZQ
                                        existeCorDerEnRepite = false;
                                        break;
                                    }

                                }

                                if (!existeCorDerEnRepite) {
                                    existeCorDerEnRepite = false;
                                    e = new MiError(linea, " ERROR 103: falta corchete derecho");
                                    erroresEncontradosEnRepite.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;

                                }

                            } else {

                                e = new MiError(linea, " ERROR 147: esta version solo acepta corchetes en el comando REPITE");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;

                            }
                        }

                        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {

                            listaContenidoFinalSinErrores.add(nuevoContenido);

                        }

                        break;
                    case "CORDER":

                        linea = tknActual.getLinea();

                        nuevoContenido = buscarInstruccion(tknActual);

                        if (!nuevaListaTokens.isEmpty()) {
                            if (estamosEnRepite) {
                                //Solo lo aceptamos 
                                //estamosEnRepite = true;
                                // 1 - Comprobamos si el token siguiente es el que esperamos, en este caso, un   COMANDO HUGO
                                tknSigte = nuevaListaTokens.get(0);
                                //Comprobamos que sea un argumento de REPITE esperamos un COMANDOHUGO, es decir, en la misma linea

                                if (tknSigte.getLinea() == linea) {
                                    e = new MiError(linea, " ERROR 160: la lista de comandos a repetir debe estar entre un corchete izquierdo y uno derecho");
                                    erroresEncontradosEnRepite.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontradosEnRepite);
                                    existenErroresEnArchivoOriginal = true;
                                    ++numeroErroresEnArchivoOriginal;

                                }

                            } else {

                                e = new MiError(linea, " ERROR 147: esta version solo acepta corchetes en el comando REPITE");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;

                            }
                        }

                        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {

                            listaContenidoFinalSinErrores.add(nuevoContenido);

                        }
                        break;
                    default:
                        break;
                }

            }//fin del while

            //FINAL TODO NUEVO 
        } // fin if listaTokens esta vacia?

        LineaContenido nuevoContenidoPara = new LineaContenido();
        Token primerToken = listaTokens.get(0);
        primerToken.getLinea();
        nuevoContenidoPara.setLinea(0);
        nuevoContenidoPara.setInstruccion("");
        List<MiError> erroresPara = new ArrayList<>();
        if (!existeParaEnPosicionCorrecta) {
            MiError ePara = new MiError(primerToken.getLinea(), " ERROR 140: el programa debe iniciar con el comando PARA");
            erroresPara.add(ePara);
            nuevoContenidoPara.setErroresEncontrados(erroresPara);
            listaContenidoFinal.add(0, nuevoContenidoPara);
            existenErroresEnArchivoOriginal = true;
            ++numeroErroresEnArchivoOriginal;
        }

        if (!existeFinEnPosicionCorrecta) {
            LineaContenido nuevoContenidoFin = new LineaContenido();
            Token ultimoToken = listaTokens.get(listaTokens.size() - 1);
            List<MiError> erroresFin = new ArrayList<>();
            nuevoContenidoFin.setLinea(ultimoToken.getLinea() + 1);
            nuevoContenidoFin.setInstruccion("");
            MiError eFin = new MiError(ultimoToken.getLinea() + 1, " ERROR 142: el programa debe finalizar con el comando FIN");
            erroresFin.add(eFin);
            nuevoContenidoFin.setErroresEncontrados(erroresFin);
            listaContenidoFinal.add(nuevoContenidoFin);
            existenErroresEnArchivoOriginal = true;
            ++numeroErroresEnArchivoOriginal;

        }

        //Control si existen o no errores en el archivo fuente
        if (numeroErroresEnArchivoOriginal > 0) {
            crearArchivoConErrores(listaContenidoFinal, this.nombreArchivoOriginal);
            return listaContenidoFinal;
        } else {
            crearArchivoSinErrores(listaContenidoFinalSinErrores, this.nombreArchivoOriginal);
            return listaContenidoFinalSinErrores;
        }
    } //FIN DEL NUEVO SINTACTICO

    public LineaContenido casoComandoConArgumentoEntero(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin, ArrayList<String> variablesDeclaradas) {
        // Esta funcion en llamada en el caso de comandos que tiene como argumento un numero entero o una varible previamente declarada
        //La sintaxis de este tipo de comandos es ->NOMBRECOMANDO [espacio] NUMEROENTERO | :NOMBREDELAVARIABLE 

        //Token siguiente esperado debe ser tipo IDENTIFICADOR 
        int linea = tknActual.getLinea();
        boolean existeVariableDeclarada = false;

        //erroresEncontrados = new ArrayList<MiError>();
        LineaContenido nuevoContenido;
        nuevoContenido = buscarInstruccion(tknActual);

        MiError e = new MiError();
        Token tknSigte = new Token();

        if (!nuevaListaTokens.isEmpty()) {
            //Token esperado debe ser  ENTERO o un OPERADOR DE ASIGNACION
            tknSigte = nuevaListaTokens.get(0);

            //Verificamos si el tokenSiguiente esta en la misma linea del comando
            if (tknSigte.getLinea() == linea) {
                //El token esta en la misma linea, por lo tanto, es un argumento del comando => removerlo para analisis
                tknActual = nuevaListaTokens.remove(0);

                switch (tknActual.getTipo()) {
                    //Como el token encontrado es tipo ENTERO solo lo aceptamos y seguimos adelante con la nueva linea del programa
                    case ENTERO:
                        break;
                    case REAL:
                        e = new MiError(linea, " ERROR 132: se require un argumento entero");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        existenErroresEnArchivoOriginal = true;
                        ++numeroErroresEnArchivoOriginal;
                        break;
                    case ASIGNACION:
                        //Encontramos el token de asignacion => el argumento del comando es una variable declarada
                        //lo aceptamos y seguimos a revisar el siguiente token
                        //Token esperado debe ser NOMBRE DE VARIABLE ya declarada
                        tknSigte = nuevaListaTokens.get(0);
                        //Verificamos si el tokenSiguiente esta en la misma linea del comando
                        if (tknSigte.getLinea() == linea) {
                            //El token esta en la misma linea, por lo tanto, es un argumento del comando => removerlo para analisis
                            tknActual = nuevaListaTokens.remove(0);
                            //Verificamos si el tokenActual es del tipo IDENTIFICADOR o sea un nombre de variable
                            if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                                //Como es un nombre de variable debemos verificar que haya sido declarada con anteriormente
                                //para ello consultamos el ArrayList de nombre "variablesDeclaradas"
                                existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                                if (!existeVariableDeclarada) {
                                    //La variable no ha sido declarada con anterioridad => no puede usarse => ERROR
                                    e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                                    erroresEncontrados.add(e);
                                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                    this.existenErroresEnArchivoOriginal = true;
                                    ++this.numeroErroresEnArchivoOriginal;

                                }
                            } else if (tknActual.getTipo().equals(Tipos.COLOR)) {
                                e = new MiError(linea, " ERROR 160: un color valido no puede ser utilizado como valor de la variable");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                this.existenErroresEnArchivoOriginal = true;
                                ++this.numeroErroresEnArchivoOriginal;

                            } else if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                                e = new MiError(linea, " ERROR 162: un comando de hugo no puede ser usado como valor");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                this.existenErroresEnArchivoOriginal = true;
                                ++this.numeroErroresEnArchivoOriginal;

                            } else if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                                e = new MiError(linea, " ERROR 163: un comando de logo no puede ser usado como valor");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;

                            } else {
                                //El token encontrado no es del tipo IDENTIFICADOR => no es un nombre de variable valido

                                e = new MiError(linea, " ERROR 110: se require una variable o identificador valido");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                this.existenErroresEnArchivoOriginal = true;
                                ++this.numeroErroresEnArchivoOriginal;

                            }
                        } else {
                            //
                            e = new MiError(linea, " Error 112: se require un argumento entero o una variable declarada previamente para este comando");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            this.existenErroresEnArchivoOriginal = true;
                            ++this.numeroErroresEnArchivoOriginal;

                        }
                        break;
                    case COLOR:
                        e = new MiError(linea, " ERROR 134: falta el operador de asignacion de poder utilizar una variable ya declarada");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        e = new MiError(linea, " ERROR 160: un color valido no puede ser utilizado como valor de la variable");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        break;
                    case COMANDOHUGO:
                        e = new MiError(linea, " ERROR 134: falta el operador de asignacion de poder utilizar una variable ya declarada");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        e = new MiError(linea, " ERROR 162: un comando de hugo no puede ser usado como valor");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        break;
                    case COMANDOLOGO:
                        e = new MiError(linea, " ERROR 134: falta el operador de asignacion de poder utilizar una variable ya declarada");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        e = new MiError(linea, " ERROR 163: un comando de logo no puede ser usado como valor");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        existenErroresEnArchivoOriginal = true;
                        ++numeroErroresEnArchivoOriginal;
                        break;
                    default:
                        //Como el token no era entero, se espera el uso de una variable declarada, por lo tanto debe estar el operador de asignacion (:)
                        e = new MiError(linea, " ERROR 134: falta el operador de asignacion de poder utilizar una variable ya declarada");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;
                        break;
                }

            } else {
                //El siguiente token esta en otra linea y todavia faltan argumentos
                e = new MiError(linea, " Error 112: se require un argumento entero o una variable declarada previamente para este comando");
                erroresEncontrados.add(e);
                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                existenErroresEnArchivoOriginal = true;
                ++numeroErroresEnArchivoOriginal;
            }
        } //fin if isEmpty

        if (!existenErroresEnArchivoOriginal && !estamosEnRepite) {

            listaContenidoFinalSinErrores.add(nuevoContenido);
        }
        return nuevoContenido;
    }

    public LineaContenido casoComandoSinArgumento(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin) {
        // Esta funcion en llamada en el caso de comandos que no tiene un argumento 
        //La sintaxis de este tipo de comandos es -> NOMBRECOMANDO 

        //Token siguiente esperado -> NINGUNO 
        int linea = tknActual.getLinea();

        LineaContenido nuevoContenido;
        nuevoContenido = buscarInstruccion(tknActual);

        MiError e = new MiError();
        Token tknSigte = new Token();

        if (!nuevaListaTokens.isEmpty()) {
            //Token siguiente esperado -> NINGUNO 
            tknSigte = nuevaListaTokens.get(0);

            //Revisamos si sigamos en la misma linea
            if (tknSigte.getLinea() == linea) {
                while (tknSigte.getLinea() == linea) {
                    tknActual = nuevaListaTokens.remove(0);
                    if (nuevaListaTokens.size() > 0) {
                        tknSigte = nuevaListaTokens.get(0);
                    }
                }
                e = new MiError(linea, " Error 111: este comando no admite argumentos");
                erroresEncontrados.add(e);
                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                this.existenErroresEnArchivoOriginal = true;
                ++this.numeroErroresEnArchivoOriginal;

            }

        }

        if (!existenErroresEnArchivoOriginal) {

            listaContenidoFinalSinErrores.add(nuevoContenido);
        }
        return nuevoContenido;

    }

    public LineaContenido casoComandoRellena(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin, boolean existePonColorRelleno) {

        int linea = tknActual.getLinea();

        LineaContenido nuevoContenido;
        nuevoContenido = buscarInstruccion(tknActual);

        MiError e = new MiError();
        Token tknSigte = new Token();

        if (!nuevaListaTokens.isEmpty()) {
            //Token siguiente esperado -> NINGUNO 
            tknSigte = nuevaListaTokens.get(0);

            //Revisamos si sigamos en la misma linea
            if (tknSigte.getLinea() == linea) {
                while (tknSigte.getLinea() == linea) {
                    tknActual = nuevaListaTokens.remove(0);
                    if (nuevaListaTokens.size() > 0) {
                        tknSigte = nuevaListaTokens.get(0);
                    }
                }
                e = new MiError(linea, " Error 111: este comando no admite argumentos");
                erroresEncontrados.add(e);
                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                this.existenErroresEnArchivoOriginal = true;
                ++this.numeroErroresEnArchivoOriginal;

            }

        }

        //ESTA FUNCION NECESITA QUE ANTES SE HAYA FIJADO UN COLOR PARA EL RELLENO
        //USANDO LA FUNCION PONCOLORRELLENO -> PONCOLORRELLENO color/n/:variable
        if (!existePonColorRelleno) {
            //Como no se encontro el comando PONCOLORRELLENA dentro de las instrucciones del programa => NUEVO ERROR
            e = new MiError(linea, " ERROR 155: se requiere establecer previamente el color para el relleno");
            erroresEncontrados.add(e);
            nuevoContenido.setErroresEncontrados(erroresEncontrados);
            this.existenErroresEnArchivoOriginal = true;
            ++this.numeroErroresEnArchivoOriginal;

        }
        if (!existenErroresEnArchivoOriginal) {
            listaContenidoFinalSinErrores.add(nuevoContenido);
        }
        return nuevoContenido;
    }

    public LineaContenido casoPonColorRelleno(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin) {

        int linea = tknActual.getLinea();
        String nombreComando = tknActual.getNombre();

        LineaContenido nuevoContenido;
        nuevoContenido = buscarInstruccion(tknActual);

        MiError e = new MiError();
        Token tknSigte = new Token();

        if (!nuevaListaTokens.isEmpty()) {
            //Token esperado debe ser tipo COLOR
            tknSigte = nuevaListaTokens.get(0);

            //Revisamos que sigamos en la misma linea
            if (tknSigte.getLinea() == linea) {
                // Como sigue siendo un argumento de PONCOLORRELLENO lo removemos de la lista de tokens para analizarlo
                tknActual = nuevaListaTokens.remove(0);

                if (tknActual.getTipo().equals(Tipos.COLOR)) {
                    //El argumento corresponde a un color valido de HUGO => lo aceptamos
                } else if (tknActual.getTipo().equals(Tipos.ASIGNACION)) {
                    //Encontramos el token de asignacion => el argumento del comando es una variable ya declarada
                    //lo aceptamos y seguimos a revisar el siguiente token
                    //Token esperado debe ser NOMBRE DE VARIABLE ya declarada
                    tknSigte = nuevaListaTokens.get(0);
                    //Verificamos si el tokenSiguiente esta en la misma linea del comando
                    if (tknSigte.getLinea() == linea) {
                        //El token esta en la misma linea, por lo tanto, es un argumento del comando => removerlo para analisis
                        tknActual = nuevaListaTokens.remove(0);
                        //Verificamos si el tokenActual es del tipo IDENTIFICADOR o sea un nombre de variable
                        if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                            //Como es un nombre de variable debemos verificar que haya sido declarada con anteriormente
                            //para ello consultamos el ArrayList de nombre "variablesDeclaradas"
                            boolean existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                            if (!existeVariableDeclarada) {
                                //La variable no ha sido declarada con anterioridad => no puede usarse => ERROR
                                e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;

                            }
                        } else if (tknActual.getTipo().equals(Tipos.COLOR)) {
                            e = new MiError(linea, " ERROR 160: un color valido no puede ser utilizado como valor de la variable");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;

                        } else if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                            e = new MiError(linea, " ERROR 162: un comando de hugo no puede ser usado como valor");
                            erroresEncontrados.add(e);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);

                        } else if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                            e = new MiError(linea, " ERROR 163: un comando de logo no puede ser usado como valor");
                            erroresEncontrados.add(e);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);

                        } else {
                            //El token encontrado no es del tipo IDENTIFICADOR => no es un nombre de variable valido

                            e = new MiError(linea, " ERROR 110: se require una variable o identificador valido");
                            erroresEncontrados.add(e);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);

                        }
                    } else {
                        e = new MiError(linea, " ERROR 128: se esperaba un identificador valido ");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;

                    }
                } else {

                    e = new MiError(linea, " ERROR 137: la funcion requiere como argumento un color valido");
                    erroresEncontrados.add(e);
                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                    this.existenErroresEnArchivoOriginal = true;
                    ++this.numeroErroresEnArchivoOriginal;

                }
            } else {
                //No hay argumento en la funcion poncolorrelleno 

                e = new MiError(linea, " ERROR 137: la funcion requiere como argumento un color valido");
                erroresEncontrados.add(e);
                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                this.existenErroresEnArchivoOriginal = true;
                ++this.numeroErroresEnArchivoOriginal;

            }

        } // fin de if lista vacia

        if (!existenErroresEnArchivoOriginal) {

            String nombreColor = tknActual.getNombre();
            Colores colors = new Colores();
            int numeroColor;
            numeroColor = colors.numeroColorEnLogo(nombreColor);
            LineaContenido nuevoContenidoSinErrores = new LineaContenido();

            nuevoContenidoSinErrores.setLinea(tknActual.getLinea());
            nuevoContenidoSinErrores.setInstruccion(nombreComando + " " + String.valueOf(numeroColor));

            listaContenidoFinalSinErrores.add(nuevoContenidoSinErrores);
        }
        return nuevoContenido;
    }

    public LineaContenido casoPonColorLapiz(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin) {

        //Token siguiente esperado debe ser tipo COLOR
        int linea = tknActual.getLinea();
        String nombreComando = tknActual.getNombre();

        LineaContenido nuevoContenido;
        nuevoContenido = buscarInstruccion(tknActual);

        MiError e = new MiError();
        Token tknSigte = new Token();

        if (!nuevaListaTokens.isEmpty()) {
            //Token esperado debe ser tipo COLOR
            tknSigte = nuevaListaTokens.get(0);

            //Revisamos que sigamos en la misma linea
            if (tknSigte.getLinea() == linea) {
                // Como sigue siendo un argumento de PONCOLORRELLENO lo removemos de la lista de tokens para analizarlo
                tknActual = nuevaListaTokens.remove(0);

                if (tknActual.getTipo().equals(Tipos.COLOR)) {
                    //El argumento corresponde a un color valido de HUGO => lo aceptamos
                } else if (tknActual.getTipo().equals(Tipos.ASIGNACION)) {
                    //Encontramos el token de asignacion => el argumento del comando es una variable ya declarada
                    //lo aceptamos y seguimos a revisar el siguiente token
                    //Token esperado debe ser NOMBRE DE VARIABLE ya declarada
                    tknSigte = nuevaListaTokens.get(0);
                    //Verificamos si el tokenSiguiente esta en la misma linea del comando
                    if (tknSigte.getLinea() == linea) {
                        //El token esta en la misma linea, por lo tanto, es un argumento del comando => removerlo para analisis
                        tknActual = nuevaListaTokens.remove(0);
                        //Verificamos si el tokenActual es del tipo IDENTIFICADOR o sea un nombre de variable
                        if (tknActual.getTipo().equals(Tipos.IDENTIFICADOR)) {
                            //Como es un nombre de variable debemos verificar que haya sido declarada con anteriormente
                            //para ello consultamos el ArrayList de nombre "variablesDeclaradas"
                            boolean existeVariableDeclarada = consultaVariablesDeclaradas(tknActual.getNombre(), linea, variablesDeclaradas);
                            if (!existeVariableDeclarada) {
                                //La variable no ha sido declarada con anterioridad => no puede usarse => ERROR
                                e = new MiError(linea, " ERROR 123: la variable no ha sido declarada previamente");
                                erroresEncontrados.add(e);
                                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                                existenErroresEnArchivoOriginal = true;
                                ++numeroErroresEnArchivoOriginal;

                            }
                        } else if (tknActual.getTipo().equals(Tipos.COLOR)) {
                            e = new MiError(linea, " ERROR 160: un color valido no puede ser utilizado como valor de la variable");
                            erroresEncontrados.add(e);
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;

                        } else if (tknActual.getTipo().equals(Tipos.COMANDOHUGO)) {
                            e = new MiError(linea, " ERROR 162: un comando de hugo no puede ser usado como valor");
                            erroresEncontrados.add(e);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);

                        } else if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                            e = new MiError(linea, " ERROR 163: un comando de logo no puede ser usado como valor");
                            erroresEncontrados.add(e);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);

                        } else {
                            //El token encontrado no es del tipo IDENTIFICADOR => no es un nombre de variable valido

                            e = new MiError(linea, " ERROR 110: se require una variable o identificador valido");
                            erroresEncontrados.add(e);
                            existenErroresEnArchivoOriginal = true;
                            ++numeroErroresEnArchivoOriginal;
                            nuevoContenido.setErroresEncontrados(erroresEncontrados);

                        }
                    } else {
                        e = new MiError(linea, " ERROR 128: se esperaba un identificador valido ");
                        erroresEncontrados.add(e);
                        nuevoContenido.setErroresEncontrados(erroresEncontrados);
                        this.existenErroresEnArchivoOriginal = true;
                        ++this.numeroErroresEnArchivoOriginal;

                    }
                } else {

                    e = new MiError(linea, " ERROR 137: la funcion requiere como argumento un color valido");
                    erroresEncontrados.add(e);
                    nuevoContenido.setErroresEncontrados(erroresEncontrados);
                    this.existenErroresEnArchivoOriginal = true;
                    ++this.numeroErroresEnArchivoOriginal;

                }
            } else {
                //No hay argumento en la funcion poncolorrelleno 

                e = new MiError(linea, " ERROR 137: la funcion requiere como argumento un color valido");
                erroresEncontrados.add(e);
                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                this.existenErroresEnArchivoOriginal = true;
                ++this.numeroErroresEnArchivoOriginal;

            }

        } // fin de if lista vacia

        if (!existenErroresEnArchivoOriginal) {

            String nombreColor = tknActual.getNombre();
            Colores colors = new Colores();
            int numeroColor;
            numeroColor = colors.numeroColorEnLogo(nombreColor);
            LineaContenido nuevoContenidoSinErrores = new LineaContenido();

            nuevoContenidoSinErrores.setLinea(tknActual.getLinea());
            nuevoContenidoSinErrores.setInstruccion(nombreComando + " " + String.valueOf(numeroColor));

            listaContenidoFinalSinErrores.add(nuevoContenidoSinErrores);
        }
        return nuevoContenido;
    }

    public LineaContenido casoInstruccionSoloValidaEnLogo(Token tknActual, List<MiError> erroresEncontrados, List<Token> nuevaListaTokens, boolean posicionFin, boolean existenErroresEnArchivoOriginal) {

        //Token siguiente esperado = NINGUNO
        int linea = tknActual.getLinea();

        //erroresEncontrados = new ArrayList<MiError>();
        LineaContenido nuevoContenido;
        nuevoContenido = buscarInstruccion(tknActual);

        MiError e = new MiError();
        Token tknSigte = new Token();

        if (!nuevaListaTokens.isEmpty()) {
            //Token siguiente esperado -> NINGUNO 
            tknSigte = nuevaListaTokens.get(0);

            //Revisamos si sigamos en la misma linea
            if (tknActual.getTipo().equals(Tipos.COMANDOLOGO)) {
                //Encontramos un comando solo valido en logo

                e = new MiError(linea, " Advertencia: instrucción " + tknActual.getNombre() + " no es soportada por esta versión");
                erroresEncontrados.add(e);
                nuevoContenido.setErroresEncontrados(erroresEncontrados);
                this.existenErroresEnArchivoOriginal = true;
                ++this.numeroErroresEnArchivoOriginal;

            }

            //Revisamos si sigamos en la misma linea y aparecen otros argumentos => removerlos
            if (tknSigte.getLinea() == linea) {
                while (tknSigte.getLinea() == linea) {
                    tknActual = nuevaListaTokens.remove(0);
                    if (nuevaListaTokens.size() > 0) {
                        tknSigte = nuevaListaTokens.get(0);
                    }
                }
            }

        }

        if (!existenErroresEnArchivoOriginal) {
            listaContenidoFinalSinErrores.add(nuevoContenido);
        }
        return nuevoContenido;
    }

    public LineaContenido buscarInstruccion(Token tknActual) {

        int linea = tknActual.getLinea();

        LineaContenido nuevo = new LineaContenido();
        List<LineaContenido> contenidoFinal = this.getListaContenidoFinal();
        for (int i = 0; i < contenidoFinal.size(); ++i) {
            if (contenidoFinal.get(i).getLinea() == linea) {
                nuevo = (LineaContenido) contenidoFinal.get(i);

                break;
            }
        }
        return nuevo;
    }

    public List<MiError> buscarInstruccion2(Token tknActual) {

        int linea = tknActual.getLinea();

        LineaContenido nuevo = new LineaContenido();
        List<LineaContenido> contenidoFinal = this.getListaContenidoFinal();

        for (int i = 0; i < contenidoFinal.size(); ++i) {
            if (contenidoFinal.get(i).getLinea() == linea) {
                nuevo = (LineaContenido) contenidoFinal.get(i);

                break;
            }
        }
        return nuevo.getErroresEncontrados();
    }

    //Funcion para verificar si una variable ya fue declarada previamente
    //Devuelve true si encuentra que la cantidad de tokens con el nombre de la 
    //variable en mayor que 1
    public boolean consultaVariablesDeclaradas(String variable, int linea, ArrayList<String> variablesDeclaradas) {
        boolean existe = false;
        int cantidad = 0;
        for (String var : variablesDeclaradas) {
            if (var.equals(variable)) {
                ++cantidad;
            }
        }

        return existe = cantidad > 0; //Encontro una variable declarada previamente
    }

    public boolean existeComandoFin() {
        boolean existeFin = false;
        Token tkn;

        Iterator<Token> iterator = listaTokens.iterator();
        while (iterator.hasNext()) {
            tkn = (Token) iterator.next();
            if (tkn.getNombre().equals("FIN")) {
                existeFin = true;
                break;
            }
        }
        return existeFin;
    }

    //Verifica que la posicion del comando FIN sea en la ultima instruccion
    public boolean lineaDelComandoFin(Token tknActual) {

        boolean finUltimaInstruccion;

        Token ultimo = listaTokens.get(listaTokens.size() - 1);

        //System.out.println("posicionComandoFin-EL VALOR DE INDEX ES-> " + index);
        //Verificamos que la linea en la que esta la ultima ocurrencia de FIN es igual a la linea donde esta el ultimo token de la lista
        //y que la posicion de ambos tokens en la linea sea la misma, de lo contrario hay argumentos en fin o hay mas tokens despues de FIN
        finUltimaInstruccion = tknActual.getLinea() == ultimo.getLinea();

        //finUltimaInstruccion es true si fin esta en la ultima linea de instruccion, es decir, en la posicion correcta
        //finUltimaInstruccion es false fin no esta en la ultima linea de instruccion, es decir, en una posicin incorrecta
        return finUltimaInstruccion;
    }

    public boolean existeFinComoUltimaInstruccion() {

        boolean ultimaInstruccionEsFin = false;

        int index = -1; // = listaTokens.lastIndexOf(token.getNombre().equalsIgnoreCase("FIN"));
        Token token = new Token();
        Token fin = new Token();

        Iterator<Token> iterator = listaTokens.iterator();
        while (iterator.hasNext()) {
            token = (Token) iterator.next();
            if (token.getNombre().equalsIgnoreCase("FIN")) {
                index = listaTokens.lastIndexOf(token);
                //break;
            }
        }

        Token ultimo = listaTokens.get(listaTokens.size() - 1);
        if (index != -1) {
            fin = listaTokens.get(index);
            ultimaInstruccionEsFin = fin.getLinea() == ultimo.getLinea();
        }

        return ultimaInstruccionEsFin;
    }

    public boolean finEsUltimoToken() {

        boolean finUltimoToken;
        int index = -1;
        Token token = new Token();
        Iterator<Token> iterator = listaTokens.iterator();
        while (iterator.hasNext()) {
            token = (Token) iterator.next();
            if (token.getNombre().equalsIgnoreCase("FIN")) {
                index = listaTokens.lastIndexOf(token);;
                break;
            }
        }
        Token fin = listaTokens.get(index);
        Token ultimo = listaTokens.get(listaTokens.size() - 1);

        //Verificamos que la linea en la que esta la ultima ocurrencia de FIN es igual a la linea donde esta el ultimo token de la lista
        //y que la posicion de ambos tokens en la linea sea la misma, de lo contrario hay argumentos en fin o hay mas tokens despues de FIN
        finUltimoToken = fin.getLinea() == ultimo.getLinea() && fin.getPosicion() == ultimo.getPosicion();

        return finUltimoToken;
    }

    public int cantidadComandosFin() {
        int cantidad = 0;
        Token token = new Token();
        Iterator<Token> iterator = listaTokens.iterator();
        while (iterator.hasNext()) {
            token = (Token) iterator.next();
            if (token.getNombre().equalsIgnoreCase("FIN")) {
                ++cantidad;
            }
        }

        return cantidad;
    }

    public boolean existeComandoPara() {
        boolean existePara = false;
        Token tkn;
        Iterator<Token> iterator = listaTokens.iterator();
        while (iterator.hasNext()) {
            tkn = (Token) iterator.next();
            if (tkn.getNombre().equals("FIN")) {
                existePara = true;
                break;
            }
        }
        return existePara;
    }

    public boolean existeParaComoPrimeraInstruccion() {

        Token primerToken = listaTokens.get(0);

        return primerToken.getNombre().equals("PARA");
    }

    public int cantidadComandosPara() {
        int cantidad = 0;
        Token token = new Token();
        Iterator<Token> iterator = listaTokens.iterator();
        while (iterator.hasNext()) {
            token = (Token) iterator.next();
            if (token.getNombre().equalsIgnoreCase("PARA")) {
                ++cantidad;
            }
        }

        return cantidad;
    }

    public boolean lineaDelComandoPara(Token tknActual) {

        boolean paraPrimeraInstruccion;

        Token primero = listaTokens.get(0);

        //Verificamos que la linea en la que esta la ultima ocurrencia de FIN es igual a la linea donde esta el ultimo token de la lista
        //y que la posicion de ambos tokens en la linea sea la misma, de lo contrario hay argumentos en fin o hay mas tokens despues de FIN
        paraPrimeraInstruccion = tknActual.getLinea() == primero.getLinea();

        //paraPrimerInstruccion es true si fin esta en la primera linea de instruccion, es decir, en la posicion correcta
        //paraPrimeraInstruccion es false fin no esta en la primera linea de instruccion, es decir, en una posicin incorrecta
        return paraPrimeraInstruccion;
    }

    public boolean posicionComandoPara() {
        boolean posicionFin = listaTokens.get(0).getNombre().equals("PARA");
        return posicionFin;
    }

    public static void crearArchivoConErrores(List<LineaContenido> archivo, String nombreArchivoOriginal) throws IOException {
        //String ruta = "C:\\Users\\pc\\Desktop\\hexagono8-Hugo-Errores.txt";
        int index = nombreArchivoOriginal.indexOf(".");
        String nombreSinExtension = nombreArchivoOriginal.substring(0, index);

        //El  archivoErrores contiene la localizacion del resultado del compilador
        String nombreArchivoConErrores = nombreSinExtension + "-Hugo-Errores.txt";
        String rutaArchivoErrores = "C:\\Program Files (x86)\\MSWLogo\\" + nombreArchivoConErrores;

        List<String> texts = new ArrayList<>();

        for (int i = 0; i < archivo.size(); ++i) {
            if (archivo.get(i).getErroresEncontrados() == null) {
                String instruccion = archivo.get(i).getLinea() + " " + archivo.get(i).getInstruccion();
                texts.add(instruccion);
            } else {
                String instruccion = archivo.get(i).getLinea() + " " + archivo.get(i).getInstruccion();
                texts.add(instruccion);
                for (int k = 0; k < archivo.get(i).getErroresEncontrados().size(); ++k) {
                    String errores = "    " + archivo.get(i).getErroresEncontrados().get(k).getError();
                    texts.add(errores);
                }
            }
        }

        //archivo.forEach((LineaContenido linea) -> texts.add(linea.toStringConErrores()));
        Path destino = Paths.get(rutaArchivoErrores);
        Charset cs = Charset.forName("US-ASCII");
        try {
            Path p;
            p = Files.write(destino, texts,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            List<String> command = new ArrayList<>();
            command.add("cmd.exe");
            command.add("/c");
            command.add("cls");

            ProcessBuilder process;
            process = new ProcessBuilder(command);
            process.redirectErrorStream(true);
            process.start();

            System.out.println("El programa " + nombreArchivoOriginal + " contiene errores");
            System.out.println("Puede revisar el analisis, abriendo el archivo " + rutaArchivoErrores);
        } catch (IOException e) {
            System.out.println("Se produjo un error al crear el archivo de errores " + e);
        }

    }

    public static void crearArchivoSinErrores(List<LineaContenido> archivo, String nombreArchivoOriginal) throws IOException {

        int index = nombreArchivoOriginal.indexOf(".");
        String nombreSinExtension = nombreArchivoOriginal.substring(0, index);

        //El  archivoErrores contiene la localizacion del resultado del compilador
        String nombreArchivoSinErrores = nombreSinExtension + ".lgo";
        String rutaArchivoSinErrores = "C:\\Program Files (x86)\\MSWLogo\\" + nombreArchivoSinErrores;

        List<String> texts = new ArrayList<>();
        archivo.forEach((LineaContenido linea) -> texts.add(linea.toString()));

        Path destino = Paths.get(rutaArchivoSinErrores);
        Charset cs = Charset.forName("US-ASCII");
        try {
            Path p;
            p = Files.write(destino, texts,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            //Como el archivo fuente no tiene errores y ya se creo el .lgo procedemos a cargarlo y ejecutarlo
            //Utilizamos el objeto "process" de la clase ProcessBuilder para ejecutar
            //la lista de comandos contenido en "command" de esta forma podemos ejecutar
            //comandos en el el "cmd"
            List<String> command = new ArrayList<>();
            command.add("cmd.exe");
            command.add("/c");
            command.add("cd \"C:\\Program Files (x86)\\MSWLogo\" && logo32.exe");
            command.add("-l" + nombreArchivoSinErrores);
            ProcessBuilder process;
            process = new ProcessBuilder(command);
            process.redirectErrorStream(true);
            process.start();

        } catch (IOException e) {
            System.out.println("Se produjo un error al crear el archivo sin errores " + e);
        }

    }

    public List<Token> getListaTokens() {
        return listaTokens;
    }

    public void setListaTokens(List<Token> listaTokens) {
        this.listaTokens = listaTokens;
    }

    public List<MiError> getListaErrores() {
        return listaErrores;
    }

    public void setListaErrores(List<MiError> listaErrores) {
        this.listaErrores = listaErrores;
    }

    public List<LineaContenido> getListaContenidoFinal() {
        return listaContenidoFinal;
    }

    public void setListaContenidoFinal(List<LineaContenido> listaContenidoFinal) {
        this.listaContenidoFinal = listaContenidoFinal;
    }

} //FIN DE Analizador Sintactico


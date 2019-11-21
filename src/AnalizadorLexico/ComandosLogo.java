/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorLexico;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author PC
 */
public final class ComandosLogo {

    private List<ComandoLogo> listaComandosLogo;

    public ComandosLogo()  {
        this.listaComandosLogo = agregarComandosDeLogo();
    }


    public List<ComandoLogo> agregarComandosDeLogo()  {
        
        List<ComandoLogo> lista= new ArrayList();
        lista.add(new ComandoLogo("ABIERTOS", 0));
        lista.add(new ComandoLogo("ABRE", 0));
        lista.add(new ComandoLogo("ABREACTUALIZAR", 0));
        lista.add(new ComandoLogo("ABREDIALOGO", 0));
        lista.add(new ComandoLogo("ABREMIDI", 0));
        lista.add(new ComandoLogo("ABREPUERTO", 0));
        lista.add(new ComandoLogo("AC", 0));
        lista.add(new ComandoLogo("ACTIVA", 0));
        lista.add(new ComandoLogo("ACTIVAVENTANA", 0));
        lista.add(new ComandoLogo("ACTUALIZABOTON", 0));
        lista.add(new ComandoLogo("ACTUALIZAESTATICO", 0));
        lista.add(new ComandoLogo("ADIOS", 1));
        lista.add(new ComandoLogo("AJUSTA", 0));
        lista.add(new ComandoLogo("ALTO", 0));
        lista.add(new ComandoLogo("ANALIZA", 0));
        lista.add(new ComandoLogo("ANTERIOR", 0));
        lista.add(new ComandoLogo("ANTES", 0));
        lista.add(new ComandoLogo("APLICA", 0));
        lista.add(new ComandoLogo("ARCCOS", 0));
        lista.add(new ComandoLogo("ARCODEELIPSE", 0));
        lista.add(new ComandoLogo("ARCSEN", 0));
        lista.add(new ComandoLogo("ARCTAN", 0));
        lista.add(new ComandoLogo("AREAACTIVA", 0));
        lista.add(new ComandoLogo("ARREGLO", 0));
        lista.add(new ComandoLogo("ASCII", 0));
        lista.add(new ComandoLogo("ATRAPA", 0));
        lista.add(new ComandoLogo("ATRAS", 0));
        lista.add(new ComandoLogo("AV", 1));
        lista.add(new ComandoLogo("AVANZA", 1));
        lista.add(new ComandoLogo("AYUDA", 0));
        lista.add(new ComandoLogo("AYUDADEWINDOWS", 0));
        lista.add(new ComandoLogo("AZAR", 0));
        lista.add(new ComandoLogo("AÑADECADENALISTBOX", 0));
        lista.add(new ComandoLogo("AÑADELINEACOMBOBOX", 0));
        lista.add(new ComandoLogo("BA", 0));
        lista.add(new ComandoLogo("BAJALAPIZ", 1));
        lista.add(new ComandoLogo("BAJAN", 0));
        lista.add(new ComandoLogo("BAJANARIZ", 0));
        lista.add(new ComandoLogo("BAL", 0));
        lista.add(new ComandoLogo("BALANCEA", 0));
        lista.add(new ComandoLogo("BALANCEAIZQUIERDA", 0));
        lista.add(new ComandoLogo("BALANCEO", 0));
        lista.add(new ComandoLogo("BARRERA", 0));
        lista.add(new ComandoLogo("BITINVERSO", 0));
        lista.add(new ComandoLogo("BITO", 0));
        lista.add(new ComandoLogo("BITXOR", 0));
        lista.add(new ComandoLogo("BITY", 0));
        lista.add(new ComandoLogo("BL", 1));
        lista.add(new ComandoLogo("BO", 0));
        lista.add(new ComandoLogo("BOARCHIVO", 0));
        lista.add(new ComandoLogo("BORRA ", 0));
        lista.add(new ComandoLogo("BORRABARRADESPLAZAMIENTO", 0));
        lista.add(new ComandoLogo("BORRABOTON", 0));
        lista.add(new ComandoLogo("BORRABOTONRADIO", 0));
        lista.add(new ComandoLogo("BORRACADENALISTBOX", 0));
        lista.add(new ComandoLogo("BORRACHECKBOX", 0));
        lista.add(new ComandoLogo("BORRACOMBOBOX", 0));
        lista.add(new ComandoLogo("BORRADIALOGO", 0));
        lista.add(new ComandoLogo("BORRADIR", 0));
        lista.add(new ComandoLogo("BORRAESTATICO", 0));
        lista.add(new ComandoLogo("BORRAGROUPBOX", 0));
        lista.add(new ComandoLogo("BORRALINEACOMBOBOX", 0));
        lista.add(new ComandoLogo("BORRALISTBOX", 0));
        lista.add(new ComandoLogo("BORRAPALETA", 0));
        lista.add(new ComandoLogo("BORRAPANTALLA", 1));
        lista.add(new ComandoLogo("BP", 1));
        lista.add(new ComandoLogo("BORRAR", 0));
        lista.add(new ComandoLogo("BORRARARCHIVO", 0));
        lista.add(new ComandoLogo("BORRATEXTO", 0));
        lista.add(new ComandoLogo("BORRAVENTANA", 0));
        lista.add(new ComandoLogo("BOTON", 0));
        lista.add(new ComandoLogo("BT", 0));
        lista.add(new ComandoLogo("CABECEA", 0));
        lista.add(new ComandoLogo("CABECEO", 0));
        lista.add(new ComandoLogo("CAI", 0));
        lista.add(new ComandoLogo("CAMBIADIRECTORIO", 0));
        lista.add(new ComandoLogo("CAMBIASIGNO", 0));
        lista.add(new ComandoLogo("CAR", 0));
        lista.add(new ComandoLogo("CARACTER", 0));
        lista.add(new ComandoLogo("CARGA", 0));
        lista.add(new ComandoLogo("CARGADIB", 0));
        lista.add(new ComandoLogo("CARGADIBTAMAÑO", 0));
        lista.add(new ComandoLogo("CARGADLL", 0));
        lista.add(new ComandoLogo("CARGAGIF", 0));
        lista.add(new ComandoLogo("CD", 0));
        lista.add(new ComandoLogo("CENTRO", 1));
        lista.add(new ComandoLogo("CERCA", 0));
        lista.add(new ComandoLogo("CIERRA", 0));
        lista.add(new ComandoLogo("CIERRAMIDI", 0));
        lista.add(new ComandoLogo("CIERRAPUERTO", 0));
        lista.add(new ComandoLogo("CL", 0));
        lista.add(new ComandoLogo("CO", 0));
        lista.add(new ComandoLogo("COGE", 0));
        lista.add(new ComandoLogo("COLORLAPIZ", 0));
        lista.add(new ComandoLogo("COLORPAPEL", 0));
        lista.add(new ComandoLogo("COLORRELLENO", 0));
        lista.add(new ComandoLogo("COMODEVUELVE", 0));
        lista.add(new ComandoLogo("CONTADORACERO", 0));
        lista.add(new ComandoLogo("CONTENIDO", 0));
        lista.add(new ComandoLogo("CONTINUA", 0));
        lista.add(new ComandoLogo("COPIAAREA", 0));
        lista.add(new ComandoLogo("COPIADEF", 0));
        lista.add(new ComandoLogo("CORTAAREA", 0));
        lista.add(new ComandoLogo("CREABARRADESPLAZAMIENTO", 0));
        lista.add(new ComandoLogo("CREABOTON", 0));
        lista.add(new ComandoLogo("CREABOTONRADIO", 0));
        lista.add(new ComandoLogo("CREACHECKBOX", 0));
        lista.add(new ComandoLogo("CREACOMBOBOX", 0));
        lista.add(new ComandoLogo("CREADIALOGO", 0));
        lista.add(new ComandoLogo("CREADIR", 0));
        lista.add(new ComandoLogo("CREADIRECTORIO", 0));
        lista.add(new ComandoLogo("CREAESTATICO", 0));
        lista.add(new ComandoLogo("CREAGROUPBOX", 0));
        lista.add(new ComandoLogo("CREALISTBOX", 0));
        lista.add(new ComandoLogo("CREAVENTANA", 0));
        lista.add(new ComandoLogo("CS", 0));
        lista.add(new ComandoLogo("CUENTA", 0));
        lista.add(new ComandoLogo("CUENTAREPITE", 0));
        lista.add(new ComandoLogo("CURSOR", 0));
        lista.add(new ComandoLogo("DEFINE", 0));
        lista.add(new ComandoLogo("DEFINEMACRO", 0));
        lista.add(new ComandoLogo("DEFINIDO", 0));
        lista.add(new ComandoLogo("DEFINIDOP", 0));
        lista.add(new ComandoLogo("DESPLAZA", 0));
        lista.add(new ComandoLogo("DESPLAZAIZQUIERDA", 0));
        lista.add(new ComandoLogo("DESPLAZAX", 0));
        lista.add(new ComandoLogo("DESPLAZAY", 0));
        lista.add(new ComandoLogo("DESTAPA", 0));
        lista.add(new ComandoLogo("DEV", 0));
        lista.add(new ComandoLogo("DEVUELVE", 0));
        lista.add(new ComandoLogo("DIFERENCIA", 0));
        lista.add(new ComandoLogo("DIRECTORIO", 0));
        lista.add(new ComandoLogo("DIRECTORIOPADRE", 0));
        lista.add(new ComandoLogo("DIRECTORIOS", 0));
        lista.add(new ComandoLogo("DIVISION", 0));
        lista.add(new ComandoLogo("ED", 0));
        lista.add(new ComandoLogo("EDITA", 0));
        lista.add(new ComandoLogo("EDITAFICHERO", 0));
        lista.add(new ComandoLogo("EJECUTA", 0));
        lista.add(new ComandoLogo("EJECUTAANALIZA", 0));
        lista.add(new ComandoLogo("ELEMENTO", 0));
        lista.add(new ComandoLogo("EMPIEZAPOLIGONO", 0));
        lista.add(new ComandoLogo("ENCADENA", 0));
        lista.add(new ComandoLogo("ENTERO", 0));
        lista.add(new ComandoLogo("ENVIA", 0));
        lista.add(new ComandoLogo("ENVIAVALORRED", 0));
        lista.add(new ComandoLogo("ENVOLVER", 0));
        lista.add(new ComandoLogo("ERROR", 0));
        lista.add(new ComandoLogo("ESCRIBE", 0));
        lista.add(new ComandoLogo("ESCRIBEBOTONRADIO", 0));
        lista.add(new ComandoLogo("ESCRIBECADENAPUERTO", 0));
        lista.add(new ComandoLogo("ESCRIBECARACTERPUERTO", 0));
        lista.add(new ComandoLogo("ESCRIBEPUERTO", 0));
        lista.add(new ComandoLogo("ESCRIBEPUERTO2", 0));
        lista.add(new ComandoLogo("ESCRIBERED", 0));
        lista.add(new ComandoLogo("ESCRIBIRARCHIVO", 0));
        lista.add(new ComandoLogo("ESCRITURA", 0));
        lista.add(new ComandoLogo("ESPERA", 0));
        lista.add(new ComandoLogo("ESTADO", 0));
        lista.add(new ComandoLogo("ESTADOCHECKBOX", 0));
        lista.add(new ComandoLogo("EXCLUSIVO", 0));
        lista.add(new ComandoLogo("EXP", 0));
        lista.add(new ComandoLogo("FIN", 1));
        lista.add(new ComandoLogo("FINLEC", 0));
        lista.add(new ComandoLogo("FINRED", 0));
        lista.add(new ComandoLogo("FORMATONUMERO", 0));
        lista.add(new ComandoLogo("FR", 0));
        lista.add(new ComandoLogo("FRASE", 0));
        lista.add(new ComandoLogo("GD", 1));
        lista.add(new ComandoLogo("GI", 1));
        lista.add(new ComandoLogo("GIRADERECHA", 1));
        lista.add(new ComandoLogo("GIRAIZQUIERDA", 1));
        lista.add(new ComandoLogo("GOMA", 1));
        lista.add(new ComandoLogo("GOTEAR", 0));
        lista.add(new ComandoLogo("GROSOR", 0));
        lista.add(new ComandoLogo("GUARDA", 0));
        lista.add(new ComandoLogo("GUARDADIALOGO", 0));
        lista.add(new ComandoLogo("GUARDADIB", 0));
        lista.add(new ComandoLogo("GUARDAGIF", 0));
        lista.add(new ComandoLogo("HABILITABOTON", 0));
        lista.add(new ComandoLogo("HABILITACHECKBOX", 0));
        lista.add(new ComandoLogo("HABILITACOMBOBOX", 0));
        lista.add(new ComandoLogo("HACIA", 0));
        lista.add(new ComandoLogo("HACIAXYZ", 0));
        lista.add(new ComandoLogo("HAZ", 1));
        lista.add(new ComandoLogo("HORA", 0));
        lista.add(new ComandoLogo("HORAMILI", 0));
        lista.add(new ComandoLogo("IG", 0));
        lista.add(new ComandoLogo("IGUAL", 0));
        lista.add(new ComandoLogo("IGUALES", 0));
        lista.add(new ComandoLogo("ILA", 0));
        lista.add(new ComandoLogo("IM", 0));
        lista.add(new ComandoLogo("IMPROP", 0));
        lista.add(new ComandoLogo("IMTS", 0));
        lista.add(new ComandoLogo("IMTSP", 0));
        lista.add(new ComandoLogo("INDICEIMAGEN", 0));
        lista.add(new ComandoLogo("INICIARED", 0));
        lista.add(new ComandoLogo("INVERSOLAPIZ", 0));
        lista.add(new ComandoLogo("IZ", 0));
        lista.add(new ComandoLogo("IZQUIERDA", 0));
        lista.add(new ComandoLogo("LAPIZ", 0));
        lista.add(new ComandoLogo("LAPIZNORMAL", 1));
        lista.add(new ComandoLogo("LC", 0));
        lista.add(new ComandoLogo("LCS", 0));
        lista.add(new ComandoLogo("LECTURA", 0));
        lista.add(new ComandoLogo("LEEBARRADESPLAZAMIENTO", 0));
        lista.add(new ComandoLogo("LEEBOTONRADIO", 0));
        lista.add(new ComandoLogo("LEECADENAPUERTO", 0));
        lista.add(new ComandoLogo("LEECAR", 0));
        lista.add(new ComandoLogo("LEECARACTERPUERTO", 0));
        lista.add(new ComandoLogo("LEECARC", 0));
        lista.add(new ComandoLogo("LEECARCS", 0));
        lista.add(new ComandoLogo("LEEFOCO", 0));
        lista.add(new ComandoLogo("LEELISTA", 0));
        lista.add(new ComandoLogo("LEEPALABRA", 0));
        lista.add(new ComandoLogo("LEEPUERTO", 0));
        lista.add(new ComandoLogo("LEEPUERTO2", 0));
        lista.add(new ComandoLogo("LEEPUERTOJUEGOS", 0));
        lista.add(new ComandoLogo("LEERED", 0));
        lista.add(new ComandoLogo("LEESELECCIONLISTBOX", 0));
        lista.add(new ComandoLogo("LEETECLA", 0));
        lista.add(new ComandoLogo("LEETEXTOCOMBOBOX", 0));
        lista.add(new ComandoLogo("LEEVALORRED", 0));
        lista.add(new ComandoLogo("LIMPIA", 0));
        lista.add(new ComandoLogo("LIMPIAPUERTO", 0));
        lista.add(new ComandoLogo("LISTA", 0));
        lista.add(new ComandoLogo("LISTAARCH", 0));
        lista.add(new ComandoLogo("LL", 0));
        lista.add(new ComandoLogo("LLAMADLL", 0));
        lista.add(new ComandoLogo("LN", 0));
        lista.add(new ComandoLogo("LOCAL", 0));
        lista.add(new ComandoLogo("LOG", 0));
        lista.add(new ComandoLogo("LPROP", 0));
        lista.add(new ComandoLogo("LR", 0));
        lista.add(new ComandoLogo("LUZ", 0));
        lista.add(new ComandoLogo("LVARS", 0));
        lista.add(new ComandoLogo("MACRO", 0));
        lista.add(new ComandoLogo("MATRIZ", 0));
        lista.add(new ComandoLogo("MAYOR", 0));
        lista.add(new ComandoLogo("MAYORQUE", 0));
        lista.add(new ComandoLogo("MAYUSCULAS", 0));
        lista.add(new ComandoLogo("MCI", 0));
        lista.add(new ComandoLogo("MENOR", 0));
        lista.add(new ComandoLogo("MENORQUE", 0));
        lista.add(new ComandoLogo("MENOS", 0));
        lista.add(new ComandoLogo("MENOSPRIMERO", 0));
        lista.add(new ComandoLogo("MENOSPRIMEROS", 0));
        lista.add(new ComandoLogo("MENSAJE", 0));
        lista.add(new ComandoLogo("MENSAJEMIDI", 0));
        lista.add(new ComandoLogo("MIEMBRO", 0));
        lista.add(new ComandoLogo("MINUSCULAS", 0));
        lista.add(new ComandoLogo("MODOBITMAP", 0));
        lista.add(new ComandoLogo("MODOPUERTO", 0));
        lista.add(new ComandoLogo("MODOTORTUGA", 0));
        lista.add(new ComandoLogo("MODOVENTANA", 0));
        lista.add(new ComandoLogo("MODULO", 0));
        lista.add(new ComandoLogo("MP", 0));
        lista.add(new ComandoLogo("MPR", 0));
        lista.add(new ComandoLogo("MPS", 0));
        lista.add(new ComandoLogo("MT", 1));
        lista.add(new ComandoLogo("MU", 0));
        lista.add(new ComandoLogo("MUESTRA", 0));
        lista.add(new ComandoLogo("MUESTRAPOLIGONO", 0));
        lista.add(new ComandoLogo("MUESTRAT", 0));
        lista.add(new ComandoLogo("MUESTRATORTUGA", 1));
        lista.add(new ComandoLogo("NO", 0));
        lista.add(new ComandoLogo("NODOS", 0));
        lista.add(new ComandoLogo("NOESTADO", 0));
        lista.add(new ComandoLogo("NOEXCLUSIVO", 0));
        lista.add(new ComandoLogo("NOGOTEAR", 0));
        lista.add(new ComandoLogo("NOMBRE", 0));
        lista.add(new ComandoLogo("NOMBRES", 0));
        lista.add(new ComandoLogo("NOPAS", 0));
        lista.add(new ComandoLogo("NORED", 0));
        lista.add(new ComandoLogo("NOTRAZA", 0));
        lista.add(new ComandoLogo("NUMERO", 0));
        lista.add(new ComandoLogo("O", 0));
        lista.add(new ComandoLogo("OCULTATORTUGA", 1));
        lista.add(new ComandoLogo("OT", 1));
        lista.add(new ComandoLogo("PALABRA", 0));
        lista.add(new ComandoLogo("PARA", 1));
        lista.add(new ComandoLogo("PARADA", 0));
        lista.add(new ComandoLogo("PASO", 0));
        lista.add(new ComandoLogo("PATRONLAPIZ", 0));
        lista.add(new ComandoLogo("PAUSA", 0));
        lista.add(new ComandoLogo("PEGA", 0));
        lista.add(new ComandoLogo("PEGAENINDICE", 0));
        lista.add(new ComandoLogo("PERSPECTIVA", 0));
        lista.add(new ComandoLogo("PFT", 0));
        lista.add(new ComandoLogo("PINTACOLOR", 0));
        lista.add(new ComandoLogo("PIXEL", 0));
        lista.add(new ComandoLogo("PLA", 0));
        lista.add(new ComandoLogo("POCCR", 0));
        lista.add(new ComandoLogo("PONAREAACTIVA", 0));
        lista.add(new ComandoLogo("PONBALANCEO", 0));
        lista.add(new ComandoLogo("PONBARRADESPLAZAMIENTO", 0));
        lista.add(new ComandoLogo("PONCABECEO", 0));
        lista.add(new ComandoLogo("PONCHECKBOX", 0));
        lista.add(new ComandoLogo("PONCL", 1));
        lista.add(new ComandoLogo("PONCLIP", 0));
        lista.add(new ComandoLogo("PONCOLORLAPIZ", 1));
        lista.add(new ComandoLogo("PONCOLORPAPEL", 0));
        lista.add(new ComandoLogo("PONCOLORRELLENO", 1));
        lista.add(new ComandoLogo("PONCONTADOR", 0));
        lista.add(new ComandoLogo("PONCP", 0));
        lista.add(new ComandoLogo("PONCURSORESPERA", 0));
        lista.add(new ComandoLogo("PONCURSORNOESPERA", 0));
        lista.add(new ComandoLogo("PONELEMENTO", 0));
        lista.add(new ComandoLogo("PONESCRITURA", 0));
        lista.add(new ComandoLogo("PONF", 0));
        lista.add(new ComandoLogo("PONFOCO", 0));
        lista.add(new ComandoLogo("PONFONDO", 0));
        lista.add(new ComandoLogo("PONFORMATORTUGA", 0));
        lista.add(new ComandoLogo("PONG", 0));
        lista.add(new ComandoLogo("PONGROSOR", 0));
        lista.add(new ComandoLogo("PONINDICEBIT", 0));
        lista.add(new ComandoLogo("PONLAPIZ", 1));
        lista.add(new ComandoLogo("PONLECTURA", 0));
        lista.add(new ComandoLogo("PONLUPA", 0));
        lista.add(new ComandoLogo("PONLUZ", 0));
        lista.add(new ComandoLogo("PONMARGENES", 0));
        lista.add(new ComandoLogo("PONMODOBIT", 0));
        lista.add(new ComandoLogo("PONMODOTORTUGA", 0));
        lista.add(new ComandoLogo("PONMP", 0));
        lista.add(new ComandoLogo("PONPATRONLAPIZ", 0));
        lista.add(new ComandoLogo("PONPIXEL", 0));
        lista.add(new ComandoLogo("PONPOS", 0));
        lista.add(new ComandoLogo("PONPOSESCRITURA", 0));
        lista.add(new ComandoLogo("PONPOSLECTURA", 0));
        lista.add(new ComandoLogo("PONPRIMERO", 0));
        lista.add(new ComandoLogo("PONPROP", 0));
        lista.add(new ComandoLogo("PONR", 0));
        lista.add(new ComandoLogo("PONRATON", 0));
        lista.add(new ComandoLogo("PONRED", 0));
        lista.add(new ComandoLogo("PONRONZAL", 0));
        lista.add(new ComandoLogo("PONRUMBO", 0));
        lista.add(new ComandoLogo("PONTAMAÑOTIPO", 0));
        lista.add(new ComandoLogo("PONTECLADO", 0));
        lista.add(new ComandoLogo("PONTEXTOCOMBOBOX", 0));
        lista.add(new ComandoLogo("PONULTIMO", 0));
        lista.add(new ComandoLogo("PONX", 0));
        lista.add(new ComandoLogo("PONXY", 0));
        lista.add(new ComandoLogo("PONXYZ", 0));
        lista.add(new ComandoLogo("PONY", 0));
        lista.add(new ComandoLogo("PONZ", 0));
        lista.add(new ComandoLogo("POS", 0));
        lista.add(new ComandoLogo("POS3D", 0));
        lista.add(new ComandoLogo("POSICIONATE", 0));
        lista.add(new ComandoLogo("POSLECTURA", 0));
        lista.add(new ComandoLogo("POSRATON", 0));
        lista.add(new ComandoLogo("POTENCIA", 0));
        lista.add(new ComandoLogo("PP", 0));
        lista.add(new ComandoLogo("PPR", 0));
        lista.add(new ComandoLogo("PREGUNTABOX", 0));
        lista.add(new ComandoLogo("PRI", 0));
        lista.add(new ComandoLogo("PRIMERO", 0));
        lista.add(new ComandoLogo("PRIMEROS", 0));
        lista.add(new ComandoLogo("PRIMITIVA", 0));
        lista.add(new ComandoLogo("PRODUCTO", 0));
        lista.add(new ComandoLogo("PROP", 0));
        lista.add(new ComandoLogo("PROPIEDAD", 0));
        lista.add(new ComandoLogo("PRUEBA", 0));
        lista.add(new ComandoLogo("PTT", 0));
        lista.add(new ComandoLogo("PUL", 0));
        lista.add(new ComandoLogo("QUITADIBUJOTORTUGA", 0));
        lista.add(new ComandoLogo("QUITADLL", 0));
        lista.add(new ComandoLogo("QUITAESTADO", 0));
        lista.add(new ComandoLogo("QUITARED", 0));
        lista.add(new ComandoLogo("QUITARRATON", 0));
        lista.add(new ComandoLogo("QUITATECLADO", 0));
        lista.add(new ComandoLogo("RADARCCOS", 0));
        lista.add(new ComandoLogo("RADARCSEN", 0));
        lista.add(new ComandoLogo("RADARCTAN", 0));
        lista.add(new ComandoLogo("RADCOS", 0));
        lista.add(new ComandoLogo("RADSEN", 0));
        lista.add(new ComandoLogo("RADTAN", 0));
        lista.add(new ComandoLogo("RAIZCUADRADA", 0));
        lista.add(new ComandoLogo("RC", 0));
        lista.add(new ComandoLogo("RE", 1));
        lista.add(new ComandoLogo("REAZAR", 0));
        lista.add(new ComandoLogo("RECTANGULORRELLENO", 0));
        lista.add(new ComandoLogo("REDONDEA", 0));
        lista.add(new ComandoLogo("RELLENA", 1));
        lista.add(new ComandoLogo("REPITE", 1));
        lista.add(new ComandoLogo("RESTO", 0));
        lista.add(new ComandoLogo("RESULTADOEJECUTA", 0));
        lista.add(new ComandoLogo("RETROCEDE", 1));
        lista.add(new ComandoLogo("RO", 0));
        lista.add(new ComandoLogo("RONZAL", 0));
        lista.add(new ComandoLogo("ROTULA", 0));
        lista.add(new ComandoLogo("RUMBO", 0));
        lista.add(new ComandoLogo("SELECCIONBOX", 0));
        lista.add(new ComandoLogo("SEN", 0));
        lista.add(new ComandoLogo("SHELL", 0));
        lista.add(new ComandoLogo("SI", 0));
        lista.add(new ComandoLogo("SIC", 0));
        lista.add(new ComandoLogo("SICIERTO", 0));
        lista.add(new ComandoLogo("SIEMPRE", 0));
        lista.add(new ComandoLogo("SIEVENTO", 0));
        lista.add(new ComandoLogo("SIF", 0));
        lista.add(new ComandoLogo("SIFALSO", 0));
        lista.add(new ComandoLogo("SINOBOX", 0));
        lista.add(new ComandoLogo("SIRED", 0));
        lista.add(new ComandoLogo("SISINO", 0));
        lista.add(new ComandoLogo("SISTEMA", 0));
        lista.add(new ComandoLogo("SIVERDADERO", 0));
        lista.add(new ComandoLogo("SL", 1));
        lista.add(new ComandoLogo("STANDOUT", 0));
        lista.add(new ComandoLogo("SUBELAPIZ", 1));
        lista.add(new ComandoLogo("SUENAWAVE", 0));
        lista.add(new ComandoLogo("SUMA", 0));
        lista.add(new ComandoLogo("TAMAÑODECORADO", 0));
        lista.add(new ComandoLogo("TAMAÑODIBUJO", 0));
        lista.add(new ComandoLogo("TAMAÑOGIF", 0));
        lista.add(new ComandoLogo("TAMAÑOTIPO", 0));
        lista.add(new ComandoLogo("TAN", 0));
        lista.add(new ComandoLogo("TAPA", 0));
        lista.add(new ComandoLogo("TAPADO", 0));
        lista.add(new ComandoLogo("TAPANOMBRE", 0));
        lista.add(new ComandoLogo("TECLA", 0));
        lista.add(new ComandoLogo("TERMINAPOLIGONO", 0));
        lista.add(new ComandoLogo("TEXTO", 0));
        lista.add(new ComandoLogo("TIENEBARRA", 0));
        lista.add(new ComandoLogo("TIPO", 0));
        lista.add(new ComandoLogo("TONO", 0));
        lista.add(new ComandoLogo("TORTUGA", 0));
        lista.add(new ComandoLogo("TORTUGAS", 0));
        lista.add(new ComandoLogo("TRAZA", 0));
        lista.add(new ComandoLogo("UL", 0));
        lista.add(new ComandoLogo("ULTIMO", 0));
        lista.add(new ComandoLogo("UNSTE", 0));
        lista.add(new ComandoLogo("VACIA", 0));
        lista.add(new ComandoLogo("VACIO", 0));
        lista.add(new ComandoLogo("VALOR", 0));
        lista.add(new ComandoLogo("VAR", 0));
        lista.add(new ComandoLogo("VENTANADEPURADOR", 0));
        lista.add(new ComandoLogo("VIRA", 0));
        lista.add(new ComandoLogo("VISIBLE", 0));
        lista.add(new ComandoLogo("Y", 0));
    
    return lista;
    }

    public boolean esComando( String str ) {
       
        boolean consulta = false;
        List<ComandoLogo> lista;
        lista = getListaComandosLogo();
        Iterator<ComandoLogo> iter;
        iter = lista.iterator();
        while (iter.hasNext()) {
            ComandoLogo cl = (ComandoLogo) iter.next();
            if (cl.getNombre().equalsIgnoreCase( str.trim()) ) {
                consulta = true;
                break;
            } else {
                consulta = false;
            }
        }
   
        return consulta;

    }

    
    public Token esComandoDeHugo(String str, int linea, int posicion) {
      
        Token token = new Token();
        boolean consulta = false;
        List<ComandoLogo> lista;
        lista = getListaComandosLogo();
        Iterator<ComandoLogo> iter;
        iter = lista.iterator();
        while (iter.hasNext()) {
            ComandoLogo cl = (ComandoLogo) iter.next();
            
            if (cl.getNombre().equalsIgnoreCase( str.trim() ) && cl.getEnHugo() == 1) {
                token.setNombre(str);
                token.setTipo(Token.Tipos.COMANDOHUGO);
                token.setLinea(linea);
                token.setPosicion(posicion);
            
                break;
            } else if (cl.getNombre().equalsIgnoreCase(str.trim()) && cl.getEnHugo() == 0) {
                token.setNombre(str);
                token.setTipo(Token.Tipos.COMANDOLOGO);
                token.setLinea(linea);
                token.setPosicion(posicion);
             
                break;
            }
        }
        
        
        return token;

    }

    public List<ComandoLogo> getListaComandosLogo() {
        return listaComandosLogo;
    }

    public void setListaComandosLogo(List<ComandoLogo> listaComandosLogo) {
        this.listaComandosLogo = listaComandosLogo;
    }

}

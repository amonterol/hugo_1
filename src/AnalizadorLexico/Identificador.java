/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorLexico;

/**
 *
 * @author PC
 */
public class Identificador {

    public Identificador() {

    }

    public boolean verificarInicioIdentificador(String primerCaracter) {

        return primerCaracter.matches("^[a-zA-Z].*");
    }

    public boolean verificarSecuenciaPalabra_Palabra(String cadena) {
        int i = 0;
        int index = 0;
        boolean resultado = false;

        //Primero verificamos que solo exista un guion bajo y que no este ni 
        //al inicio ni al final del posible identificador
        for (int x = 0; x < cadena.length(); x++) {
            //System.out.println("Caracter " + x + ": " + cadena.charAt(x));
            if (cadena.charAt(x) == '_') {
                ++i;
            }
        }
        boolean existeSoloUnGuionBajo = ((cadena.charAt(cadena.length() - 1) != '_') && (cadena.charAt(0) != '_') && i == 1);
        //Como solo hay un guion bajo, encontramos el indice donde se encuentra y 
        //dividimos el posible identificador en dos cadenas para comprobar que ambas
        //son secuencias de letras y numeros
        if (existeSoloUnGuionBajo) {
            index = cadena.indexOf("_");
            String palabraInicial = cadena.substring(0, index);
       
            String palabraFinal = cadena.substring(index + 1);
         
            boolean existeSecuenciaLetrasNumerosAntesGuion = verificarSecuenciaLetraDigitos(palabraInicial);
          
            boolean existeSecuenciaLetrasNumerosDespuesGuion = verificarSecuenciaLetraDigitos(palabraFinal) && !verificarSecuenciaSoloDigitos(palabraFinal);

           

            if (existeSecuenciaLetrasNumerosAntesGuion && existeSecuenciaLetrasNumerosDespuesGuion) {
                resultado = true;
            } else {
                resultado = false;
            }
        } else {
            resultado = false;
        }
       

        return resultado;

    }

    public boolean verificarSecuenciaLetraDigitos(String cadena) {

        return cadena.matches("[A-Za-z0-9]*");
    }

    public boolean verificarSecuenciaSoloDigitos(String cadena) {
        return cadena.matches("[0-9]*");
    }

    public boolean esIdentificador(String str) {

        boolean resultado = false;
        if (verificarInicioIdentificador(str)) {
          
            if (verificarSecuenciaPalabra_Palabra(str)) {
               
                resultado = true;
            } else if (verificarSecuenciaLetraDigitos(str)) {
              
                resultado = true;
            } else {
               
                resultado = false;
            }
        } else {
           
            resultado = false;
        }
      
        return resultado;
    }

}

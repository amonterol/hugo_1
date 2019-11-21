/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalizadorLexico;

/**
 *
 * @author Administrator
 */
public class NombreProcedimiento {

    public NombreProcedimiento() {
    }
     public boolean verificarInicioConSoloLetras(String primerCaracter) {

        return primerCaracter.matches("^[a-zA-Z].*");
    }

    public boolean verificarSecuenciaLetraDigitos(String cadena) {

        return cadena.matches("[A-Za-z0-9]*");
    }

    public boolean esIdentificador(String str) {

        boolean resultado = false;
        if (verificarInicioConSoloLetras(str)) {
          
            if (verificarSecuenciaLetraDigitos(str)) {
              
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

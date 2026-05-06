/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eac3.gestors;

import eac3.model.Pista;
import net.xqj.basex.BaseXXQDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xquery.*;

/**
 * Classe que gestiona la persistència dels objectes de la classe model.Pista
 *
 * @author professor
 */
public class PistaManager {

    //arrel del document (permet simplificar les expressions)
    //public static final String ARREL = "doc(\"pistes/pistes.xml\")/collection(\"pistes\")/";
    public static final String ARREL = "doc(\"pistes/pistes.xml\")/";

    private final XQConnection conn;

    public PistaManager(XQConnection conn) {
        this.conn = conn;
    }

    /**
     * Dóna d'alta una pista a la base de dades
     *
     * @param pista la pista a donar d'alta
     * @throws ManagerException en cas d'error, com per exemple clau duplicada
     */
    public void insert(Pista pista) throws ManagerException {

        String pistaxml = Utilitats.formaObjecteXML(pista);

        String checkQuery = ARREL + "pistes/pista[@id='" + pista.getId() + "']";

        String insertQuery = "insert node " + pistaxml + " as last into " + ARREL + "pistes";
        try{
            XQExpression exp = conn.createExpression();

           XQResultSequence resultCheck = exp.executeQuery(checkQuery);

            if (resultCheck.next()) {
                exp.close();
                throw  new ManagerException("Error al insertar pistas");
            }

            exp.executeQuery(insertQuery);

            exp.close();

        } catch (XQException ex) {
            throw new ManagerException("Error al insertar la pista: " + ex.getMessage(), ex);
        }
    }

    /**
     * Elimina una pista de la base de dades
     *
     * @param id l'identificador de la pista
     * @throws ManagerException en cas d'error, com per exemple que no existeixi
     */
    public void delete(String id) throws ManagerException {
        String checkQuery = ARREL + "pistes/pista[@id='" + id + "']";

        String deleteQuery = "delete node " + checkQuery;

        try {
            XQExpression exp = conn.createExpression();
            XQResultSequence resultCheck = exp.executeQuery(checkQuery);

            if (!resultCheck.next()) {
                exp.close();
                throw  new ManagerException("Error al esborrar la pista");
            }

            exp.executeQuery(deleteQuery);
            exp.close();
        } catch ( XQException ex) {
            throw new ManagerException("Error al esborrar la pista: " + ex.getMessage(), ex);
        }
    }

    /**
     * Elimina totes les pistes de la base de dades
     *
     * @throws ManagerException
     */
    public void deleteAll() throws ManagerException {
    try {
            String consulta = "delete node " + ARREL + "pistes/pista";
            XQExpression exp = conn.createExpression();
            exp.executeQuery(consulta);
        } catch (XQException ex) {
            Logger.getLogger(PistaManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new ManagerException("Error en esborrar totes les pistes", ex);
        }
    }

    /**
     * Obté una pista de la base de dades
     *
     * @param id l'identificador de la pista
     * @return l'objecte pista
     * @throws ManagerException en cas d'error, com per exemple que no existeixi
     */
    public Pista getPista(String id) throws ManagerException {
        String findQuery = ARREL + "pistes/pista[@id='" + id + "']";
        try {
            XQExpression exp = conn.createExpression();
            XQResultSequence resultFind = exp.executeQuery(findQuery);

            if (!resultFind.next()) {
                exp.close();
                throw  new ManagerException("No s'ha pogut trobar la pista");
            }
            String xmlPista = resultFind.getItemAsString(null);

            exp.close();

            return Utilitats.obteObjecte(xmlPista);

        } catch (XQException ex) {
            throw new ManagerException("Error al trobar la pista: " + ex.getMessage(), ex);
        }

    }

    /**
     * Obtenir les pistes que tenen un tipus de remuntador
     *
     * @param tipusRemuntador tipus del remuntador
     * @return la llista de pistes
     */
    List<Pista> getPistesAmbRemuntador(String tipusRemuntador) throws ManagerException {
        String forFindQuery = ARREL + "pistes/pista[remuntadors/remuntador='" + tipusRemuntador + "']";
        List<Pista> pistes = new ArrayList<Pista>();
        try {
            XQExpression exp = conn.createExpression();
            XQResultSequence resultFind = exp.executeQuery(forFindQuery);


            while (resultFind.next()) {
                String xmlPista = resultFind.getItemAsString(null);
                pistes.add(Utilitats.obteObjecte(xmlPista));
            }

            return pistes;

        }catch (XQException ex) {
            throw new ManagerException("Error al trobar les pistes: " + ex.getMessage(), ex);
        }

    }
}

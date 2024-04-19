package arbitros;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Principal {
    static Scanner teclado = new Scanner (System.in);
    
    public static void main(String[] args){
        String url = "jdbc:postgresql://200.131.206.13:5432/vitorlemes";
        
        System.out.print("User: ");
        String user = teclado.nextLine();
        
        System.out.print("Senha: ");
        String password = teclado.nextLine();
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String query = "";
        
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try{
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
            stmt.execute("SET search_path TO see");
            
            int idpes;
            while(true){
                System.out.print("\nDigite o idpes do arbitro zero: ");
                idpes = teclado.nextInt();

                query = "WITH RECURSIVE arbitros (level, idpes, idcoo, nomepes) AS\n" +
                "(SELECT 0, root.idpes, root.idcoo, nomepes\n" +
                "FROM arbitro as root NATURAL JOIN pessoa  \n" +
                "WHERE root.idpes = " + idpes + " \n" +
                "	UNION ALL	\n" +
                "SELECT level+1, child.idpes, child.idcoo, pessoa.nomepes\n" +
                "FROM arbitros as parent, arbitro as child NATURAL JOIN pessoa\n" +
                "WHERE parent.idpes = child.idcoo AND child.idcoo <> child.idpes)\n" +
                "\n" +
                "SELECT DISTINCT level, idpes, idcoo, nomepes\n" +
                "FROM arbitros\n" +
                "ORDER BY 1,2,3;";

                ResultSet resultado = stmt.executeQuery(query);
                
                System.out.println("'level', 'idpes', 'idcoo', 'nomepes'");  
                
                while(resultado.next()){
                    int level = resultado.getInt("level");
                    int idpesrs = resultado.getInt("idpes");
                    int idcoo = resultado.getInt("idcoo");
                    String nomepes = resultado.getString("nomepes");
                    System.out.println("'" + level + "', '" + idpes + "', '" + idcoo + "', '" + nomepes + "'");
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
}

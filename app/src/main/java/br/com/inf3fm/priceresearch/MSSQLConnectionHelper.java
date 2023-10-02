package br.com.inf3fm.priceresearch;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MSSQLConnectionHelper {

    public static final String TAG = "MSSQLConnectionHelper";

    // modelo para o banco de dados em SOMEE.COM
    private static String mStringServerIpName = "dbLoginRegister.mssql.somee.com";
    private static String mStringUserName = "marcosprofdigita_SQLLogin_1";
    private static String mStringPassword = "mwjac7862d";
    private static String mStringDatabase = "dbLoginRegister";
    // os dados acima são fornecidos pela hospedagem do banco de dados

    // modelo para o banco de dados em LOCALHOST (127.0.0.1) pc do lab
   // private static String mStringServerIpName = "10.0.2.2";
   // private static String mStringUserName = "sa";
   // private static String mStringPassword = "@ITB123456";
  //  private static String mStringDatabase = "ParceiroDoPet";
   // private static String mStringPort = "1433";
  //  private static String mStringInstance = "SQLEXPRESS";
    // os dados acima são do seu PC


    // objeto que irá representar a forma/técnica de conexão com o banco
    private static String mStringConnectionUrl;

    public static Connection getConnection(Context mContext){
        Connection mConnection = null;
        try{
            // adicionar política (policy) para criar uma tarefa (thread)
            // a tarefa aqui é permitir tudo
            StrictMode.ThreadPolicy mPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(mPolicy);

            // verificação se foi copiado/implementada a biblioteca jdts
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            // construir a string de conexao com o banco de dados
            // concatenar strings
            mStringConnectionUrl = "jdbc:jtds:sqlserver://" + mStringServerIpName +
                    ";database=" + mStringDatabase +
                    ";user=" + mStringUserName +
                    ";password=" + mStringPassword + ";" ;
            // a string de conexao acima funciona para o SOMEE.COM

            mConnection = DriverManager.getConnection(mStringConnectionUrl);

            // registar o sucesso da conexao - isso é para o teste com o aplicativo
            Log.i(TAG, "Conexao feita com sucesso"); // connection successful

        } catch (ClassNotFoundException e){
            String mMessage = "Classe não encontrada" + e.getMessage();
            Toast.makeText(mContext , mMessage , Toast.LENGTH_LONG).show();
            Log.e(TAG , mMessage);
            e.printStackTrace();

        } catch (SQLException e){
            String mMessage = "Falha com o banco de dados" + e.getMessage();
            Toast.makeText(mContext , mMessage , Toast.LENGTH_LONG).show();
            Log.e(TAG , mMessage);
            e.printStackTrace();

        } catch (Exception e){
            String mMessage = "Falha desconhecida" + e.getMessage();
            Toast.makeText(mContext , mMessage , Toast.LENGTH_LONG).show();
            Log.e(TAG , mMessage);
            e.printStackTrace();

        }

        return mConnection;

    }


}

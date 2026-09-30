package com.example.ejercicioconexionbd.connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5433/bdpruebag3";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Prueba123";

    public static Connection conectar() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("¡Conexión exitosa a PostgreSQL!");
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
        return con;
    }
}
package com.example.ejercicioconexionbd.controller;

import com.example.ejercicioconexionbd.connection.DatabaseConnection;
import com.example.ejercicioconexionbd.model.Libro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibroController {

    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtAutor;
    @FXML
    private ComboBox<String> cmbCategoria;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;

    @FXML
    private Button guardarLibro;
    @FXML
    private Button limpiarCampos;
    @FXML
    private Button cargarLibro;

    @FXML
    private TableView<Libro> tblLibros;
    @FXML
    private TableColumn<Libro, String> colTitulo;
    @FXML
    private TableColumn<Libro, String> colAutor;
    @FXML
    private TableColumn<Libro, String> colCategoria;
    @FXML
    private TableColumn<Libro, Double> colPrecio;
    @FXML
    private TableColumn<Libro, Integer> colStock;

    private final ObservableList<Libro> libros = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabla();
        configurarComboBox();
        cargarLibros();
    }

    private void configurarComboBox() {
        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Novela",
                "Ciencia Ficción",
                "Fantasía",
                "Historia",
                "Tecnología",
                "Poesía",
                "Educación"
        ));
    }

    private void configurarTabla() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        tblLibros.setItems(libros);
    }

    @FXML
    public void cargarLibro() {
        cargarLibros();
    }

    public void cargarLibros() {
        libros.clear();
        String sql = "SELECT titulo, autor, COALESCE(categoria, 'Sin categoría') AS categoria, precio, stock FROM libros";

        try (Connection con = DatabaseConnection.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                String categoria = rs.getString("categoria");
                double precio = rs.getDouble("precio");
                int stock = rs.getInt("stock");

                libros.add(new Libro(titulo, autor, categoria, precio, stock));
            }

        } catch (SQLException | NullPointerException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Consulta", "No se pudieron cargar los libros: " + e.getMessage());
        }
    }

    @FXML
    public void guardarLibro() {
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String categoria = cmbCategoria.getValue();
        String strPrecio = txtPrecio.getText().trim();
        String strStock = txtStock.getText().trim();

        if (titulo.isEmpty() || autor.isEmpty() || categoria == null || strPrecio.isEmpty() || strStock.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor completa todos los campos (incluyendo la categoría).");
            return;
        }

        try {
            double precio = Double.parseDouble(strPrecio);
            int stock = Integer.parseInt(strStock);

            String sql = "INSERT INTO libros (titulo, autor, categoria, precio, stock) VALUES (?, ?, ?, ?, ?)";

            try (Connection con = DatabaseConnection.conectar();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, titulo);
                ps.setString(2, autor);
                ps.setString(3, categoria);
                ps.setDouble(4, precio);
                ps.setInt(5, stock);

                ps.executeUpdate();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Libro registrado exitosamente.");

                limpiarFormulario();
                cargarLibros();

            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "Error al guardar: " + e.getMessage());
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato Inválido", "Precio debe ser decimal y Stock un número entero.");
        }
    }

    @FXML
    public void limpiarCampos() {
        limpiarFormulario();
    }

    public void limpiarFormulario() {
        txtTitulo.clear();
        txtAutor.clear();
        cmbCategoria.setValue(null);
        txtPrecio.clear();
        txtStock.clear();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
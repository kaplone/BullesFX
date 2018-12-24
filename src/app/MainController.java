package app;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import models.Extrait;
import models.FileResultat;
import outils.Encodage;
import outils.Recherche;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class MainController implements Initializable{

    @FXML
    private Button parcourir_button;
    @FXML
    private RadioButton html_radioButton;
    @FXML
    private TextField rechercher_textField;
    @FXML
    private Button rechercher_button;
    @FXML
    private ListView resultat_listView;
    @FXML
    private ListView extraits_listView;
    @FXML
    private WebView webView;
    @FXML
    private Label nombres_label;

    private File repLec;
    private String extension;

    private FileResultat fileResultat;
    private String content = null;

    private ObservableList<FileResultat> filesObservables;
    private ObservableList<Extrait> extraitsObservables;

    public MainController() {
    }

    protected void onParcourir_button(){
        repLec = chooseRepLec("Répertoire des bulles");
    }

    protected void onRechercher_button() {

        filesObservables.clear();
        extraitsObservables.clear();

        final String motif = rechercher_textField.getText();

        if (motif != null && !motif.isEmpty() && !motif.trim().equals("")){

            final String motif_enc = Encodage.escapeHTML(motif);

            Recherche.init();

            Path archives = Paths.get(repLec.toString(), "archives");
            if (! archives.toFile().exists()) {
                archives.toFile().mkdir();
            }
            Path archive = Paths.get(repLec.toString(), "archives", String.format("%s.txt", motif));

            System.out.println(archive.toString());

            try {
                archive.toFile().createNewFile();
                FileWriter fw = new FileWriter(archive.toFile());
                Stream<File> listFilesStream = Stream.of(repLec.listFiles());
                listFilesStream.filter(f -> f.getName().endsWith(extension))
                        .forEach(a -> Recherche.verifierMotif(a, filesObservables, motif_enc, fw));
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            nombres_label.setText(String.format("%d dans %d fichier%s", Recherche.getNombre_occurences(), Recherche.getNombre_fichiers(), Recherche.getNombre_fichiers() > 1 ? "s" : ""));

        }
        else {
            rechercher_textField.setPromptText("Saisir une valeur à chercher ...");
        }
    }

    protected File chooseRepLec(String s){

        Stage newStage = new Stage();

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(Paths.get(System.getProperty("user.home"), "Bulles").toFile());
        dirChooser.setTitle(s);
        File selectedDir = dirChooser.showDialog(newStage);

        return validateDir(selectedDir);
    }

    private File validateDir(File fileToValidate){

        if (fileToValidate != null) {
            Platform.runLater(
                    () -> {
                        rechercher_textField.requestFocus();
                        rechercher_button.setDisable(false);
                    }
            );
            repLec = fileToValidate;
        }
        parcourir_button.setText(repLec.toString());
        return repLec;
    }

    public void afficher(FileResultat f){

        extraitsObservables.clear();
        extraits_listView.scrollTo(0);
        fileResultat = f;

        if (f != null){
            extraitsObservables.addAll(f.getContenu());

            try {
                content = new String(Files.readAllBytes(f.getFile().toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            content = "";
        }
        extraits_listView.scrollTo(0);

        webView.getEngine().loadContent("");
        webView.getEngine().loadContent(content);
    }

    public void surligner(Extrait e){

        if (e != null){
            webView.getEngine().loadContent(content.replace(e.getEncode(), String.format("<span style='background: orange'>%s</span>", e.getEncode())));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        validateDir(Paths.get(System.getProperty("user.home"), "Bulles").toFile());

        extension = ".html";
        filesObservables = FXCollections.observableArrayList();
        extraitsObservables = FXCollections.observableArrayList();

        resultat_listView.setItems(filesObservables);
        resultat_listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> afficher((FileResultat) newValue));

        extraits_listView.setItems(extraitsObservables);
        extraits_listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> surligner((Extrait) newValue));

        rechercher_textField.setOnKeyPressed(a -> {
            if (a.getCode().equals(KeyCode.ENTER)){
                onRechercher_button();
        }});

        parcourir_button.setOnAction(a -> onParcourir_button());

        rechercher_button.setDisable(true);
        rechercher_button.setOnAction(a -> onRechercher_button());

        html_radioButton.selectedProperty().addListener((observable, oldValue, newValue) -> extension = newValue ? ".html" : "txt");
    }
}

package outils;

import javafx.collections.ObservableList;
import models.Extrait;
import models.FileResultat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Recherche {

    static int nombre_occurences;
    static int nombre_fichiers;

    public static List<Extrait> chercheMotif(File file, String motif){

        String content = null;
        try {
             content = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Stream.of(content.split("<body>")[1].split("<br>"))
                         .filter( a -> a.toLowerCase(Locale.FRANCE).contains(motif.toLowerCase(Locale.FRANCE)))
                         .map(s -> new Extrait(s, Encodage.rebuildString(s)))
                         .collect(Collectors.toList());

    }

    public static void verifierMotif(File file, ObservableList<FileResultat> resultats, String motif, FileWriter archive){

        List<Extrait> resMotif = chercheMotif(file, motif);

        if (! resMotif.isEmpty()){
            try {
                archive.write(resMotif.stream().map(a -> a.getDecode()).collect(Collectors.joining("\n", "", "\n")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            resultats.add(new FileResultat(file, resMotif));
            nombre_fichiers ++;
            nombre_occurences += resMotif.size();
        }
    }

    public static int getNombre_occurences() {
        return nombre_occurences;
    }

    public static int getNombre_fichiers() {
        return nombre_fichiers;
    }

    public static void init() {
        Recherche.nombre_occurences = 0;
        Recherche.nombre_fichiers = 0;
    }
}

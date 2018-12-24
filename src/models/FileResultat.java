package models;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileResultat {

    private File file;
    private List<Extrait> contenu;

    public FileResultat(File file, List<Extrait> contenu) {
        this.file = file;
        this.contenu = contenu;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<Extrait> getContenu() {
        return contenu;
    }

    public List<String> getContenuEncode() {
        return contenu.stream()
                      .map(a -> a.getEncode())
                      .collect(Collectors.toList());
    }

    public List<String> getContenuDecode() {
        return contenu.stream()
                      .map(a -> a.getDecode())
                      .collect(Collectors.toList());
    }

    public void setContenu(List<Extrait> contenu) {
        this.contenu = contenu;
    }

    @Override
    public String toString(){
        return String.format("%s (%d)",  getNameLight(file.getName()), contenu.size() );
    }

    private String getNameLight(String input){

        String [] tranches = input.split("-");

        return Arrays.stream(tranches, 0, tranches.length -1).collect(Collectors.joining(" "));
    }

}

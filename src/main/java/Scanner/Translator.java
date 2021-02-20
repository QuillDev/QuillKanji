package Scanner;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Translator {

    private final ArrayList<ArrayList<String>> dictionaries = new ArrayList<>();

    public Translator(){
        loadDictionaries();
    }

    public String searchFor(String query){
        System.out.println("Started Searching!");
        for(final var dict : dictionaries) {
            for(final var entry : dict){
                var document = Configuration.defaultConfiguration().jsonProvider().parse(entry);
                ArrayList<String> keys = JsonPath.read(document, "$");
                for(int index = 0; index < keys.size(); index++){
                    var key = JsonPath.read(document, String.format("$[%s][0]", index)).toString();
                    if(!key.equals(query)){
                        continue;
                    }

                    return JsonPath.read(document, String.format("$[%s][5][0]", index)).toString();
                }
            }
        }

        return "";
    }
    public void loadDictionaries(){
        var dictionaryPaths = getFiles("Dictionaries", true);

        //Get all dictionary paths
        for(final var path : dictionaryPaths){

            //Get all files from the given directory
            var filePaths = getFiles(path);
            var list = new ArrayList<String>();
            for(final var filePath : filePaths){
                var file = filePath.toFile();
                if(file.getName().contains("term_bank_")){
                    try {
                        var data = Files.readString(filePath);
                        list.add(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //if the list length is zero continue
            if(list.size() == 0){
                continue;
            }
            dictionaries.add(list);
        }
    }

    public ArrayList<Path> getFiles(Path path, boolean onlyDirs){

        ArrayList<Path> subDirs = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(path)) {
            paths
                    .filter((onlyDirs ? Files::isDirectory : Files::isRegularFile)) // Filter into only directories
                    .skip((onlyDirs ? 1 : 0)) // Skip itself
                    .forEach(subDirs::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return subDirs;
    }

    public ArrayList<Path> getFiles(Path path){
        return getFiles(path, false);
    }

    public ArrayList<Path> getFiles(String path, boolean onlyDirs){
        return getFiles(Paths.get(path), onlyDirs);
    }
}
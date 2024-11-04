package Main;

public class Main {

    static String wordsPath = "C:/Users/alvar/Desktop/Universidad/03 - AÑO3/Semestre-1/04-BD/01-Teoria/01-Assignments/01-Group Assignment/Stage 1/queryEngine/datamart/wordsTest.json";
    static String metadataPath = "C:/Users/alvar/Desktop/Universidad/03 - AÑO3/Semestre-1/04-BD/01-Teoria/01-Assignments/01-Group Assignment/Stage 1/queryEngine/datamart/metadatosTest.json";
    static String datalakePath = "C:/Users/alvar/Desktop/Universidad/03 - AÑO3/Semestre-1/04-BD/01-Teoria/01-Assignments/01-Group Assignment/Stage 1/queryEngine/datalake";

    public static void main(String[] args) {
        QueryEngine.execute(wordsPath, metadataPath, datalakePath);
    }
}

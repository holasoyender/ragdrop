import app.lacabra.ragdrop.Importer;

public class Main {

    public static void main(String[] args) {

        Importer schema = new Importer("classpath:schema.json");

        System.out.println(schema.verify());
        System.out.println(schema.getErrorMessage());
    }
}

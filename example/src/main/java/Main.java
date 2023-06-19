import app.lacabra.ragdrop.Schema;

public class Main {

    public static void main(String[] args) {

        Schema schema = new Schema("classpath:schema.json");

        System.out.println(schema.verify());
        System.out.println(schema.getErrorMessage());
    }
}

import app.lacabra.ragdrop.Schema;
import app.lacabra.ragdrop.Yaml;

public class Main {

    public static void main(String[] args) {

        Schema schema = new Schema("classpath:schema.json");
        Yaml yaml = new Yaml().loadFromPath("example/config.yaml");

        System.out.println(schema.verify());
        System.out.println(schema.getErrorMessage());

        System.out.println(schema.validate(yaml));
    }
}

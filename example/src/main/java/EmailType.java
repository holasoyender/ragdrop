import app.lacabra.ragdrop.Type;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailType implements Type {

    EmailType(String value) {
        // This method is not used in this example
    }

    @Override
    public boolean verify() {
        // This method is not used in this example
        return true;
    }

    @Override
    public boolean validate(@NotNull String value) {
        // Here you should compare the value with an email pattern and return true if it matches
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
    
    @Override
    public void withTypes(@NotNull Map<String, ? extends Function1<? super String, ? extends Type>> types) {
        // This method is not used in this example
    }
}

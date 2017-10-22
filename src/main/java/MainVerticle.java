import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class MainVerticle extends Verticle {

    public void start() {
        JsonObject config = new JsonObject();
        config.putBoolean("worker", true);
        container.deployWorkerVerticle("com.weblogism.vertxsandbox.ProcessorVerticle", config, 10);
    }
}

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class MainVerticle extends Verticle {

    public void start() {
        JsonObject config = new JsonObject();
        config.putBoolean("worker", true);
        System.out.println("MainVerticle " + this + " - thread: " +
                           Thread.currentThread() +
                           " - classloader: " +
                           this.getClass().getClassLoader());

        // By default workers are single-threaded, i.e. only one instance
        // can be executed at once by the worker pool.
        container.deployWorkerVerticle("com.weblogism.vertxsandbox.ProcessorVerticle", config, 1, true);
    }
}

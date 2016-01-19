import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

/**
 * Created by oelbaer on 15.01.16.
 */
public class MyTailerListener implements TailerListener {
    @Override
    public void init(Tailer tailer) {

    }

    @Override
    public void fileNotFound() {

    }

    @Override
    public void fileRotated() {

    }

    @Override
    public void handle(String s) {
        System.out.println(s);

    }

    @Override
    public void handle(Exception e) {

    }
}

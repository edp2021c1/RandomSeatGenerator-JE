import com.edp2021c1.randomseatgenerator.core.SeatTableFactory;
import com.edp2021c1.randomseatgenerator.ui.node.SeatTableView;
import com.edp2021c1.randomseatgenerator.util.ConfigUtils;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestWindow extends Stage {
    public TestWindow() {
        setScene(new Scene(new SeatTableView(SeatTableFactory.generate(ConfigUtils.reloadConfig(), ""))));
    }
}

import com.edp2021c1.randomseatgenerator.core.SeatGenerator;
import com.edp2021c1.randomseatgenerator.core.SeatTable;
import com.edp2021c1.randomseatgenerator.util.ConfigUtils;

import java.util.Random;

public class Test {
    public static void main(String[] args) {
        SeatTable s = new SeatGenerator().generate(ConfigUtils.reloadConfig(), new Random().nextLong());
        System.out.println(s);
        System.exit(0);
    }
}

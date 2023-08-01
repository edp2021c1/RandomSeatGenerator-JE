import com.edp2021c1.util.Seat;
import com.edp2021c1.util.SeatGenerator;

import java.util.ArrayList;
import java.util.Random;

public class Test {

    public static void main(String[] args) {
        System.out.println("Started testing...");
        SeatGenerator sg = new SeatGenerator();
        Random rd = new Random();
        long seed=rd.nextLong();
        Seat s = sg.next(seed);
        ArrayList<String> l = s.seat;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(l.get(i * 7 + j) + "\t");
            }
            System.out.println();
        }
    }

}
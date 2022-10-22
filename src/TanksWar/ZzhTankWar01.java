package TanksWar;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;

@SuppressWarnings({"all"})
public class ZzhTankWar01 extends JFrame {
    MyPanel mp = null;
    Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new ZzhTankWar01();
    }

    public ZzhTankWar01() {
        System.out.println("请输入选择 1：新游戏 2：继续上局");
        mp = new MyPanel(scanner.next());

        //菜单初始化
        this.add(mp);
        this.setSize(1240, 750);
        this.addKeyListener(mp);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        new Thread(mp).start();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Recorder.saveRecord();
            }
        });
    }
}

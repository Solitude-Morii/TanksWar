package TanksWar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

@SuppressWarnings({"all"})
public class MyPanel extends JPanel implements KeyListener, Runnable {
    //自己坦克属性
    private Hero hero = null;
    private int x;  //自己坦克x坐标
    private int y;  //自己坦克y坐标

    //地方坦克属性
    Vector<EnemyTank> enemyTanks = new Vector<>();
    int enemyTanksSize = 5;     //敌人坦克数量

    //坦克爆炸属性
    Vector<Bomb> bombs = new Vector<>();
    Image image1 = null;
    Image image2 = null;
    Image image3 = null;

    //记录敌人坦克的信息
    private Vector<TankInfo> tankInfos = new Vector<>();

    public MyPanel(String key) {
        //自己坦克初始化
        this.x = 100;
        this.y = 100;
        hero = new Hero(x, y);
        hero.setSpeed(5);

        //敌方坦克初始
        if ((tankInfos = Recorder.getTankInfos()) == null) {
            System.out.println("没有上局记录,只能开启新游戏");
            key = "1";
        }
        switch (key) {
            case "1"://新游戏
                for (int i = 0; i < enemyTanksSize; i++) {
                    EnemyTank enemyTank = new EnemyTank(100 * i, 0);
                    enemyTank.setDirect((int) (Math.random() * 4)); //坦克的方向
                    enemyTank.setEnemyTanks(enemyTanks);    //记录坦克信息-碰撞
                    enemyTank.setDirect(0); //坦克的方向
                    enemyTank.setSpeed(1);  //坦克的移速
                    enemyTanks.add(enemyTank);  //记录坦克信息-画图
                    new Thread(enemyTank).start();  //敌人子弹线程
                }
                break;
            case "2"://上局游戏
                for (int i = 0; i < tankInfos.size(); i++) {
                    TankInfo tankInfo = tankInfos.get(i);   //上局的敌人坦克信息记录
                    EnemyTank enemyTank = new EnemyTank(tankInfo.getX(), tankInfo.getY());  //上局的位置初始化
                    enemyTank.setEnemyTanks(enemyTanks);  //记录坦克信息-碰撞
                    enemyTank.setDirect(tankInfo.getDirect()); //上局坦克的方向
                    enemyTank.setSpeed(1);  //坦克的移速
                    enemyTanks.add(enemyTank);  //记录坦克信息-画图
                    new Thread(enemyTank).start();  //敌人子弹线程
                }
                break;
        }
        Recorder.setEnemyTanks(enemyTanks); //记录坦克信息-文本


        //炸弹初始化
        image1 = Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("/bomb_1.gif"));
        image2 = Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("/bomb_2.gif"));
        image3 = Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("/bomb_3.gif"));

        //音乐初始化
        new AePlayWave("src\\111.wav").start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        //界面的绘制
        g.fillRect(0, 0, 1000, 750);

        //自己坦克的绘制
        if (hero.isLive() == true) {
            drawTank(hero.getX(), hero.getY(), g, hero.getDirect(), 1);
            for (int i = 0; i < hero.shots.size(); i++) {
                Shot shot = hero.shots.get(i);
                if (shot != null && hero.shots.get(i).isLive() == true) {
                    drawBullt(hero.shots.get(i).getX(), hero.shots.get(i).getY(), g, hero.shots.get(i).getDirect(), 1);
                    hitEnemy();
                } else {
                    hero.shots.remove(shot);
                }
            }
        }


        //敌方坦克的绘制
        for (int i = 0; i < enemyTanks.size(); i++) {
            EnemyTank enemyTank = enemyTanks.get(i);
            if (enemyTank.isLive() == true) {
                drawTank(enemyTank.getX(), enemyTank.getY(), g, enemyTank.getDirect(), 0);
            } else {
                enemyTanks.remove(enemyTank);
            }
            for (int j = 0; j < enemyTank.shots.size(); j++) {
                Shot shot = enemyTank.shots.get(j);
                if (shot.isLive() == true) {
                    drawBullt(enemyTank.shots.get(j).getX(), enemyTank.shots.get(j).getY(), g, enemyTank.shots.get(j).getDirect(), 0);
                    hitHero();
                } else {
                    enemyTank.shots.remove(shot);
                }
            }
        }

        //坦克被击毁的绘制
        for (int i = 0; i < bombs.size(); i++) {

            Bomb bomb = bombs.get(i);
            if (bomb.life >= 6) {
                g.drawImage(image1, bomb.getX(), bomb.getY(), 60, 60, this);
            } else if (bomb.life >= 3) {
                g.drawImage(image2, bomb.getX(), bomb.getY(), 60, 60, this);
            } else {
                g.drawImage(image3, bomb.getX(), bomb.getY(), 60, 60, this);
            }
            bomb.lifeDown();
            if (bomb.life <= 0) {
                bombs.remove(bomb);
            }
        }

        showInfo(g);
    }

    /**
     * @param x      坦克的左上角x坐标
     * @param y      坦克的左上角y坐标
     * @param g      画笔
     * @param direct 坦克的方向(上下左右)
     * @param type   坦克类型
     */
    public void drawTank(int x, int y, Graphics g, int direct, int type) {
        switch (type) {
            case 0://敌人坦克
                g.setColor(Color.cyan);
                break;
            case 1://自己坦克
                g.setColor(Color.yellow);
                break;
        }
        //坦克方向（0：向上，1：向右，2：向下，3：向左）
        switch (direct) {
            case 0://向上
                g.fill3DRect(x, y, 10, 60, false);//坦克左轮
                g.fill3DRect(x + 30, y, 10, 60, false);//坦克右轮
                g.fill3DRect(x + 10, y + 10, 20, 40, false);//坦克壳
                g.fillOval(x + 10, y + 20, 20, 20);//坦克圆盖
                g.drawLine(x + 20, y + 30, x + 20, y);//坦克炮口
                break;
            case 1://向右
                g.fill3DRect(x, y, 60, 10, false);//坦克左轮
                g.fill3DRect(x, y + 30, 60, 10, false);//坦克右轮
                g.fill3DRect(x + 10, y + 10, 40, 20, false);//坦克壳
                g.fillOval(x + 20, y + 10, 20, 20);//坦克圆盖
                g.drawLine(x + 30, y + 20, x + 60, y + 20);//坦克炮口
                break;
            case 2://向下
                g.fill3DRect(x, y, 10, 60, false);//坦克左轮
                g.fill3DRect(x + 30, y, 10, 60, false);//坦克右轮
                g.fill3DRect(x + 10, y + 10, 20, 40, false);//坦克壳
                g.fillOval(x + 10, y + 20, 20, 20);//坦克圆盖
                g.drawLine(x + 20, y + 30, x + 20, y + 60);//坦克炮口
                break;
            case 3://向左
                g.fill3DRect(x, y, 60, 10, false);//坦克左轮
                g.fill3DRect(x, y + 30, 60, 10, false);//坦克右轮
                g.fill3DRect(x + 10, y + 10, 40, 20, false);//坦克壳
                g.fillOval(x + 20, y + 10, 20, 20);//坦克圆盖
                g.drawLine(x + 30, y + 20, x, y + 20);//坦克炮口
                break;
        }
    }

    /**
     * @param x      子弹的在炮口的x坐标
     * @param y      子弹的在炮口的y坐标
     * @param g      画笔
     * @param direct 子弹的方向
     * @param type   子弹的种类
     */
    public void drawBullt(int x, int y, Graphics g, int direct, int type) {
        switch (type) {
            case 0://敌人子弹
                g.setColor(Color.cyan);
                break;
            case 1://自己子弹
                g.setColor(Color.yellow);
                break;
        }

        switch (direct) {
            case 0://向上
                g.draw3DRect(x, y, 1, 7, false);
                break;
            case 1://向右
                g.draw3DRect(x, y, 7, 1, false);
                break;
            case 2://向下
                g.draw3DRect(x, y, 1, 7, false);
                break;
            case 3://向左
                g.draw3DRect(x, y, 7, 1, false);
                break;
        }
    }

    /**
     * @param s    子弹
     * @param tank 坦克
     * @return 是否被击毁
     */
    public boolean hitTank(Shot s, Tank tank) {
        switch (tank.getDirect()) {
            case 0://向上
            case 2://向下
                if (s.getX() > tank.getX() && s.getX() < tank.getX() + 40
                        && s.getY() > tank.getY() && s.getY() < tank.getY() + 60) {
                    s.setLive(false);
                    tank.setLive(false);
                    if (tank instanceof EnemyTank) {
                        Recorder.add();  //击毁敌方数量记录
                    }
                    bombs.add(new Bomb(tank.getX(), tank.getY()));
                    return true;
                }
                break;
            case 1://向右
            case 3://向左
                if (s.getX() > tank.getX() && s.getX() < tank.getX() + 60
                        && s.getY() > tank.getY() && s.getY() < tank.getY() + 40) {
                    s.setLive(false);
                    tank.setLive(false);
                    if (tank instanceof EnemyTank) {
                        Recorder.add();  //击毁敌方数量记录
                    }
                    bombs.add(new Bomb(tank.getX(), tank.getY()));
                    return true;
                }
                break;
        }
        return false;
    }

    public void hitEnemy() {
        for (int i = 0; i < hero.shots.size(); i++) {
            Shot shot = hero.shots.get(i);
            if (shot.isLive() == true) {
                for (int j = 0; j < enemyTanks.size(); j++) {
                    EnemyTank enemyTank = enemyTanks.get(j);
                    hitTank(shot, enemyTank);
                }
            }
        }
    }

    public void hitHero() {
        for (int i = 0; i < enemyTanks.size(); i++) {
            EnemyTank enemyTank = enemyTanks.get(i);
            for (int j = 0; j < enemyTank.shots.size(); j++) {
                Shot shot = enemyTank.shots.get(j);
                if (hero.isLive() == true && shot.isLive() == true) {
                    hitTank(shot, hero);
                }
            }
        }
    }

    //记录击毁敌方坦克的数量
    public void showInfo(Graphics g) {
        g.setColor(Color.gray);
        g.fillRect(1000, 0, 240, 750);
        g.setColor(Color.BLACK);
        Font font = new Font("宋体", Font.BOLD, 20);
        g.setFont(font);
        g.drawString("你累积击毁敌方坦克", 1020, 30);
        drawTank(1020, 60, g, 0, 0);
        g.setColor(Color.BLACK);
        g.drawString(Recorder.getAllEnemyTankNum() + "", 1080, 100);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //坦克移动上下左右
        if (e.getKeyCode() == KeyEvent.VK_W) {
            hero.moveUp();
            hero.setDirect(0);
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            hero.moveDown();
            hero.setDirect(2);
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            hero.moveLeft();
            hero.setDirect(3);
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            hero.moveRight();
            hero.setDirect(1);
        }
        //自己发射子弹
        if (e.getKeyCode() == KeyEvent.VK_J) {
            hero.shotEnemyTank();
        }

        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.repaint();
        }
    }
}

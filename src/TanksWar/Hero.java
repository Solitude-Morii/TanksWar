package TanksWar;

import java.util.Vector;

@SuppressWarnings({"all"})
public class Hero extends Tank {
    private Shot shot = null;
    Vector<Shot> shots = new Vector<>();

    public Hero(int x, int y) {
        super(x, y);
    }

    public void shotEnemyTank() {

        if (shots.size() > 10) { //控制自己子弹数量
            return;
        }

        switch (getDirect()) { //自己坦克方向
            case 0://向上
                shot = new Shot(getX() + 20, getY(), 0);
                break;
            case 1://向右
                shot = new Shot(getX() + 60, getY() + 20, 1);
                break;
            case 2://向下
                shot = new Shot(getX() + 20, getY() + 60, 2);
                break;
            case 3://向左
                shot = new Shot(getX(), getY() + 20, 3);
                break;
        }
        shot.setSpeed(15);  //子弹速度
        shots.add(shot);
        new Thread(shot).start(); //给每一个子弹创建线程
    }

}

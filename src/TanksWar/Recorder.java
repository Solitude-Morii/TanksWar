package TanksWar;

import java.io.*;
import java.util.Vector;

@SuppressWarnings({"all"})
public class Recorder {
    private static int allEnemyTankNum = 0;
    private static BufferedWriter bw = null;
    private static BufferedReader br = null;
    private static String recordFile = "src\\myRecord.txt";

    private static Vector<EnemyTank> enemyTanks = new Vector<>();
    private static Vector<TankInfo> tankInfos = new Vector<>();

    //读取上局坦克的信息
    public static Vector<TankInfo> getTankInfos() {
        try {
            File file = new File(recordFile);
            if (!file.exists()) {
                return null;
            }
            br = new BufferedReader(new FileReader(file));
            allEnemyTankNum = Integer.parseInt(br.readLine());
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] xyd = line.split(" ");
                TankInfo tankInfo = new TankInfo(Integer.parseInt(xyd[0]), Integer.parseInt(xyd[1]), Integer.parseInt(xyd[2]));
                tankInfos.add(tankInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return tankInfos;
    }

    //保存上局坦克的信息
    public static void saveRecord() {
        try {
            //记录击败的坦克数量
            bw = new BufferedWriter(new FileWriter(recordFile));
            bw.write(allEnemyTankNum + "");
            bw.newLine();

            //保存退出前坦克的位置
            for (int i = 0; i < enemyTanks.size(); i++) {
                EnemyTank enemyTank = enemyTanks.get(i);
                if (enemyTank.isLive() == true) {
                    String record = enemyTank.getX() + " " + enemyTank.getY() + " " + enemyTank.getDirect();
                    bw.write(record);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void setTankInfos(Vector<TankInfo> tankInfos) {
        Recorder.tankInfos = tankInfos;
    }

    public static void setEnemyTanks(Vector<EnemyTank> enemyTanks) {
        Recorder.enemyTanks = enemyTanks;
    }

    public static int getAllEnemyTankNum() {
        return allEnemyTankNum;
    }

    public static void setAllEnemyTankNum(int allEnemyTankNum) {
        Recorder.allEnemyTankNum = allEnemyTankNum;
    }

    public static void add() {
        Recorder.allEnemyTankNum++;
    }
}

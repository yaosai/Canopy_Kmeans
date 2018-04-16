package hadoop.mapreduce.yaosai;

import java.util.ArrayList;
import java.util.List;


/**
 * Canopy算法 借助canopy算法计算对应的Kmeans中的K值大小
 * 其中对于计算K值来说，canopy算法中的T1没有意义，只用设定T2(T1>T2) 我们这里将T2设置为平均距离
 * 
 * @author YD
 *
 */
public class Canopy {
    private List<Point> points = new ArrayList<Point>(); // 进行聚类的点
    private List<List<Point>> clusters = new ArrayList<List<Point>>(); // 存储簇
    private double T2 = -1; // 阈值

    public Canopy(List<Point> points) {
        for (Point point : points)
            // 进行深拷贝
            this.points.add(point);
    }

    /**
     * 进行聚类，按照Canopy算法进行计算，将所有点进行聚类
     */
    public void cluster() {
        T2 = getAverageDistance(points);
        while (points.size() != 0) {
            List<Point> cluster = new ArrayList<Point>();
            Point basePoint = points.get(0); // 基准点
            cluster.add(basePoint);
            points.remove(0);
            int index = 0;
            while (index < points.size()) {
                Point anotherPoint = points.get(index);
                double distance = Math.sqrt((basePoint.x - anotherPoint.x)
                        * (basePoint.x - anotherPoint.x)
                        + (basePoint.y - anotherPoint.y)
                        * (basePoint.y - anotherPoint.y));
                if (distance <= T2) {
                    cluster.add(anotherPoint);
                    points.remove(index);
                } else {
                    index++;
                }
            }
            clusters.add(cluster);
        }
    }

    /**
     * 得到Cluster的数目
     * 
     * @return 数目
     */
    public int getClusterNumber() {
        return clusters.size();
    }

    /**
     * 获取Cluster对应的中心点(各点相加求平均)
     * 
     * @return
     */
    public List<Point> getClusterCenterPoints() {
        List<Point> centerPoints = new ArrayList<Point>();
        for (List<Point> cluster : clusters) {
            centerPoints.add(getCenterPoint(cluster));
        }
        return centerPoints;
    }

    /**
     * 得到的中心点(各点相加求平均)
     * 
     * @return 返回中心点
     */
    private double getAverageDistance(List<Point> points) {
        double sum = 0;
        int pointSize = points.size();
        for (int i = 0; i < pointSize; i++) {
            for (int j = 0; j < pointSize; j++) {
                if (i == j)
                    continue;
                Point pointA = points.get(i);
                Point pointB = points.get(j);
                sum += Math.sqrt((pointA.x - pointB.x) * (pointA.x - pointB.x)
                        + (pointA.y - pointB.y) * (pointA.y - pointB.y));
            }
        }
        int distanceNumber = pointSize * (pointSize + 1) / 2;
        double T2 = sum / distanceNumber / 2; // 平均距离的一半
        return T2;
    }

    /**
     * 得到的中心点(各点相加求平均)
     * 
     * @return 返回中心点
     */
    private Point getCenterPoint(List<Point> points) {
        double sumX = 0;
        double sumY = 0;
        for (Point point : points) {
            sumX += point.x;
            sumY += point.y;
        }
        int clusterSize = points.size();
        Point centerPoint = new Point(sumX / clusterSize, sumY / clusterSize);
        return centerPoint;
    }

 
    /**
     * 测试9个点，进行操作
     * @param args
     */
    public static int getKey(List<Point> points) {
        List<Point> points1 = points;
       Canopy canopy = new Canopy(points1);
        canopy.cluster();
                //获取canopy数目
        int k = canopy.getClusterNumber();
		return k;
    }
}
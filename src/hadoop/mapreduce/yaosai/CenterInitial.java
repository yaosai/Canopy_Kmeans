package hadoop.mapreduce.yaosai;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class CenterInitial {
	private static String string;
	private static String[] clist;
	private static String inpath;
	private static String outpath;
	private static Configuration conf1;
	private static List<Point> points;

	public List<Point> getPoint(String[] args) throws IOException {
		string = "";
		inpath = args[0] + "/cluster"; // cluster
		outpath = args[1] + "/center"; // center
		conf1 = new Configuration(); // 读取hadoop文件系统的配置
		conf1.set("hadoop.job.ugi", "hadoop,hadoop");
		FileSystem fs = FileSystem.get(URI.create(inpath), conf1); // FileSystem是用户操作HDFS的核心类，它获得URI对应的HDFS文件系统
		FSDataInputStream in = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {

			in = fs.open(new Path(inpath));
			IOUtils.copyBytes(in, out, 50, false); // 用Hadoop的IOUtils工具方法来让这个文件的指定字节复制到标准输出流上
			clist = out.toString().split(" "); // 按照" "切割
		} finally {
			IOUtils.closeStream(in);
		}
		points = new ArrayList<Point>();
		for (String s : clist) {
			Point p = new Point(s.codePointAt(1), s.codePointAt(3));
			points.add(p);
		}
		return points;
	}

	public void run() throws IOException {
		int k = Canopy.getKey(points);
		FileSystem filesystem = FileSystem.get(URI.create(outpath), conf1);

		for (int i = 0; i < k; i++) {
			int j = (int) (Math.random() * 100) % clist.length;
			if (string.contains(clist[j])) // choose the same one
			{
				k++;
				continue;
			}
			string = string + clist[j].replace(" ", "") + " ";
		}
		OutputStream out2 = filesystem.create(new Path(outpath));
		IOUtils.copyBytes(new ByteArrayInputStream(string.getBytes()), out2,
				4096, true); // write string
		System.out.println(string);
	}

}
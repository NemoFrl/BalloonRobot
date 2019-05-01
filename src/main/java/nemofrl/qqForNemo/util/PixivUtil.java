package nemofrl.qqForNemo.util;

import java.io.File;
import java.util.Random;

public class PixivUtil {

	public static String getPixivUrl() {
//		File pixiv=new File("/opt/lampp/htdocs/data/User/admin/home/pixiv/pixiv");
//		String[] dateFilelist=pixiv.list();
//	    int dateIndex=(int)(Math.random()*dateFilelist.length);
//	    File datefile=new File("/opt/lampp/htdocs/data/User/admin/home/pixiv/pixiv/"+dateFilelist[dateIndex]);
//	    String[] photoFilelist=datefile.list();
//	    int photoIndex=(int)(Math.random()*photoFilelist.length);
//	    String pixivUrl="http://www.fornemo.club:88/index.php?share/file&sid=Rdt9mz5Y&user=1&path=%2F"+dateFilelist[dateIndex]+"%2F"+photoFilelist[photoIndex];
	    return "http://www.fornemo.club:88/index.php?share/folder&user=1&sid=Rdt9mz5Y";
	}
}

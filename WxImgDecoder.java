import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;


public class WxImgDecoder {
	static String PATH = "";
	static boolean RUNNING = false;
	
	public static void main(String[] args) {
		//PATH = "I:\\Honor6\\test";
		PATH = JOptionPane.showInputDialog(null, "Please input PATH of the dat image files", "Wechat dat img file converter", JOptionPane.INFORMATION_MESSAGE);

		if(PATH == null){
			return;
		}
		File f = new File(PATH);
		if(f.isDirectory()){
			RUNNING = true;
			File[] files = f.listFiles();
			for(File tmp : files){
				decodeImg(tmp);
			}
			RUNNING = false;
			System.out.println("All done!");
			new Thread(){
				public void run(){
					while(RUNNING){
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					JOptionPane.showMessageDialog(null, "All img files done!");
				}
			}.start();
		}else{
			JOptionPane.showMessageDialog(null, "You input PATH is error. Please try again!");
		}
	}

	public static void decodeImg(File datFile){
		try {
			Date d = new Date(datFile.lastModified());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			
			String jpgFile = "";
			String type = guessImgType(datFile);
			if(type.length() != 3){
				System.out.println(datFile.getName()+":不能转换该dat文件为正确的图片格式 can NOT decode.");
				return ;
			}
			jpgFile = sdf.format(d) + "." + type;
			FileInputStream fis = new FileInputStream(datFile);
			FileOutputStream fos = new FileOutputStream(PATH+"\\"+jpgFile);
			int ch = fis.read();
			int factor = 0;
			if(type.equals("jpg")){
				factor = ch^0xff;
				fos.write(0xff);
			}else if(type.equals("png")){
				factor = ch^0x89;
				fos.write(0x89);
			}else if(type.equals("gif")){
				factor = ch^0x47;
				fos.write(0x47);
			}
			//System.out.println(Integer.toHexString(factor));
			
			while((ch = fis.read()) != -1){
				fos.write(ch^factor);
			}
			fos.flush();
			fos.close();
			fis.close();
			System.out.println("Write file OK");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String guessImgType(File f){
		try {
			FileInputStream fis = new FileInputStream(f);
			//read the first two bytes from the file for guessing image type
			int ch = fis.read();
			int ch2 = fis.read();
			fis.close();
			if(Integer.toHexString(ch).equalsIgnoreCase("8D") && Integer.toHexString(ch2).equalsIgnoreCase("AA")){
				return "jpg";
			}else if(Integer.toHexString(ch).equalsIgnoreCase("FB") && Integer.toHexString(ch2).equalsIgnoreCase("22")){
				return "png";
			}else if(Integer.toHexString(ch).equalsIgnoreCase("35") && Integer.toHexString(ch2).equalsIgnoreCase("3B")){
				return "gif";
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "unknown";
	}
}

package top.wboost.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoggerViewer {
 
	private static final Logger logger = LoggerFactory.getLogger(LoggerViewer.class);
	ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
	private long pointer = 0; //上次文件大小
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
	Observer observer;

	public LoggerViewer(Observer observer) {
		this.observer = observer;
	}

	public void realtimeShowLog(File logFile) throws Exception{

		if(logFile == null) {
			throw new IllegalStateException("logFile can not be null");
		}

		//启动一个线程每2秒读取新增的日志信息
		exec.scheduleWithFixedDelay(new Runnable(){

			@Override
			public void run() {

				//获得变化部分
				try {
					long len = logFile.length();
					if(len < pointer){
						logger.info("Log file was reset. Restarting logging from start of file.");
						pointer = 0;
					}else{

						//指定文件可读可写
						RandomAccessFile randomFile= new RandomAccessFile(logFile,"rw");

						//获取RandomAccessFile对象文件指针的位置，初始位置是0
						//System.out.println("RandomAccessFile文件指针的初始位置:"+pointer);

						randomFile.seek(pointer);//移动文件指针位置
						String tmp = "";
						while((tmp = randomFile.readLine()) != null) {
							observer.update(null, new String(tmp.getBytes("utf-8")));
							pointer = randomFile.getFilePointer();
						}
						randomFile.close();
					}

				} catch (Exception e) {
					//实时读取日志异常，需要记录时间和lastTimeFileSize 以便后期手动补充
					logger.error(dateFormat.format(new Date())  + " File read error, pointer: "+pointer);
				} finally {
					//将pointer 落地以便下次启动的时候，直接从指定位置获取
				}
			}

		}, 0, 2, TimeUnit.SECONDS);

	}
	
	public void stop(){
		if(exec != null){
			exec.shutdown();
			logger.info("file read stop ！");
		}
	}
	
}
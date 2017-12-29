package cn.ucas.irsys.util;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * LuceneUtil 用来生成索引目录和分词器
 * @author lockjk
 *
 */
public class LuceneUtil {
	
	private static Directory directory;  //  存储索引目录
	private static Analyzer analyzer;   // 分词器
	private static IndexWriter indexWriter;
	
	public static int  pageSize = 10;
	static {
		try {
//			directory = new RAMDirectory();
			directory = FSDirectory.open(new File(GetGProperties.getGProperties().getProperty("finalIndexDir")));
			analyzer = new IKAnalyzer();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获取全局唯一的IndexWriter对象
	 * 
	 * @return
	 */
	public static IndexWriter getIndexWriter() {
		// 在第一次使用IndexWriter是进行初始化
		if (indexWriter == null) {
			synchronized (LuceneUtil.class) { // 注意线程安全问题
				if (indexWriter == null) {
					try {
						indexWriter = new IndexWriter(directory, analyzer, MaxFieldLength.LIMITED);
						System.out.println("=== 已经初始化 IndexWriter ===");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}

			// 指定一段代码，会在JVM退出之前执行。
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						indexWriter.close();
						System.out.println("=== 已经关闭 IndexWriter ===");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		}
		return indexWriter;
	}

	// 获取所有目录
	public static Directory getDirectory() {
		return directory;
	}
	// 获取分词器
	public static Analyzer getAnalyzer() {
		return analyzer;
	}
}

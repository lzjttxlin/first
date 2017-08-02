package com.itheima.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;




public class LuceneFirst {
	
	
	@SuppressWarnings("resource")
	@Test
	public void createIndex() throws Exception{
		//1.创建一个Dictionary对象，指定索引的存放位置，可以是磁盘，可以是内存，通常是保存在磁盘上
		//保存在内存中
		//Directory directory =new RAMDirectory();
		//保存到磁盘中
		Directory directory=FSDirectory.open(new File("F:/黑马基础资料/springmvc/day04/03.教案/index"));
		//2.创建一个indexwrite对象
		Analyzer analyzer=new StandardAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, conf);
		//3.读取文档
		File filePath = new File("F:/黑马基础资料/springmvc/day04/resource");
		for (File f : filePath.listFiles()) {
			//取文件名
			String fileName = f.getName();
			//取文件路径
			String path = f.getPath();
			//取文件内容
			String content = FileUtils.readFileToString(f);
			//取文件大小
			long fileSize = FileUtils.sizeOf(f);
			//4.创建文档对象
			Document document = new Document();
			//5.向文档中添加域
			Field fName = new TextField("name", fileName,Store.YES);
			Field fPath = new TextField("path", path,Store.YES);
			Field fContent = new TextField("content", content,Store.NO);
			Field fSizde = new TextField("size", fileSize+"",Store.YES);
			//6.将域添加到文档中
			document.add(fName);
			document.add(fPath);
			document.add(fContent);
			document.add(fName);
			//将文档写入索引库中
			indexWriter.addDocument(document);
		}
		//关闭indexWriter
		indexWriter.commit();
		indexWriter.close();
	}
	@Test
	public void queryIndex() throws Exception{
		//创建一个IndexReader对象 
		Directory directory =FSDirectory.open(new File("F:/黑马基础资料/springmvc/day04/03.教案/index"));
		IndexReader indexReader=DirectoryReader.open(directory);
		//创建一个indexsearch对象
		IndexSearcher indexSearcher= new IndexSearcher(indexReader);
		//把查询条件创建一个query对象
		Query query = new TermQuery(new Term("name", "lucene"));
		//执行查询
		TopDocs search = indexSearcher.search(query, 10);
		System.out.println("查询的总记录数"+search.totalHits);
		//遍历列表，根据id去除document对象
		ScoreDoc[] scoreDocs = search.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int id = scoreDoc.doc;
			Document document = indexSearcher.doc(id);
			//从document中取出域中的内容
			System.out.println(document.get("content"));
			System.out.println(document.get("name"));
			System.out.println(document.get("path"));
			System.out.println(document.get("size"));
		}
	}
	@SuppressWarnings("resource")
	@Test
	public void testAnalyzer() throws Exception{
		Analyzer ikAnalyzer = new IKAnalyzer();
		TokenStream tokenStream = ikAnalyzer.tokenStream("", "毛泽东共产党台独");
		tokenStream.reset();
		CharTermAttribute attribute = tokenStream.addAttribute(CharTermAttribute.class);
		while (tokenStream.incrementToken()) {
			System.out.println(attribute.toString());
		}
		tokenStream.close();
	}
}

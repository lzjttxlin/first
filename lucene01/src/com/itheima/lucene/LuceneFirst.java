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
		//1.����һ��Dictionary����ָ�������Ĵ��λ�ã������Ǵ��̣��������ڴ棬ͨ���Ǳ����ڴ�����
		//�������ڴ���
		//Directory directory =new RAMDirectory();
		//���浽������
		Directory directory=FSDirectory.open(new File("F:/�����������/springmvc/day04/03.�̰�/index"));
		//2.����һ��indexwrite����
		Analyzer analyzer=new StandardAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, conf);
		//3.��ȡ�ĵ�
		File filePath = new File("F:/�����������/springmvc/day04/resource");
		for (File f : filePath.listFiles()) {
			//ȡ�ļ���
			String fileName = f.getName();
			//ȡ�ļ�·��
			String path = f.getPath();
			//ȡ�ļ�����
			String content = FileUtils.readFileToString(f);
			//ȡ�ļ���С
			long fileSize = FileUtils.sizeOf(f);
			//4.�����ĵ�����
			Document document = new Document();
			//5.���ĵ��������
			Field fName = new TextField("name", fileName,Store.YES);
			Field fPath = new TextField("path", path,Store.YES);
			Field fContent = new TextField("content", content,Store.NO);
			Field fSizde = new TextField("size", fileSize+"",Store.YES);
			//6.������ӵ��ĵ���
			document.add(fName);
			document.add(fPath);
			document.add(fContent);
			document.add(fName);
			//���ĵ�д����������
			indexWriter.addDocument(document);
		}
		//�ر�indexWriter
		indexWriter.commit();
		indexWriter.close();
	}
	@Test
	public void queryIndex() throws Exception{
		//����һ��IndexReader���� 
		Directory directory =FSDirectory.open(new File("F:/�����������/springmvc/day04/03.�̰�/index"));
		IndexReader indexReader=DirectoryReader.open(directory);
		//����һ��indexsearch����
		IndexSearcher indexSearcher= new IndexSearcher(indexReader);
		//�Ѳ�ѯ��������һ��query����
		Query query = new TermQuery(new Term("name", "lucene"));
		//ִ�в�ѯ
		TopDocs search = indexSearcher.search(query, 10);
		System.out.println("��ѯ���ܼ�¼��"+search.totalHits);
		//�����б�����idȥ��document����
		ScoreDoc[] scoreDocs = search.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int id = scoreDoc.doc;
			Document document = indexSearcher.doc(id);
			//��document��ȡ�����е�����
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
		TokenStream tokenStream = ikAnalyzer.tokenStream("", "ë�󶫹�����̨��");
		tokenStream.reset();
		CharTermAttribute attribute = tokenStream.addAttribute(CharTermAttribute.class);
		while (tokenStream.incrementToken()) {
			System.out.println(attribute.toString());
		}
		tokenStream.close();
	}
}

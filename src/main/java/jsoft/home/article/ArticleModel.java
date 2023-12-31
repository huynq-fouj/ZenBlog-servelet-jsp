package jsoft.home.article;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.javatuples.Sextet;
import org.javatuples.Triplet;
import org.javatuples.Unit;

import jsoft.ConnectionPool;
import jsoft.library.Utilities;
import jsoft.objects.ArticleObject;
import jsoft.objects.CategoryObject;

public class ArticleModel {
	
	private Article a;
	
	public ArticleModel(ConnectionPool cp) {
		this.a = new ArticleImpl(cp);
	}
	
	public ConnectionPool getCP() {
		return this.a.getCP();
	}
	
	public void releaseConnection() {
		this.a.releaseConnection();
	}
	
	public ArticleObject getArticleObject(int id) {
		ArticleObject item = null;
		ResultSet rs = this.a.getArticle(id);
		if(rs != null) {
			try {
				if(rs.next()) {
					item = new ArticleObject();
					item.setArticle_id(rs.getInt("article_id"));
					item.setArticle_title(rs.getString("article_title"));
					item.setArticle_summary(rs.getString("article_summary"));
					item.setArticle_content(rs.getString("article_content"));
					item.setArticle_image(rs.getString("article_image"));
					item.setArticle_created_date(rs.getString("article_created_date"));
					item.setArticle_last_modified(rs.getString("article_last_modified"));
					item.setArticle_author_name(rs.getString("article_author_name"));
					item.setArticle_tag(rs.getString("article_tag"));
					item.setCategory_id(rs.getShort("category_id"));
					item.setCategory_name(rs.getString("category_name"));
					item.setSection_id(rs.getShort("section_id"));
					item.setSection_name(rs.getString("section_name"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return item;
	}
	
	public Pair<ArrayList<ArticleObject>, ArrayList<ArticleObject>> getArticleObjects(Quartet<ArticleObject, Short, Byte, Boolean> infors) {
		ArrayList<ResultSet> res = this.a.getArticles(infors);
		//Lấy danh sách bài viết mới nhất và bài viết xem nhiều nhất
		return new Pair<>(this.getArticleObjects(res.get(0)), this.getArticleObjects(res.get(1)));
	}
	
	public Sextet<ArrayList<ArticleObject>, ArrayList<ArticleObject>, ArrayList<CategoryObject>, HashMap<String, Integer>, Integer, ArrayList<ArticleObject>> getNewArticleObjects(Quartet<ArticleObject, Short, Byte, Boolean> infors) {
		ArrayList<ResultSet> res = this.a.getArticles(infors);
		boolean isDetail = infors.getValue3();
		//Lấy danh sách bài viết mới nhất và bài viết xem nhiều nhất
		ArrayList<CategoryObject> cates = new ArrayList<>();
		CategoryObject cate = null;
		ResultSet rs = res.get(2);
		if(rs != null ) {
			try {
				while(rs.next()) {
					cate = new CategoryObject();
					cate.setCategory_id(rs.getShort("category_id"));
					cate.setCategory_name(rs.getString("category_name"));
					cates.add(cate);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		HashMap<String, Integer> tags = new HashMap<>();
		rs = res.get(3);
		String tag;
		String[] tag_list;
		if(rs != null ) {
			try {
				while(rs.next()) {
					tag = rs.getString("article_tag");
					tag = Utilities.decode(tag).toLowerCase();
					tag_list = tag.split(",");
					for(String word: tag_list) {
						word = word.trim();
						if(!"".equals(word)){
							if(tags.containsKey(word)) {
								tags.replace(word, tags.get(word) + 1);
							}else {
								tags.put(word, 1);
							}
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		tags.keySet().removeAll(
				tags.entrySet().stream()
					.filter(a -> a.getValue()
							.compareTo(3) < 0).map(e -> e.getKey())
								.collect(Collectors.toList()));
		
		int total = 0;
		if(!isDetail) {	
			rs = res.get(5);//Tong so bai viet
			if(rs != null) {
				try {
					if(rs.next()) {
						total = rs.getInt("total");
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		//Baif vieets cos phaan trang
		rs = res.get(4);
		ArrayList<ArticleObject> articles = this.getArticleObjects(rs);
		//
		return new Sextet<>(this.getArticleObjects(res.get(0)), this.getArticleObjects(res.get(1)), cates, tags, total, articles);
	}
	
	private ArrayList<ArticleObject> getArticleObjects(ResultSet rs){
		ArrayList<ArticleObject> items = new ArrayList<>();
		ArticleObject item = null;
		if(rs != null) {
			try {
				while(rs.next()) {
					item = new ArticleObject();
					item.setArticle_id(rs.getInt("article_id"));
					item.setArticle_title(rs.getString("article_title"));
					item.setArticle_summary(rs.getString("article_summary"));
					item.setArticle_content(rs.getString("article_content"));
					item.setArticle_image(rs.getString("article_image"));
					item.setArticle_created_date(rs.getString("article_created_date"));
					item.setArticle_author_name(rs.getString("article_author_name"));
					item.setArticle_tag(rs.getString("article_tag"));
					item.setCategory_id(rs.getShort("category_id"));
					item.setCategory_name(rs.getString("category_name"));
					item.setSection_id(rs.getShort("section_id"));
					item.setSection_name(rs.getString("section_name"));
					items.add(item);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		return items;
	}
	
}

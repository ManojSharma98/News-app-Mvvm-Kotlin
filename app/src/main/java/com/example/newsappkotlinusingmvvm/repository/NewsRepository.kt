package com.example.newsappkotlinusingmvvm.repository

import com.example.newsappkotlinusingmvvm.api.RetrofitInstance
import com.example.newsappkotlinusingmvvm.db.ArticleDatabase
import com.example.newsappkotlinusingmvvm.model.Article

class NewsRepository(val db:ArticleDatabase) {

    suspend fun getBreakingNews(countryCode:String,pageNumber:Int)=
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery:String,pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article:Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticle()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

}
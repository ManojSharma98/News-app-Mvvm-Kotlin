package com.example.newsappkotlinusingmvvm.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsappkotlinusingmvvm.model.Article


@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article):Long

    @Query("SELECT * FROM articles")
    fun getAllArticle() :LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)


}
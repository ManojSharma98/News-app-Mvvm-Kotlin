package com.example.newsappkotlinusingmvvm.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsappkotlinusingmvvm.R
import com.example.newsappkotlinusingmvvm.databinding.ItemArticlePreviewBinding
import com.example.newsappkotlinusingmvvm.model.Article

class NewsAdapter :RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

     class ArticleViewHolder(val binding: ItemArticlePreviewBinding):RecyclerView.ViewHolder(binding.root){
        companion object{
                fun from(parent: ViewGroup):ArticleViewHolder{
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = ItemArticlePreviewBinding.inflate(layoutInflater,parent,false)
                    return ArticleViewHolder(binding)
                }
        }
    }

    private val differCallback = object :DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }
    }

    val differ= AsyncListDiffer(this,differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(holder.binding.ivArticleImage)
            holder.binding.tvSource.text = article.source.name
            holder.binding.tvTitle.text = article.title
            holder.binding.tvDescription.text = article.description
            holder.binding.tvPublishedAt.text = article.publishedAt
            setOnClickListener {
                Log.e("data","${article}")
                onItemClickListener?.let { it(article)}
            }
        }

    }

    private var onItemClickListener: ((Article)->Unit)?=null

    fun setOnItemClickListener(listener:(Article)-> Unit){
        onItemClickListener=listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
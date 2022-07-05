package com.example.newsappkotlinusingmvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappkotlinusingmvvm.R
import com.example.newsappkotlinusingmvvm.adapters.NewsAdapter
import com.example.newsappkotlinusingmvvm.databinding.FragmentBreakingNewsBinding
import com.example.newsappkotlinusingmvvm.databinding.FragmentSearchNewsBinding
import com.example.newsappkotlinusingmvvm.ui.NewsActivity
import com.example.newsappkotlinusingmvvm.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.newsappkotlinusingmvvm.util.Resource
import com.example.newsappkotlinusingmvvm.viewModels.NewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragment : Fragment() {

    private lateinit var binding: FragmentSearchNewsBinding
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter :NewsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            Navigation.findNavController(view).navigate(R.id.action_searchNewsFragment_to_articleFragment,bundle)
        }

        var job : Job? =null
        binding.etSearch.addTextChangedListener { editable->
         job?.cancel()
         job = MainScope().launch {
             delay(SEARCH_NEWS_TIME_DELAY)
             editable?.let {
                 if (editable.toString().isNotEmpty()){
                     viewModel.searchNews(editable.toString())
                 }
             }
         }

        }
        viewModel.searchNews.observe(viewLifecycleOwner, Observer {
                response ->
            when(response){
                is Resource.Success ->{
                    hideProgressBar()
                    response.data?.let {
                            newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults/20 +2
                        isLastPage = viewModel.searchNewsPage == totalPages
                    }
                }
                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let {
                            message-> Log.e("SearchNewsFragment","An error occcured : $message")
                    }
                }

                is Resource.Loading->{
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isloading = false
    }


    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isloading = true
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }

    }

    var isloading = false
    var isLastPage = false
    var isScrolling = false

    var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isloading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotArBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= 20

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotArBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate){
                viewModel.searchNews(binding.etSearch.toString())
                isScrolling = false
            }
        }
    }

}
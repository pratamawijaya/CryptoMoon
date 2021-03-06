package com.rmnivnv.cryptomoon.ui.news

import android.support.v7.widget.LinearLayoutManager
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.models.Search
import com.twitter.sdk.android.core.models.Tweet
import retrofit2.Call
import javax.inject.Inject

/**
 * Created by ivanov_r on 26.09.2017.
 */
class NewsPresenter @Inject constructor(private val view: INews.View) : INews.Presenter {

    private var tweets: ArrayList<Tweet> = ArrayList()
    private var twitterSession: TwitterSession? = null
    private lateinit var twitterApiClient: TwitterApiClient
    private var call: Call<Search>? = null
    private var isSwipeRefreshing = false
    private var pastVisibleItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var loading = true
    private var lastId: Long? = null

    override fun onCreate(tweets: ArrayList<Tweet>) {
        this.tweets = tweets
    }

    override fun onStart() {
        if (tweets.isNotEmpty()) {
            view.showRecView()
        } else {
            val activeSession = TwitterCore.getInstance().sessionManager.activeSession
            if (activeSession != null) {
                twitterApiClient = TwitterApiClient(activeSession)
                TwitterCore.getInstance().addApiClient(activeSession, twitterApiClient)
                searchTweets()
            } else {
                view.showLoginBtn()
            }
        }
    }

    private fun searchTweets() {
        if (!isSwipeRefreshing) view.showLoading()
        val searchService = twitterApiClient.searchService
        call = searchService.tweets("cryptocurrency", null, null, null, null, null, null, null, lastId, null)
        call?.enqueue(object : Callback<Search>() {
            override fun success(result: Result<Search>?) {
                val resultTweets = result?.data?.tweets
                if (resultTweets != null) {
                    tweets.addAll(resultTweets)
                    if (lastId != null) tweets.remove(tweets.find { it.id == lastId })
                    view.updateTweets()
                    loading = true
                }
                afterSearch()
            }

            override fun failure(exception: TwitterException?) {

            }
        })
    }

    private fun afterSearch() {
        if (isSwipeRefreshing) {
            isSwipeRefreshing = false
            view.hideSwipeRefreshing()
        } else {
            view.hideLoading()
            view.showRecView()
        }
    }

    override fun onSuccessLogin(result: Result<TwitterSession>?) {
        twitterSession = result?.data
        view.hideLoginBtn()
        twitterApiClient = TwitterCore.getInstance().apiClient
        searchTweets()
    }

    override fun onStop() {
        call?.cancel()
    }

    override fun onSwipeUpdate() {
        isSwipeRefreshing = true
        searchTweets()
    }

    override fun onScrolled(dy: Int, linearLayoutManager: LinearLayoutManager) {
        if (dy > 0) {
            visibleItemCount = linearLayoutManager.childCount
            totalItemCount = linearLayoutManager.itemCount
            pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
            if (loading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                loading = false
                lastId = tweets.last().id
                searchTweets()
            }
        }
    }
}
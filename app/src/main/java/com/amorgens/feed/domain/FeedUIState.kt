package com.amorgens.feed.domain

data class FeedUIState(
    val isFeedLoading:Boolean = false,
    val isFeedLoadingError:Boolean = false,
    val isFeedLoadingSuccess:Boolean = false,
    val getFeedErrorMessage:String = "",
    val isCreatePostLoading:Boolean = false,
    val isCreatePostError:Boolean=false,
    val isCreatePostSuccess:Boolean = false,
    val createPostErrorMessage:String =""
)

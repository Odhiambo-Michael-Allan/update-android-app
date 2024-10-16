package com.squad.update.core.data.repository.impl

import com.squad.update.core.data.Synchronizer
import com.squad.update.core.data.repository.NewsRepository
import com.squad.update.core.data.repository.NewsResourceQuery
import com.squad.update.core.model.data.NewsResource
import com.squad.update.core.model.data.Topic
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import javax.inject.Inject

class OfflineFirstNewsRepository @Inject constructor() : NewsRepository {


    private val newsResourcesFlow: MutableSharedFlow<List<NewsResource>> =
        MutableSharedFlow( replay = 1, onBufferOverflow =  BufferOverflow.DROP_OLDEST )

    init {
        newsResourcesFlow.tryEmit( newsResources )
    }

    override fun getNewsResources( query: NewsResourceQuery ): Flow<List<NewsResource>> =
        newsResourcesFlow.map { newsResources ->
            var result = newsResources
            query.filterTopicIds?.let { filterTopicIds ->
                result = newsResources.filter {
                    it.topics.map( Topic::id ).intersect( filterTopicIds ).isNotEmpty()
                }
            }
            query.filterNewsIds?.let { filterNewsIds ->
                result = newsResources.filter { it.id in filterNewsIds }
            }
            result
        }

    override suspend fun syncWith( synchronizer: Synchronizer ) = true
}

private val newsResources = listOf(
    NewsResource(
        id = "1",
        title = "Android Basics with Compose",
        content = "We released the first two units of Android Basics with Compose, our first free course that teaches Android Development with Jetpack Compose to anyone; you do not need any prior programming experience other than basic computer literacy to get started. You’ll learn the fundamentals of programming in Kotlin while building Android apps using Jetpack Compose, Android’s modern toolkit that simplifies and accelerates native UI development. These two units are just the beginning; more will be coming soon. Check out Android Basics with Compose to get started on your Android development journey",
        url = "https://android-developers.googleblog.com/2022/05/new-android-basics-with-compose-course.html",
        headerImageUrl = "https://developer.android.com/images/hero-assets/android-basics-compose.svg",
        publishDate = LocalDateTime(
            year = 2022,
            monthNumber = 5,
            dayOfMonth = 4,
            hour = 23,
            minute = 0,
            second = 0,
            nanosecond = 0,
        ).toInstant( TimeZone.UTC ),
        type = "Codelab",
        topics = listOf(topics[2]),
    ),
    NewsResource(
        id = "2",
        title = "Thanks for helping us reach 1M YouTube Subscribers",
        content = "Thank you everyone for following the Now in Android series and everything the " +
                "Android Developers YouTube channel has to offer. During the Android Developer " +
                "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
                "thank you all.",
        url = "https://youtu.be/-fJ6poHQrjM",
        headerImageUrl = "https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
        type = "Video 📺",
        topics = topics.take(2),
    ),
    NewsResource(
        id = "3",
        title = "Transformations and customisations in the Paging Library",
        content = "A demonstration of different operations that can be performed " +
                "with Paging. Transformations like inserting separators, when to " +
                "create a new pager, and customisation options for consuming " +
                "PagingData.",
        url = "https://youtu.be/ZARz0pjm5YM",
        headerImageUrl = "https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(topics[2]),
    ),
    NewsResource(
        id = "4",
        title = "Our first Spotlight Week: diving into Android 15",
        content = "By now, you’ve probably heard the news: Android 15 was just released" +
                " earlier today to AOSP. To celebrate, we’re kicking off a new series" +
                " called “Spotlight Week” where we’ll shine a light on technical" +
                " areas across Android development and equip you with the tools" +
                " you need to take advantage of each area.",
        url = "https://youtu.be/ZARz0pjm5YM",
        headerImageUrl = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEhTydOm5voDz-5BOLceHlbNYE7m_L9xE8Ze6-UKQ3cDufxRLrO7EgtA57PALcouXbo6TtSY6c0_QGRPFSqH4igiaOf7pCHCxvMxqwhgwRZMwKSsZEuDNjJ0PBHM-JcyI_pH2_VoxNcqiTi3FbZ8GfRtasRwOAbQkbsSItQ1-4bQn-gydBHJiGbQpUzsxMg/s1600/AndroidSpotlight_Android15_Blog_Header.png",
        publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(topics[2]),
    ),
    NewsResource(
        id = "5",
        title = "AllTrails gains over 1 million downloads after implementing its Wear OS app",
        content = "With more than 65 million global users, AllTrails is one of the world’s" +
                " most popular and trusted platforms for outdoor exploration. The app is" +
                " designed to be the ultimate adventure companion, so the AllTrails team" +
                " always works to improve users’ outdoor experience using the latest" +
                " technology. Recently, its developers created a new Wear OS" +
                " application. Now, users can access their favorite AllTrails" +
                " features using their favorite Android wearables.",
        url = "https://youtu.be/ZARz0pjm5YM",
        headerImageUrl = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjRjX7k75TQLi3ETLKpHXHiT3CHjvO-QfV9DEnBBawdQeOTqJzQ3e9B-nzGmQxfwF_L5RPMV7hqNAMEm_JKQ7-MMRd25jkPUtn9gh2uIC59nDBFGyTMUSUmeIS0azpRTaMsDhaYMLkE3tQxi_iUhCoFdS5cuX3k5wY9wJ2bxjxNplOnEWEUN65u_yAWAvs/s1600/image4.gif",
        publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf( topics[1] ),
    ),
    NewsResource(
        id = "5",
        title = "Attestation format change for the Android FIDO2 API",
        content = "In 2019 we introduced a FIDO2 API, adopted by many leading developers, " +
                "which allows users to generate an attested, device-bound FIDO2" +
                " credential on Android devices.",
        url = "https://youtu.be/ZARz0pjm5YM",
        headerImageUrl = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjFZAwTuEczCixWe-6SN-cKy9wx7VuFML578tLjcX-sI4TfzG0M3Y7ODYo_dOEWZzrizvLDgQit-aRGtvmixausO1waHpb_ERBvwmkX6vMMBdFqJXMTGfW43B2KyVZVigGgRcBESvFnR8UST5aAzRBM1mn7FSz-ICYUoxe44NfsxAinWDB48XOhMN_rVEg/s1600/header-Blog-post-describing-migration-from-existing-Auth-APIs-to-CredMan-API.png",
        publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf( topics[2] ),
    ),

)
package com.squad.update.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.squad.update.core.model.data.DarkThemeConfig
import com.squad.update.core.model.data.NewsResource
import com.squad.update.core.model.data.ThemeBrand
import com.squad.update.core.model.data.Topic
import com.squad.update.core.model.data.UserData
import com.squad.update.core.model.data.UserNewsResource
import com.squad.update.core.ui.PreviewParameterData.newsResources
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [UserNewsResource] for Composable previews.
 */
class UserNewsResourcePreviewParameterProvider : PreviewParameterProvider<List<UserNewsResource>> {

    override val values: Sequence<List<UserNewsResource>> = sequenceOf( newsResources )
}

object PreviewParameterData {

    private val userData: UserData = UserData(
        bookmarkedNewsResources = setOf("1", "3"),
        viewedNewsResources = setOf("1", "2", "4"),
        followedTopics = emptySet(),
        themeBrand = ThemeBrand.ANDROID,
        darkThemeConfig = DarkThemeConfig.DARK,
        shouldHideTopicSelection = true,
        useDynamicColor = false,
    )

    val topics = listOf(
        Topic(
            id = "2",
            name = "Headlines",
            shortDescription = "News we want everyone to see",
            longDescription = "Stay up to date with the latest events and announcements from Android!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
            url = "",
        ),
        Topic(
            id = "3",
            name = "UI",
            shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
            longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
            url = "",
        ),
        Topic(
            id = "4",
            name = "Testing",
            shortDescription = "CI, Espresso, TestLab, etc",
            longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
            url = "",
        ),
    )

    val newsResources = listOf(
        UserNewsResource(
            newsResource = NewsResource(
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
                ).toInstant(TimeZone.UTC),
                type = "Codelab",
                topics = listOf(topics[2]),
            ),
            userData = userData,
        ),
        UserNewsResource(
            newsResource = NewsResource(
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
            userData = userData,
        ),
        UserNewsResource(
            newsResource = NewsResource(
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
            userData = userData,
        ),
        UserNewsResource(
            newsResource = NewsResource(
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
            userData = userData,
        ),
        UserNewsResource(
            newsResource = NewsResource(
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
            userData = userData,
        ),
        UserNewsResource(
            newsResource = NewsResource(
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
            userData = userData,
        ),
    )
}
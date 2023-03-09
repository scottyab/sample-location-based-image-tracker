package com.scottyab.challenge.di

import android.app.NotificationManager
import android.content.Context
import com.scottyab.challenge.BuildConfig
import com.scottyab.challenge.data.RealActivityRepository
import com.scottyab.challenge.data.RealSnapshotRepository
import com.scottyab.challenge.data.datasource.local.db.TrackerDatabase
import com.scottyab.challenge.data.datasource.location.LocationProvider
import com.scottyab.challenge.data.datasource.location.LocationServiceNotificationHelper
import com.scottyab.challenge.data.datasource.location.RealLocationProvider
import com.scottyab.challenge.data.datasource.location.SampleLocationProvider
import com.scottyab.challenge.data.datasource.location.util.AndroidLocationCalculator
import com.scottyab.challenge.data.datasource.remote.FlickrService
import com.scottyab.challenge.domain.ActivityNameGenerator
import com.scottyab.challenge.domain.ActivityRepository
import com.scottyab.challenge.domain.IdGenerator
import com.scottyab.challenge.domain.LocationCalculator
import com.scottyab.challenge.domain.SnapshotRepository
import com.scottyab.challenge.domain.SnapshotTracker
import com.scottyab.challenge.domain.TimeOfDayActivityNameGenerator
import com.scottyab.challenge.domain.UUIDGenerator
import com.scottyab.challenge.domain.mappers.ActivityMapper
import com.scottyab.challenge.domain.mappers.PhotoMapper
import com.scottyab.challenge.domain.mappers.SnapshotMapper
import com.scottyab.challenge.domain.usecase.NewLocationUsecase
import com.scottyab.challenge.domain.usecase.StartActivityUsecase
import com.scottyab.challenge.presentation.activities.ActivitiesViewModel
import com.scottyab.challenge.presentation.common.AndroidResources
import com.scottyab.challenge.presentation.common.AndroidResourcesProvider
import com.scottyab.challenge.presentation.common.AppCoroutineScope
import com.scottyab.challenge.presentation.common.CoilImageLoader
import com.scottyab.challenge.presentation.common.ImageLoader
import com.scottyab.challenge.presentation.common.SimpleThrottler
import com.scottyab.challenge.presentation.snapshots.SnapshotUiMapper
import com.scottyab.challenge.presentation.snapshots.SnapshotsViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModule = module {
    single<ImageLoader> { CoilImageLoader() }
    single { AppCoroutineScope() }
    single<AndroidResources> { AndroidResourcesProvider(androidContext()) }
    factory { SimpleThrottler() }
    single { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    factory<LocationCalculator> { AndroidLocationCalculator() }
}

val networkModule = module {
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    if (BuildConfig.DEBUG) {
                        setLevel(HttpLoggingInterceptor.Level.BASIC)
                    }
                }
            ).build()
    }

    single<FlickrService> {
        Retrofit.Builder()
            .baseUrl(BuildConfig.FLICKR_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(get())
            .build()
            .create(FlickrService::class.java)
    }
}

val domainModule = module {
    factory<IdGenerator> { UUIDGenerator() }
    factory<ActivityNameGenerator> { TimeOfDayActivityNameGenerator() }

    factory<SnapshotRepository> {
        RealSnapshotRepository(
            flickrService = get(),
            snapshotMapper = get(),
            snapshotDao = get(),
            photoMapper = get()
        )
    }
    factory { SnapshotMapper(idGenerator = get()) }
    factory { PhotoMapper() }
    factory { ActivityMapper() }

    factory<ActivityRepository> {
        RealActivityRepository(
            activityMapper = get(),
            activityDao = get(),
            idGenerator = get()
        )
    }

    factory {
        NewLocationUsecase(
            snapshotRepository = get(),
            locationCalculator = get()
        )
    }

    factory {
        StartActivityUsecase(
            activityNameGenerator = get(),
            activityRepository = get()
        )
    }

    single {
        SnapshotTracker(
            locationProvider = get(),
            newLocationUsecase = get(),
            startActivityUsecase = get(),
            appCoroutineScope = get()
        )
    }
}

val dataModule = module {
    single { TrackerDatabase.getInstance(androidContext()) }
    factory { get<TrackerDatabase>().snapshotsDao() }
    factory { get<TrackerDatabase>().activityDao() }

    factory {
        LocationServiceNotificationHelper(
            context = androidContext(),
            androidResources = get(),
            notificationManager = get()
        )
    }
    single {
        RealLocationProvider(
            context = androidContext(),
            appCoroutineScope = get()
        )
    }

    single<LocationProvider> {
        if (BuildConfig.SAMPLE_LOCATIONS) {
            SampleLocationProvider(
                context = androidContext(),
                moshi = get(),
                appCoroutineScope = get()
            )
        } else {
            get<RealLocationProvider>()
        }
    }
}

val presentationModule = module {
    factory { SnapshotUiMapper() }
    viewModel {
        SnapshotsViewModel(
            snapshotRepository = get(),
            snapshotUiMapper = get(),
            snapshotTracker = get()
        )
    }
    viewModel {
        ActivitiesViewModel(
            activityRepository = get()
        )
    }
}

val allModules = listOf(appModule, networkModule, domainModule, dataModule, presentationModule)
